#ifndef TRACK_HPP
#define TRACK_HPP

#include <string>
#include <iostream>
#include <vector>
#include <unicode/unistr.h>
#include <unicode/ustream.h>
#include <QObject>
#include <QtQml>

namespace staccato {

    enum class sortmode;

    /// @brief A container for basic information about a track: its title, artists, and album (or "release group" e.g. EP, single release, etc.)
    class Track: public QObject {

        Q_OBJECT
        QML_ELEMENT
        Q_PROPERTY(QString title READ title)
        Q_PROPERTY(QVector<QString> artists READ artists)
        Q_PROPERTY(QString album READ album)

    private:
        QString title_;
        QVector<QString> artists_;
        QString album_;

    public:
        /// @return The track title
        const QString& title() const;
        /// @return The track's artists
        const QVector<QString>& artists() const;
        /// @return The track's "release group" (e.g. album, EP, single release, etc.)
        const QString& album() const;

        /// @brief Creates a Track object
        /// @param title 
        /// @param artists 
        /// @param album 
        explicit Track(const QString& title, const QVector<QString>& artists, const QString& album, QObject* parent = nullptr);

        /// @brief Creates an empty Track object. Encountering an empty Track object should signify that an error occurred.
        explicit Track(QObject* parent = nullptr);

        /// @brief Used to see if an error was encountered (empty Track objects should signify that an error occurred)
        /// @return `true` if this Track object is empty, `false` otherwise
        bool is_empty() const;
        
        /// @return A string representation of this Track object
        std::string string() const;

        /// @brief Compares two tracks based on an attribute. Note that some attributes require usage of TrackManager.
        /// @param track1 
        /// @param track2 
        /// @param sort_mode The attribute to compare the tracks to (e.g. track title, duration, bitrate)
        /// @return -1 if track1 < track2, 0 if track1 == track2, and 1 if track1 > track2
        static int compare(const Track& track1, const Track& track2, sortmode sort_mode);

        /// @param other 
        /// @return `true` if the other Track's title, artists, and album are equal to this Track's, `false` otherwise
        bool operator==(const Track& other) const;

    };

}

//For debugging purposes
std::ostream& operator<<(std::ostream& os, const staccato::Track& track);

//To make hash-using collections work
template<> struct std::hash<staccato::Track> {

    inline std::size_t operator()(const staccato::Track& track) const {

        const QVector<QString>& artists = track.artists();
        QString artists_str ("");
        for(std::size_t i {0}; i < artists.size(); i++) {

            artists_str += artists[i] + " ";

        }

        return qHash(track.title() + " " + artists_str + track.album());

    }

};

#endif