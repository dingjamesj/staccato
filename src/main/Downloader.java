package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.formdev.flatlaf.icons.FlatOptionPaneAbstractIcon;
import com.formdev.flatlaf.icons.FlatOptionPaneInformationIcon;
import com.formdev.flatlaf.icons.FlatOptionPaneWarningIcon;

public abstract class Downloader {
	
	private static BottomPanel bottomPanel;
	
	/**
	 * Downloads an mp3 given a YouTube URL.
	 * 
	 * @param url YouTube URL
	 * @param dir Directory to put the mp3
	 * @return 0 if download successful, -1 if directory does not exist, -2 if yt-dlp isn't installed, -3 if IOException was thrown during process execution, -4 if process was interrupted
	 */
	public static int download(String url, String dir) {
		
		String[] command = {"yt-dlp", "--extract-audio", "\"" + url + "\""};
		ProcessBuilder downloadProcess = new ProcessBuilder(command);
		File dirObject = new File(dir);
		if(!dirObject.exists()) {
			
			setGUIErrorStatus("Directory does not exist");
			return -1;
			
		}
		
		downloadProcess.directory(new File(dir));
		downloadProcess.inheritIO();
		try {
			
			Process process = downloadProcess.start();
			process.waitFor();
			
		} catch (IOException e) {
			
			String message = e.getMessage();
			
			if(message.toLowerCase().contains("cannot run program \"yt-dlp\"")) {
				
				setGUIErrorStatus("Cannot run yt-dlp");
				e.printStackTrace();
				return -2;
				
			}
			
			setGUIErrorStatus("IO Error: " + message);
			e.printStackTrace();
			return -3;
			
		} catch (InterruptedException e) {
			
			setGUIErrorStatus("Download was interrupted");
			e.printStackTrace();
			return -4;
			
		}
		
		return 0;
		
	}

	/**
	 * 
	 * @return 0 if updated successfully, -1 if update ended unsuccessfully, -2 if there is missing software
	 */
	public static int updateSoftware() {
		
		setGUIStatus("Checking for updates");
		
		String[] command = {"yt-dlp", "--update"};
		ProcessBuilder updateProcess = new ProcessBuilder(command);
		updateProcess.inheritIO();
		
		try {
			
			Process process = updateProcess.start();
			
			BufferedReader processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String output = "";
			int lineCount = 0;
			while(output != null) {
				
				lineCount++;
				
				if(output.contains("up to date")) {
					
					break;
					
				}
				
				if(lineCount > 2) {
					
					setGUIStatus("Updating");
					break;
					
				}
				
				output = processOutput.readLine();
				
			}
			
			process.waitFor();
			
		} catch (IOException e) {

			e.printStackTrace();
			
			if(e.getMessage().contains("cannot run program \"yt-dlp\"")) {
				
				setGUIErrorStatus("yt-dlp is not installed");
				return -2;
				
			} else {
				
				setGUIErrorStatus("IOException: " + e.getMessage());
				return -1;
				
			}
			
		} catch(InterruptedException e) {
			
			setGUIErrorStatus("Download process was interrupted");
			e.printStackTrace();
			return -3;
			
		}
		
		setGUIStatus(0, "Idle");
		return 0;
		
	}
	
	/**
	 * If yt-dlp is missing, then does </br><code>winget install yt-dlp</code></br>
	 * If FFmpeg is missing, then does </br><code>winget uninstall yt-dlp ffmpeg</br>winget install yt-dlp</code></br>
	 * The latter has more steps because, for whatever reason, FFmpeg only works when it is installed as part of yt-dlp.</br>
	 * Hence, if FFmpeg is shown as "missing," it is oftentimes actually installed but just not showing for some reason.
	 * @return
	 */
	public static int installSoftware() {
				
		setGUIStatus(5, "Checking if yt-dlp is installed");
		
		int returnValue;
		
		if(!checkSoftwareInstalled("yt-dlp")) {
			
			setGUIStatus(40, "Installing yt-dlp and FFmpeg");
						
			//If we install yt-dlp, then FFmpeg will be installed too (they are bundled together)
			returnValue = installSoftware("yt-dlp");
			if(returnValue != 0) {
				
				return returnValue;
				
			}
			
			setGUIStatus(0, "Idle");
			showGUIPopup("Restart staccato", "Please restart staccato to complete the installation.", new FlatOptionPaneWarningIcon());
			return 0;
			
		}
		
		//If yt-dlp isn't installed but FFmpeg is...
		setGUIStatus(10, "Checking if FFmpeg is installed");
		if(!checkSoftwareInstalled("ffmpeg")) {
			
			setGUIStatus(40, "Uninstalling yt-dlp and FFmpeg");
			
			returnValue = uninstallSoftware("ffmpeg", "yt-dlp");
			if(returnValue != 0) {
				
				return returnValue;
				
			}
			
			setGUIStatus(60, "Re-installing yt-dlp and FFmpeg");
			
			returnValue = installSoftware("yt-dlp");
			if(returnValue != 0) {
				
				return returnValue;
				
			}
			
			setGUIStatus(0, "Idle");
			showGUIPopup("Restart staccato", "Please restart staccato to complete the installation.", new FlatOptionPaneWarningIcon());
			return 0;
			
		}
		
		//This should never happen.
		showGUIPopup("Information", "No software was installed (TELL JAMES DING THAT THIS SHOULD NEVER HAPPEN).", new FlatOptionPaneInformationIcon());
		return Integer.MIN_VALUE;
		
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
			
		} catch (IOException e) {
			
			System.out.println(software + " does not exist.");
			return false;
			
		} catch(InterruptedException e) {
			
			e.printStackTrace();
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

			setGUIErrorStatus("IO Error: " + e.getMessage());
			e.printStackTrace();
			return -1;
			
		} catch (InterruptedException e) {
			
			setGUIErrorStatus("Installation was interrupted");
			e.printStackTrace();
			return -2;
			
		}
		
		setGUIStatus(0, "Idle");
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

			setGUIErrorStatus("IO Error: " + e.getMessage());
			e.printStackTrace();
			return -1;
			
		} catch (InterruptedException e) {
			
			setGUIErrorStatus("Installation was interrupted");
			e.printStackTrace();
			return -2;
			
		}
		
		setGUIStatus(0, "Idle");
		return 0;
		
	}
	
	private static void setGUIStatus(int progressBar, String status) {
		
		if(bottomPanel == null) {
			
			return;
			
		}
		
		bottomPanel.setProgressBar(progressBar);
		bottomPanel.setStatusText(status, false);
		
	}
	
	private static void setGUIStatus(String status) {
		
		if(bottomPanel == null) {
			
			return;
			
		}
		
		bottomPanel.setStatusText(status, false);
		
	}
	
	private static void setGUIErrorStatus(String status) {
		
		if(bottomPanel == null) {
			
			return;
			
		}
		
		bottomPanel.setProgressBar(0);
		bottomPanel.setStatusText(status, true);
		
	}
	
	private static void showGUIPopup(String title, String message, FlatOptionPaneAbstractIcon icon) {
		
		if(bottomPanel == null) {
			
			return;
			
		}
		
		bottomPanel.getStaccatoWindow().createPopup(title, message, icon);
		
	}
	
	public static void main(String[] args) {
		
		StaccatoWindow.main(args);
//		updateSoftware();
		
	}
	
}
