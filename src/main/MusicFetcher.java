package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.apache.hc.core5.http.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.neovisionaries.i18n.CountryCode;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchAlbumsRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

public abstract class MusicFetcher {
	
	/**
	 * @param url Spotify URL
	 * @return An array of StaccatoTracks
	 */
	public static StaccatoTrack[] convertSpotifyData(String url) {
		
		Track[] tracks;
		if(url.contains("/track/")) {
			
			tracks = new Track[] {getSpotifyTrack(url)};
			
		} else if(url.contains("/playlist/")) {
			
			tracks = getSpotifyPlaylist(url);
			
		} else {
			
			BottomPanel.setGUIErrorStatus("Only Spotify songs and playlists are supported");
			return null;
			
		}
		
		StaccatoTrack[] data = new StaccatoTrack[tracks.length];
		String artistsStr;
		for(int i = 0; i < tracks.length; i++) {
			
			artistsStr = "";
			for(int a = 0; a < tracks[i].getArtists().length; a++) {
				
				artistsStr += tracks[i].getName() + " ";
				
			}
			
			data[i] = new StaccatoTrack(tracks[i].getName(), artistsStr, tracks[i].getAlbum().getName(), 
					searchYouTube(tracks[i].getName(), artistsStr, 5), tracks[i].getAlbum().getImages()[0].getUrl());
			
		}
		
		return data;
		
	}
	
	public static String getAlbumCoverURL(String albumTitle, String artists) {
		
		String[] apiKeys = APIKeysStorage.getIDandSecret();
		if(apiKeys == null) {
			
			return null;
			
		}
		
		String clientID = apiKeys[0];
		String clientSecret = apiKeys[1];
		SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientID).setClientSecret(clientSecret).build();
		
		SearchAlbumsRequest request = spotifyApi.searchAlbums(albumTitle + " " + artists).market(CountryCode.US).build();
		try {
			
			AlbumSimplified[] albums = request.execute().getItems();
			return albums[0].getImages()[0].getUrl();
			
		} catch (ParseException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Parse Exception (getAlbumCoverURL): " + e.getMessage());
			
		} catch (SpotifyWebApiException e) {

			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Spotify API Exception (getAlbumCoverURL): " + e.getMessage());
			
		} catch (IOException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("IO Exception (getAlbumCoverURL): " + e.getMessage());
			
		}
		
		return null;
		
	}
	
	private static Track[] getSpotifyPlaylist(String url) {
		
		String[] apiKeys = APIKeysStorage.getIDandSecret();
		if(apiKeys == null) {
			
			return null;
			
		}
		
		String clientID = apiKeys[0];
		String clientSecret = apiKeys[1];
		SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientID).setClientSecret(clientSecret).build();
		
		GetPlaylistsItemsRequest request = spotifyApi.getPlaylistsItems(url).market(CountryCode.US).build();
		try {
			
			PlaylistTrack[] playlistItems = request.execute().getItems();
			Track[] tracks = new Track[playlistItems.length];
			for(int i = 0; i < playlistItems.length; i++) {
				
				tracks[i] = (Track) playlistItems[i].getTrack();
				
			}
			return tracks;
			
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
	
	private static Track getSpotifyTrack(String url) {
		
		String[] apiKeys = APIKeysStorage.getIDandSecret();
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
	
	public static String getSpotifyPlaylistName(String url) {
		
		String[] apiKeys = APIKeysStorage.getIDandSecret();
		if(apiKeys == null) {
			
			return null;
			
		}
		
		String clientID = apiKeys[0];
		String clientSecret = apiKeys[1];
		
		SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientID).setClientSecret(clientSecret).build();
		GetPlaylistRequest request = spotifyApi.getPlaylist(url).market(CountryCode.US).build();
		try {
			
			Playlist playlist = request.execute();
			return playlist.getName();
			
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
	 * @return The ID of the YouTube video with the best matching results to arg
	 */
	private static String searchYouTube(String title, String artist, int numResults) {
		
		String[] command = {"yt-dlp", "--write-info-json", "--skip-download", "--no-write-playlist-metafiles", "\"ytsearch" + numResults + ":" + title + " " + artist + "\""};
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		File jsonDir = new File(StaccatoWindow.TEMP_JSON_FILES_DIR_STR);
		jsonDir.mkdir();
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
				clearTemporaryJSONs();
				return null;
				
			}
			
			BottomPanel.setGUIErrorStatus("IO Exception (searchYouTube): " + message);
			clearTemporaryJSONs();
			return null;
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Download was interrupted");
			clearTemporaryJSONs();
			return null;
			
		}
		
		File[] jsonFiles = jsonDir.listFiles();
		if(jsonFiles == null) {
			
			BottomPanel.setGUIErrorStatus("No search results found");
			clearTemporaryJSONs();
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
				clearTemporaryJSONs();
				return null;
				
			} catch (JsonSyntaxException e) {
				
				e.printStackTrace();
				BottomPanel.setGUIErrorStatus("JSON file is malformed");
				clearTemporaryJSONs();
				return null;
				
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
				BottomPanel.setGUIErrorStatus("JSON file was not found");
				clearTemporaryJSONs();
				return null;
				
			}
			
			points[i] = calculateVideoScore(map, i, title, artist);
			ids[i] = map.get("id").toString();
			
			if(points[i] > maxPoints) {
				
				maxPoints = points[i];
				maxPointsIndex = i;
				
			}
			
		}
		
		clearTemporaryJSONs();
		return ids[maxPointsIndex];
		
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
	
	private static void clearTemporaryJSONs() {
		
		String[] jsonDirStrs = new File(StaccatoWindow.TEMP_JSON_FILES_DIR_STR).list();
		for(String dirStr: jsonDirStrs) {
			
			new File(StaccatoWindow.TEMP_JSON_FILES_DIR_STR, dirStr).delete();
			
		}
		
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
