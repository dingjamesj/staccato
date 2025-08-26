#ifndef TRACK_HPP
#define TRACK_HPP

#include <string>
#include <unordered_set>
#include <format>
#include <iostream>

namespace staccato {

enum class URLType {SPOTIFY, YOUTUBE, UNKNOWN};

struct Track {

//Note that the only way to create a new Track should be through FileManager
private:
    Track(std::string title, std::string artists, std::string album);

public:
    std::string title;
    std::string artists;
    std::string album;

    /** For debugging purposes */
    void print() const;

    /**
     * @return The track's path, according to staccato's track dictionary. If the track is not in the dictionary, then returns the empty string ""
     */
    std::string get_path() const;
    
    /**
     * @return True if the file is on staccato's track dictionary AND if it can be opened as an audio file, false otherwise.
     */
    bool has_valid_audio_file() const;

    /** Sets member variable to empty strings, deletes the file, and removes this track from staccato's track dictionary. */
    bool delete_file();

    void write_changes_to_file() const;

    bool is_empty() const;

    bool operator==(const Track& other) const;

    //=========================
    //  Public track creation
    //=========================
    
    /**
     * @brief Imports a track file and adds it to staccato's track dictionary.
     * 
     * The import can be unsuccessful if the imported track has metadata that already exists in staccato's track dictionary.
     * It can also be unsuccessful if the path does not lead to a track file.
     * 
     * @param path The path of the track file to be imported
     * @return The imported track with the metadata--an empty imported track if the import was unsuccessful
     * 
     */
    static Track import_track(std::string path);
    static Track download_track(std::string title, std::string artists, std::string album);
    static Track download_track(URLType url_type, std::string url);

};

}

namespace std {

template<> struct hash<staccato::Track> {

    inline std::size_t operator()(const staccato::Track& track) const {

        return std::hash<std::string>()(track.title + " " + track.artists + " " + track.album);

    }

};

}

#endif