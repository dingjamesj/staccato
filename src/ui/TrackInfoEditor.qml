import QtQuick
import QtQuick.Layouts
import QtQuick.Controls
import staccato

GridLayout {
    property alias titleText: titleField.text
    property alias artistsContainer: artistsTextFieldRow
    property alias albumText: albumField.text
    property alias artworkSource: artwork.imageSource

    property bool readOnly: false

    id: container
    rows: 3
    columns: 3
    rowSpacing: Style.smallSpacing
    columnSpacing: Style.smallSpacing

    function addArtistField() {
        let component = Qt.createComponent("RoundTextField.qml");
        component.createObject(container.artistsContainer, {
            "Layout.preferredWidth": 1,
            "Layout.fillWidth": true,
            "Layout.fillHeight": true,
            readOnly: Qt.binding(function() {
                return container.readOnly
            })
        });
    }

    function removeArtistField() {
        let artistsContainer = container.artistsContainer;
        if(artistsContainer.children.length > 0) {

            artistsContainer.children[artistsContainer.children.length - 1].destroy();

        }
    }

    //Title

    Text {
        text: "Title:"
        font.family: Style.mainFontFamily
        font.pointSize: Style.normalTextSize
        font.weight: Font.DemiBold
        wrapMode: Text.NoWrap
        color: enabled ? Style.white : Qt.darker(Style.white, 1.5)

        Layout.row: 0
        Layout.column: 0
    }

    RoundTextField {
        id: titleField
        readOnly: container.readOnly

        Layout.row: 0
        Layout.column: 1
        Layout.preferredHeight: Style.buttonSize
        Layout.fillWidth: true;
    }

    //Artists

    Text {
        text: "Artists:"
        font.family: Style.mainFontFamily
        font.pointSize: Style.normalTextSize
        font.weight: Font.DemiBold
        wrapMode: Text.NoWrap
        color: enabled ? Style.white : Qt.darker(Style.white, 1.5)

        Layout.row: 1
        Layout.column: 0
    }

    RowLayout {
        spacing: 6

        Layout.row: 1
        Layout.column: 1
        Layout.preferredHeight: Style.buttonSize
        Layout.fillWidth: true;

        ScrollView {
            id: artistsScrollView
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
                width: artistsScrollView.width
                height: artistsScrollView.height
                spacing: 6

                RoundTextField {
                    readOnly: container.readOnly

                    Layout.preferredWidth: 1
                    Layout.fillWidth: true
                    Layout.fillHeight: true
                }
            }
        }

        RoundButton {
            radius: Style.buttonRadius
            defaultColor: Style.gray
            imageSource: "qrc:/staccato/src/ui/resources/plus.svg"
            onClicked: addArtistField()
            visible: !container.readOnly
            
            Layout.preferredWidth: height
            Layout.fillHeight: true
        }

        RoundButton {
            radius: Style.buttonRadius
            defaultColor: Style.gray
            imageSource: "qrc:/staccato/src/ui/resources/minus.svg"
            onClicked: removeArtistField()
            visible: !container.readOnly
            enabled: parent.enabled && container.artistsContainer.children.length > 0

            Layout.preferredWidth: height
            Layout.fillHeight: true
        }
    }

    //Album

    Text {
        text: "Album:"
        font.family: Style.mainFontFamily
        font.pointSize: Style.normalTextSize
        font.weight: Font.DemiBold
        wrapMode: Text.NoWrap
        color: enabled ? Style.white : Qt.darker(Style.white, 1.5)

        Layout.row: 2
        Layout.column: 0
    }

    RoundTextField {
        id: albumField
        readOnly: container.readOnly

        Layout.row: 2
        Layout.column: 1
        Layout.preferredHeight: Style.buttonSize
        Layout.fillWidth: true;
    }

    //Cover art

    RoundButton {
        id: artwork
        radius: Style.buttonRadius
        imageSource: ""
        clickable: !container.readOnly
        disabledColor: Style.background

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
}