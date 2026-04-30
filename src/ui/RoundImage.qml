import QtQuick
import QtQuick.Effects

pragma ComponentBehavior: Bound

Rectangle {
    id: container
    color: Style.lightBackground
    required property string source

    Image {
        id: artwork
        anchors.fill: parent
        visible: false
        source: parent.source
        fillMode: Image.PreserveAspectFit
    }

    MultiEffect {
        anchors.fill: artwork
        source: artwork
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
            radius: container.radius
            color: "#000000"
        }
    }
}