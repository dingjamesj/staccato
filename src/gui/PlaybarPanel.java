package gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import main.TracklistPlayer;

public class PlaybarPanel extends JPanel {

	private static final Font TIME_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final ImageIcon PLAY_ICON = createImageIcon("src/main/resources/play.png");
    private static final ImageIcon PAUSE_ICON = createImageIcon("src/main/resources/pause.png");
    private static final ImageIcon SKIP_ICON = createImageIcon("src/main/resources/skip.png");
    private static final ImageIcon GO_BACK_ICON = createImageIcon("src/main/resources/go back.png");
    
    //GUI spacing constants
    private static final int BUTTONS_TO_PROGRESSBAR_GAP = 9;
    private static final int BUTTONS_SPACING = 9;

    private JLabel timeElapsedLabel;
    private JLabel timeRemainingLabel;
    private JProgressBar progressBar;
    private JButton goBackButton;
    private JButton playPauseButton;
    private JButton skipButton;

    public static PlaybarPanel playbarPanel;

    public PlaybarPanel() {

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        //------------------BEGIN GUI BUILDING------------------

        goBackButton = new JButton(GO_BACK_ICON);
        playPauseButton = new JButton(PLAY_ICON);
        skipButton = new JButton(SKIP_ICON);
        timeElapsedLabel = new JLabel("0:00");
        timeRemainingLabel = new JLabel("-:--");
        progressBar = new JProgressBar();

        timeElapsedLabel.setFont(TIME_FONT);
        timeRemainingLabel.setFont(TIME_FONT);
        progressBar.setAlignmentX(CENTER_ALIGNMENT);
        progressBar.putClientProperty("JProgressBar.largeHeight", true);
        playPauseButton.setEnabled(false);
        goBackButton.setEnabled(false);
        skipButton.setEnabled(false);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        constraints.fill = GridBagConstraints.NONE;
        add(timeElapsedLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 0, 0, BUTTONS_SPACING);
        add(goBackButton, constraints);
        constraints.gridx = 2;
        add(playPauseButton, constraints);
        constraints.gridx = 3;
        constraints.insets = new Insets(0, 0, 0, 0);
        add(skipButton, constraints);

        constraints.gridx = 4;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.LAST_LINE_END;
        constraints.fill = GridBagConstraints.NONE;
        add(timeRemainingLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 5;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(BUTTONS_TO_PROGRESSBAR_GAP, 0, 0, 0);
        add(progressBar, constraints);

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

        //-----------------END ACTION LISTENERS-----------------

    }

    public void setButtonsEnabled(boolean enabled) {

        playPauseButton.setEnabled(enabled);
        goBackButton.setEnabled(enabled);
        skipButton.setEnabled(enabled);

    }

    public void setIsPlaying(boolean isPlaying) {

        if(isPlaying) {

            playPauseButton.setIcon(PAUSE_ICON);

        } else {

            playPauseButton.setIcon(PLAY_ICON);

        }

    }

    private static ImageIcon createImageIcon(String urlStr) {

        URL url = PlaybarPanel.class.getResource(urlStr);
        if(url != null) {

            return new ImageIcon(url);

        }

        return new ImageIcon(urlStr);

    }

}