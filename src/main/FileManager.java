package main;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class FileManager {

    private static final String PLAYLIST_DATA_LOCATION = "playlists.dat";

    public static List<Playlist> readPlaylists() throws FileNotFoundException, IOException {

        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(PLAYLIST_DATA_LOCATION));
        
        ArrayList<Playlist> playlists = new ArrayList<Playlist>();
        try {

            playlists.add((Playlist) inputStream.readObject());

        } catch(EOFException e) {

            //Do nothing

        } catch (ClassNotFoundException e) {

            e.printStackTrace();

        } finally {

            inputStream.close();

        }

        return playlists;

    }

    public static void addPlaylist(Playlist playlist) throws FileNotFoundException, IOException {

        AppendingObjectOutputStream outputStream = new AppendingObjectOutputStream(new FileOutputStream(PLAYLIST_DATA_LOCATION));
        outputStream.writeObject(playlist);


    }

    public static class AppendingObjectOutputStream extends ObjectOutputStream {

        public AppendingObjectOutputStream(OutputStream out) throws IOException {
            
        super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
        // do not write a header, but reset:
        // this line added after another question
        // showed a problem with the original
        reset();
        }

    }

}