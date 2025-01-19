package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public abstract class MusicFetcher {
	
	public static String convertSpotifyToYouTubeURL(String url) {
		
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
			
			BottomPanel.setGUIErrorStatus("IO Error: " + message);
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
				BottomPanel.setGUIErrorStatus("JSON IO Error: " + e.getMessage());
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
		
		return 0;
		
	}
	
	public static void main(String[] args) {
		
		System.out.println(System.getProperty("user.dir"));
		
	}
	
}
