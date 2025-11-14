#ifndef QT_INTERFACE_HPP
#define QT_INTERFACE_HPP

#include "track_manager.hpp"
#include <QObject>
#include <QtQml>

namespace staccato {

    class QtTrack: public QObject {

        Q_OBJECT
        QML_ELEMENT

    }

    class QtPlaylist: public QObject{

        Q_OBJECT
        QML_ELEMENT

    }

}

#endif