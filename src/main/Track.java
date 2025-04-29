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

public class Track {

	//Remember that staccato only...
	//  1. Tracks where audio files are sourced from and where they are located.
	//  2. Reads the audio files.
	//In other words, we only store the file location and the YouTube ID.

	//Properties shown on the playlist and current track viewer
	private transient String fileLocation;
	private transient String title = null;
	private transient String artists = null;
	private transient String album = null;
	private transient int duration = -1;
	private transient String artworkURL;
	
	public Track(String title, String artists, String album, String artworkURL) {
		
		this.title = title;
		this.artists = artists;
		this.album = album;
		this.artworkURL = artworkURL;
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
	public int hashCode() {

		return fileLocation.hashCode() + title.hashCode() + artists.hashCode() + album.hashCode() + artworkURL.hashCode();

	}

	@Override
	public String toString() {
		
		return "\"" + title + "\" by " + artists;
		
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
	
}
