#include "track.hpp"
#include "playlist.hpp"
#include "util.hpp"
#include <iostream>

int main() {

    staccato::Track track = staccato::import_track("C:\\Users\\James\\Music\\mongo\\Tyler, The Creator - Balloon (Lyrics).mp3");
    track.print();

    return 0;

}