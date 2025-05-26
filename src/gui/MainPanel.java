package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import main.FileManager;
import main.Playlist;
import main.Track;
import main.TracklistPlayer;
import net.miginfocom.swing.MigLayout;

import gui.GUIUtil.InvisibleScrollPane;
import gui.GUIUtil.HoverableButton;

public class MainPanel extends JPanel {

    //Playlist selection view
    private static final Font PANEL_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 72);
    private static final Font PLAYLIST_SELECTION_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 42);

    //Tracklist view
    private static final Font PLAYLIST_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 84);
    private static final Font PLAYLIST_DESCRIPTION_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font PLAYLIST_LOADING_FONT = new Font("Segoe UI", Font.PLAIN, 20);
    private static final Font TRACK_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font TRACK_ARTISTS_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font TRACK_ALBUM_FONT = new Font("Segoe UI", Font.PLAIN, 18);

    private static final ImageIcon PLACEHOLDER_ART_ICON = GUIUtil.createImageIcon("src/main/resources/placeholder art.png");
    private static final ImageIcon REFRESH_ICON = GUIUtil.createImageIcon("src/main/resources/refresh.png");
    private static final ImageIcon RESYNC_ICON = GUIUtil.createImageIcon("src/main/resources/resync.png");
    private static final ImageIcon EDIT_ICON = GUIUtil.createImageIcon("src/main/resources/edit.png");
    private static final ImageIcon HOME_ICON = GUIUtil.createImageIcon("src/main/resources/home.png");
    private static final ImageIcon MORE_OPTIONS_ICON = GUIUtil.createImageIcon("src/main/resources/more options.png");
    private static final ImageIcon ADD_ICON = GUIUtil.createImageIcon("src/main/resources/resync.png");

    private static final Color ALTERNATE_TRACKLIST_ROW_COLOR = new Color(0x151515);
    private static final Color HIGHLIGHTED_TRACKLIST_ROW_COLOR = new Color(0x303030);
    private static final Color HIGHLIGHTED_PLAYLIST_BUTTON_COLOR = new Color(0x303030);

    //---------------------------------------- /\ VISUALS /\ ----------------------------------------
    //-----------------------------------------------------------------------------------------------
    //---------------------------------------- \/ SPACING \/ ----------------------------------------

    private static final int SCROLL_SPEED = 9;
    private static final int MIN_PLAYLIST_NAME_FONT_SIZE = 36;

    //Playlist selection view
    private static final int PANEL_TITLE_TO_PLAYLIST_SELECTION_PANEL_GAP = 5;
    private static final int PLAYLIST_SELECTION_HSPACING = 10;
    private static final int PLAYLIST_SELECTION_VSPACING = 10;
    private static final int PLAYLIST_SELECTION_ICON_SIZE = 128;
    private static final int PLAYLIST_SELECTION_INFO_MIN_WIDTH_PX = 260;
    
    //Info panel sizing and spacing
    private static final double INFO_PANEL_HEIGHT_PROPORTION = 0.27;
    private static final int INFO_PANEL_SPACING = 7;
    private static final int INFO_PANEL_TABLE_GAP = 3;
    private static final int INFO_PANEL_PLAYLIST_ICON_SIZE = 220;
    private static final int INFO_PANEL_PLAYLIST_OPTION_BUTTON_SIZE = 48;
    private static final int PLAYLIST_DESCRIPTION_TO_OPTION_BUTTONS_GAP_PX = 30;
    //For resizing the playlist name label
    private static final int INFO_PANEL_PLAYLIST_NAME_MIN_FONT_SIZE = 36;
    private static final int INFO_PANEL_PLAYLIST_NAME_SUGGESTED_WIDTH_PX = 2800;
    //Tracklist sizing and spacing
    private static final int TRACKLIST_ROWS_SPACING = 3;
    private static final int TRACK_ARTWORK_WIDTH_PX = 64;
    private static final double TRACK_TITLE_ARTISTS_COLUMN_WIDTH_PROPORTION = 0.5;
    private static final double TRACK_ALBUM_COLUMN_WIDTH_PROPORTION = 0.3;
    private static final int TRACK_EDIT_COLUMN_WIDTH_PX = 10;
    private static final int TRACKLIST_ROW_TO_PANEL_EDGE_SPACING_PX = 9;

    //--------------------------------------- \/ VARIABLES \/ ---------------------------------------

    public static MainPanel mainPanel;

    private JButton refreshButton;
    private JPanel tracklistPanel;
    private JLabel playlistDescriptionLabel;

    private final AtomicBoolean killTracklistLoadingThreadFlag = new AtomicBoolean(false);
    private Track[] currentTracklist;

    public MainPanel() {

        initHomePage();

        MainPanel.mainPanel = this;

    }

    private void initHomePage() {

        FileManager.stopReadingTracks();
        currentTracklist = null;

        removeAll();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("Your Playlists");
        JPanel playlistSelectionPanel = new JPanel(new WrapLayout(FlowLayout.CENTER, PLAYLIST_SELECTION_HSPACING, PLAYLIST_SELECTION_VSPACING));
        JScrollPane playlistSelectionScrollPane = new JScrollPane(playlistSelectionPanel);
        JPanel addPlaylistButtonPanel = createPlaylistButton(new Playlist("")); //This is just a modified playlist panel

        titleLabel.setFont(PANEL_TITLE_FONT);
        playlistSelectionScrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        playlistSelectionPanel.setAlignmentX(CENTER_ALIGNMENT);
        Set<Playlist> playlists;
        try {

            playlists = FileManager.readPlaylists();

        } catch (IOException e) {

            e.printStackTrace();
            return;

        }
        playlistSelectionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        ((JLabel) addPlaylistButtonPanel.getComponent(0)).setIcon(GUIUtil.createResizedIcon(ADD_ICON, PLAYLIST_SELECTION_ICON_SIZE, PLAYLIST_SELECTION_ICON_SIZE, Image.SCALE_SMOOTH));
        ((JLabel) addPlaylistButtonPanel.getComponent(1)).setText("Add Playlist");
        ((JLabel) addPlaylistButtonPanel.getComponent(2)).setText("Each playlist is associated with a folder of mp3 files on your computer.");
        addPlaylistButtonPanel.getComponent(3).setVisible(false);;

        add(titleLabel);
        playlistSelectionPanel.add(addPlaylistButtonPanel);
        for(Playlist playlist: playlists) {

            playlistSelectionPanel.add(createPlaylistButton(playlist));

        }
        add(Box.createVerticalStrut(PANEL_TITLE_TO_PLAYLIST_SELECTION_PANEL_GAP));
        add(playlistSelectionScrollPane);

        revalidate();
        repaint();

        //-----------------------------END GUI BUILDING-----------------------------

        //-------------------------BEGIN LISTENERS ADDITION-------------------------

        for(MouseListener listener: addPlaylistButtonPanel.getMouseListeners()) {

            addPlaylistButtonPanel.removeMouseListener(listener);

        }
        addPlaylistButtonPanel.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseEntered(MouseEvent e) {

                addPlaylistButtonPanel.setBackground(HIGHLIGHTED_PLAYLIST_BUTTON_COLOR);
                if(addPlaylistButtonPanel.isEnabled()) {

                    addPlaylistButtonPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                }

            }

            @Override
            public void mouseExited(MouseEvent e) {

                addPlaylistButtonPanel.setBackground(getBackground());
                addPlaylistButtonPanel.setCursor(Cursor.getDefaultCursor());

            }

            @Override
            public void mouseClicked(MouseEvent e) {

                if(e.getButton() != MouseEvent.BUTTON1) {

                    return;

                }

                String playlistDirectory = JOptionPane.showInputDialog(
                    StaccatoWindow.staccatoWindow, 
                    "Enter the playlist's directory:", 
                    "Add Playlist", 
                    JOptionPane.QUESTION_MESSAGE
                );
                if(playlistDirectory == null) {

                    return;

                }

                File directory = new File(playlistDirectory);
                if(!directory.isDirectory()) {

                    JOptionPane.showMessageDialog(StaccatoWindow.staccatoWindow, "Invalid directory.", "Add Playlist", JOptionPane.ERROR_MESSAGE);

                } else {

                    try {

                        Playlist newPlaylist = new Playlist(playlistDirectory);
                        FileManager.addPlaylist(newPlaylist);
                        SwingUtilities.invokeLater(() -> {

                            initTracklistPage(newPlaylist);

                        });

                    } catch (FileNotFoundException error) {

                        //Dialog box that tells the user to ensure the staccato folder is clear of extraneous files
                        JOptionPane.showMessageDialog(
                            StaccatoWindow.staccatoWindow, 
                            "<html>Cannot create playlist file.<br></br>Please make sure that the staccato program directory is clear of any extraneous files.</html>", 
                            "Cannot Create Playlist File",
                            JOptionPane.ERROR_MESSAGE
                        );
                        error.printStackTrace();

                    } catch (IOException error) {

                        error.printStackTrace();

                    }

                }

            }

        });

        //--------------------------END LISTENERS ADDITION--------------------------

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
        setLayout(new MigLayout(
            "insets 0 0 0 0", 
            "", 
            "[" + (int) (INFO_PANEL_HEIGHT_PROPORTION * 100) + "%][" + (int) ((1 - INFO_PANEL_HEIGHT_PROPORTION) * 100) + "%]"
        ));

        //-----BEGIN BUILDING PLAYLIST INFO PANEL-----

        JPanel playlistInfoPanel = new JPanel(new MigLayout("insets 0, gap 0"));

        ImageIcon playlistCoverImageIcon = playlist.getCoverArtByteArray() != null ? new ImageIcon(playlist.getCoverArtByteArray()) : PLACEHOLDER_ART_ICON;
        JLabel playlistCoverLabel = new JLabel();
        JLabel playlistNameLabel = new JLabel(playlist.getName());
        JScrollPane playlistNameScrollPane = new InvisibleScrollPane(playlistNameLabel);
        playlistDescriptionLabel = new JLabel("<html>" + playlist.getDirectory() + "<br></br><i>Loading...</i></html>");
        JScrollPane playlistDescriptionScrollPane = new InvisibleScrollPane(playlistDescriptionLabel);
        JButton returnToHomeButton = new HoverableButton(GUIUtil.createResizedIcon(HOME_ICON, INFO_PANEL_PLAYLIST_OPTION_BUTTON_SIZE, INFO_PANEL_PLAYLIST_OPTION_BUTTON_SIZE, Image.SCALE_SMOOTH));
        JButton playPlaylistButton = new HoverableButton(GUIUtil.createResizedIcon(EDIT_ICON, INFO_PANEL_PLAYLIST_OPTION_BUTTON_SIZE, INFO_PANEL_PLAYLIST_OPTION_BUTTON_SIZE, Image.SCALE_SMOOTH));
        /*JButton*/ refreshButton = new HoverableButton(GUIUtil.createResizedIcon(REFRESH_ICON, INFO_PANEL_PLAYLIST_OPTION_BUTTON_SIZE, INFO_PANEL_PLAYLIST_OPTION_BUTTON_SIZE, Image.SCALE_SMOOTH));
        JButton addTrackButton = new HoverableButton(GUIUtil.createResizedIcon(REFRESH_ICON, INFO_PANEL_PLAYLIST_OPTION_BUTTON_SIZE, INFO_PANEL_PLAYLIST_OPTION_BUTTON_SIZE, Image.SCALE_SMOOTH));

        playlistCoverLabel.setPreferredSize(new Dimension(INFO_PANEL_PLAYLIST_ICON_SIZE, INFO_PANEL_PLAYLIST_ICON_SIZE));
        playlistCoverImageIcon = GUIUtil.createResizedIcon(playlistCoverImageIcon, INFO_PANEL_PLAYLIST_ICON_SIZE, INFO_PANEL_PLAYLIST_ICON_SIZE, Image.SCALE_SMOOTH);
        playlistCoverLabel.setIcon(playlistCoverImageIcon);
        playlistNameLabel.setFont(PLAYLIST_TITLE_FONT);
        playlistNameLabel.setAlignmentX(LEFT_ALIGNMENT);
        playlistNameLabel.setVerticalAlignment(JLabel.BOTTOM);
        //Shrink the playlist name label if the text is too long
        int textWidth = GUIUtil.calculateTextWidth(playlistNameLabel);
        if(GUIUtil.calculateTextWidth(playlistNameLabel) > INFO_PANEL_PLAYLIST_NAME_SUGGESTED_WIDTH_PX) {

            float newFontSize = playlistNameLabel.getFont().getSize() * INFO_PANEL_PLAYLIST_NAME_SUGGESTED_WIDTH_PX / (float) textWidth;
            playlistNameLabel.setFont(playlistNameLabel.getFont().deriveFont(Math.max(newFontSize, MIN_PLAYLIST_NAME_FONT_SIZE)));

        }
        playlistDescriptionLabel.setFont(PLAYLIST_DESCRIPTION_FONT);
        playlistDescriptionLabel.setAlignmentX(LEFT_ALIGNMENT);

        playlistInfoPanel.add(
            playlistCoverLabel, ""
            + "cell 0 0, "
            + "span 1 2, "
            + "gaptop " + INFO_PANEL_TABLE_GAP + ", "
            + "align center center"
        );

        playlistInfoPanel.add(
            playlistNameScrollPane, ""
            + "cell 1 0, "
            + "span 6 1, "
            + "align left bottom, "
            + "pushy, "
            + "gapleft " + INFO_PANEL_SPACING + ", gapbottom " + INFO_PANEL_SPACING + ", "
            + "wmax 70%, "
        );

        playlistInfoPanel.add(
            playlistDescriptionScrollPane, ""
            + "cell 1 1, "
            + "span 1 1, "
            + "align left bottom, "
            + "gapleft " + INFO_PANEL_SPACING + ", "
        );

        JPanel gluePanel = new JPanel();
        gluePanel.setOpaque(false);
        playlistInfoPanel.add(
            gluePanel, ""
            + "cell 2 1, "
            + "span 1 1, "
            + "pushx, "
            + "align center center"
        );

        playlistInfoPanel.add(
            refreshButton, ""
            + "cell 3 1, "
            + "span 1 1, "
            + "align left bottom, "
            + "gapleft " + PLAYLIST_DESCRIPTION_TO_OPTION_BUTTONS_GAP_PX + ", "
        );

        playlistInfoPanel.add(
            addTrackButton, ""
            + "cell 4 1, "
            + "span 1 1, "
            + "align left bottom, "
            + "gapleft " + INFO_PANEL_SPACING + ", "
        );

        playlistInfoPanel.add(
            playPlaylistButton, ""
            + "cell 5 1, "
            + "span 1 1, "
            + "align left bottom, "
            + "gapleft " + INFO_PANEL_SPACING + ", "
        );

        playlistInfoPanel.add(
            returnToHomeButton, ""
            + "cell 6 1, "
            + "span 1 1, "
            + "align left bottom, "
            + "gapleft " + INFO_PANEL_SPACING + ", "
        );

        add(playlistInfoPanel,
            "cell 0 0, "
            + "span 1 1, "
            + "growx, "
            + "pushx"
        );

        //------END BUILDING PLAYLIST INFO PANEL------
        
        //-------BEGIN BUILDING TRACKLIST PANEL-------

        tracklistPanel = new JPanel();
        JPanel tracklistWrapperPanel = new JPanel(new BorderLayout());
        JScrollPane tracklistScrollPane = new JScrollPane(tracklistWrapperPanel);

        tracklistPanel.setLayout(new BoxLayout(tracklistPanel, BoxLayout.Y_AXIS));
        tracklistScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tracklistScrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);

        tracklistWrapperPanel.add(tracklistPanel, BorderLayout.NORTH);

        add(tracklistScrollPane,
            "cell 0 1, "
            + "span 1 1, "
            + "grow, "
            + "pushx, "
            + "pushy"
        );

        //--------END BUILDING TRACKLIST PANEL--------

        revalidate();
        repaint();

        Thread trackLoadingThread = new Thread(() -> {

            killTracklistLoadingThreadFlag.set(true);
            loadTracklistInfo(playlist);

        });
        trackLoadingThread.start();

        //-------------------------BEGIN LISTENERS ADDITION-------------------------

        returnToHomeButton.addActionListener((unused) -> {

            SwingUtilities.invokeLater(() -> {

                killTracklistLoadingThreadFlag.set(true);
                initHomePage();

            });

        });

        playlistCoverLabel.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {

                if(e.getButton() == MouseEvent.BUTTON1) {

                    GUIUtil.createPlaylistEditorPopup(playlist);

                }

            }

            @Override
            public void mouseEntered(MouseEvent e) {

                if(playlistCoverLabel.isEnabled()) {

                    playlistCoverLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                }
                
            }

            @Override
            public void mouseExited(MouseEvent e) {

                playlistCoverLabel.setCursor(Cursor.getDefaultCursor());

            }

        });

        playlistNameScrollPane.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {

                if(e.getButton() == MouseEvent.BUTTON1) {

                    GUIUtil.createPlaylistEditorPopup(playlist);

                }

            }

            @Override
            public void mouseEntered(MouseEvent e) {

                if(playlistNameScrollPane.isEnabled()) {

                    playlistNameScrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                }

            }

            @Override
            public void mouseExited(MouseEvent e) {

                playlistNameScrollPane.setCursor(Cursor.getDefaultCursor());

            }

        });

        playlistDescriptionScrollPane.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if(e.getButton() == MouseEvent.BUTTON1) {

                    GUIUtil.createPlaylistEditorPopup(playlist);

                }

            }
            
            @Override
            public void mouseEntered(MouseEvent e) {

                if(playlistDescriptionScrollPane.isEnabled()) {

                    playlistDescriptionScrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                }

            }

            @Override
            public void mouseExited(MouseEvent e) {

                playlistDescriptionScrollPane.setCursor(Cursor.getDefaultCursor());

            }

        });

        refreshButton.addActionListener((unused) -> {

            Thread refreshTracksThread = new Thread(() -> {

                killTracklistLoadingThreadFlag.set(true);
                loadTracklistInfo(playlist);

            });
            refreshTracksThread.start();
            
        });

        playPlaylistButton.addActionListener((unused) -> {

            TracklistPlayer.playTracks(currentTracklist);

        });

    }

    /**
     * Loads the tracklist info
     * @param playlist
     */
    private synchronized void loadTracklistInfo(Playlist playlist) {

        refreshButton.setEnabled(false);

        //This will essentially be a track entry panel but with just a label that says "Loading..."
        JPanel loadingLabelPanel = new JPanel(new MigLayout("insets " + TRACKLIST_ROWS_SPACING + " " + TRACKLIST_ROWS_SPACING + " " + TRACKLIST_ROWS_SPACING + " 0"));
        JLabel loadingLabel = new JLabel("<html><i><b>Loading tracklist...</b></i></html>");

        loadingLabel.setFont(PLAYLIST_LOADING_FONT);
        loadingLabel.setAlignmentX(CENTER_ALIGNMENT);
        loadingLabel.setHorizontalAlignment(JLabel.CENTER);

        tracklistPanel.removeAll();
        SwingUtilities.invokeLater(() -> {

            loadingLabelPanel.add(loadingLabel);
            tracklistPanel.add(loadingLabelPanel);
            tracklistPanel.revalidate();
            tracklistPanel.repaint();

        });

        if(playlist.getTracks() == null) {

            boolean loadWasSuccessful = playlist.loadTracks();
        
            //The tracks may not have loaded successfully if the loading process was interrupted by a user event.
            //Or if the playlist directory does not exist anymore.
            if(!loadWasSuccessful) {

                return;

            }

        }

        currentTracklist = playlist.getTracks();

        SwingUtilities.invokeLater(() -> {

            playlistDescriptionLabel.setText(createDescription(playlist));

        });

        //Load the track entry GUIs
        killTracklistLoadingThreadFlag.set(false); //Reset the kill flag
        Track[] tracks = playlist.getTracks();
        for(int i = 0; i < tracks.length; i++) {

            if(killTracklistLoadingThreadFlag.get()) {

                killTracklistLoadingThreadFlag.set(false);
                return;

            }

            JPanel trackPanel;
            if(i % 2 == 0) {

                trackPanel = createTrackEntry(tracks[i], i, ALTERNATE_TRACKLIST_ROW_COLOR);
                trackPanel.setBackground(ALTERNATE_TRACKLIST_ROW_COLOR);

            } else {

                trackPanel = createTrackEntry(tracks[i], i, getBackground());
                
            }

            SwingUtilities.invokeLater(() -> {

                tracklistPanel.add(trackPanel);
                tracklistPanel.revalidate();
                tracklistPanel.repaint();

            });

        }

        SwingUtilities.invokeLater(() -> {

            tracklistPanel.remove(0); //Remove the "Loading..." panel
            tracklistPanel.revalidate();
            tracklistPanel.repaint();
            refreshButton.setEnabled(true);

        });

    }

    private JPanel createPlaylistButton(Playlist playlist) {
        
        JPanel playlistPanel = new JPanel(new MigLayout("insets 3 3 3 3"));

        ImageIcon playlistCoverImageIcon = playlist.getCoverArtByteArray() != null ? new ImageIcon(playlist.getCoverArtByteArray()) : PLACEHOLDER_ART_ICON;
        JLabel playlistCoverLabel = new JLabel(playlist.getCoverArtByteArray() != null ? new ImageIcon(playlist.getCoverArtByteArray()) : PLACEHOLDER_ART_ICON);
        JLabel playlistNameLabel = new JLabel(playlist.getName());
        JLabel playlistDirectoryText = new JLabel();
        JButton moreOptionsButton = new HoverableButton(MORE_OPTIONS_ICON);
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem removePlaylistMenuItem = new JMenuItem("Remove");
        JMenuItem deleteFromDirectoryMenuItem = new JMenuItem("Delete");
        JMenuItem editPlaylistMenuItem = new JMenuItem("Edit");

        playlistCoverLabel.setIcon(GUIUtil.createResizedIcon(playlistCoverImageIcon, PLAYLIST_SELECTION_ICON_SIZE, PLAYLIST_SELECTION_ICON_SIZE, Image.SCALE_SMOOTH));
        playlistNameLabel.setFont(PLAYLIST_SELECTION_TITLE_FONT);
        playlistDirectoryText.setOpaque(false);
        playlistDirectoryText.setFont(PLAYLIST_DESCRIPTION_FONT);
        playlistDirectoryText.setText(playlist.getDirectory() == null || playlist.getDirectory().isEmpty() 
            ? "[No Directory]" 
            : GUIUtil.truncateWithEllipsis(playlist.getDirectory(), playlistDirectoryText.getFontMetrics(PLAYLIST_DESCRIPTION_FONT), PLAYLIST_SELECTION_INFO_MIN_WIDTH_PX)
        );
        // playlistDirectoryText.setLineWrap(true);
        // playlistDirectoryText.setWrapStyleWord(true);
        // playlistDirectoryText.setEditable(false);
        playlistDirectoryText.setFocusable(false);
        playlistDirectoryText.setForeground(Color.gray);
        // playlistDirectoryText.setMargin(new Insets(0, 0, 0, 0));

        playlistPanel.add(
            playlistCoverLabel, ""
            + "cell 0 0, "
            + "span 2 2, "
            + "align center center, "
        );

        playlistPanel.add(
            playlistNameLabel, ""
            + "cell 2 0, "
            + "span 1 1, "
            + "align left bottom, "
            + "pushy 0.55, "
            + "wmin " + PLAYLIST_SELECTION_INFO_MIN_WIDTH_PX + ", "
            + "wmax " + PLAYLIST_SELECTION_INFO_MIN_WIDTH_PX + ", "
        );

        playlistPanel.add(
            playlistDirectoryText, ""
            + "cell 2 1, "
            + "span 2 1, "
            + "align left center, "
            + "pushy 0.45, growy, "
            + "wmin " + PLAYLIST_SELECTION_INFO_MIN_WIDTH_PX + ", "
            + "wmax " + PLAYLIST_SELECTION_INFO_MIN_WIDTH_PX + ", "
        );

        playlistPanel.add(
            moreOptionsButton, ""
            + "cell 3 0, "
            + "span 1 1, "
            + "align right top, "
        );

        popupMenu.add(editPlaylistMenuItem);
        popupMenu.add(removePlaylistMenuItem);
        popupMenu.add(deleteFromDirectoryMenuItem);

        //---------------------------------START ADDING LISTENERS---------------------------------

        playlistPanel.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {

                if(e.getButton() != MouseEvent.BUTTON1) {

                    return;

                }

                if(!playlist.directoryExists()) {

                    JOptionPane.showMessageDialog(
                        StaccatoWindow.staccatoWindow, 
                        "<html>" + playlist.getName() + " could not be found at<br></br><i>" + playlist.getDirectory() + "</i>", 
                        "Couldn't Find Playlist", 
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;

                }

                SwingUtilities.invokeLater(() -> {

                    initTracklistPage(playlist);
                    
                });

            }

            @Override
            public void mouseEntered(MouseEvent e) {

                playlistPanel.setBackground(HIGHLIGHTED_PLAYLIST_BUTTON_COLOR);
                if(playlistPanel.isEnabled()) {

                    playlistPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                }

            }

            @Override
            public void mouseExited(MouseEvent e) {

                playlistPanel.setBackground(getBackground());
                playlistPanel.setCursor(Cursor.getDefaultCursor());

            }

        });

        moreOptionsButton.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {

                if(e.getButton() == MouseEvent.BUTTON1) {

                    popupMenu.show(moreOptionsButton, e.getX(), e.getY());

                }

            }

        });

        editPlaylistMenuItem.addActionListener((unused) -> {

            GUIUtil.createPlaylistEditorPopup(playlist);

        });

        removePlaylistMenuItem.addActionListener((unused) -> {

            boolean removalWasSuccess;
            try {

                removalWasSuccess = FileManager.removePlaylist(playlist);

            } catch (IOException e) {

                e.printStackTrace();
                JOptionPane.showMessageDialog(
                    StaccatoWindow.staccatoWindow, 
                    "<html>Could not remove " + playlist.getName() + ".<br></br>Details: " + e.getMessage() + "", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
                return;

            }

            if(removalWasSuccess) {

                JOptionPane.showMessageDialog(
                    StaccatoWindow.staccatoWindow, 
                    "Removed " + playlist.getName() + ".", 
                    "Removed Playlist", 
                    JOptionPane.INFORMATION_MESSAGE
                );
                initHomePage();

            } else {

                JOptionPane.showMessageDialog(
                    StaccatoWindow.staccatoWindow, 
                    "<html>Could not remove " + playlist.getName() + " because it was not found.</html>", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );

            }

        });

        deleteFromDirectoryMenuItem.addActionListener((unused) -> {

            int confirmationResult = JOptionPane.showConfirmDialog(
                StaccatoWindow.staccatoWindow, 
                "<html>Are you sure you want to delete the following playlist and its files?<br></br><br></br>" + playlist.getName() + "<br></br><i>" + playlist.getDirectory() + "</i><br></br><br></br><b>This process is irreversible.</b></html>", 
                "Confirm Playlist Deletion", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE
            );

            if(confirmationResult == JOptionPane.YES_OPTION) {

                boolean directoryWasFullyDeleted;
                try {

                    directoryWasFullyDeleted = FileManager.deletePlaylist(playlist);
                    FileManager.removePlaylist(playlist); //Then remove playlist from staccato

                } catch (FileNotFoundException e) {

                    JOptionPane.showMessageDialog(
                        StaccatoWindow.staccatoWindow, 
                        "<html>Could not delete " + playlist.getName() + " because the directory <br></br><i>" + playlist.getDirectory() + "</i><br></br>was not found.</html>", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;

                } catch (IOException e) {

                    JOptionPane.showMessageDialog(
                        StaccatoWindow.staccatoWindow, 
                        "<html>Could not delete " + playlist.getName() + ".<br></br>Details: " + e.getMessage() + "", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;

                }

                if(directoryWasFullyDeleted) {

                    JOptionPane.showMessageDialog(
                        StaccatoWindow.staccatoWindow, 
                        "<html>Deleted " + playlist.getName() + "'s directory <br></br><i>" + playlist.getDirectory() + "</html>", 
                        "Deleted Playlist", 
                        JOptionPane.INFORMATION_MESSAGE
                    );

                } else {

                    JOptionPane.showMessageDialog(
                        StaccatoWindow.staccatoWindow, 
                        "<html>Deleted " + playlist.getName() + "'s music files, directory left undeleted at <br></br><i>" + playlist.getDirectory() + "</html>", 
                        "Deleted Playlist", 
                        JOptionPane.INFORMATION_MESSAGE
                    );

                }

                initHomePage();

            }

        });

        //----------------------------------END ADDING LISTENERS----------------------------------

        return playlistPanel;

    }

    private JPanel createTrackEntry(Track track, int trackIndex, Color defaultBackgroundColor) {

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
        JButton moreOptionsButton = new HoverableButton(MORE_OPTIONS_ICON);
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem redownloadTrackMenuItem = new JMenuItem("Redownload");
        JMenuItem editMetadataMenuItem = new JMenuItem("Edit Info");
        JMenuItem deleteTrackMenuItem = new JMenuItem("Delete");

        titleAndArtistsPanel.setLayout(new BoxLayout(titleAndArtistsPanel, BoxLayout.Y_AXIS));
        titleAndArtistsPanel.setOpaque(false);
        artworkLabel.setPreferredSize(new Dimension(TRACK_ARTWORK_WIDTH_PX, TRACK_ARTWORK_WIDTH_PX));
        artworkIcon = GUIUtil.createResizedIcon(artworkIcon, TRACK_ARTWORK_WIDTH_PX, TRACK_ARTWORK_WIDTH_PX, Image.SCALE_SMOOTH);
        artworkLabel.setIcon(artworkIcon);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        titleLabel.setFont(TRACK_TITLE_FONT);
        artistsLabel.setAlignmentX(LEFT_ALIGNMENT);
        artistsLabel.setFont(TRACK_ARTISTS_FONT);
        albumLabel.setAlignmentX(LEFT_ALIGNMENT);
        albumLabel.setFont(TRACK_ALBUM_FONT);

        trackPanel.add(artworkLabel, "cell 0 0");
        titleAndArtistsPanel.add(titleLabel);
        titleAndArtistsPanel.add(artistsLabel);
        trackPanel.add(titleAndArtistsPanel, "cell 1 0, pushx, wmax " + (int) (TRACK_TITLE_ARTISTS_COLUMN_WIDTH_PROPORTION * 100) + "%");
        trackPanel.add(albumLabel, "cell 2 0, wmax " + (int) (TRACK_ALBUM_COLUMN_WIDTH_PROPORTION * 100) + "%");
        trackPanel.add(moreOptionsButton, "cell 3 0, gapright " + TRACKLIST_ROW_TO_PANEL_EDGE_SPACING_PX);
        popupMenu.add(redownloadTrackMenuItem);
        popupMenu.add(editMetadataMenuItem);
        popupMenu.add(deleteTrackMenuItem);

        trackPanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if(e.getClickCount() < 2) {

                    return;

                }

                TracklistPlayer.playTracks(currentTracklist, trackIndex);

            }

            @Override
            public void mouseEntered(MouseEvent e) {

                trackPanel.setBackground(HIGHLIGHTED_TRACKLIST_ROW_COLOR);
                if(trackPanel.isEnabled()) {

                    trackPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                }

            }

            @Override
            public void mouseExited(MouseEvent e) {

                trackPanel.setBackground(defaultBackgroundColor);
                trackPanel.setCursor(Cursor.getDefaultCursor());

            }

        });

        moreOptionsButton.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {

                if(e.getButton() == MouseEvent.BUTTON1) {

                    popupMenu.show(moreOptionsButton, e.getX(), e.getY());

                }

            }

        });

        redownloadTrackMenuItem.addActionListener((unused) -> {

            GUIUtil.createRedownloadPopup();

        });

        editMetadataMenuItem.addActionListener((unused) -> {

            GUIUtil.createEditMetadataDialog(track, trackPanel);

        });

        deleteTrackMenuItem.addActionListener((unused) -> {

            int confirmationResult = JOptionPane.showConfirmDialog(
                StaccatoWindow.staccatoWindow, 
                "<html>Are you sure you want to remove the following track?<br></br><br></br>" + track.getTitle() + "<br></br><i>" + track.getFileLocation() + "</i><br></br><br></br><b>This will also delete the track from your file system.</b></html>", 
                "Confirm Track Deletion", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE
            );

            if(confirmationResult != JOptionPane.YES_OPTION) {

                return;

            }

            try {

                FileManager.deleteTrack(track);
                SwingUtilities.invokeLater(() -> {

                    tracklistPanel.remove(trackPanel);
                    tracklistPanel.revalidate();
                    tracklistPanel.repaint();

                });
                
                JOptionPane.showMessageDialog(
                    StaccatoWindow.staccatoWindow, 
                    "<html>Deleted " + track.getTitle() + ".</html>", 
                    "Deleted Track", 
                    JOptionPane.INFORMATION_MESSAGE
                );

            } catch (FileNotFoundException e) {

                JOptionPane.showMessageDialog(
                    StaccatoWindow.staccatoWindow, 
                    "<html>Track was not deleted: file wasn't found at <br></br><i>" + track.getFileLocation() + "</i></html>", 
                    "Couldn't Delete Track", 
                    JOptionPane.ERROR_MESSAGE
                );

            } catch (SecurityException e) {

                JOptionPane.showMessageDialog(
                    StaccatoWindow.staccatoWindow, 
                    "<html>Track was not deleted: needs security permissions at <br></br><i>" + track.getFileLocation() + "</i></html>", 
                    "Couldn't Delete Track", 
                    JOptionPane.ERROR_MESSAGE
                );
                
            }
            
        });

        return trackPanel;

    }

    public static void updateTrackPanel(JPanel trackPanel, Track track) {

        //Artwork label
        ImageIcon artworkIcon;
        if(track.getArtworkByteArray() == null) {

            artworkIcon = GUIUtil.createResizedIcon(PLACEHOLDER_ART_ICON, TRACK_ARTWORK_WIDTH_PX, TRACK_ARTWORK_WIDTH_PX, Image.SCALE_SMOOTH);

        } else {

            artworkIcon = GUIUtil.createResizedIcon(new ImageIcon(track.getArtworkByteArray()), TRACK_ARTWORK_WIDTH_PX, TRACK_ARTWORK_WIDTH_PX, Image.SCALE_SMOOTH);

        }
        ((JLabel) trackPanel.getComponent(0)).setIcon(artworkIcon);

        //Title label
        ((JLabel) ((JPanel) trackPanel.getComponent(1)).getComponent(0)).setText(track.getTitle());

        //Artists label
        ((JLabel) ((JPanel) trackPanel.getComponent(1)).getComponent(1)).setText(track.getArtists());

        //Album label
        ((JLabel) trackPanel.getComponent(2)).setText(track.getAlbum());

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
