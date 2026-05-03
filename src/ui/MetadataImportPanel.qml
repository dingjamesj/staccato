import QtQuick
import QtQuick.Controls
import QtQuick.Layouts
import staccato
import "trackImporter.js" as Logic

Column {
    property alias titleText: titlefield.text
    property alias artistsContainer: artistsTextFieldRow
    property alias albumText: albumField.text

    property int previewMetadataFieldsMaxWidth: 400

    id: container
    spacing: Style.medSpacing

    Component.onCompleted: {
        Logic.startup(StaccatoInterface);
    }

    GridLayout {
        rows: 3
        columns: 4
        rowSpacing: Style.smallSpacing
        columnSpacing: Style.smallSpacing
        width: parent.width
        height: implicitHeight

        //Text that says "Title: "
        Text {
            text: "Title: "
            font.family: Style.mainFontFamily
            font.pointSize: Style.normalTextSize
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: enabled ? Style.white : Qt.darker(Style.white, 1.5)

            Layout.row: 0
            Layout.column: 0
        }

        //Text field for the title
        RoundTextField {
            id: titlefield

            Layout.row: 0
            Layout.column: 1
            Layout.preferredHeight: Style.buttonSize
            Layout.fillWidth: true;
            Layout.maximumWidth: container.previewMetadataFieldsMaxWidth
        }

        //Text that says "Artists: "
        Text {
            text: "Artists: "
            font.family: Style.mainFontFamily
            font.pointSize: Style.normalTextSize
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: enabled ? Style.white : Qt.darker(Style.white, 1.5)

            Layout.row: 1
            Layout.column: 0
        }

        //List of artists for the preview
        RowLayout {
            spacing: Style.tinySpacing

            Layout.row: 1
            Layout.column: 1
            Layout.preferredHeight: Style.buttonSize
            Layout.fillWidth: true;
            Layout.maximumWidth: container.previewMetadataFieldsMaxWidth

            ScrollView {
                id: previewArtistsScrollView
                contentHeight: height
                clip: true

                ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
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
                    Logic.addArtistTextFieldForMetadataImporter(container);
                }
                
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
                enabled: parent.enabled && container.artistsContainer.children.length > 0

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
            color: enabled ? Style.white : Qt.darker(Style.white, 1.5)

            Layout.row: 2
            Layout.column: 0
        }

        //Text field for the preview album name
        RoundTextField {
            id: albumField

            Layout.row: 2
            Layout.column: 1
            Layout.preferredHeight: Style.buttonSize
            Layout.fillWidth: true;
            Layout.maximumWidth: container.previewMetadataFieldsMaxWidth
        }

        //Cover art
        RoundButton {
            id: previewArtwork
            radius: Style.buttonRadius
            imageSource: ""

            Layout.fillHeight: true
            Layout.row: 0
            Layout.column: 2
            Layout.rowSpan: 3
            Layout.preferredWidth: height
            Layout.leftMargin: Style.smallSpacing

            onClicked: {
                FilePicker.open(["Image files (*.jpg, *.png, *.jpeg)"]);
            }
        }

        //Empty component to shove everything to the left
        Item {
            Layout.row: 0
            Layout.column: 3
            Layout.rowSpan: 3
            Layout.fillWidth: true
        }
    }
    
    RoundButton {
        width: 60
        height: Style.buttonSize
        radius: Style.buttonRadius
        text: "Search"
        defaultColor: Style.purple
        enabled: titleText.length > 0
    }
}