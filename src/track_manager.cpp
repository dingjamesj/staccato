#include "track.hpp"
#include "playlist.hpp"
#include "util.hpp"
#include "track_manager.hpp"
#include "app_manager.hpp"
#include "playlist_tree.hpp"
#include <nlohmann/json.hpp>

using namespace staccato;

std::unordered_map<Track, std::string> TrackManager::track_dict {};
PlaylistTree TrackManager::playlist_tree {};

//====================
//  HELPER FUNCTIONS
//====================

std::filesystem::path TrackManager::get_unique_filename(std::filesystem::path path) {

    std::string original_file_stem = path.stem().string();
    std::size_t count = {1};
    while(std::filesystem::exists(path)) {
    
        if(count > FILENAME_COLLISIONS_TOLERANCE) {

            return std::filesystem::path("");

        }

        path.replace_filename(std::format("{} ({}){}", original_file_stem, count, path.extension().string()));
        count++;

    }

    return path;

}

bool TrackManager::write_file_metadata(const std::string& path, const Track& track) {

    TagLib::FileRef file_ref(path.c_str());

    file_ref.tag()->setTitle(track.title());

    const std::vector<std::string>& track_artists = track.artists();
    std::string artists_str {""};
    for(std::size_t i {0}; i < track_artists.size(); i++) {

        artists_str += track_artists[i];
        if(i < track_artists.size() - 1) {

            artists_str += ", ";

        }

    }
    file_ref.tag()->setArtist(artists_str);

    file_ref.tag()->setAlbum(track.album());

    return file_ref.save();

}

void TrackManager::populate_tree_unorganized() {

    playlist_tree.clear();

    std::filesystem::directory_iterator directory_iter;
    try {

        directory_iter = std::filesystem::directory_iterator(PLAYLIST_FILES_DIR);

    } catch(const std::exception& e) {

        return;

    }

    for(const std::filesystem::directory_entry& file: directory_iter) {

        const std::filesystem::path& path = file.path();
        if(path.extension().string() == ".json") {

            playlist_tree.add_playlist(path.stem().string(), {});
            
        }        

    }

}

//===========================
//  PUBLIC ACCESS FUNCTIONS
//===========================

Track TrackManager::get_local_track_info(const std::string& path) {

    if(!path_is_readable_track_file(path)) {

        return Track();

    }

    TagLib::FileRef file_ref(path.c_str());

    std::string title = file_ref.tag()->title().to8Bit();
    std::string artist_str = file_ref.tag()->artist().to8Bit();
    std::string album = file_ref.tag()->album().to8Bit();

    std::vector<std::string> artists = get_artists_vector_from_str(artist_str);

    return Track(title, artists, album);

}

std::pair<Track, std::string> TrackManager::get_online_track_full_info(const std::string& url) {

    PyObject* py_script = PyUnicode_DecodeFSDefault(AppManager::PY_SCRIPT_NAME.data());
    PyObject* py_module = PyImport_Import(py_script);
    Py_DECREF(py_script);
    if(py_module == nullptr) {

        return {Track(), ""};

    }

    PyObject* py_func = PyObject_GetAttrString(py_module, AppManager::PY_GET_SINGLE_TRACK_FUNC_NAME.data());
    Py_DECREF(py_module);
    //Check if the python function was found
    if(py_func == nullptr || !PyCallable_Check(py_func)) {

        Py_XDECREF(py_func);
        return {Track(), ""};

    }

    PyObject* py_param = PyUnicode_FromString(url.c_str());
    PyGILState_STATE gstate;
    gstate = PyGILState_Ensure();
    PyObject* py_return = PyObject_CallFunctionObjArgs(py_func, py_param, NULL);
    PyGILState_Release(gstate);

    Py_DECREF(py_func);
    Py_DECREF(py_param);
    if(py_return == nullptr || !PyDict_Check(py_return)) {

        Py_XDECREF(py_return);
        return {Track(), ""};

    }

    PyObject* py_title = PyDict_GetItemString(py_return, TrackManager::PY_TITLE_KEY.data());
    PyObject* py_artists_list = PyDict_GetItemString(py_return, TrackManager::PY_ARTISTS_KEY.data());
    PyObject* py_album = PyDict_GetItemString(py_return, TrackManager::PY_ALBUM_KEY.data());
    PyObject* py_artwork_url = PyDict_GetItemString(py_return, TrackManager::PY_ARTWORK_KEY.data());
    if(
        !py_title || !py_artists_list || !py_album || !py_artwork_url || 
        !PyList_Check(py_artists_list) || !PyUnicode_Check(py_title) || !PyUnicode_Check(py_album) || !PyUnicode_Check(py_artwork_url)
    ) {

        Py_DECREF(py_return);
        return {Track(), ""};

    }

    std::vector<std::string> artists {};
    for(Py_ssize_t i {0}; i < PyList_Size(py_artists_list); i++) {

        artists.push_back(PyUnicode_AsUTF8(PyList_GetItem(py_artists_list, i)));

    }

    
    Py_DECREF(py_return);
    return {
        Track(
            PyUnicode_AsUTF8(py_title),
            artists,
            PyUnicode_AsUTF8(py_album)
        ), 
        PyUnicode_AsUTF8(py_artwork_url)
    };

}

std::vector<Track> TrackManager::get_online_tracks(const std::string& url) {

    /*
    PyObject* py_fetcher = PyUnicode_DecodeFSDefault("fetcher");
    PyObject* py_module = PyImport_Import(py_fetcher);
    Py_DECREF(py_fetcher);
    if(py_module == nullptr) {

        return std::vector<Track> {};

    }

    PyObject* py_func = PyObject_GetAttrString(py_module, "get_playlist");
    Py_DECREF(py_module);
    if(py_func == nullptr || !PyCallable_Check(py_func)) {

        Py_XDECREF(py_func);
        return std::vector<Track> {};

    }

    PyObject* py_param = PyUnicode_FromString(url.c_str());
    PyGILState_STATE gstate;
    gstate = PyGILState_Ensure();
    PyObject* py_return = PyObject_CallFunctionObjArgs(py_func, py_param, NULL);
    PyGILState_Release(gstate);
    
    Py_DECREF(py_func);
    Py_DECREF(py_param);
    //Note that the return value should be a list[dict]
    if(py_return == nullptr || !PyList_Check(py_return)) {

        Py_XDECREF(py_return);
        return std::vector<Track> {};

    }

    std::vector<Track> connected_tracklist {};
    Py_ssize_t size = PyList_Size(py_return);
    for(Py_ssize_t i {0}; i < size; i++) {

        PyObject* py_item = PyList_GetItem(py_return, i); //Note that this is a borrowed reference (no need to DECREF)
        if(!PyDict_Check(py_item)) {

            Py_DECREF(py_return);
            return std::vector<Track> {};

        }

        PyObject* py_artists_list = PyDict_GetItemString(py_item, "artists");
        if(py_artists_list == nullptr) {

            Py_DECREF(py_return);
            return std::vector<Track> {};

        }

        std::vector<std::string> artists {};
        for(Py_ssize_t i {0}; i < PyList_Size(py_artists_list); i++) {

            artists.push_back(PyUnicode_AsUTF8(PyList_GetItem(py_artists_list, i)));

        }

        connected_tracklist.push_back(Track(
            PyUnicode_AsUTF8(PyDict_GetItemString(py_item, "title")),
            artists,
            PyUnicode_AsUTF8(PyDict_GetItemString(py_item, "album"))
        ));

    }

    Py_DECREF(py_return);
    return connected_tracklist;
    */

    return {};
}

bool TrackManager::import_track_from_filesystem(const std::string& path, const Track& track) {

    if(track_dict.contains(track)) {

        //(we return true because we only return false if the import was unsuccessful,
        // since an import didn't happen, then the import vacuously was successful)
        return true;

    }

    std::filesystem::path source_path = path;
    std::filesystem::path destination_path = TRACK_FILES_DIR / source_path.filename();

    if(source_path != destination_path) {

        destination_path = get_unique_filename(destination_path);
        std::error_code error;
        bool copy_success = std::filesystem::copy_file(source_path, destination_path, std::filesystem::copy_options::skip_existing, error);

        if(!copy_success) {

            return false;

        }

    }

    track_dict.insert({track, destination_path.string()});
    return write_file_metadata(destination_path.string(), track);
    
}

std::pair<Track, std::string> TrackManager::download_track_from_url(const std::string& url, const std::vector<std::string>& args) {

    PyObject* py_script = PyUnicode_DecodeFSDefault(AppManager::PY_SCRIPT_NAME.data());
    PyObject* py_module = PyImport_Import(py_script);
    Py_DECREF(py_script);
    if(py_module == nullptr) {

        return {Track(), ""};

    }

    PyObject* py_func = PyObject_GetAttrString(py_module, AppManager::PY_URL_DOWNLOAD_FUNC_NAME.data());
    Py_DECREF(py_module);
    if(py_func == nullptr || !PyCallable_Check(py_func)) {

        Py_XDECREF(py_func);
        return {Track(), ""};

    }

    PyObject* py_param_url = PyUnicode_FromString(url.c_str());
    PyObject* py_param_track_files_directory = PyUnicode_FromString(std::string(TRACK_FILES_DIR).c_str());
    PyObject* py_param_args = PyList_New(args.size());
    for(std::size_t i {0}; i < args.size(); i++) {

        PyList_SetItem(py_param_args, i, PyUnicode_FromString(args[i].c_str()));

    }

    PyGILState_STATE gstate;
    gstate = PyGILState_Ensure();
    PyObject* py_return = PyObject_CallFunctionObjArgs(py_func, py_param_url, py_param_track_files_directory, py_param_args, NULL);
    PyGILState_Release(gstate);

    Py_DECREF(py_func);
    Py_DECREF(py_param_url);
    Py_DECREF(py_param_track_files_directory);
    Py_DECREF(py_param_args);
    if(py_return == nullptr || !PyDict_Check(py_return)) {

        Py_XDECREF(py_return);
        return {Track(), ""};

    }

    std::string downloaded_path {PyUnicode_AsUTF8(py_return)};
    PyObject* py_title = PyDict_GetItemString(py_return, TrackManager::PY_TITLE_KEY.data());
    PyObject* py_artists_list = PyDict_GetItemString(py_return, TrackManager::PY_ARTISTS_KEY.data());
    PyObject* py_album = PyDict_GetItemString(py_return, TrackManager::PY_ALBUM_KEY.data());
    PyObject* py_filepath = PyDict_GetItemString(py_return, TrackManager::PY_FILEPATH_KEY.data());
    if(
        !py_title || !py_artists_list || !py_album || !py_filepath || 
        !PyList_Check(py_artists_list) || !PyUnicode_Check(py_title) || !PyUnicode_Check(py_album) || !PyUnicode_Check(py_filepath)
    ) {

        Py_DECREF(py_return);
        return {Track(), ""};

    }

    std::vector<std::string> artists {};
    for(Py_ssize_t i {0}; i < PyList_Size(py_artists_list); i++) {

        artists.push_back(PyUnicode_AsUTF8(PyList_GetItem(py_artists_list, i)));

    }
    
    Py_DECREF(py_return);
    return {
        Track(
            PyUnicode_AsUTF8(py_title),
            artists,
            PyUnicode_AsUTF8(py_album)
        ),
        PyUnicode_AsUTF8(py_filepath)
    };

}

bool TrackManager::import_track_from_info(const Track& track, const std::vector<std::string>& args) {

    if(track_dict.contains(track)) {

        return false;

    }

    PyObject* py_script = PyUnicode_DecodeFSDefault(AppManager::PY_SCRIPT_NAME.data());
    PyObject* py_module = PyImport_Import(py_script);
    Py_DECREF(py_script);
    if(py_module == nullptr) {

        return false;

    }

    PyObject* py_func = PyObject_GetAttrString(py_module, AppManager::PY_INFO_DOWNLOAD_FUNC_NAME.data());
    Py_DECREF(py_module);
    if(py_func == nullptr || !PyCallable_Check(py_func)) {

        Py_XDECREF(py_func);
        return false;

    }

    PyObject* py_param_track_files_directory = PyUnicode_FromString(std::string(TRACK_FILES_DIR).c_str());
    PyObject* py_param_title = PyUnicode_FromString(track.title().c_str());
    PyObject* py_param_artists = PyList_New(track.artists().size());
    for(std::size_t i {0}; i < track.artists().size(); i++) {

        PyList_SetItem(py_param_artists, i, PyUnicode_FromString(track.artists()[i].c_str()));

    }
    PyObject* py_param_album = PyUnicode_FromString(track.album().c_str());
    PyObject* py_param_args = PyList_New(args.size());
    for(std::size_t i {0}; i < args.size(); i++) {

        PyList_SetItem(py_param_args, i, PyUnicode_FromString(args[i].c_str()));

    }

    PyGILState_STATE gstate;
    gstate = PyGILState_Ensure();
    PyObject* py_return = PyObject_CallFunctionObjArgs(py_func, py_param_track_files_directory, py_param_title, py_param_artists, py_param_album, py_param_args, NULL);
    PyGILState_Release(gstate);

    Py_DECREF(py_func);
    Py_DECREF(py_param_track_files_directory);
    Py_DECREF(py_param_title);
    Py_DECREF(py_param_title);
    Py_DECREF(py_param_artists);
    Py_DECREF(py_param_album);
    Py_DECREF(py_param_args);
    if(py_return == nullptr || !PyUnicode_Check(py_return)) {

        Py_XDECREF(py_return);
        return false;

    }

    std::string downloaded_path {PyUnicode_AsUTF8(py_return)};
    Py_DECREF(py_return);
    track_dict[track] = downloaded_path;
    return write_file_metadata(downloaded_path, track);

}

bool TrackManager::path_is_readable_track_file(const std::string& path) {

    //Check if the file is an audio file

    TagLib::FileRef file_ref(path.c_str());
    if(file_ref.isNull()) {

        return false;

    }

    TagLib::AudioProperties* audio_properties = file_ref.audioProperties();
    if(audio_properties == nullptr || audio_properties->lengthInSeconds() == 0) {

        return false;

    }

    //Check if the file is openable

    std::ifstream file(path, std::ios::binary);
    return file.is_open();

}

std::string TrackManager::get_track_file_path(const Track& track) {

    if(track_dict.contains(track)) {

        return track_dict.at(track);

    } else {

        return std::string("");

    }

}

bool TrackManager::track_is_in_dict(const Track& track) {

    return track_dict.contains(track);

}


bool TrackManager::delete_track(const Track& track) {

    if(!track_dict.contains(track)) {

        //If the track isn't in the dict, then we return true immediately
        //(we return true because we only return false if the deletion was unsuccessful,
        // since a deletion didn't happen, then the deletion vacuously was successful)
        return true;

    }

    std::string file_path = track_dict[track];
    track_dict.erase(track);
    return std::filesystem::remove(file_path);

}

bool TrackManager::edit_track(const Track& original_track, const Track& new_track) {

    if(!track_dict.contains(original_track) || track_dict.contains(new_track) || !path_is_readable_track_file(track_dict.at(original_track))) {

        //If the track isn't in the dict, or if the edit will conflict with an existing track, 
        //or if the file isn't readable, then we return true immediately.

        //(we return true because we only return false if the edit was unsuccessful,
        // since an edit didn't happen, then the edit vacuously was successful)
        return true;

    }

    if(write_file_metadata(track_dict.at(original_track), new_track)) {

        std::unordered_map<staccato::Track, std::string>::node_type map_node = track_dict.extract(original_track);
        map_node.key() = new_track;
        track_dict.insert(std::move(map_node));

        return true;

    } else {

        return false;

    }

}

int TrackManager::get_track_duration(const Track& track) {

    if(!track_dict.contains(track)) {

        return 0;

    }

    TagLib::FileRef file_ref(track_dict.at(track).c_str());
    if(file_ref.isNull()) {

        return 0;

    }

    TagLib::AudioProperties* audio_properties = file_ref.audioProperties();
    if(audio_properties == nullptr) {

        return 0;

    }

    return audio_properties->lengthInSeconds();

}

int TrackManager::get_track_bitrate(const Track& track) {

    if(!track_dict.contains(track)) {

        return 0;

    }

    TagLib::FileRef file_ref(track_dict.at(track).c_str());
    if(file_ref.isNull()) {

        return 0;

    }

    TagLib::AudioProperties* audio_properties = file_ref.audioProperties();
    if(audio_properties == nullptr) {

        return 0;

    }

    return audio_properties->bitrate();

}

audiotype TrackManager::get_track_file_type(const Track& track) {

    if(!track_dict.contains(track)) {

        return audiotype::unsupported;

    }

    std::filesystem::path path = track_dict.at(track);

    TagLib::MP4::File m4a (path.c_str());
    if(m4a.isValid()) {

        return audiotype::m4a;

    }

    TagLib::MPEG::File mp3 (path.c_str());
    if(mp3.isValid()){

        return audiotype::mp3;

    }

    TagLib::Ogg::Opus::File opus (path.c_str());
    if(opus.isValid()){

        return audiotype::opus;

    }

    TagLib::Ogg::Vorbis::File vorbis (path.c_str());
    if(vorbis.isValid()) {

        return audiotype::vorbis;

    }

    TagLib::RIFF::WAV::File wav (path.c_str());
    if(wav.isValid()) {

        return audiotype::wav;

    }

    TagLib::FLAC::File flac (path.c_str());
    if(flac.isValid()) {

        return audiotype::flac;

    }

    return audiotype::unsupported;

}

TagLib::ByteVector TrackManager::get_track_artwork(const Track& track) {

    if(!track_dict.contains(track)) {

        return {nullptr, 0};

    }

    return get_track_artwork(track_dict.at(track));

}

TagLib::ByteVector TrackManager::get_track_artwork(const std::string& audio_file_path) {

    TagLib::FileRef file_ref(audio_file_path.c_str());
    if(file_ref.isNull()) {

        return {nullptr, 0};

    }

    TagLib::List<TagLib::VariantMap> picture_properties = file_ref.complexProperties("PICTURE");
    if(picture_properties.isEmpty() || picture_properties.front().isEmpty() || picture_properties.front().value("data").isEmpty()) {

        return {nullptr, 0};

    }

    return picture_properties.front().value("data").toByteVector();

}

bool TrackManager::set_track_artwork(const Track& track, const std::string& artwork_file_path) {

    if(!track_dict.contains(track)) {

        //Like said previously we return true because we only return false if setting the artwork went wrong
        //Since we're not even going to attempt to set up the artwork, it's vacuously true
        return true;

    }

    TagLib::FileRef file_ref(track_dict.at(track).c_str());
    if(file_ref.isNull()) {

        //This however we return false because we ARE trying to set the artwork metadata.
        //It fails because the track file isn't a valid audio file
        return false;

    }

    std::filesystem::path artwork_file = artwork_file_path;
    std::string file_ext = artwork_file.extension().string();
    std::string lowercase_file_ext {""};
    for(std::size_t i {0}; i < file_ext.size(); i++) {

        lowercase_file_ext.push_back(std::tolower(file_ext[i]));

    }
    if(lowercase_file_ext != ".jpg" && lowercase_file_ext != ".png" && lowercase_file_ext != ".jpeg") {

        return false;

    }

    std::ifstream input_stream (artwork_file, std::ios::binary);
    if(!input_stream.is_open()) {

        return false;

    }
    std::vector<char> image_data = {std::istreambuf_iterator<char>(input_stream), {}};
    file_ref.setComplexProperties("PICTURE", {

        {
            {"data", TagLib::ByteVector(image_data.data(), image_data.size())},
            {"pictureType", ""},
            {"mimeType", (file_ext == ".png") ? "image/png" : "image/jpeg"},
            {"description", ""}
        }

    });

    return file_ref.save();

}

bool TrackManager::delete_track_artwork(const Track& track) {

    if(!track_dict.contains(track)) {

        //Like said previously we return true because we only return false if setting the artwork went wrong
        //Since we're not even going to attempt to set up the artwork, it's vacuously true
        return true;

    }

    TagLib::FileRef file_ref(track_dict.at(track).c_str());
    if(file_ref.isNull()) {

        //This however we return false because we ARE trying to set the artwork metadata.
        //It fails because the track file isn't a valid audio file
        return false;

    }

    file_ref.setComplexProperties("PICTURE", {});
    return file_ref.save();

}

int TrackManager::get_playlist_duration(const Playlist& playlist) {

    int total_duration = 0;
    const std::vector<Track>& tracklist = playlist.tracklist();
    for(std::vector<Track>::const_iterator iter = tracklist.begin(); iter != tracklist.end(); ++iter) {

        total_duration += TrackManager::get_track_duration(*iter);

    }

    return total_duration;

}

bool TrackManager::read_track_dict() {

    track_dict.clear();

    std::filesystem::path track_dict_path = std::filesystem::current_path() / std::filesystem::path(TRACK_FILES_DIR) / std::filesystem::path(TRACK_DICT_FILENAME);
    std::ifstream input (track_dict_path);
    if(!input.is_open()) {

        return false;

    }

    const nlohmann::json root = nlohmann::json::parse(input);
    if(!root.contains(TRACK_DICT_JSON_KEY)) {

        return false;

    }

    //Adds each Track-filepath pair from the JSON file to the track dict
    bool no_errors = true;
    for(nlohmann::json::const_iterator iter = root[TRACK_DICT_JSON_KEY].begin(); iter != root[TRACK_DICT_JSON_KEY].end(); ++iter) {

        //Ensure that the JSON object contains ALL required keys
        if(!(*iter).contains(TRACK_TRACK_DICT_JSON_KEY) || !(*iter).contains(FILEPATH_TRACK_DICT_JSON_KEY) || !(*iter)[TRACK_TRACK_DICT_JSON_KEY].contains(TITLE_JSON_KEY) || !(*iter)[TRACK_TRACK_DICT_JSON_KEY].contains(ARTISTS_JSON_KEY) || !(*iter)[TRACK_TRACK_DICT_JSON_KEY].contains(ALBUM_JSON_KEY)) {

            no_errors = false;
            continue;

        }

        const nlohmann::json& title_json = (*iter)[TRACK_TRACK_DICT_JSON_KEY][TITLE_JSON_KEY];
        const nlohmann::json& artists_json = (*iter)[TRACK_TRACK_DICT_JSON_KEY][ARTISTS_JSON_KEY];
        const nlohmann::json& album_json = (*iter)[TRACK_TRACK_DICT_JSON_KEY][ALBUM_JSON_KEY];
        const nlohmann::json& path_json = (*iter)[FILEPATH_TRACK_DICT_JSON_KEY];

        //Ensure that every value is of the correct type
        if(!title_json.is_string() || !artists_json.is_array() || !album_json.is_string() || !path_json.is_string()) {

            no_errors = false;
            continue;

        }

        std::vector<std::string> artists {};
        for(nlohmann::json::const_iterator artists_iter = artists_json.begin(); artists_iter != artists_json.end(); ++artists_iter) {

            //Ensure that only strings exist inside the artists array
            if(!(*artists_iter).is_string()) {

                no_errors = false;
                artists.clear();
                break;

            }

            artists.push_back((*artists_iter).get<std::string>());

        }

        track_dict.insert({
            Track(
                title_json.get<std::string>(),
                artists,
                album_json.get<std::string>()
            ),
            path_json.get<std::string>()
        });

    }

    return no_errors;

}

bool TrackManager::serialize_track_dict() {

    //Since std::ofstream can't create files in nonexistent folders, we first need to ensure that the track folder exists
    std::filesystem::path track_files_directory = std::filesystem::current_path() / std::filesystem::path(TRACK_FILES_DIR);
    std::filesystem::create_directories(track_files_directory);

    std::ofstream output (track_files_directory / std::filesystem::path(TRACK_DICT_FILENAME));
    if(!output.is_open()) {

        return false;

    }

    //Create the JSON object
    nlohmann::json root {};
    root[TRACK_DICT_JSON_KEY] = nlohmann::json::array();

    std::size_t i {0};
    for(const std::pair<const staccato::Track, std::string>& item: track_dict) {

        root[TRACK_DICT_JSON_KEY].push_back(nlohmann::json::object());
        nlohmann::json& track_json = root[TRACK_DICT_JSON_KEY][i];
        track_json[TRACK_TRACK_DICT_JSON_KEY][TITLE_JSON_KEY] = item.first.title();
        track_json[TRACK_TRACK_DICT_JSON_KEY][ARTISTS_JSON_KEY] = nlohmann::json::array();
        for(const std::string& artist: item.first.artists()) {

            track_json[TRACK_TRACK_DICT_JSON_KEY][ARTISTS_JSON_KEY].push_back(artist);

        }
        track_json[TRACK_TRACK_DICT_JSON_KEY][ALBUM_JSON_KEY] = item.first.album();
        track_json[FILEPATH_TRACK_DICT_JSON_KEY] = item.second;
        i++;

    }

    //Serialize the JSON object
    output << std::setw(4) << root << std::endl;

    if(output.fail()) {

        return false;

    }

    return true;

}

std::vector<Track> TrackManager::find_missing_tracks() {

    std::vector<Track> missing_tracks {};
    for(const std::pair<Track, std::string>& pair: track_dict) {

        if(!path_is_readable_track_file(pair.second)) {

            missing_tracks.push_back(pair.first);

        }

    }

    return missing_tracks;

}

std::vector<std::string> TrackManager::find_extraneous_track_files() {

    std::filesystem::directory_iterator directory_iter;
    try {

        directory_iter = std::filesystem::directory_iterator(TRACK_FILES_DIR);

    } catch(const std::exception& e) {

        return {};

    }

    //Obtain a set of all audio file paths from staccato, then find all audio files in the track folder that aren't in this set.
    std::unordered_set<std::filesystem::path> paths_in_dict {};
    for(const std::pair<Track, std::string>& pair: track_dict) {

        paths_in_dict.insert(std::filesystem::path(pair.second));

    }

    std::vector<std::string> extraneous_track_files {};
    for(const std::filesystem::directory_entry& file: directory_iter) {

        //Ignore everything except for valid audio files
        if(!path_is_readable_track_file(file.path().string())) {

            continue;

        }

        if(!paths_in_dict.contains(file.path())) {

            extraneous_track_files.push_back(file.path().string());

        }

    }

    return extraneous_track_files;

}

std::vector<std::tuple<std::string, std::string, std::string, unsigned int>> TrackManager::get_basic_playlist_info_from_files() {

    std::vector<std::tuple<std::string, std::string, std::string, unsigned int>> info {};

    std::filesystem::directory_iterator directory_iter;
    try {

        directory_iter = std::filesystem::directory_iterator(PLAYLIST_FILES_DIR);

    } catch(const std::exception& e) {

        return {};

    }

    //We want to obtain each playlist's id, name, online connection, and size.
    for(const std::filesystem::directory_entry& file: directory_iter) {

        //Ignore non-JSON files
        if(file.path().extension().string() != ".json") {

            continue;

        }
        
        std::ifstream input(file.path());
        if(!input.is_open()) {

            continue;

        }

        //We strictly require each JSON object to have ALL the required keys (playlist name, connection, and size)
        const nlohmann::json root = nlohmann::json::parse(input);
        if(
            root.contains(NAME_PLAYLIST_JSON_KEY) && root[CONNECTION_PLAYLIST_JSON_KEY].is_string() &&
            root.contains(CONNECTION_PLAYLIST_JSON_KEY) && root[CONNECTION_PLAYLIST_JSON_KEY].is_string() &&
            root.contains(SIZE_PLAYLIST_JSON_KEY) && root[SIZE_PLAYLIST_JSON_KEY].is_number_unsigned()
        ) {

            info.push_back({
                file.path().stem().string(), 
                root[NAME_PLAYLIST_JSON_KEY].get<std::string>(), 
                root[CONNECTION_PLAYLIST_JSON_KEY].get<std::string>(), 
                root[SIZE_PLAYLIST_JSON_KEY]
            });

        }
        
    }

    return info;

}

std::string TrackManager::create_playlist(const std::string& name, const std::vector<std::string>& folder_hierarchy) {

    //Do not allow empty names since that'll create an empty Playlist object, which we use as an error value.
    if(name.empty()) {

        return "";

    }

    Playlist playlist (Playlist::create_random_id(PLAYLIST_ID_LENGTH), name, {}, "");

    //Add the playlist ID to the playlist tree and create a JSON file for it
    bool success = playlist_tree.add_playlist(playlist.id(), folder_hierarchy);
    if(!success) {

        return "";

    }

    success = serialize_playlist(playlist);
    if(!success) {

        return "";

    }

    return playlist.id();

}

bool TrackManager::remove_playlist(const std::string& id, const std::vector<std::string>& folder_hierarchy) {

    bool found = false; //Found in either the playlists folder or the playlist tree

    //Remove from the playlist tree,
    //we will remove from the playlist tree no matter what.
    found = playlist_tree.remove_playlist(id, folder_hierarchy);

    //Remove its JSON file:
    //First check if the playlist folder exists
    std::filesystem::directory_iterator directory_iter;
    try {

        directory_iter = std::filesystem::directory_iterator(PLAYLIST_FILES_DIR);

    } catch(const std::exception& e) {

        return found;

    }

    for(const std::filesystem::directory_entry& file: directory_iter) {

        //Ignore directories and non-json files
        if(file.path().extension() != ".json") {

            continue;

        }

        if(file.path().stem().string() == id) {

            std::filesystem::remove(file);
            found = true;
            break;

        }

    }

    return found;

}

Playlist TrackManager::get_playlist(const std::string& id, bool& error_flag) {

    error_flag = false;

    std::filesystem::path playlist_files_directory_path = PLAYLIST_FILES_DIR / std::filesystem::path(id + ".json");
    std::ifstream input (playlist_files_directory_path);
    if(!input.is_open()) {

        error_flag = true;
        return Playlist();

    }

    const nlohmann::json root = nlohmann::json::parse(input);

    std::string name, connection;
    std::vector<Track> tracklist {};

    if(root.contains(NAME_PLAYLIST_JSON_KEY) && root[NAME_PLAYLIST_JSON_KEY].is_string()) {

        name = root[NAME_PLAYLIST_JSON_KEY].get<std::string>();

    }

    if(root.contains(CONNECTION_PLAYLIST_JSON_KEY) && root[CONNECTION_PLAYLIST_JSON_KEY].is_string()) {

        connection = root[CONNECTION_PLAYLIST_JSON_KEY].get<std::string>();

    }

    if(!root.contains(TRACKLIST_PLAYLIST_JSON_KEY) || !root[TRACKLIST_PLAYLIST_JSON_KEY].is_array()) {

        error_flag = true;
        return Playlist(id, name, {}, connection); //If no tracklist exists, early return to reduce code indentation

    }

    //Read the tracklist from the JSON object, and be loose about the format (i.e. it's ok if we're missing some fields, just mark the error flag as true)
    const nlohmann::json& tracklist_json = root[TRACKLIST_PLAYLIST_JSON_KEY];
    for(nlohmann::json::const_iterator iter = tracklist_json.begin(); iter != tracklist_json.end(); ++iter) {

        if(!(*iter).contains(TITLE_JSON_KEY) || !(*iter).contains(ARTISTS_JSON_KEY) || !(*iter).contains(ALBUM_JSON_KEY)) {

            error_flag = true;
            continue;

        }

        const nlohmann::json& title_json = (*iter)[TITLE_JSON_KEY];
        const nlohmann::json& artists_json = (*iter)[ARTISTS_JSON_KEY];
        const nlohmann::json& album_json = (*iter)[ALBUM_JSON_KEY];

        //Ensure that the values are of the correct types
        if(!title_json.is_string() || !artists_json.is_array() || !album_json.is_string()) {

            error_flag = true;
            continue;

        }

        std::vector<std::string> artists {};
        for(nlohmann::json::const_iterator artists_iter = artists_json.begin(); artists_iter != artists_json.end(); ++artists_iter) {

            //Ensure that only strings exist inside the artists array
            if(!(*artists_iter).is_string()) {

                error_flag = true;
                artists.clear();
                break;

            }

            artists.push_back((*artists_iter).get<std::string>());

        }

        tracklist.push_back(Track(
            title_json.get<std::string>(),
            artists,
            album_json.get<std::string>()
        ));

    }

    return Playlist(id, name, tracklist, connection);

}

bool TrackManager::serialize_playlist(const Playlist& playlist) {

    //Since std::ofstream can't create files in nonexistent folders, we first need to ensure that the playlists folder exists
    std::filesystem::path playlist_files_directory = std::filesystem::current_path() / std::filesystem::path(PLAYLIST_FILES_DIR);
    std::filesystem::create_directories(playlist_files_directory);

    std::filesystem::path playlist_files_directory_path = playlist_files_directory / std::filesystem::path(playlist.id() + ".json");
    std::ofstream output (playlist_files_directory_path);
    if(!output.is_open()) {

        return false;

    }

    //Create the JSON object
    nlohmann::json root {};
    root[NAME_PLAYLIST_JSON_KEY] = playlist.name();
    root[CONNECTION_PLAYLIST_JSON_KEY] = playlist.online_connection();
    root[SIZE_PLAYLIST_JSON_KEY] = playlist.tracklist().size();
    root[TRACKLIST_PLAYLIST_JSON_KEY] = nlohmann::json::array();
    
    std::size_t i {0};
    for(const Track& track: playlist.tracklist()) {

        root[TRACKLIST_PLAYLIST_JSON_KEY].push_back(nlohmann::json::object());
        nlohmann::json& track_json = root[TRACKLIST_PLAYLIST_JSON_KEY][i];
        track_json[TITLE_JSON_KEY] = track.title();
        track_json[ARTISTS_JSON_KEY] = nlohmann::json::array();
        track_json[ALBUM_JSON_KEY] = track.album();

        for(const std::string& artist: track.artists()) {

            track_json[ARTISTS_JSON_KEY].push_back(artist);

        }

        i++;

    }

    //Serialize the JSON object
    output << std::setw(4) << root << std::endl;

    if(output.fail()) {

        return false;

    }

    return true;

}

bool TrackManager::read_playlist_tree() {

    playlist_tree.clear();

    std::filesystem::path playlists_directory = std::filesystem::current_path() / std::filesystem::path(PLAYLIST_FILES_DIR);
    std::filesystem::path playlist_tree_path = playlists_directory / std::filesystem::path(PLAYLIST_TREE_FILENAME);
    std::ifstream input (playlist_tree_path);
    if(!input.is_open()) {

        return false;

    }

    //First obtain every playlist in the playlists folder
    std::filesystem::directory_iterator directory_iter;
    try {

        directory_iter = std::filesystem::directory_iterator(playlists_directory);

    } catch(const std::exception& e) {

        return false;

    }

    std::unordered_set<std::string> id_set {};
    for(const std::filesystem::directory_entry& file: directory_iter) {

        const std::filesystem::path& path = file.path();
        if(path.extension().string() == ".json") {

            id_set.insert(path.stem().string());

        }

    }

    //Now iter. through the playlist tree JSON

    const nlohmann::json root = nlohmann::json::parse(input);
    if(!root.contains(CONTENTS_PLAYLIST_TREE_JSON_KEY)) {

        populate_tree_unorganized();
        return false;

    }

    //We will read the playlist tree JSON strictly (i.e. any error in the JSON structure renders the entire tree gone)
    std::vector<std::string> folder_hierarchy {};
    std::stack<std::pair<const nlohmann::json&, nlohmann::json::const_iterator>> stack_json;
    stack_json.push({root, root[CONTENTS_PLAYLIST_TREE_JSON_KEY].begin()});
    while(!stack_json.empty()) {

        nlohmann::json::const_iterator& iter = stack_json.top().second;
        if(iter == stack_json.top().first.end()) {

            stack_json.pop();
            folder_hierarchy.pop_back();
            continue;

        }

        if((*iter).is_string()) { //Iter. is at a playlist ID

            std::string id = (*iter).get<std::string>();
            if(id_set.contains(id)) { //Check that the playlist exists in the Playlists folder

                playlist_tree.add_playlist(id, folder_hierarchy);
                id_set.erase(id);

            }

        } else if((*iter).is_object()) { //Iter. is at a folder

            const nlohmann::json& child_name_json = (*iter)[FOLDER_NAME_PLAYLIST_TREE_JSON_KEY];
            const nlohmann::json& child_contents_json = (*iter)[CONTENTS_PLAYLIST_TREE_JSON_KEY];

            if(!child_name_json.is_string() || !child_contents_json.is_array()) {

                populate_tree_unorganized();
                return false;

            }

            std::string child_name = child_name_json.get<std::string>();
            playlist_tree.add_folder(child_name, folder_hierarchy);
            folder_hierarchy.push_back(child_name);
            stack_json.push({child_contents_json, child_contents_json.begin()});

        } else {

            populate_tree_unorganized();
            return false;

        }

    }

    //Append any playlists left in id_set to the tree (these ones weren't found in the tree JSON)
    for(const std::string& id: id_set) {

        playlist_tree.add_playlist(id, {});

    }

    return true;

}

bool TrackManager::serialize_playlist_tree() {

    //Since std::ofstream can't create files in nonexistent folders, we first need to ensure that the playlists folder exists
    std::filesystem::path playlist_files_directory = std::filesystem::current_path() / std::filesystem::path(PLAYLIST_FILES_DIR);
    std::filesystem::create_directories(playlist_files_directory);

    std::filesystem::path playlist_files_directory_path = playlist_files_directory / std::filesystem::path(PLAYLIST_TREE_FILENAME);
    std::ofstream output (playlist_files_directory_path);
    if(!output.is_open()) {

        return false;

    }

    //Loop through the tree
    nlohmann::json root;
    root[CONTENTS_PLAYLIST_TREE_JSON_KEY] = nlohmann::json::array();
    std::stack<nlohmann::json*> stack_json;
    stack_json.push(&root[CONTENTS_PLAYLIST_TREE_JSON_KEY]);
    for(PlaylistTree::ConstIterator iter = playlist_tree.cbegin(); iter != playlist_tree.cend() && !stack_json.empty(); ++iter) {

        //nullptr signifies that we reached the end of a subfolder
        if(iter.operator->() == nullptr) {

            stack_json.pop();
            continue;

        }

        nlohmann::json& folder_contents_json = *stack_json.top();
        if(iter.is_folder_start()) { //Add a child folder to this folder's contents array

            nlohmann::json&& folder_json = nlohmann::json::object();
            folder_json[FOLDER_NAME_PLAYLIST_TREE_JSON_KEY] = *iter;
            folder_json[CONTENTS_PLAYLIST_TREE_JSON_KEY] = nlohmann::json::array();
            stack_json.push(&folder_json[CONTENTS_PLAYLIST_TREE_JSON_KEY]);
            folder_contents_json.push_back(folder_json);

        } else {

            folder_contents_json.push_back(*iter);

        }

    }

    //Serialize the JSON object
    output << std::setw(4) << root << std::endl;

    if(output.fail()) {

        return false;

    }

    return true;

}

void TrackManager::print_track_dict() {

    if(track_dict.empty()) {

        std::cout << "[empty]" << std::endl << std::endl;
        return;

    }

    int count = 0;
    for(const std::pair<const staccato::Track, std::string>& key_value: track_dict) {

        std::cout << "K: " << key_value.first.string() << "V: " << key_value.second << std::endl;

        if(count < track_dict.size() - 1) {

            std::cout << std::endl;

        }

        count++;

    }

}

void TrackManager::print_basic_playlists_info() {

    std::vector<std::tuple<std::string, std::string, std::string, unsigned int>> playlists_info = get_basic_playlist_info_from_files();
    if(playlists_info.size() == 0) {

        std::cout << "[no playlists available]" << std::endl;

    } else {

        for(std::tuple<std::string, std::string, std::string, unsigned int> info: playlists_info) {

            std::cout << std::get<0>(info) << " " << std::get<1>(info) << " " << std::get<2>(info) << " " << std::get<3>(info) << std::endl;

        }

    }

}
