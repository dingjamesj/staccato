package main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import gui.BottomPanel;

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
		
		if(!fileExists()) {
			
			BottomPanel.setGUIErrorStatus("File does not exist at \"" + fileLocation + "\" (writeID3Tags, THIS SHOULD NOT HAPPEN)");
			return;
			
		}
		
		try {
			
			AudioFile audioFile = AudioFileIO.read(new File(fileLocation));
			Tag tag = audioFile.getTag();
			
			Artwork coverImage = null;
			if(coverImageURL != null) {
				
				try {
					
					URL url = new URI(coverImageURL).toURL();
					ByteArrayOutputStream coverImageURLByteArrayStream = new ByteArrayOutputStream();
					url.openStream().transferTo(coverImageURLByteArrayStream);
					coverImageURLByteArrayStream.toByteArray();
					coverImage = ArtworkFactory.getNew();
					coverImage.setBinaryData(coverImageURLByteArrayStream.toByteArray());
					coverImageURLByteArrayStream.close();
					
				} catch (Exception e) {
					
					e.printStackTrace();
					BottomPanel.setGUIErrorStatus(e.getClass().getSimpleName() + " (writeID3Tags): " + e.getMessage());
					
				}
				
			}
			
			tag.setField(FieldKey.TITLE, title);
			tag.setField(FieldKey.ARTIST, artist);
			tag.setField(FieldKey.ALBUM, album);
			if(coverImage != null) {
				
				tag.setField(coverImage);
				
			}
			
			audioFile.commit();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus(e.getClass().getSimpleName() + " (writeID3Tags): " + e.getMessage());
			
		}
		
	}
	
	public boolean fileExists() {
		
		return fileLocation == null || new File(fileLocation).exists();
		
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
	
	public String getTitle() {
		
		return title;
		
	}
	
	public String getArtist() {
		
		return artist;
		
	}
	
	public String getAlbum() {
		
		return album;
		
	}
	
	public String getYouTubeID() {
		
		return youtubeID;
		
	}
	
	public String getCoverImageURL() {
		
		return coverImageURL;
		
	}
	
	public String getFileLocation() {
		
		return fileLocation;
		
	}
	
	public void setTitle(String title) {
		
		this.title = title;
		
	}
	
	public void setArtist(String artist) {
		
		this.artist = artist;
		
	}
	
	public void setAlbum(String album) {
		
		this.album = album;
		
	}
	
	public void setYouTubeID(String youtubeID) {
		
		this.youtubeID = youtubeID;
		
	}
	
	public void setCoverImageURL(String coverImageURL) {
		
		this.coverImageURL = coverImageURL;
		
	}
	
	@Override
	public String toString() {
		
		return "\"" + title + "\" by " + artist + " @ " + youtubeID;
		
	}
	
	public static void main(String[] args) {
		
		BottomPanel.main(args);
		
	}
	
}
