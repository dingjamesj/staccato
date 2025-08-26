#ifndef TRACK_MANAGER_HPP
#define TRACK_MANAGER_HPP

#include "track.hpp"
#include "playlist.hpp"
#include "util.hpp"

#include <string>
#include <unordered_map>
#include <filesystem>
#include <iostream>
#include <fstream>
#include <taglib/fileref.h>
#include <vector>

namespace staccato {
    
    class TrackManager {

        private:
        //The dict that maps all Tracks to a file path
        static std::unordered_map<Track, std::string> track_dict;

        public:

        //Adding new tracks to staccato

        static Track get_local_track_info(const std::string& path);
        static Track get_online_track_info(const URLType& url_type, const std::string& url);
        static bool import_local_track(const std::string& path, const Track& track);
        static bool download_track(const URLType& url_type, const std::string& url, const Track& track);

        //Reading and writing tracks

        /** Returns if the track file is readable */
        static bool track_has_readable_file(const std::string& path);
        /** Returns the track's file path that's stored in the dictionary */
        static std::string get_track_file_path(const Track& track);
        /** Deletes the track's file and removes the track key-value pair from the dictionary */
        static bool delete_track(const Track& track);
        /** Finds the key original_track and replaces it with new_track, then updates the track file's metadata accordingly */
        static bool edit_track(const Track& original_track, const Track& new_track);
        /** Searches for the track in the dictionary, then returns the duration of the track's file */
        static int get_track_duration(const Track& track);
        /** Searches for the track in the dictionary, then returns the raw artwork metadata of the track's file */
        static std::vector<char> get_track_artwork_raw(const Track& track);
        /** Searches for the track in the dictionary, then writes the track's file's artwork metadata */
        static bool set_track_artwork(const Track& track, const std::string& artwork_file_path);

        //Reading and writing the track dictionary

        /** Loads track_dict from the .stkl file */
        static void read_track_dict_from_file();
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
        static Playlist read_playlist_from_file(const std::string& id, const std::string& playlist_name);
        /** Serializes the playlist to its file */
        static bool write_playlist_to_file(const Playlist& playlist);
        /** What the .sply file name would be, given a playlist ID and name */
        static std::string get_sply_file_name(const std::string& id, const std::string& playlist_name);

        //Debugging

        static void print_track_dict();

        //Constexprs

        static constexpr std::string_view PLAYLIST_FILES_DIRECTORY {"playlists"};
        static constexpr std::string_view TRACK_FILES_DIRECTORY {"tracks"};

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