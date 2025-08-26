#include "util.hpp"
#include "track.hpp"
#include "playlist.hpp"

using namespace staccato;

Playlist::Playlist(
    std::string name, 
    std::string cover_image_file_path, 
    const std::unordered_multiset<Track>& tracklist, 
    std::string online_connection
): 
    name{name},
    cover_image_file_path{cover_image_file_path},
    tracklist{std::move(tracklist)},
    online_connection{online_connection}
{

    set_online_connection(online_connection);

}

void Playlist::set_online_connection(std::string url) {

    //validate url

}

void Playlist::remove_online_connection() {

    online_connection = "";

}

std::string Playlist::get_online_connection() const {

    return online_connection;

}

const std::unordered_multiset<Track>& Playlist::get_tracklist() const {

    return tracklist;

}

void Playlist::add_track(Track track) {

    tracklist.insert(track);

}

bool Playlist::remove_track(Track track) {

    std::unordered_multiset<staccato::Track>::iterator iter = tracklist.find(track);
    if(iter == tracklist.end()) {

        return false;

    }

    tracklist.erase(iter);
    return true;

}

bool Playlist::contains_track(Track track) const {

    return tracklist.contains(track);

}

int Playlist::get_total_duration() const {

    int total_duration = 0;
    std::unordered_multiset<staccato::Track>::const_iterator iter = tracklist.cbegin();
    for(; iter != tracklist.end(); iter++) {

        // total_duration += iter->;

    }

    return total_duration;

}