#include "track_manager.hpp"
#include "interface_to_qt.hpp"

using namespace staccato;

QVector<QVariantList> TrackManagerInterface::get_pinned_items() {
    
    std::vector<std::tuple<bool, std::string, std::vector<std::string>, std::string>> pinned_items = TrackManager::get_pinned_items();

    QVector<QVariantList> qt_pinned_items {};
    for(const std::tuple<bool, std::string, std::vector<std::string>, std::string>& item: pinned_items) {

        QStringList properties {};
        for(const std::string& property: std::get<2>(item)) {

            properties.push_back(QString::fromStdString(property));

        }
        
        qt_pinned_items.push_back({QVariant(std::get<0>(item)), QVariant(QString::fromStdString(std::get<1>(item))), QVariant(properties), QVariant(QString::fromStdString(std::get<3>(item)))});

    }

    return qt_pinned_items;

}
