#include "app_manager.hpp"
#include "util.hpp"
#include "track_manager.hpp"
#include "qml_interface.hpp"
#include "audio_file_image_provider.hpp"
#include "track.hpp"
#include "playlist.hpp"
#include "playlist_tree.hpp"

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
        &app, 
        []() {
            QCoreApplication::exit(-1);
        },
        Qt::QueuedConnection
    );
    
    engine.loadFromModule("staccato", "Main");

    return app.exec();
    */

    PlaylistTree tree;
    std::cout << tree.string() << std::endl;

    std::cout << "\nIterator testing: " << std::endl;
    for(PlaylistTree::ConstIterator iter = tree.cbegin(); iter != tree.cend(); ++iter) {

        if(iter.operator->() == nullptr) {

            std::cout << "nullptr" << std::endl;

        } else {

            std::cout << *iter << std::endl;

        }

    }
    std::cout << std::endl;
    for(PlaylistTree::Iterator iter = tree.begin(); iter != tree.end(); ++iter) {

        if(iter.operator->() == nullptr) {

            std::cout << "nullptr" << std::endl;

        } else {

            std::cout << *iter << std::endl;

        }

    }

    std::cout << (tree.add_playlist("1", {}) ? "true" : "false") << std::endl;
    std::cout << (tree.add_playlist("2", {}) ? "true" : "false") << std::endl;
    std::cout << (tree.add_folder("folder", {}) ? "true" : "false") << std::endl;
    std::cout << (tree.add_playlist("3", {"folder"}) ? "true" : "false") << std::endl;
    std::cout << (tree.add_playlist("4", {"folder"}) ? "true" : "false") << std::endl;
    std::cout << (tree.add_playlist("5", {}) ? "true" : "false") << std::endl;
    std::cout << (tree.add_folder("singular folder", {}) ? "true" : "false") << std::endl;
    std::cout << (tree.add_folder("nested folder", {"folder"}) ? "true" : "false") << std::endl;
    std::cout << (tree.add_playlist("6", {"folder", "nested folder"}) ? "true" : "false") << std::endl;
    std::cout << (tree.add_playlist("7", {"folder", "nested folder"}) ? "true" : "false") << std::endl;
    std::cout << (tree.add_playlist("8", {"nested folder"}) ? "true" : "false") << std::endl;
    std::cout << (tree.add_playlist("8", {"folder"}) ? "true" : "false") << std::endl;
    std::cout << tree.string() << std::endl;

    std::cout << "\nIterator testing: " << std::endl;
    for(PlaylistTree::ConstIterator iter = tree.cbegin(); iter != tree.cend(); ++iter) {

        if(iter.operator->() == nullptr) {

            std::cout << "nullptr" << std::endl;

        } else {

            std::cout << *iter << std::endl;

        }

    }
    std::cout << std::endl;
    for(PlaylistTree::Iterator iter = tree.begin(); iter != tree.end(); ++iter) {

        if(iter.operator->() == nullptr) {

            std::cout << "nullptr" << std::endl;

        } else {

            std::cout << *iter << std::endl;

        }

    }

    

    std::vector<std::string> deleted_playlists = tree.remove_folder("singular folder", {});
    std::cout << '[';
    if(deleted_playlists.size() > 0) {

        std::cout << deleted_playlists[0];

    }
    for(std::size_t i {0}; i < deleted_playlists.size(); i++) {

        std::cout << ", " << deleted_playlists[i];

    }
    std::cout << ']' << std::endl;

    std::cout << (tree.remove_playlist("6", {"folder", "nested folder"}) ? "true" : "false") << std::endl;

    deleted_playlists = tree.remove_folder("folder", {});
    std::cout << '[';
    if(deleted_playlists.size() > 0) {

        std::cout << deleted_playlists[0];

    }
    for(std::size_t i {1}; i < deleted_playlists.size(); i++) {

        std::cout << ", " << deleted_playlists[i];

    }
    std::cout << ']' << std::endl;
    std::cout << tree.string() << std::endl;
    
    std::cout << "\nIterator testing: " << std::endl;
    for(PlaylistTree::ConstIterator iter = tree.cbegin(); iter != tree.cend(); ++iter) {

        if(iter.operator->() == nullptr) {

            std::cout << "nullptr" << std::endl;

        } else {

            std::cout << *iter << std::endl;

        }

    }
    std::cout << std::endl;
    for(PlaylistTree::Iterator iter = tree.begin(); iter != tree.end(); ++iter) {

        if(iter.operator->() == nullptr) {

            std::cout << "nullptr" << std::endl;

        } else {

            std::cout << *iter << std::endl;

        }

    }

}