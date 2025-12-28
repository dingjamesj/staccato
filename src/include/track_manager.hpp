#ifndef TRACK_MANAGER_HPP
#define TRACK_MANAGER_HPP

// =========================================================================================
// 
//                SET TO TRUE DURING DEVELOPMENT, SET TO FALSE WHEN DEPLOYED.             
//   USED TO ADJUST FOR THE DIFFERENCE BETWEEN DEVELOPMENT AND DEPLOYED PROJECT STRUCTURE.

#define DEVELOPMENT_BUILD true

// =========================================================================================

#include <string>
#include <unordered_map>
#include <unordered_set>
#include <filesystem>
#include <iostream>
#include <fstream>
#include <taglib/fileref.h>
#include <taglib/attachedpictureframe.h>
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

/* Examples of .stkl and .sply file structures:

NOTE: new lines are only there for visual purposes---they don't exist in the actual binary files.
      (the actual binary files are just series of chars)

.stkl file format:

File header
\0
Title 1
\0
Artist 1
\0
Artist 2
\0
\0      <-- Note that two null chars separate the artists list and the album since one null char separates each artist.
Album 1
\0
Path 1
\0
Title 2
\0
...

.sply file format:

File header
\0
Playlist name
\0
Online connection
\0
Playlist size (as a uint64_t, which has a fixed number of bytes, so no need for a null char to end it)
Title 1
\0
Artist 1
\0
\0      <-- Again, note that two null chars separate the artists list and the album since one null char separates each artist.
Album 1
\0
Title 2
\0
...

*/



/* Familiarize yourself with some terminology:

Internal tracks  -- Audio files that staccato has a record of (specifically in the .stkl "track dictionary" file).

External tracks  -- Audio files that staccato has NOT recorded (is not in the "track dictionary"). These can be
                    tracks on the local computer system (a "local" track) or a track from an online streaming
                    service that isn't downloaded (an "online" track).

Local playlists  -- A playlist from staccato. Has an associated .sply file and can be expressed as a Playlist object.

Online playlists -- A playlist from an online streaming service. Only exists online and can only be expressed as a 
                    set of tracks (std::unordered_multiset<Track>)

Track dictionary -- The core part of staccato's file tracking system. It maps Tracks (so a title, artist, and album)
                    to file paths, and it is serialized as the .stkl file found in the "tracks" directory. In this
                    documentation, it might also be referred to as "the track dict" or "the .stkl file."

*/

namespace staccato {

    struct Track;
    class Playlist;
    enum class audiotype;
    
    class TrackManager {

        private:

        /// @brief The unordered map that maps all Tracks to a file path as a std::string
        static std::unordered_map<Track, std::string> track_dict;

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
        /// @return A vector of tuples that contain each playlist's ID, name, size, and online connection
        static std::vector<std::tuple<std::string, std::string, std::string, std::uint64_t>> get_basic_playlist_info_from_files();

        /// @brief Gets complete information about a singular playlist (including the tracklist)
        /// @param id (A playlist's ID is actually just the .sply filename's stem)
        /// @return A Playlist object containing complete information about the playlist
        static Playlist get_playlist(const std::string& id);

        /// @brief Reads through each track in the tracklist and finds the track's associated file's audio length
        /// @param playlist 
        /// @return The total duration in seconds of the playlist
        static int get_playlist_duration(const Playlist& playlist);

        /// @brief Writes the playlist to its .sply file (creating one if needed)
        /// @param id (What the .sply filename's stem will be)
        /// @param playlist
        /// @return `true` if the serialization was successful, `false` otherwise
        static bool serialize_playlist(const std::string& id, const Playlist& playlist);
        
        //------------------------------- REQUIRES THE INTERNET -------------------------------

        /// @brief *(Calls python function "fetcher.can_access_playlist")*
        /// @param url 
        /// @return `true` if the online playlist is accessible, `false` otherwise
        static bool online_playlist_is_accessible(const std::string& url);
        
        /// @brief *(Calls python function "fetcher.get_playlist")*
        /// @param url 
        /// @return The online playlist's tracklist as an unordered multiset of Track objects, an empty unordered multiset if accessing the playlist was unsuccessful
        static std::unordered_multiset<Track> get_online_tracklist(const std::string& url);

        //=====================================================================================
        //                             TRACK DICTIONARY MANAGEMENT                             
        //=====================================================================================

        /// @brief Reads the .stkl file and loads its information to the static field `track_dict` (declaration found somewhere else in this header file)
        /// @return `true` if the read was successful, `false` otherwise
        static bool read_track_dict();

        /// @brief Writes the track dict from the static field `track_dict` to the .stkl file (declaration of `track_dict` found somewhere else in this header file)
        /// @return `true` if the serialization was successful, `false` otherwise
        static bool serialize_track_dict();

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

        #if(DEVELOPMENT_BUILD)

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view PLAYLIST_FILES_DIRECTORY {"..\\playlists"};
        static constexpr std::string_view TRACK_FILES_DIRECTORY {"..\\tracks"};
        static constexpr std::string_view PLAYLIST_FILE_EXTENSION {".sply"};
        static constexpr std::string_view TRACK_DICTIONARY_PATH {"..\\tracks\\trackdict.stkl"};
        #else
        static constexpr std::string_view PLAYLIST_FILES_DIRECTORY {"../playlists"};
        static constexpr std::string_view TRACK_FILES_DIRECTORY {"../tracks"};
        static constexpr std::string_view PLAYLIST_FILE_EXTENSION {".sply"};
        static constexpr std::string_view TRACK_DICTIONARY_PATH {"../tracks/trackdict.stkl"};
        #endif

        #else

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view PLAYLIST_FILES_DIRECTORY {"playlists"};
        static constexpr std::string_view TRACK_FILES_DIRECTORY {"tracks"};
        static constexpr std::string_view PLAYLIST_FILE_EXTENSION {".sply"};
        static constexpr std::string_view TRACK_DICTIONARY_PATH {"tracks\\trackdict.stkl"};
        #else
        static constexpr std::string_view PLAYLIST_FILES_DIRECTORY {"playlists"};
        static constexpr std::string_view TRACK_FILES_DIRECTORY {"tracks"};
        static constexpr std::string_view PLAYLIST_FILE_EXTENSION {".sply"};
        static constexpr std::string_view TRACK_DICTIONARY_PATH {"tracks/trackdict.stkl"};
        #endif

        #endif
        
    };

}

#endif