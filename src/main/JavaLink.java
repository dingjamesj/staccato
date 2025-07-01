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

        public List<Map<String, String>> send_spotify_tracks_to_java(String spotify_id, boolean is_playlist);
        public String find_best_match_youtube_url(String title, String artists);
        public int download_raw_music_file(String youtube_url, String location);

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
        List<Map<String, String>> pythonData = pythonLink.send_spotify_tracks_to_java(spotifyID, isPlaylist);
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
            track.writeFileMetadata();

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
        String youtubeURL = pythonLink.find_best_match_youtube_url(title, artists);
        clientServer.shutdown();
        return youtubeURL;

    }

    /**
     * Downloads an audio file from the specified YouTube URL to the specified location. <br></br>
     * Does not contain any metadata.
     * @param youtubeURL
     * @param location
     * @return 0 if download was successful, 1 otherwise
     */
    public static int downloadRawTrackFile(String youtubeURL, String location) {

        ClientServer clientServer = new ClientServer(null);
        IPythonLink pythonLink = (IPythonLink) clientServer.getPythonServerEntryPoint(new Class[] {IPythonLink.class});
        int downloadResult = pythonLink.download_raw_music_file(youtubeURL, location);
        clientServer.shutdown();

        return downloadResult;

    }

    public static void main(String[] args) {

        Set<Track> tracks = null;

        try {

            tracks = getSpotifyTracks("https://open.spotify.com/playlist/1MBIdnT23Xujh3iHDAURfB?si=cda098d366544530", true);

        } catch(SpotipyException e) {

            e.printStackTrace();

        }

        for(Track track: tracks) {

            System.out.println(track);
            
        }

        // tracks = getTracks("https://open.spotify.com/playlist/1MBIdnT23Xujh3iHDAURfB?si=cb2f14163fde4403", false);
        // tracks = getTracks("https://open.spotify.com/playlist/1MBIdnT23Xujh3iHDAURfB?si=cb2f14163fde4403", true);
        try {

            tracks = getSpotifyTracks("https://open.spotify.com/track/0rx7xu0RmZLpJjKNVZjSVv?si=f4edb873a32e4629", false);

        } catch (SpotipyException e) {

            e.printStackTrace();

        }

        if(tracks == null) {

            return;

        }

        for(Track track: tracks) {

            System.out.println(track);
            
        }

        try {

            tracks = getSpotifyTracks("https://open.spotify.com/playlist/3oMkpen2toJFAvPDPml7HC?si=11f6570295a84372", true);

        } catch(SpotipyException e) {

            e.printStackTrace();

        }

        for(Track track: tracks) {

            System.out.println(track);
            
        }

    }

}
