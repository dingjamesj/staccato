#ifndef QT_INTERFACE_HPP
#define QT_INTERFACE_HPP

#include <QObject>
#include <QtQml>

namespace staccato {

    class TrackManagerInterface: public QObject {

        Q_OBJECT
        QML_NAMED_ELEMENT(TrackManage)
        QML_UNCREATABLE("")

        public:

        Q_INVOKABLE
        QList<QVariantList> get_pinned_items();

        Q_INVOKABLE
        QList<QStringList> get_basic_playlists_info();

    };

}

#endif