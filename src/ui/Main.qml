import QtQuick
import QtQuick.Controls.Basic
import QtQuick.Layouts
import "main.js" as Logic

pragma ComponentBehavior: Bound

ApplicationWindow {
    width: 1350
    height: 810
    visible: true
    title: ""

    Component.onCompleted: {
        Logic.startup(StaccatoInterface);
    }

    Rectangle {
        anchors.fill: parent
        color: "#1e1e1e"
    }

    GridLayout {
        id: mainGrid
        rows: 2
        columns: 3
        columnSpacing: 0
        anchors.fill: parent

        //The left panel (with the queue and playbar, essentially the controls for the media player)
        ControlPanel {
            id: queuePanel
            Layout.fillHeight: true
            Layout.fillWidth: true
            Layout.column: 0
            Layout.preferredWidth: parent.width * 0.25
        }

        //The center panel that either shows the home menu or a playlist's tracklist
        StackView {
            id: mainPanel
            Layout.fillHeight: true
            Layout.fillWidth: true
            Layout.column: 1
            Layout.preferredWidth: parent.width * 0.5

            //The center panel shows the home panel on startup
            initialItem: HomePanel {
                anchors.fill: parent
            }
        }

        //The right panel
        StackView {
            id: currentlyPlayingAndLibraryPanel
            Layout.fillHeight: true
            Layout.fillWidth: true
            Layout.column: 2
            Layout.preferredWidth: parent.width * 0.25

            initialItem: Rectangle {
                anchors.fill: parent
            }
        }
    }
}