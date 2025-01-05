package main;

import java.io.File;
import java.io.IOException;

public abstract class Downloader {
	
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
	 * @return 0 if software is up-to-date, 1 if software was successfully updated, -1 if software was unsuccessfully updated
	 */
	public static int checkAndUpdateSoftware() {
		
		//TODO update ffmpeg and ffprobe
		
		int sumReturnVal = 0;
		
		if(!checkDLPInstalled()) {
			
			String[] command = {"winget", "install", "yt-dlp"};
			ProcessBuilder installProcess = new ProcessBuilder(command);
			installProcess.inheritIO();
			
			try {
				
				installProcess.start();
				
			} catch (IOException e) {

				e.printStackTrace();
				sumReturnVal -= 1;
				
			}
			
		}
		
		if(!checkFFMPEGInstalled()) {
			
			//Here, if it was unsuccessful, the sumReturnVal would decrease by 2
			
		}
		
		if(!checkFFPROBEInstalled()) {
			
			//Here, if it was unsuccessful, the sumReturnVal would decrease by 4
			
		}
		
		if(sumReturnVal >= 1) {
			
			sumReturnVal = 1;
			
		}
		return sumReturnVal;
		
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
	
	public static void main(String[] args) {
		
		System.out.println(download("https://www.youtube.com/watch?v=HfWLgELllZs", "D:/"));
		
	}
	
}
