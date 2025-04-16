package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

    private DefaultTableModel tableModel;

    public TracklistPanel() {

        //you would say initHomePage()
        //the home page would just have recently opened playlists

        initPlaylistPage(new Playlist("D:\\"));

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

        playlistTitleLabel.setFont(PLAYLIST_TITLE_FONT);
        playlistDescriptionLabel.setFont(PLAYLIST_DESCRIPTION_FONT);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 2;
        add(playlistCoverButton, constraints);

        constraints.anchor = GridBagConstraints.LAST_LINE_START; //(this is just SOUTH-EAST)
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        add(playlistTitleLabel, constraints);

        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        add(playlistDescriptionLabel, constraints);

        constraints.anchor = GridBagConstraints.PAGE_END;
        constraints.gridx = 3;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        add(refreshDirectoryButton, constraints);

        constraints.anchor = GridBagConstraints.PAGE_END;
        constraints.gridx = 4;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        add(resyncToOriginButton, constraints);

        //------END BUILDING PLAYLIST INFO PANEL------
        
        //-------BEGIN BUILDING TRACKLIST PANEL-------

        tableModel = new DefaultTableModel(new String[] {"No.", "", "Title", "Artists", "Album", "Length"}, 0);
        JTable tracklistTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tracklistTable);
        tracklistTable.setFillsViewportHeight(true);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 5;
        constraints.gridheight = 1;
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

        return "<html>" + playlist.getDirectory() + "<br></br>" + playlist.getSize() + ", " + playlist.getDuration() + " </html>";
        
    }

}
