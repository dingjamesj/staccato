#include "audio_file_image_provider.hpp"
#include "track_manager.hpp"

using namespace staccato;

AudioFileImageProvider::AudioFileImageProvider(): QQuickImageProvider(QQuickImageProvider::Pixmap) {}

QPixmap AudioFileImageProvider::requestPixmap(const QString &id, QSize *size, const QSize &requestedSize) {

    const char* raw_image_data = TrackManager::get_track_artwork_raw(id.toStdString());
    QPixmap pixmap;
    int width {100}, height {100};

    if(raw_image_data == nullptr) {

        pixmap.load(":/staccato/src/ui/resources/placeholder.jpg");
        if(size != nullptr) {

            *size = QSize(width, height);

        }

    } else {

        bool success = pixmap.loadFromData((const uchar*) raw_image_data, 13284);
        // bool success = pixmap.load("D:\\car\\idiot6.png");
        // bool success = pixmap.load("C:\\Users\\James\\Music\\aodsh.mp3");

        if(!success) {

            pixmap = QPixmap(requestedSize.width() > 0 ? requestedSize.width() : width, requestedSize.height() > 0 ? requestedSize.height() : height);
            pixmap.fill(QColor("magenta").rgba());
            if(size != nullptr) {

                *size = QSize(width, height);

            }

            return pixmap;

        }

        if(size != nullptr) {

            *size = pixmap.size();

        }

    }

    return pixmap;

}