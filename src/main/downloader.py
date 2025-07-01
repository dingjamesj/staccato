from yt_dlp import YoutubeDL

from subprocess import CompletedProcess
import subprocess
import sys

def download_youtube_track(url: str, location: str) -> int:
    """Returns 0 if download was successful, 1 otherwise."""
    ydl_opts = {
        "format": "m4a/aac/mp3/bestaudio",
        "paths" : {"home": location},
        "output": "%(title)s %(id)s.%(ext)s"
    }
    with YoutubeDL(ydl_opts) as ydl:
        return ydl.download([url])

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

if __name__ == "__main__":
    update_yt_dlp()
    # print(download_youtube_track("https://www.youtube.com/watch?v=uXwRgnZ990I&t=15s", "D:\\"))

    # print("===========================================================")

    # print(download_youtube_track("https://www.youtube.com/watch?v=uXwRgnZ990I&t=15s", "C:\\Users\\James\\Downloads"))