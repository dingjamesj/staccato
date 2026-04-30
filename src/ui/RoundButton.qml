import QtQuick
import QtQuick.Effects

pragma ComponentBehavior: Bound

Rectangle {
    property string text: ""
    property url imageSource: ""
    
    property color defaultColor: Style.gray
    property color hoverColor: Qt.lighter(defaultColor, 1.2)
    property color pressedColor: Qt.lighter(defaultColor, 1.5)
    property color disabledColor: Qt.darker(defaultColor, 1.5)
    
    property color textColor: Style.white
    property color disabledTextColor: Qt.darker(textColor, 1.5)
    
    property double disabledColorTintStrength: 0.5

    property int textSize: Style.normalTextSize
    property var textStyle: Font.Bold

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
        anchors.margins: Style.smallSpacing
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
        source: container.imageSource
        visible: false
    }

    MultiEffect {
        id: buttonImage
        source: img
        anchors.fill: img
        colorization: container.enabled ? 0 : container.disabledColorTintStrength
        colorizationColor: '#000000'
        autoPaddingEnabled: true
    }
}