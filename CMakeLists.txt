cmake_minimum_required(VERSION 3.16)

project(staccato VERSION 0.1 LANGUAGES CXX)

# We use C++ 23
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_STANDARD 23)
set(QT_QML_GENERATE_QMLLS_INI ON)
set(QT_QML_GENERATE_QMLTYPES ON)

# We require Qt Quick, Unicode (i.e. ICU), Python's C API, and Taglib.
find_package(Qt6 REQUIRED COMPONENTS Quick)
find_package(Qt6 REQUIRED COMPONENTS Svg)
find_package(ICU REQUIRED COMPONENTS uc i18n)
find_package(Python3 COMPONENTS Interpreter Development REQUIRED)
find_package(TagLib REQUIRED)
find_path(TaglibIncludeFolder tag.h REQUIRED PATH_SUFFIXES taglib)

# Create the executable
qt_standard_project_setup(REQUIRES 6.8)
file(GLOB SOURCES "src/*.cpp")
qt_add_executable(staccatoapp ${SOURCES})

# Include our include folder AND Taglib's include folder (Taglib requires this)
target_include_directories(staccatoapp PRIVATE 
    "src/include"
    ${TaglibIncludeFolder}
)

# Add Qt files
qt_policy(SET QTP0004 NEW)
qt_add_qml_module(staccatoapp
    URI staccato
    VERSION 0.1
    QML_FILES
        src/ui/Main.qml
        src/ui/main.js
        src/ui/TracklistPanel.qml
        src/ui/CurrentlyPlayingPanel.qml
        src/ui/QueuePanel.qml
        src/ui/ControlBarPanel.qml
        src/ui/HomePanel.qml
        src/ui/homePanel.js
        src/ui/ArtworkTextButton.qml
        src/ui/RoundedImage.qml
        src/ui/RoundButton.qml
        src/ui/HomePanelArtistsTextField.qml
    RESOURCES
        src/ui/resources/piggo58.jpg
        src/ui/resources/placeholder.jpg
        src/ui/resources/Inter-VariableFont_opsz,wght.ttf
        src/ui/resources/Inter-Italic-VariableFont_opsz,wght.ttf
        src/ui/resources/play.svg
        src/ui/resources/rewind.svg
        src/ui/resources/skip.svg
        src/ui/resources/shuffle.svg
        src/ui/resources/loop.svg
        src/ui/resources/pause.svg
        src/ui/resources/list.svg
        src/ui/resources/grid.svg
        src/ui/resources/plus.svg
        src/ui/resources/zoom.svg
        src/ui/resources/minus.svg
        src/ui/resources/chevrondown.svg
    SOURCES
        src/qml_interface.cpp
        src/include/qml_interface.hpp
    RESOURCE_PREFIX /
)

target_link_libraries(staccatoapp PRIVATE
    Qt6::Quick
    Qt6::Svg
    TagLib::TagLib
    ICU::uc ICU::i18n
    Python3::Python
)

include(GNUInstallDirs)
install(TARGETS staccatoapp
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)
