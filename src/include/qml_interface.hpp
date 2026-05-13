#ifndef QML_INTERFACE_HPP
#define QML_INTERFACE_HPP

#include <QObject>
#include <QtQml>
#include <QPixmap>
#include <unordered_map>
#include <vector>
#include <string>

//=====================================================================================================================================
// Since Qt requires their own Qt objects such as QList and QString, we need an "interface" that converts STL objects into Qt objects.
// This is that interface.
//=====================================================================================================================================

namespace staccato {

    class StaccatoInterface: public QObject {

        Q_OBJECT
        QML_ELEMENT
        QML_SINGLETON

        public:

        Q_INVOKABLE
        void readSettings();

        Q_INVOKABLE
        QVariantList readPersistentData();

        Q_INVOKABLE
        QList<QVariantList> getMainQueue();

        Q_INVOKABLE
        QList<QVariantList> getAddedQueue();

        Q_INVOKABLE
        QString getTrackFilePath(const QString& title, const QStringList& artists, const QString& album);

        Q_INVOKABLE
        QString getPlaylistImagePath(const QString& id);

        /// @brief Each QList represents a folder, and each QList's first element is the folder name.
        Q_INVOKABLE
        QList<QVariant> getPlaylistTree();

        Q_INVOKABLE
        QList<QVariant> getLocalTrackInfo(const QString& path);

        Q_INVOKABLE
        QList<QVariant> getOnlineTrackInfo(const QString& url);

        Q_INVOKABLE
        bool importTrackFromFilesystem(const QString& title, const QStringList& artists, const QString& album, const QString& path);

        Q_INVOKABLE
        bool deleteTrack(const QString& title, const QStringList& artists, const QString& album);

        Q_INVOKABLE
        QList<QVariant> downloadTrackFromUrl(const QString& url, const QStringList& args);

        Q_INVOKABLE
        QString getPlaceholderImagePath();

    };

}

#endif