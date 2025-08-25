#ifndef TRACK_HPP
#define TRACK_HPP

#include <string>
#include <unordered_set>
#include <format>
#include <iostream>

namespace staccato {

enum class URLType {SPOTIFY, YOUTUBE, UNKNOWN};

struct Track {

    std::string title;
    std::string artists;
    std::string album;

    //==================
    //  Track creation
    //==================

    Track(std::string title, std::string artists, std::string album);
    
    /*
     * 
     * For debugging purposes
     */
    void print() const;

    /**
     * @return The track's path, according to staccato's track dictionary. 
     */
    std::string get_path() const;
    
    bool file_readable() const;

    /**
     * 
     *  
     * Sets member variable to empty strings, deletes the file, and removes this track from staccato's track dictionary.
     */
    bool delete_file();

    void write_changes_to_file() const;

    bool is_empty() const;
    
    bool operator==(const Track& other) const;
    
};

}

template<> struct std::hash<staccato::Track> {

    std::size_t operator()(const staccato::Track& track) const {

        return std::hash<std::string>()(std::format("{} {} {}", track.title, track.artists, track.album));

    }

};

#endif