import QtQuick
import QtQuick.Controls
import QtQuick.Layouts

ApplicationWindow {
    width: 1350
    height: 810
    visible: true
    title: ""

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

        CurrentlyPlayingPanel {
            id: currentlyPlayingPanel
            Layout.fillHeight: true
            Layout.fillWidth: true
            Layout.row: 0
            Layout.column: 0
            Layout.preferredWidth: parent.width * 17 / 60
            Layout.preferredHeight: parent.height * 21 / 25
        }
        Item {
            Layout.fillHeight: true
            Layout.fillWidth: true
            Layout.row: 1
            Layout.column: 0
            Layout.preferredWidth: parent.width * 17 / 60
            Layout.preferredHeight: parent.height * 4 / 25

            ControlBarPanel {
                id: controlBarPanel
                anchors.centerIn: parent
                width: parent.width * 75 / 85
                height: parent.height
            }
        }
        MainPanel {
            id: mainPanel
            Layout.fillHeight: true
            Layout.fillWidth: true
            Layout.column: 1
            Layout.rowSpan: 2
            Layout.preferredWidth: parent.width * 30 / 60
        }
        QueuePanel {
            id: queuePanel
            Layout.fillHeight: true
            Layout.fillWidth: true
            Layout.column: 2
            Layout.rowSpan: 2
            Layout.preferredWidth: parent.width * 13 / 60
        }
    }
}