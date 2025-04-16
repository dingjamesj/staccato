package main;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ImageIcon;

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

    //Remember that staccato tracks where files were sourced from and where they are located.
    //Hence we need to store the Track set---each track contains where they're sourced from and where they're located.
    private Set<Track> tracks;
    private String title;
    private String directory;
    private ImageIcon coverArt;
    private int duration = -1;

    public Playlist(String directory) {

        tracks = new HashSet<Track>();
        this.directory = directory;

    }

    public Track[] getTracks() {

        return (Track[]) tracks.toArray();

    }

    public String getTitle() {

        return title;

    }

    public String getDirectory() {

        return directory;

    }

    public ImageIcon getCoverArt() {

        return coverArt;

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

            Iterator<Track> iterator = tracks.iterator();
            duration = 0;
            while(iterator.hasNext()) {

                duration += iterator.next().getDuration();

            }

        }

        return formatHoursMinutesSeconds(duration);

    }

    public int getSize() {

        return tracks.size();

    }
    
    private static String formatHoursMinutesSeconds(int seconds) {

        int hours = seconds /= 3600;
        int minutes = seconds /= 60;
        return hours + " hr, " + minutes + " min, " + seconds + " s";

    }

}
