#ifndef QT_INTERFACE_HPP
#define QT_INTERFACE_HPP

#include <QObject>
#include <QtQml>

namespace staccato {

    class StaccatoInterface: public QObject {

        Q_OBJECT
        QML_NAMED_ELEMENT(Staccato)
        QML_UNCREATABLE("")

        public:

        Q_INVOKABLE
        QList<QVariantList> get_pinned_items();

        Q_INVOKABLE
        QList<QStringList> get_basic_playlists_info();

        Q_INVOKABLE
        QVariantList read_last_session_data();

        Q_INVOKABLE
        QList<QVariantList> get_saved_main_queue();

        //TODO check that you can use QVariantList in QML
        //TODO port to here the get_added_queue and get_main_queue functions

        Q_INVOKABLE
        // QList<Q get_added_queue();

    };

}

#endif