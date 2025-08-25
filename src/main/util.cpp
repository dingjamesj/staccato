#include "util.hpp"

std::string staccato::seconds_to_hms(int seconds) {

    int hours = seconds / 3600;
    int minutes = (seconds % 3600) / 60;
    seconds = seconds % 60;
    return std::format("{:02d}:{:02d}:{:02d}", hours, minutes, seconds);

}