import QtQuick
import QtQuick.Controls.Basic
import QtQuick.Layouts
import staccato
import "homePanel.js" as Logic

pragma ComponentBehavior: Bound

//===============================================
// Shows the track importer and recent playlists 
//===============================================

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

    //Track importer
    Column {
        id: trackImporter
        width: parent.width - parent.leftPadding - parent.rightPadding
        height: implicitHeight
        spacing: 12
        leftPadding: 0
        rightPadding: 0
        topPadding: 0
        bottomPadding: 0
        
        //Title text
        Text {
            id: addTracksText
            height: 39
            text: "Add New Tracks"
            font.family: interFont.name
            font.pointSize: 24
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: "#ffffff"
        }

        //URL textbox title text
        Text {
            id: urlTitleText
            width: parent.width
            height: 20
            text: "Location"
            font.family: interFont.name
            font.pointSize: 13
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: "#ffffff"
        }

        //URL input text box & download button
        Row {
            id: urlInputPanel
            width: parent.width
            height: implicitHeight
            spacing: 8

            //Text field to input the URL / file path
            TextField {
                id: urlTextField
                width: 240
                height: 25
                color: "#d4d4d4"
                font.family: interFont.name
                font.pointSize: 9
                padding: 5
                placeholderText: "Paste here a web URL or a file path to import audio from"
                placeholderTextColor: '#7f7f7f'

                background: Rectangle {
                    radius: 6
                    color: "#303030"
                }
            }

            RoundButton {
                id: downloadButton
                width: 80
                height: 25
                radius: 6
                text: "Download"
                defaultColor: "#80005d"
                textSize: 10
                textStyle: Font.Bold
                enabled: false
            }

            RoundButton {
                id: loadPreviewButton
                width: 105
                height: 25
                radius: 6
                text: "Load Preview"
                defaultColor: "#434343"
                textSize: 10
                textStyle: Font.Bold
                enabled: false
                
                onClicked: {
                    Logic.loadTrackInfo(urlTextField.text, loadPreviewButton, previewTitleField, artistsTextFieldRow, previewAlbumField, previewArtwork);
                }
            }
        }

        //Track preview title text
        Text {
            id: previewTitleText
            width: parent.width
            height: 20
            text: "Preview"
            font.family: interFont.name
            font.pointSize: 13
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: "#ffffff"
        }

        //Track preview contents
        GridLayout {
            id: previewContainer
            rows: 3
            columns: 3
            rowSpacing: 6
            columnSpacing: 8
            width: parent.width * 0.8
            height: implicitHeight

            //Cover art
            RoundedImage {
                id: previewArtwork
                radius: 6
                source: ""

                Layout.fillHeight: true
                Layout.row: 0
                Layout.column: 0
                Layout.rowSpan: 3
                Layout.preferredWidth: 25 * 3 + parent.rowSpacing * 2
            }

            //Text that says "Title: "
            Text {
                id: titleContainerTitleText
                text: "Title: "
                font.family: interFont.name
                font.pointSize: 10
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: "#ffffff"

                Layout.row: 0
                Layout.column: 1
            }

            //Text field for the title
            TextField {
                id: previewTitleField
                color: "#d4d4d4"
                font.family: interFont.name
                font.pointSize: 10
                enabled: false

                background: Rectangle {
                    radius: 6
                    color: "#303030"
                }

                Layout.row: 0
                Layout.column: 2
                Layout.preferredHeight: 25
                Layout.fillWidth: true;
            }

            //Text that says "Artists: "
            Text {
                id: artistsContainerTitleText
                text: "Artists: "
                font.family: interFont.name
                font.pointSize: 10
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: "#ffffff"

                Layout.row: 1
                Layout.column: 1
            }

            //List of artists for the preview
            Row {
                id: previewArtistsContainer
                spacing: 6

                Layout.row: 1
                Layout.column: 2
                Layout.preferredHeight: 25
                Layout.fillWidth: true;

                ScrollView {
                    id: previewArtistsScrollView
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
                        id: artistsTextFieldRow
                        width: previewArtistsScrollView.width
                        height: previewArtistsScrollView.height
                        spacing: 6

                        TextField {
                            id: defaultAddTracksArtistField
                            width: 200
                            height: 25
                            color: "#d4d4d4"
                            font.family: interFont.name
                            font.pointSize: 10
                            padding: 5
                            enabled: false

                            background: Rectangle {
                                radius: 6
                                color: "#303030"
                            }
                        }
                    }
                }

                //Button to add a text field
                RoundButton {
                    id: addArtistButton
                    width: height
                    height: parent.height
                    radius: 6
                    spacing: 3
                    defaultColor: "#434343"
                    imageSource: "qrc:/staccato/src/ui/resources/plus.svg"
                    onClicked: {
                        Logic.addArtistTextField(artistsTextFieldRow);
                    }
                    enabled: false
                }

                //Button to remove the last text field
                RoundButton {
                    id: removeArtistButton
                    width: height
                    height: parent.height
                    radius: 6
                    spacing: 3
                    defaultColor: "#434343"
                    imageSource: "qrc:/staccato/src/ui/resources/minus.svg"
                    onClicked: {
                        Logic.removeArtistTextField(artistsTextFieldRow);
                    }
                    enabled: true
                }
            }

            //Text that just says "Album: "
            Text {
                id: albumContainerTitleText
                text: "Album: "
                font.family: interFont.name
                font.pointSize: 10
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: "#ffffff"

                Layout.row: 2
                Layout.column: 1
            }

            //Text field for the preview album name
            TextField {
                id: previewAlbumField
                color: "#d4d4d4"
                font.family: interFont.name
                font.pointSize: 10
                padding: 5
                enabled: false

                background: Rectangle {
                    radius: 6
                    color: "#303030"
                }

                Layout.row: 2
                Layout.column: 2
                Layout.preferredHeight: 25
                Layout.fillWidth: true;
            }
        }
    }

    //Recent playlists

}