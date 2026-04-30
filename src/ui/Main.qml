import QtQuick
import QtQuick.Controls.Basic
import QtQuick.Layouts
import "main.js" as Logic

pragma ComponentBehavior: Bound

ApplicationWindow {
    property int topRowHeight: 35 //Height for the row containing the search bar, settings button, etc.

    id: container
    width: 1350
    height: 810
    visible: true
    title: ""

    Component.onCompleted: {
        Logic.startup(StaccatoInterface);
    }

    Rectangle {
        anchors.fill: parent
        color: Style.background
    }

    GridLayout {
        id: mainGrid
        rows: 2
        columns: 3
        rowSpacing: 7
        columnSpacing: 10
        anchors.fill: parent

        Rectangle {
            id: topLeftCorner
            color: "#ffffff"

            Layout.row: 0
            Layout.column: 0
            Layout.preferredWidth: parent.width * 0.25
            Layout.preferredHeight: container.topRowHeight
        }

        Rectangle {
            id: searchBarContainer
            color: "#ffffff"

            Layout.row: 0
            Layout.column: 1
            Layout.preferredWidth: parent.width * 0.5
            Layout.preferredHeight: container.topRowHeight
        }

        Rectangle {
            id: topRightCorner
            color: "#ffffff"

            Layout.row: 0
            Layout.column: 2
            Layout.preferredWidth: parent.width * 0.25
            Layout.preferredHeight: container.topRowHeight
        }

        //The left panel (with the queue and playbar, essentially the controls for the media player)
        ControlPanel {
            id: queuePanel

            Layout.fillHeight: true
            Layout.row: 1
            Layout.column: 0
            Layout.preferredWidth: parent.width * 0.25
        }

        //The center panel that either shows the home menu or a playlist's tracklist
        StackView {
            id: mainPanel

            Layout.fillHeight: true
            Layout.row: 1
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
            Layout.row: 1
            Layout.column: 2
            Layout.preferredWidth: parent.width * 0.25

            initialItem: Rectangle {
                anchors.fill: parent
            }
        }
    }
}