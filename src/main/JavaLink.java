package main;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import py4j.ClientServer;

public class JavaLink {

    /**
     * Python methods
     */
    public interface IPythonLink {

        public List<Map<String, String>> send_tracks_to_java(String spotify_id, boolean is_playlist);

    }

    public static Set<Track> getTracks(String spotifyID, boolean isPlaylist) throws SpotipyException {

        ClientServer clientServer = new ClientServer(null);
        IPythonLink pythonLink = (IPythonLink) clientServer.getPythonServerEntryPoint(new Class[] {IPythonLink.class});
        System.out.println("1.1");
        List<Map<String, String>> pythonData = pythonLink.send_tracks_to_java(spotifyID, isPlaylist);
        System.out.println("1.2");
        clientServer.shutdown();
        System.out.println("1.3");

        //Spotipy exception handling
        if(pythonData.size() == 1 && pythonData.get(0).containsKey("httpStatus")) {

            System.out.println("1.3.1");
            throw new SpotipyException(Integer.parseInt(pythonData.get(0).get("httpStatus")), pythonData.get(0).get("msg"));

        }

        //Return tracks
        Set<Track> tracks = new HashSet<Track>();
        for(int i = 0; i < pythonData.size(); i++) {

            System.out.println("1.4." + i);

            tracks.add(new Track(
                pythonData.get(i).get("title"),
                pythonData.get(i).get("artists"),
                pythonData.get(i).get("album"),
                pythonData.get(i).get("artworkURL"),
                pythonData.get(i).get("youtubeID")
            ));

        }

        System.out.println("1.5");
        return tracks;

    }

    public static void main(String[] args) {

        System.out.println("1");
        Set<Track> tracks = null;

        try {

            tracks = getTracks("tps://open.spotify.com/playlist/1MBIdnT23Xujh3iHDAURfB?si=cb2f14163fde4403", false);

        } catch(SpotipyException e) {

            e.printStackTrace();

        }

        // tracks = getTracks("https://open.spotify.com/playlist/1MBIdnT23Xujh3iHDAURfB?si=cb2f14163fde4403", false);
        // tracks = getTracks("https://open.spotify.com/playlist/1MBIdnT23Xujh3iHDAURfB?si=cb2f14163fde4403", true);
        try {

            tracks = getTracks("https://open.spotify.com/track/0rx7xu0RmZLpJjKNVZjSVv?si=f4edb873a32e4629", false);

        } catch (SpotipyException e) {

            e.printStackTrace();

        }

        if(tracks == null) {

            return;

        }

        int i = 0;
        for(Track track: tracks) {

            System.out.println("2." + i);
            i++;

            System.out.println(track);
            
        }

    }

}
