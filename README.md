# staccato
A desktop app that tracks music files & their metadata, organizes them into playlists, and downloads music files from the web.

Uses Qt, TagLib, yt-dlp, and Spotipy.

## Contributing
### Setting Up the Project
--


### Program Structure
#### `class staccato::FileManager`
**The Goal:**  
To ensure that every interaction with music files are tracked by staccato.  
To achieve this goal, 
- `FileManager` is the sole gateway to the file system,
- Its file-managing operations cannot be accessed outside of the `Playlist` and `Track` classes, and
- The only way to create new `Playlist` and `Track` instances is through `FileManager`.

**More Details:**
- `FileManager` tracks music files by mapping `Track` objects to file paths (as `string`)
  - Known as "*staccato's track dictionary*" in code documentation
  - Is an `unordered_map<Track, string>`
- Most `FileManager` functions are only visible to `Playlist` and `Track`
  - `Playlist` and `Track` provide abstractions for file system interactions, eliminating calls to `FileManager` elsewhere in the program.
  - *e.g.* instead of calling `FileManager::delete_track(Track& track)`, one may call `track.delete_file()`.
  - This ensures *synchronization between `Track` objects and the file system*
- Only functions that are accessible outside of `Playlist` and `Track` are the functions that create `Playlist` and `Track` instances.
  - *e.g.* the `Track`-creating function `FileManager::import_track(string path)`
  - This is because `Playlist` and `Track` have no public constructors (to avoid accidentally creating their instances without tracking them in the file system, i.e. *synchronization between `Track` objects and the file system*)