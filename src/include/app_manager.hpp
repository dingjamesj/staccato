#ifndef APP_MANAGER_HPP
#define APP_MANAGER_HPP

// =========================================================================================
// 
//                SET TO TRUE DURING DEVELOPMENT, SET TO FALSE WHEN DEPLOYED.             
//   USED TO ADJUST FOR THE DIFFERENCE BETWEEN DEVELOPMENT AND DEPLOYED PROJECT STRUCTURE.

#define DEVELOPMENT_BUILD true

// =========================================================================================

#include "track.hpp"
#include "util.hpp"

#include <string>
#include <iostream>
#include <fstream>
#include <vector>

namespace staccato {

    class AppManager {

        private:

        static std::vector<Track> main_queue; //The "main" tracklist that is playing-- in other words, the currently playing playlist
        static std::vector<Track> added_queue; //The tracklist composed of tracks that the user manually added using the "add to queue" feature
        static std::vector<std::tuple<bool, std::string, std::vector<std::string>, std::string>> pinned_items; //A vector of tuples-- each tuple represents an item. Each tuple begins with a bool, has one string, a string vector, and another string. If the bool is true, then the tuple represents a Track, otherwise a Playlist. If it represents a Playlist, the other parts of the tuple represent the name, simple properties, and ID. If it represents a Track, the other parts represent the title, artists, and album. This property acts like a buffer so every change to the pinned items doesn't require a write to the hard drive.

        public:

        /// @brief Used for reading the queue from the last staccato session's saved data, so that the user can continue where they left off. Should be ran once at the beginning of the program.
        /// @return A tuple of the saved main queue's playlist ID, main queue position, and added queue position.
        static bool read_last_session_data(std::string& main_queue_playlist_id, std::uint64_t& main_position, std::uint64_t& added_position);

        /// @brief Used to get the saved main queue from the last session (should be used once at the beginning of the program)
        /// @return A const ref to the main queue Track vector
        static const std::vector<Track>& get_saved_main_queue();

        /// @brief Used to get the saved added queue from the last session (should be used once at the beginning of the program)
        /// @return A const ref to the added queue Track vector
        static const std::vector<Track>& get_saved_added_queue();

        static void set_main_queue(const std::vector<Track>& tracklist);

        static void set_added_queue(const std::vector<Track>& tracklist);

        /// @brief Used to save the track queue to the hard drive, so that when the user opens staccato later, they can continue where they left off. Should be ran once at the end of the program.
        /// @param main_queue_playlist_id 
        /// @param main_position
        /// @param added_position
        /// @return `true` if the serialization was successful, `false` otherwise
        static bool serialize_session_data(const std::string& main_queue_playlist_id, std::uint64_t main_position, std::uint64_t added_position);

        /// @brief Reads the settings (updates the `pinned_items` property). Should only be called at the beginning of the program since settings are stored as static variables at runtime.
        static void read_settings();

        /// @brief Used to get the pinned items
        /// @return Returns a const ref of the `pinned_items` field
        static const std::vector<std::tuple<bool, std::string, std::vector<std::string>, std::string>>& get_pinned_items();

        /// @brief Adds a playlist to the `pinned_items` property (does not serialize to the file system)
        /// @param id 
        /// @return `true` if the playlist was not already pinned, `false` otherwise
        static bool add_pinned_playlist(const std::string& id, const std::string& name, std::uint64_t size, const std::string& online_connection);

        /// @brief Adds a track to the `pinned_items` property (does not serialize to the file system)
        /// @param track 
        /// @return `true` if the playlist was not already pinned, `false` otherwise
        static bool add_pinned_track(const Track& track);

        /// @brief Removes an item from the `pinned_items` property (does not serialize to the file system)
        /// @param id 
        /// @return `false` if the index is out of bounds, `true` otherwise
        static bool remove_pinned_item(std::size_t index);

        /// @brief Moves the pinned item at `original_index` to `new_index`
        /// @param original_index 
        /// @param new_index 
        /// @return `false` if either index is out of bounds, `true` otherwise
        static bool move_pinned_item(std::size_t original_index, std::size_t new_index);

        /// @brief Serializes the `pinned_items` property to the file system (specifically the settings file)
        /// @return `true` if the serialization was successful, `false` otherwise
        static bool serialize_settings();

        //=====================================================================================
        //                                      CONSTANTS                                      
        //=====================================================================================

        /// @brief The header that appears on all .sply files and on the .stkl file. Should be in the format: "staccato[version number]"
        static constexpr std::string_view FILE_HEADER {"staccato1"};

        #if(DEVELOPMENT_BUILD)

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"..\\settings.config"};
        static constexpr std::string_view QUEUE_STORAGE_PATH {"..\\last_session_data.dat"};
        #else
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"../settings.config"};
        static constexpr std::string_view QUEUE_STORAGE_PATH {"../last_session_data.dat"};
        #endif

        #else

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"settings.config"};
        static constexpr std::string_view QUEUE_STORAGE_PATH {"last_session_data.dat"};
        #else
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"settings.config"};
        static constexpr std::string_view QUEUE_STORAGE_PATH {"last_session_data.dat"};
        #endif

        #endif

    };
    
}

#endif