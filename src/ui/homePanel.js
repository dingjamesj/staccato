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

        console.log(pinnedItems[i]);
        
    }

}