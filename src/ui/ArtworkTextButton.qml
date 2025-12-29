import QtQuick

Rectangle {
    id: container

    required property string artworkSource
    required property string name
    required property string description
    property color defaultColor: "#303030"
    property color hoverColor: Qt.lighter(defaultColor, 1.2)
    property color pressedColor: Qt.lighter(defaultColor, 1.5)
    property color nameTextColor: "#ffffff"
    property color descriptionTextColor: "#9a9a9a"
    property int nameTextSize: 18
    property int descriptionTextSize: 10
    property var nameTextStyle: Font.Bold
    property var descriptionTextStyle: Font.Normal
    property int spacing: 8
    
    signal clicked()
    signal doubleClicked()

    Component.onCompleted: {
        color = defaultColor
    }

    FontLoader {
        id: interFont
        source: "qrc:/staccato/src/ui/resources/Inter-VariableFont_opsz,wght.ttf"
    }

    MouseArea {
        id: mouseArea
        anchors.fill: parent
        hoverEnabled: true
        onClicked: container.clicked()
        onDoubleClicked: container.doubleClicked()
        onEntered: {
            if(!pressed) {

                container.color = container.hoverColor;
                
            }
        }
        onExited: {
            if(!pressed) {

                container.color = container.defaultColor

            }
        }
        onPressed: {
            container.color = container.pressedColor
        }
        onReleased: {
            if(containsMouse) {

                container.color = container.hoverColor
            
            } else {

                container.color = container.defaultColor

            }
        }
    }

    RoundedImage {
        id: artworkBackground
        anchors.left: parent.left
        anchors.verticalCenter: parent.verticalCenter
        anchors.leftMargin: container.spacing
        width: height
        height: parent.height - container.spacing * 2
        radius: container.radius
        source: container.artworkSource
    }

    Column {
        id: textContainer
        anchors.left: artworkBackground.right
        anchors.verticalCenter: parent.verticalCenter
        anchors.leftMargin: container.spacing
        width: parent.width - artworkBackground.width - container.spacing * 2.5
        height: implicitHeight

        Text {
            id: nameText
            width: parent.width
            height: implicitHeight
            text: container.name
            font.family: interFont.name
            font.pointSize: container.nameTextSize
            font.weight: container.nameTextStyle
            color: container.nameTextColor
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
            font.weight: container.descriptionTextStyle
            color: container.descriptionTextColor
            verticalAlignment: Text.AlignTop
            clip: true
            elide: Text.ElideRight
        }
    }
}