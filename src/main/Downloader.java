package main;

import java.io.File;
import java.io.IOException;

import com.formdev.flatlaf.icons.FlatOptionPaneInformationIcon;
import com.formdev.flatlaf.icons.FlatOptionPaneWarningIcon;

public abstract class Downloader {
	
	private static BottomPanel bottomPanel;
	
	/**
	 * Downloads an mp3 given a YouTube URL.
	 * 
	 * @param url YouTube URL
	 * @param dir Directory to put the mp3
	 * @return 0 if download successful, -1 if directory does not exist, -2 if yt-dlp isn't installed, -3 if IOException was thrown during process execution
	 */
	public static int download(String url, String dir) {
		
		String[] command = {"yt-dlp", "--extract-audio", "\"" + url + "\""};
		ProcessBuilder downloadProcess = new ProcessBuilder(command);
		File dirObject = new File(dir);
		if(!dirObject.exists()) {
			
			return -1;
			
		}
		
		downloadProcess.directory(new File(dir));
		downloadProcess.inheritIO();
		try {
			
			downloadProcess.start();
			
		} catch (IOException e) {
			
			String message = e.getMessage();
			
			if(message.toLowerCase().contains("cannot run program \"yt-dlp\"")) {
				
				return -2;
				
			}
			
			return -3;
			
		}
		
		return 0;
		
	}
	
	/**
	 * If yt-dlp is missing, then does </br><code>winget install yt-dlp</code></br>
	 * If FFmpeg is missing, then does </br><code>winget uninstall yt-dlp ffmpeg</br>winget install yt-dlp</code></br>
	 * The latter has more steps because, for whatever reason, FFmpeg only works when it is installed as part of yt-dlp.</br>
	 * Hence, if FFmpeg is shown as "missing," it is oftentimes actually installed but just not showing for some reason.
	 * @return
	 */
	public static int checkAndInstallSoftware() {
		
		int returnValue;
		
		bottomPanel.setProgressBar(5);
		bottomPanel.setStatusText("Checking if yt-dlp is installed");
		if(!checkSoftwareInstalled("yt-dlp")) {
			
			bottomPanel.setProgressBar(40);
			bottomPanel.setStatusText("Installing yt-dlp and FFmpeg");
			returnValue = installSoftware("yt-dlp");
			
			//If we install yt-dlp, then FFmpeg will be installed too (they are bundled together)
			returnValue = installSoftware("yt-dlp");
			if(returnValue != 0) {
				
				return returnValue;
				
			}
			
			bottomPanel.setProgressBar(0);
			bottomPanel.setStatusText("Idle");
			bottomPanel.getStaccatoWindow().createPopup("Restart staccato", "Please restart staccato to complete the installation.", new FlatOptionPaneWarningIcon());
			
			return 0;
			
		}
		
		bottomPanel.setProgressBar(10);
		bottomPanel.setStatusText("Checking if FFmpeg is installed");
		if(!checkSoftwareInstalled("ffmpeg")) {
			
			bottomPanel.setProgressBar(40);
			bottomPanel.setStatusText("Uninstalling yt-dlp and FFmpeg");
			returnValue = uninstallSoftware("ffmpeg", "yt-dlp");
			if(returnValue != 0) {
				
				return returnValue;
				
			}
			
			bottomPanel.setProgressBar(60);
			bottomPanel.setStatusText("Re-installing yt-dlp and FFmpeg");
			returnValue = installSoftware("yt-dlp");
			if(returnValue != 0) {
				
				return returnValue;
				
			}
			
			bottomPanel.setProgressBar(0);
			bottomPanel.setStatusText("Idle");
			bottomPanel.getStaccatoWindow().createPopup("Restart staccato", "Please restart staccato to complete the installation.", new FlatOptionPaneWarningIcon());
			
			return 0;
			
		}
		
		//This should never happen.
		bottomPanel.getStaccatoWindow().createPopup("Information", "No software was installed.", new FlatOptionPaneInformationIcon());
		return 0;
		
	}
	
	public static boolean checkSoftwareInstalled(String software) {
		
		String[] command = new String[2];
		command[0] = software;
		if(software.equals("yt-dlp")) {
			
			command[1] = "--version";
			
		} else {
			
			command[1] = "-version";
			
		}
		
		ProcessBuilder checkerProcess = new ProcessBuilder(command);
		checkerProcess.inheritIO();
		
		try {
			
			Process process = checkerProcess.start();
			process.waitFor();
			
		} catch (IOException | InterruptedException e) {
			
			System.out.println(software + " does not exist.");
			return false;
			
		}
		
		return true;
		
	}
	
	public static void setBottomPanel(BottomPanel bottomPanel) {
		
		Downloader.bottomPanel = bottomPanel;
		
	}
	
	private static int installSoftware(String... names) {
		
		if(names == null) {
			
			return 0;
			
		}
		
		String[] command = new String[2 + names.length];
		command[0] = "winget";
		command[1] = "install";
		for(int i = 0; i < names.length; i++) {
			
			command[2 + i] = names[i];
			
		}
		
		ProcessBuilder installProcess = new ProcessBuilder(command);
		installProcess.inheritIO();
		
		try {
			
			Process process = installProcess.start();
			process.waitFor();
			
		} catch (IOException e) {

			e.printStackTrace();
			return -1;
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			return -2;
			
		}
		
		return 0;
		
	}
	
	private static int uninstallSoftware(String... names) {
		
		if(names == null) {
			
			return 0;
			
		}
		
		String[] command = new String[2 + names.length];
		command[0] = "winget";
		command[1] = "uninstall";
		for(int i = 0; i < names.length; i++) {
			
			command[2 + i] = names[i];
			
		}
		
		ProcessBuilder uninstallProcess = new ProcessBuilder(command);
		uninstallProcess.inheritIO();
		
		try {
			
			Process process = uninstallProcess.start();
			process.waitFor();
			
		} catch (IOException e) {

			e.printStackTrace();
			return -1;
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			return -2;
			
		}
		
		return 0;
		
	}
	
	public static void main(String[] args) {
		
		StaccatoWindow.main(args);
		
	}
	
}
