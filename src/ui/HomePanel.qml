import QtQuick
import QtQuick.Controls
import QtQuick.Layouts
import staccato

pragma ComponentBehavior: Bound

//===============================================
// Shows the track importer and recent playlists 
//===============================================

ColumnLayout {
    property alias recentlyPlayedContainer: recentsContainer

    id: container
    spacing: 0

    Text {
        text: "Recents"
        font.family: Style.mainFontFamily
        font.pointSize: Style.h1TextSize
        font.weight: Font.DemiBold
        wrapMode: Text.NoWrap
        color: Style.white

        Layout.fillWidth: true
        Layout.preferredHeight: implicitHeight
        Layout.topMargin: Style.medSpacing
    }

    //Recent playlists
    Rectangle {
        color: Style.background

        Layout.fillWidth: true
        Layout.fillHeight: true
        Layout.topMargin: Style.medSpacing

        Column {
            id: recentsContainer
        }
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

        Layout.fillWidth: true
        Layout.preferredHeight: implicitHeight
        Layout.topMargin: Style.bigSpacing
    }

    //Track importer
    ColumnLayout {
        spacing: Style.smallSpacing

        Layout.fillWidth: true
        Layout.fillHeight: false
        Layout.topMargin: Style.medSpacing
        Layout.bottomMargin: Style.medSpacing

        TabBar {
            id: trackImportTabBar
            spacing: Style.tinySpacing

            Layout.fillWidth: false
            Layout.fillHeight: false

            background: Rectangle {
                anchors.fill: parent
                color: "#00000000"
            }

            Repeater {
                model: ["URL", "Search"]

                RoundTabButton {
                    required property string modelData
                    text: modelData
                    width: implicitWidth
                }
            }
        }

        StackLayout {
            currentIndex: trackImportTabBar.currentIndex

            Layout.fillWidth: true
            Layout.fillHeight: true

            URLImportPanel {}
            SearchQueryImportPanel {}
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