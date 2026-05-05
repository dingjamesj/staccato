from subprocess import CompletedProcess
import subprocess
import sys
import os
import venv

#======================================================================================================
# This Python file contains every function that's exposed to C++.
# For functions that are used in C++, you...
#  1. Must not change their function names or parameters
#  2. Must not delete them
#  3. Are allowed to modify the code inside the function
# 
# The purpose of these functions is to give the C++ and QML/JavaScript side of the program the 
# information that they need.
# For some functions, extra arguments are given in case their implementations require it. These
# extra arguments are given by the user.
# ----------------------------------------------------------------------------------------------------
# 
# LIST OF REQUIRED FUNCTIONS:
# 
# download_track_from_url(download_dir: str, url: str) -> dict
# - Given a URL to a track from a streaming service, downloads audio and returns a dict with the downloaded
#   filepath and track information. Returns an empty dict if an unexpected error occured.
# - The implementation of this function does not need to directly download audio from the URL parameter.
#   That is, the URL parameter serves to identify the track and is not necessarily where the audio file
#   needs to be sourced from.
# - The implementation does not need to write metadata to the audio file since C++ handles that.
#
# download_track_from_info(download_dir: str, title: str, artists: list[str], album: str, args: list[str]) -> str
# - Given a track's title, artists, and album, downloads audio and returns the downloaded filepath.
#   Returns an empty string if an unexpected error occured.
# - The implementation does not need to write metadata to the audio file since C++ handles that.
# 
# search_tracks(query: str, num_results: int) -> list[dict]
# - Returns a list of tracks from the search query.
# 
# get_online_track_full_info(url: str) -> dict
# - Returns information about the track (title, artists, album, and artwork).
# - Keys must be exactly "title", "artists", "album", and "artwork".
# 
# ensure_environment(venv_dir: str) -> bool
# - Ensures that the Python environment is ready to use (ex. the virtual env. exists, libraries are 
#   installed and up to date, etc.). Returns true if the environment is ready to use after running
#   this function.
# 
#======================================================================================================

# Required by C++
def download_track_from_url(download_dir: str, url: str, args: list[str]) -> dict:
    return ""


# Required by C++
def download_track_from_info(download_dir: str, title: str, artists: list[str], album: str, args: list[str]) -> str:
    return ""


# Required by C++
def get_online_track_full_info(url: str) -> dict:
    return {}


# Required by C++
def ensure_environment(venv_dir: str) -> bool:
    if not ensure_venv(venv_dir):
        return False
    return update_libraries()


#====================
#  Helper functions
#====================

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

