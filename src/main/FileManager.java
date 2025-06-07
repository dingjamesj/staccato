package main;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.LogManager;

public abstract class FileManager {

    public static final String PLAYLIST_DATA_LOCATION = "playlists.dat";

    private static AtomicBoolean isReadingTracks = new AtomicBoolean(false);

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
     * Adds a playlist to the playlists data file.
     * @param playlist
     * @return True if the playlist was successfully added
     * @throws FileNotFoundException If the playlist file can't be created
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

        if(!storedPlaylists.add(playlist)) {

            return false;

        }

        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(PLAYLIST_DATA_LOCATION));
        outputStream.writeObject(storedPlaylists);
        outputStream.flush();
        outputStream.close();

        return true;

    }

    /**
     * Removes a playlist from the playlists data file.
     * @param playlist
     * @return True if the playlist was successfully found and removed
     * @throws IOException
     */
    public static boolean removePlaylist(Playlist playlist) throws IOException {

        Set<Playlist> storedPlaylists;
        File playlistFile = new File(PLAYLIST_DATA_LOCATION);
        if(playlistFile.exists()) {

            storedPlaylists = readPlaylists();

        } else {

            return false;

        }

        if(!storedPlaylists.remove(playlist)) {

            return false;

        }

        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(PLAYLIST_DATA_LOCATION));
        outputStream.writeObject(storedPlaylists);
        outputStream.flush();
        outputStream.close();

        return true;

    }

    /**
     * Deletes a playlist's actual files on the computer.
     * @param playlist
     * @return True if the whole directory was deleted, false if some files were left undeleted (e.g. any non-mp3 files or subdirectories)
     * @throws FileNotFoundException If the playlist's directory wasn't found
     * @throws IOException
     */
    public static boolean deletePlaylist(Playlist playlist) throws FileNotFoundException, IOException {

        boolean directoryIsAllMP3s = true;
        File playlistDirectory = new File(playlist.getDirectory());
        File[] files = playlistDirectory.listFiles();
        if(files == null) {

            throw new FileNotFoundException();

        }
        for(int i = 0; i < files.length; i++) {

            if((files[i].isDirectory() || !files[i].getName().endsWith(".mp3")) && (!files[i].getName().equals("AlbumArtSmall.jpg") && !files[i].getName().equals("Folder.jpg"))) {

                directoryIsAllMP3s = false;
                continue;

            }

            if(!files[i].delete()) {

                throw new IOException("File at " + files[i].getCanonicalPath() + " wasn't able to be deleted.");

            }

        }

        //If the directory is now all empty, delete the directory.
        if(directoryIsAllMP3s) {

            playlistDirectory.delete();

        }

        return directoryIsAllMP3s;

    }

    /**
     * Removes <code>oldPlaylist</code> and adds <code>newPlaylist</code>.
     * @param oldPlaylist The playlist to remove
     * @param newPlaylist The playlist to add
     * @return True if the old playlist was successfully found and removed and if the new playlist was successfully added.
     * @throws IOException
     */
    public static boolean replacePlaylist(Playlist oldPlaylist, Playlist newPlaylist) throws IOException {

        Set<Playlist> storedPlaylists;
        File playlistFile = new File(PLAYLIST_DATA_LOCATION);
        if(playlistFile.exists()) {

            storedPlaylists = readPlaylists();

        } else {

            return false;
            
        }

        if(!storedPlaylists.remove(oldPlaylist)) {

            return false;

        }

        if(!storedPlaylists.add(newPlaylist)) {

            return false;

        }

        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(PLAYLIST_DATA_LOCATION));
        outputStream.writeObject(storedPlaylists);
        outputStream.flush();
        outputStream.close();

        return true;

    }

    public static void deleteTrack(Track track) throws FileNotFoundException, SecurityException {

        File file = new File(track.getFileLocation());
        if(!file.exists() || file.isDirectory()) {

            throw new FileNotFoundException("Track file not found at " + track.getFileLocation());

        }

        file.delete();

    }

    /**
     * 
     * @param dirStr
     * @return The Track set found from reading the directory. Null if the reading was interrupted.
     * @throws FileNotFoundException
     */
    public static Set<Track> readTracksFromDirectory(String dirStr) throws FileNotFoundException {

        isReadingTracks.set(true);

        File[] files = new File(dirStr).listFiles();

        if(files == null) {

            throw new FileNotFoundException();

        }

        Set<Track> tracks = new HashSet<Track>();
        for(int i = 0; i < files.length; i++) {

            if(!isReadingTracks.get()) {

                return null;

            }

            if(files[i].isDirectory() || !files[i].getName().endsWith(".mp3")) {

                continue;

            }

            tracks.add(new Track(files[i]));

        }

        return tracks;

    }

    public static void stopReadingTracks() {

        isReadingTracks.set(false);

    }

    public static byte[] readByteArray(String artworkURL) throws URISyntaxException, IOException {

        return readByteArray(new URI(artworkURL).toURL());

    }

    public static byte[] readByteArray(File file) throws MalformedURLException, IOException {

        return readByteArray(file.toURI().toURL());

    }

    public static byte[] readByteArray(URL url) throws IOException {

        ByteArrayOutputStream coverImageURLByteArrayStream = new ByteArrayOutputStream();
        url.openStream().transferTo(coverImageURLByteArrayStream);
        byte[] artworkByteArray = coverImageURLByteArrayStream.toByteArray();
        coverImageURLByteArrayStream.close();

        return artworkByteArray;

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

}