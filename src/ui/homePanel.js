function loadPinnedItems(pinnedItemsPanel) {

    console.log("loadPinnedItems()");
    Staccato.readSettings();
    let pinnedItems = Staccato.getPinnedItems();
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