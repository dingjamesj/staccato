#ifndef TRACK_HPP
#define TRACK_HPP

#include <string>
#include <unordered_set>

namespace staccato {

enum class URLType {SPOTIFY, YOUTUBE, UNKNOWN};

class Track {

private:
    std::string path;
    std::string youtube_url;
    int duration {0};

public:
    //These aren't transient because we need these to search for songs
    std::string title;
    std::string artists;
    std::string album;

    //For importing an existing file
    Track(std::string path, std::string youtube_url, std::string title, std::string artists, std::string album);
    
};

Track import_track(std::string path);
Track download_track(std::string title, std::string artists, std::string album);
Track download_track(URLType url_type, std::string url);

}

#endif