#ifndef TRACK_HPP
#define TRACK_HPP

#include <string>
#include <unordered_set>

namespace staccato {

enum class URLType {SPOTIFY, YOUTUBE, UNKNOWN};

struct Track {

    const std::string path;
    const std::string youtube_url;
    const int duration {0};

    //These aren't transient because we need these to search for songs
    std::string title;
    std::string artists;
    std::string album;

    //For importing an existing file
    Track(std::string path, std::string youtube_url, int duration, std::string title, std::string artists, std::string album);

    //For debugging
    void print() const;

    //Misc
    void write_changes_to_file() const;
    bool operator==(const Track& other) const;
    bool file_exists() const;
    /**
     * @brief Deletes the file and clears all member variables
     * @return True if successfully deleted, false otherwise
     * 
     * 
     */
    bool delete_file();
    
};

Track import_track(std::string path);
Track download_track(std::string title, std::string artists, std::string album);
Track download_track(URLType url_type, std::string url);

}

template<> struct std::hash<staccato::Track> {

    std::size_t operator()(const staccato::Track& track) const {

        return std::hash<std::string>()(track.path);

    }

};

#endif