#ifndef TRACK_HPP
#define TRACK_HPP

#include <string>

namespace staccato {

enum class URLType {SPOTIFY, YOUTUBE};

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
    Track(std::string path, std::string youtube_url, std::string title, std::string artists, std::string album): 
        path{path}, 
        youtube_url{youtube_url}, 
        title{title}, 
        artists{artists}, 
        album{album} 
    {}
    
};

}

staccato::Track import_track(std::string path);
staccato::Track download_track(std::string title, std::string artists, std::string album);
staccato::Track download_track(staccato::URLType url_type, std::string url);

#endif