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

void staccato::redirect_python_sys() {

    #if(DEVELOPMENT_BUILD)
    PyRun_SimpleString("print(\"DEVELOPMENT BUILD\")");
    PyRun_SimpleString("import sys");
    PyRun_SimpleString("sys.path.append(f\"{sys.executable}/../../scripts\")");
    PyRun_SimpleString("print(sys.path)");
    PyRun_SimpleString("print(sys.executable)");
    #else
    PyRun_SimpleString("print(\"NON-DEVELOPMENT BUILD\")");
    PyRun_SimpleString("import sys");
    PyRun_SimpleString("sys.path.append(f\"{sys.executable}/../scripts\")");
    PyRun_SimpleString("print(sys.path)");
    PyRun_SimpleString("print(sys.executable)");
    #endif

}

bool staccato::init_python() {

    PyConfig config;
    PyConfig_InitIsolatedConfig(&config);
    std::wstring build_path {std::filesystem::current_path().wstring()};

    //Make the interpreter see the .py files

    std::wstring relative_path_to_scripts;
    #if(DEVELOPMENT_BUILD)
    relative_path_to_scripts = L"/../scripts";
    #else
    relative_path_to_scripts = L"/scripts";
    #endif

    // config.module_search_paths_set = 0;
    // PyWideStringList_Append(&config.module_search_paths, (build_path + relative_path_to_scripts).c_str());

//     PyRun_SimpleString("import sysconfig; import sys; sys._debugmallocstats = True");
// PyRun_SimpleString("import sys; print('sys.prefix =', sys.prefix)");
// PyRun_SimpleString("print('sys.executable =', sys.executable)");
// PyRun_SimpleString("import pprint; pprint.pprint(sys.path)");


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
    return !(bool) PyStatus_Exception(status);

}