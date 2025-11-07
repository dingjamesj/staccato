import QtQuick
import QtQuick.Effects

Rectangle {
    id: container
    color: "#ff0000"

    FontLoader {
        id: interFont
        source: "qrc:/staccato/src/ui/resources/Inter-VariableFont_opsz,wght.ttf"
    }

    //=========================================
    //                COVER ART                
    //=========================================

    Rectangle {
        id: artworkBackground
        anchors.horizontalCenter: parent.horizontalCenter
        anchors.top: parent.top
        anchors.topMargin: parent.height * 8 / 165
        width: parent.width * 364 / 425
        height: parent.width * 364 / 425
        radius: 22
        color: "#ffff00"
    }

    Image {
        id: artwork
        source: "qrc:/staccato/src/ui/resources/piggo58.jpg"
        anchors.horizontalCenter: parent.horizontalCenter
        anchors.top: parent.top
        anchors.topMargin: parent.height * 8 / 165
        width: parent.width * 364 / 425
        height: parent.width * 364 / 425
        fillMode: Image.PreserveAspectFit
        visible: false
    }

    MultiEffect {
        source: artwork
        anchors.fill: artwork
        maskEnabled: true
        maskSource: mask
    }

    Item {
        id: mask
        width: artwork.width
        height: artwork.height
        layer.enabled: true
        visible: false

        Rectangle {
            width: artwork.width
            height: artwork.height
            radius: 22
            color: "black"
        }
    }

    //=========================================
    //               TRACK INFO               
    //=========================================

    Text {
        anchors.horizontalCenter: parent.horizontalCenter
        anchors.top: artworkBackground.bottom
        anchors.topMargin: parent.height * 4 / 165
        text: "Track Title"
        font.family: interFont.name
        font.pointSize: 48
        color: "#ffffff"
    }

}