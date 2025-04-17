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

    public static Set<Track> getTracks(String spotifyID, boolean isPlaylist) {

        ClientServer clientServer = new ClientServer(null);
        IPythonLink pythonLink = (IPythonLink) clientServer.getPythonServerEntryPoint(new Class[] {IPythonLink.class});
        List<Map<String, String>> pythonData = pythonLink.send_tracks_to_java(spotifyID, isPlaylist);
        clientServer.shutdown();

        Set<Track> tracks = new HashSet<Track>();
        for(int i = 0; i < pythonData.size(); i++) {

            tracks.add(new Track(
                pythonData.get(i).get("title"),
                pythonData.get(i).get("artists"),
                pythonData.get(i).get("album"),
                pythonData.get(i).get("artworkURL"),
                pythonData.get(i).get("youtubeID")
            ));

        }

        return tracks;

    }

}
