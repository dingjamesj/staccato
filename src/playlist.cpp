#include "util.hpp"
#include "track_manager.hpp"
#include "track.hpp"
#include "playlist.hpp"

using namespace staccato;

//====================
//  HELPER FUNCTIONS
//====================

urltype Playlist::get_url_type(const std::string& url) {

    if(url.find("spotify.com") != std::string::npos) {

        return urltype::spotify;

    }

    if(url.find("youtube.com") != std::string::npos || url.find("youtu.be") != std::string::npos) {

        return urltype::youtube;

    }

    return urltype::unknown;

}

//===========================
//  PUBLIC ACCESS FUNCTIONS
//===========================

Playlist::Playlist(
    const std::string& name, 
    const std::unordered_multiset<Track>& tracklist, 
    const std::string& online_connection
): 
    name_ {name},
    tracklist_ {tracklist},
    last_played_time_ {0}
{

    set_online_connection(online_connection);

}

Playlist::Playlist(): name_ {""}, tracklist_ {}, online_connection_ {""}, last_played_time_ {0} {}

bool Playlist::set_online_connection(const std::string& url) {

    if(TrackManager::online_playlist_is_accessible(url)) {

        online_connection_ = url;
        return true;

    }

    return false;

}

void Playlist::remove_online_connection() {

    online_connection_ = "";

}

std::string Playlist::online_connection() const {

    return online_connection_;

}

const std::unordered_multiset<Track>& Playlist::tracklist() const {

    return tracklist_;

}

std::vector<Track> Playlist::get_sorted_tracklist(sortmode sort_mode, bool is_ascending) const {

    std::vector sorted_tracklist (tracklist_.begin(), tracklist_.end());

    auto comparator_lambda = [sort_mode, is_ascending](const Track& track1, const Track& track2) {

        int compare_result = Track::compare(track1, track2, sort_mode);

        //If the two tracks are equal together according to the sort mode, then sort based on title.
        //If the sort mode is by title, then sort by artists.
        if(compare_result == 0 && sort_mode != sortmode::title) {

            compare_result = Track::compare(track1, track2, sortmode::title);

        } else if(compare_result == 0 && sort_mode == sortmode::title) {

            compare_result = Track::compare(track1, track2, sortmode::artists);

        }

        if(is_ascending) {

            return compare_result == -1;

        } else {

            return compare_result == 1;

        }

    };

    std::sort(sorted_tracklist.begin(), sorted_tracklist.end(), comparator_lambda);
    return sorted_tracklist;

}

void Playlist::add_track(const Track& track) {

    tracklist_.insert(track);

}

void Playlist::add_tracks_from_online_connection() {

    std::unordered_multiset<Track> online_tracklist = TrackManager::get_online_tracklist(online_connection_);
    std::unordered_multiset<Track>::iterator iter = online_tracklist.begin();
    while(iter != online_tracklist.end()) {

        if(tracklist_.contains(*iter)) {

            iter++;
            continue;

        }

        tracklist_.insert(*iter);
        iter++;

    }

}

const std::string& Playlist::name() const {

    return name_;

}

void Playlist::set_name(std::string name) {

    name_ = name;

}

bool Playlist::remove_track(const Track& track) {

    std::unordered_multiset<staccato::Track>::iterator iter = tracklist_.find(track);
    if(iter == tracklist_.end()) {

        return false;

    }

    tracklist_.erase(iter);
    return true;

}

bool Playlist::contains_track(const Track& track) const {

    return tracklist_.contains(track);

}

std::string Playlist::string() const {

    if(is_empty()) {

        return "[empty]\n";

    }

    std::string str = name_ + "\n";

    if(!online_connection_.empty()) {

        str += online_connection_ + "\n";

    } else {

        str += "[no online connection]\n";

    }

    str += std::format("Num. tracks: {}\n", tracklist_.size());

    std::unordered_multiset<staccato::Track>::const_iterator iter = tracklist_.begin();
    for(; iter != tracklist_.end(); iter++) {

        str += (*iter).string();

    }

    return str;

}

int64_t Playlist::last_played_time() const {

    return last_played_time_;

}

void Playlist::set_last_played_time_to_now() {

    last_played_time_ = std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now().time_since_epoch()).count();

}

bool Playlist::is_empty() const {

    return name_ == "" && tracklist_.size() == 0 && online_connection_ == "" && last_played_time_ == 0;

}

std::ostream& operator<<(std::ostream& os, const Playlist& playlist) {

    os << playlist.string();
    return os;

}