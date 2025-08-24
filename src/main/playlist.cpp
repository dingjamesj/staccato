#include "playlist.hpp"
#include "track.hpp"
#include <filesystem>

using namespace staccato;

void Playlist::set_cover_image(std::string image_path) {

    //Check how Qt wants its raw image data

}

void Playlist::remove_cover_image() {

    cover_image_raw.clear();

}

const std::vector<char>& Playlist::get_cover_image_raw() const {

    return cover_image_raw;

}

void Playlist::add_directory_connection(std::string directory) {

    if(std::filesystem::is_directory(directory)) {

        directory_connection = directory;

    } else {

        directory_connection = "";

    }

}

void Playlist::remove_directory_connection() {

    directory_connection = "";

}

std::string Playlist::get_directory_connection() const {

    return directory_connection;

}

void Playlist::add_online_connection(std::string url) {

    //validate url

}

void Playlist::remove_online_connection() {

    online_connection = "";

}

std::string Playlist::get_online_connection() const {

    return online_connection;

}

const std::unordered_set<Track>& Playlist::get_tracklist() const {

    return tracklist;

}

int Playlist::add_track(Track track) {

    tracklist.insert(track);

}

bool Playlist::track_exists(Track track) const {

    //Use the current sort setting to binary search the tracklist for the track
    return tracklist.contains(track);

}