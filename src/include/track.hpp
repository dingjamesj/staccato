#ifndef TRACK_HPP
#define TRACK_HPP

#include <string>
#include <iostream>
#include <unicode/unistr.h>
#include <unicode/ustream.h>

namespace staccato {

    enum class sortmode;

    /** Struct that contains metadata for a track (title, artists, album) */
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
        /** Returns -1 if track1 < track2, 0 if track1 == track2, and 1 if track1 > track2 */
        static int compare(const Track& track1, const Track& track2, sortmode sort_mode);

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