import QtQuick
import QtQuick.Controls.Basic
import QtQuick.Layouts
import staccato
import "homePanel.js" as Logic

pragma ComponentBehavior: Bound

//===============================================
// Shows the track importer and recent playlists 
//===============================================

GridLayout {
    property int h1TextSize: 24
    property int h2TextSize: 13
    property int normalTextSize: 10

    id: container
    rows: 2
    columns: 1
    rowSpacing: 30
    columnSpacing: 0

    Component.onCompleted: {
        // Logic.startup(StaccatoInterface);
    }

    //Track importer
    Column {
        property int componentHeight: 25

        id: trackImporter
        spacing: 12

        Layout.row: 0
        Layout.column: 0
        Layout.fillWidth: true
        Layout.fillHeight: false
        
        //Title text
        Text {
            id: addTracksText
            height: implicitHeight
            text: "Add New Tracks"
            font.family: Style.mainFontFamily
            font.pointSize: container.h1TextSize
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: "#ffffff"
        }

        //URL textbox title text
        Text {
            id: urlTitleText
            width: parent.width
            height: implicitHeight
            text: "Location"
            font.family: Style.mainFontFamily
            font.pointSize: container.h2TextSize
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
            RoundTextField {
                id: urlTextField
                width: 240
                height: trackImporter.componentHeight
                font.pointSize: container.normalTextSize - 1
                placeholderText: "Paste here a web URL or a file path to import audio from"
            }

            RoundButton {
                id: downloadButton
                width: 80
                height: trackImporter.componentHeight
                radius: 6
                text: "Download"
                defaultColor: "#80005d"
                textSize: container.normalTextSize
                textStyle: Font.Bold
                enabled: false
            }

            RoundButton {
                id: loadPreviewButton
                width: 105
                height: trackImporter.componentHeight
                radius: 6
                text: "Load Preview"
                defaultColor: "#434343"
                textSize: container.normalTextSize
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
            height: implicitHeight
            text: "Preview"
            font.family: Style.mainFontFamily
            font.pointSize: container.h2TextSize
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
            RoundImage {
                id: previewArtwork
                radius: 6
                source: ""

                Layout.fillHeight: true
                Layout.row: 0
                Layout.column: 0
                Layout.rowSpan: 3
                Layout.preferredWidth: trackImporter.componentHeight * 3 + parent.rowSpacing * 2
            }

            //Text that says "Title: "
            Text {
                id: titleContainerTitleText
                text: "Title: "
                font.family: Style.mainFontFamily
                font.pointSize: container.normalTextSize
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: "#ffffff"

                Layout.row: 0
                Layout.column: 1
            }

            //Text field for the title
            RoundTextField {
                id: previewTitleField
                enabled: false

                Layout.row: 0
                Layout.column: 2
                Layout.preferredHeight: trackImporter.componentHeight
                Layout.fillWidth: true;
            }

            //Text that says "Artists: "
            Text {
                id: artistsContainerTitleText
                text: "Artists: "
                font.family: Style.mainFontFamily
                font.pointSize: container.normalTextSize
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
                Layout.preferredHeight: trackImporter.componentHeight
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

                        RoundTextField {
                            id: defaultAddTracksArtistField
                            width: 200
                            height: trackImporter.componentHeight
                            enabled: false
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
                    enabled: false
                }
            }

            //Text that just says "Album: "
            Text {
                id: albumContainerTitleText
                text: "Album: "
                font.family: Style.mainFontFamily
                font.pointSize: container.normalTextSize
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: "#ffffff"

                Layout.row: 2
                Layout.column: 1
            }

            //Text field for the preview album name
            RoundTextField {
                id: previewAlbumField
                enabled: false

                Layout.row: 2
                Layout.column: 2
                Layout.preferredHeight: trackImporter.componentHeight
                Layout.fillWidth: true;
            }
        }
    }

    //Recent playlists
    Column {
        id: recents
        spacing: 12

        Layout.row: 1
        Layout.column: 0
        Layout.fillWidth: true
        Layout.fillHeight: true

        Text {
            id: recentsText
            height: implicitHeight
            text: "Recents"
            font.family: Style.mainFontFamily
            font.pointSize: container.h1TextSize
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: "#ffffff"
        }

        // Rectangle {
        //     id: recentsContainer
        //     width: parent.width
        //     height: parent.height - recentsText.height
        //     color: "#ff0000"
        // }

    }

    Button {
        Layout.preferredHeight: 6
        font.pointSize: 6
        onClicked: {

            console.log(urlTextField.height);

        }
        background: Rectangle {
            color: "#ff0000"
        }
    }

}