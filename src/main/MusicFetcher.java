package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.hc.core5.http.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.neovisionaries.i18n.CountryCode;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.exceptions.detailed.BadRequestException;
import se.michaelthelin.spotify.exceptions.detailed.NotFoundException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.AbstractDataRequest;
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
		
		if(!url.contains("spotify.com")) {
			
			BottomPanel.setGUIErrorStatus("This URL " + url + " is not a Spotify link (this message should never appear since we check beforehand)");
			return null;
			
		}
		
		Track[] tracks;
		if(url.contains("/track/")) {
			
			Track singleTrack = getSpotifyTrack(extractSpotifyIDFromURL(url));
			if(singleTrack == null) {
				
				tracks = null;
				
			} else {
				
				tracks = new Track[] {singleTrack};
				
			}
						
		} else if(url.contains("/playlist/")) {
			
			tracks = getSpotifyPlaylist(extractSpotifyIDFromURL(url));
			
		} else {
			
			BottomPanel.setGUIErrorStatus("Only Spotify songs and playlists are supported");
			return null;
			
		}
		
		if(tracks == null) {
			
			return null;
			
		}
		
		StaccatoTrack[] data = new StaccatoTrack[tracks.length];
		String artistsStr;
		for(int i = 0; i < tracks.length; i++) {
			
			artistsStr = "";
			for(int a = 0; a < tracks[i].getArtists().length; a++) {
				
				artistsStr += tracks[i].getArtists()[a].getName() + " ";
				
			}
			
			data[i] = new StaccatoTrack(tracks[i].getName(), artistsStr, tracks[i].getAlbum().getName(), 
					searchYouTube(tracks[i].getName(), artistsStr, 5), tracks[i].getAlbum().getImages()[0].getUrl());
			
		}
		
		return data;
		
	}
	
	public static String getAlbumCoverURL(String albumTitle, String artists) {
		
		SpotifyApi spotifyApi = getSpotifyAPI();
		if(spotifyApi == null) {
			
			return null;
			
		}
		
		SearchAlbumsRequest request = spotifyApi.searchAlbums(albumTitle + " " + artists).market(CountryCode.US).build();
		Paging<AlbumSimplified> data = getDataWithSpotifyAPIRequest(request, "getAlbumCoverURL");
		
		if(data == null) {
			
			return null;
			
		} else {
			
			return data.getItems()[0].getImages()[0].getUrl();
			
		}
		
	}
	
	private static Track[] getSpotifyPlaylist(String id) {
		
		SpotifyApi spotifyApi = getSpotifyAPI();
		if(spotifyApi == null) {
			
			return null;
			
		}
		
		GetPlaylistsItemsRequest request = spotifyApi.getPlaylistsItems(id).market(CountryCode.US).build();
		Paging<PlaylistTrack> data = getDataWithSpotifyAPIRequest(request, "getSpotifyPlaylist");
		
		if(data == null) {
			
			return null;
			
		} else {
			
			PlaylistTrack[] playlistItems = data.getItems();
			Track[] tracks = new Track[playlistItems.length];
			for(int i = 0; i < playlistItems.length; i++) {
				
				tracks[i] = (Track) playlistItems[i].getTrack();
				
			}
			return tracks;
			
		}
				
	}
	
	private static Track getSpotifyTrack(String id) {
		
		SpotifyApi spotifyApi = getSpotifyAPI();
		if(spotifyApi == null) {
			
			return null;
			
		}
		
		GetTrackRequest request = spotifyApi.getTrack(id).market(CountryCode.US).build();
		
		return getDataWithSpotifyAPIRequest(request, "getSpotifyTrack");
		
	}
	
	public static String getSpotifyPlaylistName(String id) {
		
		SpotifyApi spotifyApi = getSpotifyAPI();
		if(spotifyApi == null) {
			
			return null;
			
		}
		
		GetPlaylistRequest request = spotifyApi.getPlaylist(id).market(CountryCode.US).build();
		Playlist playlist = getDataWithSpotifyAPIRequest(request, "getSpotifyPlaylistName");
		
		if(playlist == null) {
			
			return null;
			
		} else {
			
			return playlist.getName();
			
		}
		
	}
	
	/**
	 * @param arg
	 * @param numResults
	 * @return The ID of the YouTube video with the best matching results to arg
	 */
	private static String searchYouTube(String title, String artist, int numResults) {
		
		String[] command = {
				"yt-dlp", 
				"--write-info-json",
				"--skip-download", 
				"--no-write-playlist-metafiles", 
				"\"ytsearch" + numResults + ":" + title + " " + artist + "\""
				};
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
	
	public static String getYouTubePlaylistName(String url) {
		
		String[] command = {
				"yt-dlp", 
				"--skip-download",
				"--no-warning",
				"-I",
				"1:1",
				"--print",
				"playlist_title",
				"\"" + url + "\""
				};
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.inheritIO();
		
		try {
			
			Process playlistNameGetterProcess = processBuilder.start();
			BufferedReader processOutput = new BufferedReader(new InputStreamReader(playlistNameGetterProcess.getInputStream()));
			String output = "";
			String buffer = "";
			while(buffer != null) {
				
				buffer = processOutput.readLine();
				if(buffer != null) {
					
					output = buffer;
					
				}
				
			}
			playlistNameGetterProcess.waitFor();
			
			return output;
			
		} catch (IOException e) {

			e.printStackTrace();
			
			if(e.getMessage().contains("cannot run program \"yt-dlp\"")) {
				
				BottomPanel.setGUIErrorStatus("Cannot run yt-dlp");
				
			} else {
				
				BottomPanel.setGUIErrorStatus("IOException (updateSoftware): " + e.getMessage());
				
			}
			
		} catch(InterruptedException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Download process was interrupted (updateSoftware)");
			
		}
		
		return null;
		
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
	
	private static SpotifyApi getSpotifyAPI() {
		
		String[] apiKeys = APIKeysStorage.getIDandSecret();
		if(apiKeys == null) {
			
			APIKeysStorage.openSetAPIKeysDialog(true);
			return null;
			
		}
		
		String clientID = apiKeys[0];
		String clientSecret = apiKeys[1];
		System.out.println(clientID + " " + clientSecret);
		SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientID).setClientSecret(clientSecret).build();
		ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
		try {
			
			ClientCredentials clientCredentials = clientCredentialsRequest.execute();
			spotifyApi.setAccessToken(clientCredentials.getAccessToken());
			return spotifyApi;
			
		} catch (ParseException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Parse Exception (getSpotifyAPI): " + e.getMessage());
			
		} catch(BadRequestException e) {
						
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("The provided Spotify API client ID and secret may be invalid");
			APIKeysStorage.openSetAPIKeysDialog(true);
			
		} catch (SpotifyWebApiException e) {

			e.printStackTrace();
			BottomPanel.setGUIErrorStatus(e.getClass().getSimpleName() + "(getSpotifyAPI): " + e.getMessage());
			
		} catch (IOException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("IO Exception (getSpotifyAPI): " + e.getMessage());
			
		}
		
		return null;
		
	}
	
	private static <T> T getDataWithSpotifyAPIRequest(AbstractDataRequest<T> request, String methodName) {
		
		try {
			
			T data = request.execute();
			return data;
			
		} catch (ParseException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Parse Exception (" + methodName + "): " + e.getMessage());
			
		} catch(BadRequestException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus(e.getClass().getSimpleName() + "(" + methodName + "): " + "The provided Spotify API client ID and secret may be invalid");
			APIKeysStorage.openSetAPIKeysDialog(true);
			
		} catch(NotFoundException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Spotify track/playlist not found");
			
		} catch (SpotifyWebApiException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus(e.getClass().getSimpleName() + "(" + methodName + "): " + e.getMessage());
			
		}  catch (IOException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("IO Exception (" + methodName + "): " + e.getMessage());
			
		}
		
		return null;
		
	}
	
	public static String extractSpotifyIDFromURL(String url) {
		
		//https://open.spotify.com/track/50a8bKqlwDEqeiEknrzkTO?si=7d803f686f0c4175
		//https://open.spotify.com/playlist/1zUUwGZh02drh2M13yNcVD?si=9e646c914f5843d6
		
		int idBeginIndex = url.indexOf("/track/");
		if(idBeginIndex == -1) {
			
			idBeginIndex = url.indexOf("/playlist/");
			if(idBeginIndex == -1) {
				
				return null;
				
			}
			
			idBeginIndex += "/playlist/".length();
			
		} else {
			
			idBeginIndex += "/track/".length();
			
		}
		
		int questionMarkIndex = url.indexOf('?');
		if(questionMarkIndex == -1) {
			
			return url.substring(idBeginIndex);
			
		} else {
			
			return url.substring(idBeginIndex, questionMarkIndex);
			
		}
		
	}
	
	public static String extractYouTubeIDFromURL(String url) {
		
		//https://youtu.be/tfSS1e3kYeo?si=v4rcNgiHwUIQV5J6
		//https://www.youtube.com/watch?v=tfSS1e3kYeo
		//https://youtu.be/tfSS1e3kYeo?si=v4rcNgiHwUIQV5J6&t=2
		
		int idBeginIndex;
		if(url.contains("youtu.be/")) {
			
			idBeginIndex = url.indexOf("youtu.be/") + "youtu.be/".length();
			
		} else if(url.contains("youtube.com/watch?v=")) {
			
			idBeginIndex = url.indexOf("youtube.com/watch?v=") + "youtube.com/watch?v=".length();
			
		} else {
			
			return null;
			
		}
		
		int questionMarkIndex = url.indexOf('?', idBeginIndex);
		if(questionMarkIndex == -1) {
			
			return url.substring(idBeginIndex);
			
		} else {
			
			return url.substring(idBeginIndex, questionMarkIndex);
			
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
//		
//		System.out.println(extractSpotifyIDFromURL("https://open.spotify.com/track/50a8bKqlwDEqeiEknrzkTO"));
//		System.out.println(extractSpotifyIDFromURL("https://open.spotify.com/playlist/1zUUwGZh02drh2M13yNcVD"));
//		System.out.println(extractSpotifyIDFromURL("https://open.spotify.com/track/3ruoIF2UnoXdzK8mR61ebq?si=65c4f9f7b10a4973"));
		
//		getSpotifyAPI();
		
//		System.out.println(getYouTubePlaylistName("https://www.youtube.com/playlist?list=PLpeFO20OwBF7iEECy0biLfP34s0j-8wzk"));
		
		StaccatoWindow.main(args);
		
	}
	
}
