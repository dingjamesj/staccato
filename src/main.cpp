#include "app_manager.hpp"
#include "util.hpp"
#include "qml_interface.hpp"
#include "audio_file_image_provider.hpp"

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

    QGuiApplication app (argc, argv);
    QQmlApplicationEngine engine;

    StaccatoInterface cpp_to_qt_interface;
    engine.rootContext()->setContextProperty("Staccato", &cpp_to_qt_interface);
    engine.addImageProvider(QLatin1String("audioFile"), new AudioFileImageProvider);

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

}