#include "track_manager.hpp"

using namespace staccato;

std::unordered_map<Track, std::string> TrackManager::track_dict = {};

bool TrackManager::track_has_readable_file(const std::string& path) {

    //Check if the file is an audio file

    TagLib::FileRef file_ref(path.c_str());
    if(file_ref.isNull()) {

        return false;

    }

    TagLib::AudioProperties* audio_properties = file_ref.audioProperties();
    if(audio_properties == nullptr || audio_properties->lengthInSeconds() == 0) {

        return false;

    }

    //Check if the file is openable

    std::ifstream file(path, std::ios::binary);
    return file.is_open();

}

bool TrackManager::delete_track(const Track& track) {

    if(!track_dict.contains(track)) {

        return false;

    }

    std::string file_path = track_dict[track];
    track_dict.erase(track);
    return std::filesystem::remove(file_path);

}

std::string TrackManager::get_sply_file_name(const std::string& id, const std::string& playlist_name) {

    return id + " " + playlist_name.substr(0, 40);

}

void TrackManager::print_track_dict() {

    int count = 0;
    for(std::pair<const staccato::Track, std::string> key_value: track_dict) {

        std::cout << "K: " << key_value.first.title + " " + key_value.first.artists + " " + key_value.first.album << std::endl << "V: " << key_value.second << std::endl;

        if(count < track_dict.size() - 1) {

            std::cout << std::endl;

        }

        count++;

    }

}
