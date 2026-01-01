#include "track_manager.hpp"
#include "app_manager.hpp"
#include "track.hpp"
#include "qml_interface.hpp"

using namespace staccato;

void StaccatoInterface::sort_pinned_items_alphabetically(QList<QVariantList>& qt_pinned_items, qsizetype begin, qsizetype end) {

    if(end - begin <= 1) {

        return;

    }

    qsizetype mid = (begin + end) / 2;

    sort_pinned_items_alphabetically(qt_pinned_items, begin, mid);
    sort_pinned_items_alphabetically(qt_pinned_items, mid, end);

    //Merge:

    QList<QVariantList> list_left {};
    QList<QVariantList> list_right {};

    for(qsizetype i {0}; i < mid - begin; i++) {

        list_left.append(qt_pinned_items[i + begin]);

    }

    for(qsizetype i {0}; i < end - mid; i++) {

        list_right.append(qt_pinned_items[i + mid]);

    }

    qsizetype l {0}, r {0}, i {begin};
    while(l < list_left.size() && r < list_right.size()) {

        int compare = QString::compare(list_left[l][1].value<QString>(), list_right[r][1].value<QString>(), Qt::CaseInsensitive);
        if(compare < 0) {

            qt_pinned_items[i] = list_left[l];
            l++;

        } else {

            qt_pinned_items[i] = list_right[r];
            r++;

        }
        
        i++;

    }

    while(l < list_left.size()) {

        qt_pinned_items[i] = list_left[l];
        l++;
        i++;

    }

    while(r < list_right.size()) {

        qt_pinned_items[i] = list_right[r];
        r++;
        i++;

    }

}

void StaccatoInterface::sort_playlists_alphabetically(QList<QStringList>& qt_playlists, qsizetype begin, qsizetype end) {

    if(end - begin <= 1) {

        return;

    }

    qsizetype mid = (begin + end) / 2;

    sort_playlists_alphabetically(qt_playlists, begin, mid);
    sort_playlists_alphabetically(qt_playlists, mid, end);

    //Merge:

    QList<QStringList> list_left {};
    QList<QStringList> list_right {};

    for(qsizetype i {0}; i < mid - begin; i++) {

        list_left.append(qt_playlists[i + begin]);

    }

    for(qsizetype i {0}; i < end - mid; i++) {

        list_right.append(qt_playlists[i + mid]);

    }

    qsizetype l {0}, r {0}, i {begin};
    while(l < list_left.size() && r < list_right.size()) {

        int compare = QString::compare(list_left[l][1], list_right[r][1], Qt::CaseInsensitive);
        if(compare < 0) {

            qt_playlists[i] = list_left[l];
            l++;

        } else {

            qt_playlists[i] = list_right[r];
            r++;

        }
        
        i++;

    }

    while(l < list_left.size()) {

        qt_playlists[i] = list_left[l];
        l++;
        i++;

    }

    while(r < list_right.size()) {

        qt_playlists[i] = list_right[r];
        r++;
        i++;

    }

}

void StaccatoInterface::readSettings() {

    AppManager::read_settings();

}

QList<QVariantList> StaccatoInterface::getPinnedItems(const QString& sortMode) {
    
    std::vector<std::tuple<bool, std::string, std::vector<std::string>, std::string>> pinned_items = AppManager::get_pinned_items();

    QList<QVariantList> qt_pinned_items {};
    for(const std::tuple<bool, std::string, std::vector<std::string>, std::string>& item: pinned_items) {

        QStringList properties {};
        for(const std::string& property: std::get<2>(item)) {

            properties.push_back(QString::fromStdString(property));

        }
        
        qt_pinned_items.push_back({
            QVariant(std::get<0>(item)),
            QVariant(QString::fromStdString(std::get<1>(item))),
            QVariant(properties),
            QVariant(QString::fromStdString(std::get<3>(item)))
        });

    }

    if(sortMode == "ALPHA") {

        sort_pinned_items_alphabetically(qt_pinned_items, 0, qt_pinned_items.size());

    }

    return qt_pinned_items;

}

QList<QStringList> StaccatoInterface::getBasicPlaylistsInfo() {

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

    sort_playlists_alphabetically(qt_playlists, 0, qt_playlists.size());

    return qt_playlists;

}

QVariantList StaccatoInterface::readLastSessionData() {

    std::string main_queue_playlist_id {""};
    std::uint64_t main_position {0}, added_position {0};

    if(AppManager::read_last_session_data(main_queue_playlist_id, main_position, added_position)) {

        return {QVariant(QString::fromStdString(main_queue_playlist_id)), QVariant(main_position), QVariant(added_position)};

    }

    return {QVariant(QString::fromStdString("")), QVariant(0), QVariant(0)};

}

QList<QVariantList> StaccatoInterface::getMainQueue() {

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

QList<QVariantList> StaccatoInterface::getAddedQueue() {

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

QString StaccatoInterface::getTrackFilePath(const QString& title, const QStringList& artists, const QString& album) {

    std::vector<std::string> artists_std {};
    for(const QString& artist: artists) {

        artists_std.push_back(artist.toStdString());

    }

    Track track (title.toStdString(), artists_std, album.toStdString());
    std::string file_path_std = TrackManager::get_track_file_path(track);
    return QString::fromStdString(file_path_std);

}

QString StaccatoInterface::getPlaylistImagePath(const QString& id) {

    return QString::fromStdString(AppManager::get_playlist_image_path(id.toStdString()));

}

int StaccatoInterface::getPinnedItemsZoomLevel() {

    return AppManager::get_pinned_items_zoom_level();

}

int StaccatoInterface::getPlaylistsZoomLevel() {

    return AppManager::get_playlists_zoom_level();

}

QString StaccatoInterface::getPinnedItemsSortMode() {

    return QString::fromStdString(AppManager::get_pinned_items_sort_mode());

}

void StaccatoInterface::setPinnedItemsZoomLevel(int zoomLevel) {

    AppManager::set_pinned_items_zoom_level(zoomLevel);

}

void StaccatoInterface::setPlaylistsZoomLevel(int zoomLevel) {

    AppManager::set_playlists_zoom_level(zoomLevel);

}

void StaccatoInterface::setPinnedItemsSortMode(const QString& sortMode) {

    AppManager::set_pinned_items_sort_mode(sortMode.toStdString());

}

QList<QVariant> StaccatoInterface::getLocalTrackInfo(const QString& path) {

    Track track = TrackManager::get_local_track_info(path.toStdString());
    QStringList artists {};
    for(std::string artist: track.artists()) {

        artists.append(QString::fromStdString(artist));

    }

    return {QVariant(QString::fromStdString(track.title())), artists, QVariant(QString::fromStdString(track.album()))};

}

QList<QVariant> StaccatoInterface::getOnlineTrackInfo(const QString& url) {

    std::pair<Track, std::string> track_artwork_pair = TrackManager::get_online_track_info(url.toStdString());
    Track& track = track_artwork_pair.first;
    QStringList artists {};
    for(std::string artist: track.artists()) {

        artists.append(QString::fromStdString(artist));

    }
    
    return {QVariant(QString::fromStdString(track.title())), artists, QVariant(QString::fromStdString(track.album())), QVariant(QString::fromStdString(track_artwork_pair.second))};

}