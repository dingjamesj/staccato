package main;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.Random;

import javax.imageio.ImageIO;

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
	private transient byte[] artworkByteArray;
	private transient String artworkURL;
	
	/**
	 * For reading a track from python fetcher
	 * @param title
	 * @param artists
	 * @param album
	 * @param artworkURL
	 */
	public Track(String fileLocation, String title, String artists, String album, String artworkURL) {
		
		this.fileLocation = fileLocation;
		this.title = title;
		this.artists = artists;
		this.album = album;
		this.artworkURL = artworkURL;
		
	}

	/**
	 * For reading a track from a mp3 file
	 * @param fileLocation
	 * @throws FileNotFoundException
	 */
	public Track(String fileLocation) throws FileNotFoundException {

		this.fileLocation = fileLocation;
		if(!new File(fileLocation).exists()) {

			throw new FileNotFoundException();

		}

		setAttributesFromFileMetadata();

	}

	public Track(File file) throws FileNotFoundException {

		try{

			this.fileLocation = file.getCanonicalPath();

		} catch(IOException e) {

			try {

				this.fileLocation = file.getAbsolutePath();

			} catch(SecurityException e1) {

				this.fileLocation = file.getPath();

			}

		} catch(SecurityException e) {

			this.fileLocation = file.getPath();

		}

		if(!file.exists()) {

			throw new FileNotFoundException();

		}

		setAttributesFromFileMetadata();

	}
	
	/**
	 * Checks if the track file can be read
	 * @return True if can be read, false otherwise
	 */
	public boolean canRead() {
		
		if(fileLocation == null) {

			return false;
			
		}

		try {

			//(says if we can read the track file)
			//Note that the canRead() method throws SecurityException if we don't have perms to read it.
			if(!(new File(fileLocation).canRead())) {

				return false;

			}

		} catch(SecurityException e) {

			return false;

		}

		return true;
		
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
	
	public byte[] getArtworkByteArray() {
		
		return artworkByteArray;
		
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

	public void setArtworkByteArray(byte[] artworkByteArray) {

		this.artworkByteArray = artworkByteArray;

	}

	public void setArtworkURL(String artworkURL) {
		
		this.artworkURL = artworkURL;
		
	}

	/**
	 * Reads the track file's metadata and stores it in this track object. 
	 */
	private synchronized void setAttributesFromFileMetadata() {

		if(!canRead()) {

			return;

		}

		AudioFile audioFile;
		try {

			AudioFileIO.logger = null;
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
		if(tag.getFirstArtwork() != null) {

			artworkByteArray = tag.getFirstArtwork().getBinaryData();

		} else {

			artworkByteArray = null;

		}

	}

	/**
	 * Writes this track object's attributes onto the track file
	 */
	public synchronized void writeMetadata() {
		
		if(!canRead()) {
			
			return;
			
		}
		
		try {
			
			AudioFile audioFile = AudioFileIO.read(new File(fileLocation));
			Tag tag = audioFile.getTag();
			
			Artwork coverImage = null;
			if(artworkURL != null || artworkByteArray != null) {

				byte[] newArtworkByteArray;
				if(artworkURL != null) {

					newArtworkByteArray = FileManager.readByteArray(artworkURL);

				} else {

					newArtworkByteArray = artworkByteArray;

				}

				//Validate the byte array and get the MIME type (png, jpg, or jpeg)
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(newArtworkByteArray));
				if(bufferedImage == null) {

					throw new IOException("Image from URL " + artworkURL + " cannot be decoded.");

				}
				String mime = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(newArtworkByteArray));
				if(mime == null) {

					throw new IOException("Cannot determine image type from URL " + artworkURL);

				}

				//Write the artwork byte array to the mp3 file and to memory
				coverImage = ArtworkFactory.getNew();
				coverImage.setMimeType(mime);
				coverImage.setBinaryData(newArtworkByteArray);
				this.artworkByteArray = newArtworkByteArray;
				
			}
			
			tag.setField(FieldKey.TITLE, title);
			tag.setField(FieldKey.ARTIST, artists);
			tag.setField(FieldKey.ALBUM, album);
			if(coverImage != null) {
				
				tag.deleteArtworkField();
				tag.setField(coverImage);
				
			}
			
			audioFile.commit();

		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException | URISyntaxException | CannotWriteException e) {

			e.printStackTrace();
			
		}
		
	}

	@Override
	public int hashCode() {

		int hashCode = 0;
		if(fileLocation != null) {

			hashCode += fileLocation.hashCode();

		}
		if(title != null) {

			hashCode += title.hashCode();

		}
		if(artists != null) {

			hashCode += artists.hashCode();

		}
		if(album != null) {

			hashCode += album.hashCode();

		}

		return hashCode;

	}

	@Override
	public String toString() {
		
		String toString = "\"" + null + "\" [" + formatHoursMinutesSeconds(duration) + "] from \"" + album + "\" by " + artists;
		
		if(artworkURL != null && !artworkURL.isBlank()) {

			toString += " (artwork @ " + artworkURL + ")";

		}

		return toString;

	}

	@Override
	public boolean equals(Object obj) {

		if(obj == null) {

			return false;

		}

		if(getClass() != obj.getClass()) {

			return false;

		}

		if(System.getProperty("os.name").toLowerCase().contains("windows")) {

			return fileLocation.equalsIgnoreCase(((Track) obj).getFileLocation());

		} else {

			return fileLocation.equals(((Track) obj).getFileLocation());

		}

	}

	public static void shuffleTracklist(Track[] tracks, Track beginningTrack) {

		if(tracks == null) {

			return;

		}

		int beginningTrackIndex = -1;
		Random random = new Random();
        for(int i = tracks.length - 1; i > 0; i--) {

			int j = random.nextInt(i + 1);
			Track temp = tracks[j];
			tracks[j] = tracks[i];
			tracks[i] = temp;
			if(beginningTrackIndex == -1 && tracks[i].equals(beginningTrack)) {

				beginningTrackIndex = i;

			}

		}

		Track temp = tracks[beginningTrackIndex];
		tracks[beginningTrackIndex] = tracks[0];
		tracks[0] = temp;

    }

    public static void shuffleTracklist(Track[] tracks) {

		if(tracks == null) {

			return;

		}

		Random random = new Random();
        for(int i = tracks.length - 1; i > 0; i--) {

			int j = random.nextInt(i + 1);
			Track temp = tracks[j];
			tracks[j] = tracks[i];
			tracks[i] = temp;

		}

    }

	private static String formatHoursMinutesSeconds(int seconds) {

		if(seconds < 0) {

			return "--:--";

		}

        String minutesStr = String.format("%02d", (seconds % 3600) / 60);
        String secondsStr = String.format("%02d", (seconds % 3600) % 60);
        return minutesStr + ":" + secondsStr;

    }
	
}
