# Fetches Spotify and YouTube information

import spotipy
from spotipy.oauth2 import SpotifyClientCredentials

from yt_dlp import YoutubeDL

SETTINGS_FILE_LOCATION: str = "staccatoapikeys.txt"
NUM_ACCEPTED_SEARCHES: int = 3

api_keys: list[str] = []
market: str = ""

# In the order of client id, client secret, and market
def read_api_settings():
    global market
    try:
        with open(SETTINGS_FILE_LOCATION, "r") as file:
            api_keys.clear()
            api_keys.append(file.readline().strip())
            api_keys.append(file.readline().strip())
            market = file.readline().strip()
    except IOError as e:
        print(e)
    if market == "":
        market = "US"


def change_api_settings(client_id: str, client_secret: str, market: str):
    try:
        with open(SETTINGS_FILE_LOCATION, "w") as file:
            file.writelines(f"{client_id}\n{client_secret}\n{market}")
    except IOError as e:
        print(e)


def get_spotify_playlist_tracks(spotify_id: str) -> list[dict]:
    """ID includes URL, URI, and alphanumeric ID"""
    try:
        sp = spotipy.Spotify(auth_manager=SpotifyClientCredentials(client_id=api_keys[0], client_secret=api_keys[1]))
        playlist_data: list[dict] = sp.playlist_tracks(playlist_id=spotify_id, market=market)["items"]
        playlist: list[dict] = []
        for track_data in playlist_data:
            artists = ""
            for artist_data in track_data["track"]["artists"]:
                artists = artists + artist_data["name"] + ", "
            artists = artists[:-2]
            playlist.append({
                "title": track_data["track"]["name"],
                "artists": artists,
                "album": track_data["track"]["album"]["name"],
                "artwork_url": track_data["track"]["album"]["images"][0]["url"]
            })
        return playlist
    except:
        return []


def get_spotify_track(spotify_id: str) -> dict:
    """ID includes URL, URI, and alphanumeric ID"""
    try:
        sp = spotipy.Spotify(auth_manager=SpotifyClientCredentials(client_id=api_keys[0], client_secret=api_keys[1]))
        track_data: dict = sp.track(track_id=spotify_id, market=market)
        artists: str = ""
        for artist_data in track_data["artists"]:
            artists = artists + artist_data["name"] + ", "
        artists = artists[:-2]
        return {
            "title": track_data["name"],
            "artists": artists,
            "album": track_data["album"]["name"],
            "artwork_url": track_data["album"]["images"][0]["url"]
        }
    except:
        return {}


def get_youtube_track(url: str) -> dict:
    ydl_opts: dict = {
        "ignoreerrors": True,
        "quiet": True
    }
    with YoutubeDL(ydl_opts) as ydl:
        info: dict = ydl.extract_info(url, download=False)
        if info is None:
            return {}
        track: dict = {}
        # Title
        if "track" in info:
            track["title"] = info["track"]
        else:
            track["title"] = info["title"]
        # Artists
        if "creator" in info:
            track["artists"] = info["creator"]
        elif "artists" in info:
            track["artists"] = info["artists"]
        else:
            track["artists"] = info["uploader"]
        # Album
        if "album" in info:
            track["album"] = info["album"]
        else:
            track["album"] = ""
        return track


def find_best_youtube_url(title: str, artists: str) -> str:
    # Search for the top few videos---searching with "{Title} {1st artist}"
    # e.g. "née-nah 21 Savage"
    ydl_opts: dict = {
        "ignoreerrors": True,
        "quiet": True
    }
    with YoutubeDL(ydl_opts) as ydl:
        info: dict = ydl.extract_info(url=f"ytsearch{NUM_ACCEPTED_SEARCHES}:{title} {artists}", download=False)
    search_results: list[dict] = info["entries"]
    # Find the ID of the highest-scoring video
    index: int = 0
    largest_score: int = -9999999
    id_with_largest_score: str = ""
    for search_result in search_results:
        if search_result is None:
            index = index + 1
            continue
        score: int = calculate_video_score(search_result, index, title, artists)
        if score > largest_score:
            largest_score = score
            id_with_largest_score = search_result["id"]
    # Return
    return f"https://www.youtube.com/watch?v={id_with_largest_score}"

# How well a video's info matches up with the target track information.
def calculate_video_score(search_result: dict, index: int, target_title: str, target_artists: str) -> int:
    video_title: str = search_result["title"].lower()
    video_channel: str = search_result["channel"].lower()
    video_desc: str = search_result["description"].lower()
    target_title = target_title.lower()
    target_artists = target_artists.lower()
    score: int = 0

    if target_title not in video_title:
        score = score - 2
    if target_artists in video_title:
        score = score + 1
    if "hour" in video_title or "loop" in video_title or "edit" in video_title or "live" in video_title or "reverb" in video_title or "sped-up" in video_title or "sped up" in video_title or "slowed" in video_title or "remix" in video_title or "cover" in video_title or "mtv" in video_title:
        score = score - 3
    if "audio" in video_title or "lyrics" in video_title:
        score = score + 3
    if index == 0:
        score = score + 1
    if target_artists in video_channel:
        score = score + 1
    if "auto-generated by youtube" in video_desc:
        score = score + 2
    return score


if __name__ == "__main__":
    read_api_settings()
    print(get_youtube_track("https://www.youtube.com/watch?v=bu7nU9Mhpyo"))