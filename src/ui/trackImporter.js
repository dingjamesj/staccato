var cpp;

function startup(_cpp) {

    cpp = _cpp;
    
}

function loadPreview(container) {

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

function importTrackFromUrl(container) {

    container.isDownloading = true;

    let url = container.urlText;
    let extraParamsStr = container.extraParamText;
    if(!container.isOverwritingMetadata) {

        extraParamsStr = "";

    }

    //Tokenize the extra parameters with newlines as the delimiter
    let extraParamsList = extraParamsStr.split(/\r?\n/);

    let downloadInfo = cpp.downloadTrackFromUrl(url, extraParamsList);
    if(downloadInfo.length === 1) {

        //TODO: Download failed action
        Dialogs.openMessageDialog("", "Unexpected download error:", downloadInfo[0], true, false)

    } else {



    }

    container.isDownloading = false;

}