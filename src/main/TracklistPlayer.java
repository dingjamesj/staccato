package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public abstract class TracklistPlayer {
    
    public static ArrayList<Runnable> nextTrackActions;

    private static MediaPlayer mediaPlayer;
    private static Thread currentPlaybackThread;
    private static AtomicBoolean killCurrentPlaybackThreadFlag = new AtomicBoolean(false);
    private static AtomicBoolean isPlaying = new AtomicBoolean(false);

    static {

        //Initialize JavaFX so that it can play audio
        new JFXPanel();

    }

    /**
     * Kills the current track playback thread and starts a new one with this track list.
     * @param tracks
     */
    public static void playTracks(Track... tracks) {

        //Kill the current thread and wait for it to be completely killed.
        killCurrentPlaybackThread();

        //Start a new thread
        currentPlaybackThread = new Thread(() -> {

            if(mediaPlayer != null) {

                mediaPlayer.stop();
                mediaPlayer = null;
    
            }
    
            isPlaying.set(true);
            for(int i = 0; i < tracks.length; i++) {

                try {
    
                    playTrack(tracks[i]);
    
                } catch (FileNotFoundException e) {
                    
                    //Move onto the next track
                    e.printStackTrace();
    
                } finally {
    
                    //Run all the callback functions
                    for(int a = 0; a < nextTrackActions.size(); a++) {
    
                        nextTrackActions.get(a).run();
    
                    }
    
                }

                //Stop executing this thread if the kill flag is on
                if(killCurrentPlaybackThreadFlag.get()) {

                    isPlaying.set(false);
                    return;

                }
    
            }

        });

        currentPlaybackThread.start();

    }

    private static void playTrack(Track track) throws FileNotFoundException {

        //First see if we can access the track file
        if(track.getFileLocation() == null) {

            throw new FileNotFoundException();

        }

        File trackFile = new File(track.getFileLocation());
        if(!trackFile.isFile()) {

            throw new FileNotFoundException();

        }

        try {

            if(!trackFile.canRead()) { //The canRead method can throw a SecurityException

                throw new FileNotFoundException();

            }

        } catch(SecurityException e) {

            throw new FileNotFoundException();

        }
        
        Media media = new Media(trackFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        
        AtomicBoolean trackHasEnded = new AtomicBoolean(false);
        mediaPlayer.setOnEndOfMedia(() -> {

            trackHasEnded.set(true);
            mediaPlayer = null;

        });

        //Wait for the track to finish playing
        while(!trackHasEnded.get() && !killCurrentPlaybackThreadFlag.get()) {}
        
    }

    /**
     * Kills the current playback thread and waits for it to completely end.
     */
    private static void killCurrentPlaybackThread() {

        //Don't execute the rest of the method if there's no thread to kill
        if(currentPlaybackThread == null || !currentPlaybackThread.isAlive()) {

            return;

        }

        killCurrentPlaybackThreadFlag.set(true);
        try {

            currentPlaybackThread.join();

        } catch (InterruptedException e) {
            
            //Do nothing
            e.printStackTrace();

        } finally {

            killCurrentPlaybackThreadFlag.set(false);
            isPlaying.set(false);

        }

    }

    /**
     * Pauses audio playback
     */
    public static void pausePlayback() {

        if(mediaPlayer == null) {

            return;

        }

        mediaPlayer.pause();
        isPlaying.set(false);

    }

    /**
     * Resumes audio playback
     */
    public static void resumePlayback() {

        if(mediaPlayer == null) {

            return;

        }

        mediaPlayer.play();
        isPlaying.set(true);

    }

    /**
     * Stops audio playback and kills the current playback thread
     */
    public static void stopPlayback() {

        if(mediaPlayer != null) {

            mediaPlayer.stop();
            mediaPlayer = null;

        }

        killCurrentPlaybackThread();
        isPlaying.set(false);

    }

    public static boolean isPlaying() {

        return isPlaying.get();

    }

}
