import QtQuick
import QtQuick.Layouts
import QtQuick.Controls
import QtQuick.Window
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

    Component.onCompleted: {
        addArtistField("");
    }

    function addArtistField(_artistName) {
        artistsModel.append({
            "name": _artistName
        })
    }

    function setArtists(_artists) {
        //Overwrite or add artist fields for every artist name in _artists:
        let i = 0;
        for(; i < _artists.length; i++) {
            if(i < artistsModel.count) {
                artistsModel.setProperty(i, "name", _artists[i]);
            } else {
                container.addArtistField(_artists[i]);
            }
        }
        //Remove any excess artist fields:
        for(let j = artistsModel.count - 1; j >= i; j--) {
            artistsModel.remove(j);
        }
    }

    function removeArtistField() {
        if(artistsModel.count > 0) {
            artistsModel.remove(artistsModel.count - 1);
        }
    }

    function clearArtistFields() {
        artistsModel.clear();
    }

    ListModel {
        id: artistsModel
    }

    Instantiator {
        model: artistsModel
        delegate: RoundTextField {
            readOnly: container.readOnly
            text: model.name

            Layout.preferredWidth: 1
            Layout.fillWidth: true
            Layout.fillHeight: true
        }
        onObjectAdded: (index, object) => {
            object.parent = container.artistsContainer;
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
            }
        }

        RoundButton {
            radius: Style.buttonRadius
            defaultColor: Style.gray
            imageSource: "qrc:/staccato/src/ui/resources/plus.svg"
            onClicked: addArtistField("")
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
            Dialogs.openFileDialog(
                "Choose a file", 
                ["Image files (*.jpg *.png *.jpeg *.JPG *.PNG *.JPEG)"], 
                (file) => {
                    console.log("file was picked: " + file);
                }
            );
        }
    }
}