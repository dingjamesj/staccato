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
    /**
     * The number of milliseconds between each time playbackUpdateActions are run
     */
    private static final int PLAYBACK_UPDATE_LOOP_PERIOD = 250;

    public static Set<Runnable> startTrackActions = new HashSet<Runnable>();
    public static Set<Runnable> switchTrackActions = new HashSet<Runnable>();
    public static Set<Runnable> playbackUpdateActions = new HashSet<Runnable>();
    public static Set<Runnable> endTrackActions = new HashSet<Runnable>();

    private static MediaPlayer activeMediaPlayer;
    private static final AtomicInteger currentTrackNum = new AtomicInteger(0);
    private static final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private static final AtomicBoolean loopIsOn = new AtomicBoolean(false);
    private static final AtomicBoolean shuffleIsOn = new AtomicBoolean(false);
    private static List<Track> queuedTracks = new ArrayList<Track>();
    private static Thread playbackUpdateThread;

    static {

        //Initialize JavaFX so that it can play audio
        new JFXPanel();

    }

    /**
     * Puts all the tracks in a queue and plays them in the given order <b><i>from the start.</b></i>
     * @param tracks Array of tracks to be played in order
     * @return A List of the tracks successfully put in the queue
     */
    public synchronized static void playTracks(Track[] tracks) {

        //Simply forward it to the original playTracks method.
        //By passing -1, we tell playTracks that if shuffle is on, they can start with any track.
        if(shuffleIsOn.get()) {

            playTracks(tracks, -1);

        } else {

            playTracks(tracks, 0);

        }

    }

    /**
     * Puts all the tracks in a queue and plays them in the given order from the given track number.
     * @param tracks Array of tracks to be played in order
     * @param startingTrackIndex The track number to start playback on. If < 0 and shuffle is on, playback starts with any track.
     * @return A List of the tracks successfully put in the queue
     */
    public synchronized static void playTracks(Track[] tracks, int startingTrackIndex) {

        //Stop playback and clear queue
        queuedTracks.clear();
        if(activeMediaPlayer != null) {

            activeMediaPlayer.stop();

        }

        if(tracks == null || tracks.length == 0) {

            return;

        }

        //Shuffle the tracklist if necessary.
        //Note that if the starting track index >= 0, then the shuffled tracklist will still begin at the specified starting track.
        Track[] orderedTracks;
        if(shuffleIsOn.get()) {

            currentTrackNum.set(0);

            orderedTracks = new Track[tracks.length];
            for(int i = 0; i < tracks.length; i++) {

                orderedTracks[i] = tracks[i];

            }

            if(startingTrackIndex >= 0) {

                //If we are starting at a specific track...
                Track.shuffleTracklist(orderedTracks, tracks[startingTrackIndex]);

            } else {

                Track.shuffleTracklist(orderedTracks);

            }

        } else {

            currentTrackNum.set(Math.max(startingTrackIndex, 0));

            orderedTracks = tracks;

        }

        //Queue and play the tracks
        for(int i = 0; i < orderedTracks.length; i++) {

            Track track = orderedTracks[i];

            //First see if we can access the track file
            if(!track.canRead()) {

                continue;

            }
            
            queuedTracks.add(track);

        }

        Media media = new Media(new File(queuedTracks.get(currentTrackNum.get()).getFileLocation()).toURI().toString());
        activeMediaPlayer = new MediaPlayer(media);
        activeMediaPlayer.setOnEndOfMedia(TracklistPlayer::playNextTrack);
        activeMediaPlayer.play();
        isPlaying.set(true);
        startPlaybackUpdateThread();

        for(Runnable action: startTrackActions) {

            action.run();

        }

    }

    /**
     * Calls 
     * <code>
     * advanceTrackQueue(1);
     * </code>
     */
    public synchronized static void playNextTrack() {

        advanceTrackQueue(1);

    }

    /**
     * Advances the queue by <code>numAdvance</code>. <br></br>
     * If the queue goes out of bounds and if looping is on, then the queue wraps back from the beginning. <br></br>
     * Otherwise, playback stops.
     */
    public synchronized static void advanceTrackQueue(int numAdvance) {

        int newTrackNum = currentTrackNum.get() + numAdvance;
        //Wrap the track number if it went over the queue size and if looping is on
        if(newTrackNum >= queuedTracks.size()) {

            if(!loopIsOn.get()) {

                isPlaying.set(false);
                for(Runnable action: endTrackActions) {

                    action.run();

                }
                
                return;

            }
            
            newTrackNum %= queuedTracks.size();

        } else if(newTrackNum < 0) {

            if(!loopIsOn.get()) {

                newTrackNum = 0;

            } else {

                while(newTrackNum < 0) {

                    newTrackNum += queuedTracks.size();

                }

            }

        }

        skipToTrack(newTrackNum);

    }

    /**
     * Stops the current media player, <br></br>
     * Runs the next track actions, <br></br>
     * Skips to the specified track index, <br></br>
     * And plays the music with MediaPlayer and 
     * <b>
     * assigns setOnEndOfMedia to repeat this process for the next track.
     * </b>
     * @param queueIndex
     */
    public synchronized static void skipToTrack(int queueIndex) {

        if(activeMediaPlayer != null) {

            activeMediaPlayer.stop();

        }

        currentTrackNum.set(queueIndex);

        Media nextMedia = new Media(new File(queuedTracks.get(queueIndex).getFileLocation()).toURI().toString());
        activeMediaPlayer = new MediaPlayer(nextMedia);
        activeMediaPlayer.setOnEndOfMedia(TracklistPlayer::playNextTrack);

        activeMediaPlayer.play();
        isPlaying.set(true);
        startPlaybackUpdateThread();

        for(Runnable action: switchTrackActions) {

            action.run();

        }

    }

    /**
     * Pauses audio playback
     */
    public synchronized static void pausePlayback() {

        if(activeMediaPlayer == null) {

            return;

        }

        activeMediaPlayer.pause();
        isPlaying.set(false);

    }

    /**
     * Resumes audio playback
     */
    public synchronized static void resumePlayback() {

        if(activeMediaPlayer == null) {

            return;

        }

        activeMediaPlayer.play();
        isPlaying.set(true);
        startPlaybackUpdateThread();

    }

    /**
     * If is called <code>GO_TO_PREVIOUS_TIME_LIMIT</code> amount of seconds after the start of the track, this will restart the track. <br></br>
     * Otherwise, it will go to the previous track.
     */
    public synchronized static void rewindTrack() {

        if(activeMediaPlayer == null) {

            return;

        }

        if(activeMediaPlayer.getCurrentTime().toSeconds() > GO_TO_PREVIOUS_TIME_LIMIT) {

            activeMediaPlayer.seek(Duration.millis(0));

        } else {

            advanceTrackQueue(-1);

        }

    }

    public synchronized static void seekTrack(int milliseconds) {

        if(activeMediaPlayer == null) {

            return;

        }

        if(
            activeMediaPlayer.getStatus() != MediaPlayer.Status.UNKNOWN && 
            activeMediaPlayer.getTotalDuration().greaterThanOrEqualTo(Duration.millis(milliseconds))
        ) {

            activeMediaPlayer.seek(Duration.millis(milliseconds));

        }

    }

    public static void setIsLooping(boolean isLooping) {

        loopIsOn.set(isLooping);

    }

    public static void setShuffleMode(boolean isShuffling) {

        shuffleIsOn.set(isShuffling);

    }

    public static void addSwitchTrackAction(Runnable action) {

        switchTrackActions.add(action);

    }

    public static void clearSwitchTrackActions() {

        switchTrackActions.clear();

    }

    public static void addStartTrackAction(Runnable action) {

        startTrackActions.add(action);

    }

    public static void clearStartTrackActions() {

        startTrackActions.clear();

    }

    public static void addEndTrackActions(Runnable action) {

        endTrackActions.add(action);

    }

    public static void clearEndTrackActions() {

        endTrackActions.clear();

    }

    public static void addPlaybackUpdateAction(Runnable action) {

        playbackUpdateActions.add(action);

    }

    public static void clearPlaybackUpdateActions() {

        playbackUpdateActions.clear();

    }

    public static int getCurrentTrackNumber() {

        return currentTrackNum.get();

    }

    public static Track getCurrentTrack() {

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
    public static double getCurrentTrackTimeProportion() {

        if(activeMediaPlayer == null) {

            return 0;

        }

        return activeMediaPlayer.getCurrentTime().toMillis() / activeMediaPlayer.getTotalDuration().toMillis();

    }

    /**
     * The currently playing track's progress in seconds
     * @return The active media player's current time
     */
    public static int getCurrentTrackTime() {

        if(activeMediaPlayer == null) {

            return 0;

        }

        return (int) activeMediaPlayer.getCurrentTime().toSeconds();

    }

    /**
     * The currently playing track's total duration in seconds
     * @return
     */
    public static int getCurrentTrackTotalDuration() {

        if(activeMediaPlayer == null) {

            return 0;

        }

        return (int) activeMediaPlayer.getTotalDuration().toSeconds();

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
    public static boolean isLooping() {

        return loopIsOn.get();

    }

    /**
     * @return True if shuffle is on, false otherwise.
     */
    public static boolean isShuffleOn() {

        return shuffleIsOn.get();

    }

    /**
     * Starts the playback update thread.
     */
    private static void startPlaybackUpdateThread() {

        if(playbackUpdateThread != null && playbackUpdateThread.isAlive()) {

            return;

        }

        playbackUpdateThread = new Thread(() -> {

            while(isPlaying.get()) {

                for(Runnable action: playbackUpdateActions) {

                    action.run();

                }

                try {

                    Thread.sleep(PLAYBACK_UPDATE_LOOP_PERIOD);

                } catch (InterruptedException e) {

                    //Do nothing

                }

            }

        });

        playbackUpdateThread.start();

    }

}
