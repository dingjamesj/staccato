#ifndef PLAYLIST_HPP
#define PLAYLIST_HPP

#include <string>
#include <vector>
#include <unordered_set>
#include <filesystem>
#include <iostream>
#include <chrono>

namespace staccato {

    struct Track;
    enum class urltype;
    enum class sortmode;

    /// @brief Contains a name, an online connection URL, and a tracklist. Note that playlists also have IDs associated with them, but this is simply stored in the .sply file names.
    class Playlist {

    private:
        std::string name_;
        std::unordered_multiset<Track> tracklist_;
        std::string online_connection_;
        std::int64_t last_played_time_;
        
        //Helper functions
        static urltype get_url_type(const std::string& url);

    public:

        /// @brief Creates a Playlist object
        /// @param name 
        /// @param tracklist 
        /// @param online_connection 
        Playlist(
            const std::string& name, 
            const std::unordered_multiset<Track>& tracklist, 
            const std::string& online_connection
        );

        /// @brief Creates an empty Playlist object. Encountering an empty Playlist object should signify that an error occurred.
        Playlist();

        //==================================
        //        ONLINE CONNECTIONS        
        //==================================

        /// @brief Only sets the online connection if the URL leads to an accessible online playlist
        /// @param url 
        /// @return `true` if the connection is set (i.e. if the URL leads to an accessible online playlist), `false` otherwise
        bool set_online_connection(const std::string& url);

        /// @brief Removes the online connection
        void remove_online_connection();

        /// @return The online connection URL
        std::string online_connection() const;

        //==================================
        //       TRACKLIST FUNCTIONS
        //==================================

        /// @return A const ref to the tracklist as an unordered_multiset
        const std::unordered_multiset<Track>& tracklist() const;

        /// @brief Takes the tracklist and sorts it based on an attribute. Note that some attributes require usage of TrackManager.
        /// @param sort_mode The attribute to sort the tracklist with (e.g. track title, duration, bitrate)
        /// @param is_ascending Decides if the sort is ascending or descending
        /// @return A *brand new* vector containing the sorted tracklist
        std::vector<Track> get_sorted_tracklist(sortmode sort_mode, bool is_ascending) const;

        /// @brief Adds a track to the tracklist
        /// @param track 
        void add_track(const Track& track);

        /// @brief Removes a track from the tracklist
        /// @param track 
        /// @return `true` if successfully removed, `false` otherwise (e.g. `false` if the track wasn't in the tracklist)
        bool remove_track(const Track& track);

        /// @param track 
        /// @return `true` if the track is in the tracklist, `false` otherwise
        bool contains_track(const Track& track) const;

        //----- REQUIRES THE INTERNET -----

        /// @brief Adds all tracks from the online connection that aren't already in the tracklist
        void add_tracks_from_online_connection();

        //==================================
        //               MISC               
        //==================================

        const std::string& name() const;

        void set_name(std::string name);

        int64_t last_played_time() const;

        /// @brief Sets the last played time of this playlist to the current time
        void set_last_played_time_to_now();

        /// @brief Used to see if an error was encountered (empty Playlist objects should signify that an error occurred)
        /// @return `true` if this Playlist object is empty, `false` otherwise
        bool is_empty() const;
        
        /// @return A string representation of this Playlist object
        std::string string() const;

    };

}

//For debugging purposes
std::ostream& operator<<(std::ostream& os, const staccato::Playlist& track);

#endif