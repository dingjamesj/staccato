#include "track.hpp"
#include "playlist.hpp"
#include "track_manager.hpp"

using namespace staccato;

void playlist_testing() {

    //Notes about testing: 
    // "+" means that the test is meant to pass
    // "-" means that the test is meant to fail

    std::cout << "+ Playlist contructor (YouTube)" << std::endl;
    Playlist spotify_playlist ("Playlist 1", std::unordered_multiset<Track> {}, "https://www.youtube.com/playlist?list=PLmfSdJj_ZUFD_YvXNxd89Mq5pysTjpMSF");
    std::cout << spotify_playlist.string() << std::endl;

    std::cout << "+ Playlist.remove_online_connection()" << std::endl;
    spotify_playlist.remove_online_connection();
    std::cout << spotify_playlist.string() << std::endl;
    
    std::cout << "+ Playlist.set_online_connection() (Spotify)" << std::endl;
    spotify_playlist.set_online_connection("https://open.spotify.com/playlist/4hvp9WUZ7x0YxbkMMQi3e6?si=0e009312d8ed4dc7");
    std::cout << spotify_playlist.string() << std::endl;

    std::cout << "+ Playlist.get_online_connection_tracklist() (Spotify)" << std::endl;
    std::unordered_multiset<Track> spotify_tracklist = spotify_playlist.get_online_connection_tracklist();
    std::unordered_multiset<staccato::Track>::iterator spotify_iter = spotify_tracklist.begin();
    while(spotify_iter != spotify_tracklist.end()) {

        std::cout << (*spotify_iter).string() << std::endl;
        spotify_iter++;

    }

    std::cout << std::endl << "+ Playlist.add_track()" << std::endl;
    spotify_iter = spotify_tracklist.begin();
    while(spotify_iter != spotify_tracklist.end()) {

        spotify_playlist.add_track(*spotify_iter);
        spotify_iter++;

    }
    std::cout << spotify_playlist.string() << std::endl;

    std::cout << std::endl << "+ Playlist.contains_track()" << std::endl;
    std::cout << (spotify_playlist.contains_track(Track("SAFARI", {"Tyler, The Creator"}, "CALL ME IF YOU GET LOST")) ? "true" : "false") << std::endl << std::endl;
    std::cout << std::endl << "- Playlist.contains_track()" << std::endl;
    std::cout << (spotify_playlist.contains_track(Track("SAFAI", {"Tyler, The Creator"}, "CALL ME IF YOU GET LOST")) ? "true" : "false") << std::endl << std::endl;

    std::cout << "- Playlist constructor (Spotify)" << std::endl;
    Playlist invalid_playlist_1 ("Playlist 2", std::unordered_multiset<Track> {}, "https://open.spotify.com/playlist/6Qj4W3ybeItAPg0d5e8XVy?si=5bce260d95c6436e&pt=c61881f0d2e0b444b89c0519dfe05748");
    std::cout << invalid_playlist_1.string() << std::endl;

    std::cout << "- Playlist constructor (YouTube)" << std::endl;
    Playlist invalid_playlist_2 ("Playlist 3", std::unordered_multiset<Track> {}, "https://www.youtube.com/playlist?list=PLmfSdJj_ZUFD4_T3E6jPbd6Z8n1zv_cRY");
    std::cout << invalid_playlist_2.string() << std::endl;

    std::cout << "+ Playlist constructor (from tracklist)" << std::endl;
    std::unordered_multiset<Track> custom_tracklist {Track("Track 1", {"artist"}, "asdoi"), Track("asdoihias", {"late"}, "s"), Track("a", {"a"}, "a"), Track("a", {"a"}, "a")};
    Playlist playlist ("Playlist", custom_tracklist, "");
    std::cout << playlist.string() << std::endl;

    std::cout << "+ Playlist.add_track() (unique track)" << std::endl;
    playlist.add_track(Track("Track4324", {"poop"}, "pee"));
    std::cout << playlist.string() << std::endl;

    std::cout << "+ Playlist.add_track() (duplicate track)" << std::endl;
    playlist.add_track(Track("a", {"a"}, "a"));
    std::cout << playlist.string() << std::endl;

    std::cout << "+ Playlist.remove_track() (unique track)" << std::endl;
    playlist.remove_track(Track("Track4324", {"poop"}, "pee"));
    std::cout << playlist.string() << std::endl;
    
    std::cout << "+ Playlist.remove_track() (duplicate track, ONLY ONE OF THE DUPLICATES SHOULD BE REMOVED)" << std::endl;
    playlist.remove_track(Track("a", {"a"}, "a"));
    std::cout << playlist.string() << std::endl;

    std::cout << "- Playlist.remove_track()" << std::endl;
    bool remove_track_result = playlist.remove_track(Track("NOT EXIST", {"appleseed"}, "john"));
    std::cout << (remove_track_result ? "true" : "false") << ", " << playlist.string() << std::endl;

    std::cout << "- Playlist.is_empty()" << std::endl;
    std::cout << (playlist.is_empty() ? "true" : "false") << std::endl << std::endl;

    std::cout << "+ Playlist.is_empty()" << std::endl;
    Playlist empty_playlist {};
    std::cout << (empty_playlist.is_empty() ? "true" : "false") << std::endl << std::endl;

    std::cout << "+ Playlist.get_sorted_playlist() (title ascending)" << std::endl;
    std::vector<Track> sorted_tracklist_1 = spotify_playlist.get_sorted_tracklist(sortmode::title, true);
    for(std::size_t i {0}; i < sorted_tracklist_1.size(); i++) {

        std::cout << sorted_tracklist_1[i].string() << std::endl;

    }
    std::cout << std::endl;

    std::cout << "+ Playlist.get_sorted_playlist() (title descending)" << std::endl;
    std::vector<Track> sorted_tracklist_2 = spotify_playlist.get_sorted_tracklist(sortmode::title, false);
    for(std::size_t i {0}; i < sorted_tracklist_2.size(); i++) {

        std::cout << sorted_tracklist_2[i].string() << std::endl;

    }
    std::cout << std::endl;

    std::cout << "+ Playlist.get_sorted_playlist() (artists ascending)" << std::endl;
    std::vector<Track> sorted_tracklist_3 = spotify_playlist.get_sorted_tracklist(sortmode::artists, true);
    for(std::size_t i {0}; i < sorted_tracklist_3.size(); i++) {

        std::cout << sorted_tracklist_3[i].string() << std::endl;

    }
    std::cout << std::endl;

    std::cout << "+ Playlist.get_sorted_playlist() (album ascending)" << std::endl;
    std::vector<Track> sorted_tracklist_4 = spotify_playlist.get_sorted_tracklist(sortmode::album, true);
    for(std::size_t i {0}; i < sorted_tracklist_4.size(); i++) {

        std::cout << sorted_tracklist_4[i].string() << std::endl;

    }
    std::cout << std::endl;

}

void track_manager_testing() {

    std::string sample_track_path = "..\\tracks\\90210.mp3";

    //Accessing external tracks
    std::cout << "TrackManager::get_local_track_info()" << std::endl;
    Track local_track = TrackManager::get_local_track_info(sample_track_path);
    std::cout << local_track.string() << std::endl;

    //Reading and writing the track dictionary
    
    std::cout << "TrackManager::read_track_dict_from_file()" << std::endl;
    TrackManager::read_track_dict_from_file();
    std::cout << std::endl;

    std::cout << "TrackManager::find_extraneous_track_files()" << std::endl;
    std::vector<std::string> extraneous_track_files = TrackManager::find_extraneous_track_files();
    for(std::string track_file: extraneous_track_files) {

        std::cout << track_file << std::endl;

    }
    std::cout << std::endl;

}

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

    // playlist_testing();
    track_manager_testing();

    Py_FinalizeEx();

    return 0;

}