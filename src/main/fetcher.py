# Fetches Spotify information

import spotipy
from spotipy.oauth2 import SpotifyClientCredentials

SETTINGS_FILE_LOCATION = ""

keys: list[str] = None
market: str = None

def update_settings():
    try:
        file = open(SETTINGS_FILE_LOCATION, "r")
        keys.clear()
        keys.append(file.readline())
        keys.append(file.readline())
        market = file.readline()
        file.close()
    except IOError as e:
        print(e)

def set_spotify_api_keys(client_id: str, client_secret: str):
    try:
        file = open(SETTINGS_FILE_LOCATION, "w")
        file.writelines(f"{client_id}\n", f"{client_secret}\n", f"{market}")
        file.close()
    except IOError as e:
        print(e)

def get_spotify_playlist_tracks(id: str) -> list[dict]:
    sp = spotipy.Spotify(auth_manager=SpotifyClientCredentials(client_id=keys[0], client_secret=keys[1]))
    return sp.playlist_tracks(playlist_id=id, market=market)["items"]

def search_youtube(title: str, artists: str) -> str:
    pass

def send_tracks_to_java(id: str):
    tracks_java_data: list[dict] = [] # Data we will send over to our Java program
    tracks: list[dict] = get_spotify_playlist_tracks(id)
    artists: str = None
    for track in tracks:
        artists = ""
        for artist_data in track["track"]["artists"]:
            artists = artists + artist_data["name"] + ", "
        artists = artists[:-2]
        tracks_java_data.append({
            "title": track["track"]["name"],
            "artists": artists,
            "album": track["track"]["album"]["name"],
            "artworkURL": track["track"]["album"]["images"][0]["url"],
            "youtubeID": search_youtube(track["track"]["name"], artists.replace(",", ""))
        })
    # TODO: Send tracks_java_data over to the Java program
        
    

if __name__ == "__main__":
    # keys = ["1c41bf31426e46a6a4c44d3f9bac1424", "bb9d381f22204bdabbf0418767ca3aaf"]
    # market = "US"
    # tracks = None
    # tracks = get_spotify_playlist_tracks("https://open.spotify.com/playlist/1zUUwGZh02drh2M13yNcVD?si=fe3a24a9e483426b")
    # print(tracks)
    # print("\n")
    # print(type(tracks))
    # print(type(tracks[0]))
    pass
