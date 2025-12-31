import QtQuick
import QtQuick.Controls.Basic
import staccato
import "homePanel.js" as Logic

Column {
    id: container
    spacing: (height - pinnedItemsPanel.height - playlistsPanel.height - topPadding - bottomPadding) / 2
    leftPadding: 5
    rightPadding: 10
    topPadding: height * 0.048
    bottomPadding: 25

    Component.onCompleted: {
        Logic.startup(StaccatoInterface);
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
                width: pinnedItemsHeader.width - pinnedItemsZoomButton.width - pinnedItemsSortModeButton.width - parent.spacing * 2
                text: "Pinned"
                font.family: interFont.name
                font.pointSize: 24
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: "#ffffff"
            }

            //Sort mode button
            RoundButton {
                id: pinnedItemsSortModeButton
                anchors.verticalCenter: parent.verticalCenter
                width: 35
                height: 35
                radius: 7
                spacing: 5
                defaultColor: "#404040"
                imageSource: "qrc:/staccato/src/ui/resources/list.svg"
                onClicked: {
                    Logic.cyclePinnedItemsSortMode(pinnedItemsPanel, pinnedItemsContainer);
                }
            }

            //Zoom button
            RoundButton {
                id: pinnedItemsZoomButton
                anchors.verticalCenter: parent.verticalCenter
                width: 35
                height: 35
                radius: 7
                spacing: 6
                defaultColor: "#404040"
                imageSource: "qrc:/staccato/src/ui/resources/zoom.svg"
                onClicked: {
                    Logic.incrementPinnedItemsZoomLevel(pinnedItemsContainer);
                }
            }
        }

        ScrollView {
            id: pinnedItemsScrollView
            width: parent.width
            height: parent.height - pinnedItemsHeader.height - parent.topPadding - parent.bottomPadding - parent.spacing
            contentWidth: width
            contentHeight: height
            clip: true
            ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
            ScrollBar.vertical.policy: ScrollBar.AlwaysOn
            Component.onCompleted: {
                contentItem.boundsBehavior = Flickable.StopAtBounds;
            }

            Flow {
                id: pinnedItemsContainer
                width: parent.width
                spacing: 7
                flow: Flow.LeftToRight

                onWidthChanged: {
                    Logic.loadPinnedItems(pinnedItemsPanel, pinnedItemsContainer);
                }
            }
        }
    }

    //=========================================
    //             PLAYLISTS PANEL             
    //=========================================

    Column {
        id: playlistsPanel
        width: parent.width - parent.leftPadding - parent.rightPadding
        height: parent.height * 0.33
        spacing: 10
        leftPadding: 0
        rightPadding: 0
        topPadding: 0
        bottomPadding: 10

        Row {
            id: playlistsHeader
            width: parent.width
            height: implicitHeight
            spacing: 9
            
            Text {
                id: playlistsHeaderText
                width: playlistsHeader.width - playlistsZoomButton.width - playlistsHeader.spacing
                text: "Your Playlists"
                font.family: interFont.name
                font.pointSize: 24
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: "#ffffff"
            }

            //Zoom button
            RoundButton {
                id: playlistsZoomButton
                anchors.verticalCenter: parent.verticalCenter
                width: 35
                height: 35
                radius: 7
                spacing: 6
                defaultColor: "#404040"
                imageSource: "qrc:/staccato/src/ui/resources/zoom.svg"
                onClicked: {
                    Logic.incrementPlaylistsZoomLevel(playlistsContainer);
                }
            }
        }

        ScrollView {
            id: playlistsScrollView
            width: parent.width
            height: parent.height - playlistsHeader.height - parent.topPadding - parent.bottomPadding - parent.spacing
            contentWidth: width
            contentHeight: height
            clip: true
            ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
            ScrollBar.vertical.policy: ScrollBar.AlwaysOn
            Component.onCompleted: {
                contentItem.boundsBehavior = Flickable.StopAtBounds;
            }

            Flow {
                id: playlistsContainer
                width: parent.width
                spacing: 7
                flow: Flow.LeftToRight

                onWidthChanged: {
                    Logic.loadPlaylists(playlistsPanel, playlistsContainer);
                }
            }
        }
    }

    // Column {
    //     id: downloaderPanel
    // }
}