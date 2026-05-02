import QtQuick
import Qt.labs.platform

pragma Singleton

Item {
    id: container

    signal fileSelected(url fileURL)

    FileDialog {
        id: globalFileDialog
        title: "Select a File"
        
        onAccepted: {
            container.fileSelected(globalFileDialog.file)
        }
    }

    function open(filter) {
        globalFileDialog.title = "Choose a file";
        if(filter) {

            globalFileDialog.nameFilters = filter;
            
        }
        globalFileDialog.open()
    }
}