package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

import main.Track;
import main.TracklistPlayer;
import net.miginfocom.swing.MigLayout;

public class CurrentTrackInfoPanel extends JPanel {

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font ARTISTS_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font ALBUM_FONT = new Font("Segoe UI", Font.ITALIC, 18);

    private static final ImageIcon EDIT_METADATA_ICON = GUIUtil.createImageIcon("src/main/resources/refresh.png");
    private static final ImageIcon REDOWNLOAD_ICON = GUIUtil.createImageIcon("src/main/resources/refresh.png");

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

    public static CurrentTrackInfoPanel currentTrackInfoPanel;

    static {

        Runnable updateTrackInfoAction = () -> {

            currentTrackInfoPanel.setTrack(TracklistPlayer.getCurrentlyPlayingTrack());

        };

        TracklistPlayer.addSwitchTrackAction(updateTrackInfoAction);
        TracklistPlayer.addStartTrackAction(updateTrackInfoAction);

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
        JButton editMetadataButton = new JButton(GUIUtil.createResizedIcon(EDIT_METADATA_ICON, TRACK_OPTIONS_BUTTON_SIZE_PX, TRACK_OPTIONS_BUTTON_SIZE_PX, Image.SCALE_SMOOTH));
        JButton redownloadButton = new JButton(GUIUtil.createResizedIcon(REDOWNLOAD_ICON, TRACK_OPTIONS_BUTTON_SIZE_PX, TRACK_OPTIONS_BUTTON_SIZE_PX, Image.SCALE_SMOOTH));
        JScrollPane titleLabelScrollPane = new JScrollPane(titleLabel);
        JScrollPane artistsLabelScrollPane = new JScrollPane(artistsLabel);
        JScrollPane albumLabelScrollPane = new JScrollPane(albumLabel);
        trackInfoScrollPanes = new JScrollPane[] {titleLabelScrollPane, artistsLabelScrollPane, albumLabelScrollPane};
        buttonsPanel = new JPanel();

        titleLabel.setFont(TITLE_FONT);
        artistsLabel.setFont(ARTISTS_FONT);
        albumLabel.setFont(ALBUM_FONT);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        titleLabelScrollPane.setVerticalScrollBar(new JInvisibleScrollBar(JScrollBar.VERTICAL));
        titleLabelScrollPane.setHorizontalScrollBar(new JInvisibleScrollBar(JScrollBar.HORIZONTAL));
        artistsLabelScrollPane.setVerticalScrollBar(new JInvisibleScrollBar(JScrollBar.VERTICAL));
        artistsLabelScrollPane.setHorizontalScrollBar(new JInvisibleScrollBar(JScrollBar.HORIZONTAL));
        albumLabelScrollPane.setVerticalScrollBar(new JInvisibleScrollBar(JScrollBar.VERTICAL));
        albumLabelScrollPane.setHorizontalScrollBar(new JInvisibleScrollBar(JScrollBar.HORIZONTAL));
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

        ImageIcon albumArtImageIcon = GUIUtil.createResizedIcon(new ImageIcon(track.getArtworkByteArray()), ALBUM_ART_ICON_SIZE_PX, ALBUM_ART_ICON_SIZE_PX, Image.SCALE_SMOOTH);
        albumArtLabel.setIcon(albumArtImageIcon);

        for(JScrollPane scrollpane: trackInfoScrollPanes) {

            scrollpane.getVerticalScrollBar().setValue(0);
            scrollpane.getHorizontalScrollBar().setValue(0);

        }
        
        revalidate();
        repaint();

    }

    private static class JInvisibleScrollBar extends JScrollBar {

        public JInvisibleScrollBar(int orientation) {

            super(orientation);

            setOpaque(false);
            setPreferredSize(new Dimension(0, 0));
            setMinimumSize(new Dimension(0, 0));
            setMaximumSize(new Dimension(0, 0));

            setUI(new BasicScrollBarUI() {

                @Override
                protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {

                    //Do nothing for no track

                }

                @Override
                protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {

                    // Do nothing for no track

                }

                @Override
                protected JButton createDecreaseButton(int orientation) {

                    return createInvisibleButton();

                }

                @Override
                protected JButton createIncreaseButton(int orientation) {

                    return createInvisibleButton();
                
                }

                private JButton createInvisibleButton() {
                    
                    JButton invisibleButton = new JButton();
                    invisibleButton.setPreferredSize(new Dimension(0, 0));
                    invisibleButton.setMinimumSize(new Dimension(0, 0));
                    invisibleButton.setMaximumSize(new Dimension(0, 0));
                    invisibleButton.setVisible(false);

                    return invisibleButton;

                }
            });

        }

    }

}
