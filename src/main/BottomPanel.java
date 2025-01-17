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

import com.formdev.flatlaf.icons.FlatOptionPaneWarningIcon;

public class BottomPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7262047713711324913L;

	private JButton downloadButton;
	private JLabel statusLabel;
	private JProgressBar progressBar;
	private JLabel infoLabel;
	private ButtonGroup radioButtons;
	private JRadioButton playlistButton;
	private JRadioButton audioButton;
	private JRadioButton videoButton;
	
	private final StaccatoWindow parentWindow;
	private final InputPanel inputPanel;
	
	public BottomPanel(Font buttonFont, Font statusFont, Font infoFont, InputPanel inputPanel, StaccatoWindow parentWindow) {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		downloadButton = new JButton("Download");
		downloadButton.setFont(buttonFont);
		downloadButton.setAlignmentX(CENTER_ALIGNMENT);
		downloadButton.setBackground(new Color(0x80005d));
		downloadButton.setBorderPainted(false);
		downloadButton.addActionListener((event) -> {
			
			Thread downloadThread = new Thread(() -> {
				
				parentWindow.setIsDownloading(true);
				downloadAction();
				parentWindow.setIsDownloading(false);
				
			});
			
			downloadThread.start();
			
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
		radioButtons = new ButtonGroup();
		radioButtons.add(playlistButton);
		radioButtons.add(audioButton);
		radioButtons.add(videoButton);
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
		add(Box.createVerticalStrut(5));
		add(statusLabel);
		add(Box.createVerticalStrut(5));
		add(radioButtonPanel);
		add(Box.createVerticalStrut(10));
		add(progressBar);
		add(Box.createVerticalStrut(10));
		add(infoLabel);
		
		radioButtons.setSelected(playlistButton.getModel(), true);
		this.parentWindow = parentWindow;
		this.inputPanel = inputPanel;
		
	}
	
	private void downloadAction() {
		
		if(!Downloader.checkSoftwareInstalled("yt-dlp") || !Downloader.checkSoftwareInstalled("ffmpeg")) {
			
			parentWindow.createPopup("Restart staccato", "Please restart staccato to complete the installation.", new FlatOptionPaneWarningIcon());
			return;
			
		}
		
		if(audioButton.isSelected()) {
			
			downloadAudioAction();
			
		}
				
	}
	
	private void downloadAudioAction() {
		
		String url = inputPanel.getURL();
		String dir = inputPanel.getDirectory();
		
		if(url.contains("youtube.com")) {
			
			Downloader.download(url, dir);
			
		}
		
	}
	
	public void setStatusText(String text) {
		
		statusLabel.setText(text);
		statusLabel.setForeground(Color.WHITE);
		
	}
	
	public void setStatusText(String text, boolean isError) {
		
		statusLabel.setText(text);
		if(isError) {
			
			statusLabel.setForeground(StaccatoWindow.ERROR_STATUS_COLOR);
			
		} else {
			
			statusLabel.setForeground(Color.WHITE);
			
		}
		
	}
	
	public void setProgressBar(int percent) {
		
		System.out.println("PROGRESS BAR " + percent);
		progressBar.setValue(percent);
		
	}
	
	public StaccatoWindow getStaccatoWindow() {
		
		return parentWindow;
		
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		
		playlistButton.setEnabled(enabled);
		audioButton.setEnabled(enabled);
		videoButton.setEnabled(enabled);
		downloadButton.setEnabled(enabled);
		
	}
	
	public static void main(String[] args) {
		
		StaccatoWindow.main(args);
		
	}
	
}
