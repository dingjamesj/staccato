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

//===========================
//  PUBLIC ACCESS FUNCTIONS
//===========================

Track TrackManager::get_local_track_info(const std::string& path) {

    if(!path_is_readable_track_file(path)) {

        return Track("", "", "");

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

std::string TrackManager::get_sply_file_name(const std::string& id, const std::string& playlist_name) {

    return std::string(id) + std::string(" ") + std::string(playlist_name.substr(0, 40));

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
