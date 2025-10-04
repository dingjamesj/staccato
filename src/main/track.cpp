#include "track.hpp"
#include "util.hpp"
#include "track_manager.hpp"

using namespace staccato;

const std::string& Track::title() const {

    return title_;

}

const std::vector<std::string>& Track::artists() const {

    return artists_;

}

const std::string& Track::album() const {

    return album();

}

Track::Track(const std::string& title, const std::vector<std::string>& artists, const std::string& album): 
    title_ {title.empty() ? "Unknown Title" : title}, 
    artists_ {}, 
    album_ {album.empty() ? "Unknown Album" : album} 
{

    if(artists.size() == 0) {

        Track::artists_ = {"Unknown Artists"};
        return;

    }

    for(std::string artist: artists) {

        if(artist.empty()) {

            Track::artists_.push_back("Unknown Artist");

        } else {

            Track::artists_.push_back(artist);

        }

    }

}

Track::Track(): title_ {""}, artists_ {}, album_ {""} {}

bool Track::is_empty() const {

    return title_.empty() && artists_.empty() && album_.empty();

}

std::string Track::string() const {

    if(is_empty()) {

        return "[empty]\n";

    }

    std::string artists_str {};
    for(std::size_t i {0}; i < artists_.size(); i++) {

        artists_str += artists_[i];
        if(i < artists_.size() - 1) {

            artists_str += ", ";

        }

    }

    return title_ + " by " + artists_str + " from " + album_ + "\n";
    
}

int Track::compare(const Track& track1, const Track& track2, sortmode sort_mode) {

    switch(sort_mode) {

    case sortmode::title: {
        icu::UnicodeString title1 = icu::UnicodeString::fromUTF8(track1.title_).toLower();
        icu::UnicodeString title2 = icu::UnicodeString::fromUTF8(track2.title_).toLower();
        if(title1 < title2) {

            return -1;

        } else if(title1 == title2) {

            return 0;

        } else {

            return 1;

        }
    }
    case sortmode::artists: {
        if(track1.artists_.size() == 0 && track2.artists_.size() == 0) {

            return 0;

        } else if(track1.artists_.size() == 0) {

            return -1;

        } else if(track2.artists_.size() == 0) {

            return 1;

        }

        icu::UnicodeString artists1 = icu::UnicodeString::fromUTF8(track1.artists_[0]).toLower();
        icu::UnicodeString artists2 = icu::UnicodeString::fromUTF8(track2.artists_[0]).toLower();
        if(artists1 < artists2) {

            return -1;

        } else if(artists1 == artists2) {

            return 0;

        } else {

            return 1;

        }
    }
    case sortmode::album: {
        icu::UnicodeString album1 = icu::UnicodeString::fromUTF8(track1.album_).toLower();
        icu::UnicodeString album2 = icu::UnicodeString::fromUTF8(track2.album_).toLower();
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

    return title_ == other.title_ && artists_ == other.artists_ && album_ == other.album_;

}

std::ostream& operator<<(std::ostream& os, const Track& track) {

    os << track.string();
    return os;

}