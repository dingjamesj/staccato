from py4j.clientserver import ClientServer, JavaParameters, PythonParameters
from py4j.java_collections import MapConverter, ListConverter

from spotipy.exceptions import SpotifyException

import fetcher
import downloader

class PythonLink(object):
    """Functions to find in JavaLink.java"""
    def __init__(self):
        self._gateway: ClientServer = None
    
    def set_gateway(self, gateway):
        self._gateway = gateway
        pass
    
    def send_spotify_tracks_to_java(self, spotify_id: str, is_playlist: bool) -> list[dict[str, str]]:
        """If is_playlist is true, returns a list of dicts that contain info about each track in the Spotify playlist. 
        If is_playlist is false, returns a list with one dict that contains info about the Spotify track."""
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
            }, self._gateway._gateway_client)]

        return ListConverter().convert(send_to_java_tracks, self._gateway._gateway_client)
    
    # Returns the YouTube URL of the best matching YouTube video
    def find_best_youtube_url(self, title: str, artists: str) -> str:
        return fetcher.find_best_youtube_url(title, artists)

    # Returns the path to the downloaded file
    def download_raw_track(self, youtube_url: str, location: str) -> str:
        return downloader.download_youtube_track(youtube_url, location)["download_path"]
    
    def update_yt_dlp(self) -> int:
        return downloader.update_yt_dlp()

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