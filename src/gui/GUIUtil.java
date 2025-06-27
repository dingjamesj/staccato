package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;

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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;

import main.FileManager;
import main.Playlist;
import main.Track;
import net.miginfocom.swing.MigLayout;

public abstract class GUIUtil {
    
    //Track adder popup
    private static final Dimension TRACK_ADDER_DIALOG_WINDOW_SIZE = new Dimension(320, 550);
    private static final double IMPORTED_TRACKS_PANEL_HEIGHT_PROPORTION = 0.55;
    private static final int REMOVE_BUTTON_SIZE_PX = 18;
    private static final double FILE_LOCATION_LABEL_SIZE_PROPORTION = 0.87;
    private static final int IMPORT_TRACK_PREVIEW_ICON_SIZE_PX = 84;
    private static final Color HIGHLIGHTED_TRACKLIST_ROW_COLOR = new Color(0x272727);
    private static final Color SELECTED_TRACKLIST_ROW_COLOR = new Color(0x353535);
    private static final Font TRACK_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font TRACK_ARTISTS_FONT = new Font("Segoe UI", Font.ITALIC, 14);
    private static final Font TRACK_ALBUM_FONT = new Font("Segoe UI", Font.ITALIC, 14);

    //Track editor popup
    private static final Dimension EDIT_METADATA_WINDOW_SIZE = new Dimension(400, 275);
    private static final int EDIT_METADATA_ARTWORK_BUTTON_SIZE_PX = 64;
    private static final int EDIT_METADATA_ARTWORK_BUTTON_TO_LABEL_GAP_PX = 3;
    private static final int EDIT_METADATA_ARTWORK_URL_LABEL_MAX_WIDTH_PX = 240;
    private static final ImageIcon EDIT_METADATA_ADD_ARTWORK_ICON = createImageIcon("src/main/resources/add.png");

    //Track redownloader popup
    private static final Dimension REDOWNLOAD_DIALOG_WINDOW_SIZE = new Dimension(300, 175);
    private static final int REDOWNLOAD_FIELD_GAPRIGHT_PX = 55;
    private static final int REDOWNLOAD_PROGRESSBAR_GAPTOP_PX = 10;

    //Playlist adder popup
    private static final Dimension PLAYLIST_ADDER_DIALOG_WINDOW_SIZE = new Dimension(310, 550);

    //Playlist editor popup
    private static final Dimension PLAYLIST_EDITOR_DIALOG_WINDOW_SIZE = new Dimension(320, 420);
    private static final int PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX = 200;
    private static final int PLAYLIST_NAME_LABEL_MAX_WIDTH_PX = 280;
    
    //General
    private static final int BOTTOM_BUTTONS_GAP_PX = 5;
    private static final int MIN_VERTICAL_GAP_PX = 3;
    private static final int SECTION_VERTICAL_GAP_PX = 11;
    private static final int SCROLL_SPEED = 3;
    private static final ImageIcon PLACEHOLDER_ART_ICON = createImageIcon("src/main/resources/placeholder art.png");
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
        dialog.setLayout(new MigLayout("gap 0 " + MIN_VERTICAL_GAP_PX));
        dialog.setResizable(false);
        dialog.setSize(PLAYLIST_ADDER_DIALOG_WINDOW_SIZE);

        JLabel titleLabel = new JLabel("Add Playlist");
        JRadioButton createNewButton = new JRadioButton("Create new playlist from scratch");
        JRadioButton importExistingButton = new JRadioButton("Import existing mp3 folder");
        ButtonGroup buttonGroup = new ButtonGroup();
        JLabel playlistCoverLabel = new JLabel(createResizedIcon(PLACEHOLDER_ART_ICON, PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX, PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX, Image.SCALE_SMOOTH));
        JButton removeCoverButton = new HoverableButton("Remove Cover Photo");
        JLabel nameLabel = new JLabel("Name: ");
        JTextField nameField = new JTextField(12);
        JButton chooseDirectoryButton = new HoverableButton(UIManager.getIcon("Tree.closedIcon"));
        JLabel directoryLabel = new JLabel(System.getProperty("user.dir"));
        JScrollPane directoryScrollPane = new InvisibleScrollPane(directoryLabel);
        JLabel spotifyURLLabel = new JLabel("Spotify URL: ");
        JTextField spotifyURLField = new JTextField(12);
        JLabel resultingActionLabel = new JLabel();
        JButton saveButton = new HoverableButton("Create");
        JButton cancelButton = new HoverableButton("Cancel");
        
        titleLabel.setFont(POPUP_TITLE_LABEL_FONT);
        buttonGroup.add(createNewButton);
        buttonGroup.add(importExistingButton);
        buttonGroup.setSelected(createNewButton.getModel(), true);
        resultingActionLabel.setText(
            createResultingAddPlaylistActionString(directoryLabel.getText(), true, null, resultingActionLabel)
        );
        nameField.putClientProperty("JTextField.placeholderText", "New Playlist");

        dialog.add(
            titleLabel, ""
            + "cell 0 0, "
            + "span 1 1, "
            + "gapbottom " + SECTION_VERTICAL_GAP_PX + ", "
        );

        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.Y_AXIS));
        radioButtonPanel.add(createNewButton);
        radioButtonPanel.add(importExistingButton);
        dialog.add(
            radioButtonPanel, ""
            + "cell 0 1, "
            + "span 1 1, "
            + "gapbottom " + SECTION_VERTICAL_GAP_PX + ", "
        );

        dialog.add(
            playlistCoverLabel, ""
            + "cell 0 2, "
            + "span 1 1, "
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
        chooseDirectoryPanel.add(chooseDirectoryButton);
        chooseDirectoryPanel.add(Box.createHorizontalStrut(MIN_VERTICAL_GAP_PX));
        chooseDirectoryPanel.add(directoryScrollPane);
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
            + "pushy, "
            + "align right bottom, "
            + "gapbottom " + MIN_VERTICAL_GAP_PX + ", "
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
            + "pushx, "
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
            saveButton.setText("Create");
            nameField.putClientProperty("JTextField.placeholderText", "New Playlist");

            if(directoryLabel.getText().isBlank()) {

                directoryLabel.setText(System.getProperty("user.dir"));

            }

            resultingActionLabel.setText(
                createResultingAddPlaylistActionString(directoryLabel.getText(), true, null, resultingActionLabel)
            );

        });

        importExistingButton.addActionListener((unused) -> {

            spotifyURLLabel.setEnabled(false);
            spotifyURLField.setEnabled(false);            
            saveButton.setText("Import");
            if(directoryLabel.getText().equals(System.getProperty("user.dir"))) {

                directoryLabel.setText("");
                resultingActionLabel.setText(
                    "<html><p align=\"right\"><i><br></br>Please select a directory</html>"
                );
                nameField.putClientProperty("JTextField.placeholderText", "---");

            } else {

                resultingActionLabel.setText(
                    createResultingAddPlaylistActionString(directoryLabel.getText(), false, null, resultingActionLabel)
                );
                nameField.putClientProperty("JTextField.placeholderText", new File(directoryLabel.getText()).getName());

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

                        error.printStackTrace();;

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
                directoryLabel.setText(selectedDirectory.getAbsolutePath());
                if(buttonGroup.getSelection().equals(createNewButton.getModel())) {

                    resultingActionLabel.setText(
                        createResultingAddPlaylistActionString(directoryLabel.getText(), true, null, resultingActionLabel)
                    );

                } else {

                    resultingActionLabel.setText(
                        createResultingAddPlaylistActionString(directoryLabel.getText(), false, null, resultingActionLabel)
                    );
                    nameField.putClientProperty("JTextField.placeholderText", new File(directoryLabel.getText()).getName());

                }

            }

        });

        cancelButton.addActionListener((unused) -> {

            dialog.dispose();

        });

        saveButton.addActionListener((unused) -> {

            Playlist playlist;
            if(buttonGroup.getSelection().equals(createNewButton.getModel())) {

                return;

            } else {

                System.out.println(nameField.getText());

                if(directoryLabel.getText().isBlank()) {

                    JOptionPane.showMessageDialog(
                        dialog, 
                        "Please select a directory", 
                        "", 
                        JOptionPane.ERROR_MESSAGE
                    );

                    return;

                }

                playlist = new Playlist(directoryLabel.getText());
                playlist.setCoverArtByteArray(dialog.getByteArray());
                playlist.setName(nameField.getText().isBlank() ? new File(directoryLabel.getText()).getName() : nameField.getText());

            }

            try {

                FileManager.addPlaylist(playlist);
                SwingUtilities.invokeLater(() -> {

                    MainPanel.mainPanel.initTracklistPage(playlist);

                });

            } catch (FileNotFoundException e) {

                JOptionPane.showMessageDialog(
                    dialog, 
                    "<html>Could not create playlists data file. Please ensure that the location " + new File(FileManager.PLAYLIST_DATA_LOCATION).getAbsolutePath() + " is clear of extra files.</html>", 
                    "", 
                    JOptionPane.ERROR_MESSAGE
                );

                e.printStackTrace();

            } catch (IOException e) {

                JOptionPane.showMessageDialog(
                    dialog, 
                    "<html>An unknown error occurred.<br></br>" + e.getMessage() + "</html>", 
                    "", 
                    JOptionPane.ERROR_MESSAGE
                );

                e.printStackTrace();

            } finally {

                dialog.dispose();

            }

        });

        //---------------------------END ADDING ACTION LISTENERS---------------------------

        dialog.setLocationRelativeTo(StaccatoWindow.staccatoWindow);
        dialog.setVisible(true);

        return dialog;

    }

    private static String createResultingAddPlaylistActionString(String dirStr, boolean isCreatingNew, String outsideSource, JComponent component) {

        if(isCreatingNew) {

            return "<html><p align=\"right\"><i>staccato will create a new folder in<br></br>" + truncateWithEllipsis(dirStr, component, PLAYLIST_ADDER_DIALOG_WINDOW_SIZE.width - 20) + "</html>";

        } else {

            return "<html><p align=\"right\"><i>staccato will import mp3 files from<br></br>" + truncateWithEllipsis(dirStr, component, PLAYLIST_ADDER_DIALOG_WINDOW_SIZE.width - 20) + "</html>";

        }

    }

    public static JDialog createPlaylistEditorPopup(Playlist playlist, int playlistIndex) {

        InternalDataDialog dialog = new InternalDataDialog(StaccatoWindow.staccatoWindow, true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setLayout(new MigLayout("gap 0 " + MIN_VERTICAL_GAP_PX));
        dialog.setResizable(false);
        dialog.setSize(PLAYLIST_EDITOR_DIALOG_WINDOW_SIZE);
        dialog.setByteArray(playlist.getCoverArtByteArray());

        JLabel titleLabel = new JLabel();
        ImageIcon playlistCoverIcon = playlist.getCoverArtByteArray() != null ? new ImageIcon(playlist.getCoverArtByteArray()) : PLACEHOLDER_ART_ICON;
        JLabel playlistCoverLabel = new JLabel();
        JButton removeCoverButton = new HoverableButton("Remove Cover Photo");
        JLabel nameLabel = new JLabel("Name: ");
        JTextField nameField = new JTextField(12);
        JButton chooseDirectoryButton = new HoverableButton(UIManager.getIcon("Tree.closedIcon"));
        JLabel currentDirectoryLabel = new JLabel();
        JScrollPane currentDirectoryScrollPane = new InvisibleScrollPane(currentDirectoryLabel);
        JButton saveButton = new HoverableButton("Save");
        JButton cancelButton = new HoverableButton("Cancel");

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
        chooseDirectoryPanel.add(chooseDirectoryButton);
        chooseDirectoryPanel.add(Box.createHorizontalStrut(5));
        chooseDirectoryPanel.add(currentDirectoryScrollPane);
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

                    if(MainPanel.mainPanel.isOnTracklistView()) {

                        MainPanel.mainPanel.initTracklistPage(newPlaylist);

                    } else {

                        MainPanel.mainPanel.updatePlaylistButton(newPlaylist, playlistIndex);

                    }

                } else {

                    editWasSuccessful = FileManager.replacePlaylist(playlist, playlist);
                    
                    if(MainPanel.mainPanel.isOnTracklistView()) {

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
                        "", 
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
                    "", 
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

    public static JDialog createAddTrackDialog(Playlist playlist) {

        InternalDataDialog dialog = new InternalDataDialog(StaccatoWindow.staccatoWindow, true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setLayout(new MigLayout("gap 0 " + MIN_VERTICAL_GAP_PX));
        dialog.setResizable(false);
        dialog.setSize(TRACK_ADDER_DIALOG_WINDOW_SIZE);

        JLabel titleLabel = new JLabel("Add Track");
        JRadioButton downloadNewButton = new JRadioButton("Download new track");
        JRadioButton importExistingButton = new JRadioButton("Import mp3 files");
        ButtonGroup buttonGroup = new ButtonGroup();
        JPanel contentPanel = new JPanel();

        titleLabel.setFont(POPUP_TITLE_LABEL_FONT);
        buttonGroup.add(downloadNewButton);
        buttonGroup.add(importExistingButton);
        buttonGroup.setSelected(downloadNewButton.getModel(), true);
        initDownloadNewTrackDialog(contentPanel);

        dialog.add(
            titleLabel, ""
            + "cell 0 0, "
            + "span 1 1, "
            + "gapbottom " + SECTION_VERTICAL_GAP_PX + ", "
        );

        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.Y_AXIS));
        radioButtonPanel.add(downloadNewButton);
        radioButtonPanel.add(importExistingButton);
        dialog.add(
            radioButtonPanel, ""
            + "cell 0 1, "
            + "span 1 1, "
            + "gapbottom " + SECTION_VERTICAL_GAP_PX + ", "
        );

        dialog.add(
            contentPanel, ""
            + "cell 0 2, "
            + "span 1 1, "
            + "grow, push, "
        );

        //--------------------------START ADDING ACTION LISTENERS--------------------------

        dialog.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeDialog");
        dialog.getRootPane().getActionMap().put("closeDialog", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {

                dialog.dispose();

            }

        });

        dialog.getRootPane().addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {

                titleLabel.requestFocusInWindow();

            }

        });

        downloadNewButton.addActionListener((unused) -> {

            SwingUtilities.invokeLater(() -> {

                initDownloadNewTrackDialog(contentPanel);

            });

        });

        importExistingButton.addActionListener((unused) -> {

            SwingUtilities.invokeLater(() -> {

                initImportExistingTracksDialog(contentPanel, dialog, playlist);

            });

        });

        //---------------------------END ADDING ACTION LISTENERS---------------------------

        dialog.setLocationRelativeTo(StaccatoWindow.staccatoWindow);
        dialog.setVisible(true);

        return dialog;

    }

    private static void initImportExistingTracksDialog(JPanel panel, InternalDataDialog dialog, Playlist playlist) {

        panel.removeAll();
        panel.setLayout(new MigLayout("gap 0 " + MIN_VERTICAL_GAP_PX + ", insets 0"));

        JPanel chooseFilesPanel = new JPanel();
        JButton chooseFilesButton = new HoverableButton(UIManager.getIcon("Tree.closedIcon"));
        JLabel directoryLabel = new JLabel("<html><i>Select one or more mp3 files to import.</html>");
        JScrollPane directoryScrollPane = new InvisibleScrollPane(directoryLabel);
        JPanel importedTracksPanel = new JPanel();
        JPanel tracklistWrapperPanel = new JPanel(new BorderLayout());
        JScrollPane importedTracksScrollPane = new JScrollPane(tracklistWrapperPanel);
        JPanel trackPreviewPanel = new JPanel(new MigLayout("gap 0 " + MIN_VERTICAL_GAP_PX + ", insets 0"));
        JLabel resultingActionLabel = new JLabel();
        JPanel bottomButtonPanel = new JPanel();
        JButton importButton = new HoverableButton("Import");
        JButton cancelButton = new HoverableButton("Cancel");
        
        chooseFilesPanel.setLayout(new BoxLayout(chooseFilesPanel, BoxLayout.X_AXIS));
        bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.X_AXIS));
        importedTracksPanel.setLayout(new BoxLayout(importedTracksPanel, BoxLayout.Y_AXIS));
        importedTracksScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        importedTracksScrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);
        resultingActionLabel.setText(
            createResultingAddTrackActionString(false, dialog.getNumFiles(), null, playlist.getDirectory(), resultingActionLabel)
        );
        Iterator<File> previouslySelectedFiles = dialog.getFilesIterator();
        while(previouslySelectedFiles.hasNext()) {

            importedTracksPanel.add(createImportedTrackEntry(previouslySelectedFiles.next(), trackPreviewPanel, dialog, resultingActionLabel, playlist));

        }

        chooseFilesPanel.add(chooseFilesButton);
        chooseFilesPanel.add(Box.createHorizontalStrut(MIN_VERTICAL_GAP_PX));
        chooseFilesPanel.add(directoryScrollPane);
        panel.add(
            chooseFilesPanel, ""
            + "cell 0 0, "
            + "span 1 1, "
            + "gapbottom " + MIN_VERTICAL_GAP_PX + ", "
        );

        tracklistWrapperPanel.add(importedTracksPanel, BorderLayout.NORTH);
        panel.add(
            importedTracksScrollPane, ""
            + "cell 0 1, "
            + "span 1 1, "
            + "grow, pushx, "
            + "hmin " + (int) (IMPORTED_TRACKS_PANEL_HEIGHT_PROPORTION * 100) + "%, "
        );

        panel.add(
            trackPreviewPanel, ""
            + "cell 0 2, "
            + "span 1 1, "
            + "growx, pushx, "
        );

        panel.add(
            resultingActionLabel, ""
            + "cell 0 3, "
            + "span 1 1, "
            + "pushy, "
            + "align right bottom, "
            + "gapbottom " + MIN_VERTICAL_GAP_PX + ", "
        );

        bottomButtonPanel.add(Box.createHorizontalGlue());
        bottomButtonPanel.add(importButton);
        bottomButtonPanel.add(Box.createHorizontalStrut(BOTTOM_BUTTONS_GAP_PX));
        bottomButtonPanel.add(cancelButton);
        panel.add(
            bottomButtonPanel, ""
            + "cell 0 4, "
            + "span 1 1, "
            + "pushx, "
            + "align right bottom, "
        );

        //--------------------------START ADDING ACTION LISTENERS--------------------------

        dialog.getRootPane().addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {

                trackPreviewPanel.removeAll();
                trackPreviewPanel.revalidate();
                trackPreviewPanel.repaint();

            }

        });

        trackPreviewPanel.addMouseListener(new MouseAdapter() {
            //This empty mouse listener prevents clicks on the panel from being sent to the root pane (thereby destroying the preview pane)
        });

        chooseFilesButton.addActionListener((unused) -> {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("*.mp3", "mp3"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(true);
            int result = fileChooser.showOpenDialog(dialog);

            if(result == JFileChooser.APPROVE_OPTION) {

                File[] newlySelectedFiles = fileChooser.getSelectedFiles();
                dialog.addFiles(newlySelectedFiles);
                for(int i = 0; i < newlySelectedFiles.length; i++) {

                    importedTracksPanel.add(createImportedTrackEntry(newlySelectedFiles[i], trackPreviewPanel, dialog, resultingActionLabel, playlist));

                }

                resultingActionLabel.setText(createResultingAddTrackActionString(false, dialog.getNumFiles(), null, playlist.getDirectory(), resultingActionLabel));

                importedTracksPanel.revalidate();
                importedTracksPanel.repaint();

            }

        });

        chooseFilesButton.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {

                trackPreviewPanel.removeAll();
                trackPreviewPanel.revalidate();
                trackPreviewPanel.repaint();

            }

        });

        importButton.addActionListener((unused) -> {

            //TODO: Copy the selected files into the playlist's directory
            //Include a progress bar
            
            Iterator<File> selectedFiles = dialog.getFilesIterator();
            while(selectedFiles.hasNext()) {

                System.out.println(selectedFiles.next().getAbsolutePath());

            }

        });

        importButton.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {

                trackPreviewPanel.removeAll();
                trackPreviewPanel.revalidate();
                trackPreviewPanel.repaint();

            }

        });

        cancelButton.addActionListener((unused) -> {

            dialog.dispose();

        });

        cancelButton.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {

                trackPreviewPanel.removeAll();
                trackPreviewPanel.revalidate();
                trackPreviewPanel.repaint();

            }

        });

        //---------------------------END ADDING ACTION LISTENERS---------------------------

        panel.revalidate();
        panel.repaint();

    }

    private static void initDownloadNewTrackDialog(JPanel panel) {

        panel.removeAll();
        panel.setLayout(new MigLayout("gap 0 " + MIN_VERTICAL_GAP_PX + ", insets 0"));

        panel.revalidate();
        panel.repaint();

    }

    private static JPanel createImportedTrackEntry(File trackFile, JPanel trackPreviewPanel, InternalDataDialog dialog, JLabel resultingActionLabel, Playlist playlist) {

        JPanel trackPanel = new JPanel(new MigLayout("insets 2 4 2 4"));
        JLabel fileLocationLabel = new JLabel(trackFile.getAbsolutePath());
        JScrollPane fileLocationScrollPane = new InvisibleScrollPane(fileLocationLabel);
        JButton removeButton = new JButton("-");
        
        fileLocationScrollPane.setFocusable(false);

        trackPanel.add(
            fileLocationScrollPane, ""
            + "cell 0 0, "
            + "span 1 1, "
            + "growx, "
            + "wmax " + (int) (FILE_LOCATION_LABEL_SIZE_PROPORTION * 100) + "%, "
        );

        trackPanel.add(
            removeButton, ""
            + "cell 1 0, "
            + "span 1 1, "
            + "pushx, "
            + "align right center, "
            + "wmax " + REMOVE_BUTTON_SIZE_PX + ", hmax " + REMOVE_BUTTON_SIZE_PX + ", "
        );

        //--------------------------START ADDING ACTION LISTENERS--------------------------

        trackPanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                if(!trackPanel.isFocusOwner()) {

                    trackPanel.setOpaque(true);
                    trackPanel.setBackground(HIGHLIGHTED_TRACKLIST_ROW_COLOR);
                    trackPanel.revalidate();
                    trackPanel.repaint();

                }

                if(trackPanel.isEnabled()) {

                    trackPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                }

            }

            @Override
            public void mouseExited(MouseEvent e) {

                if(!trackPanel.isFocusOwner()) {

                    trackPanel.setOpaque(false);
                    trackPanel.revalidate();
                    trackPanel.repaint();

                }

                trackPanel.setCursor(Cursor.getDefaultCursor());

            }

            @Override
            public void mouseClicked(MouseEvent e) {

                trackPanel.requestFocusInWindow();
                trackPanel.setOpaque(true);
                trackPanel.setBackground(SELECTED_TRACKLIST_ROW_COLOR);
                trackPanel.revalidate();
                trackPanel.repaint();

                trackPreviewPanel.removeAll();
                trackPreviewPanel.setLayout(new MigLayout("insets 0, gap 3 0"));
                Track track;
                try {

                    track = new Track(trackFile.getAbsolutePath());
                    
                    JLabel artworkLabel = new JLabel(createResizedIcon(
                        track.getArtworkByteArray() == null ? PLACEHOLDER_ART_ICON : new ImageIcon(track.getArtworkByteArray()), 
                        IMPORT_TRACK_PREVIEW_ICON_SIZE_PX, 
                        IMPORT_TRACK_PREVIEW_ICON_SIZE_PX, 
                        Image.SCALE_SMOOTH
                    ));
                    JLabel trackTitleLabel = new JLabel(track.getTitle());
                    JLabel trackArtistsLabel = new JLabel(track.getArtists());
                    JLabel trackAlbumLabel = new JLabel(track.getAlbum());
                    JScrollPane trackTitleScrollPane = new InvisibleScrollPane(trackTitleLabel);
                    JScrollPane trackArtistsScrollPane = new InvisibleScrollPane(trackArtistsLabel);
                    JScrollPane trackAlbumScrollPane = new InvisibleScrollPane(trackAlbumLabel);

                    trackTitleLabel.setFont(TRACK_TITLE_FONT);
                    trackArtistsLabel.setFont(TRACK_ARTISTS_FONT);
                    trackAlbumLabel.setFont(TRACK_ALBUM_FONT);

                    trackPreviewPanel.add(
                        artworkLabel, ""
                        + "cell 0 0, "
                        + "span 1 3, "
                    );
                    
                    trackPreviewPanel.add(
                        trackTitleScrollPane, ""
                        + "cell 1 0, "
                        + "span 1 1, "
                        + "pushx, pushy 0.5, "
                        + "aligny bottom, "
                    );
                    
                    trackPreviewPanel.add(
                        trackArtistsScrollPane, ""
                        + "cell 1 1, "
                        + "span 1 1, "
                        + "pushx, "
                        + "aligny top, "
                    );

                    trackPreviewPanel.add(
                        trackAlbumScrollPane, ""
                        + "cell 1 2, "
                        + "span 1 1, "
                        + "pushx, pushy 0.5, "
                        + "aligny top, "
                    );

                    trackPreviewPanel.revalidate();
                    trackPreviewPanel.repaint();

                } catch (FileNotFoundException error) {

                    error.printStackTrace();

                }

            }

        });

        trackPanel.addFocusListener(new FocusAdapter() {
            
            @Override
            public void focusLost(FocusEvent e) {

                trackPanel.setOpaque(false);
                trackPanel.revalidate();
                trackPanel.repaint();

            }

        });

        fileLocationScrollPane.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseEntered(MouseEvent e) {

                // Convert coordinates to panel
                MouseEvent converted = SwingUtilities.convertMouseEvent(fileLocationScrollPane, e, trackPanel);
                for(MouseListener mouseListener: trackPanel.getMouseListeners()) {

                    mouseListener.mouseEntered(converted);

                }

            }

            @Override
            public void mouseExited(MouseEvent e) {

                MouseEvent converted = SwingUtilities.convertMouseEvent(fileLocationScrollPane, e, trackPanel);
                for(MouseListener mouseListener: trackPanel.getMouseListeners()) {
                    
                    mouseListener.mouseExited(converted);

                }
            
            }

            @Override
            public void mouseClicked(MouseEvent e) {

                MouseEvent converted = SwingUtilities.convertMouseEvent(fileLocationScrollPane, e, trackPanel);
                for(MouseListener mouseListener: trackPanel.getMouseListeners()) {
                    
                    mouseListener.mouseClicked(converted);

                }
            
            }

        });
        
        removeButton.addActionListener((unused) -> {

            dialog.removeFiles(trackFile);
            Container parent = trackPanel.getParent();
            parent.remove(trackPanel);
            parent.revalidate();
            parent.repaint();
            resultingActionLabel.setText(createResultingAddTrackActionString(false, dialog.getNumFiles(), null, playlist.getDirectory(), resultingActionLabel));

        });

        return trackPanel;

    }

    private static String createResultingAddTrackActionString(boolean isCreatingNew, int numNewTracks, String musicPlatform, String playlistDirStr, JComponent component) {

        if(isCreatingNew) {

            return "<html><p align=\"right\"><i>Downloading tracks from " + musicPlatform + "</html>";

        } else {

            if(numNewTracks == 0) {

                return "<html><p align=\"right\"><i>Please select files to copy into<br></br>" + truncateWithEllipsis(playlistDirStr, component, PLAYLIST_ADDER_DIALOG_WINDOW_SIZE.width - 20) + "</html>";

            } else {

                return "<html><p align=\"right\"><i>Will copy " + numNewTracks + " mp3 files into<br></br>" + truncateWithEllipsis(playlistDirStr, component, PLAYLIST_ADDER_DIALOG_WINDOW_SIZE.width - 20) + "</html>";

            }

        }

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
        JButton downloadButton = new HoverableButton("Download");
        JButton okButton = new HoverableButton("OK");

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

        dialog.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeDialog");
        dialog.getRootPane().getActionMap().put("closeDialog", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {

                dialog.dispose();

            }

        });

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
        JButton artworkButton = new HoverableButton();
        JLabel artworkURLLabel = new JLabel();
        JPanel bottomButtonPanel = new JPanel();
        JButton saveButton = new HoverableButton("Save");
        JButton cancelButton = new HoverableButton("Cancel");

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
            + "span 2 1, "
            + "gapbottom " + SECTION_VERTICAL_GAP_PX + ", "
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
        
        dialog.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeDialog");
        dialog.getRootPane().getActionMap().put("closeDialog", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {

                dialog.dispose();

            }

        });

        cancelButton.addActionListener((unused) -> {

            dialog.dispose();

        });

        saveButton.addActionListener((unused) -> {

            track.setTitle(titleField.getText());
            track.setArtists(artistsField.getText());
            track.setAlbum(albumField.getText());
            track.setArtworkByteArray(dialog.getByteArray());
            track.writeMetadata();

            if(trackPanel != null) {

                MainPanel.updateTrackPanel(trackPanel, track);

            }
            if(CurrentTrackInfoPanel.currentTrackInfoPanel.getTrack().equals(track)) {

                CurrentTrackInfoPanel.currentTrackInfoPanel.setTrack(track);

            }

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
        private HashSet<File> files = new HashSet<File>();

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

        public Iterator<File> getFilesIterator() {

            return files.iterator();

        }

        public int getNumFiles() {

            return files.size();

        }

        public void addFiles(File... files) {

            for(int i = 0; i < files.length; i++) {

                this.files.add(files[i]);

            }

        }

        public void removeFiles(File... files) {

            for(int i = 0; i < files.length; i++) {

                this.files.remove(files[i]);

            }

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
