#include "app_manager.hpp"
#include "util.hpp"
#include "track_manager.hpp"
#include "qml_interface.hpp"
#include "audio_file_image_provider.hpp"
#include "tests.hpp"
#include "track.hpp"

#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <iostream>

using namespace staccato;

int main(int argc, char* argv[]) {

    AppManager::read_settings();

    bool init_success = init_python();
    if(!init_success) {

        return 2;

    }
    
    // /*

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
        &app, 
        []() {
            QCoreApplication::exit(-1);
        },
        Qt::QueuedConnection
    );
    
    engine.loadFromModule("staccato", "Main");

    return app.exec(); 
    
    // */

    /*

    std::cout << TrackManager::get_local_track_info("D:\\ss5msvokUkY.mp3").string();

    return 0;

    */

}