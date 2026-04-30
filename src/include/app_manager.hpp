#ifndef APP_MANAGER_HPP
#define APP_MANAGER_HPP

//=======================================================================================
// Main job of AppManager is to organize the application's settings and persistent data.
// It also controls anything else that is unrelated to data contained within Track and 
// Playlist objects (i.e. playlist images).
//=======================================================================================

//Used to adjust for the difference between development and deployed project structure
#define DEVELOPMENT_BUILD true

#include <string>
#include <iostream>
#include <fstream>
#include <vector>
#include <unordered_map>
#include <variant>
#include <nlohmann/json_fwd.hpp>

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
    class Playlist;
    class PlaylistTree;

    class AppManager {

        private:

        /// @brief The "main" tracklist that is playing-- in other words, the currently playing playlist
        static std::vector<Track> main_queue;

        /// @brief The tracklist composed of tracks that the user manually added using the "add to queue" feature
        static std::vector<Track> added_queue;

        /// @brief Recently played Playlists & singular Tracks. Index 0 contains the LEAST recent.
        static std::vector<std::variant<Track, Playlist>> recents;

        /// @brief Maps setting names to their values. Values can be strings, ints, or doubles (signed).
        static std::unordered_map<std::string, std::variant<std::string, int, double, std::vector<std::string>>> settings;

        public:

        /// @brief Used for reading the queue from the last staccato session's saved data, so that the user can continue where they left off. Should be ran once at the beginning of the program.
        /// @return A tuple of the saved main queue's playlist ID, main queue position, and added queue position.
        static bool read_persistent_data(std::string& main_queue_playlist_id, unsigned int& main_position, unsigned int& added_position);

        /// @brief Used to get the main queue
        /// @return A const ref to the main queue Track vector
        static const std::vector<Track>& get_main_queue();

        /// @brief Used to get the added queue
        /// @return A const ref to the added queue Track vector
        static const std::vector<Track>& get_added_queue();

        /// @brief Used to get the recently played items. Index 0 contains the LEAST recent and the last index contains the MOST recent.
        /// @return A const ref to the `recents` vector
        static const std::vector<std::variant<Track, Playlist>>& get_recents();

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

        /// @brief Adds a Playlist or Track to the recently played list, popping the oldest item if the list is at capacity.
        /// @param item 
        static void add_recently_played_item(std::variant<Track, Playlist> item);

        /// @brief Used to save the track queue to the hard drive, so that when the user opens staccato later, they can continue where they left off. Should be ran once at the end of the program.
        /// @param main_queue_playlist_id 
        /// @param main_position
        /// @param added_position
        /// @return `true` if the serialization was successful, `false` otherwise
        static bool serialize_persistent_session_data(const std::string& main_queue_playlist_id, unsigned int main_position, unsigned int added_position);

        /// @brief Reads the settings (updates the `pinned_items` property). Should only be called at the beginning of the program since settings are stored as static variables at runtime.
        static void read_settings();

        /// @brief Serializes the settings to settings.config
        /// @return `true` if the serialization was successful, `false` otherwise
        static bool serialize_settings();

        /// @brief Used to find the absolute path of the playlist image, since the path could have various file extensions
        /// @param playlist_id 
        /// @return The absolute path of the playlist image
        static std::string get_playlist_image_path(const std::string& playlist_id); //NOTE: AppManager controls playlist images because playlist images aren't controlled by the Playlist class

        /// @brief Used to set the cover image of a playlist, given the image location and playlist's ID
        /// @param image_path 
        /// @param playlist_id 
        /// @return `true` if successful, `false` otherwise
        static bool set_playlist_image(const std::string& image_absolute_path, const std::string& playlist_id);

        //=====================================================================================
        //                                      DEBUGGING                                      
        //=====================================================================================

        /// @brief Prints the main queue tracklist to std::cout in order
        static void print_main_queue();

        /// @brief Prints the added queue tracklist to std::cout in order
        static void print_added_queue();

        //=====================================================================================
        //                                      CONSTANTS                                      
        //=====================================================================================

        static constexpr std::string_view PLACEHOLDER_ART_PATH {":/staccato/src/ui/resources/placeholder.jpg"};

        static constexpr std::size_t RECENTS_CAPACITY {5};

        //Key names for the persistent data JSON
        static constexpr std::string_view MAIN_QUEUE_JSON_KEY {"mainQueue"};
        static constexpr std::string_view MAIN_QUEUE_POSITION_JSON_KEY {"mainQueuePosition"};
        static constexpr std::string_view MAIN_QUEUE_PLAYLIST_ID_JSON_KEY {"mainQueuePlaylistID"};
        static constexpr std::string_view ADDED_QUEUE_JSON_KEY {"addedQueue"};
        static constexpr std::string_view ADDED_QUEUE_POSITION_JSON_KEY {"addedQueuePosition"};

        #if(DEVELOPMENT_BUILD)

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"..\\settings.json"};
        static constexpr std::string_view PERSISTENT_DATA_PATH {"..\\persistent.json"};
        static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"..\\playlists\\images"};
        #else
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"../settings.json"};
        static constexpr std::string_view PERSISTENT_DATA_PATH {"../persistent.json"};
        static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"../playlists/images"};
        #endif

        #else

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"settings.json"};
        static constexpr std::string_view PERSISTENT_DATA_PATH {"persistent.json"};
        static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"playlists\\images"};
        #else
        static constexpr std::string_view STACCATO_SETTINGS_PATH {"settings.json"};
        static constexpr std::string_view PERSISTENT_DATA_PATH {"persistent.json"};
        static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"playlists/images"};
        #endif

        #endif

    };
    
}

#endif