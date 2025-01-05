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
	
	public BottomPanel(Font buttonFont, Font statusFont, Font infoFont, InputPanel inputPanel) {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JButton downloadButton = new JButton("Download");
		downloadButton.setFont(buttonFont);
		downloadButton.setAlignmentX(CENTER_ALIGNMENT);
		downloadButton.setBackground(new Color(0x80005d));
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
			
			inputPanel.setSongTextFieldsEnabled(false);
			
		});
		audioButton = new JRadioButton("Individual Audio");
		audioButton.addActionListener((event) -> {
			
			inputPanel.setSongTextFieldsEnabled(true);
			
		});
		videoButton = new JRadioButton("Video");
		videoButton.addActionListener((event) -> {
			
			inputPanel.setSongTextFieldsEnabled(false);
			
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
		progressBar.setForeground(new Color(0x80005d));
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.putClientProperty("JProgressBar.largeHeight", true);
		
		infoLabel = new JLabel("100-song limit for Spotify Playlists");
		infoLabel.setFont(infoFont);
		infoLabel.setAlignmentX(CENTER_ALIGNMENT);
		
		add(downloadButton);
		add(Box.createVerticalStrut(4));
		add(statusLabel);
		add(Box.createVerticalStrut(5));
		add(radioButtonPanel);
		add(Box.createVerticalStrut(10));
		add(progressBar);
		add(Box.createVerticalStrut(8));
		add(infoLabel);
		
		radioButtonGroup.setSelected(playlistButton.getModel(), true);
		
	}
	
	private void downloadAction() {
		
		
		
	}
	
	private void setStatusText(String text) {
		
		statusLabel.setText(text);
		
	}
	
	public static void main(String[] args) {
		
		StaccatoWindow.main(args);
		
	}
	
}
