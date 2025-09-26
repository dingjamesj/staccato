#include "tests.hpp"

using namespace staccato;

int main() {

    /* Test Cases
    
    get_online_track_info
     - Regular Spotify track
     - Unavailable Spotify track
     - Spotify podcast
     - Unavailable Spotify podcast
     - Regular YouTube Music vdeo
     - Non-music regular YouTube video 
     - Age restricted YouTube video
     - Unavailable YouTube video
     - Privated YouTube video
     - Unlisted YouTube video

    */

    bool init_success = init_python();
    if(!init_success) {

        return false;

    }

    playlist_testing();
    // track_manager_passive_testing();

    Py_FinalizeEx();

    return 0;

}