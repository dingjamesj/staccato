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
#include <Python.h>

namespace staccato {
    
    class TrackManager {

        private:
        //The dict that maps all Tracks to a file path
        static std::unordered_map<Track, std::string> track_dict;

        //Helper functions

        static std::filesystem::path get_unique_filename(std::filesystem::path path);
        static bool write_file_metadata(const std::string& path, const Track& track);
        static urltype get_url_type(const std::string& url);

        public:

        //Accessing external tracks

        /** Returns metadata of the track accessed from the local file system */
        static Track get_local_track_info(const std::string& path);
        /** Returns metadata of the track accessed from the URL */
        static Track get_online_track_info(const std::string& url);
        /** (DOES NOT REPLACE) Copies the track file, overwrites the copy's metadata, puts it into staccato, and pairs it with the Track object */
        static bool import_local_track(const std::string& path, const Track& track);
        /** (DOES NOT REPLACE) Downloads the track from the YouTube URL, puts it into staccato and pairs it with the Track object */
        static bool download_track(const Track& track, const std::string& youtube_url);
        /** (DOES NOT REPLACE) Finds the best matching YouTube URL, downloads it, puts it into staccato and pairs it with the Track object */
        static bool download_track(const Track& track);

        //Reading and writing tracks

        /** Returns if the track file is readable */
        static bool path_is_readable_track_file(const std::string& path);
        /** Returns the track's file path that's stored in the dictionary */
        static std::string get_track_file_path(const Track& track);
        /** Returns if the track is in the dictionary */
        static bool track_is_in_dict(const Track& track);
        /** Deletes the track's file and removes the track key-value pair from the dictionary */
        static bool delete_track(const Track& track);
        /** Finds the key original_track and replaces it with new_track, then updates the track file's metadata accordingly */
        static bool edit_track(const Track& original_track, const Track& new_track);
        /** Returns the duration of the track's file */
        static int get_track_duration(const Track& track);
        /** Returns the bitrate of the track's file in kbps */
        static int get_track_bitrate(const Track& track);
        /** Returns the file extension of the track's file */
        static std::string get_track_file_ext(const Track& track);
        /** Searches for the track in the dictionary, then returns the raw artwork metadata of the track's file */
        static std::vector<char> get_track_artwork_raw(const Track& track);
        /** Searches for the track in the dictionary, then writes the track's file's artwork metadata */
        static bool set_track_artwork(const Track& track, const std::string& artwork_file_path);
        /** Searches for the track in the dictionary, then deletes the track's file's artwork metadata */
        static bool delete_track_artwork(const Track& track);

        //Reading and writing the track dictionary

        /** Loads track_dict from the .stkl file */
        static bool read_track_dict_from_file();
        /** Serializes track_dict to the .stkl file */
        static bool write_track_dict_to_file();
        /** Tracks in track_dict whose associated file no longer exists */
        static std::vector<Track> find_missing_tracks();
        /** Track file paths that do not have an associated Track in track_dict */
        static std::vector<std::string> find_extraneous_track_files();

        //Reading and writing playlists

        /** Returns basic info about every playlist (a tuple of the playlist id, name, and cover image file path) */
        static std::vector<std::tuple<std::string, std::string, std::string>> read_basic_playlist_info_from_files();
        /** Searches for .sply file with the same name, and returns complete playlist info */
        static Playlist read_playlist_from_file(const std::string& id);
        /** Serializes the playlist to its file */
        static bool write_playlist_to_file(const std::string& id, const Playlist& playlist);

        //Debugging

        static void print_track_dict();

        //Constexprs

        static constexpr std::string_view PLAYLIST_FILES_DIRECTORY {"playlists"};
        static constexpr std::string_view TRACK_FILES_DIRECTORY {"tracks"};
        static constexpr std::string_view PLAYLIST_FILE_EXTENSION {".sply"};

        #if defined(_WIN32) || defined(_WIN64)
        static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"playlists\\images"};
        static constexpr std::string_view TRACK_DICTIONARY_PATH {"tracks\\trackdict.stkl"};
        #else
        static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"playlists/images"};
        static constexpr std::string_view TRACK_DICTIONARY_PATH {"tracks/trackdict.stkl"};
        #endif
        
    };

}

#endif