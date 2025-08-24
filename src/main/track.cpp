#include "track.hpp"
#include "util.hpp"
#include <iostream>
#include <taglib/fileref.h>
#include <filesystem>

using namespace staccato;

Track::Track(std::string path, std::string youtube_url, int duration, std::string title, std::string artists, std::string album): 
    path{path}, 
    youtube_url{youtube_url}, 
    duration{duration},
    title{title}, 
    artists{artists}, 
    album{album} 
{}

void Track::print() const {

    std::cout << path << std::endl;
    if(!youtube_url.empty()) {

        std::cout << youtube_url << std::endl;

    }
    std::cout << seconds_to_hms(duration) << std::endl << title << std::endl << artists << std::endl << album << std::endl;

}

bool Track::operator==(const Track& other) const {

    return path == other.path;

}

bool Track::file_exists() const {

    return std::filesystem::is_regular_file(path);

}

bool Track::delete_file() {

    if(!std::filesystem::is_regular_file(path)) {

        return false;

    }

    return std::filesystem::remove(path);

}

Track staccato::import_track(std::string path) {

    TagLib::FileRef file_ref(path.c_str());
    if(file_ref.isNull()) {

        return Track("", "", 0, "--", "--", "--");

    }

    TagLib::Tag* tag = file_ref.tag();
    TagLib::AudioProperties* audio_properties = file_ref.audioProperties();
    return Track(path, "", audio_properties->lengthInSeconds(), tag->title().to8Bit(true), tag->artist().to8Bit(true), tag->album().to8Bit(true));

}