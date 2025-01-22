package main;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.icons.FlatOptionPaneAbstractIcon;
import com.formdev.flatlaf.icons.FlatOptionPaneWarningIcon;

public class BottomPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7262047713711324913L;

	private static BottomPanel mainBottomPanel;
	
	private JButton downloadButton;
	private JLabel statusLabel;
	private JProgressBar progressBar;
	private JLabel infoLabel;
	
	private final StaccatoWindow parentWindow;
	
	public BottomPanel(Font buttonFont, Font statusFont, Font infoFont, StaccatoWindow parentWindow) {
		
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
		
		progressBar = new JProgressBar();
		progressBar.setAlignmentX(CENTER_ALIGNMENT);
		progressBar.setForeground(new Color(0x80005d));
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.putClientProperty("JProgressBar.largeHeight", true);
		
		infoLabel = new JLabel("<html><div style='text-align: center;'>100-song limit for Spotify Playlists<br></br>"
				+ "Title, artist, and album fields are only for single audio downloads</div></html>");
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setFont(infoFont);
		infoLabel.setAlignmentX(CENTER_ALIGNMENT);
		
		add(downloadButton);
		add(Box.createVerticalStrut(10));
		add(statusLabel);
		add(Box.createVerticalStrut(10));
		add(progressBar);
		add(Box.createVerticalStrut(12));
		add(infoLabel);
		
		this.parentWindow = parentWindow;
		
		mainBottomPanel = this;
		
	}
	
	private void downloadAction() {
		
		if(!Downloader.checkSoftwareInstalled("yt-dlp") || !Downloader.checkSoftwareInstalled("ffmpeg")) {
			
			parentWindow.createPopup("Restart staccato", "Please restart staccato to complete the installation.", new FlatOptionPaneWarningIcon());
			return;
			
		}
		
		String url = InputPanel.getInputURL();
		String title = InputPanel.getInputTitle();
		String artist = InputPanel.getInputArtist();
		String album = InputPanel.getInputAlbum();
		String dir = InputPanel.getInputAlbum();
		
		if(url.isBlank()) {
			
			setStatusText("Please input a URL", true);
			return;
			
		}
		
		if(url.contains("youtube.com")) {
			
			downloadYouTubeAction(url, dir, title + " " + artist);
			//Now we need to find the file in the directory. Note that the file will be in the format of "[title] [artist] [video id].mp3"
			
		} else if(url.contains("spotify.com")) {
			
			downloadSpotifyAction(url, dir);
			
		}
		
	}
	
	private void downloadYouTubeAction(String url, String dir, String fileName) {
		
		boolean isPlaylist = false;
		
		if(isPlaylist) {
			
			//Do something else if it's a playlist
			
		} else {
			
			Downloader.download(url, dir, fileName);
			
		}
		
	}
	
	private void downloadSpotifyAction(String url, String dir) {
		
		StaccatoTrack[] data = MusicFetcher.convertSpotifyData(url);
		
		if(data.length > 1) {
			
			dir = "make this a playlist dir";
			
		}
		
		for(int i = 0; i < data.length; i++) {
			
			Downloader.download(data[i].getYouTubeURL(), dir, data[i].getTitle() + " " + data[i].getArtist());
			
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
		
		downloadButton.setEnabled(enabled);
		
	}
	
	public static void setGUIStatus(int progressBar, String status) {
		
		if(mainBottomPanel == null) {
			
			System.out.println("==================================================================");
			System.out.println("Progress: " + progressBar + "%");
			System.out.println(status);
			System.out.println("==================================================================");
			return;
			
		}
		
		mainBottomPanel.setProgressBar(progressBar);
		mainBottomPanel.setStatusText(status, false);
		
	}
	
	public static void setGUIErrorStatus(String status) {
		
		if(mainBottomPanel == null) {
			
			System.err.println("==================================================================");
			System.err.println(status);
			System.err.println("==================================================================");
			return;
			
		}
		
		mainBottomPanel.setProgressBar(0);
		mainBottomPanel.setStatusText(status, true);
		
	}
	
	public static void showGUIPopup(String title, String message, FlatOptionPaneAbstractIcon icon) {
		
		if(mainBottomPanel == null) {
			
			System.out.println("==================================================================");
			System.out.println(title);
			System.out.println(message);
			System.out.println("[icon " + icon.getClass().getName() + "]");
			System.out.println("==================================================================");
			return;
			
		}
		
		mainBottomPanel.getStaccatoWindow().createPopup(title, message, icon);
		
	}
	
	public static void main(String[] args) {
		
		StaccatoWindow.main(args);
		
	}
	
}
