package main;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

public class StaccatoTrack {

	private String title;
	private String artist;
	private String album;
	private String youtubeID;
	private String coverImageURL;
	private String fileLocation;
	
	public StaccatoTrack(String title, String artist, String album, String youtubeID, String coverImageURL) {
		
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.youtubeID = youtubeID;
		this.coverImageURL = coverImageURL;
		this.fileLocation = null;
		
	}
	
	/**
	 * Downloads an mp3 given a YouTube URL.
	 * 
	 * @param url YouTube video URL
	 * @param dir Directory to put the mp3
	 * @return The directory of where the file is located
	 */
	public int download(String dirStr) {
		
		//Make the file name
		String fileName = (title + " " + artist + " " + youtubeID).replace("\\", "").replace("/", "").replace(":", "").replace("*", "").replace("?", "").replace("\"", "")
				.replace("<", "").replace(">", "").replace("|", "");
		
		//Check if the specified directory exists
		File dir = new File(dirStr);
		if(!dir.exists()) {
			
			BottomPanel.setGUIErrorStatus("Directory \"" + dirStr + "\" does not exist");
			return -1;
			
		}
		
		//This is the "(number)" at the end of a file (for example, "FileName (1).mp3")
		int uniqueNumber = countRepeatedFileNames(dirStr, fileName);
		
		//Write out the command
		String[] command;
		if(uniqueNumber > 0) {
			
			command = new String[] {
					"yt-dlp", 
					"--audio-format", "mp3", 
					"-o", "\"" + fileName + " (" + uniqueNumber + ").%(ext)s\"",
					"--extract-audio",
					"--no-playlist",
					"\"https://www.youtube.com/watch?v=" + youtubeID + "\""
					};
			
		} else {
			
			command = new String[] {
					"yt-dlp", 
					"--audio-format", "mp3", 
					"-o", "\"" + fileName + ".%(ext)s\"",
					"--extract-audio",
					"--no-playlist",
					"\"https://www.youtube.com/watch?v=" + youtubeID + "\""
					};
			
		}
		
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(dir);
		processBuilder.inheritIO();
		try {
			
			Process downloadProcess = processBuilder.start();
			downloadProcess.waitFor();
			if(uniqueNumber > 0) {
				
				fileLocation = dirStr + "\\" + fileName + " (" + uniqueNumber + ").mp3";
				
			} else {
				
				fileLocation = dirStr + "\\" + fileName + ".mp3";
				
			}
			
			return 0;
			
		} catch (IOException e) {
			
			e.printStackTrace();
			String message = e.getMessage();
			
			if(message.toLowerCase().contains("cannot run program \"yt-dlp\"")) {
				
				BottomPanel.setGUIErrorStatus("Cannot run yt-dlp");
				e.printStackTrace();
				
			}
			
			BottomPanel.setGUIErrorStatus("IO Error: " + message);
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("Download was interrupted");
			
		}
		
		return -1;
		
	}
	
	public void writeID3Tags() {
		
		try {
			
			AudioFile audioFile = AudioFileIO.read(new File(fileLocation));
			
		} catch (CannotReadException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		} catch (TagException e) {
			
			e.printStackTrace();
			
		} catch (ReadOnlyFileException e) {
			
			e.printStackTrace();
			
		} catch (InvalidAudioFrameException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public static int countRepeatedFileNames(String dirStr, String fileName) {
		
		File dir = new File(dirStr);
		if(!dir.exists()) {
			
			BottomPanel.setGUIErrorStatus("Directory " + dirStr + " does not exist (countRepeatedFileNames)");
			return -1;
			
		}
		
		int count = 0;
		int fileNameLength = fileName.length();
		String[] fileNameStrs = dir.list();
		for(String fileNameStr: fileNameStrs) {
			
			if(fileNameStr.length() < fileNameLength) {
				
				continue;
				
			}
			
			if(fileNameStr.substring(0, fileNameLength).equals(fileName)) {
				
				count++;
				
			}
			
		}
		
		return count;
		
	}
	
}
