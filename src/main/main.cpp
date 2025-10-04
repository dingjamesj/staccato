#include "tests.hpp"

using namespace staccato;

int main() {

    bool init_success = init_python();
    if(!init_success) {

        return false;

    }

    // playlist_testing();
    // track_manager_passive_local_testing();
    track_manager_active_local_testing();

    Py_FinalizeEx();

    return 0;

}