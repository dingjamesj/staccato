#ifndef PLAYLIST_HPP
#define PLAYLIST_HPP

#include <string>
#include <vector>
#include <unordered_set>
#include "track.hpp"

namespace staccato {

class Playlist {

private:
    std::vector<char> cover_image_raw;
    std::unordered_set<Track> tracklist;
    std::string directory_connection;
    std::string online_connection;

public:
    //For playlist information
    std::string name;
    void set_cover_image(std::string image_path);
    void remove_cover_image();
    const std::vector<char>& get_cover_image_raw() const;
    
    //Connections
    void add_directory_connection(std::string directory);
    void remove_directory_connection();
    std::string get_directory_connection() const;
    void add_online_connection(std::string url);
    void remove_online_connection();
    std::string get_online_connection() const;

    //Tracklist info
    const std::unordered_set<Track>& get_tracklist() const;
    int add_track(Track track);
    int remove_track(Track track);
    bool track_exists(Track track) const;
    int get_total_duration() const;

    Playlist(std::string name, const std::vector<char>& cover_image_raw, const std::unordered_set<Track>& tracklist, std::string directory_connection, std::string online_connection);

};

Playlist read_playlist_file(std::string path);
int write_playlist_file(std::string path, Playlist playlist);

}

#endif