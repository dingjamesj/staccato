import {setPinnedItemsZoomLevel} from "homePanel.mjs"

export function startup(staccatoInterface) {

    console.log("PROGRAM STARTUP");
    staccatoInterface.readSettings();
    setPinnedItemsZoomLevel(1);

}