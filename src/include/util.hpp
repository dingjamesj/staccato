#ifndef UTIL_HPP
#define UTIL_HPP

#include <string>
#include <format>

namespace staccato {

    enum class URLType {SPOTIFY, YOUTUBE, UNKNOWN};

    enum class SortMode {
        TITLE_ASCENDING, TITLE_DESCENDING, 
        ARTISTS_ASCENDING, ARTISTS_DESCENDING,
        ALBUM_ASCENDING, ALBUM_DESCENDING,
        DURATION_ASCENDING, DURATION_DESCENDING,
        BITRATE_ASCENDING, BITRATE_DESCENDING,
        FILE_EXT_ASCENDING, FILE_EXT_DESCENDING
    };

    std::string seconds_to_hms(int seconds);

}

#endif