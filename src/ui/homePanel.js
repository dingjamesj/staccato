var pinnedItemsZoomLevel = -1;
var playlistsZoomLevel = -1;
var pinnedItemsSortMode = "";

function startup(staccatoInterface, pinnedItemsPanel) {

    console.log("HOME PANEL STARTUP");

    //Set the zoom levels and sort modes
    pinnedItemsZoomLevel = staccatoInterface.getPinnedItemsZoomLevel();
    playlistsZoomLevel = staccatoInterface.getPlaylistsZoomLevel();
    pinnedItemsSortMode = staccatoInterface.getPinnedItemsSortMode();

    if(staccatoInterface.getPinnedItems().length <= 0) {

        pinnedItemsPanel.visible = false;

    }

}

function incrementPinnedItemsZoomLevel(pinnedItemsContainer) {

    pinnedItemsZoomLevel = (pinnedItemsZoomLevel + 1) % 3;
    updateItemsZoomLevel(pinnedItemsContainer, pinnedItemsZoomLevel);

}

function incrementPlaylistsZoomLevel(playlistsContainer) {

    playlistsZoomLevel = (playlistsZoomLevel + 1) % 3;
    updateItemsZoomLevel(playlistsContainer, playlistsZoomLevel);

}

function getPinnedItemsZoomLevel() {

    return pinnedItemsZoomLevel;

}

function getPlaylistsZoomLevel() {

    return playlistsZoomLevel;

}

function loadPinnedItems(staccatoInterface, pinnedItemsPanel, pinnedItemsContainer) {

    let pinnedItems = staccatoInterface.getPinnedItems();
    pinnedItemsPanel.visible = true;
    for(let i = pinnedItemsContainer.children.length - 1; i >= 0; i--) {

        pinnedItemsContainer.children[i].destroy();

    }

    let component = Qt.createComponent("ArtworkTextButton.qml");
    let buttonWidth = 0;
    let nameTextSize = 0;
    let descriptionTextSize = 0;
    switch(pinnedItemsZoomLevel) {
    
    case 0:
        //List view
        break;
    case 1:
        buttonWidth = (pinnedItemsContainer.width - pinnedItemsContainer.spacing - pinnedItemsContainer.spacing) / 3;
        nameTextSize = 12;
        descriptionTextSize = 8;
        break;
    case 2:
        buttonWidth = (pinnedItemsContainer.width - pinnedItemsContainer.spacing) / 2;
        nameTextSize = 16;
        descriptionTextSize = 10;
        break;
    default:
        buttonWidth = (pinnedItemsContainer.width - pinnedItemsContainer.spacing - pinnedItemsContainer.spacing) / 3;
        nameTextSize = 12;
        descriptionTextSize = 8;
        break;
    
    }

    let artworkSource = "";
    let description = "";
    for(let i = 0; i < pinnedItems.length; i++) {

        if(pinnedItems[i][0] === false) {

            //Playlists
            artworkSource = staccatoInterface.getPlaylistImagePath(pinnedItems[i][3]);
            if(artworkSource.substring(0, 5) !== "qrc:/") { //The image path will be in QRC if it's the placeholder image

                artworkSource = "file:///" + artworkSource;

            }

            if(pinnedItems[i][2][0] === 1) {

                description = "1 track";

            } else {

                description = pinnedItems[i][2][0] + " tracks";

            }

        } else {

            //Tracks
            artworkSource = "image://audiofile/" + staccatoInterface.getTrackFilePath(pinnedItems[i][1], pinnedItems[i][2], pinnedItems[i][3]);

            description = "";
            for(let a = 0; a < pinnedItems[i][2].length; a++) {

                description += pinnedItems[i][2][a] + ", "

            }
            description = description.substring(0, description.length - 2);

        }

        let object = component.createObject(pinnedItemsContainer, {
            width: buttonWidth,
            height: buttonWidth / 3,
            radius: 10,
            spacing: buttonWidth / 40,
            defaultColor: "#303030",
            artworkSource: artworkSource,
            name: pinnedItems[i][1],
            description: description,
            nameTextSize: nameTextSize,
            descriptionTextSize: descriptionTextSize,
        });
        
    }

}

function updateItemsZoomLevel(itemsContainer, zoomLevel) {

    console.log(zoomLevel);

    let buttonWidth = 0;
    let nameTextSize = 0;
    let descriptionTextSize = 0;
    switch(zoomLevel) {
    
    case 0:
        //List view
        return;
    case 1:
        buttonWidth = (itemsContainer.width - itemsContainer.spacing - itemsContainer.spacing) / 3;
        nameTextSize = 12;
        descriptionTextSize = 8;
        break;
    case 2:
        buttonWidth = (itemsContainer.width - itemsContainer.spacing) / 2;
        nameTextSize = 16;
        descriptionTextSize = 10;
        break;
    default:
        buttonWidth = (itemsContainer.width - itemsContainer.spacing - itemsContainer.spacing) / 3;
        nameTextSize = 12;
        descriptionTextSize = 8;
        break;
    
    }

    for(let i = 0; i < itemsContainer.children.length; i++) {

        itemsContainer.children[i].width = buttonWidth;
        itemsContainer.children[i].height = buttonWidth / 3;
        itemsContainer.children[i].spacing = buttonWidth / 40;

    }
    console.log("");

}