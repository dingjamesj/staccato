#ifndef FILE_MANAGER_HPP
#define FILE_MANAGER_HPP

#include "track.hpp"
#include "playlist.hpp"
#include <string>
#include <unordered_map>
#include <filesystem>
#include <iostream>
#include <taglib/fileref.h>

namespace staccato {

/**
 * @brief Manages playlist (.sply) files, the track dictionary (.stkl), and the track folder.
 * 
 * 
 * ALL interactions with the file system and track dictionary should come through here. 
 * 
 * Track and Playlist should provide abstractions for interactions with the file system--i.e. FileManager shouldn't be seen outside of Track and Playlist.
 */
class FileManager {

friend Track;
friend Playlist;

private:
    static const std::unordered_map<Track, std::pair<std::string, std::string>>& get_track_dict();

    /**
     * @brief A dict that maps Track objects to the track's file path and the track's YouTube URL 
     * 
     * The pair's first is the file path, second is the YouTube URL
     */
    static std::unordered_map<Track, std::pair<std::string, std::string>> track_dict;

    /**
     * @return True if the file exists in staccato's track dictionary and is a real file, false otherwise.
     * 
     */
    static bool track_file_exists(const Track& track);
    /**
     * @return True if the path is accessible as as music file.
     * 
     */
    static bool path_is_track_file(const std::string& path);
    /**
     * @brief Deletes the file and removes this track from staccato's track dictionary.
     * @return True if successfully deleted, false otherwise
     * 
     * Note that the track is removed from the track dictionary whether or not the file was successfully deleted.
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

    /**
     * @brief Imports a track file and adds it to staccato's track dictionary
     * @param path The path of the track file to be imported
     * @return The imported track with the metadata--an empty imported track if the import was unsuccessful
     * 
     * 
     * The import can be unsuccessful if the imported track has metadata that already exists in staccato's track dictionary.
     * 
     * It can also be unsuccessful if the path does not lead to a track file.
     */
    static Track import_track(std::string path);
    static Track download_track(std::string title, std::string artists, std::string album);
    static Track download_track(URLType url_type, std::string url);

};

}

#endif