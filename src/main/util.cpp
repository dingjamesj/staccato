#include "util.hpp"
#include <iostream>

std::string staccato::seconds_to_hms(int seconds) {

    int hours = seconds / 3600;
    int minutes = (seconds % 3600) / 60;
    seconds = seconds % 60;
    return std::format("{:02}:{:02}:{:02}", hours, minutes, seconds);

}

void staccato::trim_string(std::string& str) {

    while(str.length() > 0 && std::isspace(str.at(0))) {

        str.erase(0);

    }

    while(str.length() > 0 && std::isspace(str.length() - 1)) {

        str.erase(str.length() - 1);

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

    list.push_back(str.substr(begin_index));

    return list;

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