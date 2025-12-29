import QtQuick

Rectangle {
    id: container

    property int spacing: 8
    required property string artworkSource
    required property string name
    required property string description
    property int nameTextSize: 18
    property int descriptionTextSize: 10
    property color originalColor: color
    property color hoverColor: Qt.lighter(originalColor, 1.2)
    property color pressedColor: Qt.lighter(originalColor, 1.5)
    signal clicked()
    signal doubleClicked()

    FontLoader {
        id: interFont
        source: "qrc:/staccato/src/ui/resources/Inter-VariableFont_opsz,wght.ttf"
    }

    MouseArea {
        id: mouseArea
        anchors.fill: parent
        hoverEnabled: true
        onClicked: parent.clicked()
        onDoubleClicked: parent.doubleClicked()
        onEntered: {
            if(!pressed) {

                container.originalColor = container.color;
                container.color = container.hoverColor;
                
            }
        }
        onExited: {
            if(!pressed) {

                container.color = container.originalColor

            }
        }
        onPressed: {
            container.color = container.pressedColor
        }
        onReleased: {
            if(containsMouse) {

                container.color = container.hoverColor
            
            } else {

                container.color = container.originalColor

            }
        }
    }

    RoundedImage {
        id: artworkBackground
        anchors.left: parent.left
        anchors.verticalCenter: parent.verticalCenter
        anchors.leftMargin: parent.spacing
        width: height
        height: parent.height - parent.spacing * 2
        radius: parent.radius
        source: container.artworkSource
    }

    Column {
        id: textContainer
        anchors.left: artworkBackground.right
        anchors.verticalCenter: parent.verticalCenter
        anchors.leftMargin: parent.spacing
        width: parent.width - artworkBackground.width - parent.spacing * 2.5
        height: implicitHeight

        Text {
            id: nameText
            width: parent.width
            height: implicitHeight
            text: container.name
            font.family: interFont.name
            font.pointSize: container.nameTextSize
            font.weight: Font.Bold
            color: "#ffffff"
            verticalAlignment: Text.AlignVCenter
            clip: true
            elide: Text.ElideRight
        }

        Text {
            id: descriptionText
            width: parent.width
            height: implicitHeight
            text: container.description
            font.family: interFont.name
            font.pointSize: container.descriptionTextSize
            font.weight: Font.Normal
            color: "#9a9a9a"
            verticalAlignment: Text.AlignTop
            clip: true
            elide: Text.ElideRight
        }
    }
}