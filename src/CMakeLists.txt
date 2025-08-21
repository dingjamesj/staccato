cmake_minimum_required(VERSION 4.0.3)

project(staccato)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

include_directories(headers)

add_executable(staccato 
    main/main.cpp
    main/file_manager.cpp
    headers/file_manager.h
)