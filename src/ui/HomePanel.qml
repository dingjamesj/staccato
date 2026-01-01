import QtQuick
import QtQuick.Controls.Basic
import staccato
import "homePanel.js" as Logic

pragma ComponentBehavior: Bound

Column {
    id: container
    spacing: 8
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
        height: (parent.height - parent.topPadding - parent.bottomPadding - addTracksPanel.height - parent.spacing * 2) * 0.4
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
                    color: pinnedItemsSortModeComboBox.pressed ? "#7f7f7f" : (pinnedItemsSortModeComboBox.hovered ? "#4d4d4d" : "#404040")
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
            clip: true
            ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
            ScrollBar.vertical.policy: ScrollBar.AsNeeded

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
        height: (parent.height - parent.topPadding - parent.bottomPadding - addTracksPanel.height - parent.spacing * 2) * 0.6
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
            clip: true
            ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
            ScrollBar.vertical.policy: ScrollBar.AsNeeded

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

    Column {
        id: addTracksPanel
        width: parent.width - parent.leftPadding - parent.rightPadding
        height: implicitHeight
        spacing: 12
        leftPadding: 0
        rightPadding: 0
        topPadding: 0
        bottomPadding: 0
        
        Text {
            id: addTracksText
            height: 39
            text: "Import New Tracks"
            font.family: interFont.name
            font.pointSize: 24
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: "#ffffff"
        }

        Row {
            id: addTracksContainer
            width: parent.width
            height: implicitHeight
            spacing: 8

            Column {
                id: urlContainer
                width: 270
                height: implicitHeight
                spacing: 8

                Text {
                    id: urlText
                    width: parent.width
                    height: 20
                    text: "URL/File Path"
                    font.family: interFont.name
                    font.pointSize: 13
                    font.weight: Font.DemiBold
                    wrapMode: Text.NoWrap
                    color: "#ffffff"
                }

                TextField {
                    id: urlTextField
                    width: parent.width
                    height: 25
                    color: "#d4d4d4"
                    font.family: interFont.name
                    font.pointSize: 10
                    padding: 5

                    background: Rectangle {
                        radius: 6
                        color: "#303030"
                    }
                }

                Row {
                    id: addTracksButtonPanel
                    width: parent.width
                    height: 30
                    spacing: 8

                    RoundButton {
                        id: downloadButton
                        width: 100
                        height: 30
                        radius: 6
                        text: "Download"
                        defaultColor: "#80005d"
                        textSize: 12
                        textStyle: Font.Bold
                    }

                    RoundButton {
                        id: resetTrackInfoButton
                        width: 145
                        height: 30
                        radius: 6
                        text: "Reset Track Info"
                        defaultColor: "#434343"
                        textSize: 12
                        textStyle: Font.Bold
                    }
                }
            }

            Item {
                id: emptySpacingItem1
                width: 10
                height: 2
            }

            RoundedImage {
                id: addTracksArtwork
                width: height
                height: parent.height
                radius: 6
                source: ""
            }

            Column {
                id: addTracksInfoContainer
                width: 270
                height: implicitHeight
                spacing: 6

                TextField {
                    id: addTracksTitleField
                    width: parent.width
                    height: 25
                    color: "#d4d4d4"
                    font.family: interFont.name
                    font.pointSize: 10
                    padding: 5
                    placeholderText: "Title"
                    placeholderTextColor: '#7f7f7f'

                    background: Rectangle {
                        radius: 6
                        color: "#303030"
                    }
                }

                Row {
                    id: addTracksArtistsPanel
                    width: parent.width
                    height: 25
                    spacing: 6

                    ScrollView {
                        id: addTracksArtistsScrollView
                        width: parent.width - addArtistButton.width - removeArtistButton.width - parent.spacing * 2
                        height: parent.height
                        contentHeight: height
                        clip: true
                        ScrollBar.horizontal.policy: ScrollBar.AsNeeded
                        ScrollBar.vertical.policy: ScrollBar.AlwaysOff
                        
                        Component.onCompleted: {
                            contentItem.boundsBehavior = Flickable.StopAtBounds;
                        }

                        Row {
                            id: addTracksArtistsContainer
                            width: addTracksArtistsScrollView.width
                            height: addTracksArtistsScrollView.height
                            spacing: 6

                            TextField {
                                id: defaultAddTracksArtistField
                                width: 70
                                height: 25
                                color: "#d4d4d4"
                                font.family: interFont.name
                                font.pointSize: 10
                                padding: 5
                                placeholderText: "Artist"
                                placeholderTextColor: '#7f7f7f'

                                background: Rectangle {
                                    radius: 6
                                    color: "#303030"
                                }
                            }
                        }
                    }

                    RoundButton {
                        id: addArtistButton
                        width: height
                        height: parent.height
                        radius: 6
                        spacing: 3
                        defaultColor: "#434343"
                        imageSource: "qrc:/staccato/src/ui/resources/plus.svg"
                        onClicked: {
                            Logic.addArtistTextField(addTracksArtistsContainer);
                        }
                    }

                    RoundButton {
                        id: removeArtistButton
                        width: height
                        height: parent.height
                        radius: 6
                        spacing: 3
                        defaultColor: "#434343"
                        imageSource: "qrc:/staccato/src/ui/resources/minus.svg"
                        onClicked: {
                            Logic.removeArtistTextField(addTracksArtistsContainer);
                        }
                    }
                }

                TextField {
                    id: addTracksAlbumField
                    width: parent.width
                    height: 25
                    color: "#d4d4d4"
                    font.family: interFont.name
                    font.pointSize: 10
                    padding: 5
                    placeholderText: "Album"
                    placeholderTextColor: '#7f7f7f'

                    background: Rectangle {
                        radius: 6
                        color: "#303030"
                    }
                }
            }
        }
    }
}