import QtQuick

Rectangle {
    id: container

    property string text: ""
    property url imageSource: ""
    property color defaultColor: "#404040"
    property color hoverColor: Qt.lighter(defaultColor, 1.2)
    property color pressedColor: Qt.lighter(defaultColor, 1.5)
    property color textColor: "#ffffff"
    property int textSize: 18
    property var textStyle: Font.Normal
    property int spacing: 8
    property bool enableDoubleClick: false

    signal clicked()
    signal doubleClicked()

    Component.onCompleted: {
        color = defaultColor
        if(enableDoubleClick) {

            mouseArea.doubleClicked.connect(container.doubleClicked);

        }
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

    Text {
        id: buttonText
        anchors.fill: parent
        anchors.margins: container.spacing
        verticalAlignment: Text.AlignVCenter
        horizontalAlignment: Text.AlignHCenter
        font.family: interFont.name
        font.pointSize: container.textSize
        font.weight: container.textStyle
        clip: true
        text: text
        color: container.textColor
    }

    Image {
        id: buttonImage
        anchors.fill: parent
        anchors.margins: container.spacing
        source: container.imageSource
    }
}