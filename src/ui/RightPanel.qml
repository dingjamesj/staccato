import QtQuick
import QtQuick.Layouts
import QtQuick.Controls
import staccato
// import "libraryPanel.mjs" as Logic

pragma ComponentBehavior: Bound

ColumnLayout {
    id: container
    spacing: Style.medSpacing

    TabBar {
        id: tabBar
        spacing: Style.tinySpacing

        Layout.fillWidth: false
        Layout.fillHeight: false

        background: Rectangle {
            anchors.fill: parent
            color: "#00000000"
        }

        Repeater {
            model: ["Currently Playing", "Playlist Library"]

            RoundTabButton {
                required property string modelData
                text: modelData
                width: implicitWidth
            }
        }
    }

    StackLayout {
        currentIndex: tabBar.currentIndex
        width: parent.width

        Layout.leftMargin: Style.tinySpacing
        Layout.rightMargin: Style.tinySpacing

        CurrentlyPlayingPanel {}
        LibraryPanel {}
    }
}