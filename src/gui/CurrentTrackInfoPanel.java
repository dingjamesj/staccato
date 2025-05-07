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

public class CurrentTrackInfoPanel extends JPanel {
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font ARTISTS_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font ALBUM_FONT = new Font("Segoe UI", Font.ITALIC, 36);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 18);

    private static final int GUI_GAP = 10;
    private static final int GUI_SECTIONS_GAP = 30;
    private static final int ALBUM_ART_ICON_SIZE = 200;

    private JLabel titleLabel;
    private JLabel albumArtLabel;
    private JLabel artistsLabel;
    private JLabel albumLabel;
    private JButton youtubeLinkButton;
    private JButton openFileLocationButton;

    public static CurrentTrackInfoPanel currentTrackInfoPanel;

    public CurrentTrackInfoPanel() {

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

    }

    private void initWithTrack(Track track) {

        titleLabel.setText(track.getTitle());
        artistsLabel.setText(track.getArtists());
        albumLabel.setText(track.getAlbum());
        
        ImageIcon albumArtImageIcon = new ImageIcon(track.getArtworkByteArray());
        albumArtImageIcon = new ImageIcon(albumArtImageIcon.getImage().getScaledInstance(getWidth(), getWidth(), Image.SCALE_SMOOTH));
        albumArtLabel.setIcon(albumArtImageIcon);

        youtubeLinkButton.setVisible(true);
        openFileLocationButton.setVisible(true);

        revalidate();
        repaint();

    }

    public static void setTrack(Track track) {

        currentTrackInfoPanel.initWithTrack(track);

    }

}
