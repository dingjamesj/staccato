package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public abstract class TracklistPlayer {
    
    public static ArrayList<Runnable> nextTrackActions;

    private static MediaPlayer activeMediaPlayer;
    private static Thread currentPlaybackThread;
    private static boolean isPlaying = false;
    private static Track currentlyPlayingTrack;

    static {

        //Initialize JavaFX so that it can play audio
        new JFXPanel();
        nextTrackActions = new ArrayList<Runnable>();

    }

    /**
     * Puts all the tracks in a queue and plays them in the given order.
     * @param tracks Array of tracks to be played in order
     * @return A List of the tracks successfully put in the queue
     */
    public static List<Track> playTracks(Track... tracks) {

        //Stop playback
        if(activeMediaPlayer != null) {

            activeMediaPlayer.stop();

        }

        if(tracks == null || tracks.length == 0) {

            return new ArrayList<Track>(0);

        }

        //Keeping track of 
        List<Track> queuedTracks = new ArrayList<Track>();
        MediaPlayer prevMediaPlayer = null;
        for(int i = 0; i < tracks.length; i++) {

            Track track = tracks[i];

            //First see if we can access the track file
            if(!track.canRead()) {

                continue;

            }
            
            queuedTracks.add(track);

            Media media = new Media(new File(track.getFileLocation()).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            //If this isn't the first track, then make it play after the previous track ends.
            //If this is the first track, then assign it to activeMediaPlayer, which will be played right after this for-loop.
            if(prevMediaPlayer != null) {

                prevMediaPlayer.setOnEndOfMedia(() -> {

                    mediaPlayer.play();
                    for(int a = 0; a < nextTrackActions.size(); a++) {

                        nextTrackActions.get(a).run();

                    }

                });

            } else {

                activeMediaPlayer = mediaPlayer;

            }
            
            mediaPlayer.setOnPlaying(() -> {

                activeMediaPlayer = mediaPlayer;
                currentlyPlayingTrack = track;

            });

            prevMediaPlayer = mediaPlayer;

        }

        activeMediaPlayer.play();
        isPlaying = true;

        return queuedTracks;

    }

    /**
     * Pauses audio playback
     */
    public static void pausePlayback() {

        if(activeMediaPlayer == null) {

            return;

        }

        activeMediaPlayer.pause();
        isPlaying = false;

    }

    /**
     * Resumes audio playback
     */
    public static void resumePlayback() {

        if(activeMediaPlayer == null) {

            return;

        }

        activeMediaPlayer.play();
        isPlaying = true;

    }

    /**
     * Stops audio playback and kills the current playback thread
     */
    public static void stopPlayback() {

        if(activeMediaPlayer != null) {

            activeMediaPlayer.stop();
            activeMediaPlayer = null;

        }

        // killCurrentPlaybackThread();
        isPlaying = false;

    }

    public static void skipTrack() {



    }

    public static boolean isPlaying() {

        return isPlaying;

    }

    public static Track getCurrentlyPlayingTrack() {

        return currentlyPlayingTrack;

    }

}
