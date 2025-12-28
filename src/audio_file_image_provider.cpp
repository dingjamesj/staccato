#include "audio_file_image_provider.hpp"
#include "app_manager.hpp"
#include "track_manager.hpp"

using namespace staccato;

AudioFileImageProvider::AudioFileImageProvider(): QQuickImageProvider(QQuickImageProvider::Pixmap) {}

QPixmap AudioFileImageProvider::requestPixmap(const QString &id, QSize *size, const QSize &requestedSize) {

    std::cout << std::endl << "CALLED" << std::endl << std::endl;

    QPixmap pixmap = TrackManager::get_track_artwork(id.toStdString());

    if(pixmap.isNull()) {

        std::cout << "pingle fart" << std::endl;

        pixmap.load(AppManager::PLACEHOLDER_ART_PATH.data());
        if(size != nullptr) {

            *size = QSize(100, 100);

        }

        std::cout << "shit" << std::endl;

        return pixmap;

    }
    
    if(size != nullptr) {

        *size = pixmap.size();

    }

    return pixmap;

}