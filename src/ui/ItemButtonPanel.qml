import QtQuick

Rectangle {
    id: container
    width: 200
    height: 100
    radius: 10
    color: "#303030"

    readonly property int spacing: 8
    required property string artworkSource
    required property string name
    required property string description

    FontLoader {
        id: interFont
        source: "qrc:/staccato/src/ui/resources/Inter-VariableFont_opsz,wght.ttf"
    }

    Rectangle {
        id: artworkBackground
        anchors.left: parent.left
        anchors.verticalCenter: parent.verticalCenter
        anchors.leftMargin: parent.spacing
        width: height
        height: parent.height - parent.spacing * 2
        color: "#2b2b2b"

        Image {
            id: artwork
            anchors.fill: parent
            source: "image://audioFile/" + container.artworkSource
        }
    }

    Column {
        id: textContainer
        anchors.left: artworkBackground.right
        anchors.verticalCenter: parent.verticalCenter
        anchors.leftMargin: parent.spacing / 2
        width: parent.width - artworkBackground.width - parent.spacing * 2.5
        height: implicitHeight

        Text {
            id: nameText
            height: implicitHeight
            text: container.name
            font.family: interFont.name
            font.pointSize: 18
            font.weight: Font.Bold
            color: "#ffffff"
            verticalAlignment: Text.AlignVCenter
        }

        Text {
            id: descriptionText
            height: implicitHeight
            text: container.description
            font.family: interFont.name
            font.pointSize: 12
            font.weight: Font.Normal
            color: "#9a9a9a"
            verticalAlignment: Text.AlignTop
        }
    }
}