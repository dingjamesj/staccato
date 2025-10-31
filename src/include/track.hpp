#ifndef TRACK_HPP
#define TRACK_HPP

#include <string>
#include <iostream>
#include <vector>
#include <unicode/unistr.h>
#include <unicode/ustream.h>

namespace staccato {

    enum class sortmode;

    /// @brief A container for basic information about a track: its title, artists, and album (or "release group" e.g. EP, single release, etc.)
    struct Track {

    private:
        std::string title_;
        std::vector<std::string> artists_;
        std::string album_;

    public:
        /// @return The track title
        const std::string& title() const;
        /// @return The track's artists
        const std::vector<std::string>& artists() const;
        /// @return The track's "release group" (e.g. album, EP, single release, etc.)
        const std::string& album() const;

        /// @brief Creates a Track object
        /// @param title 
        /// @param artists 
        /// @param album 
        Track(const std::string& title, const std::vector<std::string>& artists, const std::string& album);

        /// @brief Creates an empty Track object. Encountering an empty Track object should signify that an error occurred.
        Track();

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

        const std::vector<std::string>& artists = track.artists();
        std::string artists_str {};
        for(std::size_t i {0}; i < artists.size(); i++) {

            artists_str += artists[i] + " ";

        }

        return std::hash<string>()(track.title() + " " + artists_str + track.album());

    }

};

#endif