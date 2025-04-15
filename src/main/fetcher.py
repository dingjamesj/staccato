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

def get_spotify_playlist_tracks(id: str):
    sp = spotipy.Spotify(auth_manager=SpotifyClientCredentials(client_id=keys[0], client_secret=keys[1]))
    result: dict = sp.playlist_tracks(playlist_id=id, market=market)
    if "error" in result:
        return result.get("error")
    # Use Py4J to communicate the result to the Java program