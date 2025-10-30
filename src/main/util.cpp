#include "util.hpp"
#include <iostream>

std::vector<std::string> staccato::comma_containing_phrases {"Tyler, The Creator"};

std::string staccato::seconds_to_hms(int seconds) {

    int hours = seconds / 3600;
    int minutes = (seconds % 3600) / 60;
    seconds = seconds % 60;
    return std::format("{:02}:{:02}:{:02}", hours, minutes, seconds);

}

void staccato::trim_string(std::string& str) {

    while(str.length() > 0 && std::isspace(str.at(0))) {

        str.erase(0, 1);

    }

    while(str.length() > 0 && std::isspace(str.at(str.length() - 1))) {

        str.erase(str.length() - 1, 1);

    }

}

std::vector<std::string> staccato::tokenize_comma_separated_string(const std::string& str) {

    std::vector<std::string> list {};
    std::size_t begin_index {0};
    std::size_t index_of_comma {str.find(',', begin_index)};
    std::string curr_token {};
    while(index_of_comma != -1) {

        curr_token = str.substr(begin_index, index_of_comma - begin_index);
        trim_string(curr_token);
        list.push_back(curr_token);
        begin_index = index_of_comma + 1;
        index_of_comma = str.find(',', begin_index);

    }

    curr_token = str.substr(begin_index);
    trim_string(curr_token);
    list.push_back(curr_token);

    return list;

}

std::vector<std::string> staccato::get_artists_vector_from_str(const std::string& str) {

    std::vector<std::string> list = tokenize_comma_separated_string(str);
    for(std::string phrase: comma_containing_phrases) {

        std::vector<std::string> tokenized_phrase = tokenize_comma_separated_string(phrase);
        if(tokenized_phrase.size() == 0) {

            continue;

        }

        std::size_t target_token_index {0};
        for(std::size_t i {0}; i < list.size(); i++) {

            if(list[i] == tokenized_phrase[target_token_index]) {

                target_token_index++;

                if(target_token_index >= tokenized_phrase.size()) {

                    target_token_index = 0;
                    i -= tokenized_phrase.size() - 1;
                    list[i] = phrase;
                    for(std::size_t j {1}; j < tokenized_phrase.size(); j++) {

                        list.erase(list.begin() + i + 1);

                    }

                }

            }

        }

    }

    return list;

}

std::string staccato::audio_type_to_string(const staccato::audiotype& audio_type) {

    switch(audio_type) {

    case staccato::audiotype::m4a:
        return "m4a";
    case staccato::audiotype::mp3:
        return "mp3";
    case staccato::audiotype::opus:
        return "opus";
    case staccato::audiotype::vorbis:
        return "vorbis";
    case staccato::audiotype::wav:
        return "wav";
    case staccato::audiotype::flac:
        return "flac";
    case staccato::audiotype::unsupported:
        return "[unsupported file type]";
    default:
        return "[YOU FORGOT TO INCLUDE THIS AUDIO TYPE IN THE OPERATOR << METHOD]";

    }

}

staccato::urltype staccato::get_url_type(const std::string& url) {

    if(url.find("spotify.com") != std::string::npos) {

        return urltype::spotify;

    }

    if(url.find("youtube.com") != std::string::npos || url.find("youtu.be") != std::string::npos) {

        return urltype::youtube;

    }

    return urltype::unknown;

}
    
bool staccato::init_python() {

    PyConfig config;
    PyConfig_InitIsolatedConfig(&config);
    std::wstring build_path {std::filesystem::current_path().wstring()};

    //Make the interpreter see the virtual environment

    std::wstring relative_path_to_venv;
    #if(DEVELOPMENT_BUILD)
    relative_path_to_venv = L"/../scripts/.venv";
    #else
    relative_path_to_venv = L"/scripts/.venv";
    #endif

    std::wstring python_exe_path_in_venv;
    #if defined(_WIN32) || defined(_WIN64)
    python_exe_path_in_venv = L"/Scripts/python.exe";
    #else
    python_exe_path_in_venv = L"/bin/python";
    #endif

    PyConfig_SetString(&config, &config.executable, (build_path + relative_path_to_venv + python_exe_path_in_venv).c_str());

    PyStatus status = Py_InitializeFromConfig(&config);
    PyConfig_Clear(&config);

    //Make the interpreter see the .py files

    std::string relative_path_to_scripts;
    #if(DEVELOPMENT_BUILD)
    relative_path_to_scripts = "../scripts";
    #else
    relative_path_to_scripts = "./scripts";
    #endif

    PyRun_SimpleString(("import sys; sys.path.append('" + relative_path_to_scripts + "')").c_str());

    return !(bool) PyStatus_Exception(status);

}

std::ostream& operator<<(std::ostream& os, const staccato::audiotype& audio_type) {

    os << audio_type_to_string(audio_type);
    return os;

}