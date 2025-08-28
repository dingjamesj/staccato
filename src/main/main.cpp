
#include <iostream>
#include <taglib/fileref.h>

int main() {

    TagLib::FileRef file_ref("C:\\Users\\James\\Music\\mongo\\Tyler, The Creator - Balloon (Lyrics).mp3");
    if(file_ref.isNull()) {

        std::cout << "File null";
        return 1;

    }

    TagLib::List<TagLib::VariantMap> picture_properties = file_ref.complexProperties("PICTURE");
    for(const TagLib::VariantMap& property: picture_properties) {

        for(const std::pair<const TagLib::String, TagLib::Variant>& pair: property) {

            std::cout << "K: " << pair.first << std::endl;

        }

    }

    return 0;

}