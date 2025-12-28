var pinnedItemsZoomLevel = -1;
var playlistsZoomLevel = -1;
var pinnedItemsSortMode = "";

function startup(staccatoInterface, pinnedItemsPanel, pinnedItemsContainer) {

    console.log("HOME PANEL STARTUP");

    //Set the zoom levels and sort modes
    pinnedItemsZoomLevel = staccatoInterface.getPinnedItemsZoomLevel();
    playlistsZoomLevel = staccatoInterface.getPlaylistsZoomLevel();
    pinnedItemsSortMode = staccatoInterface.getPinnedItemsSortMode();

    let pinnedItems = staccatoInterface.getPinnedItems();
    if(pinnedItems.length <= 0) {

        pinnedItemsPanel.visible = false;
        return;

    }

    pinnedItemsPanel.visible = true;
    let component = Qt.createComponent("ArtworkTextButton.qml");
    let artworkTextButton;
    for(let i = 0; i < pinnedItems.length; i++) {

        console.log(pinnedItems[i]);
        if(pinnedItems[i][0] === false) {

            //Playlists

            let imagePath = staccatoInterface.getPlaylistImagePath(pinnedItems[i][3]);
            if(imagePath.substring(0, 5) !== "qrc:/") { //The image path will be in QRC if it's the placeholder image

                imagePath = "file:///" + imagePath;

            }

            artworkTextButton = component.createObject(pinnedItemsContainer, {
                width: 200,
                height: 100,
                radius: 10,
                color: "#303030",
                artworkSource: imagePath,
                name: pinnedItems[i][1],
                description: pinnedItems[i][2][0] + " tracks"
            });

        } else {

            //Tracks

            let artistsString = "";
            for(let a = 0; a < pinnedItems[i][2].length; a++) {

                artistsString += pinnedItems[i][2][a] + ", "

            }
            artistsString = artistsString.substring(0, artistsString.length - 2);

            artworkTextButton = component.createObject(pinnedItemsContainer, {
                width: 200,
                height: 100,
                radius: 10,
                color: "#303030",
                artworkSource: "image://audiofile/" + staccatoInterface.getTrackFilePath(pinnedItems[i][1], pinnedItems[i][2], pinnedItems[i][3]),
                name: pinnedItems[i][1],
                description: artistsString
            });

        }
        
    }

}