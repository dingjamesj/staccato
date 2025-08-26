#ifndef FILE_MANAGER_HPP
#define FILE_MANAGER_HPP

#include <string>
#include <unordered_map>
#include <filesystem>
#include <iostream>
#include <fstream>
#include <taglib/fileref.h>
#include <format>

namespace staccato {

struct Track;
class Playlist;

/**
 * @brief Manages ALL interactions with the file system and staccato's track dictionary.
 * 
 * Track and Playlist should provide abstractions for interactions with the file system--i.e. FileManager shouldn't be seen outside of Track and Playlist.
 */
class FileManager {

friend Track;
friend Playlist;

private:
    /**
     * @brief A dict that maps Track objects to the track's file path and the track's YouTube URL 
     * 
     * The pair's first is the file path, second is the YouTube URL
     */
    static std::unordered_map<Track, std::pair<std::string, std::string>> track_dict;
    
    /** @return True if the path is readable as an audio file. */
    static bool path_is_readable_audio_file(const std::string& path);
    
    /**
     * @brief Deletes the file and removes this track from staccato's track dictionary.
     * 
     * Note that the track is removed from the track dictionary whether or not the file was successfully deleted.
     * 
     * @return True if successfully deleted, false otherwise
     */
    static bool delete_track(const Track& track);

public:
    static constexpr std::string_view PLAYLIST_FILES_DIRECTORY {"playlists"};
    static constexpr std::string_view TRACK_FILES_DIRECTORY {"tracks"};

    #if defined(_WIN32) || defined(_WIN64)
    static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"playlists\\images"};
    static constexpr std::string_view TRACK_DICTIONARY_PATH {"tracks\\tracks.stkl"};
    #else
    static constexpr std::string_view PLAYLIST_IMAGES_DIRECTORY {"playlists/images"};
    static constexpr std::string_view TRACK_DICTIONARY_PATH {"tracks/tracks.stkl"};
    #endif

    //For debugging purposes
    static void print_track_dict();

};

}

#endif