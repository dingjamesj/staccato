#ifndef TRACK_HPP
#define TRACK_HPP

#include <string>
#include <iostream>

namespace staccato {

    /** Immutable struct that contains metadata for a track (title, artists, album) */
    struct Track {

    public:
        const std::string title;
        const std::string artists;
        const std::string album;

        Track(std::string title, std::string artists, std::string album);

        /** For debugging purposes */
        void print() const;

        bool is_empty() const;

        bool operator==(const Track& other) const;

    };

}

namespace std {

    template<> struct hash<staccato::Track> {

        inline std::size_t operator()(const staccato::Track& track) const {

            return std::hash<std::string>()(track.title + " " + track.artists + " " + track.album);

        }

    };

}

#endif