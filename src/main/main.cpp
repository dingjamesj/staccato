#include "track.hpp"
#include "playlist.hpp"
#include "util.hpp"
#include "file_manager.hpp"

#include <iostream>

using namespace staccato;

int main() {

    Track track = Track::import_track("C:\\Users\\James\\Music\\mongo\\Tyler, The Creator - Balloon (Lyrics).mp3");
    std::cout << track.get_path() << std::endl;
    std::cout << track.has_valid_audio_file() << std::endl;
    std::cout << "==============================" << std::endl;

    Track track2 = Track::import_track("C:\\Users\\James\\Music\\file_example_MP3_2MG.mp3");
    std::cout << track2.get_path() << std::endl;
    std::cout << track2.has_valid_audio_file() << std::endl;
    std::cout << "==============================" << std::endl;

    Track fake_track = Track::import_track("C:\\Users\\James\\Documents\\Empty Document.pdf");
    std::cout << fake_track.get_path() << std::endl;
    std::cout << fake_track.has_valid_audio_file() << std::endl;
    std::cout << "==============================" << std::endl;

    Track dupe_track = Track::import_track("C:\\Users\\James\\Music\\aodsh.mp3");
    std::cout << dupe_track.get_path() << std::endl;
    std::cout << dupe_track.has_valid_audio_file() << std::endl;

    // std::cout << std::format("{} {} {}", "goon1", "poop", "second") << std::endl;

    return 0;

}