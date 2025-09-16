#include "track.hpp"
#include "playlist.hpp"

using namespace staccato;

int main() {

    /* Test Cases
    
    get_online_track_info
     - Regular Spotify track
     - Unavailable Spotify track
     - Spotify podcast
     - Unavailable Spotify podcast
     - Regular YouTube Music vdeo
     - Non-music regular YouTube video 
     - Age restricted YouTube video
     - Unavailable YouTube video
     - Privated YouTube video
     - Unlisted YouTube video

    */

    std::cout << "PRE goon" << std::endl;
    Playlist playlist ("test", "", std::unordered_multiset<Track> {}, "https://www.youtube.com/playlist?list=PLmfSdJj_ZUFD_YvXNxd89Mq5pysTjpMSF");
    // Playlist playlist ("test", "", std::unordered_multiset<Track> {}, "https://open.spotify.com/playlist/4hvp9WUZ7x0YxbkMMQi3e6?si=0e009312d8ed4dc7");
    std::cout << "POST goon" << std::endl;
    std::cout << playlist.string() << std::endl;

    return 0;

}