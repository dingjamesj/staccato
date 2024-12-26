package main;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;

public class BottomPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7262047713711324913L;

	private JLabel statusLabel;
	private JProgressBar progressBar;
	private JLabel infoLabel;
	private JRadioButton playlistButton;
	private JRadioButton audioButton;
	private JRadioButton videoButton;
	
	private enum Download {
		
		PLAYLIST, AUDIO, VIDEO
		
	}
	
	public BottomPanel(Font buttonFont, Font statusFont, Font infoFont) {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JButton downloadButton = new JButton("Download");
		downloadButton.setFont(buttonFont);
		downloadButton.setAlignmentX(CENTER_ALIGNMENT);
		downloadButton.setBackground(new Color(128, 0, 93));
		downloadButton.setBorderPainted(false);
		downloadButton.addActionListener((event) -> {
			
			downloadAction();
			
		});
		
		statusLabel = new JLabel("Status: Idle");
		statusLabel.setFont(statusFont);
		statusLabel.setAlignmentX(CENTER_ALIGNMENT);
		
		JPanel radioButtonPanel = new JPanel();
		radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.X_AXIS));
		
		playlistButton = new JRadioButton("Playlist");
		playlistButton.addActionListener((event) -> {
			
			switchDownloadMode(Download.PLAYLIST);
			
		});
		audioButton = new JRadioButton("Individual Audio");
		audioButton.addActionListener((event) -> {
			
			switchDownloadMode(Download.AUDIO);
			
		});
		videoButton = new JRadioButton("Video");
		videoButton.addActionListener((event) -> {
			
			switchDownloadMode(Download.VIDEO);
			
		});
		ButtonGroup radioButtonGroup = new ButtonGroup();
		radioButtonGroup.add(playlistButton);
		radioButtonGroup.add(audioButton);
		radioButtonGroup.add(videoButton);
		radioButtonPanel.add(playlistButton);
		radioButtonPanel.add(Box.createHorizontalStrut(20));
		radioButtonPanel.add(audioButton);
		radioButtonPanel.add(Box.createHorizontalStrut(20));
		radioButtonPanel.add(videoButton);
		
		progressBar = new JProgressBar();
		progressBar.setAlignmentX(CENTER_ALIGNMENT);
		
		infoLabel = new JLabel("100-song limit for Spotify Playlists");
		infoLabel.setFont(infoFont);
		infoLabel.setAlignmentX(CENTER_ALIGNMENT);
		
		add(downloadButton);
		add(Box.createVerticalStrut(7));
		add(statusLabel);
		add(Box.createVerticalStrut(10));
		add(radioButtonPanel);
		add(Box.createVerticalStrut(12));
		add(progressBar);
		add(Box.createVerticalStrut(10));
		add(infoLabel);
		
	}
	
	private void downloadAction() {
		
		
		
	}
	
	private void switchDownloadMode(Download mode) {
		
		
		
	}
	
	private void setStatusText(String text) {
		
		statusLabel.setText(text);
		
	}
	
	public static void main(String[] args) {
		
		StaccatoWindow.main(args);
		
	}
	
}
