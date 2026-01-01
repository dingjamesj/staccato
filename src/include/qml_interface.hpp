#ifndef QML_INTERFACE_HPP
#define QML_INTERFACE_HPP

#include <QObject>
#include <QtQml>

namespace staccato {

    class StaccatoInterface: public QObject {

        Q_OBJECT
        QML_ELEMENT
        QML_SINGLETON

        private:
        
        //Helper functions

        void sort_pinned_items_alphabetically(QList<QVariantList>& qt_pinned_items, qsizetype begin, qsizetype end);
        void sort_playlists_alphabetically(QList<QStringList>& qt_playlists, qsizetype begin, qsizetype end);

        public:

        Q_INVOKABLE
        void readSettings();

        Q_INVOKABLE
        QList<QVariantList> getPinnedItems(const QString& sortMode);

        Q_INVOKABLE
        QList<QStringList> getBasicPlaylistsInfo();

        Q_INVOKABLE
        QVariantList readLastSessionData();

        Q_INVOKABLE
        QList<QVariantList> getMainQueue();

        Q_INVOKABLE
        QList<QVariantList> getAddedQueue();

        Q_INVOKABLE
        QString getTrackFilePath(const QString& title, const QStringList& artists, const QString& album);

        Q_INVOKABLE
        QString getPlaylistImagePath(const QString& id);

        Q_INVOKABLE
        int getPinnedItemsZoomLevel();

        Q_INVOKABLE
        int getPlaylistsZoomLevel();

        Q_INVOKABLE
        QString getPinnedItemsSortMode();

        Q_INVOKABLE
        void setPinnedItemsZoomLevel(int zoomLevel);

        Q_INVOKABLE
        void setPlaylistsZoomLevel(int zoomLevel);

        Q_INVOKABLE
        void setPinnedItemsSortMode(const QString& sortMode);

        Q_INVOKABLE
        QList<QVariant> getLocalTrackInfo(const QString& path);

        Q_INVOKABLE
        QList<QVariant> getOnlineTrackInfo(const QString& url);

    };

}

#endif