#include "app_manager.hpp"

using namespace staccato;

std::vector<Track> AppManager::main_queue {};
std::vector<Track> AppManager::added_queue {};
std::vector<std::tuple<bool, std::string, std::vector<std::string>, std::string>> AppManager::pinned_items {};

bool AppManager::serialize_queue(std::string main_queue_playlist_id, std::uint64_t main_position, std::uint64_t added_position) {

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

std::tuple<std::string, std::uint64_t, std::uint64_t> AppManager::read_saved_queue() {

    std::ifstream input (std::string{QUEUE_STORAGE_PATH}, std::ios::binary);
    if(!input.is_open()) {

        return {"", -1, -1};

    }

    std::string header = ifstream_read_file_header(input);
    if(header != std::string(FILE_HEADER)) {

        return {"", -1, -1};

    }

    main_queue.clear();
    added_queue.clear();

    std::uint16_t total_count {0};
    std::string main_queue_playlist_id {""};
    std::uint64_t main_position {0}, added_position {0};

    char c = '\0';
    while(total_count < 65500) {

        c = input.get();
        total_count++;
        
        if(input.fail()) {

            //Input should not fail AND input should not be EOF at this point of the reading
            return {"", -1, -1};

        }

        main_queue_playlist_id.push_back(c);

        if(c == '\0') {

            break;

        }

    }

    bool uint64_input_failed {false};
    main_position = read_next_uint64(input, uint64_input_failed);
    if(uint64_input_failed) {

        return {"", -1, -1};

    }
    added_position = read_next_uint64(input, uint64_input_failed);
    if(uint64_input_failed) {

        return {"", -1, -1};

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

            return {"", -1, -1};

        }

        if(count != 1) {

            if(c == '\0') {

                count++;

                //Remember, double null chars separate the main queue's last album and the added queue's first title.
                //If we encounter a null char when the title is empty, that means no title was read, and hence the
                //previous char was a null char-- forming double null chars.
                if(title.size() == 0) {

                    is_on_added_queue = true;

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

        return {"", -1, -1};

    }

    return {main_queue_playlist_id, main_position, added_position};

}

void AppManager::read_settings() {

    std::ifstream input {std::string(STACCATO_SETTINGS_PATH)};
    if(!input.is_open()) {

        return;

    }

    std::string text {};

    while(std::getline(input, text)) {

        if(text == "[PINNED]") {

            break;

        }

    }

    pinned_items.clear();
    bool is_track {0};
    std::string property_1 {}; //Either the playlist name or the track title
    std::vector<std::string> property_2 {}; //Either the playlist size & online connection or the track artists
    std::string property_3 {}; //Either the playlist ID or the track album

    for(std::size_t i {0}; i < 4; i++) {

        if(!std::getline(input, text)) {

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

    if(original_index >= pinned_items.size() || new_index >= pinned_items.size()) {

        return false;

    }

    if(original_index > new_index) {

        std::rotate(pinned_items.begin() + new_index, pinned_items.begin() + original_index, pinned_items.begin() + original_index + 1);

    } else if(original_index < new_index) {

        std::rotate(pinned_items.begin() + original_index, pinned_items.begin() + original_index + 1, pinned_items.begin() + new_index + 1);

    }

    return true;

}
