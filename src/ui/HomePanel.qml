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
        Layout.preferredHeight: 1
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
        Layout.fillHeight: true
        Layout.preferredHeight: 1
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

        ScrollView {
            clip: true
            
            ScrollBar.vertical.policy: ScrollBar.AsNeeded
            ScrollBar.horizontal.policy: ScrollBar.AlwaysOff

            Layout.fillWidth: true
            Layout.fillHeight: true

            Component.onCompleted: {
                contentItem.boundsBehavior = Flickable.StopAtBounds;
            }

            StackLayout {
                id: importPanels
                currentIndex: trackImportTabBar.currentIndex
                width: parent.width
                height: currentItem ? currentItem.implicitHeight : 0

                URLImportPanel {}
                SearchQueryImportPanel {}
            }
        }
    }
}