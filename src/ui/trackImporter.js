var cpp;

function startup(_cpp) {

    cpp = _cpp;
    
}

function addArtistTextField(container) {

    let component = Qt.createComponent("RoundTextField.qml");
    component.createObject(container.previewArtistsContainer, {
        "Layout.preferredWidth": 1,
        "Layout.fillWidth": true,
        "Layout.fillHeight": true,
        readOnly: Qt.binding(function() {
            return !overwriteMetadataCheckbox.checked
        })
    });

}

function removeArtistTextField(container) {

    let artistsContainer = container.previewArtistsContainer;
    if(artistsContainer.children.length > 0) {

        artistsContainer.children[artistsContainer.children.length - 1].destroy();

    }

}

function loadTrackInfo(container) {

    let url = container.importURLText;
    let loadingFlag = container.previewIsLoading;
    let loadCompletionFlag = container.previewIsLoaded;
    let title = container.previewTitleText;
    let artistsContainer = container.previewArtistsContainer;
    let album = container.previewAlbumText;
    let artworkSource = container.previewArtworkSource;

    loadingFlag = true;
    loadCompletionFlag = false;
    let track;
    if(url.charAt(1) === ":") {

        //Local filesystem track
        track = cpp.getLocalTrackInfo(url);

    } else {

        //Online track
        track = cpp.getOnlineTrackInfo(url);

    }

    console.log(track);

    if(track.length < 3) {

        return;

    }

    title = track[0];

    //Set the artists
    for(let i = artistsContainer.children.length - 1; i >= 0; i--) {

        artistsContainer.children[i].destroy();

    }

    let component = Qt.createComponent("RoundTextField.qml");
    for(let i = 0; i < track[1].length; i++) {

        component.createObject(artistsContainer, {
            "Layout.preferredWidth": 1,
            "Layout.fillWidth": true,
            "Layout.fillHeight": true,
            enabled: "container.previewIsLoaded",
            text: track[1][i]
        });

    }

    album = track[2];

    //Set the artwork
    if(track.length >= 4) {

        //An online track would have returned an extra artwork URL link
        artworkSource = track[3];

    } else {

        //Local audio file
        let file_path = "";
        for(let i = 0; i < url.length; i++) {

            if(url.charAt(i) === "\\") {

                file_path += "/";

            } else {

                file_path += url.charAt(i);

            }

        }
        artworkSource = "image://audiofile/" + file_path;

    }

    loadingFlag = false;
    loadCompletionFlag = true;

}