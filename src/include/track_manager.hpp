#ifndef TRACK_MANAGER_HPP
#define TRACK_MANAGER_HPP

//========================================================
// Main job of TrackManager is the organize audio files.
// Controls data contained in Track and Playlist objects.
//========================================================

//Used to adjust for the difference between development and deployed project structure
#define DEVELOPMENT_BUILD true

#include <string>
#include <unordered_map>
#include <unordered_set>
#include <filesystem>
#include <iostream>
#include <fstream>
#include <fileref.h>
#include <attachedpictureframe.h>
#include <tfile.h>
#include <mp4file.h>
#include <mpegfile.h>
#include <oggfile.h>
#include <opusfile.h>
#include <vorbisfile.h>
#include <wavfile.h>
#include <flacfile.h>
#include <vector>
#include <bit>
#include <any>
#include <nlohmann/json_fwd.hpp>
#include <stack>
#include <Python.h>


/* Familiarize yourself with some terminology:

Track dictionary -- The core part of staccato's audio file tracking system. It maps Tracks (title, artist, album)
                    to file paths, and it is serialized as a JSON file found in the "tracks" directory.

Playlist tree    -- The tree that organizes user-created playlists in an ordered folder hierarchy. It only stores
                    playlist IDs, not actual Playlist objects.

Internal tracks  -- Audio files that staccato has recorded inside the track dictionary.

External tracks  -- Audio files that staccato has not recorded inside the track dictionary. These can be
                    tracks on the local computer system (a "local" track) or a track from an online streaming
                    service that isn't downloaded (an "online" track).

Local playlists  -- A playlist made by the user in staccato. Each has an associated JSON file with the filename being the playlist ID,
                    and can be expressed as a Playlist object.

Online playlists -- A playlist from an online streaming service (not made by the user in staccato) and is expressed as a 
                    vector of Track objects.

*/

namespace staccato {

    struct Track;
    class Playlist;
    class PlaylistTree;
    enum class audiotype;
    
    class TrackManager {

        private:

        /// @brief The unordered map that maps all Tracks to a file path as a std::string
        static std::unordered_map<Track, std::string> track_dict;

        /// @brief Stores the hierarchical structure of user-created playlists
        static PlaylistTree playlist_tree;

        //Helper functions

        /// @brief Adds enumerators to repeated file names (e.g. "Repeated File Name.mp3" and "Repeated File Name (1).mp3")
        /// @param path 
        /// @return A filesystem::path object with a unique file name
        static std::filesystem::path get_unique_filename(std::filesystem::path path);

        /// @param path 
        /// @param track 
        /// @return `true` if the write was successful, `false` otherwise
        static bool write_file_metadata(const std::string& path, const Track& track);

        /// @brief Used when reading the playlist tree. 
        ///        In the case of a JSON read error, we will just add all the playlists from the playlist directory to the root of the tree.
        /// @param id_set 
        static void populate_tree_unorganized();

        public:

        //=====================================================================================
        //                            READING EXTERNAL AUDIO FILES                            
        //=====================================================================================

        /// @param path 
        /// @return `true` if the file associated with the Track in the track dict is a readable audio file, `false` otherwise
        static bool path_is_readable_track_file(const std::string& path);

        /// @brief Gets info about a track from a local source (i.e. a file from the hard drive)
        /// @param path 
        /// @return A Track object representing the local track
        static Track get_local_track_info(const std::string& path);

        //=====================================================================================
        //                               READING INTERNAL TRACKS                               
        //=====================================================================================

        /// @brief Checks if staccato has an associated audio file for the Track object
        /// @param track 
        /// @return `true` if the track is a key in the track dictionary, `false` otherwise
        static bool track_is_in_dict(const Track& track);

        /// @param track 
        /// @return The path to the Track object's associated audio file as a string, empty string if the Track object has no associated audio file
        static std::string get_track_file_path(const Track& track);

        /// @param track 
        /// @return The duration in seconds of the Track object's associated audio file, `0` if the Track object has no associated audio file
        static int get_track_duration(const Track& track);

        /// @param track 
        /// @return The bitrate of the Track object's associated audio file in kbps, `0` if the Track object has no associated audio file
        static int get_track_bitrate(const Track& track);

        /// @param track 
        /// @return The file type of the Track object's associated audio file (supported types can be found in the enum `staccato::audiotype`)
        static audiotype get_track_file_type(const Track& track);

        /// @brief Short form of `get_track_artwork_raw(get_track_file_path(track))`
        /// @param track 
        /// @return The embedded artwork of the Track object's associated audio file as a vector of bytes (chars)
        static TagLib::ByteVector get_track_artwork(const Track& track);

        /// @param audio_file_path 
        /// @return The embedded artwork of the audio file as a vector of bytes (chars)
        static TagLib::ByteVector get_track_artwork(const std::string& audio_file_path);

        /// @brief To keep staccato's file tracking in sync, this should be ran in the background when staccato starts
        /// @return A vector of Tracks that are keys in the track dict and whose corresponding audio files are no longer accessible
        static std::vector<Track> find_missing_tracks();

        /// @brief To keep staccato's file tracking in sync, this should be ran in the background when staccato starts
        /// @return A vector of strings that are paths to audio files in staccato's "tracks" directory that aren't in the track dict (i.e. untracked audio files that were manually placed in the "tracks" directory by the user)
        static std::vector<std::string> find_extraneous_track_files();

        //=====================================================================================
        //                   ADDING, MODIFYING, AND DELETING INTERNAL TRACKS                   
        //=====================================================================================

        /// @brief Imports an audio file from the local filesystem by copying the file into staccato's "track" directory, writing its metadata according to param `track`, and then adding `track` and the copied file into the track dict. DOES NOTHING IF `track` ALREADY EXISTS IN THE TRACK DICT.
        /// @param path 
        /// @param track 
        /// @return `true` if copying the audio file was successful, `false` otherwise. NOTE : returns `true` if `track` is already a key in the track dict (vacuously since copying the audio file wasn't attempted)
        static bool import_track_from_filesystem(const std::string& path, const Track& track);

        /// @brief Removes the param `track` from the track dict and deletes its associated audio file
        /// @param track 
        /// @return `true` if deleting the audio file was successful, `false` otherwise. NOTE : returns `true` if `track` is not in the track dict (vacuously since the deletion wasn't attempted)
        static bool delete_track(const Track& track);

        /// @brief Edits a track-file pair by overwriting `original_track`'s audio file's metadata according to `new_track` and then replacing `original_track` with `new_track` in the track dict
        /// @param original_track The Track object to modify
        /// @param new_track A Track object containing the new metadata info
        /// @return `true` if the edit overwriting the audio file was successful, `false` otherwise. NOTE : returns `true` if `original_track` is not in the track dict or if `new_track` is already in the track dict (vacuously since editing the metadata wasn't attempted)
        static bool edit_track(const Track& original_track, const Track& new_track);

        /// @brief Sets a track's audio file's embedded artwork to an image from the local filesystem
        /// @param track 
        /// @param artwork_file_path The path to an image file (JPEG and PNG accepted)
        /// @return `true` if embedding the artwork was successful, `false` otherwise. NOTE : returns `true` if `track` is not in the track dict (vacuously since setting the artwork wasn't attempted)
        static bool set_track_artwork(const Track& track, const std::string& artwork_file_path);

        /// @brief Deletes a track's audio file's embedded artwork
        /// @param track 
        /// @return `true` if deleting the artwork was successful, `false` otherwise. NOTE : returns `true` if `track` is not in the track dict (vacuously since deleting the artwork wasn't attempted)
        static bool delete_track_artwork(const Track& track);

        //=====================================================================================
        //                                PLAYLISTS MANAGEMENT                        
        //=====================================================================================
        
        /// @brief This is when you want to know what playlists are saved in staccato (you don't want complete information including tracklist for each of them)
        /// @return A vector of tuples that contain each playlist's ID, name, online connection, and size
        static std::vector<std::tuple<std::string, std::string, std::string, unsigned int>> get_basic_playlist_info_from_files();

        /// @brief Adds a playlist to staccato (i.e. adds it to the PlaylistTree and creates an empty JSON file for it)
        /// @param name 
        /// @return The ID of the created playlist
        static std::string create_playlist(const std::string& name, const std::vector<std::string>& folder_hierarchy);

        /// @brief Removes a playlist from staccato (i.e. removes it from the PlaylistTree if found and deletes its JSON file if exists)
        /// @param id 
        /// @return `true` if the playlist was found in the tree and removed or if the playlist's JSON was deleted, `false` otherwise
        static bool remove_playlist(const std::string& id, const std::vector<std::string>& folder_hierarchy);

        /// @brief Used to get complete info on a Playlist given an ID
        /// @param id
        /// @return A reference to the `Playlist` object containing complete information about the playlist
        static Playlist get_playlist(const std::string& id, bool& error_flag);
        
        /// @brief Reads through each track in the tracklist and finds the track's associated file's audio length
        /// @param playlist 
        /// @return The total duration in seconds of the playlist
        static int get_playlist_duration(const Playlist& playlist);

        /// @brief Writes the playlist to its JSON file (creating one if needed)
        /// @param id
        /// @param playlist
        /// @return `true` if the serialization was successful, `false` otherwise
        static bool serialize_playlist(const Playlist& playlist);
        
        //=====================================================================================
        //                      ONLINE TRACK ACCESS (via. Python scripts)                      
        //=====================================================================================

        /// @brief Used 
        /// @param url 
        /// @return 
        static std::vector<Track> get_online_tracks(const std::string& url);

        /// @brief Used to get complete info about a singular track (e.g. including picture data). 
        ///        Meant to be used sparingly, for example when loading a track preview.
        /// @param url The track URL
        /// @return A pair of the track info and artwork URL
        static std::pair<Track, std::string> get_online_track_full_info(const std::string& url);

        /// @brief Takes in a search query and returns tracks that match that query.
        /// @param query 
        /// @param num_results
        /// @return A vector of track information, stored as pairs of Track objects and any other extra information
        static std::vector<Track> search_tracks(const std::string& query, std::size_t num_results);

        /// @brief Used to download audio. Does NOT add the track to the track dict.
        /// @param url A URL to a streaming service such as Spotify or YouTube
        /// @param track Track information
        /// @param args Extra parameters for the Python script (if any)
        /// @return Information on the downloaded track and the downloaded filepath
        static std::pair<Track, std::string> download_track_from_url(const std::string& url, const std::vector<std::string>& args);

        /// @brief Used to download audio. The audio's filepath is then mapped to the param `track` in the track dictionary.
        ///        Note that this calls the Python "download track" function and does nothing else.
        ///        The download Python script may or may not use both the URL and the track info, but are given for flexibility in implementation.
        /// @param url A URL to a streaming service such as Spotify or YouTube
        /// @param track Track information
        /// @param args Extra parameters for the Python script (if any)
        /// @return `false` if the download encountered an unexpected error, `true` otherwise
        static bool import_track_from_info(const Track& track, const std::vector<std::string>& args);

        //=====================================================================================
        //                     TRACK DICTIONARY & PLAYLIST TREE MANAGEMENT                     
        //=====================================================================================

        /// @brief Reads the track dict file and loads its information to the static field `track_dict`
        /// @return `true` if the read was successful, `false` otherwise
        static bool read_track_dict();

        /// @brief Writes the track dict to its file
        /// @return `true` if the serialization was successful, `false` otherwise
        static bool serialize_track_dict();

        /// @brief Used to read the stored playlist tree, should be run once at the beginning of the program. 
        ///        Also adds any playlists to the tree that were found in the playlists folder but not originally in the tree.
        /// @return `true` if the read was successful, `false` otherwise
        static bool read_playlist_tree();

        /// @brief Writes the playlist tree to its JSON file
        /// @return `true` if the serialization was successful, `false` otherwise
        static bool serialize_playlist_tree();

        //=====================================================================================
        //                                      DEBUGGING                                      
        //=====================================================================================

        /// @brief Prints the track dict to std::cout
        static void print_track_dict();

        /// @brief Prints basic info about all playlists in staccato to std::cout
        static void print_basic_playlists_info();

        //=====================================================================================
        //                                      CONSTANTS                                      
        //=====================================================================================

        static constexpr std::size_t FILENAME_COLLISIONS_TOLERANCE {100000}; //The max number of attempts to find a unique filename
        static constexpr std::size_t PLAYLIST_ID_LENGTH {10};

        //Key names for dicts returned by Python scripts
        static constexpr std::string_view PY_TITLE_KEY {"title"};
        static constexpr std::string_view PY_ARTISTS_KEY {"artists"};
        static constexpr std::string_view PY_ALBUM_KEY {"album"};
        static constexpr std::string_view PY_FILEPATH_KEY {"filepath"};
        static constexpr std::string_view PY_ARTWORK_KEY {"artwork"};

        //Key names for JSON representation of Track objects
        static constexpr std::string_view TITLE_JSON_KEY {"title"};
        static constexpr std::string_view ARTISTS_JSON_KEY {"artists"};
        static constexpr std::string_view ALBUM_JSON_KEY {"album"};
        //Key names in the track dictionary JSON file
        static constexpr std::string_view TRACK_DICT_JSON_KEY {"dict"};
        static constexpr std::string_view TRACK_TRACK_DICT_JSON_KEY {"track"};
        static constexpr std::string_view FILEPATH_TRACK_DICT_JSON_KEY {"path"};
        //Key names in playlist JSON files
        static constexpr std::string_view NAME_PLAYLIST_JSON_KEY {"name"};
        static constexpr std::string_view CONNECTION_PLAYLIST_JSON_KEY {"connection"};
        static constexpr std::string_view SIZE_PLAYLIST_JSON_KEY {"size"};
        static constexpr std::string_view TRACKLIST_PLAYLIST_JSON_KEY {"tracklist"};
        //Key names in the playlist tree JSON file
        static constexpr std::string_view CONTENTS_PLAYLIST_TREE_JSON_KEY {"contents"};
        static constexpr std::string_view FOLDER_NAME_PLAYLIST_TREE_JSON_KEY {"name"};

        static constexpr std::string_view TRACK_DICT_FILENAME {"trackdict.json"};
        static constexpr std::string_view PLAYLIST_TREE_FILENAME {"playlisttree.json"};

        #if(DEVELOPMENT_BUILD)

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view PLAYLIST_FILES_DIR {"..\\playlists"};
        static constexpr std::string_view TRACK_FILES_DIR {"..\\tracks"};
        #else
        static constexpr std::string_view PLAYLIST_FILES_DIR {"../playlists"};
        static constexpr std::string_view TRACK_FILES_DIR {"../tracks"};
        #endif

        #else

        static constexpr std::string_view PLAYLIST_FILES_DIR {"playlists"};
        static constexpr std::string_view TRACK_FILES_DIR {"tracks"};

        #endif
        
    };

}

#endif