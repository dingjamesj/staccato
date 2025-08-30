#ifndef UTIL_HPP
#define UTIL_HPP

#include <string>
#include <format>

namespace staccato {

    enum class URLType {SPOTIFY, YOUTUBE, UNKNOWN};

    enum class SortMode {
        TITLE,
        ARTISTS,
        ALBUM,
        BITRATE,
        DURATION,
        FILE_EXT
    };

    std::string seconds_to_hms(int seconds);
    void trim_string(std::string& str);

}

#endif