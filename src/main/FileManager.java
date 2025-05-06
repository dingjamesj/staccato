package main;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.LogManager;

public abstract class FileManager {

    private static final String PLAYLIST_DATA_LOCATION = "playlists.dat";

    /**
     * Returns the HashSet<Playlist> stored in the playlist data file
     * @return The HashSet<Playlist> stored in the playlist data file
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static Set<Playlist> readPlaylists() throws IOException {

        ObjectInputStream inputStream;
        try {

            inputStream = new ObjectInputStream(new FileInputStream(PLAYLIST_DATA_LOCATION));

        } catch(FileNotFoundException e) {

            return new HashSet<Playlist>();

        }
        
        Set<Playlist> playlists;
        try {

            playlists = (HashSet<Playlist>) inputStream.readObject();

        } catch(EOFException e) {

            throw e;

        } catch (ClassNotFoundException e) {

            //This should never happen!!
            e.printStackTrace();
            return new HashSet<Playlist>();

        } finally {

            inputStream.close();

        }

        return playlists;

    }

    /**
     * Adds a playlist to the playlists data file. <br></br>
     * Returns true if was successful (i.e. playlist's directory isn't already taken )
     * @param playlist
     * @return If the playlist was added
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static boolean addPlaylist(Playlist playlist) throws FileNotFoundException, IOException {

        Set<Playlist> storedPlaylists;
        File playlistFile = new File(PLAYLIST_DATA_LOCATION);
        if(playlistFile.exists()) {

            storedPlaylists = readPlaylists();

        } else {

            storedPlaylists = new HashSet<Playlist>();

        }

        boolean writeWasSuccess = storedPlaylists.add(playlist);

        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(PLAYLIST_DATA_LOCATION));
        outputStream.writeObject(storedPlaylists);
        outputStream.flush();
        outputStream.close();

        return writeWasSuccess;

    }

    public static Set<Track> readTracksFromDirectory(String dirStr) throws FileNotFoundException {

        File[] files = new File(dirStr).listFiles();

        if(files == null) {

            throw new FileNotFoundException();

        }

        Set<Track> tracks = new HashSet<Track>();
        for(int i = 0; i < files.length; i++) {

            if(files[i].isDirectory() || !files[i].getName().endsWith(".mp3")) {

                continue;

            }

            tracks.add(new Track(files[i]));

        }

        return tracks;

    }

    /**
     * Inititalizer
     * https://stackoverflow.com/questions/50778442/how-to-disable-jaudiotagger-logger-completely
     */
    static {

        LogManager manager = LogManager.getLogManager();

        try {

            manager.readConfiguration(FileManager.class.getResourceAsStream("/audioTagger.properties"));

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public static void main(String[] args) {

        try {

            System.out.println(addPlaylist(new Playlist("C:\\Users\\James\\Music\\saco")));
            Set<Playlist> playlists = readPlaylists();
            System.out.println(playlists.size());
            for(Playlist playlist: playlists) {

                System.out.println(playlist);

            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        // try {
            
        //     Set<Track> tracks = readTracksFromDirectory("C:\\Users\\James\\Music\\saco");

        //     for(Track track: tracks) {

        //         System.out.println(track);
    
        //     }

        // } catch (FileNotFoundException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }

    }

}