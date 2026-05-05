var cpp;

function startup(_cpp) {

    cpp = _cpp;
    
}

function loadPreview(container) {

    let url = container.urlText;
    let loadingFlag = container.previewIsLoading;
    let loadCompletionFlag = container.previewIsLoaded;
    let previewEditor = container.previewEditor;

    loadingFlag = true;
    loadCompletionFlag = false;
    
    //We'll first assume that the URI is a track on the local filesystem.
    //If it isn't, then C++ will return an empty list, and then we'll assume that the URI is an internet link.

    //The C++ API should return the title, artists, and album-- or nothing if the URI isn't a local file.
    let track = cpp.getLocalTrackInfo(url);
    if(track.length === 0) {

        //The C++ API should return the title, artists, album, and artwork URL
        track = cpp.getOnlineTrackInfo(url);

        if(track.length < 4) {

            return;
            
        }

    } else if(track.length < 3) {

        return; 

    }

    previewEditor.titleText = track[0];
    previewEditor.albumText = track[2];

    //Display the artists
    previewEditor.clearArtistFields();
    for(let i = 0; i < track[1].length; i++) {

        previewEditor.addArtistField();
        previewEditor.artistsContainer.children[i].text = track[1][i];

    }

    //To display the artwork for...
    // ... a track on the local filesystem, we just simply set the artwork image source as the audio file path.
    // ... an online track, we use the artwork URL given by the C++ API.
    
    if(track.length >= 4) {

        previewEditor.artworkSource = track[3];

    } else {

        previewEditor.artworkSource = "image://audiofile/" + url.replaceAll("\\", "/");

    }

    loadingFlag = false;
    loadCompletionFlag = true;

}

function importTrackFromUrl(container) {

    let url = container.urlText;
    let extraParamsStr = container.extraParamText;
    let downloadingFlag = container.isDownloading;
    let previewEditor = container.previewEditor;
    downloadingFlag = true;

    //Tokenize the extra parameters with newlines as the delimiter
    let extraParamsList = myString.split(/\r?\n/);

    let downloadInfo = cpp.downloadTrackFromUrl(url, extraParamsList);
    if(downloadInfo.length === 0) {

        //TODO: Download failed action
        return;

    }

}