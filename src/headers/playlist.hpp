#ifndef PLAYLIST_HPP
#define PLAYLIST_HPP

#include <string>
#include <vector>
#include "track.hpp"

namespace staccato {

enum class TrackSortMode {TITLE_ASCEND, TITLE_DESCEND, ARTISTS_ASCEND, ARTISTS_DESCEND, ALBUM_ASCEND, ALBUM_DESCEND, DURATION_ASCEND, DURATION_DESCEND, CREATION_ASCEND, CREATION_DESCEND, BITRATE_ASCEND, BITRATE_DESCEND};

class Playlist {

private:
    std::vector<char> cover_image_raw;
    std::vector<staccato::Track> tracklist;
    staccato::TrackSortMode current_sort_setting;
    std::string directory_connection;

public:
    std::string name;
    void set_cover_image(std::string image_path);
    void remove_cover_image();
    const std::vector<char>& get_cover_image_raw();
    const std::vector<char>& get_tracklist();
    int add_track(staccato::Track track);
    int remove_track(staccato::Track track);
    staccato::TrackSortMode get_current_sort_mode();
    bool track_exists(staccato::Track track);
    int get_duration();
    void add_directory_connection(std::string directory);
    void remove_directory_connection();

};

}

#endif