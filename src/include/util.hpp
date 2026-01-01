#ifndef UTIL_HPP
#define UTIL_HPP

// =========================================================================================
// 
//                SET TO TRUE DURING DEVELOPMENT, SET TO FALSE WHEN DEPLOYED.             
//   USED TO ADJUST FOR THE DIFFERENCE BETWEEN DEVELOPMENT AND DEPLOYED PROJECT STRUCTURE.

#define DEVELOPMENT_BUILD true

// =========================================================================================

#include <string>
#include <format>
#include <filesystem>
#include <vector>
#include <unordered_map>
#include <iostream>
#include <fstream>
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

    /// @brief This is for artists whose names have commas in them (first one I think of is "Tyler, The Creator"). Used in get_artists_vector_from_str()
    extern std::vector<std::string> comma_containing_phrases;

    /// @param seconds 
    /// @return An hours:minutes:seconds representation of param `seconds` 
    std::string seconds_to_hms(int seconds);

    /// @brief Removes leading and trailing whitespace from param `str`
    /// @param str 
    void trim_string(std::string& str);

    /// @brief Does NOT use the `comma_containing_phrases` field (i.e. will tokenize "Tyler, The Creator" into ["Tyler", "The Creator"])
    /// @param str 
    /// @return A vector of strings from a comma separated list of strings
    std::vector<std::string> tokenize_comma_separated_string(const std::string& str);

    /// @brief Uses `comma_containing_phrases` (i.e. won't tokenize "Tyler, The Creator" into two strings)
    /// @param str 
    /// @return A vector of strings from a comma separated list of artist names
    std::vector<std::string> get_artists_vector_from_str(const std::string& str);

    /// @param audio_type 
    /// @return A string representation of an audiotype
    std::string audio_type_to_string(const audiotype& audio_type);

    /// @param url 
    /// @return The streaming service that is param `url`
    urltype get_url_type(const std::string& url);

    /// @brief Initializes python, SHOULD ONLY BE CALLED ONCE AT THE BEGINNING OF THE ENTIRE PROGRAM
    /// @return 
    bool init_python();

    /// @brief *(Calls python function "downloader.update_libraries")* Updates python libraries (e.g. pip, yt-dlp)
    /// @return The status of the update (failure, success, already up to date) as a string, empty string if a python error occurred
    std::string update_python_libraries();

    /// @brief Reads a uint64 given a binary file input stream
    /// @param input 
    /// @param input_failed_flag A pass-by-reference boolean
    /// @return The uint64 that was read. If the reading failed, then `input_failed_flag` will be `true` and this will return 0
    std::uint64_t read_next_uint64(std::ifstream& input, bool& input_failed_flag);

    /// @brief .stkl and .sply files all have the same file header that marks it as a staccato file. The header is a constexpr string that's defined somewhere else in this file.
    /// @param input 
    /// @return The file header from the staccato file
    std::string ifstream_read_file_header(std::ifstream& input);

}

//For debugging purposes
std::ostream& operator<<(std::ostream& os, const staccato::audiotype& audio_type);

#endif