from yt_dlp import YoutubeDL

from subprocess import CompletedProcess
import subprocess
import sys

import os

from mutagen.id3 import ID3, APIC, ID3NoHeaderError, PictureType
from mutagen.mp4 import MP4, MP4Cover

import urllib.request
from http.client import HTTPResponse

import imageio_ffmpeg

import imghdr

def download_youtube_track(url: str, artwork_url: str, location: str, force_mp3: bool = False, force_opus: bool = False) -> str:
    """Returns the downloaded path if the download was successful, empty string otherwise. 
    Downloads an M4A (with AAC codec) by default, but options are included for MP3 and WEBM (with Opus codec)"""
    try:
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
            "ffmpeg_location": imageio_ffmpeg.get_ffmpeg_exe()
        }
        if force_mp3:
            ydl_opts["format"] = "bestaudio"
            ydl_opts["postprocessors"] = [{
                "key": "FFmpegExtractAudio", 
                "preferredcodec": "mp3"
            }]
        if force_opus:
            ydl_opts["format"] = "bestaudio"
            ydl_opts["postprocessors"] = [{
                "key": "FFmpegExtractAudio",
                "preferredcodec": "opus"
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
            # Get the downloaded path
            downloaded_path: str
            if unique_file_enumerator == 0:
                downloaded_path = f"{trimmed_location}{os.sep}{video_info["id"]}"
            else:
                downloaded_path = f"{trimmed_location}{os.sep}{video_info["id"]} ({unique_file_enumerator})"
            if force_mp3:
                downloaded_path = downloaded_path + ".mp3"
            else:
                downloaded_path = downloaded_path + f".{video_info["ext"]}"
            # Embed track artwork
            split_path: tuple[str] = os.path.splitext(downloaded_path)
            path_extension: str = split_path[1]
            if path_extension == ".m4a":  
                set_track_artwork_mp4(downloaded_path, artwork_url)
            elif path_extension == ".mp3":
                set_track_artwork_id3(downloaded_path, artwork_url)
            elif path_extension == ".wav":
                set_track_artwork_riff(downloaded_path, artwork_url)
            elif path_extension == ".opus" or path_extension == ".ogg" or path_extension == ".flac":
                set_track_artwork_vorbis(downloaded_path, artwork_url)
            # Return the downloaded path
            return downloaded_path
    except:
        return ""


def set_track_artwork_mp4(track_path: str, artwork_url: str) -> bool:
    try:
        mp4: MP4 = MP4(track_path)
        response: HTTPResponse = urllib.request.urlopen(artwork_url)
        image_data: bytes = response.read()
        image_type: str = imghdr.what(None, image_data)
        artwork: MP4Cover
        if image_type == "jpeg":
            artwork = MP4Cover(image_data, MP4Cover.FORMAT_JPEG)
        elif image_type == "png":
            artwork = MP4Cover(image_data, MP4Cover.FORMAT_PNG)
        else:
            return False
        mp4["covr"] = [artwork]
        mp4.save()
    except:
        return False
    return True


def set_track_artwork_id3(track_path: str, artwork_url: str) -> bool:
    try:
        id3: ID3
        try:
            id3 = ID3(track_path)
        except ID3NoHeaderError:
            id3 = ID3()
            id3.save(track_path)
            id3 = ID3(track_path)
        response: HTTPResponse = urllib.request.urlopen(artwork_url)
        image_data: bytes = response.read()
        image_type: str = imghdr.what(None, image_data)
        image_type_str: str = ""
        if image_type:
            image_type_str = f"image/{image_type}"
        else:
            return False
        id3.add(APIC(
            encoding=3,
            mime=image_type_str,
            type=PictureType.COVER_FRONT,
            desc="Cover",
            data=image_data
        ))
        id3.save(v2_version=3)
    except:
        return False
    return True


def set_track_artwork_riff(track_path: str, artwork_url: str) -> bool:
    # Unimplemented
    return False


def set_track_artwork_vorbis(track_path: str, artwork_url: str) -> bool:
    # Unimplemented
    return False


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
    print(download_youtube_track(
        url="https://www.youtube.com/watch?v=ss5msvokUkY",
        artwork_url="https://i.scdn.co/image/ab67616d0000b273dfc2f59568272de50a257f2f",
        location="D:\\",
        force_mp3=False
    ))