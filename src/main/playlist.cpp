#include "util.hpp"
#include "track_manager.hpp"
#include "track.hpp"
#include "playlist.hpp"

using namespace staccato;

Playlist::Playlist(
    std::string name, 
    std::string cover_image_file_path, 
    const std::unordered_multiset<Track>& tracklist, 
    std::string online_connection
): 
    name{name},
    cover_image_file_path{cover_image_file_path},
    tracklist{std::move(tracklist)},
    online_connection{online_connection}
{

    set_online_connection(online_connection);

}

void Playlist::remove_online_connection() {

    online_connection = "";

}

std::string Playlist::get_online_connection() const {

    return online_connection;

}

const std::unordered_multiset<Track>& Playlist::get_tracklist() const {

    return tracklist;

}

std::vector<Track> Playlist::get_sorted_tracklist(SortMode sort_mode, bool is_ascending) const {

    std::vector sorted_tracklist(tracklist.begin(), tracklist.end());

    auto comparator_lambda = [sort_mode, is_ascending](const Track& track1, const Track& track2) {

        switch(sort_mode) {

            case SortMode::TITLE:
                return is_ascending ? (track1.title < track2.title) : (track2.title > track1.title);
            case SortMode::ARTISTS:
                return is_ascending ? (track1.artists < track2.artists) : (track1.artists > track2.artists);
            case SortMode::ALBUM:
                return is_ascending ? (track1.album < track2.album) : (track1.album > track2.album);
            case SortMode::DURATION:
                return is_ascending ? (TrackManager::get_track_duration(track1) < TrackManager::get_track_duration(track2)) : (TrackManager::get_track_duration(track1) > TrackManager::get_track_duration(track2));
            case SortMode::BITRATE:
                return is_ascending ? (TrackManager::get_track_bitrate(track1) < TrackManager::get_track_bitrate(track2)) : (TrackManager::get_track_bitrate(track1) > TrackManager::get_track_bitrate(track2));
            case SortMode::FILE_EXT:
                return is_ascending ? (TrackManager::get_track_file_ext(track1) < TrackManager::get_track_file_ext(track2)) : (TrackManager::get_track_file_ext(track1) > TrackManager::get_track_file_ext(track2));
            default:
                return true;

        }

    };

    std::sort(sorted_tracklist.begin(), sorted_tracklist.end());
    return sorted_tracklist;

}

void Playlist::add_track(Track track) {

    tracklist.insert(track);

}

bool Playlist::remove_track(Track track) {

    std::unordered_multiset<staccato::Track>::iterator iter = tracklist.find(track);
    if(iter == tracklist.end()) {

        return false;

    }

    tracklist.erase(iter);
    return true;

}

bool Playlist::contains_track(Track track) const {

    return tracklist.contains(track);

}

int Playlist::get_total_duration() const {

    int total_duration = 0;
    std::unordered_multiset<staccato::Track>::const_iterator iter = tracklist.cbegin();
    for(; iter != tracklist.end(); iter++) {

        total_duration += TrackManager::get_track_duration(*iter);

    }

    return total_duration;

}

std::string Playlist::string() const {

    std::string str = name + "\n" + cover_image_file_path + "\n";
    if(!online_connection.empty()) {

        str += online_connection + "\n";

    }

    std::unordered_multiset<staccato::Track>::const_iterator iter = tracklist.cbegin();
    for(; iter != tracklist.end(); iter++) {

        str += (*iter).string() + "\n";

    }

    return str;

}

std::ostream& operator<<(std::ostream& os, const Playlist& playlist) {

    os << playlist.string();
    return os;

}