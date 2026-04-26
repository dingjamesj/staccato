import QtQuick
import QtQuick.Effects

pragma ComponentBehavior: Bound

Rectangle {
    property string text: ""
    property url imageSource: ""
    
    property color defaultColor: "#404040"
    property color hoverColor: Qt.lighter(defaultColor, 1.2)
    property color pressedColor: Qt.lighter(defaultColor, 1.5)
    property color disabledColor: Qt.darker(defaultColor, 1.5)
    
    property color textColor: "#ffffff"
    property color disabledTextColor: Qt.darker(textColor, 1.5)
    
    property color imageColor: "#ffffff"
    property color disabledImageColor: Qt.darker(imageColor, 1.5)

    property int textSize: 18
    property var textStyle: Font.Normal
    property int spacing: 8

    signal clicked()
    signal doubleClicked()

    id: container
    color: enabled ? (mouseArea.pressed ? pressedColor : (mouseArea.containsMouse ? hoverColor : defaultColor)) : disabledColor

    MouseArea {
        id: mouseArea
        anchors.fill: parent
        hoverEnabled: true
        onClicked: container.clicked()
        enabled: container.enabled
    }

    Text {
        id: buttonText
        anchors.fill: parent
        anchors.margins: container.spacing
        verticalAlignment: Text.AlignVCenter
        horizontalAlignment: Text.AlignHCenter
        font.family: Style.mainFontFamily
        font.pointSize: container.textSize
        font.weight: container.textStyle
        clip: false
        text: container.text
        color: container.enabled ? container.textColor : container.disabledTextColor
    }

    Image {
        id: img
        anchors.fill: parent
        anchors.margins: container.spacing
        source: container.imageSource
        visible: false
    }

    MultiEffect {
        id: buttonImage
        source: img
        anchors.fill: img
        colorization: 1.0
        colorizationColor: container.enabled ? container.imageColor : container.disabledImageColor
        autoPaddingEnabled: true
    }
}