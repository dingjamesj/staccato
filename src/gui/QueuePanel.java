package gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import main.Track;
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

    public static QueuePanel queuePanel;

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

    public void setTracksInQueue(List<Track> tracks) {

        tracklistPanel.removeAll();

        if(tracks == null) {

            return;

        }

        for(int i = 0; i < tracks.size(); i++) {

            JPanel trackPanel = new JPanel(new MigLayout(
                "insets " + ROW_SPACING + " 0 " + ROW_SPACING + " 0",
                "[" + (int) (NUMBER_COLUMN_WIDTH_PROPORTION * 100) + "%][" + (int) (TITLE_COLUMN_WIDTH_PROPORTION * 100) + "%]"
            ));

            JLabel numberLabel = new JLabel("" + (i + 1));
            JLabel titleLabel = new JLabel(tracks.get(i).getTitle() != null && !tracks.get(i).getTitle().isBlank() ? tracks.get(i).getTitle() : "[No Title]");

            numberLabel.setFont(TRACK_NUMBER_FONT);
            titleLabel.setFont(TRACK_TITLE_FONT);

            trackPanel.add(numberLabel, "cell 0 0, align center");
            trackPanel.add(titleLabel, "cell 1 0, pushx, wmax " + (int) (TITLE_COLUMN_WIDTH_PROPORTION * 100) + "%");

            tracklistPanel.add(trackPanel);

        }

        tracklistPanel.revalidate();
        tracklistPanel.repaint();

    }

}
