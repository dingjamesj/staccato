#include "file_manager.hpp"

using namespace staccato;

std::unordered_map<Track, std::pair<std::string, std::string>> FileManager::track_dict = {};

bool FileManager::track_file_exists(const Track& track) {

    if(!track_dict.contains(track)) {

        return false;

    }

    std::string track_file_path = track_dict[track].first;
    return std::filesystem::is_regular_file(track_file_path);

}

bool FileManager::delete_track(const Track& track) {

    std::string file_path = track_dict[track].first;
    track_dict.erase(track);
    if(!std::filesystem::is_regular_file(file_path)) {

        return false;

    }

    return std::filesystem::remove(file_path);

}

const std::unordered_map<Track, std::pair<std::string, std::string>>& FileManager::get_track_dict() {

    return track_dict;

}

void FileManager::print_track_dict() {

    std::cout << "BEGIN FileManager::print_track_dict()" << std::endl << std::endl;
    for(std::pair<const staccato::Track, std::pair<std::string, std::string>> key_value: track_dict) {

        std::cout << "K: " << std::format("({}, {}, {})", key_value.first.title, key_value.first.artists, key_value.first.album) << std::endl << "V: " << key_value.second.first << " " << key_value.second.second << std::endl << std::endl;

    }
    std::cout << "END FileManager::print_track_dict()" << std::endl;

}

Track FileManager::import_track(std::string path) {

    TagLib::FileRef file_ref(path.c_str());

    //Signify failed import when the file is not an audio file
    if(file_ref.isNull()) {

        return Track("null", "null", "null");

    }

    TagLib::Tag* tag = file_ref.tag();
    Track track(tag->title().to8Bit(true), tag->artist().to8Bit(true), tag->album().to8Bit(true));
    
    //Signify failed import when the track is already in track_dict
    if(track_dict.contains(track)) {

        return Track("", "", "");

    }

    //If the track has no metadata, we will give it metadata since returning an empty Track signifies an error in the import process.
    if(track.is_empty()) {

        track = Track("Unknown Title", "Unknown Artists", "Unknown Album");

    }

    track_dict[track] = std::pair<std::string, std::string>(path, "");
    return track;

}
