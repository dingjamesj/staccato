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
    
    private static final Font PLAYLIST_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font PLAYLIST_DESCRIPTION_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    private static final ImageIcon REFRESH_ICON = createImageIcon("src/main/resources/refresh.png");
    private static final ImageIcon RESYNC_ICON = createImageIcon("src/main/resources/resync.png");

    private DefaultTableModel tableModel;

    public TracklistPanel() {

        //you would say initHomePage()
        //the home page would just have recently opened playlists

        initPlaylistPage(new Playlist());

    }

    private void initPlaylistPage(Playlist playlist) {

        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //-----BEGIN BUILDING PLAYLIST INFO PANEL-----

        JPanel playlistInfoPanel = new JPanel();
        playlistInfoPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JButton playlistCoverButton = new JButton(playlist.getPlaylistCover());
        JLabel playlistTitleLabel = new JLabel(playlist.getTitle());
        JLabel playlistDescriptionLabel = new JLabel(playlist.getDescription());
        JButton refreshDirectoryButton = new JButton(REFRESH_ICON);
        JButton resyncToOriginButton = new JButton(RESYNC_ICON);

        playlistTitleLabel.setFont(PLAYLIST_TITLE_FONT);
        playlistDescriptionLabel.setFont(PLAYLIST_DESCRIPTION_FONT);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 2;
        playlistInfoPanel.add(playlistCoverButton, constraints);

        constraints.anchor = GridBagConstraints.LAST_LINE_START; //(this is just SOUTH-EAST)
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        playlistInfoPanel.add(playlistTitleLabel, constraints);

        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        playlistInfoPanel.add(playlistDescriptionLabel, constraints);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 3;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        playlistInfoPanel.add(refreshDirectoryButton, constraints);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 4;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        playlistInfoPanel.add(resyncToOriginButton, constraints);

        add(playlistInfoPanel);

        //------END BUILDING PLAYLIST INFO PANEL------
        
        //-------BEGIN BUILDING TRACKLIST PANEL-------

        JPanel tracklistPanel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new String[] {"No.", "", "Title", "Artists", "Album", "Length"}, 0);
        JTable tracklistTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tracklistTable);
        tracklistTable.setFillsViewportHeight(true);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 5;
        constraints.gridheight = 1;
        tracklistPanel.add(scrollPane, BorderLayout.CENTER);

        add(tracklistPanel);

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

}
