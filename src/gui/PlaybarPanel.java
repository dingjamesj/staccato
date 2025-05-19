package gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.ui.FlatSliderUI;
import com.formdev.flatlaf.util.UIScale;

import main.TracklistPlayer;
import net.miginfocom.swing.MigLayout;

public class PlaybarPanel extends JPanel {

	private static final Font TIME_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final ImageIcon PLAY_ICON = GUIUtil.createImageIcon("src/main/resources/play.png");
    private static final ImageIcon PAUSE_ICON = GUIUtil.createImageIcon("src/main/resources/pause.png");
    private static final ImageIcon SKIP_ICON = GUIUtil.createImageIcon("src/main/resources/skip.png");
    private static final ImageIcon GO_BACK_ICON = GUIUtil.createImageIcon("src/main/resources/go back.png");
    
    //GUI spacing constants
    private static final int PROGRESS_BAR_MAX_VALUE = 1000;
    private static final int TIMESTAMP_LABEL_WIDTH = 100;

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
        timeRemainingLabel.setHorizontalAlignment(JLabel.RIGHT);
        playPauseButton.setEnabled(false);
        goBackButton.setEnabled(false);
        skipButton.setEnabled(false);
        progressSlider.setEnabled(false);
        progressSlider.setAlignmentX(CENTER_ALIGNMENT);
        //Only show the slider's thumb button when the slider is being pressed on
        progressSlider.setUI(new FlatSliderUI() {

            @Override
            public void paintThumb(Graphics g) {

                if(isProgressSliderBeingPressed && progressSlider.isEnabled()) {

                    super.paintThumb(g);

                }

                //Otherwise do nothing

            }

            @Override
            public void paintTrack(Graphics g) {

                //Copied from FlatSliderUI.class
                //Changed made were:
                // - Colored track paints after background track
                // - Background track is painted from beginning of component to end of component
                boolean enabled = slider.isEnabled();
                float tw = UIScale.scale( (float) trackWidth );
                float arc = tw;

                RoundRectangle2D coloredTrack = null;
                RoundRectangle2D track;
                if( slider.getOrientation() == JSlider.HORIZONTAL ) {
                    float y = trackRect.y + (trackRect.height - tw) / 2f;
                    if( enabled && isRoundThumb() ) {
                        if( slider.getComponentOrientation().isLeftToRight() ) {
                            int cw = thumbRect.x + (thumbRect.width / 2) - trackRect.x;
                            coloredTrack = new RoundRectangle2D.Float( trackRect.x, y, cw, tw, arc, arc );
                            track = new RoundRectangle2D.Float( trackRect.x, y, trackRect.width, tw, arc, arc );
                        } else {
                            int cw = trackRect.x + trackRect.width - thumbRect.x - (thumbRect.width / 2);
                            coloredTrack = new RoundRectangle2D.Float( trackRect.x + trackRect.width - cw, y, cw, tw, arc, arc );
                            track = new RoundRectangle2D.Float( trackRect.x, y, trackRect.width - cw, tw, arc, arc );
                        }
                    } else
                        track = new RoundRectangle2D.Float( trackRect.x, y, trackRect.width, tw, arc, arc );
                } else {
                    float x = trackRect.x + (trackRect.width - tw) / 2f;
                    if( enabled && isRoundThumb() ) {
                        int ch = thumbRect.y + (thumbRect.height / 2) - trackRect.y;
                        track = new RoundRectangle2D.Float( x, trackRect.y, tw, ch, arc, arc );
                        coloredTrack = new RoundRectangle2D.Float( x, trackRect.y + ch, tw, trackRect.height - ch, arc, arc );
                    } else
                        track = new RoundRectangle2D.Float( x, trackRect.y, tw, trackRect.height, arc, arc );
                }

                g.setColor( enabled ? getTrackColor() : disabledTrackColor );
                ((Graphics2D)g).fill( track );

                if( coloredTrack != null ) {
                    if( slider.getInverted() ) {
                        RoundRectangle2D temp = track;
                        track = coloredTrack;
                        coloredTrack = temp;
                    }

                    g.setColor( getTrackValueColor() );
                    ((Graphics2D)g).fill( coloredTrack );
                }

            }

        });

        // progressSlider.setOpaque(true);
        // progressSlider.setBackground(Color.red);
        // setBackground(Color.cyan);

        add(timeElapsedLabel, "cell 0 0, span 1 1, pushx, pushy, align left bottom, gapleft 9" + ", wmin " + TIMESTAMP_LABEL_WIDTH + ", wmax " + TIMESTAMP_LABEL_WIDTH);
        add(goBackButton, "cell 1 0, span 1 1, pushy, align center bottom");
        add(playPauseButton, "cell 2 0, span 1 1, pushy, align center bottom");
        add(skipButton, "cell 3 0, span 1 1, pushy, align center bottom");
        add(timeRemainingLabel, "cell 4 0, span 1 1, pushx, pushy, align right bottom, gapright 9" + ", wmin " + TIMESTAMP_LABEL_WIDTH + ", wmax " + TIMESTAMP_LABEL_WIDTH);
        add(progressSlider, "cell 0 1, span 5 1, pushx, align center center, growx");

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

            TracklistPlayer.playNextTrack();
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