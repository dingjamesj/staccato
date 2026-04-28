#ifndef PLAYLIST_HPP
#define PLAYLIST_HPP

#include <string>
#include <vector>
#include <filesystem>
#include <iostream>
#include <random>
#include <unordered_set>

namespace staccato {

    struct Track;
    enum class urltype;
    enum class sortmode;

    /// @brief Contains an ID, a name, an online connection URL, and a tracklist.
    class Playlist {

        private:

        std::string id_;
        std::string name_;
        std::vector<Track> tracklist_;
        std::string online_connection_;
        
        //Helper functions
        static urltype get_url_type(const std::string& url);

        public:

        /// @brief Creates a Playlist object
        /// @param id
        /// @param name 
        /// @param tracklist 
        /// @param online_connection 
        Playlist(
            const std::string& id,
            const std::string& name, 
            const std::vector<Track>& tracklist, 
            const std::string& online_connection
        );

        /// @brief Creates an empty Playlist object. Encountering an empty Playlist object should signify that an error occurred.
        Playlist();

        //==================================
        //        ONLINE CONNECTIONS        
        //==================================

        /// @brief Sets the online connection (as a URL) regardless of the connection's validity
        /// @param url 
        void set_online_connection(const std::string& url);

        /// @brief Removes the online connection
        void remove_online_connection();

        /// @return The online connection URL
        std::string online_connection() const;

        //==================================
        //       TRACKLIST FUNCTIONS
        //==================================

        /// @return A const ref to the tracklist as an unordered_multiset
        const std::vector<Track>& tracklist() const;

        /// @brief Takes the tracklist and sorts it based on an attribute. Note that some attributes require usage of TrackManager.
        /// @param sort_mode The attribute to sort the tracklist with (e.g. track title, duration, bitrate)
        /// @param is_ascending Decides if the sort is ascending or descending
        /// @return A *brand new* vector containing the sorted tracklist
        std::vector<Track> get_sorted_tracklist(sortmode sort_mode, bool is_ascending) const;

        /// @brief Adds a track to the tracklist
        /// @param track 
        void add_track(const Track& track);

        /// @brief Adds a track to the tracklist at the index
        /// @param track 
        /// @param index 
        void add_track(const Track& track, std::size_t index);

        /// @brief Removes a track from the tracklist
        /// @param index
        /// @return `true` if `index` is in range
        bool remove_track(std::size_t index);

        /// @param track 
        /// @return `true` if the track is in the tracklist, `false` otherwise
        bool contains_track(const Track& track) const;

        //----- REQUIRES THE INTERNET -----

        /// @brief Adds all tracks from the online connection that aren't already in the tracklist
        void add_tracks_from_online_connection();

        //==================================
        //               MISC               
        //==================================

        const std::string& id() const;

        void set_id(std::string id);

        const std::string& name() const;

        void set_name(std::string name);

        /// @brief Used to see if an error was encountered (empty Playlist objects should signify that an error occurred)
        /// @return `true` if this Playlist object is empty, `false` otherwise
        bool is_empty() const;
        
        /// @return A string representation of this Playlist object
        std::string string() const;

        bool operator==(const Playlist& other) const;

        bool operator==(Playlist&& other) const;

        static std::string create_random_id(std::size_t id_length);

    };

}

//For debugging purposes
std::ostream& operator<<(std::ostream& os, const staccato::Playlist& track);

#endif