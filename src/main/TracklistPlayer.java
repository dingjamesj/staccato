package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public abstract class TracklistPlayer {
    
    public static ArrayList<Runnable> nextTrackActions;

    private static MediaPlayer activeMediaPlayer;
    private static boolean isPlaying = false;
    private static AtomicInteger currentTrackNum;
    private static List<Track> queuedTracks;

    static {

        //Initialize JavaFX so that it can play audio
        new JFXPanel();
        currentTrackNum = new AtomicInteger(0);
        nextTrackActions = new ArrayList<Runnable>();
        queuedTracks = new ArrayList<Track>();

    }

    /**
     * Puts all the tracks in a queue and plays them in the given order.
     * @param tracks Array of tracks to be played in order
     * @return A List of the tracks successfully put in the queue
     */
    public static void playTracks(Track... tracks) {

        //Stop playback and clear queue
        queuedTracks.clear();
        currentTrackNum.set(0);
        if(activeMediaPlayer != null) {

            activeMediaPlayer.stop();

        }

        if(tracks == null || tracks.length == 0) {

            return;

        }

        for(int i = 0; i < tracks.length; i++) {

            Track track = tracks[i];

            //First see if we can access the track file
            if(!track.canRead()) {

                continue;

            }
            
            queuedTracks.add(track);

        }

        Media media = new Media(new File(queuedTracks.get(0).getFileLocation()).toURI().toString());
        activeMediaPlayer = new MediaPlayer(media);
        activeMediaPlayer.setOnEndOfMedia(TracklistPlayer::playNextTrack);
        activeMediaPlayer.play();
        isPlaying = true;

    }

    /**
     * Stops the current media player
     * ... TODO finish documentation for waht this does
     */
    private static void playNextTrack() {

        isPlaying = false;
        if(activeMediaPlayer != null) {

            activeMediaPlayer.stop();

        }

        for(Runnable action: nextTrackActions) {

            action.run();

        }

        if(currentTrackNum.incrementAndGet() >= queuedTracks.size()) {

            //isPlaying remains false
            return;

        }

        Media nextMedia = new Media(new File(queuedTracks.get(currentTrackNum.get()).getFileLocation()).toURI().toString());
        activeMediaPlayer = new MediaPlayer(nextMedia);
        activeMediaPlayer.setOnEndOfMedia(TracklistPlayer::playNextTrack);

        activeMediaPlayer.play();
        isPlaying = true;

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

    public static void skipTrack() {

        

    }

    public static boolean isPlaying() {

        return isPlaying;

    }

    public static Track getCurrentlyPlayingTrack() {

        return queuedTracks.get(currentTrackNum.get());

    }

    public static Track[] getQueue() {

        Track[] trackArray = new Track[queuedTracks.size()];

        for(int i = 0; i < queuedTracks.size(); i++) {

            trackArray[i] = queuedTracks.get(i);

        }

        return trackArray;

    }

}
