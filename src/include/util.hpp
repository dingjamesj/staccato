#ifndef UTIL_HPP
#define UTIL_HPP

#include <string>
#include <format>

namespace staccato {

    enum class urltype {spotify, youtube, unknown};

    enum class sortmode {
        title,
        artists,
        album,
        bitrate,
        duration,
        file_ext
    };

    std::string seconds_to_hms(int seconds);
    void trim_string(std::string& str);

}

#endif