#include "track.hpp"

using namespace staccato;

Track::Track(std::string title, std::string artists, std::string album): 
    title{title}, 
    artists{artists}, 
    album{album} 
{}

bool Track::is_empty() const {

    return title.empty() && artists.empty() && album.empty();

}

std::string Track::string() const {

    if(is_empty()) {

        return "";

    }

    return title + " by " + artists + " from " + album;
    
}

bool Track::operator==(const Track& other) const {

    return title == other.title && artists == other.artists && album == other.album;

}

std::ostream& operator<<(std::ostream& os, const Track& track) {

    os << track.string() << std::endl;
    return os;

}