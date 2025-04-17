from py4j.clientserver import ClientServer, JavaParameters, PythonParameters

from spotipy.exceptions import SpotifyException

import fetcher

class PythonLink(object):
    
    def send_tracks_to_java(spotify_id: str, is_playlist: bool) -> list[dict[str, str]]:
        
        spotify_tracks: list[dict]
        try: 
            if is_playlist:
                spotify_tracks = fetcher.get_spotify_playlist_tracks(spotify_id)
            else: 
                spotify_tracks = fetcher.get_spotify_track(spotify_id)
        except SpotifyException as e:
            return [{
                "http_status": f"{e.http_status}",
                "msg": f"{e.msg}"
            }]
        
        send_to_java_tracks: list[dict] = []
        artists: str = None
        for track in spotify_tracks:
            artists = ""
            for artist_data in track["track"]["artists"]:
                artists = artists + artist_data["name"] + ", "
            artists = artists[:-2]
            send_to_java_tracks.append({
                "title": track["track"]["name"],
                "artists": artists,
                "album": track["track"]["album"]["name"],
                "artworkURL": track["track"]["album"]["images"][0]["url"],
                "youtubeID": fetcher.search_youtube(track["track"]["name"], track["track"]["artists"][0]["name"])
            })
        return send_to_java_tracks
    
    class Java:
        implements = ["main.JavaLink.IPythonLink"]

if __name__ == "__main__":
    entry_point = PythonLink()
    gateway = ClientServer(
        java_parameters=JavaParameters(auto_convert=True),
        python_parameters=PythonParameters(),
        python_server_entry_point=entry_point
    )