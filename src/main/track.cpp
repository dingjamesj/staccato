#include "track.hpp"

using namespace staccato;

Track::Track(std::string title, std::string artists, std::string album): 
    title{title}, 
    artists{artists}, 
    album{album} 
{}

void Track::print() const {

    std::cout << title << " by " << artists << " from " << album << std::endl;

}

bool Track::is_empty() const {

    return title.empty() && artists.empty() && album.empty();

}

bool Track::operator==(const Track& other) const {

    return (title + " " + artists + " " + album) == (other.title + " " + other.artists + " " + other.album);

}
