#include "track.hpp"
#include "util.hpp"
#include "track_manager.hpp"

using namespace staccato;

Track::Track(const std::string& title, const std::vector<std::string>& artists, const std::string& album): 
    title {title}, 
    artists {artists}, 
    album {album} 
{}

Track::Track(): title {""}, artists {}, album {""} {}

bool Track::is_empty() const {

    return title.empty() && artists.empty() && album.empty();

}

std::string Track::string() const {

    if(is_empty()) {

        return "";

    }

    std::string artists_str {};
    for(std::size_t i {0}; i < artists.size(); i++) {

        artists_str += artists[i];
        if(i < artists.size() - 1) {

            artists_str += ", ";

        }

    }

    return title + " by " + artists_str + " from " + album;
    
}

int Track::compare(const Track& track1, const Track& track2, sortmode sort_mode) {

    switch(sort_mode) {

    case sortmode::title: {
        icu::UnicodeString title1 = icu::UnicodeString::fromUTF8(track1.title).toLower();
        icu::UnicodeString title2 = icu::UnicodeString::fromUTF8(track2.title).toLower();
        if(title1 < title2) {

            return -1;

        } else if(title1 == title2) {

            return 0;

        } else {

            return 1;

        }
    }
    case sortmode::artists: {
        if(track1.artists.size() == 0 && track2.artists.size() == 0) {

            return 0;

        } else if(track1.artists.size() == 0) {

            return -1;

        } else if(track2.artists.size() == 0) {

            return 1;

        }

        icu::UnicodeString artists1 = icu::UnicodeString::fromUTF8(track1.artists[0]).toLower();
        icu::UnicodeString artists2 = icu::UnicodeString::fromUTF8(track2.artists[0]).toLower();
        if(artists1 < artists2) {

            return -1;

        } else if(artists1 == artists2) {

            return 0;

        } else {

            return 1;

        }
    }
    case sortmode::album: {
        icu::UnicodeString album1 = icu::UnicodeString::fromUTF8(track1.album).toLower();
        icu::UnicodeString album2 = icu::UnicodeString::fromUTF8(track2.album).toLower();
        if(album1 < album2) {

            return -1;

        } else if(album1 == album2) {

            return 0;

        } else {

            return 1;

        }
    }
    case sortmode::duration: {
        int duration1 = TrackManager::get_track_duration(track1);
        int duration2 = TrackManager::get_track_duration(track2);
        if(duration1 < duration2) {

            return -1;

        } else if(duration1 == duration2) {

            return 0;

        } else {

            return 1;

        }
    }
    case sortmode::bitrate: {
        int bitrate1 = TrackManager::get_track_bitrate(track1);
        int bitrate2 = TrackManager::get_track_bitrate(track2);
        if(bitrate1 < bitrate2) {

            return -1;

        } else if(bitrate1 == bitrate2) {

            return 0;

        } else {

            return 1;

        }
    }
    case sortmode::file_ext: {
        icu::UnicodeString ext1 = icu::UnicodeString::fromUTF8(TrackManager::get_track_file_ext(track1)).toLower();
        icu::UnicodeString ext2 = icu::UnicodeString::fromUTF8(TrackManager::get_track_file_ext(track2)).toLower();
        if(ext1 < ext2) {

            return -1;

        } else if(ext1 == ext2) {

            return 0;

        } else {

            return 1;

        }
    }
    default:
        return true;

    }

}

bool Track::operator==(const Track& other) const {

    return title == other.title && artists == other.artists && album == other.album;

}

std::ostream& operator<<(std::ostream& os, const Track& track) {

    os << track.string();
    return os;

}