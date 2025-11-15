#include "track_manager.hpp"
#include "interface_to_qt.hpp"

using namespace staccato;

QList<QVariantList> TrackManagerInterface::get_pinned_items() {
    
    std::vector<std::tuple<bool, std::string, std::vector<std::string>, std::string>> pinned_items = TrackManager::get_pinned_items();

    QList<QVariantList> qt_pinned_items {};
    for(const std::tuple<bool, std::string, std::vector<std::string>, std::string>& item: pinned_items) {

        QStringList properties {};
        for(const std::string& property: std::get<2>(item)) {

            properties.push_back(QString::fromStdString(property));

        }
        
        qt_pinned_items.push_back({QVariant(std::get<0>(item)), QVariant(QString::fromStdString(std::get<1>(item))), QVariant(properties), QVariant(QString::fromStdString(std::get<3>(item)))});

    }

    return qt_pinned_items;

}

QList<QStringList> TrackManagerInterface::get_basic_playlists_info() {

    std::vector<std::tuple<std::string, std::string, std::string>> playlists = TrackManager::get_basic_playlist_info_from_files();

    QList<QStringList> qt_playlists {};
    for(std::tuple<std::string, std::string, std::string> playlist: playlists) {

        qt_playlists.push_back({QString::fromStdString(std::get<0>(playlist)), QString::fromStdString(std::get<1>(playlist)), QString::fromStdString(std::get<2>(playlist))});

    }

    return qt_playlists;

}

