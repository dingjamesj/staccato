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
    const std::string& id,
    const std::string& name, 
    const std::vector<Track>& tracklist, 
    const std::string& online_connection
): 
    id_ {id},
    name_ {name},
    tracklist_ {tracklist},
    online_connection_ {online_connection}
{}

Playlist::Playlist(): name_ {""}, tracklist_ {}, online_connection_ {""} {}

void Playlist::set_online_connection(const std::string& url) {

    online_connection_ = url;

}

void Playlist::remove_online_connection() {

    online_connection_ = "";

}

std::string Playlist::online_connection() const {

    return online_connection_;

}

const std::vector<Track>& Playlist::tracklist() const {

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

    tracklist_.push_back(track);

}

void Playlist::add_track(const Track& track, std::size_t index) {

    tracklist_.insert(tracklist_.begin() + index, track);

}

void Playlist::add_tracks_from_online_connection() {

    std::unordered_set<Track> tracklist_set {};
    for(std::size_t i {0}; i < tracklist_.size(); i++) {

        tracklist_set.insert(tracklist_[i]);

    }

    std::vector<Track> online_tracklist = TrackManager::get_online_tracks(online_connection_);
    for(std::size_t i {0}; i < online_tracklist.size(); i++) {

        if(!tracklist_set.contains(online_tracklist[i])) {

            tracklist_.push_back(online_tracklist[i]);

        }

    }

}

const std::string& Playlist::id() const {

    return id_;

}

void Playlist::set_id(std::string id) {

    id_ = id;

}

const std::string& Playlist::name() const {

    return name_;

}

void Playlist::set_name(std::string name) {

    name_ = name;

}

bool Playlist::remove_track(std::size_t index) {

    if(index < 0 || index >= tracklist_.size()) {

        return false;

    }

    tracklist_.erase(tracklist_.begin() + index);

    return true;

}

bool Playlist::contains_track(const Track& track) const {

    for(std::size_t i {0}; i < tracklist_.size(); i++) {

        if(tracklist_[i] == track) {

            return true;

        }

    }

    return false;

}

std::string Playlist::string() const {

    if(is_empty()) {

        return "[empty]\n";

    }

    std::string str = id_ + "\n" + name_ + "\n";

    if(!online_connection_.empty()) {

        str += online_connection_ + "\n";

    } else {

        str += "[no online connection]\n";

    }

    str += std::format("Num. tracks: {}\n", tracklist_.size());

    for(std::size_t i {0}; i < tracklist_.size(); i++) {

        str += tracklist_[i].string();

    }

    return str;

}

bool Playlist::is_empty() const {

    return id_.empty() && name_.empty() && tracklist_.size() == 0 && online_connection_.empty();

}

bool Playlist::operator==(const Playlist& other) const {

    return id_ == other.id_;

}

bool Playlist::operator==(Playlist&& other) const {

    return id_ == other.id_;

}

std::string Playlist::create_random_id(std::size_t id_length) {

    std::random_device rand;
    std::mt19937 generator (rand());
    std::uniform_int_distribution<std::mt19937::result_type> distr (0, 61);

    std::string id {""};
    for(std::size_t i {0}; i < id_length; i++) {

        //If the random number is:
        // - Between 0 and 25, then we will add 65 to convert the number into an uppercase ASCII letter
        // - Between 26 and 51, then we will add 71 to convert the number into a lowercase ASCII letter
        // - Between 52 and 61, then we will subtract 4 to convert the number into a ASCII numerical digit
        int num = distr(generator);
        if(num < 26) {

            num += 65;
            
        } else if(num < 52) {

            num += 71;

        } else {

            num -= 4;

        }

        id += (char) num;

    }

    return id;

}

std::ostream& operator<<(std::ostream& os, const Playlist& playlist) {

    os << playlist.string();
    return os;

}