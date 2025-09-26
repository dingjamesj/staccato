#include "tests.hpp"

using namespace staccato;

void staccato::playlist_testing() {

    //Notes about testing: 
    // "+" means that the test is meant to pass
    // "-" means that the test is meant to fail

    const std::string youtube_playlist_url = "https://www.youtube.com/playlist?list=PLmfSdJj_ZUFD_YvXNxd89Mq5pysTjpMSF";
    const std::string spotify_playlist_url = "https://open.spotify.com/playlist/3VZpUutFs4UaQSyardPXfh?si=ac6e2f21d48347ac";
    const Track track_in_spotify_playlist ("Pardon Me (feat. Future & Mike WiLL Made-It)", {"Lil Yachty", "Future", "Mike WiLL Made-It"}, "Lil Boat 3.5");
    const Track track_not_in_spotify_playlist ("pardon Me (feat. Future & Mike WiLL Made-It)", {"Lil Yachty", "Future", "Mike WiLL Made-It"}, "Lil Boat 3.5");
    const std::string private_youtube_playlist_url = "https://www.youtube.com/playlist?list=PLmfSdJj_ZUFD4_T3E6jPbd6Z8n1zv_cRY";
    const std::string private_spotify_playlist_url = "https://open.spotify.com/playlist/6Qj4W3ybeItAPg0d5e8XVy?si=5bce260d95c6436e&pt=c61881f0d2e0b444b89c0519dfe05748";

    std::cout << "+ Playlist contructor (YouTube)" << std::endl;
    Playlist spotify_playlist ("Playlist 1", std::unordered_multiset<Track> {}, youtube_playlist_url);
    std::cout << spotify_playlist.string() << std::endl;

    std::cout << "+ Playlist.remove_online_connection()" << std::endl;
    spotify_playlist.remove_online_connection();
    std::cout << spotify_playlist.string() << std::endl;
    
    std::cout << "+ Playlist.set_online_connection() (Spotify)" << std::endl;
    spotify_playlist.set_online_connection(spotify_playlist_url);
    std::cout << spotify_playlist.string() << std::endl;

    std::cout << "+ Playlist.add_tracks_from_online_connection() (Spotify)" << std::endl;
    spotify_playlist.add_tracks_from_online_connection();
    std::cout << spotify_playlist.string() << std::endl;

    std::cout << "+ Playlist.add_tracks_from_online_connection() (should NOT result in any new tracks being added)" << std::endl; //Shouldn't add new tracks because add_tracks_from_online_connection only adds new tracks from the online tracklist
    spotify_playlist.add_tracks_from_online_connection();
    std::cout << spotify_playlist.string() << std::endl;

    std::cout << std::endl << "+ Playlist.contains_track()" << std::endl;
    std::cout << (spotify_playlist.contains_track(track_in_spotify_playlist) ? "true" : "false") << std::endl << std::endl;
    std::cout << std::endl << "- Playlist.contains_track()" << std::endl;
    std::cout << (spotify_playlist.contains_track(track_not_in_spotify_playlist) ? "true" : "false") << std::endl << std::endl;

    std::cout << "- Playlist constructor (Spotify)" << std::endl;
    Playlist invalid_playlist_1 ("Playlist 2", std::unordered_multiset<Track> {}, private_spotify_playlist_url);
    std::cout << invalid_playlist_1.string() << std::endl;

    std::cout << "- Playlist constructor (YouTube)" << std::endl;
    Playlist invalid_playlist_2 ("Playlist 3", std::unordered_multiset<Track> {}, private_youtube_playlist_url);
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

        std::cout << sorted_tracklist_1[i].string();

    }
    std::cout << std::endl;

    std::cout << "+ Playlist.get_sorted_playlist() (title descending)" << std::endl;
    std::vector<Track> sorted_tracklist_2 = spotify_playlist.get_sorted_tracklist(sortmode::title, false);
    for(std::size_t i {0}; i < sorted_tracklist_2.size(); i++) {

        std::cout << sorted_tracklist_2[i].string();

    }
    std::cout << std::endl;

    std::cout << "+ Playlist.get_sorted_playlist() (artists ascending)" << std::endl;
    std::vector<Track> sorted_tracklist_3 = spotify_playlist.get_sorted_tracklist(sortmode::artists, true);
    for(std::size_t i {0}; i < sorted_tracklist_3.size(); i++) {

        std::cout << sorted_tracklist_3[i].string();

    }
    std::cout << std::endl;

    std::cout << "+ Playlist.get_sorted_playlist() (album ascending)" << std::endl;
    std::vector<Track> sorted_tracklist_4 = spotify_playlist.get_sorted_tracklist(sortmode::album, true);
    for(std::size_t i {0}; i < sorted_tracklist_4.size(); i++) {

        std::cout << sorted_tracklist_4[i].string();

    }
    std::cout << std::endl;

}

void staccato::track_manager_passive_testing() {

    const std::string sample_track_path = "..\\tracks\\90210.mp3";
    const std::string non_music_file_path = "D:\\car\\idiot23.PNG";
    const std::string normal_spotify_track = "https://open.spotify.com/track/5WNYg3usc6H8N3MBEp4zVk?si=147b46a69ab8486a";
    const std::string unavailable_spotify_track = "https://open.spotify.com/track/2VW3Mcwjgs4NO4P6kx52C0?si=f737371736c74c05";
    const std::string spotify_podcast = "https://open.spotify.com/episode/2wd4bRSwcewwFWDyQ9vlEa?si=26d3b6b43b07460a";
    const std::string normal_youtube_music_track = "https://www.youtube.com/watch?v=SZiwpL62to8&list=PLmfSdJj_ZUFD4_T3E6jPbd6Z8n1zv_cRY&index=2";
    const std::string normal_youtube_non_music_track = "https://www.youtube.com/watch?v=uXwRgnZ990I";
    const std::string age_restricted_youtube_track = "https://www.youtube.com/watch?v=VTmaf0jggF8";
    const std::string unavailable_youtube_track = "https://www.youtube.com/watch?v=NPTHZtcSADs";
    const std::string self_harm_youtube_track = "https://www.youtube.com/watch?v=Y0hl_HRgQtc";

    //Accessing external tracks

    std::cout << "+ TrackManager::get_local_track_info()" << std::endl;
    Track local_track = TrackManager::get_local_track_info(sample_track_path);
    std::cout << local_track.string() << std::endl;

    std::cout << "- TrackManager::get_local_track_info()" << std::endl;
    local_track = TrackManager::get_local_track_info("asodihas");
    std::cout << local_track.string() << std::endl;

    std::cout << "- TrackManager::get_local_track_info()" << std::endl;
    local_track = TrackManager::get_local_track_info(non_music_file_path);
    std::cout << local_track.string() << std::endl;

    std::cout << "- TrackManager::get_local_track_info()" << std::endl;
    local_track = TrackManager::get_local_track_info("");
    std::cout << local_track.string() << std::endl << std::endl;

    std::cout << "+ TrackManager::get_online_track_info() (Normal Spotify track)" << std::endl;
    Track online_track = TrackManager::get_online_track_info(normal_spotify_track);
    std::cout << online_track.string() << std::endl;

    std::cout << "+ TrackManager::get_online_track_info() (Unavailable Spotify track)" << std::endl;
    online_track = TrackManager::get_online_track_info(unavailable_spotify_track);
    std::cout << online_track.string() << std::endl;

    std::cout << "+ TrackManager::get_online_track_info() (Spotify podcast)" << std::endl;
    online_track = TrackManager::get_online_track_info(spotify_podcast);
    std::cout << online_track.string() << std::endl;

    std::cout << "+ TrackManager::get_online_track_info() (YouTube Music)" << std::endl;
    online_track = TrackManager::get_online_track_info(normal_youtube_music_track);
    std::cout << online_track.string() << std::endl;

    std::cout << "+ TrackManager::get_online_track_info() (YouTube, non-music)" << std::endl;
    online_track = TrackManager::get_online_track_info(normal_youtube_non_music_track);
    std::cout << online_track.string() << std::endl;

    std::cout << "+ TrackManager::get_online_track_info() (Age-restricted YouTube)" << std::endl;
    online_track = TrackManager::get_online_track_info(age_restricted_youtube_track);
    std::cout << online_track.string() << std::endl;

    std::cout << "+ TrackManager::get_online_track_info() (Unavailable YouTube video)" << std::endl;
    online_track = TrackManager::get_online_track_info(unavailable_youtube_track);
    std::cout << online_track.string() << std::endl;

    std::cout << "+ TrackManager::get_online_track_info() (Suicide/self-harm YouTube video)" << std::endl;
    online_track = TrackManager::get_online_track_info(self_harm_youtube_track);
    std::cout << online_track.string() << std::endl;

    //Reading and writing the track dictionary
    
    std::cout << "TrackManager::get_track_dict_from_file()" << std::endl;
    TrackManager::get_track_dict_from_file();
    std::cout << std::endl;

    std::cout << "TrackManager::find_extraneous_track_files()" << std::endl;
    std::vector<std::string> extraneous_track_files = TrackManager::find_extraneous_track_files();
    for(std::string track_file: extraneous_track_files) {

        std::cout << track_file << std::endl;

    }
    std::cout << std::endl;

}

void staccato::track_manager_active_testing() {



}