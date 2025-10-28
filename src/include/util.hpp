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
#include <unordered_map>
#include <Python.h>

namespace staccato {

    enum class urltype {spotify, youtube, unknown};

    enum class sortmode {
        title,
        artists,
        album,
        bitrate,
        duration,
        file_codec
    };

    enum class audiotype {m4a, mp3, opus, vorbis, wav, flac, unsupported};

    extern std::vector<std::string> comma_containing_phrases;

    std::string seconds_to_hms(int seconds);
    void trim_string(std::string& str);
    std::vector<std::string> tokenize_comma_separated_string(const std::string& str);
    std::vector<std::string> get_artists_vector_from_str(const std::string& str);
    std::string audio_type_to_string(const audiotype& audio_type);
    bool init_python();

}

//For debugging purposes
std::ostream& operator<<(std::ostream& os, const staccato::audiotype& audio_type);

#endif