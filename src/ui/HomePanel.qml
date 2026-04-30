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
    property alias importStatusText: statusText.text
    property alias importURLText: urlTextField.text
    property alias previewArtworkSource: previewArtwork.source
    property alias previewTitleText: previewTitleField.text
    property alias previewArtistsContainer: artistsTextFieldRow
    property alias previewAlbumText: previewAlbumField.text
    property alias recentlyPlayedContainer: recentsContainer

    id: container
    rows: 2
    columns: 1
    rowSpacing: Style.bigSpacing

    Component.onCompleted: {
        // Logic.startup(StaccatoInterface);
    }

    //Track importer
    Column {
        id: trackImporter
        spacing: Style.medSpacing

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
            font.pointSize: Style.h1TextSize
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: Style.white
        }

        //URL textbox title text
        Text {
            id: urlTitleText
            width: parent.width
            height: implicitHeight
            text: "Location"
            font.family: Style.mainFontFamily
            font.pointSize: Style.h2TextSize
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: Style.white
        }

        //URL input text box & download button
        Row {
            id: urlInputPanel
            width: parent.width
            height: implicitHeight
            spacing: Style.smallSpacing

            //Text field to input the URL / file path
            RoundTextField {
                id: urlTextField
                width: 240
                height: Style.buttonSize
                font.pointSize: Style.normalTextSize - 1
                placeholderText: "Paste here a web URL or a file path to import audio from"
            }

            RoundButton {
                id: downloadButton
                width: 80
                height: Style.buttonSize
                radius: Style.buttonRadius
                text: "Download"
                defaultColor: Style.purple
                enabled: urlTextField.text.length > 0
            }

            RoundButton {
                id: loadPreviewButton
                width: 105
                height: Style.buttonSize
                radius: Style.buttonRadius
                text: "Load Preview"
                defaultColor: Style.gray
                enabled: urlTextField.text.length > 0
                
                onClicked: {
                    Logic.loadTrackInfo(urlTextField.text, loadPreviewButton, previewTitleField, artistsTextFieldRow, previewAlbumField, previewArtwork);
                }
            }

            Text {
                id: statusText
                width: 175
                height: Style.buttonSize
                text: ""
                font.family: Style.mainFontFamily
                font.pointSize: Style.normalTextSize
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: Style.red
                verticalAlignment: Text.AlignVCenter
            }
        }

        //Track preview title text
        Text {
            id: previewTitleText
            width: parent.width
            height: implicitHeight
            text: "Preview"
            font.family: Style.mainFontFamily
            font.pointSize: Style.h2TextSize
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: Style.white
        }

        //Track preview contents
        GridLayout {
            id: previewContainer
            rows: 3
            columns: 3
            rowSpacing: Style.smallSpacing
            columnSpacing: Style.smallSpacing
            width: parent.width * 0.8
            height: implicitHeight

            //Cover art
            RoundImage {
                id: previewArtwork
                radius: Style.buttonRadius
                source: ""

                Layout.fillHeight: true
                Layout.row: 0
                Layout.column: 0
                Layout.rowSpan: 3
                Layout.preferredWidth: Style.buttonSize * 3 + parent.rowSpacing * 2
            }

            //Text that says "Title: "
            Text {
                id: titleContainerTitleText
                text: "Title: "
                font.family: Style.mainFontFamily
                font.pointSize: Style.normalTextSize
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: Style.white

                Layout.row: 0
                Layout.column: 1
            }

            //Text field for the title
            RoundTextField {
                id: previewTitleField
                enabled: false

                Layout.row: 0
                Layout.column: 2
                Layout.preferredHeight: Style.buttonSize
                Layout.fillWidth: true;
            }

            //Text that says "Artists: "
            Text {
                id: artistsContainerTitleText
                text: "Artists: "
                font.family: Style.mainFontFamily
                font.pointSize: Style.normalTextSize
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: Style.white

                Layout.row: 1
                Layout.column: 1
            }

            //List of artists for the preview
            Row {
                id: previewArtistsContainer
                spacing: Style.tinySpacing

                Layout.row: 1
                Layout.column: 2
                Layout.preferredHeight: Style.buttonSize
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
                        spacing: Style.tinySpacing

                        RoundTextField {
                            id: defaultAddTracksArtistField
                            width: 200
                            height: Style.buttonSize
                            enabled: false
                        }
                    }
                }

                //Button to add a text field
                RoundButton {
                    id: addArtistButton
                    width: height
                    height: parent.height
                    radius: Style.buttonRadius
                    defaultColor: Style.gray
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
                    radius: Style.buttonRadius
                    defaultColor: Style.gray
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
                font.pointSize: Style.normalTextSize
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: Style.white

                Layout.row: 2
                Layout.column: 1
            }

            //Text field for the preview album name
            RoundTextField {
                id: previewAlbumField
                enabled: false

                Layout.row: 2
                Layout.column: 2
                Layout.preferredHeight: Style.buttonSize
                Layout.fillWidth: true;
            }
        }
    }

    //Recent playlists
    Column {
        id: recents
        spacing: Style.medSpacing

        Layout.row: 1
        Layout.column: 0
        Layout.fillWidth: true
        Layout.fillHeight: true

        Text {
            id: recentsText
            height: implicitHeight
            text: "Recents"
            font.family: Style.mainFontFamily
            font.pointSize: Style.h1TextSize
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: Style.white
        }

        Rectangle {
            id: recentsContainer
            width: parent.width
            height: parent.height - recentsText.height
            color: Style.background
        }

    }

    Button {
        Layout.preferredHeight: 6
        font.pointSize: 6
        onClicked: {

            console.log(urlTextField.height);

        }
        background: Rectangle {
            color: Style.red
        }
    }

}