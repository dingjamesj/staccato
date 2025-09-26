#ifndef PLAYLIST_HPP
#define PLAYLIST_HPP

#include "util.hpp"

#include <string>
#include <vector>
#include <unordered_set>
#include <filesystem>
#include <iostream>
#include <Python.h>

namespace staccato {

    struct Track;

    class Playlist {

    private:
        std::unordered_multiset<Track> tracklist;
        
        //Helper functions
        static urltype get_url_type(const std::string& url);

    public:
        std::string name;
        std::string online_connection;

        Playlist(
            const std::string& name, 
            const std::unordered_multiset<Track>& tracklist, 
            const std::string& online_connection
        );

        Playlist();

        //Connections

        /** If the URL is valid, then it is set as the online connection. Returns false if the URL isn't valid*/
        bool set_online_connection(const std::string& url);
        /** Removes the online connection */
        void remove_online_connection();
        /** Returns the online connection's URL */
        std::string get_online_connection() const;

        //Tracklist

        /** Returns a const ref to the tracklist as an unordered_multiset */
        const std::unordered_multiset<Track>& get_tracklist() const;
        /** Returns a sorted tracklist as a vector */
        std::vector<Track> get_sorted_tracklist(sortmode sort_mode, bool is_ascending) const;
        /** Adds a track to the tracklist */
        void add_track(const Track& track);
        /** Adds all tracks not yet in the tracklist that are found in the online connection */
        void add_tracks_from_online_connection();
        /** Removes a track from the tracklist, returns true if was successfully removed */
        bool remove_track(const Track& track);
        /** Check if a track is in the tracklist */
        bool contains_track(const Track& track) const;
        /** Returns a string representation of this playlist */
        std::string string() const;
        /** Returns if the playlist is empty (denoting an invalid playlist) */
        bool is_empty() const;

    };

}

std::ostream& operator<<(std::ostream& os, const staccato::Playlist& track);

#endif