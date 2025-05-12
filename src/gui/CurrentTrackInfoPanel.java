package gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Track;
import main.TracklistPlayer;
import net.miginfocom.swing.MigLayout;

public class CurrentTrackInfoPanel extends JPanel {
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font ARTISTS_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font ALBUM_FONT = new Font("Segoe UI", Font.ITALIC, 36);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 18);

    private static final int GUI_GAP = 10;
    private static final int GUI_SECTIONS_GAP = 30;
    private static final int ALBUM_ART_ICON_SIZE_PX = 200;

    private JLabel titleLabel;
    private JLabel albumArtLabel;
    private JLabel artistsLabel;
    private JLabel albumLabel;
    private JButton youtubeLinkButton;
    private JButton openFileLocationButton;

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

        

        /*
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        titleLabel = new JLabel();
        albumArtLabel = new JLabel();
        artistsLabel = new JLabel();
        albumLabel = new JLabel();
        youtubeLinkButton = new JButton("Edit YouTube source");
        openFileLocationButton = new JButton("Open file location");

        titleLabel.setFont(TITLE_FONT);
        artistsLabel.setFont(ARTISTS_FONT);
        albumLabel.setFont(ALBUM_FONT);
        youtubeLinkButton.setFont(BUTTON_FONT);
        openFileLocationButton.setFont(BUTTON_FONT);

        constraints.anchor = GridBagConstraints.LINE_START;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 0, GUI_GAP, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(titleLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 0, GUI_GAP, 0);
        constraints.fill = GridBagConstraints.BOTH;
        add(albumArtLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 0, GUI_GAP, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(artistsLabel, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 0, GUI_SECTIONS_GAP, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(albumLabel, constraints);
        
        constraints.anchor = GridBagConstraints.CENTER;

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 0, GUI_GAP, 0);
        constraints.fill = GridBagConstraints.NONE;
        add(youtubeLinkButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 0, GUI_GAP, 0);
        constraints.fill = GridBagConstraints.NONE;
        add(openFileLocationButton, constraints);

        youtubeLinkButton.setVisible(false);
        openFileLocationButton.setVisible(false);

        */

        currentTrackInfoPanel = this;

    }

    public void setTrack(Track track) {

        titleLabel.setText(track.getTitle());
        artistsLabel.setText(track.getArtists());
        albumLabel.setText(track.getAlbum());
        
        ImageIcon albumArtImageIcon = createResizedIcon(new ImageIcon(track.getArtworkByteArray()), ALBUM_ART_ICON_SIZE_PX, ALBUM_ART_ICON_SIZE_PX, Image.SCALE_SMOOTH);
        albumArtLabel.setIcon(albumArtImageIcon);

        youtubeLinkButton.setVisible(true);
        openFileLocationButton.setVisible(true);

        revalidate();
        repaint();

    }

    private static ImageIcon createResizedIcon(ImageIcon imageIcon, int width, int height, int rescalingAlgorithm) {

        return new ImageIcon(imageIcon.getImage().getScaledInstance(width, height, rescalingAlgorithm));

    }

}
