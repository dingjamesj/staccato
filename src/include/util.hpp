#ifndef UTIL_HPP
#define UTIL_HPP

// =========================================================================================
// 
//                SET TO TRUE DURING DEVELOPMENT, SET TO FALSE WHEN DEPLOYED.             
//   USED TO ADJUST FOR THE DIFFERENCE BETWEEN DEVELOPMENT AND DEPLOYED PROJECT STRUCTURE.
// 
// =========================================================================================

#define DEVELOPMENT_BUILD true

// =========================================================================================

#include <string>
#include <format>
#include <filesystem>
#include <vector>
#include <Python.h>

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
    std::vector<std::string> tokenize_comma_separated_string(const std::string& str);
    bool init_python();

}

#endif