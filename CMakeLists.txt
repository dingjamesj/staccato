cmake_minimum_required(VERSION 3.16)
project(staccato)
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

file(GLOB SOURCES "src/main/*.cpp")
add_executable(staccato ${SOURCES})
target_include_directories(staccato PRIVATE "src/include")

target_include_directories(staccato PRIVATE "C:/msys64/ucrt64/include/taglib")
target_link_directories(staccato PRIVATE "C:/msys64/ucrt64/lib")
target_link_libraries(staccato PRIVATE tag)

find_package(Python3 COMPONENTS Interpreter Development REQUIRED)
target_link_libraries(staccato PRIVATE Python3::Python)