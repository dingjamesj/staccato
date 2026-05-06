from subprocess import CompletedProcess
import subprocess
import sys
import os
import venv

#======================================================================================================
#  This Python file contains every function that's exposed to C++.
#  For functions that are used in C++, you...
#   1. Must not change their function names or parameters
#   2. Must not delete them
#   3. Are allowed to modify the code inside the function
# 
#  The purpose of these functions is to give the C++ and QML/JavaScript side of the program the 
#  information that they need.
#  For some functions, extra arguments are given in case their implementations require it. These
#  extra arguments are given by the user.
#======================================================================================================

#================================
#  Required functions are below:
#================================

# - Downloads audio givne a URL to a track from a streaming service.
# - Does not need to write metadata to the audio file since C++ handles that.
# - Does not need to download directly from the given URL.
#   - That is, the given URL's purpose is to only identify the track and audio can be downloaded from a different source.
# - Return a dict with the downloaded filepath and track information, or return an empty dict if an unexpected error occured.
# - The dict's keys must be the following: 
#   - "filepath" (str)
#   - "title" (str)
#   - "artists" (list[str])
#   - "album" (str)
def download_track_from_url(download_dir: str, url: str, args: list[str]) -> dict:
    return {
        "filepath": "testing dir download_track_from_url",
        "title": "title",
        "artists": ["artist1", "artist2"],
        "album": "album"
    }


# - Downloads audio given a track's title, artists, and album. 
# - Files should be downloaded to the staccato tracks directory.
# - Does not need to write metadata to the audio file since C++ handles that.
# - Return the downloaded filepath or an empty string if an unexpected error occurred.
def download_track_from_info(download_dir: str, title: str, artists: list[str], album: str, args: list[str]) -> str:
    return "testing dir download_track_from_info"


# - Returns information about the track (title, artists, album, and artwork).
# - The dict's keys must be the following: 
#   - "title" (str)
#   - "artists" (list[str])
#   - "album" (str)
#   - "artwork" (str)
def get_online_track_full_info(url: str) -> dict:
    return {
        "title": "online title",
        "artists": ["artist1", "artist2"],
        "album": "online album",
        "artwork": "online artwork"
    }

# - Returns a list of tracks from the search query.
def search_tracks(query: str, num_results: int) -> list[dict]:
    return [
        {
            "title": "title1",
            "artists": ["artist1"],
            "album": "album1"
        },
        {
            "title": "title2",
            "artists": ["artist2", "artist3"],
            "album": "album2"
        }
    ]

# - Ensures that the Python environment is ready to use (ex. the virtual env. exists, libraries are 
#   installed and up to date, etc.). Returns true if the environment is ready to use after running
#   this function.
def ensure_environment(venv_dir: str) -> bool:
    if not ensure_venv(venv_dir):
        return False
    return update_libraries()


#==========================================
#  Helper functions (not required by C++):
#==========================================

def ensure_venv(venv_dir: str) -> bool:
    if not os.path.exists(venv_dir):
        try:
            venv.create(venv_dir, with_pip=True)
        except Exception as e:
            print(f"Failed to create venv: {e}")
            return False
    return True


def update_libraries() -> bool:
    pip_install_result: CompletedProcess[str] = subprocess.run([sys.executable, "-m", "pip", "install", "--upgrade", "pip"])
    yt_dlp_install_result: CompletedProcess[str] = subprocess.run([sys.executable, "-m", "pip", "install", "--upgrade", "yt-dlp"])
    return pip_install_result.returncode == 0 and yt_dlp_install_result.returncode == 0

