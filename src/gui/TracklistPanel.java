package gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import main.Playlist;

public class TracklistPanel extends JPanel {
    
    private static final Font PLAYLIST_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 100);
    private static final Font PLAYLIST_DESCRIPTION_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final ImageIcon REFRESH_ICON = createImageIcon("src/main/resources/refresh.png");
    private static final ImageIcon RESYNC_ICON = createImageIcon("src/main/resources/resync.png");
    private static final ImageIcon PLACEHOLDER_ART_ICON = createImageIcon("src/main/resources/placeholder art.png");

    private static final int INFO_PANEL_SPACING = 7;
    private static final int INFO_PANEL_TABLE_GAP = 3;

    private DefaultTableModel tableModel;

    public static TracklistPanel tracklistPanel;

    public TracklistPanel() {

        //you would say initHomePage()
        //the home page would just have recently opened playlists

        initPlaylistPage(new Playlist("D:\\"));

        TracklistPanel.tracklistPanel = this;

    }

    private void initPlaylistPage(Playlist playlist) {

        removeAll();
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        //-----BEGIN BUILDING PLAYLIST INFO PANEL-----

        JButton playlistCoverButton = new JButton(playlist.getCoverArt() == null ? PLACEHOLDER_ART_ICON : playlist.getCoverArt());
        JLabel playlistTitleLabel = new JLabel(playlist.getTitle());
        JLabel playlistDescriptionLabel = new JLabel(createDescription(playlist));
        JButton refreshDirectoryButton = new JButton(REFRESH_ICON);
        JButton resyncToOriginButton = new JButton(RESYNC_ICON);
        JPanel playlistTextPanel = new JPanel();

        playlistTitleLabel.setFont(PLAYLIST_TITLE_FONT);
        playlistTitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        playlistTitleLabel.setText("saco");
        playlistDescriptionLabel.setFont(PLAYLIST_DESCRIPTION_FONT);
        playlistDescriptionLabel.setAlignmentX(LEFT_ALIGNMENT);
        playlistTextPanel.setLayout(new BoxLayout(playlistTextPanel, BoxLayout.Y_AXIS));

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        add(playlistCoverButton, constraints);

        playlistTextPanel.add(playlistTitleLabel);
        playlistTextPanel.add(playlistDescriptionLabel);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, INFO_PANEL_SPACING, 0, 0);
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        add(playlistTextPanel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        constraints.insets = new Insets(0, INFO_PANEL_SPACING, 0, 0);
        add(refreshDirectoryButton, constraints);

        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, INFO_PANEL_SPACING, 0, 0);
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        add(resyncToOriginButton, constraints);

        //------END BUILDING PLAYLIST INFO PANEL------
        
        //-------BEGIN BUILDING TRACKLIST PANEL-------

        tableModel = new DefaultTableModel(new String[] {"No.", "", "Title", "Artists", "Album", "Length"}, 0);
        JTable tracklistTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tracklistTable);
        tracklistTable.setFillsViewportHeight(true);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(INFO_PANEL_TABLE_GAP, 0, 0, 0);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        add(scrollPane, constraints);

        //--------END BUILDING TRACKLIST PANEL--------

        revalidate();
        repaint();

    }

    private static ImageIcon createImageIcon(String urlStr) {

        URL url = PlaybarPanel.class.getResource(urlStr);
        if(url != null) {

            return new ImageIcon(url);

        }

        return new ImageIcon(urlStr);

    }

    private static String createDescription(Playlist playlist) {

        if(playlist.getSize() == 1) {

            return "<html>" + playlist.getDirectory() + "<br></br><b>" + playlist.getSize() + " song:</b> " + playlist.getDuration() + " </html>";

        } else{

            return "<html>" + playlist.getDirectory() + "<br></br><b>" + playlist.getSize() + " songs:</b> " + playlist.getDuration() + " </html>";

        }
        
    }

}
