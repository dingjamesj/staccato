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
    tracklist {std::move(tracklist)}
{

    std::cout << "PRE CONNECT" << std::endl;
    set_online_connection(online_connection);
    std::cout << "POST CONNECT" << std::endl;

}

Playlist::Playlist(): name {""}, cover_image_file_path {""}, tracklist {}, online_connection {""} {}

bool Playlist::set_online_connection(const std::string& url) {

    bool init_success = init_python();
    if(!init_success) {

        return false;

    }

    PyObject* py_fetcher = PyUnicode_DecodeFSDefault("fetcher");
    PyObject* py_module = PyImport_Import(py_fetcher);
    Py_DECREF(py_fetcher);
    if(py_module == nullptr) {

        Py_Finalize();
        return false;

    }

    urltype url_type = get_url_type(url);
    PyObject* py_func = nullptr;
    if(url_type == urltype::spotify) {

        py_func = PyObject_GetAttrString(py_module, "can_access_spotify_playlist");

    } else if(url_type == urltype::youtube) {

        py_func = PyObject_GetAttrString(py_module, "can_access_youtube_playlist");

    }
    Py_DECREF(py_module);
    if(py_func == nullptr || !PyCallable_Check(py_func)) {

        Py_XDECREF(py_func);
        Py_Finalize();
        return false;

    }

    PyObject* py_param = PyUnicode_FromString(url.c_str());

    PyGILState_STATE gstate;
    gstate = PyGILState_Ensure();
    // PyObject* py_return = PyObject_CallObject(py_func, py_param); //This is crashing
    PyObject* py_return = PyObject_CallFunctionObjArgs(py_func, py_param, NULL);
    PyGILState_Release(gstate);
    std::cout << "playlist.cpp: test" << std::endl;
    Py_DECREF(py_func);
    Py_DECREF(py_param);
    if(py_return == nullptr || !PyBool_Check(py_return)) {

        Py_XDECREF(py_return);
        PyErr_Print();
        Py_Finalize();
        return false;

    }

    std::cout << "playlist.cpp: " << ((PyObject_IsTrue(py_return) == 1) ? "true" : "false") << std::endl;
    bool is_valid_url = PyObject_IsTrue(py_return) == 1;
    Py_DECREF(py_return);

    Py_Finalize();

    if(is_valid_url) {

        online_connection = url;

    }

    return is_valid_url;

}

void Playlist::remove_online_connection() {

    online_connection = "";

}

std::string Playlist::get_online_connection() const {

    return online_connection;

}

std::unordered_multiset<Track> Playlist::get_online_connection_tracklist() const {

    Py_Initialize();
    PyObject* py_fetcher = PyUnicode_DecodeFSDefault("fetcher");
    PyObject* py_module = PyImport_Import(py_fetcher);
    Py_DECREF(py_fetcher);
    if(py_module == nullptr) {

        Py_Finalize();
        return std::unordered_multiset<Track> {};

    }

    urltype url_type = get_url_type(online_connection);
    PyObject* py_func = nullptr;
    if(url_type == urltype::spotify) {

        py_func = PyObject_GetAttrString(py_module, "get_spotify_playlist");

    } else if(url_type == urltype::youtube) {

        py_func = PyObject_GetAttrString(py_module, "get_youtube_playlist");

    }
    Py_DECREF(py_module);
    if(py_func == nullptr || !PyCallable_Check(py_func)) {

        Py_XDECREF(py_func);
        Py_Finalize();
        return std::unordered_multiset<Track> {};

    }

    PyObject* py_param = PyTuple_Pack(1, online_connection.c_str());
    PyObject* py_return = PyObject_CallObject(py_func, py_param);
    Py_DECREF(py_func);
    Py_DECREF(py_param);
    //Note that the return value should be a list[dict]
    if(py_return == nullptr || !PyList_Check(py_return)) {

        Py_XDECREF(py_return);
        Py_Finalize();
        return std::unordered_multiset<Track> {};

    }

    std::unordered_multiset<Track> tracklist {};
    Py_ssize_t size = PyList_Size(py_return);
    for(Py_ssize_t i {0}; i < size; i++) {

        PyObject* py_item = PyList_GetItem(py_return, i); //Note that this is a borrowed reference (no need to DECREF)
        if(!PyDict_Check(py_item)) {

            Py_DECREF(py_return);
            Py_Finalize();
            return std::unordered_multiset<Track> {};

        }

        tracklist.insert(Track(
            PyUnicode_AsUTF8(PyDict_GetItemString(py_item, "title")),
            PyUnicode_AsUTF8(PyDict_GetItemString(py_item, "artists")),
            PyUnicode_AsUTF8(PyDict_GetItemString(py_item, "album"))
        ));

    }

    Py_DECREF(py_return);
    Py_Finalize();
    return tracklist;

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

    std::string str = name + "\n";

    if(!cover_image_file_path.empty()) {
        
        str += cover_image_file_path + "\n";

    } else {

        str += "[no cover image]\n";

    }

    if(!online_connection.empty()) {

        str += online_connection + "\n";

    } else {

        str += "[no online connection]\n";

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