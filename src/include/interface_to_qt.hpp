#ifndef QT_INTERFACE_HPP
#define QT_INTERFACE_HPP

#include "track_manager.hpp"
#include <qt6/QtQmlIntegration/qqmlintegration.h>

namespace staccato {

    class InterfaceToQt: public QObject {

        Q_OBJECT
        QML_ELEMENT
        QML_SINGLETON

        public:
        

    }

}

#endif