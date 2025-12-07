#include "track.hpp"
#include "playlist.hpp"
#include "track_manager.hpp"
#include "app_manager.hpp"
#include "util.hpp"
#include "tests.hpp"

using namespace staccato;

void staccato::track_testing() {



}

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
    for(Track track: sorted_tracklist_1) {

        std::cout << track.string();

    }
    std::cout << std::endl;

    std::cout << "+ Playlist.get_sorted_playlist() (title descending)" << std::endl;
    std::vector<Track> sorted_tracklist_2 = spotify_playlist.get_sorted_tracklist(sortmode::title, false);
    for(Track track: sorted_tracklist_2) {

        std::cout << track.string();

    }
    std::cout << std::endl;

    std::cout << "+ Playlist.get_sorted_playlist() (artists ascending)" << std::endl;
    std::vector<Track> sorted_tracklist_3 = spotify_playlist.get_sorted_tracklist(sortmode::artists, true);
    for(Track track: sorted_tracklist_3) {

        std::cout << track.string();

    }
    std::cout << std::endl;

    std::cout << "+ Playlist.get_sorted_playlist() (album ascending)" << std::endl;
    std::vector<Track> sorted_tracklist_4 = spotify_playlist.get_sorted_tracklist(sortmode::album, true);
    for(Track track: sorted_tracklist_4) {

        std::cout << track.string();

    }
    std::cout << std::endl;

}

void staccato::track_manager_passive_local_testing() {

    const std::string music_file_path = "../tracks/90210.mp3";
    const std::string non_music_file_path = "../tracks/idiot23.PNG";
    const std::string invalid_path = "asodiaosi";
    const std::string corrupted_music_file_path = "../tracks/corruptedmp3.mp3";
    const std::string unreachable_music_file_path = ""; //File unreachable by security policy
    const Track track_in_dict ("Devil In A New Dress", {"Kanye West"}, "My Beautiful Dark Twisted Fantasy");
    const Track track_not_in_dict ("asiudasd", {"asid"}, "asiodhasodh");
    const Track empty_track = Track();

    //Track dictionary
    
    std::cout << "TrackManager::read_track_dict()" << std::endl;
    TrackManager::read_track_dict();
    TrackManager::print_track_dict();

    //Reading external tracks (we're using file paths inside the staccato filesystem but that should make no difference)

    std::cout << "+ TrackManager::get_local_track_info()" << std::endl;
    Track local_track = TrackManager::get_local_track_info(music_file_path);
    std::cout << local_track.string() << std::endl;

    std::cout << "- TrackManager::get_local_track_info() (invalid file path)" << std::endl;
    local_track = TrackManager::get_local_track_info(invalid_path);
    std::cout << local_track.string() << std::endl;

    std::cout << "- TrackManager::get_local_track_info() (non music file path)" << std::endl;
    local_track = TrackManager::get_local_track_info(non_music_file_path);
    std::cout << local_track.string() << std::endl;

    std::cout << "- TrackManager::get_local_track_info() (empty file path)" << std::endl;
    local_track = TrackManager::get_local_track_info("");
    std::cout << local_track.string() << std::endl;

    std::cout << "- TrackManager::get_local_track_info() (corrupted file)" << std::endl;
    local_track = TrackManager::get_local_track_info(corrupted_music_file_path);
    std::cout << local_track.string() << std::endl;

    std::cout << "- TrackManager::get_local_track_info() (unreachable file path due to security policy)" << std::endl;
    local_track = TrackManager::get_local_track_info(unreachable_music_file_path);
    std::cout << local_track.string() << std::endl;

    std::cout << "+ TrackManager::path_is_readable_track_file()" << std::endl;
    std::cout << (TrackManager::path_is_readable_track_file(music_file_path) ? "true" : "false") << std::endl << std::endl;

    std::cout << "- TrackManager::path_is_readable_track_file() (non-music file)" << std::endl;
    std::cout << (TrackManager::path_is_readable_track_file(non_music_file_path) ? "true" : "false") << std::endl << std::endl;

    std::cout << "- TrackManager::path_is_readable_track_file() (invalid path)" << std::endl;
    std::cout << (TrackManager::path_is_readable_track_file(invalid_path) ? "true" : "false") << std::endl << std::endl;

    std::cout << "- TrackManager::path_is_readable_track_file() (corrupted mp3 file)" << std::endl;
    std::cout << (TrackManager::path_is_readable_track_file(corrupted_music_file_path) ? "true" : "false") << std::endl << std::endl;

    std::cout << "- TrackManager::path_is_readable_track_file() (unreachable file path due to security policy)" << std::endl;
    std::cout << (TrackManager::path_is_readable_track_file(unreachable_music_file_path) ? "true" : "false") << std::endl << std::endl;

    //Reading internal tracks

    std::cout << "+ TrackManager::track_is_in_dict()" << std::endl;
    std::cout << (TrackManager::track_is_in_dict(track_in_dict) ? "true" : "false") << std::endl << std::endl;

    std::cout << "- TrackManager::track_is_in_dict() (track that's not in the dict)" << std::endl;
    std::cout << (TrackManager::track_is_in_dict(track_not_in_dict) ? "true" : "false") << std::endl << std::endl;

    std::cout << "- TrackManager::track_is_in_dict() (empty track)" << std::endl;
    std::cout << (TrackManager::track_is_in_dict(empty_track) ? "true" : "false") << std::endl << std::endl;

    std::cout << "+ TrackManager::get_track_duration()" << std::endl;
    std::cout << TrackManager::get_track_duration(track_in_dict) << std::endl << std::endl;

    std::cout << "- TrackManager::get_track_duration() (track that's not in the dict)" << std::endl;
    std::cout << TrackManager::get_track_duration(track_not_in_dict) << std::endl << std::endl;

    std::cout << "- TrackManager::get_track_duration() (empty track)" << std::endl;
    std::cout << TrackManager::get_track_duration(empty_track) << std::endl << std::endl;

    std::cout << "+ TrackManager::get_track_bitrate()" << std::endl;
    std::cout << TrackManager::get_track_bitrate(track_in_dict) << std::endl << std::endl;

    std::cout << "- TrackManager::get_track_bitrate() (track that's not in the dict)" << std::endl;
    std::cout << TrackManager::get_track_bitrate(track_not_in_dict) << std::endl << std::endl;

    std::cout << "- TrackManager::get_track_bitrate() (empty track)" << std::endl;
    std::cout << TrackManager::get_track_bitrate(empty_track) << std::endl << std::endl;

    std::cout << "+ TrackManager::get_track_file_type()" << std::endl;
    std::cout << TrackManager::get_track_file_type(track_in_dict) << std::endl << std::endl;

    std::cout << "- TrackManager::get_track_file_type() (track that's not in the dict)" << std::endl;
    std::cout << TrackManager::get_track_file_type(track_not_in_dict) << std::endl << std::endl;

    std::cout << "- TrackManager::get_track_file_type() (empty track)" << std::endl;
    std::cout << TrackManager::get_track_file_type(empty_track) << std::endl << std::endl;

    std::cout << "+ TrackManager::get_track_artwork_raw()" << std::endl;
    std::cout << TrackManager::get_track_artwork_raw(track_in_dict).size() << std::endl << std::endl;

    std::cout << "- TrackManager::get_track_artwork_raw() (track that's not in the dict)" << std::endl;
    std::cout << TrackManager::get_track_artwork_raw(track_not_in_dict).size() << std::endl << std::endl;

    std::cout << "- TrackManager::get_track_artwork_raw() (empty track)" << std::endl;
    std::cout << TrackManager::get_track_artwork_raw(empty_track).size() << std::endl << std::endl;

    std::cout << "+ TrackManager::find_missing_tracks()" << std::endl;
    std::vector<Track> missing_tracks = TrackManager::find_missing_tracks();
    for(Track track: missing_tracks) {

        std::cout << track.string();

    }
    std::cout << std::endl;

    std::cout << "+ TrackManager::find_extraneous_track_files()" << std::endl;
    std::vector<std::string> extraneous_track_files = TrackManager::find_extraneous_track_files();
    for(std::string track_file: extraneous_track_files) {

        std::cout << track_file << std::endl;

    }
    std::cout << std::endl;

    //Reading local playlists

    std::cout << "+ TrackManager::get_basic_playlist_info_from_files()" << std::endl;
    std::vector<std::tuple<std::string, std::string, std::string, std::uint64_t>> basic_info_list = TrackManager::get_basic_playlist_info_from_files();
    for(std::tuple<std::string, std::string, std::string, std::uint64_t> info: basic_info_list) {

        std::cout << "ID: " << std::get<0>(info) << std::endl;
        std::cout << "Name: " << std::get<1>(info) << std::endl;
        std::cout << "Cover Image: " << std::get<2>(info) << std::endl;

    }
    std::cout << std::endl;

    Playlist playlist = Playlist();
    if(basic_info_list.size() > 0) {

        std::cout << "+ TrackManager::get_playlist()" << std::endl;
        playlist = TrackManager::get_playlist(std::get<0>(basic_info_list[0]));
        std::cout << playlist.string() << std::endl;

        std::cout << "+ TrackManager::get_playlist_duration()" << std::endl;
        std::cout << TrackManager::get_playlist_duration(playlist) << std::endl << std::endl;

    } else {

        std::cout << "SKIPPED TEST TrackManager::get_playlist() since no playlists were detected by TrackManager::get_basic_playlist_info_from_files()" << std::endl;
        std::cout << "SKIPPED TEST TrackManager::get_playlist_duration() since no playlists were detected by TrackManager::get_basic_playlist_info_from_files()" << std::endl;

    }

}

void staccato::track_manager_active_local_testing() {

    bool success = false;
    const std::string external_local_track_path = "D:\\ss5msvokUkY.ogg";
    const Track replacement_track ("Replacement", {"Replacement", "Artists"}, "Replacement Album");
    const std::string artwork_file_path = "D:/car/idiot141.JPG";

    std::cout << "+ TrackManager::read_track_dict()" << std::endl;
    success = TrackManager::read_track_dict();
    std::cout << (success ? "true" : "false") << std::endl;
    TrackManager::print_track_dict();
    std::cout << std::endl;

    std::cout << "+ TrackManager::import_local_track()" << std::endl;
    Track imported_track = TrackManager::get_local_track_info(external_local_track_path);
    success = TrackManager::import_local_track(external_local_track_path, imported_track);
    std::cout << (success ? "true" : "false") << std::endl;
    TrackManager::print_track_dict();
    std::cout << std::endl;

    std::cout << "+ TrackManager::edit_track()" << std::endl;
    success = TrackManager::edit_track(imported_track, replacement_track);
    std::cout << (success ? "true" : "false") << std::endl;
    TrackManager::print_track_dict();
    std::cout << std::endl;

    std::cout << "+ TrackManager::set_track_artwork()" << std::endl;
    success = TrackManager::set_track_artwork(replacement_track, artwork_file_path);
    std::cout << (success ? "true" : "false") << std::endl;
    TrackManager::print_track_dict();
    std::cout << std::endl;

    std::cout << "+ TrackManager::import_local_track()" << std::endl;
    imported_track = TrackManager::get_local_track_info(external_local_track_path);
    success = TrackManager::import_local_track(external_local_track_path, imported_track);
    std::cout << (success ? "true" : "false") << std::endl;
    TrackManager::print_track_dict();
    std::cout << std::endl;

    std::cout << "+ TrackManager::delete_track_artwork()" << std::endl;
    success = TrackManager::delete_track_artwork(imported_track);
    std::cout << (success ? "true" : "false") << std::endl;
    TrackManager::print_track_dict();
    std::cout << std::endl;

    std::cout << "+ TrackManager::serialize_track_dict()" << std::endl;
    success = TrackManager::serialize_track_dict();
    std::cout << (success ? "true" : "false") << std::endl;
    TrackManager::print_track_dict();
    std::cout << std::endl;

    std::cout << "+ TrackManager::get_basic_playlist_info_from_files()" << std::endl;
    std::vector<std::tuple<std::string, std::string, std::string, std::uint64_t>> saved_playlists_info = TrackManager::get_basic_playlist_info_from_files();
    TrackManager::print_basic_playlists_info();
    std::cout << std::endl;

    std::cout << "+ TrackManager::serialize_playlist()" << std::endl;
    Playlist playlist ("my playlist", {Track("CHAMPAIN & VACAY", {"Travis Scott", "Don Toliver"}, "JACKBOYS 2"), Track("4EVER", {"Clairo"}, "4EVER"), Track("ANTIFRAGILE", {"LE SSERAFIM"}, "UNFORGIVEN")}, "");
    success = TrackManager::serialize_playlist("playlistid", playlist);
    std::cout << (success ? "true" : "false") << std::endl;
    TrackManager::print_basic_playlists_info();
    std::cout << std::endl;

    std::cout << "+ TrackManager::get_playlist()" << std::endl;
    playlist = TrackManager::get_playlist("playlistid");
    std::cout << playlist.string() << std::endl;

    // std::cout << "+ TrackManager::delete_track()" << std::endl;
    // success = TrackManager::delete_track(imported_track);
    // std::cout << (success ? "true" : "false") << std::endl;
    // TrackManager::print_track_dict();
    // std::cout << std::endl;

}

void staccato::track_manager_passive_online_testing() {

    const std::string normal_spotify_track = "https://open.spotify.com/track/5WNYg3usc6H8N3MBEp4zVk?si=147b46a69ab8486a";
    const std::string unavailable_spotify_track = "https://open.spotify.com/track/2VW3Mcwjgs4NO4P6kx52C0?si=f737371736c74c05";
    const std::string spotify_podcast = "https://open.spotify.com/episode/2wd4bRSwcewwFWDyQ9vlEa?si=26d3b6b43b07460a";
    const std::string normal_youtube_music_track = "https://www.youtube.com/watch?v=SZiwpL62to8&list=PLmfSdJj_ZUFD4_T3E6jPbd6Z8n1zv_cRY&index=2";
    const std::string normal_youtube_non_music_track = "https://www.youtube.com/watch?v=uXwRgnZ990I";
    const std::string age_restricted_youtube_track = "https://www.youtube.com/watch?v=VTmaf0jggF8";
    const std::string unavailable_youtube_track = "https://www.youtube.com/watch?v=NPTHZtcSADs";
    const std::string self_harm_youtube_track = "https://www.youtube.com/watch?v=Y0hl_HRgQtc";
    const std::string normal_spotify_playlist = "https://open.spotify.com/playlist/1MBIdnT23Xujh3iHDAURfB?si=7c38781cb46a493d";
    const std::string normal_youtube_playlist = "https://www.youtube.com/playlist?list=PLmfSdJj_ZUFD_YvXNxd89Mq5pysTjpMSF";
    const std::string spotify_playlist_with_unavailable_song = "https://open.spotify.com/playlist/302qOeuyMFtdYFg5owNOiQ?si=32e42f5d179248eb";
    const std::string private_spotify_playlist_url = "https://open.spotify.com/playlist/6Qj4W3ybeItAPg0d5e8XVy?si=5bce260d95c6436e&pt=c61881f0d2e0b444b89c0519dfe05748";
    const std::string private_youtube_playlist_url = "https://www.youtube.com/playlist?list=PLmfSdJj_ZUFD4_T3E6jPbd6Z8n1zv_cRY";

    std::cout << "+ TrackManager::get_online_track_info() (Normal Spotify track)" << std::endl;
    Track online_track = TrackManager::get_online_track_info(normal_spotify_track).first;
    std::cout << online_track.string() << std::endl;

    std::cout << "+ TrackManager::get_online_track_info() (Unavailable Spotify track)" << std::endl;
    online_track = TrackManager::get_online_track_info(unavailable_spotify_track).first;
    std::cout << online_track.string() << std::endl;

    std::cout << "+ TrackManager::get_online_track_info() (Spotify podcast)" << std::endl;
    online_track = TrackManager::get_online_track_info(spotify_podcast).first;
    std::cout << online_track.string() << std::endl;

    std::cout << "+ TrackManager::get_online_track_info() (YouTube Music)" << std::endl;
    online_track = TrackManager::get_online_track_info(normal_youtube_music_track).first;
    std::cout << online_track.string() << std::endl;

    std::cout << "+ TrackManager::get_online_track_info() (YouTube, non-music)" << std::endl;
    online_track = TrackManager::get_online_track_info(normal_youtube_non_music_track).first;
    std::cout << online_track.string() << std::endl;

    std::cout << "- TrackManager::get_online_track_info() (Age-restricted YouTube)" << std::endl;
    online_track = TrackManager::get_online_track_info(age_restricted_youtube_track).first;
    std::cout << online_track.string() << std::endl;

    std::cout << "- TrackManager::get_online_track_info() (Unavailable YouTube video)" << std::endl;
    online_track = TrackManager::get_online_track_info(unavailable_youtube_track).first;
    std::cout << online_track.string() << std::endl;

    std::cout << "+ TrackManager::get_online_track_info() (Suicide/self-harm YouTube video)" << std::endl;
    online_track = TrackManager::get_online_track_info(self_harm_youtube_track).first;
    std::cout << online_track.string() << std::endl;

    std::cout << "+ TrackManager::online_playlist_is_accessible() (Spotify)" << std::endl;
    std::cout << (TrackManager::online_playlist_is_accessible(normal_spotify_playlist) ? "is accessible" : "NOT accessible") << std::endl << std::endl;

    std::cout << "+ TrackManager::online_playlist_is_accessible() (YouTube)" << std::endl;
    std::cout << (TrackManager::online_playlist_is_accessible(normal_youtube_playlist) ? "is accessible" : "NOT accessible") << std::endl << std::endl;

    std::cout << "- TrackManager::online_playlist_is_accessible() (Invalid URL)" << std::endl;
    std::cout << (TrackManager::online_playlist_is_accessible("aksjdbaksbds2") ? "is accessible" : "NOT accessible") << std::endl << std::endl;

    std::cout << "- TrackManager::online_playlist_is_accessible() (Empty parameter)" << std::endl;
    std::cout << (TrackManager::online_playlist_is_accessible("") ? "is accessible" : "NOT accessible") << std::endl << std::endl;

    std::cout << "- TrackManager::online_playlist_is_accessible() (Private Spotify)" << std::endl;
    std::cout << (TrackManager::online_playlist_is_accessible(private_spotify_playlist_url) ? "is accessible" : "NOT accessible") << std::endl << std::endl;

    std::cout << "- TrackManager::online_playlist_is_accessible() (Private YouTube)" << std::endl;
    std::cout << (TrackManager::online_playlist_is_accessible(private_youtube_playlist_url) ? "is accessible" : "NOT accessible") << std::endl << std::endl;

    std::cout << "- TrackManager::online_playlist_is_accessible() (Malformed Spotify)" << std::endl;
    std::cout << (TrackManager::online_playlist_is_accessible("https://open.spotify.com/plaaylist/3oMkpen2toJFAvPDPml7HC?si=872709bfd3cd4593") ? "is accessible" : "NOT accessible") << std::endl << std::endl;

    std::cout << "- TrackManager::online_playlist_is_accessible() (Malformed Spotify)" << std::endl;
    std::cout << (TrackManager::online_playlist_is_accessible("https://open.spotify.com/playlist/3oMakpen2toJFAvPDPml7HC?si=872709bfd3cd4593") ? "is accessible" : "NOT accessible") << std::endl << std::endl;

    std::cout << "- TrackManager::online_playlist_is_accessible() (Malformed Spotify)" << std::endl;
    std::cout << (TrackManager::online_playlist_is_accessible("https://open.spotify.com/") ? "is accessible" : "NOT accessible") << std::endl << std::endl;

    std::cout << "- TrackManager::online_playlist_is_accessible() (Malformed YouTube)" << std::endl;
    std::cout << (TrackManager::online_playlist_is_accessible("https://www.youtube.com/plaaylist?list=PLmfSdJj_ZUFD_YvXNxd89Mq5pysTjpMSF") ? "is accessible" : "NOT accessible") << std::endl << std::endl;

    std::cout << "- TrackManager::online_playlist_is_accessible() (Malformed YouTube)" << std::endl;
    std::cout << (TrackManager::online_playlist_is_accessible("https://www.youtube.com/playlist?list=PLmfSdJj_aZUFD_YvXNxd89Mq5pysTjpMSF") ? "is accessible" : "NOT accessible") << std::endl << std::endl;

    std::cout << "- TrackManager::online_playlist_is_accessible() (Malformed YouTube)" << std::endl;
    std::cout << (TrackManager::online_playlist_is_accessible("https://www.youtube.com/") ? "is accessible" : "NOT accessible") << std::endl << std::endl;

    std::cout << "+ TrackManager::online_playlist_is_accessible() (Spotify playlist with unavailable track)" << std::endl;
    std::cout << (TrackManager::online_playlist_is_accessible(spotify_playlist_with_unavailable_song) ? "is accessible" : "NOT accessible") << std::endl << std::endl;

    std::cout << "+ TrackManager::get_online_tracklist() (Spotify)" << std::endl;
    std::unordered_multiset<Track> tracklist = TrackManager::get_online_tracklist(normal_spotify_playlist);
    if(tracklist.empty()) {

        std::cout << "[empty]" << std::endl;

    }
    for(Track track: tracklist) {

        std::cout << track.string();

    }
    std::cout << std::endl;

    std::cout << "+ TrackManager::get_online_tracklist() (YouTube)" << std::endl;
    tracklist = TrackManager::get_online_tracklist(normal_youtube_playlist);
    if(tracklist.empty()) {

        std::cout << "[empty]" << std::endl;

    }
    for(Track track: tracklist) {

        std::cout << track.string();

    }
    std::cout << std::endl;

    std::cout << "+ TrackManager::get_online_tracklist() (unavailable Spotify)" << std::endl;
    tracklist = TrackManager::get_online_tracklist(unavailable_spotify_track);
    if(tracklist.empty()) {

        std::cout << "[empty]" << std::endl;

    }
    for(Track track: tracklist) {

        std::cout << track.string();

    }
    std::cout << std::endl;

    std::cout << "+ TrackManager::get_online_tracklist() (unavailable YouTube)" << std::endl;
    tracklist = TrackManager::get_online_tracklist(unavailable_youtube_track);
    if(tracklist.empty()) {

        std::cout << "[empty]" << std::endl;

    }
    for(Track track: tracklist) {

        std::cout << track.string();

    }
    std::cout << std::endl;

    std::cout << "+ TrackManager::get_online_tracklist() (Spotify with unavailable track)" << std::endl;
    tracklist = TrackManager::get_online_tracklist(spotify_playlist_with_unavailable_song);
    if(tracklist.empty()) {

        std::cout << "[empty]" << std::endl;

    }
    for(Track track: tracklist) {

        std::cout << track.string();

    }
    std::cout << std::endl;

}

void staccato::track_manager_active_online_testing() {

    bool success = false;
    const Track track ("ODD Front", {"LOOΠΔ / ODD EYE CIRCLE"}, "Max & Match");
    const std::string youtube_url {"https://www.youtube.com/watch?v=ffMfBDkmlz8"};
    const std::string artwork_url {"https://i.scdn.co/image/ab67616d0000b273fb3c690920c69107439c2866"};

    std::cout << "+ TrackManager::read_track_dict()" << std::endl;
    success = TrackManager::read_track_dict();
    std::cout << (success ? "true" : "false") << std::endl;
    TrackManager::print_track_dict();
    std::cout << std::endl;

    std::cout << "+ TrackManager::download_track()" << std::endl;
    success = TrackManager::download_track(track, youtube_url, artwork_url, false, false);
    std::cout << (success ? "true" : "false") << std::endl;
    TrackManager::print_track_dict();
    std::cout << std::endl;

}

void staccato::app_manager_passive_testing() {

    std::cout << "AppManager::read_saved_queue()" << std::endl;
    std::string main_queue_playlist_id {""};
    std::uint64_t main_position {0}, added_position {0};
    bool success = AppManager::read_last_session_data(main_queue_playlist_id, main_position, added_position);
    std::cout << main_queue_playlist_id << " " << main_position << " " << added_position << std::endl;
    std::cout << "MAIN QUEUE:" << std::endl;
    for(const Track& track: AppManager::get_main_queue()) {

        std::cout << track.string();

    }
    std::cout << "ADDED QUEUE:" << std::endl;
    for(const Track& track: AppManager::get_added_queue()) {

        std::cout << track.string();

    }

    std::cout << "AppManager::read_settings()" << std::endl;
    AppManager::read_settings();
    std::cout << "PINNED ITEMS:" << std::endl;
    for(const std::tuple<bool, std::string, std::vector<std::string>, std::string>& item: AppManager::get_pinned_items()) {

        std::cout << std::get<0>(item) << " " << std::get<1>(item) << " " << std::get<3>(item) << std::endl;

        for(std::string str: std::get<2>(item)) {

            std::cout << str << " ";

        }

        std::cout << std::endl;
        
    }

}

void staccato::app_manager_active_testing() {

    std::cout << "AppManager::push_back_added_queue(), AppManager::set_main_queue()" << std::endl;

    AppManager::push_back_added_queue(Track("CRAZY", {"LE SSERAFIM"}, "CRAZY"));
    AppManager::push_back_added_queue(Track("a lot", {"21 Savage"}, "i am > i was"));
    AppManager::push_back_added_queue(Track("awkyfud", {"sidug"}, "asidu"));
    AppManager::push_back_added_queue(Track("Around Me (feat. Don Toliver)", {"Metro Boomin", "Don Toliver"}, "A Cold Sunday"));
    AppManager::push_back_added_queue(Track("Passionfruit", {"Drake"}, "More Life"));

    AppManager::set_main_queue({
        Track("aosduhuaiusdh", {"aosdhaudh"}, "aousdh"),
        Track("Mask Off", {"Future"}, "FUTURE"),
        Track("Supernatural", {"NewJeans"}, "Supernatural"),
        Track("Summer Bummer (feat. A$AP Rocky & Playboi Carti)", {"Lana Del Rey", "A$AP Rocky", "Playboi Carti"}, "Lust For Life"),
        Track("Kill Jay Z", {"JAY-Z"}, "4:44")
    });

    AppManager::print_main_queue();
    std::cout << std::endl;
    AppManager::print_added_queue();
    std::cout << std::endl;

    std::cout << "AppManager::remove_main_queue_track(), AppManager::remove_added_queue_track()" << std::endl;
    std::cout << (AppManager::remove_main_queue_track(0) ? "true" : "false") << std::endl;
    std::cout << (AppManager::remove_main_queue_track(-1) ? "true" : "false") << std::endl;
    std::cout << (AppManager::remove_main_queue_track(10) ? "true" : "false") << std::endl;
    std::cout << (AppManager::remove_added_queue_track(2) ? "true" : "false") << std::endl;
    std::cout << (AppManager::remove_added_queue_track(20) ? "true" : "false") << std::endl;
    std::cout << (AppManager::remove_added_queue_track(-20) ? "true" : "false") << std::endl;

    AppManager::print_main_queue();
    std::cout << std::endl;
    AppManager::print_added_queue();
    std::cout << std::endl;

    std::cout << "AppManager::move_main_queue_track(), AppManager::move_added_queue_track()" << std::endl;

    std::cout << (AppManager::move_main_queue_track(1, 2) ? "true" : "false") << std::endl;
    std::cout << (AppManager::move_added_queue_track(3, 0) ? "true" : "false") << std::endl;

    AppManager::print_main_queue();
    std::cout << std::endl;
    AppManager::print_added_queue();
    std::cout << std::endl;
    
    std::cout << "AppManager::serialize_session_data()" << std::endl;
    std::cout << (AppManager::serialize_session_data("total_id", 3, 2) ? "Successful serialization" : "FAILED serialization") << std::endl;
    std::string main_queue_playlist_id {""};
    std::uint64_t main_position {0}, added_position {0};
    AppManager::read_last_session_data(main_queue_playlist_id, main_position, added_position);
    std::cout << main_queue_playlist_id << " " << main_position << " " << added_position << std::endl;
    AppManager::print_main_queue();
    std::cout << "---------------------------" << std::endl;
    AppManager::print_added_queue();
    std::cout << std::endl;

    std::cout << "AppManager::add_pinned_playlist(), AppManager::add_pinned_track()" << std::endl;
    std::cout << (AppManager::add_pinned_playlist("id1", "name1", 3, "online_connection") ? "passed" : "failed") << std::endl;
    std::cout << (AppManager::add_pinned_playlist("id1", "name2", 1, "online_connectionasud") ? "failed" : "passed") << std::endl;
    std::cout << (AppManager::add_pinned_playlist("id2", "name2", 1, "") ? "passed" : "failed") << std::endl;
    std::cout << (AppManager::add_pinned_track(Track("track1", {"artists1", "artists12"}, "album1")) ? "passed" : "failed") << std::endl;
    std::cout << (AppManager::add_pinned_track(Track("track1", {"artists1", "artists12"}, "album1")) ? "failed" : "passed") << std::endl;
    std::cout << (AppManager::add_pinned_track(Track("track2", {"artists2"}, "album2")) ? "passed" : "failed") << std::endl;
    std::cout << (AppManager::add_pinned_playlist("id3", "name3", 1, "online_connection3") ? "passed" : "failed") << std::endl;
    std::cout << (AppManager::add_pinned_playlist("id4", "name4", 1, "online_connection3") ? "passed" : "failed") << std::endl;
    std::cout << std::endl;

    AppManager::print_pinned_items();
    std::cout << std::endl;

    std::cout << "AppManager::remove_pinned_item()" << std::endl;
    std::cout << (AppManager::remove_pinned_item(5) ? "true" : "false") << std::endl;
    std::cout << (AppManager::remove_pinned_item(-5) ? "true" : "false") << std::endl;
    std::cout << (AppManager::remove_pinned_item(50) ? "true" : "false") << std::endl;

    AppManager::print_pinned_items();
    std::cout << std::endl;

    std::cout << "AppManager::move_pinned_item()" << std::endl;
    std::cout << (AppManager::move_pinned_item(3, 2) ? "true" : "false") << std::endl;
    std::cout << (AppManager::move_pinned_item(30, 2) ? "true" : "false") << std::endl;

    AppManager::print_pinned_items();
    std::cout << std::endl;

    std::cout << "AppManager::serialize_settings()" << std::endl;
    std::cout << (AppManager::serialize_settings() ? "true" : "false") << std::endl;
    AppManager::read_settings();
    AppManager::print_pinned_items();
    std::cout << std::endl;

}