#include "track.hpp"
#include "file_manager.hpp"

using namespace staccato;

Track::Track(std::string title, std::string artists, std::string album): 
    title{title}, 
    artists{artists}, 
    album{album} 
{}

void Track::print() const {

    std::cout << "BEGIN Track::print()" << std::endl << std::endl;
    std::cout << title << std::endl << artists << std::endl << album << std::endl << std::endl;
    std::cout << "END Track::print()" << std::endl;

}

std::string Track::get_path() const {

    if(!FileManager::track_dict.contains(*this)) {

        return "";

    }

    return FileManager::track_dict.at(*this).first;

}

bool Track::has_valid_audio_file() const {

    return FileManager::path_is_readable_audio_file(get_path());

}

bool Track::delete_file() {

    bool result = FileManager::delete_track(*this);
    
    if(result) {

        title = "";
        artists = "";
        album = "";

    }

    return result;

}

bool Track::is_empty() const {

    return title.empty() && artists.empty() && album.empty();

}

bool Track::operator==(const Track& other) const {

    return std::format("{} {} {}", title, artists, album) == std::format("{} {} {}", other.title, other.artists, other.album);

}

Track Track::import_track(std::string path) {

    TagLib::FileRef file_ref(path.c_str());

    //Signify failed import when the file is not an audio file
    if(file_ref.isNull()) {

        return Track("", "", "");

    }

    TagLib::Tag* tag = file_ref.tag();
    Track track(tag->title().to8Bit(true), tag->artist().to8Bit(true), tag->album().to8Bit(true));
    
    //Signify failed import when the track is already in track_dict
    if(FileManager::track_dict.contains(track)) {

        return Track("", "", "");

    }

    //If the track has no metadata, we will give it metadata since returning an empty Track signifies an error in the import process.
    if(track.is_empty()) {

        track = Track("Unknown Title", "Unknown Artists", "Unknown Album");

    }

    //Add track to staccato's track dictionary
    FileManager::track_dict[track] = std::pair<std::string, std::string>(path, "");

    return track;

}