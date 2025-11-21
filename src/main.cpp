#include "track_manager.hpp"

#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <iostream>

using namespace staccato;

int main(int argc, char* argv[]) {

    /*

    bool init_success = init_python();
    if(!init_success) {

        return 2;

    }

    QGuiApplication app (argc, argv);
    QQmlApplicationEngine engine;

    QObject::connect(
        QCoreApplication::instance(),
        &QCoreApplication::aboutToQuit,
        []() {
            Py_FinalizeEx();
        }
    );

    QObject::connect(
        &engine, 
        &QQmlApplicationEngine::objectCreationFailed, 
        &app, 
        []() {
            QCoreApplication::exit(-1);
        },
        Qt::QueuedConnection
    );
    
    engine.loadFromModule("staccato", "Main");

    return app.exec();

    */

    bool init_success = init_python();
    if(!init_success) {

        return 2;

    }

    std::string playlist_id {"playlistid1239i21093"};
    Playlist playlist ("playlist name", {Track("IRIS OUT", {"Kenshi Yonezu"}, "IRIS OUT"), Track("OMG", {"NewJeans"}, "OMG"), Track("CRAZY", {"LE SSERAFIM"}, "CRAZY")}, "https://open.spotify.com/playlist/6fyhWb9ap9110qkAuJCvrf?si=ae8a66563cd74349");
    bool success = TrackManager::serialize_playlist(playlist_id, playlist);
    std::cout << (success ? "true" : "false") << std::endl; 
    std::vector<std::tuple<std::string, std::string, std::string, uint64_t>> basic_playlist_info = TrackManager::get_basic_playlist_info_from_files();
    for(std::tuple<std::string, std::string, std::string, std::uint64_t> playlist_info: basic_playlist_info) {

        std::cout << std::get<0>(playlist_info) << " " << std::get<1>(playlist_info) << " " << std::get<2>(playlist_info) << " " << std::get<3>(playlist_info) << std::endl;

    }

    Playlist returned_playlist = TrackManager::get_playlist(playlist_id);
    std::cout << returned_playlist.string();

    Py_FinalizeEx();

}