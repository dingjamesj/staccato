package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.formdev.flatlaf.icons.FlatOptionPaneInformationIcon;
import com.formdev.flatlaf.icons.FlatOptionPaneWarningIcon;

public abstract class Installer {

	/**
	 * 
	 * @return 0 if updated successfully, -1 if update ended unsuccessfully, -2 if there is missing software
	 */
	public static int updateSoftware() {
		
		BottomPanel.setGUIStatus(4, "Checking for updates");
		
		String[] command = {"yt-dlp", "--update"};
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.inheritIO();
		
		try {
			
			Process updateProcess = processBuilder.start();
			
			BufferedReader processOutput = new BufferedReader(new InputStreamReader(updateProcess.getInputStream()));
			String output = "";
			int lineCount = 0;
			while(output != null) {
				
				lineCount++;
				
				if(output.contains("up to date")) {
					
					break;
					
				}
				
				if(lineCount > 2) {
					
					BottomPanel.setGUIStatus(20, "Updating");
					break;
					
				}
				
				output = processOutput.readLine();
				
			}
			
			processOutput.close();
			updateProcess.waitFor();
			
		} catch (IOException e) {

			e.printStackTrace();
			
			if(e.getMessage().contains("cannot run program \"yt-dlp\"")) {
				
				BottomPanel.setGUIErrorStatus("yt-dlp is not installed (updateSoftware)");
				return -2;
				
			} else {
				
				BottomPanel.setGUIErrorStatus("IOException (updateSoftware): " + e.getMessage());
				return -1;
				
			}
			
		} catch(InterruptedException e) {
			
			BottomPanel.setGUIErrorStatus("Download process was interrupted (updateSoftware)");
			e.printStackTrace();
			return -3;
			
		}
		
		BottomPanel.setGUIStatus(0, "Idle");
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
				
		BottomPanel.setGUIStatus(5, "Checking if yt-dlp is installed");
		
		int returnValue;
		
		if(!checkSoftwareInstalled("yt-dlp")) {
			
			BottomPanel.setGUIStatus(40, "Installing yt-dlp and FFmpeg");
						
			//If we install yt-dlp, then FFmpeg will be installed too (they are bundled together)
			returnValue = installSoftware("yt-dlp");
			if(returnValue != 0) {
				
				return returnValue;
				
			}
			
			BottomPanel.setGUIStatus(0, "Idle");
			BottomPanel.showGUIPopup("Restart staccato", "Please restart staccato to complete the installation.", new FlatOptionPaneWarningIcon());
			return 0;
			
		}
		
		//If yt-dlp isn't installed but FFmpeg is...
		BottomPanel.setGUIStatus(10, "Checking if FFmpeg is installed");
		if(!checkSoftwareInstalled("ffmpeg")) {
			
			BottomPanel.setGUIStatus(40, "Uninstalling yt-dlp and FFmpeg");
			
			returnValue = uninstallSoftware("ffmpeg", "yt-dlp");
			if(returnValue != 0) {
				
				return returnValue;
				
			}
			
			BottomPanel.setGUIStatus(60, "Re-installing yt-dlp and FFmpeg");
			
			returnValue = installSoftware("yt-dlp");
			if(returnValue != 0) {
				
				return returnValue;
				
			}
			
			BottomPanel.setGUIStatus(0, "Idle");
			BottomPanel.showGUIPopup("Restart staccato", "Please restart staccato to complete the installation.", new FlatOptionPaneWarningIcon());
			return 0;
			
		}
		
		//This should never happen.
		BottomPanel.showGUIPopup("Information", "No software was installed (TELL JAMES DING THAT THIS SHOULD NEVER HAPPEN).", new FlatOptionPaneInformationIcon());
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
		
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.inheritIO();
		
		try {
			
			Process checkerProcess = processBuilder.start();
			checkerProcess.waitFor();
			
		} catch (IOException e) {
			
			System.out.println(software + " does not exist.");
			return false;
			
		} catch(InterruptedException e) {
			
			e.printStackTrace();
			return false;
			
		}
		
		return true;
		
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
		
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.inheritIO();
		
		try {
			
			Process installProcess = processBuilder.start();
			installProcess.waitFor();
			
		} catch (IOException e) {

			BottomPanel.setGUIErrorStatus("IO Error (installSoftware): " + e.getMessage());
			e.printStackTrace();
			return -1;
			
		} catch (InterruptedException e) {
			
			BottomPanel.setGUIErrorStatus("Installation was interrupted (installSoftware)");
			e.printStackTrace();
			return -2;
			
		}
		
		BottomPanel.setGUIStatus(0, "Idle");
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
		
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.inheritIO();
		
		try {
			
			Process uninstallProcess = processBuilder.start();
			uninstallProcess.waitFor();
			
		} catch (IOException e) {

			BottomPanel.setGUIErrorStatus("IO Error (uninstallSoftware): " + e.getMessage());
			e.printStackTrace();
			return -1;
			
		} catch (InterruptedException e) {
			
			BottomPanel.setGUIErrorStatus("Installation was interrupted (uninstallSoftware)");
			e.printStackTrace();
			return -2;
			
		}
		
		BottomPanel.setGUIStatus(0, "Idle");
		return 0;
		
	}
	
	public static void main(String[] args) {
		
//		File dir = new File("");
//		System.out.println(dir.exists());
//		System.out.println(dir.getPath());
		
		StaccatoWindow.main(args);
		
//		download("https://www.youtube.com/watch?v=56hqrlQxMMI", "D:/");
		
	}
	
}
