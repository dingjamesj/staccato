package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.formdev.flatlaf.icons.FlatOptionPaneAbstractIcon;

import main.FileManager;
import main.Playlist;
import main.Track;
import net.miginfocom.swing.MigLayout;

public abstract class GUIUtil {
    
    private static final Dimension REDOWNLOAD_DIALOG_WINDOW_SIZE = new Dimension(300, 175);
    private static final Dimension EDIT_METADATA_WINDOW_SIZE = new Dimension(400, 275);
    private static final Dimension PLAYLIST_EDITOR_DIALOG_WINDOW_SIZE = new Dimension(232, 380);
    private static final int REDOWNLOAD_FIELD_GAPRIGHT_PX = 55;
    private static final int REDOWNLOAD_PROGRESSBAR_GAPTOP_PX = 10;
    private static final int BOTTOM_BUTTONS_GAP_PX = 5;
    private static final int EDIT_METADATA_ARTWORK_BUTTON_SIZE_PX = 64;
    private static final int EDIT_METADATA_ARTWORK_BUTTON_TO_LABEL_GAP_PX = 3;
    private static final int EDIT_METADATA_ARTWORK_URL_LABEL_MAX_WIDTH_PX = 240;
    private static final int MIN_VERTICAL_GAP_PX = 5;
    private static final int PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX = 200;

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

    public static String truncateWithEllipsis(String str, FontMetrics fontMetrics, int width) {

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
    
    public static JDialog createPopup(String title, String message, FlatOptionPaneAbstractIcon icon) {
		
		JDialog dialog = new JDialog(StaccatoWindow.staccatoWindow, true);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setTitle(title);
		dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
		dialog.setResizable(false);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		topPanel.add(Box.createHorizontalStrut(15));
		topPanel.add(new JLabel(icon));
		topPanel.add(Box.createHorizontalStrut(12));
		topPanel.add(new JLabel(message));
		topPanel.add(Box.createHorizontalStrut(15));
		
		dialog.add(Box.createVerticalStrut(10));
		dialog.add(topPanel);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		JButton okayButton = new JButton("OK");
		okayButton.setBackground(new Color(0x80005d));
		okayButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
		okayButton.addActionListener((e) -> {
			
			dialog.dispose();
			
		});
		bottomPanel.add(okayButton);
		
		dialog.add(Box.createVerticalStrut(15));
		dialog.add(bottomPanel);
		dialog.add(Box.createVerticalStrut(15));
		dialog.pack();
		dialog.setLocationRelativeTo(StaccatoWindow.staccatoWindow);
		dialog.setVisible(true);

		return dialog;
		
	}

    public static JDialog createPlaylistEditorPopup(Playlist playlist) {

        BinaryDataDialog dialog = new BinaryDataDialog(StaccatoWindow.staccatoWindow, true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Edit Playlist");
        dialog.setLayout(new MigLayout("gap 0 " + MIN_VERTICAL_GAP_PX));
        dialog.setResizable(false);
        dialog.setSize(PLAYLIST_EDITOR_DIALOG_WINDOW_SIZE);

        JLabel titleLabel = new JLabel("<html>Edit " + playlist.getName() + "</html>");
        ImageIcon playlistCoverIcon = playlist.getCoverArtByteArray() != null ? new ImageIcon(playlist.getCoverArtByteArray()) : PLACEHOLDER_ART_ICON;
        JButton playlistCoverButton = new JButton();
        JLabel nameLabel = new JLabel("Name: ");
        JTextField nameField = new JTextField();
        JLabel directoryLabel = new JLabel("Directory: ");
        JTextField directoryField = new JTextField();
        JPanel bottomButtonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        JScrollPane titleLabelScrollPane = new JInvisibleScrollPane(titleLabel);

        playlistCoverButton.setIcon(createResizedIcon(playlistCoverIcon, PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX, PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX, Image.SCALE_SMOOTH));
        nameField.setText(playlist.getName());
        directoryField.setText(playlist.getDirectory());
        bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.X_AXIS));
        titleLabel.setFont(POPUP_TITLE_LABEL_FONT);

        dialog.add(
            titleLabelScrollPane, ""
            + "cell 0 0, "
            + "span 2 1, "
        );

        dialog.add(
            playlistCoverButton, ""
            + "cell 0 1, "
            + "span 2 1, "
        );

        dialog.add(
            nameLabel, ""
            + "cell 0 2, "
            + "span 1 1, "
        );

        dialog.add(
            nameField, ""
            + "cell 1 2, "
            + "span 1 1, "
            + "growx, "
        );

        dialog.add(
            directoryLabel, ""
            + "cell 0 3, "
            + "span 1 1, "
        );

        dialog.add(
            directoryField, ""
            + "cell 1 3, "
            + "span 1 1, "
            + "growx, "
        );

        bottomButtonPanel.add(Box.createHorizontalGlue());
        bottomButtonPanel.add(saveButton);
        bottomButtonPanel.add(Box.createHorizontalStrut(BOTTOM_BUTTONS_GAP_PX));
        bottomButtonPanel.add(cancelButton);
        dialog.add(
            bottomButtonPanel, ""
            + "cell 1 4, "
            + "span 1 1, "
            + "pushx, pushy, "
            + "align right bottom, "
        );

        //--------------------------START ADDING ACTION LISTENERS--------------------------
        
        playlistCoverButton.addActionListener((unused) -> {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images (*.jpg, *.png, *.jpeg)", "jpg", "png", "jpeg"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            int result = fileChooser.showOpenDialog(dialog);

            if(result == JFileChooser.APPROVE_OPTION) {

                File selectedFile = fileChooser.getSelectedFile();

                try {

                    byte[] coverArtByteArray = FileManager.readByteArray(selectedFile);
                    playlistCoverButton.setIcon(createResizedIcon(new ImageIcon(coverArtByteArray), PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX, PLAYLIST_EDITOR_COVER_BUTTON_SIZE_PX, Image.SCALE_SMOOTH));
                    dialog.setByteArray(coverArtByteArray);

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

        });
        
        cancelButton.addActionListener((unused) -> {

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

        BinaryDataDialog dialog = new BinaryDataDialog(StaccatoWindow.staccatoWindow, true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Edit Metadata");
        dialog.setLayout(new MigLayout("gap 0 " + MIN_VERTICAL_GAP_PX));
        dialog.setResizable(false);
        dialog.setSize(EDIT_METADATA_WINDOW_SIZE);

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
            if(dialog.getArtworkByteArray() != null) {

                track.setArtworkByteArray(dialog.getArtworkByteArray());

            }
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
     * Just JDialog with a field to store a byte array
     */
    private static class BinaryDataDialog extends JDialog {

        private byte[] artworkByteArray = null;

        public BinaryDataDialog(Frame owner, boolean modal) {

            super(owner, modal);

        }

        public byte[] getArtworkByteArray() {

            return artworkByteArray;

        }

        public void setByteArray(byte[] artworkByteArray) {

            this.artworkByteArray = artworkByteArray;

        }

    }

    public static class JInvisibleScrollPane extends JScrollPane {

        public JInvisibleScrollPane(Component view) {

            super(view);
            setVerticalScrollBar(new JInvisibleScrollBar(JScrollBar.VERTICAL));
            setHorizontalScrollBar(new JInvisibleScrollBar(JScrollBar.HORIZONTAL));
            setBorder(null);
            setOpaque(false);
            getViewport().setOpaque(false);

        }

    }

    private static class JInvisibleScrollBar extends JScrollBar {

        public JInvisibleScrollBar(int orientation) {

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
