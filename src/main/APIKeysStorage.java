package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.formdev.flatlaf.icons.FlatOptionPaneWarningIcon;

public class APIKeysStorage {

	/**
	 * @return An array that contains the Spotify Client ID and the secret, in that order
	 */
	public static String[] getIDandSecret() {
		
		String clientID = getAPIKey(StaccatoWindow.SPOTIFY_CLIENT_ID_DIR_STR);
		String clientSecret = getAPIKey(StaccatoWindow.SPOTIFY_CLIENT_SECRET_DIR_STR);
		
		if(clientID == null && clientSecret == null) {
			
			BottomPanel.showGUIPopup("No API Key Found", "<html>A <b>Spotify API client ID</b> was not found at " + StaccatoWindow.SPOTIFY_CLIENT_ID_DIR_STR
					+ "<br></br>and a <b>Spotify API client secret</b> was not found at " + StaccatoWindow.SPOTIFY_CLIENT_SECRET_DIR_STR
					+ "<br></br>For more information on how to get the client ID and secret, please go to "
					+ "<br></br><a href=\"https://developer.spotify.com/documentation/web-api/tutorials/getting-started\">https://developer.spotify.com/documentation/web-api/tutorials/getting-started</a>.</html>", 
					new FlatOptionPaneWarningIcon());
			return null;
			
		} else if(clientID == null) {
			
			BottomPanel.showGUIPopup("No API Key Found", "<html>A <b>Spotify API client ID</b> was not found at " + StaccatoWindow.SPOTIFY_CLIENT_ID_DIR_STR
					+ "<br></br>For more information on how to get the client ID, please go to "
					+ "<br></br><a href=\"https://developer.spotify.com/documentation/web-api/tutorials/getting-started\">https://developer.spotify.com/documentation/web-api/tutorials/getting-started</a>.</html>", 
					new FlatOptionPaneWarningIcon());
			return null;
			
		} else if(clientSecret == null) {
			
			BottomPanel.showGUIPopup("No API Key Found", "<html>A <b>Spotify API client secret</b> was not found at " + StaccatoWindow.SPOTIFY_CLIENT_ID_DIR_STR
					+ "<br></br>For more information on how to get the client ID and secret, please go to "
					+ "<br></br><a href=\"https://developer.spotify.com/documentation/web-api/tutorials/getting-started\">https://developer.spotify.com/documentation/web-api/tutorials/getting-started</a>.</html>", 
					new FlatOptionPaneWarningIcon());
			return null;
			
		}
		
		return new String[] {clientID, clientSecret};
		
	}
	
	private static String getAPIKey(String dirStr) {
		
		File file = new File(dirStr);
		String text = "";
		String currLine = "";
		
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			currLine = reader.readLine();
			while(currLine != null) {
				
				text += currLine;
				
			}
			
			reader.close();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Spotify API key file not found at " + dirStr);
			return null;
			
		} catch (IOException e) {

			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("IO Exception (getAPIKey): " + e.getMessage());
			return null;
			
		}
				
		return text;
		
	}
	
	public static void main(String[] args) {
		
		String[] keys = getIDandSecret();
		if(keys != null) {
			
			System.out.println("Client ID: " + keys[0]);
			System.out.println("Client Secret: " + keys[1]);
			
		}
		
		StaccatoWindow.main(args);
		
	}
	
}
