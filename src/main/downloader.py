from yt_dlp import YoutubeDL

def download_youtube_track(url: str, location: str) -> int:
    """Returns 0 if download was successful, 1 otherwise."""
    
    ydl_opts = {
        "format": "m4a/aac/mp3/bestaudio",
        "paths" : {"home": location},
        "output": "%(title)s %(id)s.%(ext)s"
    }
    with YoutubeDL(ydl_opts) as ydl:
        return ydl.download([url])

if __name__ == "__main__":
    print(download_youtube_track("https://www.youtube.com/watch?v=uXwRgnZ990I&t=15s", "D:\\"))

    print("===========================================================")

    print(download_youtube_track("https://www.youtube.com/watch?v=uXwRgnZ990I&t=15s", "C:\\Users\\James\\Downloads"))