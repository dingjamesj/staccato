#ifndef APP_MANAGER_HPP
#define APP_MANAGER_HPP

// =========================================================================================
// 
//                SET TO TRUE DURING DEVELOPMENT, SET TO FALSE WHEN DEPLOYED.             
//   USED TO ADJUST FOR THE DIFFERENCE BETWEEN DEVELOPMENT AND DEPLOYED PROJECT STRUCTURE.

#define DEVELOPMENT_BUILD true

// =========================================================================================

#include <string>
#include <iostream>
#include <fstream>
#include <vector>

/*

queue.dat file format:

File version
\0
Main queue's playlist ID
\0
Main queue position
Added queue position
Main queue title 1     <-- Beginning of main queue tracklist
\0
Main queue artist 1
\0
Main queue artist 2
\0
\0                     <-- Again, note that double null chars signify the end of the artists list
Main queue album
\0
Main queue track title 2
\0
Main queue artist 2
\0
\0
Main queue album 2
\0
\0                   <-- End of main queue tracklist signified by double null chars
Added queue title 1  <-- Beginning of added queue tracklist
\0
Added queue artist 1
\0
\0
Added queue album 1
\0
...

settings.config file format: (note that this is a text file, as opposed to a binary file)

[PINNED]
"playlist" or "track"     <-- Depending on if this pinned item is a playlist or a track
Playlist ID
Playlist name
Playlist size
Online connection

"playlist" or "track"
Playlist ID
Playlist name
Playlist size             <-- Note that this pinned playlist has no online connection 
 
"playlist" or "track"
Track title
Track album
Track artist 1
Track artist 2

"playlist" or "track"
...

*/

/* Familiarize yourself with some terminology:

Main/added queue -- When a user hits "play" on a playlist, the playlist's tracklist is put into the "main queue."
                    When a user adds individual tracks to the queue (e.g. using the "add to queue" feature), those
                    tracks are added to the "added queue." In the app, the added queue tracks should be played before
                    main queue tracks, but the added queue tracks are not kept in the queue (i.e. when an added 
                    track is finished playing, it leaves the queue and rewinding will not play it).
                    The reason for this queue separation is to allow for shuffling and unshuffling mid-queue. 
                    Think about it: how would you unshuffle a queue if the user added tracks that aren't from the
                    original playlist?
                    Final note: this queue behavior emulates Spotify's queue behavior.

*/

namespace staccato {

    struct Track;

    class AppManager {

        private:

        /// @brief The "main" tracklist that is playing-- in other words, the currently playing playlist
        static std::vector<Track> main_queue;

        /// @brief The tracklist composed of tracks that the user manually added using the "add to queue" feature
        static std::vector<Track> added_queue;

        /// @brief A vector of tuples-- each tuple represents an item. Each tuple begins with a bool, has one string, a string vector, and another string. If the bool is true, then the tuple represents a Track, otherwise a Playlist. If it represents a Playlist, the other parts of the tuple represent the name, simple properties, and ID. If it represents a Track, the other parts represent the title, artists, and album. This property acts like a buffer so every change to the pinned items doesn't require a write to the hard drive.
        static std::vector<std::tuple<bool, std::string, std::vector<std::string>, std::string>> pinned_items;

        public:

        /// @brief Used for reading the queue from the last staccato session's saved data, so that the user can continue where they left off. Should be ran once at the beginning of the program.
        /// @return A tuple of the saved main queue's playlist ID, main queue position, and added queue position.
        static bool read_last_session_data(std::string& main_queue_playlist_id, std::uint64_t& main_position, std::uint64_t& added_position);

        /// @brief Used to get the main queue
        /// @return A const ref to the main queue Track vector
        static const std::vector<Track>& get_main_queue();

        /// @brief Used to get the added queue
        /// @return A const ref to the added queue Track vector
        static const std::vector<Track>& get_added_queue();

        /// @brief Used to set the main queue
        /// @param tracklist 
        static void set_main_queue(const std::vector<Track>& tracklist);

        /// @brief Removes the main queue track at an index
        /// @param index
        /// @return `false` if the index is out of bounds, `true` otherwise
        static bool remove_main_queue_track(std::size_t index);

        /// @brief In the main queue, moves the track at `original_index` to `new_index`
        /// @param original_index 
        /// @param new_index 
        /// @return `false` if either index is out of bounds, `true` otherwise
        static bool move_main_queue_track(std::size_t original_index, std::size_t new_index);

        /// @brief Used to add tracks to the added queue
        /// @param tracklist 
        static void push_back_added_queue(const Track& track);

        /// @brief Used to remove tracks from the added queue
        static void pop_front_added_queue();

        /// @brief Removes the added queue track at an index
        /// @param index
        /// @return `false` if the index is out of bounds, `true` otherwise
        static bool remove_added_queue_track(std::size_t index);

        /// @brief In the added queue, moves the track at `original_index` to `new_index`
        /// @param original_index 
        /// @param new_index 
        /// @return `false` if either index is out of bounds, `true` otherwise
        static bool move_added_queue_track(std::size_t original_index, std::size_t new_index);

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
        //                                      DEBUGGING                                      
        //=====================================================================================

        /// @brief Prints the main queue tracklist to std::cout in order
        static void print_main_queue();

        /// @brief Prints the added queue tracklist to std::cout in order
        static void print_added_queue();

        /// @brief Prints the pinned items to std::cout in order
        static void print_pinned_items();

        //=====================================================================================
        //                                      CONSTANTS                                      
        //=====================================================================================

        /// @brief The header that appears on all .sply files and on the .stkl file. Should be in the format: "staccato[version number]"
        static constexpr std::string_view FILE_HEADER {"staccato1"};

        #if(DEVELOPMENT_BUILD)

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"..\\settings.config"};
        static constexpr std::string_view QUEUE_STORAGE_PATH {"..\\lastsession.dat"};
        #else
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"../settings.config"};
        static constexpr std::string_view QUEUE_STORAGE_PATH {"../lastsession.dat"};
        #endif

        #else

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"settings.config"};
        static constexpr std::string_view QUEUE_STORAGE_PATH {"lastsession.dat"};
        #else
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"settings.config"};
        static constexpr std::string_view QUEUE_STORAGE_PATH {"lastsession.dat"};
        #endif

        #endif

    };
    
}

#endif