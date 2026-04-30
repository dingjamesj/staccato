function addArtistTextField(container) {

    let component = Qt.createComponent("RoundTextField.qml");
    component.createObject(container.previewArtistsContainer, {
        "Layout.preferredWidth": 1,
        "Layout.fillWidth": true,
        "Layout.fillHeight": true,
        enabled: "container.previewIsLoaded"
    });

}

function removeArtistTextField(container) {

    let artistsContainer = container.previewArtistsContainer;
    if(artistsContainer.children.length > 0) {

        artistsContainer.children[artistsContainer.children.length - 1].destroy();

    }

}

function loadTrackInfo(url, loadTrackInfoButton, titleField, artistsContainer, albumField, artworkRoundedImage) {

    loadTrackInfoButton.enabled = false;
    let track;
    if(url.charAt(1) === ":") {

        //Local filesystem track
        track = staccatoInterface.getLocalTrackInfo(url);

    } else {

        //Online track
        track = staccatoInterface.getOnlineTrackInfo(url);

    }

    console.log(track);

    if(track.length < 3) {

        return;

    }

    //Set the title
    titleField.text = track[0];

    //Set the artists
    for(let i = artistsContainer.children.length - 1; i >= 0; i--) {

        artistsContainer.children[i].destroy();

    }

    let component = Qt.createComponent("RoundTextField.qml");
    for(let i = 0; i < track[1].length; i++) {

        component.createObject(artistsContainer, {
            text: track[1][i]
        });

    }

    //Set the album
    albumField.text = track[2];

    //Set the artwork

    if(track.length >= 4) {

        //An online track would have returned an extra artwork URL link
        artworkRoundedImage.source = track[3];

    } else {

        let file_path = "";
        for(let i = 0; i < url.length; i++) {

            if(url.charAt(i) === "\\") {

                file_path += "/";

            } else {

                file_path += url.charAt(i);

            }

        }
        artworkRoundedImage.source = "image://audiofile/" + file_path;

    }

    loadTrackInfoButton.enabled = true;

}