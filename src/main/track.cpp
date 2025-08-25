#include "track.hpp"
#include "util.hpp"
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

bool Track::file_readable() const {

    return FileManager::track_file_exists(*this);

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