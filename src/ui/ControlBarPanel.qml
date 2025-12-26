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

    //=========================================
    //               CONTROL BAR               
    //=========================================

    Rectangle {
        id: controlsPanel
        anchors.top: parent.top
        anchors.topMargin: parent.width * 0.08
        anchors.horizontalCenter: parent.horizontalCenter
        width: parent.width * 0.84
        height: parent.height * 0.38
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
                anchors.verticalCenter: parent.verticalCenter
                width: parent.width * 0.2 * 0.94
                height: parent.height * 0.7
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
                anchors.verticalCenter: parent.verticalCenter
                width: parent.width * 0.2 * 0.94
                height: parent.height * 0.7
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
                anchors.verticalCenter: parent.verticalCenter
                width: parent.width * 0.2 * 0.94
                height: parent.height * 0.85
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
                anchors.verticalCenter: parent.verticalCenter
                width: parent.width * 0.2 * 0.94
                height: parent.height * 0.7
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
                anchors.verticalCenter: parent.verticalCenter
                width: parent.width * 0.2 * 0.94
                height: parent.height * 0.7
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

    //=========================================
    //             PROGRESS SLIDER             
    //=========================================

    Column {
        id: progressSliderPanel
        anchors.bottom: parent.bottom
        anchors.bottomMargin: 8
        anchors.horizontalCenter: parent.horizontalCenter
        width: parent.width * 0.84
        height: parent.height * 0.25
        spacing: 12

        Item {
            id: timestamps
            width: progressSliderPanel.width
            height: 10

            Text {
                id: timeElapsedText
                anchors.left: parent.left
                width: 20
                text: "24:00:00"
                horizontalAlignment: Text.AlignLeft
                font.family: interFont.name
                font.pointSize: 9
                font.weight: Font.Medium
                color: "#ffffff"
            }

            Text {
                id: timeRemainingText
                anchors.right: parent.right
                width: 20
                text: "-24:00:00"
                horizontalAlignment: Text.AlignRight
                font.family: interFont.name
                font.pointSize: 9
                font.weight: Font.Medium
                color: "#ffffff"
            }
        }

        Slider {
            id: progressSlider
            width: parent.width
            height: 8
            value: 1
            leftPadding: 0
            rightPadding: 0
            background: Rectangle {
                id: progressBarBackground
                x: progressSlider.leftPadding
                y: progressSlider.topPadding + progressSlider.availableHeight / 2 - height / 2
                width: progressSlider.availableWidth
                height: parent.height
                radius: height / 2
                color: "#80005d"

                Rectangle {
                    id: progressBarFilling
                    width: progressSlider.visualPosition * parent.width
                    height: parent.height
                    radius: height / 2
                    color: "#ffb9ff"
                }
            }
            handle: Rectangle {
                id: progressBarHandle
                x: progressSlider.leftPadding + progressSlider.visualPosition * (progressSlider.availableWidth - width)
                y: progressSlider.topPadding + progressSlider.availableHeight / 2 - height / 2
                width: 16
                height: width
                radius: width / 2
                color: "#ffffff"
                visible: progressSlider.hovered | progressSlider.pressed
            }
        }
    }
}