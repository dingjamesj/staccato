#ifndef TESTS_HPP
#define TESTS_HPP

#include "track_manager.hpp"
#include "track.hpp"
#include "playlist.hpp"

namespace staccato {

    void track_testing();
    void playlist_testing();
    void track_manager_passive_local_testing();
    void track_manager_active_local_testing();
    void track_manager_passive_online_testing();
    void track_manager_active_online_testing();

}

#endif