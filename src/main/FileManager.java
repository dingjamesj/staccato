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

    public static void main(String[] args) {

        try {
            System.out.println(addPlaylist(new Playlist("my goooning playlist", "D:\\gooning", null)));
            System.out.println(addPlaylist(new Playlist("my goooning playlist", "D:\\gooning", null)));
            System.out.println(addPlaylist(new Playlist("saco", "D:/saco", null)));
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

    }

}