package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class PlaybarPanel extends JPanel {

    private static final Font TIME_FONT = new Font("Segoe UI", Font.BOLD, 45);
    private static final ImageIcon PLAY_ICON = createImageIcon("src/main/resources/play.png");
    private static final ImageIcon PAUSE_ICON = createImageIcon("src/main/resources/pause.png");
    private static final ImageIcon SKIP_ICON = createImageIcon("src/main/resources/skip.png");
    private static final ImageIcon GO_BACK_ICON = createImageIcon("src/main/resources/go back.png");

    private JLabel timeElapsedLabel;
    private JLabel timeRemainingLabel;
    private JProgressBar progressBar;

    public PlaybarPanel() {

        System.out.println(PLAY_ICON == null);
        System.out.println(PAUSE_ICON == null);
        System.out.println(SKIP_ICON == null);
        System.out.println(GO_BACK_ICON == null);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //-----BEGIN PLAY/PAUSE, SKIP/GO-BACK BUTTONS PANEL-----

        JPanel playbackButtonsPanel = new JPanel();
        JButton goBackButton = new JButton(GO_BACK_ICON);
        JButton playPauseButton = new JButton(PLAY_ICON);
        JButton skipButton = new JButton(SKIP_ICON);

        playbackButtonsPanel.setLayout(new BoxLayout(playbackButtonsPanel, BoxLayout.X_AXIS));
        goBackButton.setPreferredSize(new Dimension(45, 45));
        playPauseButton.setPreferredSize(new Dimension(45, 45));
        skipButton.setPreferredSize(new Dimension(45, 45));

        playbackButtonsPanel.add(goBackButton);
        playbackButtonsPanel.add(Box.createHorizontalStrut(20));
        playbackButtonsPanel.add(playPauseButton);
        playbackButtonsPanel.add(Box.createHorizontalStrut(20));
        playbackButtonsPanel.add(skipButton);
        add(playbackButtonsPanel);
        add(Box.createVerticalStrut(10));

        //------END PLAY/PAUSE, SKIP/GO-BACK BUTTONS PANEL------

        //-----BEGIN PROGRESS BAR PANEL-----

        JPanel progressbarPanel = new JPanel();
        progressbarPanel.setLayout(new BoxLayout(progressbarPanel, BoxLayout.Y_AXIS));

        //Time panel
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.X_AXIS));
        timeElapsedLabel = new JLabel("0:00");
        timeRemainingLabel = new JLabel("-:--");
        timePanel.add(timeElapsedLabel);
        timePanel.add(Box.createHorizontalGlue());
        timePanel.add(timeRemainingLabel);
        progressbarPanel.add(timePanel);

        progressBar = new JProgressBar();
        progressBar.setAlignmentX(CENTER_ALIGNMENT);
        progressbarPanel.add(progressBar);
        
        add(progressbarPanel);

        //------END PROGRESS BAR PANEL------

    }

    private static ImageIcon createImageIcon(String urlStr) {

        URL url = PlaybarPanel.class.getResource(urlStr);
        if(url != null) {

            return new ImageIcon(url);

        }

        return new ImageIcon(urlStr);

    }

}