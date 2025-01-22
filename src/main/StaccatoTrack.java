package main;

public class StaccatoTrack {

	private String title;
	private String artist;
	private String album;
	private String youtubeURL;
	private String albumCoverImageURL;
	private String fileLocation;
	
	public StaccatoTrack(String title, String artist, String album, String youtubeURL, String albumCoverImageURL) {
		
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.youtubeURL = youtubeURL;
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
	
	public String getYouTubeURL() {
		
		return youtubeURL;
		
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
