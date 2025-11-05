import QtQuick
import QtQuick.Effects

Rectangle {
    id: container
    color: "#ff0000"

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
        source: "file:///D:/car/nonidiots/piggo58.jpg"
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
}