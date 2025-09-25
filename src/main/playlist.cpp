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
    name {name},
    tracklist {tracklist}
{

    set_online_connection(online_connection);

}

Playlist::Playlist(): name {""}, tracklist {}, online_connection {""} {}

bool Playlist::set_online_connection(const std::string& url) {

    PyObject* py_fetcher = PyUnicode_DecodeFSDefault("fetcher");
    PyObject* py_module = PyImport_Import(py_fetcher);
    Py_DECREF(py_fetcher);
    if(py_module == nullptr) {

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
        return false;

    }

    PyObject* py_param = PyUnicode_FromString(url.c_str());
    PyGILState_STATE gstate;
    gstate = PyGILState_Ensure();
    PyObject* py_return = PyObject_CallFunctionObjArgs(py_func, py_param, NULL);
    PyGILState_Release(gstate);

    Py_DECREF(py_func);
    Py_DECREF(py_param);
    if(py_return == nullptr || !PyBool_Check(py_return)) {

        Py_XDECREF(py_return);
        return false;

    }

    bool is_valid_url = PyObject_IsTrue(py_return) == 1;
    Py_DECREF(py_return);

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

    PyObject* py_fetcher = PyUnicode_DecodeFSDefault("fetcher");
    PyObject* py_module = PyImport_Import(py_fetcher);
    Py_DECREF(py_fetcher);
    if(py_module == nullptr) {

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
        return std::unordered_multiset<Track> {};

    }

    PyObject* py_param = PyUnicode_FromString(online_connection.c_str());
    PyGILState_STATE gstate;
    gstate = PyGILState_Ensure();
    PyObject* py_return = PyObject_CallFunctionObjArgs(py_func, py_param, NULL);
    PyGILState_Release(gstate);
    
    Py_DECREF(py_func);
    Py_DECREF(py_param);
    //Note that the return value should be a list[dict]
    if(py_return == nullptr || !PyList_Check(py_return)) {

        Py_XDECREF(py_return);
        return std::unordered_multiset<Track> {};

    }

    std::unordered_multiset<Track> connected_tracklist {};
    Py_ssize_t size = PyList_Size(py_return);
    for(Py_ssize_t i {0}; i < size; i++) {

        PyObject* py_item = PyList_GetItem(py_return, i); //Note that this is a borrowed reference (no need to DECREF)
        if(!PyDict_Check(py_item)) {

            Py_DECREF(py_return);
            return std::unordered_multiset<Track> {};

        }

        PyObject* py_artists_list = PyDict_GetItemString(py_item, "artists");
        if(py_artists_list == nullptr) {

            Py_DECREF(py_return);
            return std::unordered_multiset<Track> {};

        }

        std::vector<std::string> artists {};
        for(Py_ssize_t i {0}; i < PyList_Size(py_artists_list); i++) {

            artists.push_back(PyUnicode_AsUTF8(PyList_GetItem(py_artists_list, i)));

        }

        connected_tracklist.insert(Track(
            PyUnicode_AsUTF8(PyDict_GetItemString(py_item, "title")),
            artists,
            PyUnicode_AsUTF8(PyDict_GetItemString(py_item, "album"))
        ));

    }

    Py_DECREF(py_return);
    return connected_tracklist;

}

const std::unordered_multiset<Track>& Playlist::get_tracklist() const {

    return tracklist;

}

std::vector<Track> Playlist::get_sorted_tracklist(sortmode sort_mode, bool is_ascending) const {

    std::vector sorted_tracklist(tracklist.begin(), tracklist.end());

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

bool Playlist::contains_track(const Track& track) const {

    return tracklist.contains(track);

}

std::string Playlist::string() const {

    std::string str = "PLAYLIST: " + name + "\n";

    if(!online_connection.empty()) {

        str += online_connection + "\n";

    } else {

        str += "[no online connection]\n";

    }

    std::unordered_multiset<staccato::Track>::const_iterator iter = tracklist.begin();
    for(; iter != tracklist.end(); iter++) {

        str += (*iter).string() + "\n";

    }

    return str;

}

bool Playlist::is_empty() const {

    return name == "" && tracklist.size() == 0 && online_connection == "";

}

std::ostream& operator<<(std::ostream& os, const Playlist& playlist) {

    os << playlist.string();
    return os;

}