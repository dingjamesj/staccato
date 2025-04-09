package gui;

import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class PlaybarPanel extends JPanel {

    public static final Font TIME_FONT = new Font("Segoe UI", Font.BOLD, 45);

    private JLabel timeElapsedLabel;
    private JLabel timeRemainingLabel;
    private JProgressBar progressBar;

    public PlaybarPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //-----BEGIN PLAY/PAUSE, SKIP/GO-BACK BUTTONS PANEL-----

        JPanel playbackButtonsPanel = new JPanel();
        playbackButtonsPanel.setLayout(new BoxLayout(playbackButtonsPanel, BoxLayout.X_AXIS));
        JButton goBackButton = new JButton();
        JButton playPauseButton = new JButton();
        JButton skipButton = new JButton();
        playbackButtonsPanel.add(goBackButton);
        playbackButtonsPanel.add(playPauseButton);
        playbackButtonsPanel.add(skipButton);

        add(playbackButtonsPanel);

        //------END PLAY/PAUSE, SKIP/GO-BACK BUTTONS PANEL------

        //-----BEGIN PROGRESS BAR PANEL-----

        JPanel progressbarPanel = new JPanel();
        progressbarPanel.setLayout(new BoxLayout(progressbarPanel, BoxLayout.Y_AXIS));

        //Time panel
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.X_AXIS));
        timeElapsedLabel = new JLabel("0:00");
        timeRemainingLabel = new JLabel("-:--");
        timePanel.add(Box.createHorizontalGlue());
        timePanel.add(timeElapsedLabel);
        timePanel.add(timeRemainingLabel);
        timePanel.add(Box.createHorizontalGlue());
        progressbarPanel.add(timePanel);

        progressBar = new JProgressBar();
        progressBar.setAlignmentX(CENTER_ALIGNMENT);
        progressbarPanel.add(progressBar);
        
        add(progressbarPanel);

        //------END PROGRESS BAR PANEL------

    }

}