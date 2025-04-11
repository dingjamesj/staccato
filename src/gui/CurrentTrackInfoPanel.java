package gui;

import java.awt.Font;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class CurrentTrackInfoPanel extends JPanel {
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font ARTISTS_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font ALBUM_FONT = new Font("Segoe UI", Font.ITALIC, 36);
    private static final Font AUDIO_FORMAT_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final ImageIcon PLACEHOLDER_ART_ICON = createImageIcon("src/main/resources/placeholder art.png");

    private JLabel titleLabel;
    private JLabel albumArtLabel;
    private JLabel artistsLabel;
    private JLabel albumLabel;
    private JRadioButton mp3Button;
    private JRadioButton aacButton;
    private JRadioButton wavButton;
    private JButton youtubeLinkButton;
    private JButton openFileLocationButton;

    public CurrentTrackInfoPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Track title");
        albumArtLabel = new JLabel(PLACEHOLDER_ART_ICON);
        artistsLabel = new JLabel("Artists");
        albumLabel = new JLabel("Album");
        mp3Button = new JRadioButton("mp3");
        aacButton = new JRadioButton("aac");
        wavButton = new JRadioButton("wav");
        ButtonGroup audioFormatButtonGroup = new ButtonGroup();
        audioFormatButtonGroup.add(mp3Button);
        audioFormatButtonGroup.add(aacButton);
        audioFormatButtonGroup.add(wavButton);
        youtubeLinkButton = new JButton("Edit YouTube source");
        openFileLocationButton = new JButton("Open file location");

        titleLabel.setFont(TITLE_FONT);
        artistsLabel.setFont(ARTISTS_FONT);
        albumLabel.setFont(ALBUM_FONT);
        mp3Button.setFont(AUDIO_FORMAT_FONT);
        aacButton.setFont(AUDIO_FORMAT_FONT);
        wavButton.setFont(AUDIO_FORMAT_FONT);
        youtubeLinkButton.setFont(BUTTON_FONT);
        openFileLocationButton.setFont(BUTTON_FONT);

        add(titleLabel);
        add(albumArtLabel);
        add(artistsLabel);
        add(albumLabel);
        add(mp3Button);
        add(aacButton);
        add(wavButton);
        add(youtubeLinkButton);
        add(openFileLocationButton);

    }

    private static ImageIcon createImageIcon(String urlStr) {

        URL url = PlaybarPanel.class.getResource(urlStr);
        if(url != null) {

            return new ImageIcon(url);

        }

        return new ImageIcon(urlStr);

    }

}
