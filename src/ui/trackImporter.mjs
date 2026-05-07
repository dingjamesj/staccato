import * as Util from "util.mjs";
import {cpp, dialogs} from "util.mjs";

export function loadPreview(container) {

    container.previewIsLoading = true;
    container.previewIsLoaded = false;

    let url = container.urlText;
    let previewEditor = container.previewEditor;
    
    //We'll first assume that the URI is a track on the local filesystem.
    //If it isn't, then C++ will return an empty list, and then we'll assume that the URI is an internet link.

    //The C++ API should return the title, artists, and album-- or nothing if the URI isn't a local file.
    let track = cpp.getLocalTrackInfo(url);
    if(track.length === 0) {

        //The C++ API should return the title, artists, album, and artwork 
        track = cpp.getOnlineTrackInfo(url);

        if(track.length < 4) {

            return;
            
        }

    } else if(track.length < 3) {

        return; 

    }

    previewEditor.titleText = track[0];
    previewEditor.setArtists(track[1]);
    previewEditor.albumText = track[2];

    //To display the artwork for...
    // ... a track on the local filesystem, we just simply set the artwork image source as the audio file path.
    // ... an online track, we use the artwork URL given by the C++ API.
    
    if(track.length >= 4) {

        previewEditor.artworkSource = track[3];

    } else {

        previewEditor.artworkSource = "image://audiofile/" + url.replaceAll("\\", "/");

    }

    container.previewIsLoading = false;
    container.previewIsLoaded = true;

}

export function importTrackFromUrl(container) {

    container.isDownloading = true;
    let url = container.urlText;

    //First assume that the URL is for an audio file on the local filesystem.
    //We'll easily find out if that assumption is correct or not, and if it isn't,
    //then we'll assume that the URL is an online URL.

    let localTrackInfo = cpp.getLocalTrackInfo(url);
    if(localTrackInfo.length === 3) { //It was a local track if data was returned

        let filesystemImportSuccess = cpp.importTrackFromFilesystem(localTrackInfo[0], localTrackInfo[1], localTrackInfo[2], url);
        if(!filesystemImportSuccess) {

            dialogs.openMessageDialog(
                "", 
                "Unexpected filesystem import error:",
                "Error when copying the file from *" + downloadInfo[3] + "* or when writing metadata to the copied file.",
                true,
                false
            );

        } else {

            container.statusText = "Success"

        }

        container.isDownloading = false;
        return;

    }

    //Now assuming that the URL is an online resource:

    let extraParamsStr = container.extraParamText;
    if(!container.isUsingExtraParams) {

        extraParamsStr = "";

    }

    //Tokenize the extra parameters with newlines as the delimiter
    let extraParamsList = extraParamsStr.split(/\r?\n/);

    let downloadInfo = cpp.downloadTrackFromUrl(url, extraParamsList);
    if(downloadInfo.length === 1) {

        dialogs.openMessageDialog("", "Unexpected download error:", downloadInfo[0], true, false);

    } else if(downloadInfo.length === 4) {

        //Check if the downloaded track already exists in the track dict.
        //If it does, we want to confirm with the user that we want to replace this track.
        
        let title = "";
        let artists = [];
        let album = "";
        if(container.isOverwritingMetadata) {

            title = container.previewEditor.titleText;
            artists = container.previewEditor.getArtistsList();
            album = container.previewEditor.albumText;

        } else {

            title = downloadInfo[0];
            artists = downloadInfo[1];
            album = downloadInfo[2];

        }

        //downloadInfo contains the title, artists, album, and artwork URL in that order.

        let existingFilepath = cpp.getTrackFilePath(title, artists, album);
        if(existingFilepath != "") {

            //Confirmation
            let trackStr = Util.stringifyTrack(title, artists, album);

            dialogs.openActionDialog(
                "", 
                "Confirm track replacement:", 
                "The track\n\n**" + trackStr + "**\n\nalready has the following audio file associated with it:\n\n" + existingFilepath + "\n\nAre you sure you want to replace the existing file?", 
                () => {
                    //Delete the track and import the newly downloaded track into the track dict.
                    cpp.deleteTrack(title, artists, album);
                    let success = cpp.importTrackFromFilesystem(title, artists, album, downloadInfo[3]);
                    if(!success) {
                        dialogs.openMessageDialog(
                            "", 
                            "Unexpected download error:",
                            "Error when copying the file from *" + downloadInfo[3] + "* or when writing metadata to the copied file.",
                            true,
                            false
                        );
                    } else {

                        container.statusText = "Success"

                    }
                }
            );

        } else { //The downloaded track doesn't already exist in the track dict:

            //Import the newly downloaded track into the track dict.
            let success = cpp.importTrackFromFilesystem(title, artists, album, downloadInfo[3]);
            if(!success) {
                dialogs.openMessageDialog(
                    "", 
                    "Unexpected download error:",
                    "Error when copying the file from *" + downloadInfo[3] + "* or when writing metadata to the copied file.",
                    true,
                    false
                );
            } else {

                container.statusText = "Success"

            }

        }

    }

    container.isDownloading = false;

}