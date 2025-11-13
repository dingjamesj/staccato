#include "track_manager.hpp"
#include "util.hpp"

using namespace staccato;

const QString& Track::title() const {

    return title_;

}

const QVector<QString>& Track::artists() const {

    return artists_;

}

const QString& Track::album() const {

    return album_;

}

Track::Track(const QString& title, const QVector<QString>& artists, const QString& album, QObject* parent): 
    title_ (title.isEmpty() ? "Unknown Title" : title), 
    artists_ {}, 
    album_ (album.isEmpty() ? "Unknown Album" : album),
    QObject(parent)
{

    if(artists.size() == 0) {

        artists_ = {"Unknown Artists"};
        return;

    }

    for(const QString& artist: artists) {

        if(artist.isEmpty()) {

            artists_.push_back("Unknown Artist");

        } else {

            artists_.push_back(artist);

        }

    }

}

Track::Track(QObject* parent): title_ (""), artists_ {}, album_ (""), QObject(parent) {}

bool Track::is_empty() const {

    return title_.isEmpty() && artists_.isEmpty() && album_.isEmpty();

}

std::string Track::string() const {

    if(is_empty()) {

        return "[empty]\n";

    }

    QString artists_str {};
    for(std::size_t i {0}; i < artists_.size(); i++) {

        artists_str += artists_[i];
        if(i < artists_.size() - 1) {

            artists_str += ", ";

        }

    }

    return (title_ + " by " + artists_str + " from " + album_ + "\n").toStdString();
    
}

int Track::compare(const Track& track1, const Track& track2, sortmode sort_mode) {

    switch(sort_mode) {

    case sortmode::title: {
        QString title1 = track1.title_.toLower();
        QString title2 = track2.title_.toLower();
        
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

        QString artists1 = track1.artists_[0].toLower();
        QString artists2 = track2.artists_[0].toLower();
        if(artists1 < artists2) {

            return -1;

        } else if(artists1 == artists2) {

            return 0;

        } else {

            return 1;

        }
    }
    case sortmode::album: {
        QString album1 = track1.album_.toLower();
        QString album2 = track2.album_.toLower();
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
    case sortmode::file_codec: {
        QString ext1 = QString::fromStdString(audio_type_to_string(TrackManager::get_track_file_type(track1))).toLower();
        QString ext2 = QString::fromStdString(audio_type_to_string(TrackManager::get_track_file_type(track2))).toLower();
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