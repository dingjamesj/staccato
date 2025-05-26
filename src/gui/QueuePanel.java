package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import gui.GUIUtil.HoverableButton;
import main.Track;
import main.TracklistPlayer;
import net.miginfocom.swing.MigLayout;

public class QueuePanel extends JPanel {
    
    private static final Font PANEL_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 30);
    private static final Font TRACK_NUMBER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font TRACK_TITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);

    private static final ImageIcon LOOP_ICON = GUIUtil.createImageIcon("src/main/resources/refresh.png");
    private static final ImageIcon SHUFFLE_ICON = GUIUtil.createImageIcon("src/main/resources/refresh.png");

    private static final int SCROLL_SPEED = 11;
    private static final int ROW_SPACING = 3;
    private static final double NUMBER_COLUMN_WIDTH_PROPORTION = 0.12;
    private static final double TITLE_COLUMN_WIDTH_PROPORTION = 0.6;
    private static final double QUEUED_TRACKS_PANEL_HEIGHT_PROPORTION = 0.925;
    private static final int PANEL_TITLE_WINDOW_EDGE_GAP_PX = 2;
    private static final int QUEUE_OPTIONS_BUTTON_SIZE = 35;
    private static final int TITLE_LABEL_TO_BUTTONS_GAP_PX = 12;
    private static final int BUTTONS_GAP_PX = 0;
    
    private static final Color HIGHLIGHTED_ROW_COLOR = new Color(0x272727);

    private JPanel tracklistPanel;
    private final AtomicInteger currentTrackNum = new AtomicInteger(0);

    public static QueuePanel queuePanel;

    static {

        TracklistPlayer.addSwitchTrackAction(() -> {

            queuePanel.updateQueueGUI();

        });

        TracklistPlayer.addEndTrackActions(() -> {

            queuePanel.tracklistPanel.removeAll();
            queuePanel.tracklistPanel.revalidate();
            queuePanel.tracklistPanel.repaint();

        });

        TracklistPlayer.addStartTrackAction(() -> {

            queuePanel.setTracksInGUI(TracklistPlayer.getCurrentlyPlayingTrackNumber());
            
        });

    }

    public QueuePanel() {

        setLayout(new MigLayout(
            "insets 0 0 0 0",
            "",
            "[" + (int) ((1.0 - QUEUED_TRACKS_PANEL_HEIGHT_PROPORTION) * 100) + "%][" + (int) (QUEUED_TRACKS_PANEL_HEIGHT_PROPORTION * 100) + "%]"
        ));

        JPanel headerPanel = new JPanel();
        JLabel panelTitleLabel = new JLabel("Queue");
        JButton loopButton = new HoverableButton(GUIUtil.createResizedIcon(LOOP_ICON, QUEUE_OPTIONS_BUTTON_SIZE, QUEUE_OPTIONS_BUTTON_SIZE, Image.SCALE_SMOOTH));
        JButton shuffleButton = new HoverableButton(GUIUtil.createResizedIcon(SHUFFLE_ICON, QUEUE_OPTIONS_BUTTON_SIZE, QUEUE_OPTIONS_BUTTON_SIZE, Image.SCALE_SMOOTH));
        tracklistPanel = new JPanel();
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        
        headerPanel.setLayout(new MigLayout("insets 0 0 0 0"));
        panelTitleLabel.setFont(PANEL_TITLE_FONT);
        tracklistPanel.setLayout(new BoxLayout(tracklistPanel, BoxLayout.Y_AXIS));
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);

        headerPanel.add(panelTitleLabel, "cell 0 0, span 1 1, pushy, align left bottom, gapleft " + PANEL_TITLE_WINDOW_EDGE_GAP_PX);
        headerPanel.add(loopButton, "cell 1 0, span 1 1, pushy, gapleft " + BUTTONS_GAP_PX + ", align left bottom");
        headerPanel.add(shuffleButton, "cell 2 0, span 1 1, pushy, gapleft " + BUTTONS_GAP_PX + ", align left bottom");
        wrapperPanel.add(tracklistPanel, BorderLayout.NORTH);
        add(headerPanel, "cell 0 0, span 1 1, grow, push, align center center, hmax " + (int) ((1.0 - QUEUED_TRACKS_PANEL_HEIGHT_PROPORTION) * 100) + "%");
        add(scrollPane, "cell 0 1, span 1 1, grow, push, align center center, hmax " + (int) (QUEUED_TRACKS_PANEL_HEIGHT_PROPORTION * 100) + "%");

        queuePanel = this;

        //--------------------------START ACTION LISTENERS--------------------------

        loopButton.addActionListener((unused) -> {

            TracklistPlayer.setIsLooping(!TracklistPlayer.isLooping());

        });

        shuffleButton.addActionListener((unused) -> {

            if(TracklistPlayer.isShuffleOn()) {

                TracklistPlayer.setShuffleMode(false);

            } else {

                TracklistPlayer.setShuffleMode(true);

            }

        });

        scrollPane.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {

                scrollPane.requestFocusInWindow();

            }

        });

    }

    /**
     * If the current track number is one BEHIND the TracklistPlayer, then just remove the first track in the queue GUI. <br></br>
     * If the current track number is one AHEAD the TracklistPlayer, then add the currently playing track to the front of the queue GUI. <br></br>
     * If the current track number is MORE THAN ONE ahead/behind the TracklistPlayer, then just reset the queue GUI.
     */
    private synchronized void updateQueueGUI() {

        if(currentTrackNum.get() == TracklistPlayer.getCurrentlyPlayingTrackNumber() - 1) {

            currentTrackNum.incrementAndGet();
            removeGUIFirstTrack();

        } else if(currentTrackNum.get() == TracklistPlayer.getCurrentlyPlayingTrackNumber() + 1) {

            currentTrackNum.decrementAndGet();
            addTrackToGUIFront(TracklistPlayer.getCurrentlyPlayingTrack());

        } else {

            currentTrackNum.set(TracklistPlayer.getCurrentlyPlayingTrackNumber());
            setTracksInGUI(currentTrackNum.get());

        }

    }

    public synchronized void setTracksInGUI(int startingTrackIndex) {

        tracklistPanel.removeAll();
        currentTrackNum.set(startingTrackIndex);

        Track[] tracks = TracklistPlayer.getQueue();
        if(tracks == null) {

            return;

        }

        for(int i = startingTrackIndex; i < tracks.length; i++) {

            tracklistPanel.add(createQueueEntryPanel(tracks[i], i));

        }

        tracklistPanel.revalidate();
        tracklistPanel.repaint();

    }

    private synchronized void removeGUIFirstTrack() {

        try {

            tracklistPanel.remove(0);

        } catch(ArrayIndexOutOfBoundsException e) {

            //Do nothing

        }

        tracklistPanel.revalidate();
        tracklistPanel.repaint();

    }

    private synchronized void addTrackToGUIFront(Track track) {

        tracklistPanel.add(createQueueEntryPanel(track, currentTrackNum.get()), 0);

        tracklistPanel.revalidate();
        tracklistPanel.repaint();

    }

    private JPanel createQueueEntryPanel(Track track, int trackNum) {

        JPanel trackPanel = new JPanel(new MigLayout(
            "insets " + ROW_SPACING + " 0 " + ROW_SPACING + " 0",
            "[" + (int) (NUMBER_COLUMN_WIDTH_PROPORTION * 100) + "%][" + (int) (TITLE_COLUMN_WIDTH_PROPORTION * 100) + "%]"
        ));

        JLabel numberLabel = new JLabel("" + (trackNum + 1));
        JLabel titleLabel = new JLabel(track.getTitle() != null && !track.getTitle().isBlank() ? track.getTitle() : "[No Title]");

        numberLabel.setFont(TRACK_NUMBER_FONT);
        titleLabel.setFont(TRACK_TITLE_FONT);

        trackPanel.add(numberLabel, "cell 0 0, align center");
        trackPanel.add(titleLabel, "cell 1 0, pushx, wmax " + (int) (TITLE_COLUMN_WIDTH_PROPORTION * 100) + "%");

        trackPanel.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseEntered(MouseEvent e) {

                trackPanel.setBackground(HIGHLIGHTED_ROW_COLOR);

            }

            @Override
            public void mouseExited(MouseEvent e) {

                trackPanel.setBackground(getBackground());

            }

            @Override
            public void mouseClicked(MouseEvent e) {

                if(e.getClickCount() < 2) {

                    return;

                }

                TracklistPlayer.skipToTrack(trackNum);

            }

        });

        return trackPanel;

    }

}
