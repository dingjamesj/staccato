import QtQuick
import QtQuick.Controls.Basic

Rectangle {
    id: container
    topLeftRadius: width / 7
    topRightRadius: width / 7
    color: "#93006b"

    FontLoader {
        id: interFont
        source: "qrc:/staccato/src/ui/resources/Inter-VariableFont_opsz,wght.ttf"
    }

    Rectangle {
        id: controlsPanel
        width: parent.width * 0.84
        height: parent.height * 0.38
        anchors.top: parent.top
        anchors.topMargin: parent.width * 0.08
        anchors.horizontalCenter: parent.horizontalCenter
        radius: height / 2
        color: "#80005d"

        Row {
            id: buttons
            width: parent.width
            height: parent.height
            leftPadding: parent.width * 0.03
            rightPadding: parent.width * 0.03

            Button {
                id: loopButton
                width: parent.width * 0.2 * 0.94
                height: parent.height * 0.7
                anchors.verticalCenter: parent.verticalCenter
                background: Rectangle {
                    color: "transparent"
                }
                icon.color: "#ffffff"
                icon.source: "qrc:/staccato/src/ui/resources/loop.svg"
                icon.width: parent.width
                icon.height: parent.height
            }

            Button {
                id: rewindButton
                width: parent.width * 0.2 * 0.94
                height: parent.height * 0.7
                anchors.verticalCenter: parent.verticalCenter
                background: Rectangle {
                    color: "transparent"
                }
                icon.color: "#ffffff"
                icon.source: "qrc:/staccato/src/ui/resources/rewind.svg"
                icon.width: parent.width
                icon.height: parent.height
            }

            Button {
                id: playButton
                width: parent.width * 0.2 * 0.94
                height: parent.height * 0.85
                anchors.verticalCenter: parent.verticalCenter
                background: Rectangle {
                    color: "transparent"
                }
                icon.color: "#ffffff"
                icon.source: "qrc:/staccato/src/ui/resources/play.svg"
                icon.width: parent.width
                icon.height: parent.height
            }

            Button {
                id: skipButton
                width: parent.width * 0.2 * 0.94
                height: parent.height * 0.7
                anchors.verticalCenter: parent.verticalCenter
                background: Rectangle {
                    color: "transparent"
                }
                icon.color: "#ffffff"
                icon.source: "qrc:/staccato/src/ui/resources/skip.svg"
                icon.width: parent.width
                icon.height: parent.height
            }

            Button {
                id: shuffleButton
                width: parent.width * 0.2 * 0.94
                height: parent.height * 0.7
                anchors.verticalCenter: parent.verticalCenter
                background: Rectangle {
                    color: "transparent"
                }
                icon.color: "#ffffff"
                icon.source: "qrc:/staccato/src/ui/resources/shuffle.svg"
                icon.width: parent.width
                icon.height: parent.height
            }
        }
    }

    Column {
        id: progressBarPanel
        width: parent.width * 0.84
        height: parent.height * 0.25
        anchors.bottom: parent.bottom
        anchors.bottomMargin: 8
        anchors.horizontalCenter: parent.horizontalCenter
        spacing: 12

        Item {
            id: timestamps
            width: progressBarPanel.width
            height: 10

            Text {
                id: timeElapsedText
                width: 20
                anchors.left: parent.left
                text: "24:00:00"
                horizontalAlignment: Text.AlignLeft
                font.family: interFont.name
                font.pointSize: 9
                font.weight: Font.Medium
                color: "#ffffff"
            }

            Text {
                id: timeRemainingText
                width: 20
                anchors.right: parent.right
                text: "-24:00:00"
                horizontalAlignment: Text.AlignRight
                font.family: interFont.name
                font.pointSize: 9
                font.weight: Font.Medium
                color: "#ffffff"
            }
        }

        Slider {
            id: progressBar
            width: parent.width
            height: 8
            value: 1
            leftPadding: 0
            rightPadding: 0
            background: Rectangle {
                id: progressBarBackground
                x: progressBar.leftPadding
                y: progressBar.topPadding + progressBar.availableHeight / 2 - height / 2
                width: progressBar.availableWidth
                height: parent.height
                radius: height / 2
                color: "#80005d"

                Rectangle {
                    id: progressBarFilling
                    width: progressBar.visualPosition * parent.width
                    height: parent.height
                    radius: height / 2
                    color: "#ffb9ff"
                }
            }
            handle: Rectangle {
                id: progressBarHandle
                x: progressBar.leftPadding + progressBar.visualPosition * (progressBar.availableWidth - width)
                y: progressBar.topPadding + progressBar.availableHeight / 2 - height / 2
                width: 16
                height: width
                radius: width / 2
                color: "#ffffff"
                visible: progressBar.hovered | progressBar.pressed
            }
        }
    }
}