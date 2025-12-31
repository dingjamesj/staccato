import QtQuick
import QtQuick.Controls.Basic
import staccato
import "homePanel.js" as Logic

pragma ComponentBehavior: Bound

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
                width: pinnedItemsHeader.width - pinnedItemsZoomButton.width - pinnedItemsSortModeComboBox.width - parent.spacing * 2
                text: "Pinned"
                font.family: interFont.name
                font.pointSize: 24
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: "#ffffff"
            }

            ComboBox {
                id: pinnedItemsSortModeComboBox
                anchors.verticalCenter: parent.verticalCenter
                width: 150
                height: 30
                hoverEnabled: true
                model: ["Custom Order", "Alphabetical"]

                Component.onCompleted: {
                    let sortMode = Logic.getPinnedItemsSortMode();
                    if(sortMode === "CUSTOM") {

                        currentIndex = 0;

                    } else if(sortMode === "ALPHA") {

                        currentIndex = 1;

                    }
                }

                onActivated: {
                    if(currentIndex === 0) {

                        Logic.setPinnedItemsSortMode("CUSTOM", pinnedItemsPanel, pinnedItemsContainer);

                    } else if(currentIndex === 1) {

                        Logic.setPinnedItemsSortMode("ALPHA", pinnedItemsPanel, pinnedItemsContainer);
                        
                    }
                }

                contentItem: Text {
                    text: pinnedItemsSortModeComboBox.displayText
                    verticalAlignment: Text.AlignVCenter
                    horizontalAlignment: Text.AlignHCenter
                    font.family: interFont.name
                    font.pointSize: 11
                    font.weight: Font.DemiBold
                    color: "#ffffff"
                }
                indicator: Image {
                    anchors.right: parent.right
                    anchors.verticalCenter: parent.verticalCenter
                    height: parent.height - 5
                    source: "qrc:/staccato/src/ui/resources/chevrondown.svg"
                    fillMode: Image.PreserveAspectFit
                }
                background: Rectangle {
                    anchors.fill: pinnedItemsSortModeComboBox
                    radius: 6
                    color: pinnedItemsSortModeComboBox.pressed ? "#606060" : (pinnedItemsSortModeComboBox.hovered ? "#4d4d4d" : "#404040")
                }
                popup: Popup {
                    y: pinnedItemsSortModeComboBox.height - 1
                    width: pinnedItemsSortModeComboBox.width - 30
                    implicitHeight: popupListView.implicitHeight
                    padding: 0
                    background: Rectangle {
                        anchors.fill: parent
                        radius: 6
                        color: "#404040"
                    }
                    contentItem: ListView {
                        id: popupListView
                        clip: true
                        implicitHeight: contentHeight
                        model: pinnedItemsSortModeComboBox.delegateModel

                        currentIndex: pinnedItemsSortModeComboBox.highlightedIndex
                        Keys.onEscapePressed: pinnedItemsSortModeComboBox.popup.close()

                        delegate: ItemDelegate {
                            id: delegate
                            width: popupListView.width
                            height: 30
                            required property string modelData
                            property bool isHighlighted: ListView.isCurrentItem

                            contentItem: Text {
                                text: delegate.modelData
                                font.family: interFont.name
                                font.pointSize: 11
                                font.weight: Font.DemiBold
                                color: "#ffffff"
                                verticalAlignment: Text.AlignVCenter
                                horizontalAlignment: Text.AlignHCenter
                            }
                            
                            background: Rectangle {
                                radius: 6
                                color: delegate.isHighlighted ? "#4d4d4d" : "#404040"
                            }
                        }
                    }
                }
            }

            //Zoom button
            RoundButton {
                id: pinnedItemsZoomButton
                anchors.verticalCenter: parent.verticalCenter
                width: 30
                height: 30
                radius: 6
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
                width: 30
                height: 30
                radius: 6
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