#ifndef UTIL_HPP
#define UTIL_HPP
#define DEVELOPMENT_BUILD true

#include <string>
#include <format>
#include <Python.h>
#include <filesystem>

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
    /** Used for the C++ program to find the python scripts */
    void redirect_python_sys();
    bool init_python();

}

#endif