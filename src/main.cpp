#include "app_manager.hpp"
#include "util.hpp"
#include "track_manager.hpp"
#include "qml_interface.hpp"
#include "audio_file_image_provider.hpp"
#include "track.hpp"
#include "playlist.hpp"

#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <iostream>

using namespace staccato;

int main(int argc, char* argv[]) {

    /*
    AppManager::read_settings();

    bool init_success = init_python();
    if(!init_success) {

        return 2;

    }
    
    QGuiApplication app (argc, argv);
    QQmlApplicationEngine engine;

    engine.addImageProvider(QLatin1String("audiofile"), new AudioFileImageProvider);

    QObject::connect(
        QCoreApplication::instance(),
        &QCoreApplication::aboutToQuit,
        []() {
            AppManager::serialize_settings();
            Py_FinalizeEx();
        }
    );

    QObject::connect(
        &engine, 
        &QQmlApplicationEngine::objectCreationFailed, 
        &app, >
        []() {
            QCoreApplication::exit(-1);
        },
        Qt::QueuedConnection
    );
    
    engine.loadFromModule("staccato", "Main");

    return app.exec();

    */

    Playlist playlist1 ("playlist1", {Track("tack1", {"asd", "a"}, "asdiojads"), Track("", {}, ""), Track("asoid", {}, "")}, "a");
    Playlist playlist2 ("playlist2", {Track("tack1", {"asd", "a"}, "asdiojads"), Track("", {}, ""), Track("asoid", {}, "")}, "");

    std::cout << playlist1.string() << std::endl;
    std::cout << playlist2.string() << std::endl;

    std::cout << (TrackManager::serialize_playlist("playlist1id", playlist1) ? "serialization of playlist 1 successfull" : "serialization of playlist 1 failure") << std::endl;
    std::cout << (TrackManager::serialize_playlist("playlist2id", playlist2) ? "serialization of playlist 2 successfull" : "serialization of playlist 2 failure") << std::endl;

    bool error_flag;
    Playlist copyPlaylist1 = TrackManager::get_playlist("playlist1id", error_flag);
    if(error_flag) {

        std::cout << "error in reading playlist 1" << std::endl;

    } else {

        std::cout << copyPlaylist1.string() << std::endl;

    }

    Playlist copyPlaylist2 = TrackManager::get_playlist("playlist2id", error_flag);
    if(error_flag) {

        std::cout << "error in reading playlist 2" << std::endl;

    } else {

        std::cout << copyPlaylist2.string() << std::endl;

    }
    
}