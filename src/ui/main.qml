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
        color: "#1E1E1E"
    }

    GridLayout {
        id: mainGrid
        rows: 1
        columns: 3
        columnSpacing: 0
        anchors.fill: parent

        CurrentlyPlayingPanel {
            id: currentlyPlayingPanel
            Layout.fillHeight: true
            Layout.fillWidth: true
            Layout.preferredWidth: parent.width * 17 / 60
        }
        MainPanel {
            id: mainPanel
            Layout.fillHeight: true
            Layout.fillWidth: true
            Layout.preferredWidth: parent.width * 30 / 60
        }
        QueuePanel {
            id: queuePanel
            Layout.fillHeight: true
            Layout.fillWidth: true
            Layout.preferredWidth: parent.width * 13 / 60
        }
    }
}