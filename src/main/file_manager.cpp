#include "track.hpp"
#include "playlist.hpp"
#include "file_manager.hpp"

using namespace staccato;

std::unordered_map<Track, std::pair<std::string, std::string>> FileManager::track_dict = {};

bool FileManager::path_is_readable_audio_file(const std::string& path) {

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

bool FileManager::delete_track(const Track& track) {

    if(!track_dict.contains(track)) {

        track_dict.erase(track);
        return false;

    }

    std::string file_path = track_dict[track].first;
    track_dict.erase(track);
    return std::filesystem::remove(file_path);

}

void FileManager::print_track_dict() {

    std::cout << "BEGIN FileManager::print_track_dict()" << std::endl << std::endl;
    for(std::pair<const staccato::Track, std::pair<std::string, std::string>> key_value: track_dict) {

        std::cout << "K: " << std::format("({}, {}, {})", key_value.first.title, key_value.first.artists, key_value.first.album) << std::endl << "V: " << key_value.second.first << " " << key_value.second.second << std::endl << std::endl;

    }
    std::cout << "END FileManager::print_track_dict()" << std::endl;

}
