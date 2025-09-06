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
    std::string name, 
    std::string cover_image_file_path, 
    const std::unordered_multiset<Track>& tracklist, 
    std::string online_connection
): 
    name {name},
    cover_image_file_path {cover_image_file_path},
    tracklist {std::move(tracklist)},
    online_connection {online_connection}
{

    set_online_connection(online_connection);

}

Playlist::Playlist(): name {""}, cover_image_file_path {""}, tracklist {}, online_connection {""} {}

bool Playlist::set_online_connection(const std::string& url) {

    Py_Initialize();
    PyObject* py_fetcher = PyUnicode_DecodeFSDefault("fetcher");
    PyObject* py_module = PyImport_Import(py_fetcher);
    Py_DECREF(py_fetcher);
    if(py_module == nullptr) {

        return false;

    }

    urltype url_type = get_url_type(url);
    PyObject* py_param = PyTuple_Pack(1, url.c_str());
    PyObject* py_func = nullptr;
    if(url_type == urltype::spotify) {

        py_func = PyObject_GetAttrString(py_module, "can_access_spotify_playlist");

    } else if(url_type == urltype::youtube) {

        py_func = PyObject_GetAttrString(py_module, "can_access_youtube_playlist");

    }
    Py_DECREF(py_module);
    if(py_func == nullptr || !PyCallable_Check(py_func)) {

        Py_XDECREF(py_func);
        return false;

    }

    PyObject* py_return = PyObject_CallObject(py_func, py_param);
    Py_DECREF(py_func);
    Py_DECREF(py_param);
    if(py_return == nullptr || !PyBool_Check(py_return)) {

        Py_XDECREF(py_return);
        return false;

    }

    bool is_valid_url = PyObject_IsTrue(py_return) == 1;
    Py_DECREF(py_return);

    Py_Finalize();
    return is_valid_url;

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

std::vector<Track> Playlist::get_sorted_tracklist(sortmode sort_mode, bool is_ascending) const {

    std::vector sorted_tracklist(tracklist.begin(), tracklist.end());

    auto comparator_lambda = [sort_mode, is_ascending](const Track& track1, const Track& track2) {

        switch(sort_mode) {

            case sortmode::title:
                return is_ascending ? (track1.title < track2.title) : (track2.title > track1.title);
            case sortmode::artists:
                return is_ascending ? (track1.artists < track2.artists) : (track1.artists > track2.artists);
            case sortmode::album:
                return is_ascending ? (track1.album < track2.album) : (track1.album > track2.album);
            case sortmode::duration:
                return is_ascending ? (TrackManager::get_track_duration(track1) < TrackManager::get_track_duration(track2)) : (TrackManager::get_track_duration(track1) > TrackManager::get_track_duration(track2));
            case sortmode::bitrate:
                return is_ascending ? (TrackManager::get_track_bitrate(track1) < TrackManager::get_track_bitrate(track2)) : (TrackManager::get_track_bitrate(track1) > TrackManager::get_track_bitrate(track2));
            case sortmode::file_ext:
                return is_ascending ? (TrackManager::get_track_file_ext(track1) < TrackManager::get_track_file_ext(track2)) : (TrackManager::get_track_file_ext(track1) > TrackManager::get_track_file_ext(track2));
            default:
                return true;

        }

    };

    std::sort(sorted_tracklist.begin(), sorted_tracklist.end(), comparator_lambda);
    return sorted_tracklist;

}

void Playlist::add_track(const Track& track) {

    tracklist.insert(track);

}

bool Playlist::remove_track(const Track& track) {

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

bool Playlist::is_empty() const {

    return name == "" && cover_image_file_path == "" && tracklist.size() == 0 && online_connection == "";

}

std::ostream& operator<<(std::ostream& os, const Playlist& playlist) {

    os << playlist.string();
    return os;

}