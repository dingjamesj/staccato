#ifndef PLAYLIST_HPP
#define PLAYLIST_HPP

#include <string>
#include <vector>
#include <unordered_set>
#include <filesystem>
#include <iostream>

namespace staccato {

    enum class sortmode;
    struct Track;

    class Playlist {

    private:
        std::unordered_multiset<Track> tracklist;

    public:
        std::string name;
        std::string cover_image_file_path;
        std::string online_connection;

        Playlist(
            std::string name, 
            std::string cover_image_file_path,
            const std::unordered_multiset<Track>& tracklist, 
            std::string online_connection
        );

        Playlist();

        //Connections

        /** If the URL is valid, then it is set as the online connection. Returns false if the URL isn't valid*/
        bool set_online_connection(std::string url);
        /** Removes the online connection */
        void remove_online_connection();
        /** Returns the online connection's URL */
        std::string get_online_connection() const;
        /** Returns the tracklist of the online connection */
        std::unordered_multiset<Track> get_online_connection_tracklist() const;

        //Tracklist

        /** Returns a const ref to the tracklist as an unordered_multiset */
        const std::unordered_multiset<Track>& get_tracklist() const;
        /** Returns a sorted tracklist as a vector */
        std::vector<Track> get_sorted_tracklist(sortmode sort_mode, bool is_ascending) const;
        /** Adds a track to the tracklist */
        void add_track(Track track);
        /** Removes a track from the tracklist, returns true if was successfully removed */
        bool remove_track(Track track);
        /** Check if a track is in the tracklist */
        bool contains_track(Track track) const;
        /** The total duration of the playlist */
        int get_total_duration() const;
        /** Returns a string representation of this playlist */
        std::string string() const;
        /** Returns if the playlist is empty (denoting an invalid playlist) */
        bool is_empty() const;

    };

}

std::ostream& operator<<(std::ostream& os, const staccato::Playlist& track);

#endif