import QtQuick
import QtQuick.Controls.Basic
import "homePanel.js" as Logic

Column {
    id: container
    spacing: (height - pinnedItemsPanel.height - playlistsPanel.height - downloaderPanel.height - topPadding - bottomPadding) / 2
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
        width: parent.width - parent.leftPadding - parent.rightPadding
        height: parent.height * 0.33
        spacing: 10
        leftPadding: 0
        rightPadding: 0
        topPadding: 0
        bottomPadding: 10

        //Header containing the text "Pinned" and view buttons
        Row {
            id: pinnedItemsHeader
            width: parent.width
            height: implicitHeight
            spacing: 9

            Text {
                id: pinnedItemsHeaderText
                width: pinnedItemsHeader.width - pinnedItemsHeader.spacing - pinnedItemsViewButton.width
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

        ScrollView {
            id: pinnedItemsContainer
            width: parent.width
            height: parent.height - pinnedItemsHeader.height - parent.topPadding - parent.bottomPadding - spacing
            anchors.left: parent.left
            anchors.right: parent.right
            clip: true
            ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
            ScrollBar.vertical.policy: ScrollBar.AlwaysOn
            Component.onCompleted: {
                contentItem.boundsBehavior = Flickable.StopAtBounds;
            }

            Flow {
                id: pinnedItems
                width: pinnedItemsContainer.width
                spacing: 8
                flow: Flow.LeftToRight

                ArtworkTextButton {
                    id: testingItemButtonPanel
                    width: 200
                    height: 100
                    radius: 10
                    color: "#303030"
                    artworkSource: "image://audioFile/D:/Programming/C++/staccato/tracks/ffMfBDkmlz8.m4a"
                    name: "STARGAZING"
                    description: "43 songs"
                }
                ArtworkTextButton {
                    id: testingItemButtonPanel5
                    width: 200
                    height: 100
                    radius: 10
                    color: "#303030"
                    artworkSource: "image://audioFile/D:/Programming/C++/staccato/tracks/ffMfBDkmlz8.m4a"
                    name: "STARGAZING"
                    description: "43 songs"
                }
                ArtworkTextButton {
                    id: testingItemButtonPanel4
                    width: 200
                    height: 100
                    radius: 10
                    color: "#303030"
                    artworkSource: "image://audioFile/D:/Programming/C++/staccato/tracks/ffMfBDkmlz8.m4a"
                    name: "STARGAZING"
                    description: "43 songs"
                }
                ArtworkTextButton {
                    id: testingItemButtonPanel3
                    width: 200
                    height: 100
                    radius: 10
                    color: "#303030"
                    artworkSource: "image://audioFile/D:/Programming/C++/staccato/tracks/ffMfBDkmlz8.m4a"
                    name: "STARGAZING"
                    description: "43 songs"
                }
                ArtworkTextButton {
                    id: testingItemButtonPanel2
                    width: 200
                    height: 100
                    radius: 10
                    color: "#303030"
                    artworkSource: "image://audioFile/D:/Programming/C++/staccato/tracks/ffMfBDkmlz8.m4a"
                    name: "STARGAZING"
                    description: "43 songs"
                }
                ArtworkTextButton {
                    id: testingItemButtonPanel1
                    width: 200
                    height: 100
                    radius: 10
                    color: "#303030"
                    artworkSource: "image://audioFile/D:/Programming/C++/staccato/tracks/ffMfBDkmlz8.m4a"
                    name: "STARGAZING"
                    description: "43 songs"
                }
                ArtworkTextButton {
                    id: testingItemButtonPanel17
                    width: 200
                    height: 100
                    radius: 10
                    color: "#303030"
                    artworkSource: "image://audioFile/D:/Programming/C++/staccato/tracks/ffMfBDkmlz8.m4a"
                    name: "STARGAZING"
                    description: "43 songs"
                }
                ArtworkTextButton {
                    id: testingItemButtonPanel16
                    width: 200
                    height: 100
                    radius: 10
                    color: "#303030"
                    artworkSource: "image://audioFile/D:/Programming/C++/staccato/tracks/ffMfBDkmlz8.m4a"
                    name: "STARGAZING"
                    description: "43 songs"
                }
                ArtworkTextButton {
                    id: testingItemButtonPanel15
                    width: 200
                    height: 100
                    radius: 10
                    color: "#303030"
                    artworkSource: "image://audioFile/D:/Programming/C++/staccato/tracks/ffMfBDkmlz8.m4a"
                    name: "STARGAZING"
                    description: "43 songs"
                }
                ArtworkTextButton {
                    id: testingItemButtonPanel14
                    width: 200
                    height: 100
                    radius: 10
                    color: "#303030"
                    artworkSource: "image://audioFile/D:/Programming/C++/staccato/tracks/ffMfBDkmlz8.m4a"
                    name: "STARGAZING"
                    description: "43 songs"
                }
                ArtworkTextButton {
                    id: testingItemButtonPanel13
                    width: 200
                    height: 100
                    radius: 10
                    color: "#303030"
                    artworkSource: "image://audioFile/D:/Programming/C++/staccato/tracks/ffMfBDkmlz8.m4a"
                    name: "STARGAZING"
                    description: "43 songs"
                }
                ArtworkTextButton {
                    id: testingItemButtonPanel12
                    width: 200
                    height: 100
                    radius: 10
                    color: "#303030"
                    artworkSource: "image://audioFile/D:/Programming/C++/staccato/tracks/ffMfBDkmlz8.m4a"
                    name: "STARGAZING"
                    description: "43 songs"
                }
            }
        }
    }

    //=========================================
    //             PLAYLISTS PANEL             
    //=========================================

    Column {
        id: playlistsPanel

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

    Column {
        id: downloaderPanel
    }
}