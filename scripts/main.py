from subprocess import CompletedProcess
import subprocess
import sys
import os
import venv

#==================================================================
# This Python file contains every function that's exposed to C++.
# For functions that are used in C++, you...
#  1. Must not change their function names or parameters
#  2. Must not delete them
#  3. Are allowed to modify the code inside the function
#==================================================================


def download_track(url: str, title: str, artists: list[str], album: str, args: list[str]):
    pass


# def 


# Used in C++ code
# Checks if a virtual environment can be found, and installs a virtual environment if one cannot be found.
# Returns false if a virtual environment was not found and had to be installed, true otherwise.
def ensure_venv(venv_dir: str) -> bool:
    if not os.path.exists(venv_dir):
        venv.create(venv_dir, with_pip=True)
        return True
    else:
        return False
    

# Used in C++ code
def update_libraries() -> str:
    """Returns a string representation of the update status (failure, success, already up to date)"""
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
        return "UPDATE FAILED"
    if ("already satisfied" in pip_install_result.stdout) and ("already satisfied" in yt_dlp_install_result.stdout):
        return "ALREADY UP TO DATE"
    return "UPDATED SUCCESSFULLY"

