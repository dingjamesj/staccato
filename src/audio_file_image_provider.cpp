#include "audio_file_image_provider.hpp"
#include "app_manager.hpp"
#include "track_manager.hpp"

using namespace staccato;

AudioFileImageProvider::AudioFileImageProvider(): QQuickImageProvider(QQuickImageProvider::Pixmap) {}

QPixmap AudioFileImageProvider::requestPixmap(const QString &id, QSize *size, const QSize &requestedSize) {

    QPixmap pixmap = TrackManager::get_track_artwork(id.toStdString());

    if(pixmap.isNull()) {

        pixmap.load(AppManager::PLACEHOLDER_ART_PATH.data());
        if(size != nullptr) {

            *size = QSize(100, 100);

        }

        return pixmap;

    }
    
    if(size != nullptr) {

        *size = pixmap.size();

    }

    return pixmap;

}