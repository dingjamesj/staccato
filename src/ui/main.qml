import QtQuick
import QtQuick.Controls.Basic
import QtQuick.Layouts
import "main.js" as Logic

ApplicationWindow {
    width: 1350
    height: 810
    visible: true
    title: ""

    Component.onCompleted: {
        Logic.startup();
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

        CurrentlyPlayingPanel {
            id: currentlyPlayingPanel
            Layout.fillHeight: true
            Layout.fillWidth: true
            Layout.row: 0
            Layout.column: 0
            Layout.preferredWidth: 380
            Layout.preferredHeight: parent.height * 0.82
        }

        Item {
            Layout.fillHeight: true
            Layout.fillWidth: true
            Layout.row: 1
            Layout.column: 0
            Layout.preferredWidth: 380
            Layout.preferredHeight: parent.height * 0.18

            ControlBarPanel {
                id: controlBarPanel
                anchors.centerIn: parent
                width: parent.width * 75 / 85
                height: parent.height
            }
        }

        StackView {
            id: mainPanel
            Layout.fillHeight: true
            Layout.fillWidth: true
            Layout.column: 1
            Layout.rowSpan: 2
            Layout.preferredWidth: parent.width - 380 - 290

            initialItem: HomePanel {
                anchors.fill: parent
            }
        }

        QueuePanel {
            id: queuePanel
            Layout.fillHeight: true
            Layout.fillWidth: true
            Layout.column: 2
            Layout.rowSpan: 2
            Layout.preferredWidth: 290
        }
    }
}