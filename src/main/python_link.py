from py4j.clientserver import ClientServer, JavaParameters, PythonParameters
from py4j.java_collections import MapConverter, ListConverter

from spotipy.exceptions import SpotifyException

import fetcher

class PythonLink(object):

    def __init__(self):
        self._gateway: ClientServer = None
    
    def set_gateway(self, gateway):
        self._gateway = gateway
        pass
    
    # Method to be called from JavaLink.java
    def send_tracks_to_java(self, spotify_id: str, is_playlist: bool) -> list[dict[str, str]]:
        spotify_tracks: list[dict]
        try: 
            if is_playlist:
                spotify_tracks = fetcher.get_spotify_playlist_tracks(spotify_id)
            else: 
                spotify_tracks = [fetcher.get_spotify_track(spotify_id)]
        except SpotifyException as e:
            return ListConverter().convert([MapConverter().convert({
                "httpStatus": f"{e.http_status}",
                "msg": f"{e.msg}"
            }, self._gateway._gateway_client)], self._gateway._gateway_client)
        
        send_to_java_tracks: list[dict] = []
        artists: str = None
        if is_playlist:
            for track in spotify_tracks:
                artists = ""
                for artist_data in track["track"]["artists"]:
                    artists = artists + artist_data["name"] + ", "
                artists = artists[:-2]
                send_to_java_tracks.append(MapConverter().convert({
                    "title": track["track"]["name"],
                    "artists": artists,
                    "album": track["track"]["album"]["name"],
                    "artworkURL": track["track"]["album"]["images"][0]["url"],
                    "youtubeID": fetcher.search_youtube(track["track"]["name"], track["track"]["artists"][0]["name"])
                }, self._gateway._gateway_client))
        else:
            artists = ""
            for artist_data in spotify_tracks[0]["artists"]:
                artists = artists + artist_data["name"] + ", "
            artists = artists[:-2]
            send_to_java_tracks = [MapConverter().convert({
                "title": spotify_tracks[0]["name"],
                "artists": artists,
                "album": spotify_tracks[0]["album"]["name"],
                "artworkURL": spotify_tracks[0]["album"]["images"][0]["url"],
                "youtubeID": fetcher.search_youtube(spotify_tracks[0]["name"], spotify_tracks[0]["artists"][0]["name"])
            }, self._gateway._gateway_client)]

        return ListConverter().convert(send_to_java_tracks, self._gateway._gateway_client)
    
    class Java:
        implements = ["main.JavaLink.IPythonLink"]

if __name__ == "__main__":
    fetcher.read_api_settings()
    entry_point = PythonLink()
    gateway = ClientServer(
        java_parameters=JavaParameters(),
        python_parameters=PythonParameters(),
        python_server_entry_point=entry_point
    )
    entry_point.set_gateway(gateway)
    print("Opened gateway")
    # gateway.shutdown()
    # print("Shut down gateway")