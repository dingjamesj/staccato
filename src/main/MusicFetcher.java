package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.apache.hc.core5.http.ParseException;

import com.formdev.flatlaf.icons.FlatOptionPaneWarningIcon;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.neovisionaries.i18n.CountryCode;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

public abstract class MusicFetcher {
	
	public static Paging<PlaylistTrack> getSpotifyPlaylist(String url) {
		
		String[] apiKeys = getIDandSecret();
		if(apiKeys == null) {
			
			return null;
			
		}
		
		String clientID = apiKeys[0];
		String clientSecret = apiKeys[1];
		
		SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientID).setClientSecret(clientSecret).build();
		GetPlaylistsItemsRequest request = spotifyApi.getPlaylistsItems(url).market(CountryCode.US).build();
		try {
			
			Paging<PlaylistTrack> playlistItems = request.execute();
			return playlistItems;
			
		} catch (ParseException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Parse Exception (getSpotifyPlaylist): " + e.getMessage());
			
		} catch (SpotifyWebApiException e) {

			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Spotify API Exception (getSpotifyPlaylist): " + e.getMessage());
			
		} catch (IOException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("IO Exception (getSpotifyPlaylist): " + e.getMessage());
			
		}
		
		return null;
		
	}
	
	public static Track getSpotifyTrack(String url) {
		
		String[] apiKeys = getIDandSecret();
		if(apiKeys == null) {
			
			return null;
			
		}
		
		String clientID = apiKeys[0];
		String clientSecret = apiKeys[1];
		
		SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientID).setClientSecret(clientSecret).build();
		GetTrackRequest request = spotifyApi.getTrack(url).market(CountryCode.US).build();
		try {
			
			Track track = request.execute();
			return track;
			
		} catch (ParseException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Parse Exception (getSpotifyTrack): " + e.getMessage());
			
		} catch (SpotifyWebApiException e) {

			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Spotify API Exception (getSpotifyTrack): " + e.getMessage());
			
		} catch (IOException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("IO Exception (getSpotifyTrack): " + e.getMessage());
			
		}
		
		return null;
		
	}
	
	/**
	 * @param arg
	 * @param numResults
	 * @return The URL of the YouTube video with the best matching results to arg
	 */
	private static String searchYouTube(String title, String artist, int numResults) {
		
		String[] command = {"yt-dlp", "--write-info-json", "--skip-download", "--no-write-playlist-metafiles", "\"ytsearch" + numResults + ":" + title + " " + artist + "\""};
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		File jsonDir = new File(System.getProperty("user.dir") + "\\temp");
		processBuilder.directory(jsonDir);
		processBuilder.inheritIO();
		
		try {
			
			Process searchProcess = processBuilder.start();
			searchProcess.waitFor();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			String message = e.getMessage();
			
			if(message.toLowerCase().contains("cannot run program \"yt-dlp\"")) {
				
				BottomPanel.setGUIErrorStatus("Cannot run yt-dlp");
				e.printStackTrace();
				return null;
				
			}
			
			BottomPanel.setGUIErrorStatus("IO Exception (searchYouTube): " + message);
			e.printStackTrace();
			return null;
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Download was interrupted");
			return null;
			
		}
		
		File[] jsonFiles = jsonDir.listFiles();
		if(jsonFiles == null) {
			
			BottomPanel.setGUIErrorStatus("No search results found");
			return null;
			
		}
		
		@SuppressWarnings("rawtypes")
		Map map;
		Gson gson = new Gson();
		int[] points = new int[jsonFiles.length];
		String[] ids = new String[jsonFiles.length];
		int maxPoints = Integer.MIN_VALUE;
		int maxPointsIndex = -1;
		for(int i = 0; i < jsonFiles.length; i++) {
			
			try {
				
				map = gson.fromJson(new FileReader(jsonFiles[i]), Map.class);
				
			} catch (JsonIOException e) {
				
				e.printStackTrace();
				BottomPanel.setGUIErrorStatus("JSON IO Exception (searchYouTube): " + e.getMessage());
				return null;
				
			} catch (JsonSyntaxException e) {
				
				e.printStackTrace();
				BottomPanel.setGUIErrorStatus("JSON file is malformed");
				return null;
				
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
				BottomPanel.setGUIErrorStatus("JSON file was not found");
				return null;
				
			}
			
			points[i] = calculateVideoScore(map, i, title, artist);
			ids[i] = map.get("id").toString();
			
			if(points[i] > maxPoints) {
				
				maxPoints = points[i];
				maxPointsIndex = i;
				
			}
			
		}
		
		return "https://www.youtube.com/watch?v=" + ids[maxPointsIndex];
		
	}
	
	@SuppressWarnings("rawtypes")
	private static int calculateVideoScore(Map videoData, int videoIndex, String targetTitle, String targetArtist) {
		
		targetTitle = targetTitle.toLowerCase();
		targetArtist = targetArtist.toLowerCase();
		
		int score = 0;
		String videoTitle = videoData.get("title").toString().toLowerCase();
		String channelName = videoData.get("channel").toString().toLowerCase();
		String description = videoData.get("description").toString().toLowerCase();
		
		if(!videoTitle.contains(targetTitle)) {
			
			score -= 2;
			
		}
		
		if(videoTitle.contains(targetArtist)) {
			
			score += 1;
			
		}
		
		if(videoTitle.contains("music video") || videoTitle.contains("official video") || videoTitle.contains("mtv") || videoTitle.contains("1 hour") 
				|| videoTitle.contains("mv") || videoTitle.contains("edit") || videoTitle.contains("live") || videoTitle.contains("reverb") || videoTitle.contains("sped-up")
				|| videoTitle.contains("slowed") || videoTitle.contains("sped up") || videoTitle.contains("remix") || videoTitle.contains("cover")) {
			
			score -= 3;
			
		}
		
		if(videoTitle.contains("audio") || videoTitle.contains("lyrics")) {
			
			score += 2;
			
		}
		
		if(videoIndex == 0) {
			
			score += 2;
			
		}
		
		if(videoIndex != 0 && videoIndex > 3) {
			
			score += 1;
			
		}
		
		if(channelName.contains(targetArtist)) {
			
			score += 1;
			
		}
		
		if(description.contains("auto-generated by youtube")) {
			
			score += 3;
			
		}
		
		return score;
		
	}
	
	public static String getAPIKey(String dirStr) {
		
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
	
	public static void main(String[] args) {
		
//		File jsonDir = new File("D:\\TESTING FOR STACCATO");
//		File[] jsonFiles = jsonDir.listFiles();
//		for(int i = 0; i < jsonFiles.length; i++) {
//			
//			System.out.println(i + ": " + jsonFiles[i].getName());
//			
//		}
		
		StaccatoWindow.main(args);
		
	}
	
}
