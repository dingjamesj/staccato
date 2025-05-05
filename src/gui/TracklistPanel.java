package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import main.FileManager;
import main.Playlist;

public class TracklistPanel extends JPanel {
    
    private static final Font PANEL_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 72);
    private static final Font PLAYLIST_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 100);
    private static final Font PLAYLIST_DESCRIPTION_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final ImageIcon REFRESH_ICON = createImageIcon("src/main/resources/refresh.png");
    private static final ImageIcon RESYNC_ICON = createImageIcon("src/main/resources/resync.png");
    private static final ImageIcon PLACEHOLDER_ART_ICON = createImageIcon("src/main/resources/placeholder art.png");

    private static final int PLAYLIST_SELECTION_HSPACING = 10;
    private static final int PLAYLIST_SELECTION_VSPACING = 10;
    private static final int PLAYLIST_SELECTION_ICON_SIZE = 200;
    private static final int INFO_PANEL_SPACING = 7;
    private static final int INFO_PANEL_TABLE_GAP = 3;
    private static final int INFO_PANEL_PLAYLIST_ICON_SIZE = 220;

    private DefaultTableModel tableModel;

    private static TracklistPanel tracklistPanel;

    public TracklistPanel() {

        //you would say initHomePage()
        //the home page would just have recently opened playlists

        initHomePage();

        // initPlaylistPage(new Playlist("saco", "C:\\Users\\James\\Music\\saco", null));

        TracklistPanel.tracklistPanel = this;

    }

    private void initHomePage() {

        removeAll();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Your Playlists");
        JPanel playlistSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, PLAYLIST_SELECTION_HSPACING, PLAYLIST_SELECTION_VSPACING));
        JScrollPane playlistSelectionScrollPane = new JScrollPane(playlistSelectionPanel);
        JButton addPlaylistButton = new JButton("add");

        titleLabel.setFont(PANEL_TITLE_FONT);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        playlistSelectionPanel.setAlignmentX(CENTER_ALIGNMENT);
        Set<Playlist> playlists;
        try {

            playlists = FileManager.readPlaylists();

        } catch (IOException e) {

            e.printStackTrace();
            return;

        }
        
        add(titleLabel);
        playlistSelectionPanel.add(addPlaylistButton);
        for(Playlist playlist: playlists) {

            playlistSelectionPanel.add(createPlaylistButton(playlist));

        }
        add(playlistSelectionScrollPane);

        revalidate();
        repaint();

        //-----------------------------END GUI BUILDING-----------------------------
        //-------------------------START LISTENERS ADDITION-------------------------

        addPlaylistButton.addActionListener((e) -> {

            String playlistDirectory = JOptionPane.showInputDialog(this, "Enter in the playlist's directory:");
            File directory = new File(playlistDirectory);
            if(!directory.isDirectory()) {

                JOptionPane.showMessageDialog(this, "Invalid directory.");

            } else {

                //Add playlist

            }

        });

    }

    private void initPlaylistPage(Playlist playlist) {

        removeAll();
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        //-----BEGIN BUILDING PLAYLIST INFO PANEL-----

        ImageIcon playlistCoverImageIcon = playlist.getCoverArtByteArray() != null ? new ImageIcon(playlist.getCoverArtByteArray()) : PLACEHOLDER_ART_ICON;
        JButton playlistCoverButton = new JButton();
        JLabel playlistTitleLabel = new JLabel(playlist.getName());
        JLabel playlistDescriptionLabel = new JLabel(createDescription(playlist));
        JButton refreshDirectoryButton = new JButton(REFRESH_ICON);
        JButton resyncToOriginButton = new JButton(RESYNC_ICON);
        JPanel playlistTextPanel = new JPanel();

        playlistCoverButton.setPreferredSize(new Dimension(INFO_PANEL_PLAYLIST_ICON_SIZE, INFO_PANEL_PLAYLIST_ICON_SIZE));
        playlistCoverImageIcon = new ImageIcon(playlistCoverImageIcon.getImage().getScaledInstance(INFO_PANEL_PLAYLIST_ICON_SIZE, INFO_PANEL_PLAYLIST_ICON_SIZE, Image.SCALE_SMOOTH));
        playlistCoverButton.setIcon(playlistCoverImageIcon);
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
        constraints.insets = new Insets(INFO_PANEL_TABLE_GAP, INFO_PANEL_TABLE_GAP, 0, 0);
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

    public static void setHomePage() {

        tracklistPanel.initHomePage();

    }

    public static void setPlaylist(Playlist playlist) {

        tracklistPanel.initPlaylistPage(playlist);

    }

    private static JPanel createPlaylistButton(Playlist playlist) {

        JPanel panelButton = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        ImageIcon playlistCoverImageIcon = playlist.getCoverArtByteArray() != null ? new ImageIcon(playlist.getCoverArtByteArray()) : PLACEHOLDER_ART_ICON;
        JLabel playlistCoverLabel = new JLabel(playlist.getCoverArtByteArray() != null ? new ImageIcon(playlist.getCoverArtByteArray()) : PLACEHOLDER_ART_ICON);
        JLabel playlistNameLabel = new JLabel(playlist.getName());
        JLabel playlistDirectoryLabel = new JLabel(playlist.getDirectory().substring(0, 5) + "..." + playlist.getDirectory().substring(playlist.getDirectory().length() - 5));

        playlistCoverLabel.setPreferredSize(new Dimension(PLAYLIST_SELECTION_ICON_SIZE, PLAYLIST_SELECTION_ICON_SIZE));
        playlistCoverImageIcon = new ImageIcon(playlistCoverImageIcon.getImage().getScaledInstance(INFO_PANEL_PLAYLIST_ICON_SIZE, INFO_PANEL_PLAYLIST_ICON_SIZE, Image.SCALE_SMOOTH));
        playlistCoverLabel.setIcon(playlistCoverImageIcon);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panelButton.add(playlistCoverLabel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        panelButton.add(playlistNameLabel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        panelButton.add(playlistDirectoryLabel, constraints);

        return panelButton;

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
