#include "tests.hpp"

using namespace staccato;

int main() {

    bool init_success = init_python();
    if(!init_success) {

        return false;

    }

    // playlist_testing();
    // track_manager_passive_local_testing();
    // track_manager_active_local_testing();
    // track_manager_passive_online_testing();
    // track_manager_active_online_testing();

    Py_FinalizeEx();

    // std::vector<std::string> list = tokenize_comma_separated_string("Lil Uzi Vert, Tyler, The Creator, 21 Savage");
    std::vector<std::string> list = get_artists_vector_from_str("Lil Uzi Vert, Tyler, The Creator, 21 Savage");
    for(std::string str: list) {

        std::cout << str << std::endl;

    }

    return 0;

}