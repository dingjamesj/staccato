package main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

public class Track implements Serializable {

	//Remember that staccato only...
	//  1. Tracks where audio files are sourced from and where they are located.
	//  2. Reads the audio files.
	//In other words, we only store the file location and the YouTube ID.
	private String fileLocation;
	private String youtubeID;

	//Properties shown on the playlist and current track viewer
	private transient String title = null;
	private transient String artists = null;
	private transient String album = null;
	private transient int duration = -1;
	private transient String artworkURL;
	
	public Track(String title, String artists, String album, String artworkURL, String youtubeID) {
		
		this.title = title;
		this.artists = artists;
		this.album = album;
		this.artworkURL = artworkURL;
		this.youtubeID = youtubeID;
		this.fileLocation = "";
		
	}

	public Track(String fileLocation) throws FileNotFoundException {

		this.fileLocation = fileLocation;
		if(!new File(fileLocation).exists()) {

			throw new FileNotFoundException();
			//Execution stops here

		}

		setAttributesFromFileMetadata();

	}
	
	public boolean fileExists() {
		
		return fileLocation == null || new File(fileLocation).exists();
		
	}
	
	public static int countRepeatedFileNames(String dirStr, String fileName) {
		
		File dir = new File(dirStr);
		if(!dir.exists()) {
			
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
	
	public String getArtists() {
		
		return artists;
		
	}
	
	public String getAlbum() {
		
		return album;
		
	}
	
	public String getYouTubeID() {
		
		return youtubeID;
		
	}
	
	public String getArtworkURL() {
		
		return artworkURL;
		
	}
	
	public int getDuration() {

		return duration;

	}

	public String getFileLocation() {
		
		return fileLocation;
		
	}
	
	public void setTitle(String title) {
		
		this.title = title;
		
	}
	
	public void setArtists(String artist) {
		
		this.artists = artist;
		
	}
	
	public void setAlbum(String album) {
		
		this.album = album;
		
	}
	
	public void setYouTubeID(String youtubeID) {
		
		this.youtubeID = youtubeID;
		
	}

	public void setArtworkURL(String artworkURL) {
		
		this.artworkURL = artworkURL;
		
	}

	/**
	 * Reads the track file's metadata and stores it in this track object. 
	 */
	public void setAttributesFromFileMetadata() {

		if(!fileExists()) {

			return;

		}

		AudioFile audioFile;
		try {

			audioFile = AudioFileIO.read(new File(fileLocation));

		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {

			e.printStackTrace();
			return;

		}

		Tag tag = audioFile.getTag();
		AudioHeader header = audioFile.getAudioHeader();
		title = tag.getFirst(FieldKey.TITLE);
		artists = tag.getFirst(FieldKey.ARTIST);
		album = tag.getFirst(FieldKey.ALBUM);
		duration = header.getTrackLength();

	}

	/**
	 * Writes this track object's attributes onto the track file
	 */
	public void writeMetadata() {
		
		if(!fileExists()) {
			
			return;
			
		}
		
		try {
			
			AudioFile audioFile = AudioFileIO.read(new File(fileLocation));
			Tag tag = audioFile.getTag();
			
			Artwork coverImage = null;
			if(artworkURL != null) {
				
				URL url = new URI(artworkURL).toURL();
				ByteArrayOutputStream coverImageURLByteArrayStream = new ByteArrayOutputStream();
				url.openStream().transferTo(coverImageURLByteArrayStream);
				coverImageURLByteArrayStream.toByteArray();
				coverImage = ArtworkFactory.getNew();
				coverImage.setBinaryData(coverImageURLByteArrayStream.toByteArray());
				coverImageURLByteArrayStream.close();
				
			}
			
			tag.setField(FieldKey.TITLE, title);
			tag.setField(FieldKey.ARTIST, artists);
			tag.setField(FieldKey.ALBUM, album);
			if(coverImage != null) {
				
				tag.setField(coverImage);
				
			}
			
			audioFile.commit();

		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException | URISyntaxException | CannotWriteException e) {

			e.printStackTrace();
			
		}
		
	}

	@Override
	public String toString() {
		
		return "\"" + title + "\" by " + artists + " @ " + youtubeID;
		
	}

	@Override
	public boolean equals(Object obj) {

		if(obj == null) {

			return false;

		}

		if(getClass() != obj.getClass()) {

			return false;

		}

		return fileLocation.equalsIgnoreCase(((Track) obj).getFileLocation());

	}

	/**
	 * Downloads an mp3 given a YouTube URL.
	 * 
	 * @param url YouTube video URL
	 * @param dir Directory to put the mp3
	 * @return The directory of where the file is located
	 */
	@Deprecated
	public int download(String dirStr) {
		
		//Make the file name
		String fileName = (title + " " + artists + " " + youtubeID).replace("\\", "").replace("/", "").replace(":", "").replace("*", "").replace("?", "").replace("\"", "")
				.replace("<", "").replace(">", "").replace("|", "");
		
		//Check if the specified directory exists
		File dir = new File(dirStr);
		if(!dir.exists()) {
			
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
				
				e.printStackTrace();
				
			}
						
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			
		}
		
		return -1;
		
	}
	
}
