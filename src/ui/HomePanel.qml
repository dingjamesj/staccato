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

    property bool previewIsLoading: false
    property bool previewIsLoaded: false

    id: container
    rows: 8
    columns: 1
    rowSpacing: 0

    Component.onCompleted: {
        Logic.startup(StaccatoInterface);
    }

    Item {
        Layout.row: 0
        Layout.preferredHeight: Style.medSpacing
    }

    //Title text
    Text {
        id: trackImporterTitle
        text: "Add New Tracks"
        font.family: Style.mainFontFamily
        font.pointSize: Style.h1TextSize
        font.weight: Font.DemiBold
        wrapMode: Text.NoWrap
        color: Style.white

        Layout.row: 1
        Layout.fillWidth: true
        Layout.preferredHeight: implicitHeight
    }

    Item {
        Layout.row: 2
        Layout.preferredHeight: Style.medSpacing
    }

    //Track importer
    Column {
        id: trackImporter
        spacing: Style.smallSpacing

        Layout.row: 3
        Layout.fillWidth: true
        Layout.fillHeight: false

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
        RowLayout {
            id: urlInputPanel
            width: parent.width
            height: implicitHeight
            spacing: Style.smallSpacing

            //Text field to input the URL / file path
            RoundTextField {
                id: urlTextField
                font.pointSize: Style.normalTextSize - 1
                placeholderText: "Paste a web URL or a file path"

                Layout.preferredWidth: 3
                Layout.fillWidth: true
                Layout.fillHeight: true
            }

            RoundButton {
                radius: Style.buttonRadius
                text: "Download"
                defaultColor: Style.purple
                enabled: urlTextField.text.length > 0

                Layout.preferredWidth: 80
                Layout.fillHeight: true
            }

            RoundButton {
                radius: Style.buttonRadius
                text: "Load Preview"
                defaultColor: Style.gray
                enabled: previewIsLoading ? false : (urlTextField.text.length > 0)
                
                Layout.preferredWidth: 105
                Layout.fillHeight: true

                onClicked: {
                    Logic.loadTrackInfo(container);
                }
            }

            Text {
                id: statusText
                text: ""
                font.family: Style.mainFontFamily
                font.pointSize: Style.normalTextSize
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: Style.red
                verticalAlignment: Text.AlignVCenter

                Layout.preferredWidth: 1
                Layout.fillWidth: true
                Layout.fillHeight: true
            }
        }

        //Track preview title text
        Text {
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
                enabled: container.previewIsLoaded

                Layout.row: 0
                Layout.column: 2
                Layout.preferredHeight: Style.buttonSize
                Layout.fillWidth: true;
            }

            //Text that says "Artists: "
            Text {
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
            RowLayout {
                spacing: Style.tinySpacing

                Layout.row: 1
                Layout.column: 2
                Layout.preferredHeight: Style.buttonSize
                Layout.fillWidth: true;

                ScrollView {
                    id: previewArtistsScrollView
                    contentHeight: height
                    clip: true

                    ScrollBar.horizontal.policy: ScrollBar.AsNeeded
                    ScrollBar.vertical.policy: ScrollBar.AlwaysOff

                    Layout.fillWidth: true
                    Layout.fillHeight: true
                    
                    Component.onCompleted: {
                        contentItem.boundsBehavior = Flickable.StopAtBounds;
                    }

                    RowLayout {
                        id: artistsTextFieldRow
                        width: previewArtistsScrollView.width
                        height: previewArtistsScrollView.height
                        spacing: Style.tinySpacing

                        RoundTextField {
                            id: initialArtistPreviewField
                            enabled: container.previewIsLoaded

                            Layout.preferredWidth: 1
                            Layout.fillWidth: true
                            Layout.fillHeight: true
                        }
                    }
                }

                //Button to add a text field
                RoundButton {
                    radius: Style.buttonRadius
                    defaultColor: Style.gray
                    imageSource: "qrc:/staccato/src/ui/resources/plus.svg"
                    onClicked: {
                        Logic.addArtistTextField(container);
                    }
                    enabled: container.previewIsLoaded
                 
                    Layout.preferredWidth: height
                    Layout.fillHeight: true
                }

                //Button to remove the last text field
                RoundButton {
                    radius: Style.buttonRadius
                    defaultColor: Style.gray
                    imageSource: "qrc:/staccato/src/ui/resources/minus.svg"
                    onClicked: {
                        Logic.removeArtistTextField(container);
                    }
                    enabled: container.previewIsLoaded ? (container.previewArtistsContainer.children.length > 0 ? true : false) : false

                    Layout.preferredWidth: height
                    Layout.fillHeight: true
                }
            }

            //Text that just says "Album: "
            Text {
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
                enabled: container.previewIsLoaded

                Layout.row: 2
                Layout.column: 2
                Layout.preferredHeight: Style.buttonSize
                Layout.fillWidth: true;
            }
        }
    }

    Item {
        Layout.row: 4
        Layout.preferredHeight: Style.bigSpacing
    }

    Text {
        text: "Recents"
        font.family: Style.mainFontFamily
        font.pointSize: Style.h1TextSize
        font.weight: Font.DemiBold
        wrapMode: Text.NoWrap
        color: Style.white

        Layout.row: 5
        Layout.fillWidth: true
        Layout.preferredHeight: implicitHeight
    }

    Item {
        Layout.row: 6
        Layout.preferredHeight: Style.medSpacing
    }

    //Recent playlists
    Rectangle {
        color: Style.background

        Layout.row: 7
        Layout.fillWidth: true
        Layout.fillHeight: true

        Column {
            id: recentsContainer
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
        text: "HomePanel DEBUG"
    }

}