# staccato
A desktop app that tracks audio files & their metadata, organizes them into playlists, and downloads audio files from the web.

Uses Qt 6, TagLib 2.x.x, yt-dlp, Spotipy, and mutagen.

## Please note:
*This README currently serves as notes about the project that I take for myself.*  
I will update the README and turn it into an actual README when the project is at or near completion.

## Contributing
### Setting Up the Project
--


### Program Structure

#### The `Track` struct
- Serves as ONLY a container of information
- Most functions on Tracks will be in the `TrackManager` class/namespace
  - In original staccato (pre C++ switch), the `Track` class had most of the functions, but that's because back then the `Track` class contained the audio file path. Now it doesn't anymore.

#### File tracking
- All files are stored in a singular folder
- Importing files from places in the hard drive is as simple as copying the file into the folder
  - If the file has existing metadata, then we will first show the user that before copying
  - Regardless of the existing metadata, we will overwrite it with what the user sets it to (which can be its original information)
- Downloading files is just as simple as downloading the file into the folder
- In the folder, there is a file that serves as a dict, mapping tracks to files
  - This allows the filenames in the folder to be whatever they want
  - Known as the .stkl file
- On startup, we run through the files in the folder to make sure that they still line up with the dict
  - If there are any missing files, we bring that up to the user
  - If there are any additional files (that would've been added outside of staccato), we bring it up to the user
    - Further more, if a track object with the file's metadata does not exist in the dict, then we will add it with the path to the dict
    - Otherwise, we do nothing
    - tldr we bring the user attention to the extra file regardless, and if the extra file's metadata is indictive of a track that isn't already in the dict, then we add it to the dict

#### Playlist loading
- The .sply file is composed of
  - Playlist name
  - Cover image file path
  - Its tracks as `Track` objects
    - Represented as an `unordered_set`
  - The current sort mode
- The name of a .sply file is the playlist's unique ID
  - *e.g.* a playlist with name "rargb" and ID "1dda23" will have a .sply file "rargb.sply"
- To load a playlist, the program searches for the .sply with the unique ID
  - *e.g.* to look for the previous playlist "rargb," we try to find "1dda23.sply"
- When the playlist is loaded onto the GUI, it takes the `unordered_set`, sorts the tracks, and puts them into a `vector`.
  - The `vector` is then used in the display of the tracklist
- Note that the playlist object does not contain an ID
  - The ID is purely a `TrackManager` thing. The ID is contained only in the .sply file names and in the GUI itself