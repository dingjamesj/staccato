#include "track_manager.hpp"

using namespace staccato;

std::unordered_map<Track, std::string> TrackManager::track_dict = {};

//====================
//  HELPER FUNCTIONS
//====================

std::filesystem::path TrackManager::get_unique_filename(std::filesystem::path path) {

    bool path_already_exists = std::filesystem::exists(path);
    std::size_t count = {1};
    while(path_already_exists) {
    
        if(count > 10000) {

            //We hard stop the loop if count reaches 10000 (no way there's over 10000 files with the same name)
            return std::filesystem::path("");

        }

        path.replace_filename(std::format("{} ({}){}", path.stem().string(), count, path.extension().string()));

    }

    return path;

}

bool TrackManager::write_file_metadata(const std::string& path, const Track& track) {

    TagLib::FileRef file_ref(path.c_str());

    if(track.title.empty()) {

        file_ref.tag()->setTitle("Unknown Title");

    } else {

        file_ref.tag()->setTitle(track.title);

    }

    if(track.artists.empty()) {

        file_ref.tag()->setArtist("Unknown Artists");

    } else {

        file_ref.tag()->setArtist(track.artists);

    }

    if(track.album.empty()) {

        file_ref.tag()->setAlbum("Unknown Album");

    } else {

        file_ref.tag()->setAlbum(track.album);

    }

    return file_ref.save();

}

urltype TrackManager::get_url_type(const std::string& url) {

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

Track TrackManager::get_local_track_info(const std::string& path) {

    if(!path_is_readable_track_file(path)) {

        return Track();

    }

    TagLib::FileRef file_ref(path.c_str());

    std::string title = file_ref.tag()->title().to8Bit();
    std::string artist = file_ref.tag()->artist().to8Bit();
    std::string album = file_ref.tag()->album().to8Bit();

    if(title.empty()) {

        title = "Unknown Title";

    }
    if(artist.empty()) {

        artist = "Unknown Artists";

    }
    if(album.empty()) {

        album = "Unknown Album";

    }

    return Track(title, artist, album);

}

Track TrackManager::get_online_track_info(const std::string& url) {
    
    Py_Initialize();
    PyObject* py_fetcher = PyUnicode_DecodeFSDefault("fetcher");
    PyObject* py_module = PyImport_Import(py_fetcher);
    Py_DECREF(py_fetcher);
    if(py_module == nullptr) {

        Py_Finalize();
        return Track();

    }

    urltype url_type = get_url_type(url);
    PyObject* py_func = nullptr;
    if(url_type == urltype::spotify) {

        py_func = PyObject_GetAttrString(py_module, "get_spotify_track");

    } else if(url_type == urltype::youtube) {

        py_func = PyObject_GetAttrString(py_module, "get_youtube_track");

    }
    Py_DECREF(py_module);
    //Check if the python function was found
    if(py_func == nullptr || !PyCallable_Check(py_func)) {

        Py_XDECREF(py_func);
        Py_Finalize();
        return Track();

    }

    PyObject* py_param = PyTuple_Pack(1, url.c_str());
    PyObject* py_return = PyObject_CallObject(py_func, py_param);
    Py_DECREF(py_func);
    Py_DECREF(py_param);
    if(py_return == nullptr || !PyDict_Check(py_return)) {

        Py_XDECREF(py_return);
        Py_Finalize();
        return Track();

    }

    Track track (
        PyUnicode_AsUTF8(PyDict_GetItemString(py_return, "title")),
        PyUnicode_AsUTF8(PyDict_GetItemString(py_return, "artists")),
        PyUnicode_AsUTF8(PyDict_GetItemString(py_return, "album"))
    );
    Py_DECREF(py_return);
    Py_Finalize();
    return track;

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
    bool copy_success = std::filesystem::copy_file(source_path, destination_path, std::filesystem::copy_options::skip_existing);

    if(!copy_success) {

        return false;

    }

    std::string new_track_path = destination_path.string();
    track_dict.insert({track, new_track_path});
    return write_file_metadata(new_track_path, track);
    
}

bool TrackManager::download_track(const Track& track, const std::string& youtube_url, bool force_mp3) {

    if(track_dict.contains(track)) {

        //(we return true because we only return false if the import was unsuccessful,
        // since an import didn't happen, then the import vacuously was successful)
        return true;

    }

    Py_Initialize();
    PyObject* py_downloader = PyUnicode_DecodeFSDefault("downloader");
    PyObject* py_module = PyImport_Import(py_downloader);
    Py_DECREF(py_downloader);
    if(py_module == nullptr) {

        Py_Finalize();
        return false;

    }

    PyObject* py_func = PyObject_GetAttrString(py_module, "download_youtube_track");
    Py_DECREF(py_module);
    if(py_func == nullptr || !PyCallable_Check(py_func)) {

        Py_XDECREF(py_func);
        Py_Finalize();
        return false;

    }

    PyObject* py_param = PyTuple_Pack(3, youtube_url, TRACK_FILES_DIRECTORY, force_mp3);
    PyObject* py_return = PyObject_CallObject(py_func, py_param);
    Py_DECREF(py_func);
    Py_DECREF(py_param);
    if(py_return == nullptr || !PyUnicode_Check(py_return)) {

        Py_XDECREF(py_return);
        Py_Finalize();
        return false;

    }

    std::string downloaded_path {PyUnicode_AsUTF8(py_return)};
    Py_DECREF(py_return);
    Py_Finalize();
    track_dict.insert({track, downloaded_path});
    return write_file_metadata(downloaded_path, track);

}

bool TrackManager::download_track(const Track& track, bool force_mp3) {

    if(track_dict.contains(track)) {

        //(we return true because we only return false if the import was unsuccessful,
        // since an import didn't happen, then the import vacuously was successful)
        return true;

    }

    //Find the best YouTube URL and then download

    Py_Initialize();
    PyObject* py_fetcher = PyUnicode_DecodeFSDefault("fetcher");
    PyObject* py_module = PyImport_Import(py_fetcher);
    Py_DECREF(py_fetcher);
    if(py_module == nullptr) {

        Py_Finalize();
        return false;

    }

    PyObject* py_func = PyObject_GetAttrString(py_module, "find_best_youtube_url");
    Py_DECREF(py_module);
    if(py_func == nullptr || !PyCallable_Check(py_func)) {

        Py_XDECREF(py_func);
        Py_Finalize();
        return false;

    }

    PyObject* py_param = PyTuple_Pack(2, track.title, track.artists);
    PyObject* py_return = PyObject_CallObject(py_func, py_param);
    Py_DECREF(py_func);
    Py_DECREF(py_param);
    if(py_return == nullptr || !PyUnicode_Check(py_return)) {

        Py_XDECREF(py_return);
        Py_Finalize();
        return false;

    }

    std::string youtube_url {PyUnicode_AsUTF8(py_return)};
    Py_DECREF(py_return);
    Py_Finalize();
    return download_track(track, youtube_url, force_mp3);

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

    return write_file_metadata(track_dict.at(original_track), new_track);

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

std::string TrackManager::get_track_file_ext(const Track& track) {

    if(!track_dict.contains(track)) {

        return "";

    }

    std::filesystem::path path = track_dict.at(track);
    return path.extension().string();

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
    if(file_ext != ".jpg" && file_ext != ".png" && file_ext != ".jpeg") {

        return false;

    }

    std::ifstream input_stream(artwork_file, std::ios::binary);
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

bool TrackManager::read_track_dict_from_file() {

    track_dict.clear();

    std::ifstream input(std::string{TRACK_DICTIONARY_PATH}, std::ios::binary);
    if(!input.is_open()) {

        return false;

    }

    std::uint8_t count {0};
    std::string title, artists, album, path {""};
    char c = '\0';
    while(!input.eof()) {

        c = input.get();

        if(input.fail()) {

            return false;

        }

        switch(count) {

            case 0:
                title.push_back(c);
                break;
            case 1:
                artists.push_back(c);
                break;
            case 2:
                album.push_back(c);
                break;
            case 3:
                path.push_back(c);
                break;
            default:
                break;

        }

        if(c == '\0') {

            count++;

        }

        if(count == 4) {

            count = 0;
            track_dict.insert({Track(title, artists, album), path});
            title, artists, album, path = "";

        }

    }

    //The reading was unsuccessful if the file ends early in the middle of a track-path pair
    if(count != 0) {

        return false;

    }

    return true;

}

bool TrackManager::write_track_dict_to_file() {

    std::ofstream output(std::string{TRACK_DICTIONARY_PATH}, std::ios::binary);
    if(!output.is_open()) {

        return false;

    }

    for(const std::pair<Track, std::string>& pair: track_dict) {

        output.write(pair.first.title.c_str(), pair.first.title.size());
        output.write(pair.first.artists.c_str(), pair.first.artists.size());
        output.write(pair.first.album.c_str(), pair.first.album.size());
        output.write(pair.second.c_str(), pair.second.size());

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

        //Ignore directories and non-audio files

        if(file.is_directory()) {

            continue;

        }

        TagLib::FileRef file_ref(file.path().c_str());
        if(file_ref.isNull()) {

            continue;

        }

        if(!paths_in_dict.contains(file.path())) {

            extraneous_track_files.push_back(file.path().string());

        }

    }

    return extraneous_track_files;

}

std::vector<std::tuple<std::string, std::string, std::string>> TrackManager::read_basic_playlist_info_from_files() {

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

        std::uint8_t count {0};
        std::string name, cover_image_file_path {""};
        char c = '\0';
        while(!input.eof()) {

            c = input.get();

            if(input.fail()) {

                break;

            }

            if(count == 0) {

                name.push_back(c);

            } else if(count == 1) {

                cover_image_file_path.push_back(c);

            }

            if(c == '\0') {

                count++;

            }

        }

        //The reading was unsuccessful if the file ends early
        if(count != 2) {

            continue;

        }

        info.push_back({id, name, cover_image_file_path});
        
    }

    return info;

}

Playlist TrackManager::read_playlist_from_file(const std::string& id) {

    std::ifstream input(std::string{PLAYLIST_FILES_DIRECTORY} + id + std::string{PLAYLIST_FILE_EXTENSION});
    if(!input.is_open()) {

        return Playlist();

    }

    //Read basic playlist details
    std::uint8_t count {0};
    std::string name, cover_image_file_path, online_connection {""};
    char c = '\0';
    while(!input.eof()) {

        c = input.get();

        if(input.fail()) {

            return Playlist();

        }

        if(count == 0) {

            name.push_back(c);

        } else if(count == 1) {

            cover_image_file_path.push_back(c);

        } else if(count == 2) {

            online_connection.push_back(c);

        }

        if(c == '\0') {

            count++;

        }

    }

    //Reading unsuccessful if the file ends early
    if(count != 3) {

        return Playlist();

    }

    std::unordered_multiset<Track> tracklist {};
    std::string title, artists, album {""};
    count = 0;
    while(!input.eof()) {

        c = input.get();

        if(count == 0) {

            title.push_back(c);

        } else if(count == 1) {

            artists.push_back(c);

        } else if(count == 2) {

            album.push_back(c);

        }

        if(c == '\0') {

            count++;

        }

        if(count == 3) {

            count = 0;
            tracklist.insert(Track(title, artists, album));
            title, artists, album = "";

        }

    }

    //Reading unsuccessful if it ends early
    if(count != 0) {

        return Playlist();

    }

    return Playlist(name, cover_image_file_path, tracklist, online_connection);

}

bool TrackManager::write_playlist_to_file(const std::string& id, const Playlist& playlist) {

    std::ofstream output(std::string{PLAYLIST_FILES_DIRECTORY} + id + std::string{PLAYLIST_FILE_EXTENSION}, std::ios::binary);
    if(!output.is_open()) {

        return false;

    }

    output.write(playlist.name.c_str(), playlist.name.size());
    output.write(playlist.cover_image_file_path.c_str(), playlist.cover_image_file_path.size());
    output.write(playlist.online_connection.c_str(), playlist.online_connection.size());
    std::unordered_multiset<Track> tracklist = playlist.get_tracklist();
    for(Track track: tracklist) {

        output.write(track.title.c_str(), track.title.size());
        output.write(track.artists.c_str(), track.artists.size());
        output.write(track.album.c_str(), track.album.size());

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

    int count = 0;
    for(std::pair<const staccato::Track, std::string> key_value: track_dict) {

        std::cout << "K: " << key_value.first.title + " " + key_value.first.artists + " " + key_value.first.album << std::endl << "V: " << key_value.second << std::endl;

        if(count < track_dict.size() - 1) {

            std::cout << std::endl;

        }

        count++;

    }

}
