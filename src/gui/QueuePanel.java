package gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
    private static final Insets PANEL_TITLE_LABEL_INSETS = new Insets(16, 0, 4, 0);

    private JPanel tracklistPanel;
    private final AtomicInteger currentTrackNum = new AtomicInteger(0);

    public static QueuePanel queuePanel;

    static {

        TracklistPlayer.addSwitchTrackAction(() -> {

            queuePanel.updateQueue();

        });

    }

    public QueuePanel() {

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel panelTitleLabel = new JLabel("Queue");
        tracklistPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(tracklistPanel);

        panelTitleLabel.setFont(PANEL_TITLE_FONT);
        tracklistPanel.setLayout(new BoxLayout(tracklistPanel, BoxLayout.Y_AXIS));
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.insets = PANEL_TITLE_LABEL_INSETS;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.BOTH;
        add(panelTitleLabel, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        add(scrollPane, constraints);

        QueuePanel.queuePanel = this;

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
