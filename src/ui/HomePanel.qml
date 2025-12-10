import QtQuick
import QtQuick.Controls.Basic
import "homePanel.js" as Logic

Column {
    id: container
    spacing: height * 0.05
    leftPadding: 5
    rightPadding: 10
    topPadding: height * 0.048
    bottomPadding: 25

    Component.onCompleted: {
        Logic.loadPinnedItems(pinnedItemsPanel);
    }

    FontLoader {
        id: interFont
        source: "qrc:/staccato/src/ui/resources/Inter-VariableFont_opsz,wght.ttf"
    }

    //=========================================
    //           PINNED ITEMS PANEL           
    //=========================================
    
    Column {
        id: pinnedItemsPanel
        spacing: parent.height * 0.05
        leftPadding: 0
        rightPadding: 0
        topPadding: 0
        bottomPadding: 0

        Row {
            id: pinnedItemsHeader
            width: container.width - container.leftPadding - container.rightPadding
            height: 30
            spacing: 9

            Text {
                id: pinnedItemsHeaderText
                width: pinnedItemsHeader.width - pinnedItemsHeader.spacing - pinnedItemsViewButton.width
                height: 45
                text: "Pinned"
                font.family: interFont.name
                font.pointSize: 24
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: "#ffffff"
            }

            //Toggles between grid and list view
            Button {
                id: pinnedItemsViewButton
                width: 35
                height: 35
                padding: 3
                anchors.verticalCenter: parent.verticalCenter
                background: Rectangle {
                    width: parent.width
                    height: parent.height
                    radius: 8
                    color: "#434343"
                }
                icon.color: "#ffffff"
                icon.source: "qrc:/staccato/src/ui/resources/list.svg"
            }
        }
    }

    //=========================================
    //             PLAYLISTS PANEL             
    //=========================================

    Row {
        id: playlistsHeader
        width: container.width - container.leftPadding - container.rightPadding
        height: 30
        spacing: 9
        
        Text {
            id: playlistsHeaderText
            width: playlistsHeader.width - playlistsHeader.spacing - playlistsViewButton.width
            height: 45
            text: "Your Playlists"
            font.family: interFont.name
            font.pointSize: 24
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: "#ffffff"
        }

        //Toggles between list & grid view
        Button {
            id: playlistsViewButton
            width: 35
            height: 35
            padding: 3
            anchors.verticalCenter: parent.verticalCenter
            background: Rectangle {
                width: parent.width
                height: parent.height
                radius: 8
                color: "#434343"
            }
            icon.color: "#ffffff"
            icon.source: "qrc:/staccato/src/ui/resources/list.svg"
        }
    }
}