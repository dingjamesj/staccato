package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hc.core5.http.ParseException;

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
	
	public static final String YTDLP_SEARCH_INFO_SEPARATOR = "<><><><><><><><><>"; //This is the separator used because angle brackets aren't allowed in YouTube descriptions
	public static final String YOUTUBE_UNAVAILABLE_FIELD_PLACEHOLDER = "<N/A>";
	
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
	
	public static StaccatoTrack[] convertYouTubeData(String url, String... args) {
		
		if(!url.contains("youtube.com") && !url.contains("youtu.be")) {
			
			BottomPanel.setGUIErrorStatus("This URL " + url + " is not a YouTube link (this message should never appear since we check beforehand)");
			return null;
			
		}
		
		if(args != null && args.length != 3) {
			
			BottomPanel.setGUIErrorStatus("Missing song property arguments (convertYouTubeData) (THIS ERROR SHOULD NOT HAPPEN!!)");
			return null;
			
		}
		
		if(url.contains("/playlist")) {
			
			ProcessBuilder processBuilder;
			Process process;
			BufferedReader processOutput;
			String output;
			
			try {
				
				String[] playlistLengthCommand = {
						"yt-dlp",
						"--skip-download",
						"--no-warning",
						"--print",
						"playlist_count",
						"-I",
						"1:1",
						"\"" + url + "\""
				};
				processBuilder = new ProcessBuilder(playlistLengthCommand);
				process = processBuilder.start();
				processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
				output = processOutput.readLine();
				processOutput.close();
				process.waitFor();
				BottomPanel.setGUIProgressBar(BottomPanel.YOUTUBE_PROGRESS_MIDWAY_POINT / 5);
				
				StaccatoTrack[] data = new StaccatoTrack[Integer.parseInt(output)];
				
				String[] getPlaylistIDsCommand = {
						"yt-dlp",
						"--skip-download",
						"--no-warning",
						"--print",
						"id",
						"\"" + url + "\""
				};
				processBuilder = new ProcessBuilder(getPlaylistIDsCommand);
				process = processBuilder.start();
				processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
				output = processOutput.readLine();
				for(int i = 0; output != null; i++) {
					
					data[i] = new StaccatoTrack(null, null, null, output, null);
					output = processOutput.readLine();
					
				}
				processOutput.close();
				process.waitFor();
				BottomPanel.setGUIProgressBar(BottomPanel.YOUTUBE_PROGRESS_MIDWAY_POINT / 5 * 2);
				
				String[] getPlaylistTitlesCommand = {
						"yt-dlp",
						"--skip-download",
						"--no-warning",
						"--print",
						"title",
						"\"" + url + "\""
				};
				processBuilder = new ProcessBuilder(getPlaylistTitlesCommand);
				process = processBuilder.start();
				processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
				output = processOutput.readLine();
				for(int i = 0; output != null; i++) {
					
					data[i].setTitle(output);
					output = processOutput.readLine();
					
				}
				processOutput.close();
				process.waitFor();
				BottomPanel.setGUIProgressBar(BottomPanel.YOUTUBE_PROGRESS_MIDWAY_POINT / 5 * 3);
				
				String[] getPlaylistArtistsCommand = {
						"yt-dlp",
						"--skip-download",
						"--no-warning",
						"--print",
						"artist",
						"--output-na-placeholder",
						YOUTUBE_UNAVAILABLE_FIELD_PLACEHOLDER,
						"\"" + url + "\""
				};
				processBuilder = new ProcessBuilder(getPlaylistArtistsCommand);
				process = processBuilder.start();
				processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
				output = processOutput.readLine();
				for(int i = 0; output != null; i++) {
					
					data[i].setArtist(output);
					output = processOutput.readLine();
					
				}
				processOutput.close();
				process.waitFor();
				BottomPanel.setGUIProgressBar(BottomPanel.YOUTUBE_PROGRESS_MIDWAY_POINT / 5 * 4);
				
				String[] getPlaylistAlbumsCommand = {
						"yt-dlp",
						"--skip-download",
						"--no-warning",
						"--print",
						"album",
						"--output-na-placeholder",
						YOUTUBE_UNAVAILABLE_FIELD_PLACEHOLDER,
						"\"" + url + "\""
				};
				processBuilder = new ProcessBuilder(getPlaylistAlbumsCommand);
				process = processBuilder.start();
				processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
				output = processOutput.readLine();
				for(int i = 0; output != null; i++) {
					
					data[i].setAlbum(output);
					output = processOutput.readLine();
					
				}
				processOutput.close();
				process.waitFor();
				
				for(int i = 0; i < data.length; i++) {
					
					if(data[i].getAlbum().equals(YOUTUBE_UNAVAILABLE_FIELD_PLACEHOLDER) || data[i].getArtist().equals(YOUTUBE_UNAVAILABLE_FIELD_PLACEHOLDER)) {
						
						continue;
						
					}
					
					data[i].setCoverImageURL(getAlbumCoverURL(data[i].getAlbum(), data[i].getArtist()));
					
				}
				
				return data;
				
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
			
		} else {
			
			String title = args[0];
			String artist = args[1];
			String album = args[2];
			StaccatoTrack data = new StaccatoTrack(title, artist, album, MusicFetcher.extractYouTubeIDFromURL(url), null);
			if(!album.isBlank() && !artist.isBlank()) {
				
				data.setCoverImageURL(MusicFetcher.getAlbumCoverURL(album, artist));
				
			}
			
			return new StaccatoTrack[] {data};
			
		}
		
	}
	
	private static String getAlbumCoverURL(String albumTitle, String artists) {
		
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
	
	public static String getSpotifyPlaylistName(String url) {
		
		SpotifyApi spotifyApi = getSpotifyAPI();
		if(spotifyApi == null) {
			
			return null;
			
		}
		
		GetPlaylistRequest request = spotifyApi.getPlaylist(extractSpotifyIDFromURL(url)).market(CountryCode.US).build();
		Playlist playlist = getDataWithSpotifyAPIRequest(request, "getSpotifyPlaylistName");
		
		if(playlist == null) {
			
			BottomPanel.setGUIErrorStatus("Playlist not found");
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
				"--skip-download",
				"--print",
				"%(id)s\n%(title)s\n%(channel)s\n%(description)s\n" + YTDLP_SEARCH_INFO_SEPARATOR,
				"\"ytsearch" + numResults + ":" + title + " " + artist + "\""
		};
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		
		String[][] properties = new String[numResults][4]; //Each property is in the order of ID, title, channel, description
		try {
			
			Process searchProcess = processBuilder.start();
			BufferedReader processOutput = new BufferedReader(new InputStreamReader(searchProcess.getInputStream()));
			String outputStr = processOutput.readLine();
			int propertyCount = 0;
			while(outputStr != null) {
				
				if(propertyCount % 4 != 3) {
					
					//This is the normal case, when the property is the ID, title, or channel
					
					//output[search result index][property index]
					properties[propertyCount / 4][propertyCount % 4] = outputStr;
					propertyCount++;
					
					outputStr = processOutput.readLine();
					
					continue;
					
				}
				
				//When the property is a description, it may have multiple lines
				while(!outputStr.equals(YTDLP_SEARCH_INFO_SEPARATOR)) {
					
					if(properties[propertyCount / 4][3] == null) {
						
						properties[propertyCount / 4][3] = outputStr;
						
					} else {
						
						properties[propertyCount / 4][3] += "\n" + outputStr;
						
					}
					
					outputStr = processOutput.readLine();
					
				}
				
				propertyCount++;
				outputStr = processOutput.readLine();
				
			}
			
			processOutput.close();
			searchProcess.waitFor();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			String message = e.getMessage();
			
			if(message.toLowerCase().contains("cannot run program \"yt-dlp\"")) {
				
				BottomPanel.setGUIErrorStatus("Cannot run yt-dlp");
				return null;
				
			}
			
			BottomPanel.setGUIErrorStatus("IO Exception (searchYouTube): " + message);
			return null;
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Search was interrupted");
			return null;
			
		}
		
		int currPoints;
		int maxPoints = Integer.MIN_VALUE;
		int maxPointsIndex = -1;
		for(int i = 0; i < properties.length; i++) {
			
			currPoints = calculateVideoScore(properties[i], i, title, artist);
			
			if(currPoints > maxPoints) {
				
				maxPoints = currPoints;
				maxPointsIndex = i;
				
			}
			
		}
		
		return properties[maxPointsIndex][0];
		
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
		
		try {
			
			Process process = processBuilder.start();
			BufferedReader processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String output = null;
			while(output == null) {
				
				//We're getting the first non-null output
				output = processOutput.readLine();
				
			}
			
			processOutput.close();
			process.waitFor();
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
	
	private static int calculateVideoScore(String[] videoData, int videoIndex, String targetTitle, String targetArtist) {
		
		targetTitle = targetTitle.toLowerCase();
		targetArtist = targetArtist.toLowerCase();
		
		int score = 0;
		String videoTitle = videoData[1].toLowerCase();
		String channelName = videoData[2].toLowerCase();
		String description = videoData[3].toLowerCase();
		
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
			BottomPanel.setGUIErrorStatus(e.getClass().getSimpleName() + "(" + methodName + "): " + e.getMessage());
			
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
	
	private static String extractSpotifyIDFromURL(String url) {
		
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
	
	private static String extractYouTubeIDFromURL(String url) {
		
		//https://youtu.be/tfSS1e3kYeo?si=v4rcNgiHwUIQV5J6
		//https://www.youtube.com/watch?v=tfSS1e3kYeo
		//https://youtu.be/tfSS1e3kYeo?si=v4rcNgiHwUIQV5J6&t=2
		//https://www.youtube.com/watch?v=0Tdpq3FRGhY&list=PLmfSdJj_ZUFD_YvXNxd89Mq5pysTjpMSF
		
		int idBeginIndex;
		if(url.contains("youtu.be/")) {
			
			idBeginIndex = url.indexOf("youtu.be/") + "youtu.be/".length();
			
		} else if(url.contains("youtube.com/watch?v=")) {
			
			idBeginIndex = url.indexOf("youtube.com/watch?v=") + "youtube.com/watch?v=".length();
			
		} else {
			
			return null;
			
		}
		
		int questionMarkIndex = url.indexOf('?', idBeginIndex);
		int amperstandIndex = url.indexOf('&', idBeginIndex);
		if(questionMarkIndex == -1 && amperstandIndex == -1) {
			
			return url.substring(idBeginIndex);
			
		} else if(questionMarkIndex == -1) {
			
			return url.substring(idBeginIndex, amperstandIndex);
			
		} else if(amperstandIndex == -1) {
			
			return url.substring(idBeginIndex, questionMarkIndex);
			
		} else {
			
			return url.substring(idBeginIndex, Math.min(questionMarkIndex, amperstandIndex));
			
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
