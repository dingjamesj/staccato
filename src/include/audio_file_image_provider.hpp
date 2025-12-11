#ifndef AUDIO_FILE_IMAGE_PROVIDER_HPP
#define AUDIO_FILE_IMAGE_PROVIDER_HPP

#include <QQuickImageProvider>
#include <QPixmap>
#include <vector>
#include <string>

namespace staccato {

    class AudioFileImageProvider: public QQuickImageProvider {

        public:

        AudioFileImageProvider();

        QPixmap requestPixmap(const QString &id, QSize *size, const QSize &requestedSize) override;

    };

}

#endif