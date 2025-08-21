#include "track.hpp"

staccato::Track::Track(std::string path, std::string youtube_url, std::string title, std::string artists, std::string album): 
    path{path}, 
    youtube_url{youtube_url}, 
    title{title}, 
    artists{artists}, 
    album{album} 
{

    //Set the duration according to the file at the denoted path.
    duration = -1;

}