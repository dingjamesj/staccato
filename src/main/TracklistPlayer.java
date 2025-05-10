package main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public abstract class TracklistPlayer {
    
    /**
     * After this amount of seconds from the start of the song, calling rewind() 
     * will start the track again instead of going back to the previous track.
     */
    private static final int GO_TO_PREVIOUS_TIME_LIMIT = 3;

    public static Set<Runnable> switchTrackActions;

    private static MediaPlayer activeMediaPlayer;
    private static AtomicInteger currentTrackNum;
    private static AtomicBoolean isPlaying;
    private static AtomicBoolean isOnRepeat;
    private static List<Track> queuedTracks;

    static {

        //Initialize JavaFX so that it can play audio
        new JFXPanel();
        currentTrackNum = new AtomicInteger(0);
        isPlaying = new AtomicBoolean(false);
        isOnRepeat = new AtomicBoolean(false);
        switchTrackActions = new HashSet<Runnable>();
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
        isPlaying.set(true);

    }

    /**
     * Stops the current media player, <br></br>
     * Runs the next track actions, <br></br>
     * Tries to increment the current track number if there are more tracks, and if that's the case... <br></br>
     * Plays the music with MediaPlayer and 
     * <b>
     * assigns setOnEndOfMedia to repeat this process for the next track.
     * </b>
     */
    private static void playNextTrack() {

        if(activeMediaPlayer != null) {

            activeMediaPlayer.stop();

        }

        for(Runnable action: switchTrackActions) {

            action.run();

        }

        if(currentTrackNum.incrementAndGet() >= queuedTracks.size()) {

            if(!isOnRepeat.get()) {

                //isPlaying remains false here
                return;

            }
            
            currentTrackNum.set(currentTrackNum.get() % queuedTracks.size());

        }

        Media nextMedia = new Media(new File(queuedTracks.get(currentTrackNum.get()).getFileLocation()).toURI().toString());
        activeMediaPlayer = new MediaPlayer(nextMedia);
        activeMediaPlayer.setOnEndOfMedia(TracklistPlayer::playNextTrack);

        activeMediaPlayer.play();
        isPlaying.set(true);

    }

    /**
     * Pauses audio playback
     */
    public static void pausePlayback() {

        if(activeMediaPlayer == null) {

            return;

        }

        activeMediaPlayer.pause();
        isPlaying.set(false);

    }

    /**
     * Resumes audio playback
     */
    public static void resumePlayback() {

        if(activeMediaPlayer == null) {

            return;

        }

        activeMediaPlayer.play();
        isPlaying.set(true);

    }

    public static void skipTrack() {

        if(activeMediaPlayer == null) {

            return;
            
        }

        playNextTrack();

    }

    /**
     * If is called <code>GO_TO_PREVIOUS_TIME_LIMIT</code> amount of seconds after the start of the track, this will restart the track. <br></br>
     * Otherwise, it will go to the previous track.
     */
    public static void rewindTrack() {

        if(activeMediaPlayer == null) {

            return;

        }

        if(activeMediaPlayer.getCurrentTime().toSeconds() > GO_TO_PREVIOUS_TIME_LIMIT) {

            activeMediaPlayer.seek(Duration.millis(0));

        } else if(currentTrackNum.get() == 0) {

            if(!isOnRepeat.get()) {

                activeMediaPlayer.seek(Duration.millis(0));

            } else {

                currentTrackNum.set(queuedTracks.size() - 2);
                playNextTrack();

            }

        } else {

            currentTrackNum.addAndGet(-2);
            playNextTrack();

        }

    }

    public static void setIsOnRepeat(boolean isOnRepeat) {

        TracklistPlayer.isOnRepeat.set(isOnRepeat);

    }

    public static void addSwitchTrackAction(Runnable action) {

        switchTrackActions.add(action);

    }

    public static void removeSwitchTrackAction(Runnable action) {

        switchTrackActions.remove(action);

    }

    public static void clearSwitchTrackActions() {

        switchTrackActions.clear();

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

    /**
     * The active media player's current time divided by the track's total duration.
     * @return How much of the current track has been played, as a percent of the total track duration.
     */
    public static double getProgressProportion() {

        if(activeMediaPlayer == null) {

            return 0;

        }

        return activeMediaPlayer.getCurrentTime().toMillis() / activeMediaPlayer.getTotalDuration().toMillis();

    }

    /**
     * @return True if the player is playing something, false otherwise.
     */
    public static boolean isPlaying() {

        return isPlaying.get();

    }

    /**
     * @return True if the player is on repeat, false otherwise.
     */
    public static boolean isOnRepeat() {

        return isOnRepeat.get();

    }

}
