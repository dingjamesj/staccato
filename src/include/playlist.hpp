#ifndef PLAYLIST_HPP
#define PLAYLIST_HPP

#include <string>
#include <vector>
#include <unordered_set>
#include <filesystem>
#include "track.hpp"

namespace staccato {

enum class SortMode {
    TITLE_ASCENDING, TITLE_DESCENDING, 
    ARTISTS_ASCENDING, ARTISTS_DESCENDING,
    ALBUM_ASCENDING, ALBUM_DESCENDING,
    DURATION_ASCENDING, DURATION_DESCENDING,
    BITRATE_ASCENDING, BITRATE_DESCENDING,
    FILE_EXT_ASCENDING, FILE_EXT_DESCENDING
};

class Playlist {

private:
    std::vector<char> cover_image_raw;
    std::unordered_multiset<Track> tracklist;
    std::string directory_connection;
    std::string online_connection;

    Playlist(
        std::string name, 
        const std::vector<char> cover_image_raw, 
        const std::unordered_multiset<Track> tracklist, 
        std::string directory_connection, 
        std::string online_connection
    );

public:
    std::string name;

    //=================
    //  Playlist info
    //=================

    void set_cover_image(std::string image_path);
    void remove_cover_image();
    const std::vector<char>& get_cover_image_raw() const;
    
    //===============
    //  Connections
    //===============

    void set_directory_connection(std::string directory);
    void remove_directory_connection();
    std::string get_directory_connection() const;
    void set_online_connection(std::string url);
    void remove_online_connection();
    std::string get_online_connection() const;

    //=============
    //  Tracklist
    //=============

    /**
     * @return A const ref to the tracklist
     * 
     * Note that this is a const ref.
     */
    const std::unordered_multiset<Track>& get_unordered_tracklist() const;
    /**
     * @param sort_mode The order which the tracklist should be sorted in
     * @return A vector of all tracks in the playlist in the order defined by sort_mode
     * 
     * Note that this returns a brand-new vector, sort of expensive
     */
    std::vector<Track> get_tracklist(SortMode sort_mode) const;
    /**
     * @brief Adds the track to the playlist
     * @param track The track to add
     * 
     * Note that duplicate tracks are allowed
     */
    void add_track(Track track);
    /**
     * @brief Removes the track from the playlist
     * @param track The track to remove
     * @return True if the track was removed successfully
     * 
     * Note that the removed track is based on the location of the track's file
     */
    bool remove_track(Track track);
    /**
     * @return True if the playlist's tracklist contains the track
     * 
     * Note that the tracks are found by matching file locations
     */
    bool contains_track(Track track) const;
    /**
     * @return The total duration of the playlist in seconds
     * 
     * Sums up the duration of all tracks in the playlist
     */
    int get_total_duration() const;

    //============================
    //  Public playlist creation
    //============================

    static Playlist read_playlist_file(std::string path);
    static int write_playlist_file(std::string path, Playlist playlist);

};

// const std::string Playlist::PLAYLIST_FOLDER_PATH {std::filesystem::path::preferred_separator + "playlists"};

}

#endif