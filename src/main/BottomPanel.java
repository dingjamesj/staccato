package main;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

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
	
	private static final long serialVersionUID = -7262047713711324913L;

	public static final int YOUTUBE_PROGRESS_MIDWAY_POINT = 50;
	public static final int SPOTIFY_PROGRESS_MIDWAY_POINT = 35;
	
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
				
				try {
					
					downloadAction();
					statusLabel.setText("Idle");
					progressBar.setValue(0);
					
				} catch (Exception e) {
					
					e.printStackTrace();
					setGUIErrorStatus(e.getClass().getSimpleName() + ": " + e.getMessage());
					
				}
				
				parentWindow.setIsDownloading(false);
				
			});
			
			downloadThread.start();
			
		});
		
		statusLabel = new JLabel("Idle");
		statusLabel.setFont(statusFont);
		statusLabel.setAlignmentX(CENTER_ALIGNMENT);
		
		progressBar = new JProgressBar();
		progressBar.setForeground(new Color(0x80005d));
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.putClientProperty("JProgressBar.largeHeight", true);
		progressBar.setAlignmentX(CENTER_ALIGNMENT);
		
		infoLabel = new JLabel("<html><div style='text-align: center;'>100-song limit for Spotify Playlists<br></br>"
				+ "Title, artist, and album fields are only for single audio downloads</div></html>");
		infoLabel.setFont(infoFont);
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
		
		if(!Installer.checkSoftwareInstalled("yt-dlp") || !Installer.checkSoftwareInstalled("ffmpeg")) {
			
			parentWindow.createPopup("Restart staccato", "Please restart staccato to complete the installation.", new FlatOptionPaneWarningIcon());
			return;
			
		}
		
		String url = InputPanel.getInputURL();
		String dir = InputPanel.getInputDirectory();
		
		if(url.isBlank()) {
			
			setStatusText("Please input a URL", true);
			return;
			
		}
		
		if(url.contains("youtube.com") || url.contains("youtu.be")) {
			
			downloadYouTubeAction(url, dir);
			
		} else if(url.contains("spotify.com")) {
			
			downloadSpotifyAction(url, dir);
			
		} else {
			
			BottomPanel.setGUIErrorStatus("Only YouTube and Spotify links are supported");
			
		}
		
	}
	
	private static void downloadYouTubeAction(String url, String dir) {
		
		String title = InputPanel.getInputTitle();
		String artist = InputPanel.getInputArtist();
		String album = InputPanel.getInputAlbum();
		
		if(url.contains("/playlist")) {
			
			setGUIStatus(5, "Getting YouTube playlist name");
			
			String playlistName = MusicFetcher.getYouTubePlaylistName(url);
			System.out.println("--------------PLAYLISTNAME---------------");
			System.out.println(playlistName);
			System.out.println("-----------------------------------------");
			File playlistFolder;
			
			int uniqueNumber = StaccatoTrack.countRepeatedFileNames(dir, playlistName);
			if(uniqueNumber > 0) {
				
				playlistFolder = new File(dir + "\\" + playlistName + " (" + uniqueNumber + ")");
				dir += "\\" + playlistName + " (" + uniqueNumber + ")";
				
			} else {
				
				playlistFolder = new File(dir + "\\" + playlistName);
				dir += "\\" + playlistName;
				
			}
			
			playlistFolder.mkdir();
			
		}
		
		setGUIStatusText("Getting YouTube data");
		StaccatoTrack[] data = MusicFetcher.convertYouTubeData(url, title, artist, album);
		setGUIProgressBar(YOUTUBE_PROGRESS_MIDWAY_POINT);
		for(int i = 0; i < data.length; i++) {
			
			System.out.println("-------------------------");
			System.out.println(data[i].toString());
			System.out.println("-------------------------");
			setGUIStatusText("Downloading " + data[i].getTitle());
			data[i].download(dir);
			if(data[i].fileExists()) {
				
				setGUIStatusText("Writing metadata for " + data[i].getTitle());
				data[i].writeID3Tags();
				
			}
			
			setGUIProgressBar(YOUTUBE_PROGRESS_MIDWAY_POINT + (100 - YOUTUBE_PROGRESS_MIDWAY_POINT) / data.length * (i + 1));
			
		}
		
	}
	
	private static void downloadSpotifyAction(String url, String dir) {
		
		setGUIStatus(5, "Getting Spotify data");
		
		StaccatoTrack[] data = MusicFetcher.convertSpotifyData(url);
		if(data == null) {
			
			return;
			
		}
		
		setGUIProgressBar(35);
		
		if(url.contains("/playlist/")) {
			
			setGUIStatusText("Creating playlist folder");
			String playlistName = MusicFetcher.getSpotifyPlaylistName(url);
			if(playlistName == null) {
				
				return;
				
			}
			File playlistFolder;
			
			int uniqueNumber = StaccatoTrack.countRepeatedFileNames(dir, playlistName);
			if(uniqueNumber > 0) {
				
				playlistFolder = new File(dir + "\\" + playlistName + " (" + uniqueNumber + ")");
				dir += "\\" + playlistName + " (" + uniqueNumber + ")";
				
			} else {
				
				playlistFolder = new File(dir + "\\" + playlistName);
				dir += "\\" + playlistName;
				
			}

			playlistFolder.mkdir();
			
		}
		
		for(int i = 0; i < data.length; i++) {
			
			setGUIStatusText("Downloading " + data[i].getTitle());
			data[i].download(dir);
			if(data[i].fileExists()) {
				
				setGUIStatusText("Writing metadata for " + data[i].getTitle());
				data[i].writeID3Tags();
				
			}
			
			setGUIProgressBar(SPOTIFY_PROGRESS_MIDWAY_POINT + (100 - SPOTIFY_PROGRESS_MIDWAY_POINT) / data.length * (i + 1));
			
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
		
		progressBar.setValue(percent);
		
	}
	
	public StaccatoWindow getStaccatoWindow() {
		
		return parentWindow;
		
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		
		downloadButton.setEnabled(enabled);
		
	}
	
	public static void setGUIStatusText(String status) {
		
		if(mainBottomPanel == null) {
			
			System.out.println("==================================================================");
			System.out.println(status);
			System.out.println("==================================================================");
			return;
			
		}
		
		mainBottomPanel.setStatusText(status);
		
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
	
	public static void setGUIProgressBar(int progressBar) {
		
		if(mainBottomPanel == null) {
			
			System.out.println("==================================================================");
			System.out.println("Progress: " + progressBar + "%");
			System.out.println("==================================================================");
			return;
			
		}
		
		mainBottomPanel.setProgressBar(progressBar);
		
	}
	
	public static void setGUIErrorStatus(String status) {
		
		if(mainBottomPanel == null) {
			
			System.err.println("===========================ERROR==================================");
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
		
//		downloadSpotifyAction("https://open.spotify.com/track/74X2u8JMVooG2QbjRxXwR8?si=6c3ea48d93974722", "D:/");
		StaccatoWindow.main(args);
		
	}
	
}
