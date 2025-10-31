cmake_minimum_required(VERSION 3.16)
project(staccato)
set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(CMAKE_PREFIX_PATH C:/Qt/6.9.1/mingw_64)
find_package(Qt6 REQUIRED COMPONENTS Quick)
qt_standard_project_setup(REQUIRES 6.5)

file(GLOB SOURCES "src/main/*.cpp")
qt_add_executable(staccato ${SOURCES})
target_include_directories(staccato PRIVATE "src/include")
target_include_directories(staccato PRIVATE "src/python")

target_include_directories(staccato PRIVATE "C:/msys64/ucrt64/include/taglib")
target_link_directories(staccato PRIVATE "C:/msys64/ucrt64/lib")
target_link_libraries(staccato PRIVATE tag)

find_package(ICU REQUIRED COMPONENTS uc i18n)
target_link_libraries(staccato PRIVATE ICU::uc ICU::i18n)

find_package(Python3 COMPONENTS Interpreter Development REQUIRED)
target_link_libraries(staccato PRIVATE Python3::Python)

qt_add_qml_module(staccato
    URI staccatogui
    QML_FILES
    RESOURCES
)

target_link_libraries(staccato PRIVATE Qt6::Quick)