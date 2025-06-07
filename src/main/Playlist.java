package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

/**
 * Contains info about a playlist: 
 * <i>
 * Cover Art,
 * Title,
 * Description,
 * Tracklist
 * </i>
*/
public class Playlist implements Serializable {

    private static final long serialVersionUID = 0L;

    //Staccato only tracks the name, directory, and cover art of a playlist.
    private String name;
    private String directory;
    private byte[] coverArtByteArray;

    private transient Set<Track> tracks;
    private transient int duration;

    public Playlist(String directory) {

        File directoryFile = new File(directory);
        this.name = directoryFile.getName();
        if(this.name.isEmpty()) {

            this.name = "New Playlist";

        }
        try {

            this.directory = directoryFile.getCanonicalPath();

        } catch (IOException e) {

            e.printStackTrace();
            this.directory = directory;

        }
        this.coverArtByteArray = null;

    }

    public String getName() {

        return name;

    }

    public String getDirectory() {

        return directory;

    }

    public byte[] getCoverArtByteArray() {

        return coverArtByteArray;

    }

    public boolean directoryExists() {

        return new File(directory).exists();

    }

    public void setName(String name) {

        this.name = name;

    }

    public void setCoverArtByteArray(byte[] coverArtByteArray) {

        if(coverArtByteArray == null) {

            this.coverArtByteArray = null;
            return;

        }

        this.coverArtByteArray = new byte[coverArtByteArray.length];
        for(int i = 0; i < coverArtByteArray.length; i++) {

            this.coverArtByteArray[i] = coverArtByteArray[i];

        }

    }

    public boolean removeTrack(Track track) {

        if(tracks.contains(track)) {

            tracks.remove(track);
            duration -= track.getDuration();
            return true;

        }

        return false;

    }

    public boolean addTrack(Track track) {

        if(!tracks.contains(track)) {

            tracks.add(track);
            duration += track.getDuration();
            return true;

        }

        return false;

    }

    public String getDuration() {

        //Find the playlist duration if isn't already cached
        if(duration == -1) {

            if(tracks == null) {

                return "--:--:--";

            }

            Iterator<Track> iterator = tracks.iterator();
            duration = 0;
            while(iterator.hasNext()) {

                duration += iterator.next().getDuration();

            }

        }

        return formatHoursMinutesSeconds(duration);

    }

    public int getSize() {

        if(tracks == null) {

            return 0;

        }

        return tracks.size();

    }

    public Track[] getTracks() {

        if(tracks == null) {

            return null;

        }

        Track[] tracksArray = new Track[tracks.size()];
        Iterator<Track> iterator = tracks.iterator();
        for(int i = 0; iterator.hasNext(); i++) {

            tracksArray[i] = iterator.next();

        }

        return tracksArray;

    }

    /**
     * Load track info from this playlist's directory.
     * @return True if tracks were successfully loaded, false otherwise (e.g. if the track reading was interrupted)
     */
    public synchronized boolean loadTracks() {

        try {

            tracks = FileManager.readTracksFromDirectory(directory);
            if(tracks == null) {

                return false;

            }

            duration = 0;
            Iterator<Track> iterator = tracks.iterator();
            while(iterator.hasNext()) {

                duration += iterator.next().getDuration();

            }

        } catch (FileNotFoundException e) {

            tracks = null;
            return false;

        }

        return true;

    }

    @Override
    public String toString() {

        return name + " @ " + directory;

    }

    @Override
    public int hashCode() {

        return directory.hashCode();

    }

    @Override
    public boolean equals(Object obj) {

        if(obj == null) {

            return false;

        }

        if(getClass() != obj.getClass()) {

            return false;

        }

        try {

            return new File(directory).getCanonicalPath().equals(new File(((Playlist) obj).directory).getCanonicalPath());

        } catch (IOException e) {

            e.printStackTrace();
            return directory.equals(((Playlist) obj).directory);

        }

    }
    
    private static String formatHoursMinutesSeconds(int seconds) {

        String hoursStr = String.format("%02d", seconds / 3600);
        String minutesStr = String.format("%02d", (seconds % 3600) / 60);
        String secondsStr = String.format("%02d", (seconds % 3600) % 60);
        return hoursStr + ":" + minutesStr + ":" + secondsStr;

    }

}
