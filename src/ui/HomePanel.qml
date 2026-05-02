import QtQuick
import QtQuick.Controls
import QtQuick.Layouts
import staccato

pragma ComponentBehavior: Bound

//===============================================
// Shows the track importer and recent playlists 
//===============================================

GridLayout {
    property alias recentlyPlayedContainer: recentsContainer

    id: container
    rows: 8
    columns: 1
    rowSpacing: 0

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
    ColumnLayout {
        spacing: Style.smallSpacing

        Layout.row: 3
        Layout.fillWidth: true
        Layout.fillHeight: false

        TabBar {
            id: trackImportTabBar
            Layout.fillWidth: false
            Layout.fillHeight: false

            TabButton {
                text: "URL"
            }

            TabButton {
                text: "Metadata"
            }
        }

        StackLayout {
            currentIndex: trackImportTabBar.currentIndex

            Layout.fillWidth: true
            Layout.fillHeight: true

            URLImportPanel {}
            MetadataImportPanel {}
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