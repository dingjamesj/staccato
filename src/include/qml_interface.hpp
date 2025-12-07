#ifndef QML_INTERFACE_HPP
#define QML_INTERFACE_HPP

#include <QObject>
#include <QtQml>

namespace staccato {

    class StaccatoInterface: public QObject {

        Q_OBJECT
        QML_NAMED_ELEMENT(Staccato)
        QML_UNCREATABLE("DO NOT CREATE THIS JAWN")

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

    };

}

#endif