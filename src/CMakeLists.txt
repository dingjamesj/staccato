cmake_minimum_required(VERSION 4.0.3)
project(staccato)
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

file(GLOB SOURCES "main/*.cpp")
add_executable(staccato ${SOURCES})
target_include_directories(staccato PRIVATE include)

#
# REPLACE "C:\\Users\\James\\vcpkg\\packages\\taglib_x64-windows\\share\\taglib" WITH
# WHERE YOUR INSTALLATION OF TAGLIB IS
#
set(TagLib_DIR "C:\\Users\\James\\vcpkg\\packages\\taglib_x64-windows\\share\\taglib")
#

find_package(TagLib 2.0.2 REQUIRED)
target_link_libraries(staccato PRIVATE TagLib::TagLib)