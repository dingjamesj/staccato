package gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;

import main.FileManager;
import main.Playlist;
import main.Track;
import net.miginfocom.swing.MigLayout;

public abstract class GUIUtil {
    
    private static final Dimension PLAYLIST_ADDER_DIALOG_WINDOW_SIZE = new Dimension(310, 555);
    private static final Dimension PLAYLIST_EDITOR_DIALOG_WINDOW_SIZE = new Dimension(320, 420);
    private static final Dimension REDOWNLOAD_DIALOG_WINDOW_SIZE = new Dimension(300, 175);
    private static final Dimension EDIT_METADATA_WINDOW_SIZE = new Dimension(400, 275);
    private static final int REDOWNLOAD_FIELD_GAPRIGHT_PX = 55;
    private static final int REDOWNLOAD_PROGRESSBAR_GAPTOP_PX = 10;
    private static final int BOTTOM_BUTTONS_GAP_PX = 5;
    private static final int EDIT_METADATA_ARTWORK_BUTTON_SIZE_PX = 64;
    private static final int EDIT_METADATA_ARTWORK_BUTTON_TO_LABEL_GAP_PX = 3;
    private static final int EDIT_METADATA_ARTWORK_URL_LABEL_MAX_WIDTH_PX = 240;
    private static final int MIN_VERTICAL_GAP_PX = 5;
    private static final int SECTION_VERTICAL_GAP_PX = 8;
    private static final int PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX = 200;
    private static final int SCROLL_SPEED = 3;
    private static final int CURRENT_PLAYLIST_DIRECTORY_LABEL_MAX_WIDTH_PX = 200;
    private static final int PLAYLIST_NAME_LABEL_MAX_WIDTH_PX = 280;

    private static final ImageIcon PLACEHOLDER_ART_ICON = createImageIcon("src/main/resources/placeholder art.png");
    private static final ImageIcon EDIT_METADATA_ADD_ARTWORK_ICON = createImageIcon("src/main/resources/refresh.png");
    private static final Font POPUP_TITLE_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 24);

    public static ImageIcon createResizedIcon(ImageIcon imageIcon, int width, int height, int rescalingAlgorithm) {

        return new ImageIcon(imageIcon.getImage().getScaledInstance(width, height, rescalingAlgorithm));

    }

    public static ImageIcon createImageIcon(String urlStr) {

        URL url = PlaybarPanel.class.getResource(urlStr);
        if(url != null) {

            return new ImageIcon(url);

        }

        return new ImageIcon(urlStr);

    }

    public static String truncateWithEllipsis(String str, JComponent component, int width) {

        FontMetrics fontMetrics = component.getFontMetrics(component.getFont());

        if(fontMetrics.stringWidth(str) <= width) {

            return str;

        }

        for(int i = str.length(); i >= 0; i--) {

            String subStr = str.substring(0, i) + "...";
            if(fontMetrics.stringWidth(subStr) <= width) {

                return subStr;

            }

        }

        return "";

    }

    public static int calculateTextWidth(JLabel label) {

        return label.getFontMetrics(label.getFont()).stringWidth(label.getText());

    }

    public static int getFittingJLabelFontSize(JLabel label) {

        String labelText = label.getText();
        Font originalFont = label.getFont();
        int currentStringWidth = label.getFontMetrics(originalFont).stringWidth(labelText);
        if(currentStringWidth <= label.getWidth()) {

            return originalFont.getSize();

        }

        float testFontSize = originalFont.getSize() * label.getWidth() / (float) label.getFontMetrics(originalFont).stringWidth(labelText);
        if(label.getFontMetrics(originalFont.deriveFont((float) testFontSize)).stringWidth(labelText) < label.getWidth()) {

            //This increments the test font size until the label text will overflow
            for(
                testFontSize = testFontSize + 1; 
                label.getFontMetrics(originalFont.deriveFont((float) testFontSize)).stringWidth(labelText) < label.getWidth(); 
                testFontSize++
            ) {}

        } else if(label.getFontMetrics(originalFont.deriveFont((float) testFontSize)).stringWidth(labelText) > label.getWidth()) {

            //This decrements the test font size until the label text doesn't overflow
            for(
                testFontSize = testFontSize - 1; 
                label.getFontMetrics(originalFont.deriveFont((float) testFontSize)).stringWidth(labelText) > label.getWidth(); 
                testFontSize--
            ) {}

        }

        return (int) testFontSize;

    }

    public static JDialog createAddPlaylistDialog() {

        InternalDataDialog dialog = new InternalDataDialog(StaccatoWindow.staccatoWindow, true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Add Playlist");
        dialog.setLayout(new MigLayout("gap 0 " + MIN_VERTICAL_GAP_PX));
        dialog.setResizable(false);
        dialog.setSize(PLAYLIST_ADDER_DIALOG_WINDOW_SIZE);

        //Import playlist:
        // - Cover art
        // - Name
        // - Directory

        //Create new playlist:
        // - Cover art
        // - Name
        // - Directory to create new folder
        // - Spotify URL

        JLabel titleLabel = new JLabel("Add Playlist");
        JRadioButton createNewButton = new JRadioButton("Create new playlist from scratch");
        JRadioButton importExistingButton = new JRadioButton("Import existing mp3 folder");
        ButtonGroup buttonGroup = new ButtonGroup();
        JLabel playlistCoverLabel = new JLabel(createResizedIcon(PLACEHOLDER_ART_ICON, PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX, PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX, Image.SCALE_SMOOTH));
        JButton removeCoverButton = new JButton("Remove Cover Photo");
        JLabel nameLabel = new JLabel("Name: ");
        JTextField nameField = new JTextField(12);
        JLabel directoryLabel = new JLabel("Directory: ");
        JButton chooseDirectoryButton = new JButton(UIManager.getIcon("Tree.closedIcon"));
        JLabel currentDirectoryLabel = new JLabel();
        JLabel spotifyURLLabel = new JLabel("Spotify URL: ");
        JTextField spotifyURLField = new JTextField(12);
        JLabel resultingActionLabel = new JLabel("<html><i>staccato will create a new folder in [directory] and download songs from a Spotify playlist.</html>");
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        titleLabel.setFont(POPUP_TITLE_LABEL_FONT);
        buttonGroup.add(createNewButton);
        buttonGroup.add(importExistingButton);
        buttonGroup.setSelected(createNewButton.getModel(), true);

        dialog.add(
            titleLabel, ""
            + "cell 0 0, "
            + "span 2 1, "
            + "gapbottom " + SECTION_VERTICAL_GAP_PX + ", "
        );

        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.Y_AXIS));
        radioButtonPanel.add(createNewButton);
        radioButtonPanel.add(importExistingButton);
        dialog.add(
            radioButtonPanel, ""
            + "cell 0 1, "
            + "span 2 1, "
            + "gapbottom " + SECTION_VERTICAL_GAP_PX + ", "
        );

        dialog.add(
            playlistCoverLabel, ""
            + "cell 0 2, "
            + "span 2 1, "
        );

        dialog.add(
            removeCoverButton, ""
            + "cell 0 3, "
            + "span 1 1, "
            + "gapbottom 2, "
            + "gapbottom " + SECTION_VERTICAL_GAP_PX + ", "
        );

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        dialog.add(
            namePanel, ""
            + "cell 0 4, "
            + "span 1 1, "
        );

        JPanel chooseDirectoryPanel = new JPanel();
        chooseDirectoryPanel.setLayout(new BoxLayout(chooseDirectoryPanel, BoxLayout.X_AXIS));
        chooseDirectoryPanel.add(directoryLabel);
        chooseDirectoryPanel.add(currentDirectoryLabel);
        chooseDirectoryPanel.add(Box.createHorizontalStrut(5));
        chooseDirectoryPanel.add(chooseDirectoryButton);
        dialog.add(
            chooseDirectoryPanel, ""
            + "cell 0 5, "
            + "span 1 1, "
            + "gapbottom " + SECTION_VERTICAL_GAP_PX + ", "
        );

        JPanel spotifyURLPanel = new JPanel();
        spotifyURLPanel.setLayout(new BoxLayout(spotifyURLPanel, BoxLayout.X_AXIS));
        spotifyURLPanel.add(spotifyURLLabel);
        spotifyURLPanel.add(spotifyURLField);
        dialog.add(
            spotifyURLPanel, ""
            + "cell 0 6, "
            + "span 1 1, "
            + "gapbottom " + SECTION_VERTICAL_GAP_PX + ", "
        );

        dialog.add(
            resultingActionLabel, ""
            + "cell 0 7, "
            + "span 1 1, "
            + "gapbottom " + SECTION_VERTICAL_GAP_PX + ", "
        );

        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.X_AXIS));
        bottomButtonPanel.add(Box.createHorizontalGlue());
        bottomButtonPanel.add(saveButton);
        bottomButtonPanel.add(Box.createHorizontalStrut(BOTTOM_BUTTONS_GAP_PX));
        bottomButtonPanel.add(cancelButton);
        dialog.add(
            bottomButtonPanel, ""
            + "cell 0 8, "
            + "span 1 1, "
            + "pushx, pushy, "
            + "align right bottom, "
        );

        //--------------------------START ADDING ACTION LISTENERS--------------------------

        dialog.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeDialog");
        dialog.getRootPane().getActionMap().put("closeDialog", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {

                dialog.dispose();

            }

        });

        createNewButton.addActionListener((unused) -> {

            spotifyURLLabel.setEnabled(true);
            spotifyURLField.setEnabled(true);

        });

        importExistingButton.addActionListener((unused) -> {

            spotifyURLLabel.setEnabled(false);
            spotifyURLField.setEnabled(false);

        });

        //---------------------------END ADDING ACTION LISTENERS---------------------------

        dialog.setLocationRelativeTo(StaccatoWindow.staccatoWindow);
        dialog.setVisible(true);

        return dialog;

    }

    public static JDialog createPlaylistEditorPopup(Playlist playlist, int playlistIndex) {

        InternalDataDialog dialog = new InternalDataDialog(StaccatoWindow.staccatoWindow, true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Edit Playlist");
        dialog.setLayout(new MigLayout("gap 0 " + MIN_VERTICAL_GAP_PX));
        dialog.setResizable(false);
        dialog.setSize(PLAYLIST_EDITOR_DIALOG_WINDOW_SIZE);
        dialog.setByteArray(playlist.getCoverArtByteArray());

        JLabel titleLabel = new JLabel();
        ImageIcon playlistCoverIcon = playlist.getCoverArtByteArray() != null ? new ImageIcon(playlist.getCoverArtByteArray()) : PLACEHOLDER_ART_ICON;
        JLabel playlistCoverLabel = new JLabel();
        JButton removeCoverButton = new JButton("Remove Cover Photo");
        JLabel nameLabel = new JLabel("Name: ");
        JTextField nameField = new JTextField(12);
        JLabel chooseDirectoryLabel = new JLabel("Directory: ");
        JButton chooseDirectoryButton = new JButton(UIManager.getIcon("Tree.closedIcon"));
        JLabel currentDirectoryLabel = new JLabel();
        JScrollPane currentDirectoryScrollPane = new InvisibleScrollPane(currentDirectoryLabel);
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        playlistCoverLabel.setIcon(createResizedIcon(playlistCoverIcon, PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX, PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX, Image.SCALE_SMOOTH));
        nameField.setText(playlist.getName());
        currentDirectoryLabel.setText(playlist.getDirectory());
        titleLabel.setFont(POPUP_TITLE_LABEL_FONT);
        titleLabel.setText(GUIUtil.truncateWithEllipsis("Edit " + playlist.getName(), titleLabel, PLAYLIST_NAME_LABEL_MAX_WIDTH_PX));

        dialog.add(
            titleLabel, ""
            + "cell 0 0, "
            + "span 2 1, "
            + "gapbottom " + SECTION_VERTICAL_GAP_PX + ", "
        );

        dialog.add(
            playlistCoverLabel, ""
            + "cell 0 1, "
            + "span 2 1, "
        );

        dialog.add(
            removeCoverButton, ""
            + "cell 0 2, "
            + "span 1 1, "
            + "gapbottom " + SECTION_VERTICAL_GAP_PX + ", "
        );

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        dialog.add(
            namePanel, ""
            + "cell 0 3, "
            + "span 1 1, "
        );

        JPanel chooseDirectoryPanel = new JPanel();
        chooseDirectoryPanel.setLayout(new BoxLayout(chooseDirectoryPanel, BoxLayout.X_AXIS));
        chooseDirectoryPanel.add(chooseDirectoryLabel);
        chooseDirectoryPanel.add(currentDirectoryScrollPane);
        chooseDirectoryPanel.add(Box.createHorizontalStrut(5));
        chooseDirectoryPanel.add(chooseDirectoryButton);
        dialog.add(
            chooseDirectoryPanel, ""
            + "cell 0 4, "
            + "span 1 1, "
        );

        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.X_AXIS));
        bottomButtonPanel.add(Box.createHorizontalGlue());
        bottomButtonPanel.add(saveButton);
        bottomButtonPanel.add(Box.createHorizontalStrut(BOTTOM_BUTTONS_GAP_PX));
        bottomButtonPanel.add(cancelButton);
        dialog.add(
            bottomButtonPanel, ""
            + "cell 0 5, "
            + "span 1 1, "
            + "pushx, pushy, "
            + "align right bottom, "
        );

        //--------------------------START ADDING ACTION LISTENERS--------------------------

        dialog.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeDialog");
        dialog.getRootPane().getActionMap().put("closeDialog", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {

                dialog.dispose();

            }

        });

        playlistCoverLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if(e.getClickCount() != 1) {

                    return;

                }

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Images (*.jpg, *.png, *.jpeg)", "jpg", "png", "jpeg"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(false);
                int result = fileChooser.showOpenDialog(dialog);

                if(result == JFileChooser.APPROVE_OPTION) {

                    File selectedFile = fileChooser.getSelectedFile();

                    try {

                        byte[] coverArtByteArray = FileManager.readByteArray(selectedFile);
                        playlistCoverLabel.setIcon(createResizedIcon(new ImageIcon(coverArtByteArray), PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX, PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX, Image.SCALE_SMOOTH));
                        dialog.setByteArray(coverArtByteArray);

                    } catch (IOException error) {

                        error.printStackTrace();

                    }

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

        removeCoverButton.addActionListener((unused) -> {

            playlistCoverLabel.setIcon(createResizedIcon(PLACEHOLDER_ART_ICON, PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX, PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX, Image.SCALE_SMOOTH));
            dialog.setByteArray(null);

        });

        chooseDirectoryButton.addActionListener((unused) -> {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            int result = fileChooser.showOpenDialog(dialog);

            if(result == JFileChooser.APPROVE_OPTION) {

                File selectedDirectory = fileChooser.getSelectedFile();
                currentDirectoryLabel.setText(selectedDirectory.getAbsolutePath());

            }

        });
        
        cancelButton.addActionListener((unused) -> {

            dialog.dispose();

        });
        
        saveButton.addActionListener((unused) -> {

            playlist.setName(nameField.getText().strip());
            playlist.setCoverArtByteArray(dialog.getByteArray());

            try {

                //If the user changed the playlist's directory, then we need to create a new playlist

                File originalDirectory = new File(playlist.getDirectory());
                File newDirectory = new File(currentDirectoryLabel.getText());
                String newCanonicalPath = newDirectory.getCanonicalPath();
                boolean editWasSuccessful;

                if(!originalDirectory.getCanonicalPath().equals(newCanonicalPath)) {

                    Playlist newPlaylist = new Playlist(newCanonicalPath);
                    newPlaylist.setName(nameField.getText().strip());
                    newPlaylist.setCoverArtByteArray(dialog.getByteArray());
                    editWasSuccessful = FileManager.replacePlaylist(playlist, newPlaylist);

                    if(MainPanel.mainPanel.getIsOnTracklistView()) {

                        MainPanel.mainPanel.initTracklistPage(newPlaylist);

                    } else {

                        MainPanel.mainPanel.updatePlaylistButton(newPlaylist, playlistIndex);

                    }

                } else {

                    editWasSuccessful = FileManager.replacePlaylist(playlist, playlist);
                    
                    if(MainPanel.mainPanel.getIsOnTracklistView()) {

                        MainPanel.mainPanel.updatePlaylistInfoPanel(dialog.getByteArray(), playlist.getName());

                    } else {

                        MainPanel.mainPanel.updatePlaylistButton(playlist, playlistIndex);

                    }

                }

                if(!editWasSuccessful) {

                    dialog.dispose();
                    JOptionPane.showMessageDialog(
                        StaccatoWindow.staccatoWindow, 
                        "Changes not saved: couldn't find original playlist in the playlists file.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;

                }

            } catch (IOException e) {

                e.printStackTrace();
                dialog.dispose();
                JOptionPane.showMessageDialog(
                    StaccatoWindow.staccatoWindow, 
                    "Could not save changes to playlist.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
                return;

            }

            dialog.dispose();

        });

        //---------------------------END ADDING ACTION LISTENERS---------------------------
        
        dialog.setLocationRelativeTo(StaccatoWindow.staccatoWindow);
        dialog.setVisible(true);

        return dialog;

    }

    public static JDialog createRedownloadPopup() {

        JDialog dialog = new JDialog(StaccatoWindow.staccatoWindow, true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Redownload Track");
        dialog.setLayout(new MigLayout());
        dialog.setResizable(false);
        dialog.setSize(REDOWNLOAD_DIALOG_WINDOW_SIZE);

        JLabel titleLabel = new JLabel("New YouTube URL:");
        JTextField urlField = new JTextField();
        JProgressBar downloadProgressBar = new JProgressBar();
        JButton downloadButton = new JButton("Download");
        JButton okButton = new JButton("OK");

        titleLabel.setFont(POPUP_TITLE_LABEL_FONT);

        dialog.add(
            titleLabel, ""
            + "cell 0 0, "
            + "span 2 1, "
            + "growx"
        );

        dialog.add(
            urlField, ""
            + "cell 0 1, "
            + "span 2 1, "
            + "growx, "
            + "gapright " + REDOWNLOAD_FIELD_GAPRIGHT_PX
        );

        dialog.add(
            downloadProgressBar, ""
            + "cell 0 2, "
            + "span 2 1, "
            + "growx, "
            + "gapright " + REDOWNLOAD_FIELD_GAPRIGHT_PX + ", "
            + "gaptop " + REDOWNLOAD_PROGRESSBAR_GAPTOP_PX
        );

        dialog.add(
            downloadButton, ""
            + "cell 0 3, "
            + "span 1 1, "
            + "align right bottom, "
            + "pushx, pushy"
        );

        dialog.add(
            okButton, ""
            + "cell 1 3, "
            + "span 1 1, "
            + "align right bottom, "
            + "pushy"
        );

        //--------------------------START ADDING ACTION LISTENERS--------------------------

        okButton.addActionListener((unused) -> {

            dialog.dispose();

        });

        //---------------------------END ADDING ACTION LISTENERS---------------------------

        dialog.setLocationRelativeTo(StaccatoWindow.staccatoWindow);
        dialog.setVisible(true);

        return dialog;

    }

    public static JDialog createEditMetadataDialog(Track track, JPanel trackPanel) {

        InternalDataDialog dialog = new InternalDataDialog(StaccatoWindow.staccatoWindow, true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Edit Metadata");
        dialog.setLayout(new MigLayout("gap 0 " + MIN_VERTICAL_GAP_PX));
        dialog.setResizable(false);
        dialog.setSize(EDIT_METADATA_WINDOW_SIZE);
        dialog.setByteArray(track.getArtworkByteArray());

        JLabel popupTitleLabel = new JLabel("Edit Track Information");
        JLabel titleLabel = new JLabel("Title: ");
        JLabel artistsLabel = new JLabel("Artists: ");
        JLabel albumLabel = new JLabel("Album: ");
        JLabel artworkLabel = new JLabel("Artwork: ");
        JTextField titleField = new JTextField();
        JTextField artistsField = new JTextField();
        JTextField albumField = new JTextField();
        JPanel artworkOptionsPanel = new JPanel();
        JButton artworkButton = new JButton();
        JLabel artworkURLLabel = new JLabel();
        JPanel bottomButtonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        popupTitleLabel.setFont(POPUP_TITLE_LABEL_FONT);
        bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.X_AXIS));
        titleField.setText(track.getTitle());
        artistsField.setText(track.getArtists());
        albumField.setText(track.getAlbum());
        artworkOptionsPanel.setLayout(new MigLayout("insets 0, gap 0"));
        ImageIcon artworkIcon;
        if(track.getArtworkByteArray() == null) {

            artworkIcon = EDIT_METADATA_ADD_ARTWORK_ICON;

        } else {

            artworkIcon = createResizedIcon(new ImageIcon(track.getArtworkByteArray()), EDIT_METADATA_ARTWORK_BUTTON_SIZE_PX, EDIT_METADATA_ARTWORK_BUTTON_SIZE_PX, Image.SCALE_SMOOTH);

        }
        artworkButton.setIcon(artworkIcon);

        dialog.add(
            popupTitleLabel, ""
            + "cell 0 0, "
            + "span 2 1"
        );

        dialog.add(
            titleLabel, ""
            + "cell 0 1, "
            + "span 1 1, "
            + "align left center, "
        );

        dialog.add(
            titleField, ""
            + "cell 1 1, "
            + "span 1 1, "
            + "pushx, growx"
        );

        dialog.add(
            artistsLabel, ""
            + "cell 0 2, "
            + "span 1 1, "
            + "align left center, "
        );

        dialog.add(
            artistsField, ""
            + "cell 1 2, "
            + "span 1 1, "
            + "pushx, growx"
        );

        dialog.add(
            albumLabel, ""
            + "cell 0 3, "
            + "span 1 1, "
            + "align left center, "
        );

        dialog.add(
            albumField, ""
            + "cell 1 3, "
            + "span 1 1, "
            + "pushx, growx"
        );

        dialog.add(
            artworkLabel, ""
            + "cell 0 4, "
            + "span 1 1, "
            + "align left center, "
        );

        artworkOptionsPanel.add(artworkButton, "cell 0 0, align left center, gapright " + EDIT_METADATA_ARTWORK_BUTTON_TO_LABEL_GAP_PX);
        artworkOptionsPanel.add(artworkURLLabel, "cell 1 0, align left center, growx 0, wmax " + EDIT_METADATA_ARTWORK_URL_LABEL_MAX_WIDTH_PX);
        dialog.add(
            artworkOptionsPanel, ""
            + "cell 1 4, "
            + "span 1 1, "
            + "pushx"
        );

        bottomButtonPanel.add(Box.createHorizontalGlue());
        bottomButtonPanel.add(saveButton);
        bottomButtonPanel.add(Box.createHorizontalStrut(BOTTOM_BUTTONS_GAP_PX));
        bottomButtonPanel.add(cancelButton);
        dialog.add(
            bottomButtonPanel, ""
            + "cell 1 5, "
            + "span 1 1, "
            + "pushx, pushy, "
            + "align right bottom"
        );

        //--------------------------START ADDING ACTION LISTENERS--------------------------
        
        cancelButton.addActionListener((unused) -> {

            dialog.dispose();

        });

        saveButton.addActionListener((unused) -> {

            track.setTitle(titleField.getText());
            track.setArtists(artistsField.getText());
            track.setAlbum(albumField.getText());
            track.setArtworkByteArray(dialog.getByteArray());
            track.writeMetadata();
            MainPanel.updateTrackPanel(trackPanel, track);
            dialog.dispose();

        });

        artworkButton.addActionListener((unused) -> {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images (*.jpg, *.png, *.jpeg)", "jpg", "png", "jpeg"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            int result = fileChooser.showOpenDialog(dialog);

            if(result == JFileChooser.APPROVE_OPTION) {

                File selectedFile = fileChooser.getSelectedFile();
                try {

                    byte[] artworkByteArray = FileManager.readByteArray(selectedFile);
                    artworkButton.setIcon(createResizedIcon(new ImageIcon(artworkByteArray), EDIT_METADATA_ARTWORK_BUTTON_SIZE_PX, EDIT_METADATA_ARTWORK_BUTTON_SIZE_PX, Image.SCALE_SMOOTH));
                    artworkURLLabel.setText(selectedFile.getCanonicalPath());
                    dialog.setByteArray(artworkByteArray);

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

        });

        //---------------------------END ADDING ACTION LISTENERS---------------------------

        dialog.setLocationRelativeTo(StaccatoWindow.staccatoWindow);
        dialog.setVisible(true);

        return dialog;

    }

    /**
     * 
     */
    public static class HoverableButton extends JButton {

        public HoverableButton() {

            super();
            addMouseListener(new MouseAdapter() {
                
                @Override
                public void mouseEntered(MouseEvent e) {

                    if(isEnabled()) {

                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    }

                }

                @Override
                public void mouseExited(MouseEvent e) {

                    setCursor(Cursor.getDefaultCursor());

                }

            });

        }

        public HoverableButton(Icon icon) {

            super(icon);
            addMouseListener(new MouseAdapter() {
                
                @Override
                public void mouseEntered(MouseEvent e) {

                    if(isEnabled()) {

                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    }

                }

                @Override
                public void mouseExited(MouseEvent e) {

                    setCursor(Cursor.getDefaultCursor());

                }

            });

        }

        public HoverableButton(String string) {

            super(string);
            addMouseListener(new MouseAdapter() {
                
                @Override
                public void mouseEntered(MouseEvent e) {

                    if(isEnabled()) {

                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    }

                }

                @Override
                public void mouseExited(MouseEvent e) {

                    setCursor(Cursor.getDefaultCursor());

                }

            });

        }

        public HoverableButton(Action action) {

            super(action);
            addMouseListener(new MouseAdapter() {
                
                @Override
                public void mouseEntered(MouseEvent e) {

                    if(isEnabled()) {

                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    }

                }

                @Override
                public void mouseExited(MouseEvent e) {

                    setCursor(Cursor.getDefaultCursor());

                }

            });

        }

        public HoverableButton(String text, Icon icon) {

            super(text, icon);
            addMouseListener(new MouseAdapter() {
                
                @Override
                public void mouseEntered(MouseEvent e) {

                    if(isEnabled()) {

                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    }

                }

                @Override
                public void mouseExited(MouseEvent e) {

                    setCursor(Cursor.getDefaultCursor());

                }

            });

        }

    }

    /**
     * Just JDialog with a field to store a byte array
     */
    public static class InternalDataDialog extends JDialog {

        private byte[] byteArray = null;
        private String string = null;

        public InternalDataDialog(Frame owner, boolean modal) {

            super(owner, modal);

        }

        public byte[] getByteArray() {

            return byteArray;

        }

        public String getString() {

            return string;

        }

        public void setByteArray(byte[] byteArray) {

            this.byteArray = byteArray;

        }

        public void setString(String string) {

            this.string = string;

        }

    }

    /**
     * JScrollPane that has no scroll bar and no borders
     */
    public static class InvisibleScrollPane extends JScrollPane {

        public InvisibleScrollPane(Component view) {

            super(view);
            setVerticalScrollBar(new InvisibleScrollBar(JScrollBar.VERTICAL));
            setHorizontalScrollBar(new InvisibleScrollBar(JScrollBar.HORIZONTAL));
            setBorder(null);
            setOpaque(false);
            getViewport().setOpaque(false);
            getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);
            getHorizontalScrollBar().setUnitIncrement(SCROLL_SPEED);
            setFocusable(true);
            
            addMouseListener(new MouseAdapter() {
                
                @Override
                public void mouseClicked(MouseEvent e) {

                    requestFocusInWindow();

                }

            });

        }

    }

    private static class InvisibleScrollBar extends JScrollBar {

        public InvisibleScrollBar(int orientation) {

            super(orientation);

            setOpaque(false);
            setPreferredSize(new Dimension(0, 0));
            setMinimumSize(new Dimension(0, 0));
            setMaximumSize(new Dimension(0, 0));

            setUI(new BasicScrollBarUI() {

                @Override
                protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {

                    //Do nothing for no track

                }

                @Override
                protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {

                    // Do nothing for no track

                }

                @Override
                protected JButton createDecreaseButton(int orientation) {

                    return createInvisibleButton();

                }

                @Override
                protected JButton createIncreaseButton(int orientation) {

                    return createInvisibleButton();
                
                }

                private JButton createInvisibleButton() {
                    
                    JButton invisibleButton = new JButton();
                    invisibleButton.setPreferredSize(new Dimension(0, 0));
                    invisibleButton.setMinimumSize(new Dimension(0, 0));
                    invisibleButton.setMaximumSize(new Dimension(0, 0));
                    invisibleButton.setVisible(false);

                    return invisibleButton;

                }
            });

        }

    }

}
