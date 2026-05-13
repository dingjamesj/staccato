#include "app_manager.hpp"
#include "track_manager.hpp"
#include "track.hpp"
#include "playlist_tree.hpp"
#include "qml_interface.hpp"

using namespace staccato;

void StaccatoInterface::readSettings() {

    AppManager::read_settings();

}

QVariantList StaccatoInterface::readPersistentData() {

    std::string main_queue_playlist_id {""};
    unsigned int main_position {0}, added_position {0};

    if(AppManager::read_persistent_data(main_queue_playlist_id, main_position, added_position)) {

        return {QVariant(QString::fromStdString(main_queue_playlist_id)), QVariant((uint) main_position), QVariant((uint) added_position)};

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

QList<QVariant> StaccatoInterface::getPlaylistTree() {

    //Early retur an empty QList if the playlist tree is empty: 
    if(TrackManager::get_playlist_tree().begin().operator->() == nullptr) {

        return {};

    }

    //Every QList represents a folder of playlists containing only QStrings and child QLists.
    //Each QList begins with the folder name, followed by its contents (playlist names and child folders).

    QList<QVariant> root {};
    std::stack<QList<QVariant>*> stack_ptr_qlists;
    stack_ptr_qlists.push(&root);
    for(PlaylistTree::ConstIterator iter = TrackManager::get_playlist_tree().begin(); iter != TrackManager::get_playlist_tree().end() && !stack_ptr_qlists.empty(); ++iter) {

        if(iter.operator->() == nullptr) {

            stack_ptr_qlists.pop();
            continue;

        }

        QList<QVariant>& curr_playlist_folder = *stack_ptr_qlists.top();
        if(iter.is_folder_start()) {

            //Add a folder

            curr_playlist_folder.push_back(QVariant(QList<QVariant>()));
            QList<QVariant>* ptr_new_folder = static_cast<QList<QVariant>*>(
                curr_playlist_folder[curr_playlist_folder.size() - 1].data()
            );
            (*ptr_new_folder).push_back(QVariant(QString::fromStdString(*iter))); //First element in the QList is the folder's name

            stack_ptr_qlists.push(ptr_new_folder);

        } else {

            //Add a playlist name
            curr_playlist_folder.push_back(QVariant(QString::fromStdString(*iter)));

        }

    }

    return root;

}

QList<QVariant> StaccatoInterface::getLocalTrackInfo(const QString& path) {

    Track track = TrackManager::get_local_track_info(path.toStdString());
    if(track.is_empty()) {

        return {};

    }

    QStringList artists {};
    for(std::string artist: track.artists()) {

        artists.append(QString::fromStdString(artist));

    }

    return {QVariant(QString::fromStdString(track.title())), artists, QVariant(QString::fromStdString(track.album()))};

}

QList<QVariant> StaccatoInterface::getOnlineTrackInfo(const QString& url) {

    std::pair<Track, std::string> track_info = TrackManager::get_online_track_full_info(url.toStdString());
    Track& track = track_info.first;
    QStringList artists {};
    for(std::string artist: track.artists()) {

        artists.append(QString::fromStdString(artist));

    }
    
    return {
        QString::fromStdString(track.title()), 
        artists, 
        QString::fromStdString(track.album()), 
        QString::fromStdString(track_info.second)
    };
}

bool StaccatoInterface::importTrackFromFilesystem(const QString& title, const QStringList& artists, const QString& album, const QString& path) {

    std::vector<std::string> artistsStd {};
    for(const QString& artist: artists) {

        artistsStd.push_back(artist.toStdString());

    }
    return TrackManager::import_track_from_filesystem(path.toStdString(), Track(title.toStdString(), artistsStd, album.toStdString()));

}

bool StaccatoInterface::deleteTrack(const QString& title, const QStringList& artists, const QString& album) {

    std::vector<std::string> artistsStd {};
    for(const QString& artist: artists) {

        artistsStd.push_back(artist.toStdString());

    }
    return TrackManager::delete_track(Track(title.toStdString(), artistsStd, album.toStdString()));

}

QList<QVariant> StaccatoInterface::downloadTrackFromUrl(const QString& url, const QStringList& args) {

    std::vector<std::string> argsStd {};
    for(const QString& str: args) {

        argsStd.push_back(str.toStdString());

    }

    std::pair<Track, std::string> download_info = TrackManager::download_track_from_url(
        url.toStdString(),
        argsStd
    );

    //Returned an error value (empty track w/ error message)
    if(download_info.first.is_empty()) {

        return {QString::fromStdString(download_info.second)};

    }

    Track& track = download_info.first;
    QStringList artists {};
    for(std::string artist: track.artists()) {

        artists.append(QString::fromStdString(artist));

    }

    return {
        QString::fromStdString(track.title()), 
        artists, 
        QString::fromStdString(track.album()), 
        QString::fromStdString(download_info.second)
    };

}

QString StaccatoInterface::getPlaceholderImagePath() {

    return QString::fromStdString("qrc" + std::string(AppManager::PLACEHOLDER_ART_PATH));

}