package main;

import java.io.File;
import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;

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
	 * Updates yt-dlp, ffmpeg, and ffprobe
	 */
	public static int checkAndInstallSoftware() {
		
		//TODO update ffmpeg and ffprobe
		int returnValue = 0;
		
		bottomPanel.setStatusText("Checking if yt-dlp is installed");
		if(!checkDLPInstalled()) {
			
			returnValue = installSoftware("yt-dlp");
			if(returnValue != 0) {
				
				return returnValue;
				
			}
			
		}
		
		bottomPanel.setStatusText("Checking if ffmpeg is installed");
		if(!checkFFMPEGInstalled()) {
			
			returnValue = installSoftware("ffmpeg");
			if(returnValue != 0) {
				
				return returnValue;
				
			}
			
		}
		
		bottomPanel.setStatusText("Checking if ffprobe is installed");
		if(!checkFFPROBEInstalled()) {
			
			returnValue = installSoftware("ffprobe");
			if(returnValue != 0) {
				
				return returnValue;
				
			}
			
		}
		
		return 0;
		
	}
	
	public static boolean checkDLPInstalled() {
		
		String[] command = {"yt-dlp", "--version"};
		ProcessBuilder checkerProcess = new ProcessBuilder(command);
		checkerProcess.inheritIO();
		
		try {
			
			checkerProcess.start();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return false;
			
		}
		
		return true;
		
	}
	
	public static boolean checkFFMPEGInstalled() {
		
		String[] command = {"ffmpeg", "-version"};
		ProcessBuilder checkerProcess = new ProcessBuilder(command);
		checkerProcess.inheritIO();
		
		try {
			
			checkerProcess.start();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return false;
			
		}
		
		return true;
				
	}
	
	public static boolean checkFFPROBEInstalled() {
		
		String[] command = {"ffprobe", "-version"};
		ProcessBuilder checkerProcess = new ProcessBuilder(command);
		checkerProcess.inheritIO();
		
		try {
			
			checkerProcess.start();
			
		} catch (IOException e) {
			
			e.printStackTrace();
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
		
		System.out.println(download("https://www.youtube.com/watch?v=HfWLgELllZs", "D:/"));
		
	}
	
}
