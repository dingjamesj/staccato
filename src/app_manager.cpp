#include "track.hpp"
#include "util.hpp"
#include "track_manager.hpp"
#include "app_manager.hpp"
#include "playlist_tree.hpp"

using namespace staccato;

std::vector<Track> AppManager::main_queue {};
std::vector<Track> AppManager::added_queue {};

std::unordered_map<std::string, std::variant<std::string, int, double>> AppManager::settings;
PlaylistTree AppManager::playlistTree {};

bool AppManager::serialize_persistent_session_data(const std::string& main_queue_playlist_id, std::uint64_t main_position, std::uint64_t added_position) {

    //To represent the queue, we want to store the main queue, the added queue, the main queue's playlist ID,
    //the position in the main queue, and the position in the added queue.

    std::ofstream output (std::string{PERSISTENT_DATA_PATH});
    if(!output.is_open()) {

        return false;

    }

    nlohmann::json root {};

    root[MAIN_QUEUE_JSON_KEY] = nlohmann::json::array();
    std::size_t i {0};
    for(const Track& track: main_queue) {

        root[MAIN_QUEUE_JSON_KEY].push_back(nlohmann::json::object());
        nlohmann::json& track_json = root[MAIN_QUEUE_JSON_KEY][i];
        track_json[TrackManager::TITLE_JSON_KEY] = track.title();
        track_json[TrackManager::ALBUM_JSON_KEY] = track.album();
        track_json[TrackManager::ARTISTS_JSON_KEY] = nlohmann::json::array();
        for(const std::string& artist: track.artists()) {

            track_json[TrackManager::ARTISTS_JSON_KEY].push_back(artist);

        }

        i++;

    }

    root[ADDED_QUEUE_JSON_KEY] = nlohmann::json::array();
    i = 0;
    for(const Track& track: added_queue) {

        root[ADDED_QUEUE_JSON_KEY].push_back(nlohmann::json::object());
        nlohmann::json& track_json = root[ADDED_QUEUE_JSON_KEY][i];
        track_json[TrackManager::TITLE_JSON_KEY] = track.title();
        track_json[TrackManager::ALBUM_JSON_KEY] = track.album();
        track_json[TrackManager::ARTISTS_JSON_KEY] = nlohmann::json::array();
        for(const std::string& artist: track.artists()) {

            track_json[TrackManager::ARTISTS_JSON_KEY].push_back(artist);

        }

        i++;

    }

    root[MAIN_QUEUE_POSITION_JSON_KEY] = main_position;
    root[ADDED_QUEUE_POSITION_JSON_KEY] = added_position;
    root[MAIN_QUEUE_PLAYLIST_ID_JSON_KEY] = main_queue_playlist_id;

    //Serialize
    output << std::setw(4) << root << std::endl;

    if(output.fail()) {

        return false;

    }

    return true;

}

bool AppManager::serialize_settings() {

    std::ofstream output {std::string(STACCATO_SETTINGS_PATH)};
    if(!output.is_open()) {

        return false;

    }

    nlohmann::json root {};
    for(const std::pair<std::string, std::variant<std::string, int, double>>& pair: settings) {

        if(auto val = std::get_if<std::string>(&pair.second)) {

            root[pair.first] = *val;

        } else if(auto val = std::get_if<int>(&pair.second)) {

            root[pair.first] = *val;

        } else if(auto val = std::get_if<double>(&pair.second)) {

            root[pair.first] = *val;

        }

    }

    //Serialize
    output << std::setw(4) << root << std::endl;

    if(output.fail()) {

        return false;

    }

    return true;

}

bool AppManager::read_persistent_data(std::string& main_queue_playlist_id, std::uint64_t& main_position, std::uint64_t& added_position) {

    //Unlike some other JSON read functions, this one returns absolutely nothing if an error in the JSON formatting is detected.
    //i.e. if there's a missing JSON field anywhere in the file, then all of the data is ignored and no persistent data is read.
    //This design choice was made because the persistent data JSON should never be modified manually, so any errors in formatting 
    //would have came from the program. Bugs in the program should not be ignored.

    main_queue.clear();
    main_position = 0;
    main_queue_playlist_id.clear();
    added_queue.clear();
    added_position = 0;

    std::ifstream input (std::string{PERSISTENT_DATA_PATH});
    if(!input.is_open()) {

        return false;

    }

    const nlohmann::json& root = nlohmann::json::parse(input);
    if(
        !root.contains(MAIN_QUEUE_JSON_KEY) || !root.contains(MAIN_QUEUE_POSITION_JSON_KEY) || !root.contains(MAIN_QUEUE_PLAYLIST_ID_JSON_KEY) ||
        !root.contains(ADDED_QUEUE_JSON_KEY) || !root.contains(ADDED_QUEUE_POSITION_JSON_KEY)
    ) {

        return false;

    }

    //Read the main queue's tracklist
    const nlohmann::json& main_queue_json = root[MAIN_QUEUE_JSON_KEY];
    for(nlohmann::json::const_iterator iter = main_queue_json.begin(); iter != main_queue_json.end(); iter++) {

        if(!(*iter).contains(TrackManager::TITLE_JSON_KEY) || !(*iter).contains(TrackManager::ARTISTS_JSON_KEY) || !(*iter).contains(TrackManager::ALBUM_JSON_KEY)) {

            main_queue.clear();
            return false;

        }

        const nlohmann::json& title_json = (*iter)[TrackManager::TITLE_JSON_KEY];
        const nlohmann::json& artists_json = (*iter)[TrackManager::ARTISTS_JSON_KEY];
        const nlohmann::json& album_json = (*iter)[TrackManager::ALBUM_JSON_KEY];

        //Ensure that the values are of the correct types
        if(!title_json.is_string() || !artists_json.is_array() || !album_json.is_string()) {

            main_queue.clear();
            return false;

        }

        std::vector<std::string> artists {};
        for(nlohmann::json::const_iterator artists_iter = artists_json.begin(); artists_iter != artists_json.end(); artists_iter++) {

            //Ensure that only strings exist inside the artists array
            if(!(*artists_iter).is_string()) {

                main_queue.clear();
                return false;

            }

            artists.push_back((*artists_iter).get<std::string>());

        }

        main_queue.push_back(Track(
            title_json.get<std::string>(),
            artists,
            album_json.get<std::string>()
        ));

    }

    //Read the added queue's tracklist
    const nlohmann::json& added_queue_json = root[ADDED_QUEUE_JSON_KEY];
    for(nlohmann::json::const_iterator iter = added_queue_json.begin(); iter != added_queue_json.end(); iter++) {

        if(!(*iter).contains(TrackManager::TITLE_JSON_KEY) || !(*iter).contains(TrackManager::ARTISTS_JSON_KEY) || !(*iter).contains(TrackManager::ALBUM_JSON_KEY)) {

            main_queue.clear();
            added_queue.clear();
            return false;

        }

        const nlohmann::json& title_json = (*iter)[TrackManager::TITLE_JSON_KEY];
        const nlohmann::json& artists_json = (*iter)[TrackManager::ARTISTS_JSON_KEY];
        const nlohmann::json& album_json = (*iter)[TrackManager::ALBUM_JSON_KEY];

        //Ensure that the values are of the correct types
        if(!title_json.is_string() || !artists_json.is_array() || !album_json.is_string()) {

            main_queue.clear();
            added_queue.clear();
            return false;

        }

        std::vector<std::string> artists {};
        for(nlohmann::json::const_iterator artists_iter = artists_json.begin(); artists_iter != artists_json.end(); artists_iter++) {

            //Ensure that only strings exist inside the artists array
            if(!(*artists_iter).is_string()) {

                main_queue.clear();
                added_queue.clear();
                return false;

            }

            artists.push_back((*artists_iter).get<std::string>());

        }

        added_queue.push_back(Track(
            title_json.get<std::string>(),
            artists,
            album_json.get<std::string>()
        ));

    }

    //Read everything else
    main_position = root[MAIN_QUEUE_POSITION_JSON_KEY];
    main_queue_playlist_id = root[MAIN_QUEUE_PLAYLIST_ID_JSON_KEY];
    added_position = root[ADDED_QUEUE_POSITION_JSON_KEY];

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

    settings.clear();

    std::ifstream input {std::string(STACCATO_SETTINGS_PATH)};
    if(!input.is_open()) {

        return;

    }

    const nlohmann::json root = nlohmann::json::parse(input);
    for(auto& item: root.items()) {

        if(item.value().type() == nlohmann::json::value_t::string) {

            settings.insert({item.key(), item.value().get<std::string>()});

        } else if(item.value().type() == nlohmann::json::value_t::number_integer || item.value().type() == nlohmann::json::value_t::number_unsigned) {

            settings.insert({item.key(), item.value().get<int>()});

        } else if(item.value().type() == nlohmann::json::value_t::number_float) {

            settings.insert({item.key(), item.value().get<double>()});

        }

    }

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

