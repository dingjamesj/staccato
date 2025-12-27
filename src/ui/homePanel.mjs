var pinnedItemsZoomLevel = 1;

export function setPinnedItemsZoomLevel(zoomLevel) {

    pinnedItemsZoomLevel = zoomLevel;

}

export function startup(staccatoInterface, pinnedItemsPanel) {

    console.log("HOME PANEL STARTUP");
    let pinnedItems = staccatoInterface.getPinnedItems();
    if(pinnedItems.length <= 0) {

        pinnedItemsPanel.visible = false
        return;

    }

    pinnedItemsPanel.visible = true
    for(let i = 0; i < pinnedItems.length; i++) {

        if(pinnedItems[i][0] === false) {

            //Is a playlist

        } else {

            //Is a track

        }
        
    }

}