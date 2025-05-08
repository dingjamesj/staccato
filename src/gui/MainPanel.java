package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.icons.FlatOptionPaneErrorIcon;

import main.FileManager;
import main.Playlist;
import main.Track;
import net.miginfocom.swing.MigLayout;

public class MainPanel extends JPanel {

    private static final ImageIcon PLACEHOLDER_ART_ICON = createImageIcon("src/main/resources/placeholder art.png");

    //Playlist selection view
    private static final Font PANEL_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 72);

    //Tracklist view
    private static final Font PLAYLIST_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 100);
    private static final Font PLAYLIST_DESCRIPTION_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font PLAYLIST_LOADING_FONT = new Font("Segoe UI", Font.ITALIC, 15);
    private static final Font TRACK_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font TRACK_ARTISTS_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font TRACK_ALBUM_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    private static final ImageIcon REFRESH_ICON = createImageIcon("src/main/resources/refresh.png");
    private static final ImageIcon RESYNC_ICON = createImageIcon("src/main/resources/resync.png");
    private static final Color ALTERNATE_TRACKLIST_ROW_COLOR = new Color(0x151515);

    //---------------------------------------- /\ VISUALS /\ ----------------------------------------
    //-----------------------------------------------------------------------------------------------
    //---------------------------------------- \/ SPACING \/ ----------------------------------------

    private static final int SCROLL_SPEED = 11;

    //Playlist selection view
    private static final int PANEL_TITLE_TO_PLAYLIST_SELECTION_PANEL_GAP = 5;
    private static final int PLAYLIST_SELECTION_HSPACING = 10;
    private static final int PLAYLIST_SELECTION_VSPACING = 10;
    private static final int PLAYLIST_SELECTION_ICON_SIZE = 200;
    //Tracklist view
    private static final int INFO_PANEL_SPACING = 7;
    private static final int INFO_PANEL_TABLE_GAP = 3;
    private static final int INFO_PANEL_PLAYLIST_ICON_SIZE = 220;
    private static final int TRACKLIST_ROWS_SPACING = 3;
    private static final int TRACK_ARTWORK_WIDTH_PX = 64;
    private static final double TRACK_TITLE_ARTISTS_COLUMN_WIDTH_PROPORTION = 0.5;
    private static final double TRACK_ALBUM_COLUMN_WIDTH_PROPORTION = 0.3;
    private static final int TRACK_EDIT_COLUMN_WIDTH_PX = 10;

    //--------------------------------------- \/ VARIABLES \/ ---------------------------------------

    public static MainPanel mainPanel;

    private JPanel tracklistPanel;
    private JLabel loadingLabel;
    private JLabel playlistDescriptionLabel;

    public MainPanel() {

        //you would say initHomePage()
        //the home page would just have recently opened playlists

        initHomePage();

        // initPlaylistPage(new Playlist("saco", "C:\\Users\\James\\Music\\saco", null));

        MainPanel.mainPanel = this;

    }

    private void initHomePage() {

        removeAll();
        FileManager.stopReadingTracks();

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
        add(Box.createVerticalStrut(PANEL_TITLE_TO_PLAYLIST_SELECTION_PANEL_GAP));
        add(playlistSelectionScrollPane);

        revalidate();
        repaint();

        //-----------------------------END GUI BUILDING-----------------------------
        //-------------------------BEGIN LISTENERS ADDITION-------------------------

        addPlaylistButton.addActionListener((unused) -> {

            String playlistDirectory = JOptionPane.showInputDialog(
                this, 
                "Enter the playlist's directory:", 
                "Add Playlist", 
                JOptionPane.QUESTION_MESSAGE
            );
            if(playlistDirectory == null) {

                return;

            }

            File directory = new File(playlistDirectory);
            if(!playlistDirectory.isEmpty() && !directory.isDirectory()) {

                JOptionPane.showMessageDialog(this, "Invalid directory.", "Add Playlist", JOptionPane.ERROR_MESSAGE);

            } else if(!playlistDirectory.isEmpty()) {

                try {

                    Playlist newPlaylist = new Playlist(playlistDirectory);
                    FileManager.addPlaylist(newPlaylist);
                    SwingUtilities.invokeLater(() -> {

                        initTracklistPage(newPlaylist);

                    });

                } catch (FileNotFoundException e) {

                    //Dialog box that tells the user to ensure the staccato folder is clear of extraneous files
                    StaccatoWindow.showDialogPopup("Cannot create playlist file", "<html>Cannot create playlist file.<br></br>Please make sure that the staccato program directory is clear of any extraneous files.</html>", new FlatOptionPaneErrorIcon());
                    e.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

        });

    }

    /**
     * Only loads the playlist art and name 
     * 
     * <i>
     * (hence it's called INIT tracklist page)
     * </i>
     * @param playlist
     */
    private void initTracklistPage(Playlist playlist) {

        removeAll();
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        //-----BEGIN BUILDING PLAYLIST INFO PANEL-----

        ImageIcon playlistCoverImageIcon = playlist.getCoverArtByteArray() != null ? new ImageIcon(playlist.getCoverArtByteArray()) : PLACEHOLDER_ART_ICON;
        JButton playlistCoverButton = new JButton();
        JLabel playlistTitleLabel = new JLabel(playlist.getName());
        playlistDescriptionLabel = new JLabel(createDescription(playlist));
        JButton returnToHomeButton = new JButton(REFRESH_ICON);
        JButton refreshDirectoryButton = new JButton(REFRESH_ICON);
        JButton resyncToOriginButton = new JButton(RESYNC_ICON);
        JPanel playlistTextPanel = new JPanel();

        playlistCoverButton.setPreferredSize(new Dimension(INFO_PANEL_PLAYLIST_ICON_SIZE, INFO_PANEL_PLAYLIST_ICON_SIZE));
        playlistCoverImageIcon = new ImageIcon(playlistCoverImageIcon.getImage().getScaledInstance(INFO_PANEL_PLAYLIST_ICON_SIZE, INFO_PANEL_PLAYLIST_ICON_SIZE, Image.SCALE_SMOOTH));
        playlistCoverButton.setIcon(playlistCoverImageIcon);
        playlistTitleLabel.setFont(PLAYLIST_TITLE_FONT);
        playlistTitleLabel.setAlignmentX(LEFT_ALIGNMENT);
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

        //Glue to separate the playlist info and the buttons
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.anchor = GridBagConstraints.CENTER;
        add(new JPanel(), constraints);

        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0;
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        constraints.insets = new Insets(0, INFO_PANEL_SPACING, 0, 0);
        add(refreshDirectoryButton, constraints);

        constraints.gridx = 4;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        constraints.insets = new Insets(0, INFO_PANEL_SPACING, 0, 0);
        add(resyncToOriginButton, constraints);

        constraints.gridx = 5;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        constraints.insets = new Insets(0, INFO_PANEL_SPACING, 0, INFO_PANEL_SPACING);
        add(returnToHomeButton, constraints);

        //------END BUILDING PLAYLIST INFO PANEL------
        
        //-------BEGIN BUILDING TRACKLIST PANEL-------

        tracklistPanel = new JPanel();
        loadingLabel = new JLabel("Loading tracklist...");
        JScrollPane scrollPane = new JScrollPane(tracklistPanel);

        tracklistPanel.setLayout(new BoxLayout(tracklistPanel, BoxLayout.Y_AXIS));
        loadingLabel.setFont(PLAYLIST_LOADING_FONT);
        loadingLabel.setAlignmentX(CENTER_ALIGNMENT);
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);

        tracklistPanel.add(Box.createVerticalGlue());
        tracklistPanel.add(loadingLabel);
        tracklistPanel.add(Box.createVerticalStrut(30));
        tracklistPanel.add(Box.createVerticalGlue());

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 6;
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

        //-------------------------BEGIN LISTENERS ADDITION-------------------------
        
        returnToHomeButton.addActionListener((unused) -> {

            SwingUtilities.invokeLater(() -> {

                initHomePage();

            });

        });

    }

    /**
     * Loads the tracklist info
     * @param playlist
     */
    private void loadTracklistInfo(Playlist playlist) {

        if(playlist.getTracks() == null) {

            boolean loadWasSuccessful = playlist.loadTracks();
        
            //The tracks may not have loaded successfully if the loading process was interrupted by a user event.
            if(!loadWasSuccessful) {

                return;

            }

        }

        playlistDescriptionLabel.setText(createDescription(playlist));

        Track[] tracks = playlist.getTracks();
        tracklistPanel.removeAll();
        for(int i = 0; i < tracks.length; i++) {

            JPanel trackPanel = createTrackEntry(tracks[i]);
            tracklistPanel.add(trackPanel);
            if(i % 2 == 0) {

                trackPanel.setBackground(ALTERNATE_TRACKLIST_ROW_COLOR);

            }

        }

        tracklistPanel.revalidate();
        tracklistPanel.repaint();

    }

    private JPanel createPlaylistButton(Playlist playlist) {

        JPanel panelButton = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        ImageIcon playlistCoverImageIcon = playlist.getCoverArtByteArray() != null ? new ImageIcon(playlist.getCoverArtByteArray()) : PLACEHOLDER_ART_ICON;
        JLabel playlistCoverLabel = new JLabel(playlist.getCoverArtByteArray() != null ? new ImageIcon(playlist.getCoverArtByteArray()) : PLACEHOLDER_ART_ICON);
        JLabel playlistNameLabel = new JLabel(playlist.getName());
        JLabel playlistDirectoryLabel;
        try {

            playlistDirectoryLabel = new JLabel(playlist.getDirectory().substring(0, 5) + "..." + playlist.getDirectory().substring(playlist.getDirectory().length() - 5));

        } catch(StringIndexOutOfBoundsException e) {

            playlistDirectoryLabel = new JLabel(playlist.getDirectory().isEmpty() ? "[No Directory]" : playlist.getDirectory());

        }

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

        //---------------------------------START MOUSE LISTENER---------------------------------
        panelButton.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent unused) {

                SwingUtilities.invokeLater(() -> {

                    initTracklistPage(playlist);

                    Thread trackLoadingThread = new Thread(() -> {
    
                        loadTracklistInfo(playlist);

                    });
                    trackLoadingThread.start();
                    
                });

            }

        });

        return panelButton;

    }

    private JPanel createTrackEntry(Track track) {

        JPanel trackPanel = new JPanel(new MigLayout(
            "insets " + TRACKLIST_ROWS_SPACING + " 0 " + TRACKLIST_ROWS_SPACING + " 0",
            "[" + TRACK_ARTWORK_WIDTH_PX + "][" + (int) (TRACK_TITLE_ARTISTS_COLUMN_WIDTH_PROPORTION * 100) + "%][" + (int) (TRACK_ALBUM_COLUMN_WIDTH_PROPORTION * 100) + "%][" + TRACK_EDIT_COLUMN_WIDTH_PX + "]"
        ));

        ImageIcon artworkIcon = track.getArtworkByteArray() != null ? new ImageIcon(track.getArtworkByteArray()) : PLACEHOLDER_ART_ICON;
        JLabel artworkLabel = new JLabel();
        JPanel titleAndArtistsPanel = new JPanel();
        JLabel titleLabel = new JLabel(track.getTitle() != null && !track.getTitle().isBlank() ? track.getTitle() : "[No Title]");
        JLabel artistsLabel = new JLabel(track.getArtists() != null && !track.getArtists().isBlank() ? track.getArtists() : "[Unknown Artists]");
        JLabel albumLabel = new JLabel(track.getAlbum() != null && !track.getAlbum().isBlank() ? track.getAlbum() : "[No Album]");
        JButton editTrackButton = new JButton(REFRESH_ICON);

        titleAndArtistsPanel.setLayout(new BoxLayout(titleAndArtistsPanel, BoxLayout.Y_AXIS));
        titleAndArtistsPanel.setOpaque(false);
        artworkLabel.setPreferredSize(new Dimension(TRACK_ARTWORK_WIDTH_PX, TRACK_ARTWORK_WIDTH_PX));
        artworkIcon = new ImageIcon(artworkIcon.getImage().getScaledInstance(TRACK_ARTWORK_WIDTH_PX, TRACK_ARTWORK_WIDTH_PX, Image.SCALE_SMOOTH));
        artworkLabel.setIcon(artworkIcon);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        titleLabel.setFont(TRACK_TITLE_FONT);
        artistsLabel.setAlignmentX(LEFT_ALIGNMENT);
        artistsLabel.setFont(TRACK_ARTISTS_FONT);
        albumLabel.setAlignmentX(LEFT_ALIGNMENT);
        albumLabel.setFont(TRACK_ALBUM_FONT);
        // albumLabel.setBackground(Color.cyan);

        trackPanel.add(artworkLabel, "cell 0 0");
        titleAndArtistsPanel.add(titleLabel);
        titleAndArtistsPanel.add(artistsLabel);
        trackPanel.add(titleAndArtistsPanel, "cell 1 0, pushx, wmax " + (int) (TRACK_TITLE_ARTISTS_COLUMN_WIDTH_PROPORTION * 100) + "%");
        trackPanel.add(albumLabel, "cell 2 0, wmax " + (int) (TRACK_ALBUM_COLUMN_WIDTH_PROPORTION * 100) + "%");
        trackPanel.add(editTrackButton, "cell 3 0");

        trackPanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

                if(mouseEvent.getClickCount() < 2) {

                    return;

                }

                System.out.println(track.getFileLocation());
                try {

                    PlaybarPanel.playbarPanel.playTrack(track);

                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                    
                }

            }

        });

        return trackPanel;

    }

    private static ImageIcon createImageIcon(String urlStr) {

        URL url = PlaybarPanel.class.getResource(urlStr);
        if(url != null) {

            return new ImageIcon(url);

        }

        return new ImageIcon(urlStr);

    }

    private static String createDescription(Playlist playlist) {

        if(playlist.getTracks() == null) {

            return "<html>" + playlist.getDirectory() + "<br></br><i>Loading...</i></html>";

        }

        if(playlist.getSize() == 1) {

            return "<html>" + playlist.getDirectory() + "<br></br><b>" + playlist.getSize() + " song</b> " + playlist.getDuration() + " </html>";

        } else{

            return "<html>" + playlist.getDirectory() + "<br></br><b>" + playlist.getSize() + " songs</b> " + playlist.getDuration() + " </html>";

        }
        
    }

}
