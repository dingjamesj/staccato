#include "util.hpp"

#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <iostream>

using namespace staccato;

int main(int argc, char* argv[]) {

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
            std::cout << "Closing python" << std::endl;
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