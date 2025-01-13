package main;

import java.io.File;
import java.io.IOException;

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
	
	public static int checkAndInstallSoftware() {
		
		int returnValue = 0;
		
		bottomPanel.setProgressBar(10);
		bottomPanel.setStatusText("Checking if yt-dlp is installed");
		if(!checkSoftwareInstalled("yt-dlp")) {
			
			bottomPanel.setProgressBar(30);
			bottomPanel.setStatusText("Installing yt-dlp");
			returnValue = installSoftware("yt-dlp");
			if(returnValue != 0) {
				
				return returnValue;
				
			}
			
		}
		
		bottomPanel.setProgressBar(60);
		bottomPanel.setStatusText("Checking if FFmpeg is installed");
		if(!checkSoftwareInstalled("ffmpeg")) {
			
			bottomPanel.setProgressBar(80);
			bottomPanel.setStatusText("Installing FFmpeg");
			returnValue = installSoftware("ffmpeg");
			if(returnValue != 0) {
				
				return returnValue;
				
			}
			
		}
		
		bottomPanel.setProgressBar(0);
		bottomPanel.setStatusText("Status: Idle");
		bottomPanel.getStaccatoWindow().createErrorPopup("Restart Staccato", "Please restart staccato to complete the installation.", new FlatOptionPaneWarningIcon());
		
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
	
	private static int installSoftware(String name) {
		
		String[] command = {"winget", "install", name};
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
	
	public static void main(String[] args) {
		
//		installSoftware("yt-dlp");
		StaccatoWindow.main(args);
		
	}
	
}
