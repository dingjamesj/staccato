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
#include <QPixmap>
#include <any>
#include <nlohmann/json_fwd.hpp>


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

        public:

        //=====================================================================================
        //                               READING EXTERNAL TRACKS                               
        //=====================================================================================

        /// @param path 
        /// @return `true` if the file associated with the Track in the track dict is a readable audio file, `false` otherwise
        static bool path_is_readable_track_file(const std::string& path);

        /// @brief Gets info about a track from a local source (i.e. a file from the hard drive)
        /// @param path 
        /// @return A Track object representing the local track
        static Track get_local_track_info(const std::string& path);

        //------------------------------- REQUIRES THE INTERNET -------------------------------

        /// @brief *(Calls python function "fetcher.get_track")* Gets info about a track from an online source
        /// @param url 
        /// @return A Track object representing the online track
        static std::pair<Track, std::string> get_online_track_info(const std::string& url);

        /// @brief *(Calls python function "fetcher.find_best_youtube_url")* Searches for a YouTube video that matches best with the Track info. 
        /// @param track 
        /// @return A YouTube URL
        static std::string get_best_youtube_url(const Track& track);

        /// @brief *(Calls python function "fetcher.get_artwork_url_from_musicbrainz")* Searches for a release group on musicbrainz that contains the track described by the params, and gets its cover artwork URL
        /// @param title 
        /// @param lead_artist 
        /// @param album 
        /// @return An online URL to an image resource, an empty string if an image wasn't found
        static std::string get_musicbrainz_artwork_url(const std::string& title, const std::string& lead_artist, const std::string& album);

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
        static QPixmap get_track_artwork(const Track& track);

        /// @param audio_file_path 
        /// @return The embedded artwork of the audio file as a vector of bytes (chars)
        static QPixmap get_track_artwork(const std::string& audio_file_path);

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
        static bool import_local_track(const std::string& path, const Track& track);

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

        //------------------------------- REQUIRES THE INTERNET -------------------------------

        /// @brief *(Calls python function "downloader.download_youtube_track")* Downloads an audio file from YouTube, puts it into staccato's "track" directory, and adds param `track` and the downloaded file path to the track dict. DOES NOTHING IF `track` ALREADY EXISTS IN THE TRACK DICT.
        /// @param track The track metadata to put into the track dict and to set the downloaded audio file metadata to
        /// @param youtube_url YouTube video URL to download from
        /// @param artwork_url URL to an online image resource
        /// @param force_mp3 Forces the audio file to be an mp3
        /// @param force_opus Forces the audio file to be an ogg file with opus codec
        /// @return `true` if the download was successful, `false` otherwise (e.g. unable to download video, python error). NOTE : returns `true` if `track` is already in the track dict (vacuously since a download wasn't attempted)
        static bool download_track(const Track& track, const std::string& youtube_url, const std::string& artwork_url, bool force_mp3, bool force_opus);

        //=====================================================================================
        //                        PLAYLISTS MANAGEMENT (LOCAL & ONLINE)                        
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
        
        //------------------------------- REQUIRES THE INTERNET -------------------------------

        /// @brief *(Calls python function "fetcher.can_access_playlist")*
        /// @param url 
        /// @return `true` if the online playlist is accessible, `false` otherwise
        static bool online_playlist_is_accessible(const std::string& url);
        
        /// @brief *(Calls python function "fetcher.get_playlist")*
        /// @param url 
        /// @return The online playlist's tracklist as an unordered multiset of Track objects, an empty unordered multiset if accessing the playlist was unsuccessful
        static std::vector<Track> get_online_tracklist(const std::string& url);

        //=====================================================================================
        //                     TRACK DICTIONARY & PLAYLIST TREE MANAGEMENT                     
        //=====================================================================================

        /// @brief Reads the track dict file and loads its information to the static field `track_dict`
        /// @return `true` if the read was successful, `false` otherwise
        static bool read_track_dict();

        /// @brief Writes the track dict to its file
        /// @return `true` if the serialization was successful, `false` otherwise
        static bool serialize_track_dict();

        /// @brief Used to read the stored playlist tree, should be run once at the beginning of the program
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

        //Key names for JSON representation of Track objects
        static constexpr std::string_view TITLE_JSON_KEY {"title"};
        static constexpr std::string_view ARTISTS_JSON_KEY {"artists"};
        static constexpr std::string_view ALBUM_JSON_KEY {"album"};
        //Key names in the track dictionary JSON file
        static constexpr std::string_view TRACK_DICTIONARY_JSON_KEY {"dict"};
        static constexpr std::string_view TRACK_OBJ_JSON_KEY {"track"};
        static constexpr std::string_view FILEPATH_JSON_KEY {"path"};
        //Key names in playlist JSON files
        static constexpr std::string_view PLAYLIST_NAME_JSON_KEY {"name"};
        static constexpr std::string_view PLAYLIST_CONNECTION_JSON_KEY {"connection"};
        static constexpr std::string_view PLAYLIST_SIZE_JSON_KEY {"size"};
        static constexpr std::string_view PLAYLIST_TRACKLIST_JSON_KEY {"tracklist"};

        static constexpr std::string_view TRACK_DICTIONARY_FILENAME {"trackdict.json"};
        static constexpr std::string_view PLAYLIST_TREE_FILENAME {"playlisttree.json"};

        #if(DEVELOPMENT_BUILD)

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view PLAYLIST_FILES_DIRECTORY {"..\\playlists"};
        static constexpr std::string_view TRACK_FILES_DIRECTORY {"..\\tracks"};
        #else
        static constexpr std::string_view PLAYLIST_FILES_DIRECTORY {"../playlists"};
        static constexpr std::string_view TRACK_FILES_DIRECTORY {"../tracks"};
        #endif

        #else

        static constexpr std::string_view PLAYLIST_FILES_DIRECTORY {"playlists"};
        static constexpr std::string_view TRACK_FILES_DIRECTORY {"tracks"};

        #endif
        
    };

}

#endif