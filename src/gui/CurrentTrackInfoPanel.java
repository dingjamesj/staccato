package gui;

import java.awt.Font;
import java.awt.Image;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import main.Track;
import main.TracklistPlayer;
import net.miginfocom.swing.MigLayout;

import gui.GUIUtil.InvisibleScrollPane;
import gui.GUIUtil.HoverableButton;

public class CurrentTrackInfoPanel extends JPanel {

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font ARTISTS_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font ALBUM_FONT = new Font("Segoe UI", Font.ITALIC, 18);

    private static final ImageIcon EDIT_METADATA_ICON = GUIUtil.createImageIcon("src/main/resources/edit.png");
    private static final ImageIcon REDOWNLOAD_ICON = GUIUtil.createImageIcon("src/main/resources/resync.png");
    private static final ImageIcon PLACEHOLDER_ART_ICON = GUIUtil.createImageIcon("src/main/resources/placeholder art.png");

    private static final int GUI_TO_WINDOW_TOP_GAP = 70;
    private static final int GUI_GAP = 10;
    private static final int GUI_SECTIONS_GAP = 45;
    private static final int ALBUM_ART_ICON_SIZE_PX = 256;
    private static final int TRACK_OPTIONS_BUTTON_SIZE_PX = 60;
    private static final int TRACK_OPTIONS_BUTTON_SPACING = 38;

    private JScrollPane[] trackInfoScrollPanes;
    private JLabel titleLabel;
    private JLabel albumArtLabel;
    private JLabel artistsLabel;
    private JLabel albumLabel;
    private JPanel buttonsPanel;

    private Track track;

    public static CurrentTrackInfoPanel currentTrackInfoPanel;

    static {

        Runnable updateTrackInfoAction = () -> {

            currentTrackInfoPanel.setTrack(TracklistPlayer.getCurrentlyPlayingTrack());

        };

        TracklistPlayer.addSwitchTrackAction(updateTrackInfoAction);
        TracklistPlayer.addStartTrackAction(updateTrackInfoAction);

        TracklistPlayer.addEndTrackActions(() -> {

            currentTrackInfoPanel.clearTrackInfo();

        });

    }

    public CurrentTrackInfoPanel() {

        setLayout(new MigLayout(
            "insets 0 0 0 0",
            "",
            "[]"
        ));

        titleLabel = new JLabel();
        albumArtLabel = new JLabel();
        artistsLabel = new JLabel();
        albumLabel = new JLabel();
        JButton editMetadataButton = new HoverableButton(GUIUtil.createResizedIcon(EDIT_METADATA_ICON, TRACK_OPTIONS_BUTTON_SIZE_PX, TRACK_OPTIONS_BUTTON_SIZE_PX, Image.SCALE_SMOOTH));
        JButton redownloadButton = new HoverableButton(GUIUtil.createResizedIcon(REDOWNLOAD_ICON, TRACK_OPTIONS_BUTTON_SIZE_PX, TRACK_OPTIONS_BUTTON_SIZE_PX, Image.SCALE_SMOOTH));
        JScrollPane titleLabelScrollPane = new InvisibleScrollPane(titleLabel);
        JScrollPane artistsLabelScrollPane = new InvisibleScrollPane(artistsLabel);
        JScrollPane albumLabelScrollPane = new InvisibleScrollPane(albumLabel);
        trackInfoScrollPanes = new JScrollPane[] {titleLabelScrollPane, artistsLabelScrollPane, albumLabelScrollPane};
        buttonsPanel = new JPanel();

        titleLabel.setFont(TITLE_FONT);
        artistsLabel.setFont(ARTISTS_FONT);
        albumLabel.setFont(ALBUM_FONT);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        titleLabelScrollPane.setVisible(false);
        albumArtLabel.setVisible(false);
        artistsLabelScrollPane.setVisible(false);
        albumLabelScrollPane.setVisible(false);
        buttonsPanel.setVisible(false);

        add(
            titleLabelScrollPane, 
            "cell 0 0, "
            + "span 1 1, "
            + "align left center, "
            + "growx, "
            + "pushx, "
            + "wmax 100%, "
            + "gaptop " + GUI_TO_WINDOW_TOP_GAP
        );

        add(
            albumArtLabel,
            "cell 0 1, "
            + "span 1 1, "
            + "align center, "
            + "grow, "
            + "pushx, "
            + "wmax 100%, "
            + "hmax 100%, "
            + "wmax " + ALBUM_ART_ICON_SIZE_PX + ", "
            + "hmax " + ALBUM_ART_ICON_SIZE_PX + ", "
            + "gaptop " + GUI_GAP
        );

        add(
            artistsLabelScrollPane,
            "cell 0 2, "
            + "span 1 1, "
            + "align left center, "
            + "growx, "
            + "pushx, "
            + "wmax 100%, "
            + "gaptop " + GUI_SECTIONS_GAP
        );

        add(
            albumLabelScrollPane,
            "cell 0 3, "
            + "span 1 1, "
            + "align left center, "
            + "growx, "
            + "pushx, "
            + "wmax 100%, "
            + "gaptop " + GUI_GAP
        );

        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(editMetadataButton);
        buttonsPanel.add(Box.createHorizontalStrut(TRACK_OPTIONS_BUTTON_SPACING));
        buttonsPanel.add(redownloadButton);
        buttonsPanel.add(Box.createHorizontalGlue());

        add(
            buttonsPanel,
            "cell 0 4, "
            + "span 1 1, "
            + "alignx center, "
            + "growx, "
            + "pushx, "
            + "wmax 100%, "
            + "gaptop " + GUI_SECTIONS_GAP
        );

        currentTrackInfoPanel = this;

        //--------------------------START ACTION LISTENERS--------------------------

        editMetadataButton.addActionListener((unused) -> {

            GUIUtil.createEditMetadataDialog(track, null);

        });

        redownloadButton.addActionListener((unused) -> {

            GUIUtil.createRedownloadPopup();

        });

    }

    public void setTrack(Track track) {

        for(JScrollPane scrollpane: trackInfoScrollPanes) {

            scrollpane.setVisible(true);

        }
        albumArtLabel.setVisible(true);
        buttonsPanel.setVisible(true);
        
        titleLabel.setText(track.getTitle());
        artistsLabel.setText(track.getArtists());
        albumLabel.setText(track.getAlbum());
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        artistsLabel.setHorizontalAlignment(JLabel.CENTER);
        albumLabel.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon albumArtImageIcon = GUIUtil.createResizedIcon(
            track.getArtworkByteArray() == null ? PLACEHOLDER_ART_ICON : new ImageIcon(track.getArtworkByteArray()), 
            ALBUM_ART_ICON_SIZE_PX, 
            ALBUM_ART_ICON_SIZE_PX, 
            Image.SCALE_SMOOTH);
        albumArtLabel.setIcon(albumArtImageIcon);

        for(JScrollPane scrollpane: trackInfoScrollPanes) {

            scrollpane.getVerticalScrollBar().setValue(0);
            scrollpane.getHorizontalScrollBar().setValue(0);

        }
        
        revalidate();
        repaint();

        this.track = track;

    }

    public Track getTrack() {

        return track;

    }

    public void clearTrackInfo() {

        for(JScrollPane scrollpane: trackInfoScrollPanes) {

            scrollpane.setVisible(false);

        }
        albumArtLabel.setVisible(false);
        buttonsPanel.setVisible(false);

        titleLabel.setText("");
        artistsLabel.setText("");
        albumLabel.setText("");

        albumArtLabel.setIcon(null);

        revalidate();
        repaint();

    }

}
