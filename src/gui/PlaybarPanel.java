package gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import main.TracklistPlayer;
import net.miginfocom.swing.MigLayout;

public class PlaybarPanel extends JPanel {

	private static final Font TIME_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final ImageIcon PLAY_ICON = GUIUtil.createImageIcon("src/main/resources/play.png");
    private static final ImageIcon PAUSE_ICON = GUIUtil.createImageIcon("src/main/resources/pause.png");
    private static final ImageIcon SKIP_ICON = GUIUtil.createImageIcon("src/main/resources/skip.png");
    private static final ImageIcon GO_BACK_ICON = GUIUtil.createImageIcon("src/main/resources/go back.png");
    
    //GUI spacing constants
    private static final int BUTTONS_TO_PROGRESSBAR_GAP = 0;
    private static final int BUTTONS_SPACING = 9;
    private static final int PROGRESS_BAR_MAX_VALUE = 1000;

    private JLabel timeElapsedLabel;
    private JLabel timeRemainingLabel;
    private JSlider progressSlider;
    private JButton goBackButton;
    private JButton playPauseButton;
    private JButton skipButton;

    private boolean isProgressSliderBeingPressed = false;

    public static PlaybarPanel playbarPanel;

    static {

        TracklistPlayer.addEndTrackActions(() -> {

            playbarPanel.setButtonsEnabled(false);
            playbarPanel.setIsPlaying(false);
            playbarPanel.setProgressSliderValue(0);
            playbarPanel.timeElapsedLabel.setText("-:--");
            playbarPanel.timeRemainingLabel.setText("-:--");

        });

        TracklistPlayer.addPlaybackUpdateAction(() -> {

            if(playbarPanel.isProgressSliderBeingPressed) {

                return;

            }

            SwingUtilities.invokeLater(() -> {

                playbarPanel.timeElapsedLabel.setText(formatMinutesSeconds(TracklistPlayer.getCurrentTrackTime()));
                playbarPanel.timeRemainingLabel.setText("-" + formatMinutesSeconds(TracklistPlayer.getCurrentTrackTotalDuration() - TracklistPlayer.getCurrentTrackTime()));
                playbarPanel.setProgressSliderValue((int) (TracklistPlayer.getCurrentTrackTimeProportion() * PROGRESS_BAR_MAX_VALUE));
                playbarPanel.progressSlider.repaint();
                
            });

        });

    }

    public PlaybarPanel() {

        setLayout(new MigLayout(
            "insets 0 0 0 0"
        ));

        //------------------BEGIN GUI BUILDING------------------

        goBackButton = new JButton(GO_BACK_ICON);
        playPauseButton = new JButton(PLAY_ICON);
        skipButton = new JButton(SKIP_ICON);
        timeElapsedLabel = new JLabel("-:--");
        timeRemainingLabel = new JLabel("-:--");
        progressSlider = new JSlider(JSlider.HORIZONTAL, 0, PROGRESS_BAR_MAX_VALUE, 0);

        timeElapsedLabel.setFont(TIME_FONT);
        timeRemainingLabel.setFont(TIME_FONT);
        progressSlider.setEnabled(false);
        progressSlider.setAlignmentX(CENTER_ALIGNMENT);
        playPauseButton.setEnabled(false);
        goBackButton.setEnabled(false);
        skipButton.setEnabled(false);

        add(timeElapsedLabel, "cell 0 0, span 1 1, pushx, pushy, align left bottom");
        add(goBackButton, "cell 1 0, span 1 1, pushy, align center center");
        add(playPauseButton, "cell 2 0, span 1 1, pushy, align center center, gapleft " + BUTTONS_SPACING);
        add(skipButton, "cell 3 0, span 1 1, pushy, align center center, gapleft " + BUTTONS_SPACING);
        add(timeRemainingLabel, "cell 4 0, span 1 1, pushx, pushy, align right bottom");
        add(progressSlider, "cell 0 1, span 5 1, pushx, align center center, growx, gaptop " + BUTTONS_TO_PROGRESSBAR_GAP);

        //-------------------END GUI BUILDING-------------------

        PlaybarPanel.playbarPanel = this;

        //----------------START ACTION LISTENERS----------------

        playPauseButton.addActionListener((unused) -> {

            if(TracklistPlayer.isPlaying()) {

                TracklistPlayer.pausePlayback();
                playPauseButton.setIcon(PLAY_ICON);

            } else {

                TracklistPlayer.resumePlayback();
                playPauseButton.setIcon(PAUSE_ICON);

            }

        });

        skipButton.addActionListener((unused) -> {

            TracklistPlayer.skipTrack();
            playPauseButton.setIcon(PAUSE_ICON);

        });

        goBackButton.addActionListener((unused) -> {

            TracklistPlayer.rewindTrack();
            playPauseButton.setIcon(PAUSE_ICON);

        });

        progressSlider.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {

                isProgressSliderBeingPressed = true;

            }

            @Override
            public void mouseReleased(MouseEvent e) {

                isProgressSliderBeingPressed = false;
                TracklistPlayer.seekTrack((int) ((progressSlider.getValue() * 1000.0 / PROGRESS_BAR_MAX_VALUE) * TracklistPlayer.getCurrentTrackTotalDuration()));

            }

        });

        //-----------------END ACTION LISTENERS-----------------

    }

    public void setButtonsEnabled(boolean enabled) {

        playPauseButton.setEnabled(enabled);
        goBackButton.setEnabled(enabled);
        skipButton.setEnabled(enabled);
        progressSlider.setEnabled(enabled);

    }

    public void setIsPlaying(boolean isPlaying) {

        if(isPlaying) {

            playPauseButton.setIcon(PAUSE_ICON);

        } else {

            playPauseButton.setIcon(PLAY_ICON);

        }

    }

    private synchronized void setProgressSliderValue(int value) {

        progressSlider.setValue(value);

    }

    private synchronized void incrementProgressSliderValue(int value) {

        progressSlider.setValue(progressSlider.getValue() + value);

    }

    private static String formatMinutesSeconds(int seconds) {

        String minutesStr = String.format("%d", seconds / 60);
        String secondsStr = String.format("%02d", seconds % 60);
        return minutesStr + ":" + secondsStr;

    }

}