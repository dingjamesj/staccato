#ifndef TRACK_HPP
#define TRACK_HPP

#include <string>
#include <iostream>

namespace staccato {

    /** Immutable struct that contains metadata for a track (title, artists, album) */
    struct Track {

    public:
        std::string title;
        std::string artists;
        std::string album;

        Track(std::string title, std::string artists, std::string album);
        Track();

        /** Returns if this Track contains no metadata (if all strings are empty) */
        bool is_empty() const;
        /** Returns a string representation of this Track */
        std::string string() const;

        bool operator==(const Track& other) const;

    };

}

//For debugging purposes
std::ostream& operator<<(std::ostream& os, const staccato::Track& track);

//To make hash-using collections work
template<> struct std::hash<staccato::Track> {

    inline std::size_t operator()(const staccato::Track& track) const {

        return std::hash<string>()(track.title + " " + track.artists + " " + track.album);

    }

};

#endif