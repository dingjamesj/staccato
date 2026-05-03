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
find_package(nlohmann_json 3.12.0 REQUIRED)

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
set_source_files_properties(
    src/ui/Style.qml
    src/ui/FilePicker.qml
    PROPERTIES QT_QML_SINGLETON_TYPE TRUE
)
qt_add_qml_module(staccatoapp
    URI staccato
    VERSION 0.1
    QML_FILES
        src/ui/Main.qml
        src/ui/TracklistPanel.qml
        src/ui/CurrentlyPlayingPanel.qml
        src/ui/ControlPanel.qml
        src/ui/Playbar.qml
        src/ui/HomePanel.qml
        src/ui/ArtworkTextButton.qml
        src/ui/LibraryPanel.qml
        src/ui/Style.qml
        src/ui/MetadataImportPanel.qml
        src/ui/URLImportPanel.qml
        src/ui/HelpToolTip.qml
        src/ui/FilePicker.qml
        src/ui/TrackInfoEditor.qml
        src/ui/RoundImage.qml
        src/ui/RoundButton.qml
        src/ui/RoundTextField.qml
        src/ui/RoundTabButton.qml
        src/ui/RoundCheckBox.qml
        src/ui/RoundRadioButton.qml
        src/ui/trackImporter.js
        src/ui/main.js
    RESOURCES
        src/ui/resources/piggo58.jpg
        src/ui/resources/placeholder.jpg
        src/ui/resources/Inter-VariableFont_opsz,wght.ttf
        src/ui/resources/CascadiaCode-VariableFont_wght.ttf
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
    nlohmann_json::nlohmann_json
)

include(GNUInstallDirs)
install(TARGETS staccatoapp
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)
