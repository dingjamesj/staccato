package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import main.Track;
import main.TracklistPlayer;
import net.miginfocom.swing.MigLayout;

public class QueuePanel extends JPanel {
    
    private static final Font PANEL_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 30);
    private static final Font TRACK_NUMBER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font TRACK_TITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);

    private static final int SCROLL_SPEED = 11;
    private static final int ROW_SPACING = 3;
    private static final double NUMBER_COLUMN_WIDTH_PROPORTION = 0.12;
    private static final double TITLE_COLUMN_WIDTH_PROPORTION = 0.6;
    private static final double QUEUED_TRACKS_PANEL_HEIGHT_PROPORTION = 0.9;
    private static final String PANEL_TITLE_LABEL_INSETS = "16 0 4 0";

    private JPanel tracklistPanel;
    private final AtomicInteger currentTrackNum = new AtomicInteger(0);

    public static QueuePanel queuePanel;

    static {

        TracklistPlayer.addSwitchTrackAction(() -> {

            queuePanel.updateQueue();

        });

    }

    public QueuePanel() {

        setLayout(new MigLayout(
            "insets 0 0 0 0",
            "",
            "[" + (int) ((1.0 - QUEUED_TRACKS_PANEL_HEIGHT_PROPORTION) * 100) + "%][" + (int) (QUEUED_TRACKS_PANEL_HEIGHT_PROPORTION * 100) + "%]"
        ));

        JLabel panelTitleLabel = new JLabel("Queue");
        tracklistPanel = new JPanel();
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        
        panelTitleLabel.setFont(PANEL_TITLE_FONT);
        tracklistPanel.setLayout(new BoxLayout(tracklistPanel, BoxLayout.Y_AXIS));
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);

        wrapperPanel.add(tracklistPanel, BorderLayout.NORTH);

        add(panelTitleLabel, "cell 0 0, span 1 1, grow, pushx, align left center, hmax " + (int) ((1.0 - QUEUED_TRACKS_PANEL_HEIGHT_PROPORTION) * 100) + "%, pad " + PANEL_TITLE_LABEL_INSETS);
        add(scrollPane, "cell 0 1, span 1 1, grow, push, align center center, hmax " + (int) (QUEUED_TRACKS_PANEL_HEIGHT_PROPORTION * 100) + "%");

        queuePanel = this;

    }

    /**
     * If the current track number is one BEHIND the TracklistPlayer, then just remove the first track in the queue GUI. <br></br>
     * If the current track number is one AHEAD the TracklistPlayer, then add the currently playing track to the front of the queue GUI. <br></br>
     * If the current track number is MORE THAN ONE ahead/behind the TracklistPlayer, then just reset the queue GUI.
     */
    private synchronized void updateQueue() {

        if(currentTrackNum.get() == TracklistPlayer.getCurrentlyPlayingTrackNumber() - 1) {

            currentTrackNum.incrementAndGet();
            removeFirstTrack();

        } else if(currentTrackNum.get() == TracklistPlayer.getCurrentlyPlayingTrackNumber() + 1) {

            currentTrackNum.decrementAndGet();
            addTrackToFront(TracklistPlayer.getCurrentlyPlayingTrack());

        } else {

            currentTrackNum.set(TracklistPlayer.getCurrentlyPlayingTrackNumber());
            setTracksInQueue();

        }

    }

    public synchronized void setTracksInQueue() {

        tracklistPanel.removeAll();

        Track[] tracks = TracklistPlayer.getQueue();
        if(tracks == null) {

            return;

        }

        for(int i = 0; i < tracks.length; i++) {

            tracklistPanel.add(createQueueEntryPanel(tracks[i], i));

        }

        tracklistPanel.revalidate();
        tracklistPanel.repaint();

    }

    private synchronized void removeFirstTrack() {

        try {

            tracklistPanel.remove(0);

        } catch(ArrayIndexOutOfBoundsException e) {

            //Do nothing

        }


        revalidate();
        repaint();

    }

    private synchronized void addTrackToFront(Track track) {

        tracklistPanel.add(createQueueEntryPanel(track, currentTrackNum.get()), 0);

        revalidate();
        repaint();

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

        return trackPanel;

    }

}
