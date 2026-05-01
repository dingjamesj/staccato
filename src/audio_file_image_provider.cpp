#include "app_manager.hpp"
#include "track_manager.hpp"
#include "audio_file_image_provider.hpp"

using namespace staccato;

AudioFileImageProvider::AudioFileImageProvider(): QQuickImageProvider(QQuickImageProvider::Pixmap) {}

QPixmap AudioFileImageProvider::requestPixmap(const QString &id, QSize *size, const QSize &requestedSize) {

    TagLib::ByteVector data  = TrackManager::get_track_artwork(id.toStdString());
    QPixmap pixmap;

    if(data == nullptr || !pixmap.loadFromData(QByteArray(data.data(), data.size()))) {

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