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

[SORT]
PINNED: CUSTOM  <-- options are CUSTOM and ALPHA

[ZOOM]
PINNED
1                   <-- Ranges from 0 to 2
PLAYLISTS
1                   <-- Again ranges from 0 to 2

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

        /// @brief A vector of tuples-- each tuple represents an item. Each tuple begins with a bool, has one string, a string vector, and another string. If the bool is true, then the tuple represents a Track, otherwise a Playlist. If it represents a Playlist, the other parts of the tuple represent the name, simple properties, ID, and its artwork's absolute file path. If it represents a Track, the other parts represent the title, artists, album, and its audio file's absolute path. This property acts like a buffer so every change to the pinned items doesn't require a write to the hard drive.
        static std::vector<std::tuple<bool, std::string, std::vector<std::string>, std::string>> pinned_items;

        /// @brief The zoom level of the pinned items UI
        static int pinned_items_zoom_level;

        /// @brief The zoom level of the playlists UI
        static int playlists_zoom_level;

        /// @brief The sort mode of the pinned items UI
        static std::string pinned_items_sort_mode;

        //Helper functions

        /// @brief Reads the sort mode settings for the UI (i.e. custom order, alphabetical, by title, by artist, etc.)
        /// @param input 
        /// @param text 
        static void read_settings_sort_modes(std::ifstream& input, std::string& text);

        /// @brief Reads the zoom settings for the UI (e.g. how big the playlist tiles are for the playlist selection menu)
        /// @param input 
        /// @param text 
        static void read_settings_zoom_levels(std::ifstream& input, std::string& text);

        /// @brief Reads the pinned items
        /// @param input 
        /// @param text 
        static void read_settings_pinned_items(std::ifstream& input, std::string& text);

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
        /// @return A const ref of the `pinned_items` field
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

        /// @brief Used to get the pinned items UI zoom level
        /// @return The `pinned_items_zoom_level` field
        static int get_pinned_items_zoom_level();
        
        /// @brief Used to get the playlists UI zoom level
        /// @return The `playlists_zoom_level` field
        static int get_playlists_zoom_level();

        /// @brief Used to get the pinned items UI sort mode
        /// @return The `pinned_items_sort_mode` field
        static std::string get_pinned_items_sort_mode();

        /// @brief Used to set the pinned items UI zoom level
        /// @param zoom_level 
        static void set_pinned_items_zoom_level(int zoom_level);

        /// @brief Used to set the playlists UI zoom level
        /// @param zoom_level
        static void set_playlists_zoom_level(int zoom_level);

        /// @brief Used to set the pinned items UI sort mode
        /// @param sort_mode 
        static void set_pinned_items_sort_mode(const std::string& sort_mode);

        /// @brief Serializes the settings to settings.config
        /// @return `true` if the serialization was successful, `false` otherwise
        static bool serialize_settings();

        /// @brief Used to find the absolute path of the playlist image, since the path could have various file extensions
        /// @param playlist_id 
        /// @return The absolute path of the playlist image
        static std::string get_playlist_image_path(const std::string& playlist_id);

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
        
        static constexpr std::string_view PLACEHOLDER_ART_PATH {":/staccato/src/ui/resources/placeholder.jpg"};

        #if(DEVELOPMENT_BUILD)

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"..\\settings.config"};
        static constexpr std::string_view QUEUE_STORAGE_PATH {"..\\lastsession.dat"};
        static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"..\\playlists\\images"};
        #else
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"../settings.config"};
        static constexpr std::string_view QUEUE_STORAGE_PATH {"../lastsession.dat"};
        static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"../playlists/images"};
        #endif

        #else

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"settings.config"};
        static constexpr std::string_view QUEUE_STORAGE_PATH {"lastsession.dat"};
        static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"playlists\\images"};
        #else
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"settings.config"};
        static constexpr std::string_view QUEUE_STORAGE_PATH {"lastsession.dat"};
        static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"playlists/images"};
        #endif

        #endif

    };
    
}

#endif