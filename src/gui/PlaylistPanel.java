package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Playlist;

public class PlaylistPanel extends JPanel {
    
    public PlaylistPanel() {

        //you would say initHomePage()
        //the home page would just have recently opened playlists

        initPlaylistPage(new Playlist());

    }

    private void initPlaylistPage(Playlist playlist) {

        removeAll();
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        //-----BEGIN BUILDING PLAYLIST INFO SECTION-----

        JButton playlistCoverButton = new JButton(playlist.getPlaylistCover());
        JLabel playlistTitleLabel = new JLabel(playlist.getTitle());
        JLabel playlistDescriptionLabel = new JLabel(playlist.getDescription());
        JButton refreshDirectoryButton = new JButton();
        JButton resyncToOriginButton = new JButton();

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

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 3;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        add(refreshDirectoryButton, constraints);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 4;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        add(resyncToOriginButton, constraints);

        //------END BUILDING PLAYLIST INFO SECTION------
        
        //-------BEGIN BUILDING TRACKLIST SECTION-------



        //--------END BUILDING TRACKLIST SECTION--------

        revalidate();
        repaint();

    }

}
