#include "audio_file_image_provider.hpp"
#include "track_manager.hpp"

using namespace staccato;

AudioFileImageProvider::AudioFileImageProvider(): QQuickImageProvider(QQuickImageProvider::Pixmap) {}

QPixmap AudioFileImageProvider::requestPixmap(const QString &id, QSize *size, const QSize &requestedSize) {

    const char* raw_image_data = TrackManager::get_track_artwork_raw(id.toStdString());
    QPixmap pixmap;

    if(raw_image_data == nullptr) {

        pixmap.fill(QColor("white").rgba());
        if(size != nullptr) {

            *size = QSize(100, 100);

        }

    } else {

        pixmap.loadFromData((const uchar*) raw_image_data, (uint) (sizeof(raw_image_data) / sizeof(raw_image_data[0])));
        if(size != nullptr) {

            *size = pixmap.size();

        }

    }

    return pixmap.scaled(requestedSize, Qt::KeepAspectRatio);

}