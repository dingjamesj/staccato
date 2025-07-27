cmake_minimum_required(VERSION 4.0.3)

project(staccato)

include_directories(headers)

add_executable(staccato 
    main/main.cpp
    main/file_manager.cpp
    headers/file_manager.h
)