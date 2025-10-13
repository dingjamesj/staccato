#ifndef TRACK_MANAGER_HPP
#define TRACK_MANAGER_HPP

#include "track.hpp"
#include "playlist.hpp"
#include "util.hpp"

#include <string>
#include <unordered_map>
#include <unordered_set>
#include <filesystem>
#include <iostream>
#include <fstream>
#include <taglib/fileref.h>
#include <vector>

/* Examples of .stkl and .sply file structures:

NOTE: new lines are only there for visual purposes---they don't exist in the actual binary files.
      (the actual binary files are just series of chars)

.stkl file format:

File version
Track title 1
\0
Artist 1
\0
Artist 2
\0
\0      <-- Note that two null chars separate the artists list and the album since one null char separates each artist.
Album
\0
Path 1
\0
Track title 2
\0
...

.sply file format:

File version
Playlist name
\0
Online connection
\0
Track title 1
\0
Artist 1
\0
\0      <-- Again, note that two null chars separate the artists list and the album since one null char separates each artist.
Album
\0
Track title 2
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
    
    class TrackManager {

        private:

        static std::unordered_map<Track, std::string> track_dict; //The dict that maps all Tracks to a file path

        //Helper functions

        static std::filesystem::path get_unique_filename(std::filesystem::path path);
        static bool write_file_metadata(const std::string& path, const Track& track);
        static urltype get_url_type(const std::string& url);
        static std::string ifstream_read_file_header(std::ifstream& input);

        public:

        //Reading local and online external tracks

        /** Returns if the track file is readable */
        static bool path_is_readable_track_file(const std::string& path);
        /** Returns metadata of the track accessed from the local file system */
        static Track get_local_track_info(const std::string& path);

        /** Returns metadata of the track accessed from the URL */
        static std::pair<Track, std::pair<std::string, std::string>> get_online_track_info(const std::string& url);
        /** Finds the best matching YouTube URL*/
        static std::string get_best_youtube_url(const Track& track);

        //Reading internal tracks

        /** Returns if the track is in the dictionary */
        static bool track_is_in_dict(const Track& track);
        /** Returns the track's file path that's stored in the dictionary */
        static std::string get_track_file_path(const Track& track);
        /** Returns the duration of the track's file */
        static int get_track_duration(const Track& track);
        /** Returns the bitrate of the track's file in kbps */
        static int get_track_bitrate(const Track& track);
        /** Returns the file extension of the track's file */
        static std::string get_track_file_ext(const Track& track);
        /** Searches for the track in the dictionary, then returns the raw artwork metadata of the track's file */
        static std::vector<char> get_track_artwork_raw(const Track& track);
        /** Tracks in track_dict whose associated file no longer exists */
        static std::vector<Track> find_missing_tracks();
        /** Track file paths that do not have an associated Track in track_dict */
        static std::vector<std::string> find_extraneous_track_files();

        //Adding, modifying, and deleting internal tracks

        /** (DOES NOT REPLACE) Copies the track file, overwrites the copy's metadata, puts it into staccato, and pairs it with the Track object */
        static bool import_local_track(const std::string& path, const Track& track);
        /** Deletes the track's file and removes the track key-value pair from the dictionary */
        static bool delete_track(const Track& track);
        /** Finds the key original_track and replaces it with new_track, then updates the track file's metadata accordingly */
        static bool edit_track(const Track& original_track, const Track& new_track);
        /** Searches for the track in the dictionary, then writes the track's file's artwork metadata */
        static bool set_track_artwork(const Track& track, const std::string& artwork_file_path);
        /** Searches for the track in the dictionary, then deletes the track's file's artwork metadata */
        static bool delete_track_artwork(const Track& track);

        /** (DOES NOT REPLACE) Downloads the track from the YouTube URL, puts it into staccato and pairs it with the Track object */
        static bool download_track(const Track& track, const std::string& youtube_url, const std::pair<std::string, std::string>& artwork_urls, bool force_mp3);

        //Reading and writing local playlists + reading online playlists
        
        /** Returns basic info about every playlist (a tuple of the playlist id, name, and cover image file path) */
        static std::vector<std::tuple<std::string, std::string, std::string>> get_basic_playlist_info_from_files();
        /** Searches for .sply file with the same name, and returns complete playlist info */
        static Playlist get_playlist(const std::string& id);
        /** Returns the summed duration of each track in the playlist */
        static int get_playlist_duration(const Playlist& playlist);
        /** Serializes the playlist to its file */
        static bool serialize_playlist(const std::string& id, const Playlist& playlist);
        
        /** Returns whether or not the online playlist is accessible */
        static bool online_playlist_is_accessible(const std::string& url);
        /** Returns the tracklist of the online playlist */
        static std::unordered_multiset<Track> get_online_tracklist(const std::string& url);

        //Track dictionary management

        /** Loads track_dict from the .stkl file */
        static bool read_track_dict();
        /** Serializes track_dict to the .stkl file */
        static bool serialize_track_dict();

        //Debugging

        static void print_track_dict();
        static void print_basic_playlists_info();

        //Constexprs

        static constexpr std::string_view FILE_HEADER {"staccato1"};

        #if(DEVELOPMENT_BUILD)

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view PLAYLIST_FILES_DIRECTORY {"..\\playlists"};
        static constexpr std::string_view TRACK_FILES_DIRECTORY {"..\\tracks"};
        static constexpr std::string_view PLAYLIST_FILE_EXTENSION {".sply"};
        static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"..\\playlists\\images"};
        static constexpr std::string_view TRACK_DICTIONARY_PATH {"..\\tracks\\trackdict.stkl"};
        #else
        static constexpr std::string_view PLAYLIST_FILES_DIRECTORY {"../playlists"};
        static constexpr std::string_view TRACK_FILES_DIRECTORY {"../tracks"};
        static constexpr std::string_view PLAYLIST_FILE_EXTENSION {".sply"};
        static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"../playlists/images"};
        static constexpr std::string_view TRACK_DICTIONARY_PATH {"../tracks/trackdict.stkl"};
        #endif

        #else

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view PLAYLIST_FILES_DIRECTORY {"playlists"};
        static constexpr std::string_view TRACK_FILES_DIRECTORY {"tracks"};
        static constexpr std::string_view PLAYLIST_FILE_EXTENSION {".sply"};
        static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"playlists\\images"};
        static constexpr std::string_view TRACK_DICTIONARY_PATH {"tracks\\trackdict.stkl"};
        #else
        static constexpr std::string_view PLAYLIST_FILES_DIRECTORY {"playlists"};
        static constexpr std::string_view TRACK_FILES_DIRECTORY {"tracks"};
        static constexpr std::string_view PLAYLIST_FILE_EXTENSION {".sply"};
        static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"playlists/images"};
        static constexpr std::string_view TRACK_DICTIONARY_PATH {"tracks/trackdict.stkl"};
        #endif

        #endif
        
    };

}

#endif