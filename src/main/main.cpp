#include "track.hpp"
#include "playlist.hpp"
#include "util.hpp"
#include "file_manager.hpp"
#include <iostream>

using namespace staccato;

int main() {

    Track track = FileManager::import_track("C:\\Users\\James\\Music\\mongo\\Tyler, The Creator - Balloon (Lyrics).mp3");
    track.print();
    std::cout << std::endl;
    FileManager::print_track_dict();
    std::cout << std::endl;

    Track track2 = FileManager::import_track("C:\\Users\\James\\Music\\file_example_MP3_2MG.mp3");
    track2.print();
    std::cout << std::endl;
    FileManager::print_track_dict();
    std::cout << std::endl;

    Track fake_track = FileManager::import_track("C:\\Users\\James\\Documents\\Empty Document.pdf");
    fake_track.print();
    std::cout << std::endl;
    FileManager::print_track_dict();
    std::cout << std::endl;

    Track dupe_track = FileManager::import_track("C:\\Users\\James\\Music\\aodsh.mp3");
    dupe_track.print();
    std::cout << std::endl;
    FileManager::print_track_dict();
    std::cout << std::endl;

    // std::cout << std::format("{} {} {}", "goon1", "poop", "second") << std::endl;

    return 0;

}