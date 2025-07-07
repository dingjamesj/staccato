package main;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import py4j.ClientServer;

public class JavaLink {

    /**
     * Methods from python_link.py
     */
    public interface IPythonLink {

        public List<Map<String, String>> get_spotify_tracks(String spotify_id, boolean is_playlist);
        public String find_best_youtube_url(String title, String artists);
        public String download_raw_track(String youtube_url, String location, boolean force_mp3);
        public int update_yt_dlp();

    }

    /**
     * Gets information (all tracks in a playlist or singular track) from Spotify
     * @param spotifyID ID of a Spotify playlist or track
     * @param isPlaylist If the ID is for a playlist or a track
     * @return A set of Track objects containing info from Spotify
     * @throws SpotipyException
     */
    public static Set<Track> getSpotifyTracks(String spotifyID, boolean isPlaylist) throws SpotipyException {

        ClientServer clientServer = new ClientServer(null);
        IPythonLink pythonLink = (IPythonLink) clientServer.getPythonServerEntryPoint(new Class[] {IPythonLink.class});
        List<Map<String, String>> pythonData = pythonLink.get_spotify_tracks(spotifyID, isPlaylist);
        clientServer.shutdown();

        //Spotipy exception handling
        if(pythonData.size() == 1 && pythonData.get(0).containsKey("httpStatus")) {

            throw new SpotipyException(Integer.parseInt(pythonData.get(0).get("httpStatus")), pythonData.get(0).get("msg"));

        }

        //Return tracks
        Set<Track> tracks = new HashSet<Track>();
        for(int i = 0; i < pythonData.size(); i++) {

            Track track = new Track(
                pythonData.get(i).get("title"),
                pythonData.get(i).get("artists"),
                pythonData.get(i).get("album"),
                pythonData.get(i).get("artworkURL")
            );
            tracks.add(track);

        }

        return tracks;

    }

    /**
     * Returns the URL of the YouTube video that matches best with the specified track title and artists.
     * @param title
     * @param artists
     * @return YouTube video URL
     */
    public static String findBestYouTubeURLMatch(String title, String artists) {

        ClientServer clientServer = new ClientServer(null);
        IPythonLink pythonLink = (IPythonLink) clientServer.getPythonServerEntryPoint(new Class[] {IPythonLink.class});
        String youtubeURL = pythonLink.find_best_youtube_url(title, artists);
        clientServer.shutdown();
        return youtubeURL;

    }

    /**
     * Downloads an audio file (usually m4a) from the specified YouTube URL to the specified location. <br></br>
     * Does not contain any metadata.
     * @param youtubeURL
     * @param location
     * @param forceMp3 If true, forces the downloaded track to be an mp3
     * @return The downloaded file's path
     */
    public static String downloadRawTrack(String youtubeURL, String location, boolean forceMp3) {

        ClientServer clientServer = new ClientServer(null);
        IPythonLink pythonLink = (IPythonLink) clientServer.getPythonServerEntryPoint(new Class[] {IPythonLink.class});
        String downloadPath = pythonLink.download_raw_track(youtubeURL, location, forceMp3);
        clientServer.shutdown();
        return downloadPath;

    }

    /**
     * Downloads an mp3 from the specified YouTube URL to the specified location. <br></br>
     * Does not contain any metadata.
     * @param youtubeURL
     * @param location
     * @return The downloaded file's path
     */
    public static String downloadRawTrack(String youtubeURL, String location) {

        return downloadRawTrack(youtubeURL, location, false);

    }

    /**
     * Through pip, updates yt-dlp and pip
     * @return
     */
    public static int updateYtdlp() {

        ClientServer clientServer = new ClientServer(null);
        IPythonLink pythonLink = (IPythonLink) clientServer.getPythonServerEntryPoint(new Class[] {IPythonLink.class});
        int updateResult = pythonLink.update_yt_dlp();
        clientServer.shutdown();
        return updateResult;

    }

    public static void main(String[] args) {

        try {
            Set<Track> tracks = getSpotifyTracks("https://open.spotify.com/playlist/1MBIdnT23Xujh3iHDAURfB?si=a5c972a9d8db46f0", true);
            for(Track track: tracks) {

                System.out.println(track);

            }
            for(Track track: tracks) {

                track.download("D:\\");

            }
        } catch (SpotipyException e) {
            System.out.println("spotipy exception");
        }

    }

}
