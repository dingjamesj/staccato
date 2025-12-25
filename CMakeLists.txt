cmake_minimum_required(VERSION 3.16)

project(staccato VERSION 0.1 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_STANDARD 23)
set(QT_QML_GENERATE_QMLLS_INI ON)
set(QT_QML_GENERATE_QMLTYPES ON)

find_package(Qt6 REQUIRED COMPONENTS Quick)
find_package(Qt6 REQUIRED COMPONENTS Svg)
find_package(ICU REQUIRED COMPONENTS uc i18n)
find_package(Python3 COMPONENTS Interpreter Development REQUIRED)

qt_standard_project_setup(REQUIRES 6.8)
file(GLOB SOURCES "src/*.cpp")
qt_add_executable(staccato ${SOURCES})

qt_policy(SET QTP0004 NEW)
qt_add_qml_module(staccato
    URI staccato
    VERSION 0.1
    QML_FILES
        src/ui/Main.qml
        src/ui/TracklistPanel.qml
        src/ui/CurrentlyPlayingPanel.qml
        src/ui/QueuePanel.qml
        src/ui/ControlBarPanel.qml
        src/ui/HomePanel.qml
        src/ui/homePanel.js
        src/ui/ItemButtonPanel.qml
        src/ui/RoundedImage.qml
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
    SOURCES
        src/qml_interface.cpp
        src/include/qml_interface.hpp
    RESOURCE_PREFIX /
)

# Qt for iOS sets MACOSX_BUNDLE_GUI_IDENTIFIER automatically since Qt 6.1.
# If you are developing for iOS or macOS you should consider setting an
# explicit, fixed bundle identifier manually though.
set_target_properties(staccato PROPERTIES
#    MACOSX_BUNDLE_GUI_IDENTIFIER com.example.staccato
    MACOSX_BUNDLE_BUNDLE_VERSION ${PROJECT_VERSION}
    MACOSX_BUNDLE_SHORT_VERSION_STRING ${PROJECT_VERSION_MAJOR}.${PROJECT_VERSION_MINOR}
    MACOSX_BUNDLE TRUE
    # WIN32_EXECUTABLE TRUE
)

target_include_directories(staccato PRIVATE "src/include")
target_include_directories(staccato PRIVATE "C:/msys64/ucrt64/include/taglib")
target_link_directories(staccato PRIVATE "C:/msys64/ucrt64/lib")
target_link_libraries(staccato PRIVATE
    Qt6::Quick
    Qt6::Svg
    tag
    ICU::uc ICU::i18n
    Python3::Python
)

include(GNUInstallDirs)
install(TARGETS staccato
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)
