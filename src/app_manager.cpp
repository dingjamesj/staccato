#include "track.hpp"
#include "util.hpp"
#include "track_manager.hpp"
#include "app_manager.hpp"

using namespace staccato;

std::vector<Track> AppManager::main_queue {};
std::vector<Track> AppManager::added_queue {};
std::vector<std::tuple<bool, std::string, std::vector<std::string>, std::string>> AppManager::pinned_items {};
int AppManager::pinned_items_zoom_level {1};
int AppManager::playlists_zoom_level {1};
std::string AppManager::pinned_items_sort_mode {"CUSTOM"};

bool AppManager::serialize_session_data(const std::string& main_queue_playlist_id, std::uint64_t main_position, std::uint64_t added_position) {

    std::ofstream output (std::string(QUEUE_STORAGE_PATH), std::ios::binary);
    if(!output.is_open()) {

        return false;

    }

    output.write(std::string(FILE_HEADER).c_str(), FILE_HEADER.size());
    output.put('\0');
    output.write(main_queue_playlist_id.c_str(), main_queue_playlist_id.size());
    output.put('\0');
    output.write(reinterpret_cast<const char*>(&main_position), sizeof(std::uint64_t));
    output.write(reinterpret_cast<const char*>(&added_position), sizeof(std::uint64_t));
    for(const Track& track: main_queue) {

        output.write(track.title().c_str(), track.title().size());
        output.put('\0');
        for(const std::string& artist: track.artists()) {

            output.write(artist.c_str(), artist.size());
            output.put('\0');

        }
        output.put('\0');
        output.write(track.album().c_str(), track.album().size());
        output.put('\0');

        if(output.fail()) {

            return false;

        }

    }
    output.put('\0');
    for(const Track& track: added_queue) {

        output.write(track.title().c_str(), track.title().size());
        output.put('\0');
        for(const std::string& artist: track.artists()) {

            output.write(artist.c_str(), artist.size());
            output.put('\0');

        }
        output.put('\0');
        output.write(track.album().c_str(), track.album().size());
        output.put('\0');

        if(output.fail()) {

            return false;

        }

    }

    output.flush();
    if(output.fail()) {

        return false;

    }

    output.close();
    return true;

}

bool AppManager::serialize_settings() {

    std::ofstream output {std::string(STACCATO_SETTINGS_PATH)};
    if(!output.is_open()) {

        return false;

    }

    output << "[SORT]\n";
    output << "PINNED\n";
    output << pinned_items_sort_mode << "\n";
    output << "\n";

    output << "[ZOOM]\n";
    output << "PINNED\n";
    output << pinned_items_zoom_level << "\n";
    output << "PLAYLISTS\n";
    output << playlists_zoom_level << "\n";
    output << "\n";

    output << "[PINNED]\n";
    for(std::tuple<bool, std::string, std::vector<std::string>, std::string> item: pinned_items) {

        if(std::get<0>(item)) {

            output << "track\n";
            output << std::get<1>(item) + "\n";
            output << std::get<3>(item) + "\n";

        } else {

            output << "playlist\n";
            output << std::get<3>(item) + "\n";
            output << std::get<1>(item) + "\n";

        }

        for(const std::string& str: std::get<2>(item)) {

            if(str.empty()) {

                continue;

            }
            
            output << str + "\n";

        }

        output << "\n";

        if(output.fail()) {

            return false;

        }

    }

    output.flush();
    if(output.fail()) {

        return false;

    }

    output.close();
    return true;

}

bool AppManager::read_last_session_data(std::string& main_queue_playlist_id, std::uint64_t& main_position, std::uint64_t& added_position) {

    std::ifstream input (std::string{QUEUE_STORAGE_PATH}, std::ios::binary);
    if(!input.is_open()) {

        return false;

    }

    std::string header = ifstream_read_file_header(input);
    if(header != std::string(FILE_HEADER)) {

        return false;

    }

    main_queue.clear();
    added_queue.clear();

    std::uint16_t total_count {0};
    main_queue_playlist_id.clear();
    main_position = added_position = 0;

    char c = '\0';
    while(total_count < 65500) {

        c = input.get();
        total_count++;
        
        if(input.fail()) {

            //Input should not fail AND input should not be EOF at this point of the reading
            return false;

        }

        main_queue_playlist_id.push_back(c);

        if(c == '\0') {

            break;

        }

    }

    bool uint64_input_failed {false};
    main_position = read_next_uint64(input, uint64_input_failed);
    if(uint64_input_failed) {

        return false;

    }
    added_position = read_next_uint64(input, uint64_input_failed);
    if(uint64_input_failed) {

        return false;

    }

    bool is_on_added_queue {false};
    std::uint8_t count {0};
    std::string title {""}, curr_artist {""}, album {""};
    std::vector<std::string> artists {};
    while(total_count < 65500) {

        c = input.get();
        total_count++;

        if(input.fail()) {

            if(input.eof()) {

                break;

            }

            return false;

        }

        if(count != 1) {

            if(c == '\0') {

                count++;

                //Remember, double null chars separate the main queue's last album and the added queue's first title.
                //If we encounter a null char when the title is empty, that means no title was read, and hence the
                //previous char was a null char-- forming double null chars.
                if(title.size() == 0) {

                    is_on_added_queue = true;
                    count--;

                }

            } else {

                switch(count) {

                case 0:
                    title.push_back(c);
                    break;
                case 2:
                    album.push_back(c);
                default:
                    break;

                }

            }

            if(count > 2) {

                count = 0;
                if(is_on_added_queue) {

                    added_queue.push_back(Track(title, artists, album));

                } else {

                    main_queue.push_back(Track(title, artists, album));

                }

                title = curr_artist = album = "";
                artists.clear();

            }

        } else {

            if(c == '\0' && curr_artist.empty()) {

                count++;

            } else if(c == '\0') {

                artists.push_back(curr_artist);
                curr_artist = "";

            } else {

                curr_artist.push_back(c);

            }

        }

    }

    if(count != 0) {

        return false;

    }

    return true;

}

const std::vector<Track>& AppManager::get_main_queue() {

    return main_queue;

}

const std::vector<Track>& AppManager::get_added_queue() {

    return added_queue;

}

void AppManager::set_main_queue(const std::vector<Track>& tracklist) {

    main_queue.clear();
    for(const Track& track: tracklist) {

        main_queue.push_back(track);

    }

}

bool AppManager::remove_main_queue_track(std::size_t index) {

    if(index < 0 || index >= main_queue.size()) {

        return false;

    }

    main_queue.erase(main_queue.begin() + index);

    return true;

}

bool AppManager::move_main_queue_track(std::size_t original_index, std::size_t new_index) {

    if(original_index >= main_queue.size() || new_index >= main_queue.size() || original_index < 0 || new_index < 0) {

        return false;

    }

    if(original_index > new_index) {

        std::rotate(main_queue.begin() + new_index, main_queue.begin() + original_index, main_queue.begin() + original_index + 1);

    } else if(original_index < new_index) {

        std::rotate(main_queue.begin() + original_index, main_queue.begin() + original_index + 1, main_queue.begin() + new_index + 1);

    }

    return true;

}

void AppManager::push_back_added_queue(const Track& track) {

    added_queue.push_back(track);

}

void AppManager::pop_front_added_queue() {

    added_queue.erase(added_queue.begin());

}

bool AppManager::remove_added_queue_track(std::size_t index) {

    if(index < 0 || index >= added_queue.size()) {

        return false;

    }
    
    added_queue.erase(added_queue.begin() + index);

    return true;

}

bool AppManager::move_added_queue_track(std::size_t original_index, std::size_t new_index) {

    if(original_index >= added_queue.size() || new_index >= added_queue.size() || original_index < 0 || new_index < 0) {

        return false;

    }

    if(original_index > new_index) {

        std::rotate(added_queue.begin() + new_index, added_queue.begin() + original_index, added_queue.begin() + original_index + 1);

    } else if(original_index < new_index) {

        std::rotate(added_queue.begin() + original_index, added_queue.begin() + original_index + 1, added_queue.begin() + new_index + 1);

    }

    return true;

}

void AppManager::read_settings() {

    std::ifstream input {std::string(STACCATO_SETTINGS_PATH)};
    if(!input.is_open()) {

        return;

    }

    std::string text {};

    while(std::getline(input, text)) {

        if(text == "[SORT]") {

            read_settings_sort_modes(input, text);

        } else if(text == "[ZOOM]") {

            read_settings_zoom_levels(input, text);

        } else if(text == "[PINNED]") {

            read_settings_pinned_items(input, text);

        }

    }

}

void AppManager::read_settings_sort_modes(std::ifstream& input, std::string& text) {

    while(std::getline(input, text) && !text.empty()) {

        if(text == "PINNED" && std::getline(input, text)) {

            pinned_items_sort_mode = text;

        }

    }

}

void AppManager::read_settings_zoom_levels(std::ifstream& input, std::string& text) {

    while(std::getline(input, text) && !text.empty()) {

        if(text == "PINNED" && std::getline(input, text)) {

            try {

                pinned_items_zoom_level = std::stoi(text);

            } catch (const std::exception&) {}

        }

        if(text == "PLAYLISTS" && std::getline(input, text)) {

            try {

                playlists_zoom_level = std::stoi(text);

            } catch (const std::exception&) {}

        }

    }

}

void AppManager::read_settings_pinned_items(std::ifstream& input, std::string& text) {

    pinned_items.clear();
    bool is_track {0};
    std::string property_1 {}; //Either the playlist name or the track title
    std::vector<std::string> property_2 {}; //Either the playlist size & online connection or the track artists
    std::string property_3 {}; //Either the playlist ID or the track album

    std::size_t i {0};
    while(true) {

        if(!std::getline(input, text) || text.empty() || text[0] == '[') {

            break;

        }

        switch(i) {

        case 0:
            if(text == "playlist") {

                is_track = false;

            } else {

                is_track = true;

            }
            break;
        case 1:
            if(is_track) {

                property_1 = text;

            } else {

                property_3 = text;

            }
            break;
        case 2:
            if(is_track) {

                property_3 = text;

            } else {

                property_1 = text;

            }
            break;
        case 3:
            property_2.clear();
            property_2.push_back(text);
            while(std::getline(input, text)) {

                if(text == "") {

                    break;

                }

                property_2.push_back(text);

            }

            //When reading a pinned playlist that has no online connection, property_2 will have length 1 and only contain the playlist size.
            //However, for playlists without an online connection, property_2 should still have length 2, and have an empty string for the online connection.
            if(!is_track && property_2.size() == 1) {

                property_2.push_back("");

            }

            pinned_items.push_back({is_track, property_1, property_2, property_3});
            break;
        default:
            break;

        }

        i++;
        i %= 4;

    }

}

const std::vector<std::tuple<bool, std::string, std::vector<std::string>, std::string>>& AppManager::get_pinned_items() {

    return pinned_items;

}

bool AppManager::add_pinned_playlist(const std::string& id, const std::string& name, std::uint64_t size, const std::string& online_connection) {

    for(const std::tuple<bool, std::string, std::vector<std::string>, std::string>& item: pinned_items) {

        if(std::get<0>(item)) {

            continue;

        }

        if(std::get<3>(item) == id) {

            return false;

        }

    }

    std::string size_str = std::to_string(size);
    pinned_items.push_back({false, name, {size_str, online_connection}, id});
    return true;

}

bool AppManager::add_pinned_track(const Track& track) {

    for(const std::tuple<bool, std::string, std::vector<std::string>, std::string>& item: pinned_items) {

        if(!std::get<0>(item)) {

            continue;

        }

        if(std::get<1>(item) == track.title() && std::get<2>(item) == track.artists() && std::get<3>(item) == track.album()) {

            return false;

        }

    }

    pinned_items.push_back({true, track.title(), track.artists(), track.album()});
    return true;

}

bool AppManager::remove_pinned_item(std::size_t index) {

    if(index >= pinned_items.size()) {

        return false;

    }

    pinned_items.erase(pinned_items.begin() + index);
    return true;

}

bool AppManager::move_pinned_item(std::size_t original_index, std::size_t new_index) {

    if(original_index >= pinned_items.size() || new_index >= pinned_items.size() || original_index < 0 || new_index < 0) {

        return false;

    }

    if(original_index > new_index) {

        std::rotate(pinned_items.begin() + new_index, pinned_items.begin() + original_index, pinned_items.begin() + original_index + 1);

    } else if(original_index < new_index) {

        std::rotate(pinned_items.begin() + original_index, pinned_items.begin() + original_index + 1, pinned_items.begin() + new_index + 1);

    }

    return true;

}

int AppManager::get_pinned_items_zoom_level() {

    return pinned_items_zoom_level;

}

int AppManager::get_playlists_zoom_level() {

    return playlists_zoom_level;

}

std::string AppManager::get_pinned_items_sort_mode() {

    return pinned_items_sort_mode;

}

void AppManager::set_pinned_items_zoom_level(int zoom_level) {

    pinned_items_zoom_level = zoom_level;

}

void AppManager::set_playlists_zoom_level(int zoom_level) {

    playlists_zoom_level = zoom_level;

}

void AppManager::set_pinned_items_sort_mode(const std::string& sort_mode) {

    pinned_items_sort_mode = sort_mode;

}

std::string AppManager::get_playlist_image_path(const std::string& playlist_id) {

    std::filesystem::path jpg_path = std::filesystem::current_path() / std::filesystem::path(PLAYLIST_IMAGES_DIRECTORY) / (playlist_id + ".jpg");
    if(std::ifstream(jpg_path).good()) {

        return jpg_path.string();

    }

    std::filesystem::path png_path = std::filesystem::current_path() / std::filesystem::path(PLAYLIST_IMAGES_DIRECTORY) / (playlist_id + ".png");
    if(std::ifstream(png_path).good()) {

        return png_path.string();

    }

    std::filesystem::path jpeg_path = std::filesystem::current_path() / std::filesystem::path(PLAYLIST_IMAGES_DIRECTORY) / (playlist_id + ".jpeg");
    if(std::ifstream(jpeg_path).good()) {

        return jpeg_path.string();

    }

    std::filesystem::path jpg_path_caps = std::filesystem::current_path() / std::filesystem::path(PLAYLIST_IMAGES_DIRECTORY) / (playlist_id + ".JPG");
    if(std::ifstream(jpg_path_caps).good()) {

        return jpg_path_caps.string();

    }

    std::filesystem::path png_path_caps = std::filesystem::current_path() / std::filesystem::path(PLAYLIST_IMAGES_DIRECTORY) / (playlist_id + ".PNG");
    if(std::ifstream(png_path_caps).good()) {

        return png_path_caps.string();

    }

    std::filesystem::path jpeg_path_caps = std::filesystem::current_path() / std::filesystem::path(PLAYLIST_IMAGES_DIRECTORY) / (playlist_id + ".JPEG");
    if(std::ifstream(jpeg_path_caps).good()) {

        return jpeg_path_caps.string();

    }

    return "qrc" + std::string(PLACEHOLDER_ART_PATH);

}

void AppManager::print_main_queue() {

    for(const Track& track: main_queue) {

        std::cout << track.string();

    }

}

void AppManager::print_added_queue() {

    for(const Track& track: added_queue) {

        std::cout << track.string();

    }

}

void AppManager::print_pinned_items() {

    for(const std::tuple<bool, std::string, std::vector<std::string>, std::string> item: pinned_items) {

        std::cout << (std::get<0>(item) ? "Track" : "Playlist") << std::endl;
        std::cout << std::get<1>(item) << std::endl;
        for(const std::string& str: std::get<2>(item)) {

            std::cout << (str.empty() ? "[empty]" : str) << std::endl;

        }
        std::cout << std::get<3>(item) << std::endl;
        std::cout << std::endl;

    }

}
