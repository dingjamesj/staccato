from yt_dlp import YoutubeDL

from subprocess import CompletedProcess
import subprocess
import sys

import os

def download_youtube_track(url: str, location: str, force_mp3: bool) -> dict:
    """Returns 0 if download was successful, 1 otherwise."""

    # Get the unique file enumerator e.g. the (1) in "duplicatemusicfile (1).mp3"
    possible_file_paths: list[str] = [
        f"{location}{os.sep}{extract_youtube_id_from_url(url)}.mp3",
        f"{location}{os.sep}{extract_youtube_id_from_url(url)}.m4a",
        f"{location}{os.sep}{extract_youtube_id_from_url(url)}.aac"
    ]
    unique_file_enumerator: int = 0
    while os.path.isfile(possible_file_paths[0]) or os.path.isfile(possible_file_paths[1]) or os.path.isfile(possible_file_paths[2]):
        unique_file_enumerator = unique_file_enumerator + 1
        possible_file_paths = [
            f"{location}{os.sep}{extract_youtube_id_from_url(url)} ({unique_file_enumerator}).mp3",
            f"{location}{os.sep}{extract_youtube_id_from_url(url)} ({unique_file_enumerator}).m4a",
            f"{location}{os.sep}{extract_youtube_id_from_url(url)} ({unique_file_enumerator}).aac"
        ]

    # Download the track and return the downloaded path
    ydl_opts: dict = {
        "format": "m4a/aac/mp3/bestaudio",
        "paths": {"home": location},
    }
    if force_mp3:
        ydl_opts["format"] = "bestaudio"
        ydl_opts["postprocessors"] = [{
            "key": "FFmpegExtractAudio", 
            "preferredcodec": "mp3"
        }]
    
    if unique_file_enumerator == 0:
        ydl_opts["outtmpl"] = {
            "default": "%(id)s.%(ext)s"
        }
    else:
        ydl_opts["outtmpl"] = {
            "default": f"%(id)s ({unique_file_enumerator}).%(ext)s"
        }

    with YoutubeDL(ydl_opts) as ydl:
        video_info: dict = ydl.extract_info(url)
        # Trim the location of any trailing directory separators
        trimmed_location: str = location
        if trimmed_location[-1] == os.sep:
            trimmed_location = trimmed_location[:-1]
        # Return the downloaded path

        info: dict[str, str] = {}

        if "track" in video_info:
            info["title"] = video_info["track"]
        else:
            info["title"] = video_info["title"]
        
        if "creator" in video_info:
            info["artists"] = video_info["creator"]
        elif "artists" in video_info:
            artists_str: str = ""
            for artist_str in video_info["artists"]:
                artists_str = artists_str + artist_str
            info["artists"] = artists_str
        else:
            info["artists"] = video_info["uploader"]
        
        if "album" in video_info:
            info["album"] = video_info["album"]
        else:
            info["album"] = ""

        if unique_file_enumerator == 0:
            if force_mp3:
                info["download_path"] = f"{trimmed_location}{os.sep}{video_info["id"]}.mp3"
            else:
                info["download_path"] = f"{trimmed_location}{os.sep}{video_info["id"]}.{video_info["ext"]}"
        else:
            if force_mp3:
                info["download_path"] = f"{trimmed_location}{os.sep}{video_info["id"]} ({unique_file_enumerator}).mp3"
            else: 
                info["download_path"] = f"{trimmed_location}{os.sep}{video_info["id"]} ({unique_file_enumerator}).{video_info["ext"]}"
        
        return info

def update_yt_dlp() -> int:
    """Return 0 if update was successful, 1 if no update was needed, and -1 if update failed"""
    pip_install_result: CompletedProcess[str] = subprocess.run(
        [sys.executable, "-m", "pip", "install", "--upgrade", "pip"], 
        capture_output=True, 
        text=True
    )
    yt_dlp_install_result: CompletedProcess[str] = subprocess.run(
        [sys.executable, "-m", "pip", "install", "--upgrade", "yt-dlp"], 
        capture_output=True, 
        text=True
    )
    
    if pip_install_result.returncode != 0 or yt_dlp_install_result.returncode != 0:
        return -1
    if ("already satisfied" in pip_install_result.stdout) and ("already satisfied" in yt_dlp_install_result.stdout):
        return 1
    return 0

def extract_youtube_id_from_url(url: str) -> str:
    default_youtube_str: str = "youtube.com/watch?v="
    shortened_youtube_str: str = "youtu.be/"
    # First assume that URL is not shortened ("youtube.com/watch?v=[id]&[extra params]")
    id_begin: int = url.find(default_youtube_str) + len(default_youtube_str)
    id_end: int = url.find("&")
    # Check if this assumption was correct
    if id_begin - len(default_youtube_str) == -1:
        # URL is shortened ("youtu.be/[id]?[extra params]")
        id_begin = url.find(shortened_youtube_str) + len(shortened_youtube_str)
        id_end = url.find("?")
    # Check if URL is actually shortened
    if id_begin - len(shortened_youtube_str) == -1:
        # Not in the youtube.com or youtu.be format (now we don't know what's happening, invalid format)
        return None
    if id_end == -1:
        return url[id_begin:]
    return url[id_begin:id_end]

if __name__ == "__main__":
    print(download_youtube_track("https://www.youtube.com/watch?v=ss5msvokUkY", "D:\\"))