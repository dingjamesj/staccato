import {setPinnedItemsZoomLevel} from "homePanel.js"

function startup() {

    console.log("PROGRAM STARTUP");
    Staccato.readSettings();
    setPinnedItemsZoomLevel(1);

}