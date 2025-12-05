#include "track_manager.hpp"
#include "app_manager.hpp"
#include "interface_to_qt.hpp"

using namespace staccato;

QList<QVariantList> StaccatoInterface::get_pinned_items() {
    
    std::vector<std::tuple<bool, std::string, std::vector<std::string>, std::string>> pinned_items = AppManager::get_pinned_items();

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

QList<QStringList> StaccatoInterface::get_basic_playlists_info() {

    std::vector<std::tuple<std::string, std::string, std::string, std::uint64_t>> playlists = TrackManager::get_basic_playlist_info_from_files();

    QList<QStringList> qt_playlists {};
    for(std::tuple<std::string, std::string, std::string, std::uint64_t> playlist: playlists) {

        qt_playlists.push_back({
            QString::fromStdString(std::get<0>(playlist)), 
            QString::fromStdString(std::get<1>(playlist)), 
            QString::fromStdString(std::get<2>(playlist)), 
            QString::fromStdString(std::to_string(std::get<3>(playlist)))
        });

    }

    return qt_playlists;

}

QVariantList StaccatoInterface::read_last_session_data() {

    std::string main_queue_playlist_id {""};
    std::uint64_t main_position {0}, added_position {0};

    if(AppManager::read_last_session_data(main_queue_playlist_id, main_position, added_position)) {

        return {QVariant(QString::fromStdString(main_queue_playlist_id)), QVariant(main_position), QVariant(added_position)};

    }

    return {QVariant(QString::fromStdString("")), QVariant(0), QVariant(0)};

}

QList<QVariantList> StaccatoInterface::get_main_queue() {

    QList<QVariantList> tracklist {};
    for(const Track& track: AppManager::get_main_queue()) {

        QStringList artists {};
        for(const std::string& artist_std_str: track.artists()) {

            artists.append(QString::fromStdString(artist_std_str));

        }

        tracklist.append({QVariant(QString::fromStdString(track.title())), QVariant(artists), QVariant(QString::fromStdString(track.album()))});

    }

    return tracklist;

}

QList<QVariantList> StaccatoInterface::get_added_queue() {

    QList<QVariantList> tracklist {};
    for(const Track& track: AppManager::get_added_queue()) {

        QStringList artists {};
        for(const std::string& artist_std_str: track.artists()) {

            artists.append(QString::fromStdString(artist_std_str));

        }

        tracklist.append({QVariant(QString::fromStdString(track.title())), QVariant(artists), QVariant(QString::fromStdString(track.album()))});

    }

    return tracklist;

}