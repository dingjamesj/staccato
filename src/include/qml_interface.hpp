#ifndef QML_INTERFACE_HPP
#define QML_INTERFACE_HPP

#include <QObject>
#include <QtQml>

namespace staccato {

    class StaccatoInterface: public QObject {

        Q_OBJECT
        QML_ELEMENT
        QML_UNCREATABLE("The C++ to QML interface is not a creatable object.")

        public:

        Q_INVOKABLE
        void readSettings();

        Q_INVOKABLE
        QList<QVariantList> getPinnedItems();

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

    };

}

#endif