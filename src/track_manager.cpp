#include "track_manager.hpp"

using namespace staccato;

std::unordered_map<Track, std::string> TrackManager::track_dict = {};

//====================
//  HELPER FUNCTIONS
//====================

std::filesystem::path TrackManager::get_unique_filename(std::filesystem::path path) {

    std::string original_file_stem = path.stem().string();
    std::size_t count = {1};
    while(std::filesystem::exists(path)) {
    
        if(count > 10000) {

            //We hard stop the loop if count reaches 10000 (no way there's over 10000 files with the same name)
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

std::string TrackManager::ifstream_read_file_header(std::ifstream& input) {

    //Read file header
    char c = '\0';
    std::string header {""};
    while(!input.eof()) {

        c = input.get();
        
        if(input.fail()) {

            return "";

        }

        if(c == '\0') {

            break;

        }

        header.push_back(c);

    }

    return header;
    
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

std::pair<Track, std::string> TrackManager::get_online_track_info(const std::string& url) {

    PyObject* py_fetcher = PyUnicode_DecodeFSDefault("fetcher");
    PyObject* py_module = PyImport_Import(py_fetcher);
    Py_DECREF(py_fetcher);
    if(py_module == nullptr) {

        return {Track(), ""};

    }

    PyObject* py_func = PyObject_GetAttrString(py_module, "get_track");
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

    PyObject* py_title = PyDict_GetItemString(py_return, "title");
    PyObject* py_album = PyDict_GetItemString(py_return, "album");
    PyObject* py_artists_list = PyDict_GetItemString(py_return, "artists");
    PyObject* py_artwork_url = PyDict_GetItemString(py_return, "artwork_url");
    if(!py_title || !py_artists_list || !py_album || !py_artwork_url) {

        Py_DECREF(py_return);
        return {Track(), ""};

    }

    std::vector<std::string> artists {};
    for(Py_ssize_t i {0}; i < PyList_Size(py_artists_list); i++) {

        artists.push_back(PyUnicode_AsUTF8(PyList_GetItem(py_artists_list, i)));

    }

    Track track (
        PyUnicode_AsUTF8(py_title),
        artists,
        PyUnicode_AsUTF8(py_album)
    );
    Py_DECREF(py_return);
    return {track, PyUnicode_AsUTF8(py_artwork_url)};

}

std::string TrackManager::get_best_youtube_url(const Track& track) {

    //Find the best YouTube URL and then download

    PyObject* py_fetcher = PyUnicode_DecodeFSDefault("fetcher");
    PyObject* py_module = PyImport_Import(py_fetcher);
    Py_DECREF(py_fetcher);
    if(py_module == nullptr) {

        return "";

    }

    PyObject* py_func = PyObject_GetAttrString(py_module, "find_best_youtube_url");
    Py_DECREF(py_module);
    if(py_func == nullptr || !PyCallable_Check(py_func)) {

        Py_XDECREF(py_func);
        return "";

    }

    PyObject* py_param_title = PyUnicode_FromString(track.title().c_str());
    const std::vector<std::string>& track_artists = track.artists();
    PyObject* py_param_artists = PyList_New(0);
    for(std::size_t i {0}; i < track_artists.size(); i++) {

        PyList_Append(py_param_artists, PyUnicode_FromString(track_artists[i].c_str()));

    }
    
    PyGILState_STATE gstate;
    gstate = PyGILState_Ensure();
    PyObject* py_return = PyObject_CallFunctionObjArgs(py_func, py_param_title, py_param_artists, NULL);
    PyGILState_Release(gstate);
    
    Py_DECREF(py_func);
    Py_DECREF(py_param_title);
    Py_DECREF(py_param_artists);
    if(py_return == nullptr || !PyUnicode_Check(py_return)) {

        Py_XDECREF(py_return);
        return "";

    }

    std::string youtube_url {PyUnicode_AsUTF8(py_return)};
    Py_DECREF(py_return);
    return youtube_url;

}

std::string TrackManager::get_musicbrainz_artwork_url(const std::string& title, const std::string& lead_artist, const std::string& album) {

    PyObject* py_fetcher = PyUnicode_DecodeFSDefault("fetcher");
    PyObject* py_module = PyImport_Import(py_fetcher);
    Py_DECREF(py_fetcher);
    if(py_module == nullptr) {

        return "";

    }

    PyObject* py_func = PyObject_GetAttrString(py_module, "get_artwork_url_from_musicbrainz");
    Py_DECREF(py_module);
    if(py_func == nullptr || !PyCallable_Check(py_func)) {

        Py_XDECREF(py_func);
        return "";

    }

    PyObject* py_param_title = PyUnicode_FromString(title.c_str());
    PyObject* py_param_lead_artist = PyUnicode_FromString(lead_artist.c_str());
    PyObject* py_param_album = PyUnicode_FromString(album.c_str());

    PyGILState_STATE gstate;
    gstate = PyGILState_Ensure();
    PyObject* py_return = PyObject_CallFunctionObjArgs(py_func, py_param_album, py_param_lead_artist, py_param_title, NULL);
    PyGILState_Release(gstate);

    Py_DECREF(py_func);
    Py_DECREF(py_param_title);
    Py_DECREF(py_param_lead_artist);
    Py_DECREF(py_param_album);
    if(py_return == nullptr || !PyUnicode_Check(py_return)) {

        Py_XDECREF(py_return);
        return "";

    }

    std::string image_url {PyUnicode_AsUTF8(py_return)};
    Py_DECREF(py_return);
    return image_url;

}

bool TrackManager::online_playlist_is_accessible(const std::string& url) {

    PyObject* py_fetcher = PyUnicode_DecodeFSDefault("fetcher");
    PyObject* py_module = PyImport_Import(py_fetcher);
    Py_DECREF(py_fetcher);
    if(py_module == nullptr) {

        return false;

    }

    PyObject* py_func = PyObject_GetAttrString(py_module, "can_access_playlist");
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

    return is_valid_url;

}

std::unordered_multiset<Track> TrackManager::get_online_tracklist(const std::string& url) {

    PyObject* py_fetcher = PyUnicode_DecodeFSDefault("fetcher");
    PyObject* py_module = PyImport_Import(py_fetcher);
    Py_DECREF(py_fetcher);
    if(py_module == nullptr) {

        return std::unordered_multiset<Track> {};

    }

    PyObject* py_func = PyObject_GetAttrString(py_module, "get_playlist");
    Py_DECREF(py_module);
    if(py_func == nullptr || !PyCallable_Check(py_func)) {

        Py_XDECREF(py_func);
        return std::unordered_multiset<Track> {};

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

bool TrackManager::import_local_track(const std::string& path, const Track& track) {

    if(track_dict.contains(track)) {

        //(we return true because we only return false if the import was unsuccessful,
        // since an import didn't happen, then the import vacuously was successful)
        return true;

    }

    std::filesystem::path source_path = path;
    std::filesystem::path destination_path = TRACK_FILES_DIRECTORY / source_path.filename();

    destination_path = get_unique_filename(destination_path);
    std::error_code error;
    bool copy_success = std::filesystem::copy_file(source_path, destination_path, std::filesystem::copy_options::skip_existing, error);

    if(!copy_success) {

        return false;

    }

    std::string new_track_path = destination_path.string();
    track_dict.insert({track, new_track_path});
    return write_file_metadata(new_track_path, track);
    
}

bool TrackManager::download_track(const Track& track, const std::string& youtube_url, const std::string& artwork_url, bool force_mp3, bool force_opus) {

    if(track_dict.contains(track)) {

        //(we return true because we only return false if the download was unsuccessful,
        // since an download didn't happen, then the download vacuously was successful)
        return true;

    }

    PyObject* py_downloader = PyUnicode_DecodeFSDefault("downloader");
    PyObject* py_module = PyImport_Import(py_downloader);
    Py_DECREF(py_downloader);
    if(py_module == nullptr) {

        return false;

    }

    PyObject* py_func = PyObject_GetAttrString(py_module, "download_youtube_track");
    Py_DECREF(py_module);
    if(py_func == nullptr || !PyCallable_Check(py_func)) {

        Py_XDECREF(py_func);
        return false;

    }

    PyObject* py_param_url = PyUnicode_FromString(youtube_url.c_str());
    PyObject* py_param_artwork_url = PyUnicode_FromString(artwork_url.c_str());
    PyObject* py_param_track_files_directory = PyUnicode_FromString(std::string(TRACK_FILES_DIRECTORY).c_str());
    PyObject* py_param_force_mp3 = PyBool_FromLong(force_mp3);
    PyObject* py_param_force_opus = PyBool_FromLong(force_opus);
    PyGILState_STATE gstate;
    gstate = PyGILState_Ensure();
    PyObject* py_return = PyObject_CallFunctionObjArgs(py_func, py_param_url, py_param_artwork_url, py_param_track_files_directory, py_param_force_mp3, py_param_force_opus, NULL);
    PyGILState_Release(gstate);

    Py_DECREF(py_func);
    Py_DECREF(py_param_url);
    Py_DECREF(py_param_artwork_url);
    Py_DECREF(py_param_track_files_directory);
    Py_DECREF(py_param_force_mp3);
    Py_DECREF(py_param_force_opus);
    if(py_return == nullptr || !PyUnicode_Check(py_return)) {

        Py_XDECREF(py_return);
        return false;

    }

    std::string downloaded_path {PyUnicode_AsUTF8(py_return)};
    Py_DECREF(py_return);
    track_dict.insert({track, downloaded_path});
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

std::vector<char> TrackManager::get_track_artwork_raw(const Track& track) {

    if(!track_dict.contains(track)) {

        return std::vector<char>();

    }

    TagLib::FileRef file_ref(track_dict.at(track).c_str());
    if(file_ref.isNull()) {

        return std::vector<char>();

    }

    TagLib::List<TagLib::VariantMap> picture_properties = file_ref.complexProperties("PICTURE");

    TagLib::Variant value = picture_properties.front().value("data");
    if(value.type() == TagLib::Variant::ByteVector) {

        const char* data = value.value<TagLib::ByteVector>().data();
        std::vector<char> return_vector {};
        for(std::size_t i = 0; data[i] != '\0'; i++) {

            return_vector.push_back(data[i]);

        }

        return return_vector;

    }

    return std::vector<char>();

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
    const std::unordered_multiset<Track>& tracklist = playlist.get_tracklist();
    std::unordered_multiset<staccato::Track>::const_iterator iter = tracklist.cbegin();
    for(; iter != tracklist.end(); iter++) {

        total_duration += TrackManager::get_track_duration(*iter);

    }

    return total_duration;

}

bool TrackManager::read_track_dict() {

    track_dict.clear();

    std::ifstream input(std::string{TRACK_DICTIONARY_PATH}, std::ios::binary);
    if(!input.is_open()) {

        return false;

    }

    std::string header = ifstream_read_file_header(input);
    if(header != std::string(FILE_HEADER)) {

        return false;

    }

    std::uint16_t total_count {0};
    std::uint8_t count {0};
    std::string title {""}, curr_artist {""}, album {""}, path {""};
    std::vector<std::string> artists {};
    char c {'\0'};
    while(total_count < 65500) {

        c = input.get();
        total_count++;

        if(input.fail()) {

            if(input.eof()) {

                break;

            }

            return false;

        }

        //Handle artists-reading separately from title, album, and path reading since there can be multiple artists.
        if(count != 1) {

            if(c == '\0') {

                count++;
                total_count = 0;

            } else {

                switch(count) {
            
                case 0:
                    title.push_back(c);
                    break;
                case 2:
                    album.push_back(c);
                    break;
                case 3:
                    path.push_back(c);
                    break;
                default:
                    count = 0;
                    track_dict.insert({Track(title, artists, album), path});
                    title = curr_artist = album = path = "";
                    artists.clear();
                    break;

                }

            }

        } else {

            if(c == '\0' && curr_artist.empty()) {

                //Start reading the album if we got two null chars in a row
                count++;
                total_count = 0;

            } else if(c == '\0') {

                //Read the next artist if we received a null char
                artists.push_back(curr_artist);
                curr_artist = "";

            } else {

                curr_artist.push_back(c);

            }

        }

    }

    //The reading was unsuccessful if the file ends early in the middle of a track-path pair
    if(count != 4) {

        return false;

    }

    return true;

}

bool TrackManager::serialize_track_dict() {

    std::ofstream output(std::string{TRACK_DICTIONARY_PATH}, std::ios::binary);
    if(!output.is_open()) {

        return false;

    }

    output.write(std::string(FILE_HEADER).c_str(), FILE_HEADER.size());
    output.put('\0');

    for(const std::pair<Track, std::string>& pair: track_dict) {

        output.write(pair.first.title().c_str(), pair.first.title().size());
        output.put('\0');
        for(const std::string& artist: pair.first.artists()) {

            output.write(artist.c_str(), artist.size());
            output.put('\0');

        }
        output.put('\0');
        output.write(pair.first.album().c_str(), pair.first.album().size());
        output.put('\0');
        output.write(pair.second.c_str(), pair.second.size());
        output.put('\0');

        if(output.fail()) {

            return false;

        }

    }

    output.flush();
    if(output.fail()) {

        return false;

    }

    output.close();
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

    std::unordered_set<std::filesystem::path> paths_in_dict {};
    for(const std::pair<Track, std::string>& pair: track_dict) {

        paths_in_dict.insert(std::filesystem::path(pair.second));

    }

    std::vector<std::string> extraneous_track_files {};
    for(std::filesystem::directory_entry file: std::filesystem::directory_iterator(TRACK_FILES_DIRECTORY)) {

        //Ignore directories, non-audio files, and corrupted audio files

        if(file.is_directory() || !path_is_readable_track_file(file.path().string())) {

            continue;

        }

        if(!paths_in_dict.contains(file.path())) {

            extraneous_track_files.push_back(file.path().string());

        }

    }

    return extraneous_track_files;

}

std::vector<std::tuple<std::string, std::string, std::string>> TrackManager::get_basic_playlist_info_from_files() {

    std::vector<std::tuple<std::string, std::string, std::string>> info {};

    for(std::filesystem::directory_entry file: std::filesystem::directory_iterator(PLAYLIST_FILES_DIRECTORY)) {

        //We only want the .sply files
        if(file.path().extension().string() != std::string{PLAYLIST_FILE_EXTENSION}) {

            continue;

        }

        std::string id = file.path().stem().string();
        
        std::ifstream input(file.path(), std::ios::binary);
        if(!input.is_open()) {

            continue;

        }

        //Read file header
        std::string header = ifstream_read_file_header(input);
        if(header != std::string(FILE_HEADER)) {

            continue;

        }

        std::uint16_t total_count {0};
        std::uint8_t count {0};
        std::string name, cover_image_file_path {""};
        char c = '\0';
        while(total_count < 65500) {

            c = input.get();
            total_count++;

            if(input.fail()) {

                if(input.eof()) {

                    break;

                }

                break;

            }

            if(count == 0) {

                name.push_back(c);

            } else if(count == 1) {

                cover_image_file_path.push_back(c);

            }

            if(c == '\0') {

                if(count == 0) {

                    count++;
                    
                } else {

                    break;

                }

            }

        }

        if(count != 1) {

            continue;

        }

        info.push_back({id, name, cover_image_file_path});
        
    }

    return info;

}

Playlist TrackManager::get_playlist(const std::string& id) {

    std::filesystem::path playlist_files_directory_path = PLAYLIST_FILES_DIRECTORY / std::filesystem::path(id + std::string{PLAYLIST_FILE_EXTENSION});
    std::ifstream input (playlist_files_directory_path, std::ios::binary);
    if(!input.is_open()) {

        return Playlist();

    }

    //Read file header
    std::string header = ifstream_read_file_header(input);
    if(header != std::string(FILE_HEADER)) {

        return Playlist();

    }

    //Read basic playlist details
    std::uint16_t total_char_count {0};
    std::uint8_t count {0};
    std::string name, online_connection {""};
    char c = '\0';
    while(total_char_count < 65500) {

        c = input.get();
        total_char_count++;

        if(input.fail()) {

            if(input.eof()) {

                break;

            }

            return Playlist();

        }

        if(count == 0) {

            name.push_back(c);

        } else if(count == 1) {

            online_connection.push_back(c);

        }

        if(c == '\0') {

            if(count == 0) {

                count++;
                
            } else {

                break;

            }

        }

    }

    //Reading unsuccessful if the file ends early
    if(count != 1) {

        return Playlist();

    }

    std::unordered_multiset<Track> tracklist {};
    std::string title {""}, curr_artist {""}, album {""};
    std::vector<std::string> artists {};
    count = 0;
    while(total_char_count < 65500) {

        c = input.get();
        total_char_count++;

        if(input.fail()) {

            if(input.eof()) {

                break;

            }

            return Playlist();

        }

        if(count != 1) {

            if(c == '\0') {

                count++;

            } else {

                switch(count) {

                case 0:
                    title.push_back(c);
                    break;
                case 2:
                    album.push_back(c);
                    break;
                default:
                    break;

                }

            }

            if(count > 2) {

                count = 0;
                tracklist.insert(Track(title, artists, album));
                title = curr_artist = album = "";
                artists.clear();

            }

        } else {

            if(c == '\0' && curr_artist.empty()) {

                count++;

            } else if(c == '\0') {

                artists.push_back(curr_artist);
                curr_artist = "";

            } else {

                curr_artist.push_back(c);

            }

        }

    }

    //Reading unsuccessful if it ends early
    if(count != 0) {

        return Playlist();

    }

    return Playlist(name, tracklist, online_connection);

}

bool TrackManager::serialize_playlist(const std::string& id, const Playlist& playlist) {

    std::filesystem::path playlist_files_directory_path = PLAYLIST_FILES_DIRECTORY / std::filesystem::path(id + std::string{PLAYLIST_FILE_EXTENSION});
    std::ofstream output (playlist_files_directory_path, std::ios::binary);
    if(!output.is_open()) {

        return false;

    }

    output.write(std::string(FILE_HEADER).c_str(), FILE_HEADER.size());
    output.put('\0');
    output.write(playlist.name.c_str(), playlist.name.size());
    output.put('\0');
    output.write(playlist.get_online_connection().c_str(), playlist.get_online_connection().size());
    output.put('\0');
    std::unordered_multiset<Track> tracklist = playlist.get_tracklist();
    for(const Track& track: tracklist) {

        output.write(track.title().c_str(), track.title().size());
        output.put('\0');
        for(const std::string& artist: track.artists()) {

            output.write(artist.c_str(), artist.size());
            output.put('\0');

        }
        output.put('\0');
        output.write(track.album().c_str(), track.album().size());
        output.put('\0');

        if(output.fail()) {

            return false;

        }

    }

    output.flush();
    if(output.fail()) {

        return false;

    }

    output.close();
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

    std::vector<std::tuple<std::string, std::string, std::string>> playlists_info = get_basic_playlist_info_from_files();
    if(playlists_info.size() == 0) {

        std::cout << "[no playlists available]" << std::endl;

    } else {

        for(std::tuple<std::string, std::string, std::string> info: playlists_info) {

            std::cout << std::get<0>(info) << " " << std::get<1>(info) << " " << std::get<2>(info) << std::endl;

        }

    }

}
