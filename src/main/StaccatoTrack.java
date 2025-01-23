package main;

public class StaccatoTrack {

	private String title;
	private String artist;
	private String album;
	private String youtubeID;
	private String albumCoverImageURL;
	private String fileLocation;
	
	public StaccatoTrack(String title, String artist, String album, String youtubeID, String albumCoverImageURL) {
		
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.youtubeID = youtubeID;
		this.albumCoverImageURL = albumCoverImageURL;
		
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
	
	public String getAlbumCoverImageURL() {
		
		return albumCoverImageURL;
		
	}
	
	public String getFileLocation() {
		
		return fileLocation;
		
	}
	
	public void setFileLocation(String fileLocation) {
		
		this.fileLocation = fileLocation;
		
	}
	
}
