# Fetches Spotify and YouTube information

from spotipy import Spotify
from spotipy.oauth2 import SpotifyClientCredentials

from yt_dlp import YoutubeDL

# Note: you need to prefix the file location with "../" when running python from C++
# SETTINGS_FILE_LOCATION: str = "staccatoapikeys.txt"
SETTINGS_FILE_LOCATION: str = "../staccatoapikeys.txt"
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


def get_spotify_playlist(spotify_id: str) -> list[dict]:
    """ID includes URL, URI, and alphanumeric ID"""
    try:
        read_api_settings()
        sp = Spotify(auth_manager=SpotifyClientCredentials(client_id=api_keys[0], client_secret=api_keys[1]))
        playlist: list[dict] = []
        playlist_data: list[dict] = [{}]
        # Note that the Spotify API can only retrieve 100 tracks at a time.
        call_count: int = 0
        while len(playlist_data) > 0:
            playlist_data = sp.playlist_tracks(playlist_id=spotify_id, market=market, offset=(call_count * 100))["items"]
            for track_data in playlist_data:
                artists: list = []
                for artist_data in track_data["track"]["artists"]:
                    artists.append(artist_data["name"])
                playlist.append({
                    "title": track_data["track"]["name"],
                    "artists": artists,
                    "album": track_data["track"]["album"]["name"],
                    "artwork_url": track_data["track"]["album"]["images"][0]["url"]
                })
            call_count = call_count + 1
        return playlist
    except:
        return []


def get_youtube_playlist(url: str) -> list[dict]:
    ydl_opts: dict = {
        "ignoreerrors": True,
        "quiet": True
    }
    with YoutubeDL(ydl_opts) as ydl:
        info: dict = ydl.extract_info(url, download=False)
        if info is None:
            return []
        if "entries" not in info:
            return []
        tracklist_raw: list = info["entries"]
        tracklist: list[dict] = []
        for track_raw in tracklist_raw:
            tracklist.append(get_refined_youtube_track_info(track_raw))
        return tracklist


def can_access_spotify_playlist(spotify_id: str) -> bool:
    read_api_settings()
    try:
        sp = Spotify(auth_manager=SpotifyClientCredentials(client_id=api_keys[0], client_secret=api_keys[1]))        
        sp.playlist_tracks(playlist_id=spotify_id, market=market)
        return True
    except:
        return False


def can_access_youtube_playlist(url: str) -> bool:
    ydl_opts: dict = {
        "ignoreerrors": True,
        "quiet": True
    }
    with YoutubeDL(ydl_opts) as ydl:
        info: dict = ydl.extract_info(url, download=False)
        return "entries" in info and info is not None


def get_spotify_track(spotify_id: str) -> dict:
    """ID includes URL, URI, and alphanumeric ID"""
    try:
        read_api_settings()
        sp = Spotify(auth_manager=SpotifyClientCredentials(client_id=api_keys[0], client_secret=api_keys[1]))
        track_data: dict = sp.track(track_id=spotify_id, market=market)
        artists: list = []
        for artist_data in track_data["artists"]:
            artists.append(artist_data["name"])
        return {
            "title": track_data["name"],
            "artists": artists,
            "album": track_data["album"]["name"],
            "artwork_url": track_data["album"]["images"][0]["url"]
        }
    except:
        pass
    # Maybe the previous attempt failed because it was a podcast episode?
    try:
        sp = Spotify(auth_manager=SpotifyClientCredentials(client_id=api_keys[0], client_secret=api_keys[1]))
        podcast_data: dict = sp.episode(episode_id=spotify_id, market=market)
        return {
            "title": podcast_data["name"],
            "artists": ["Unknown Artists"],
            "album": "Unknown Album",
            "artwork_url": podcast_data["images"][0]["url"]
        }
    except Exception as e:
        return {} # Guess it wasn't a valid track nor a valid podcast episode


def get_youtube_track(url: str) -> dict:
    try:
        ydl_opts: dict = {
            "ignoreerrors": True,
            "quiet": True
        }
        with YoutubeDL(ydl_opts) as ydl:
            info: dict = ydl.extract_info(url, download=False)
            if info is None:
                return {}
            return get_refined_youtube_track_info(info)
    except:
        return {}


def find_best_youtube_url(title: str, artists: list[str]) -> str:
    try: 
        artists_str: str = ""
        for artist in artists:
            artists_str = artists_str + artist + " "
        artists_str = artists_str[:-1]

        # Search for the top few videos---searching with "{Title} {1st artist}"
        # e.g. "née-nah 21 Savage"
        ydl_opts: dict = {
            "ignoreerrors": True,
            "quiet": True
        }
        with YoutubeDL(ydl_opts) as ydl:
            info: dict = ydl.extract_info(url=f"ytsearch{NUM_ACCEPTED_SEARCHES}:{title} {artists_str}", download=False)
        search_results: list[dict] = info["entries"]
        # Find the ID of the highest-scoring video
        index: int = 0
        largest_score: int = -9999999
        id_with_largest_score: str = ""
        for search_result in search_results:
            if search_result is None:
                index = index + 1
                continue
            score: int = calculate_video_score(search_result, index, title, artists_str)
            if score > largest_score:
                largest_score = score
                id_with_largest_score = search_result["id"]
        # Return
        return f"https://www.youtube.com/watch?v={id_with_largest_score}"
    except:
        return ""

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


def get_refined_youtube_track_info(raw_info: dict) -> dict:
    # Differentiate between a YouTube track and playlist
    if "entries" in raw_info:
        return {}

    track: dict = {}
    # Title
    if "track" in raw_info:
        track["title"] = raw_info["track"]
    elif "title" in raw_info:
        track["title"] = raw_info["title"]
    else:
        track["title"] = ""
    # Artists
    if "artists" in raw_info:
        track["artists"] = raw_info["artists"]
    elif "creator" in raw_info:
        track["artists"] = [raw_info["creator"]]
    elif "uploader" in raw_info:
        track["artists"] = [raw_info["uploader"]]
    else:
        track["artists"] = ""
    # Album
    if "album" in raw_info:
        track["album"] = raw_info["album"]
    else:
        track["album"] = ""
    return track



if __name__ == "__main__":
    read_api_settings()
    # print(get_youtube_track("https://www.youtube.com/watch?v=bu7nU9Mhpyo"))
    # print(get_youtube_playlist("https://www.youtube.com/playlist?list=PLmfSdJj_ZUFD_YvXNxd89Mq5pysTjpMSF"))
    # print(get_youtube_playlist("https://www.youtube.com/playlist?list=PLmfSdJj_ZUFD4_T3E6jPbd6Z8n1zv_cRY"))

    # Regular playlist (rargb)
    # get_spotify_playlist("https://open.spotify.com/playlist/3oMkpen2toJFAvPDPml7HC?si=5bf8e98da54b4424")
    # Playlist with single track (test3)
    # get_spotify_playlist("https://open.spotify.com/playlist/302qOeuyMFtdYFg5owNOiQ?si=d42fb6936e9148c9")
    # Podcast
    # print(get_spotify_track("https://open.spotify.com/episode/2wd4bRSwcewwFWDyQ9vlEa?si=26d3b6b43b07460a"))

    print(get_youtube_track("https://www.youtube.com/watch?v=VTmaf0jggF8"))