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

    bool init_success = init_python();
    if(!init_success) {

        return false;

    }

    std::cout << "1" << std::endl;
    Playlist spotify_playlist ("test", "", std::unordered_multiset<Track> {}, "https://www.youtube.com/playlist?list=PLmfSdJj_ZUFD_YvXNxd89Mq5pysTjpMSF");
    std::cout << spotify_playlist.string() << std::endl;

    std::cout << "2" << std::endl;
    spotify_playlist.remove_online_connection();
    std::cout << spotify_playlist.string() << std::endl;
    
    std::cout << "3" << std::endl;
    spotify_playlist.set_online_connection("https://open.spotify.com/playlist/4hvp9WUZ7x0YxbkMMQi3e6?si=0e009312d8ed4dc7");
    std::cout << spotify_playlist.string() << std::endl;

    Py_FinalizeEx();

    return 0;

}