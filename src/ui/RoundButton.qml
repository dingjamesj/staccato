import QtQuick
import QtQuick.Effects
import staccato

pragma ComponentBehavior: Bound

Rectangle {
    property string text: ""
    property url imageSource: ""
    property url backupImageSource: ""
    
    property color defaultColor: Style.gray
    property color hoverColor: Qt.lighter(defaultColor, 1.2)
    property color pressedColor: Qt.lighter(defaultColor, 1.5)
    property color disabledColor: Qt.darker(defaultColor, 1.5)
    
    property color textColor: Style.white
    property color disabledTextColor: Qt.darker(textColor, 1.5)
    
    property double pressedColorTintStrength: 0.1
    property double hoverColorTintStrength: 0.05
    property double disabledColorTintStrength: 0.5

    property int textSize: Style.normalTextSize
    property var textStyle: Font.Bold

    property bool clickable: true
    property bool imageFailed: false

    signal clicked()
    signal doubleClicked()

    id: container
    color: enabled ? (mouseArea.pressed ? pressedColor : (mouseArea.containsMouse ? hoverColor : defaultColor)) : disabledColor

    MouseArea {
        id: mouseArea
        anchors.fill: parent
        hoverEnabled: true
        onClicked: container.clicked()
        enabled: container.enabled && container.clickable
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
        visible: container.text != ""
    }

    Image {
        id: img
        anchors.fill: parent
        source: container.imageSource
        visible: false

        onStatusChanged: {
            if(status === Image.Error) {
                container.imageFailed = true;
            } else {
                container.imageFailed = false;
            }
        }
    }

    Image {
        id: backupImg
        anchors.fill: parent
        source: container.backupImageSource
        visible: false
    }

    MultiEffect {
        id: buttonImage
        source: container.imageFailed ? backupImg : img
        anchors.fill: parent
        colorization: container.enabled ? 0 : container.disabledColorTintStrength
        colorizationColor: '#000000'
        brightness: container.enabled ? (mouseArea.pressed ? container.pressedColorTintStrength : (mouseArea.containsMouse ? container.hoverColorTintStrength : 0)) : 0
        autoPaddingEnabled: true
        visible: container.imageSource !== ""
        maskEnabled: true
        maskSource: mask
    }

    Item {
        id: mask
        width: parent.width
        height: parent.height
        layer.enabled: true
        visible: false
        
        Rectangle {
            width: parent.width
            height: parent.height
            radius: container.radius
            color: "#000000"
        }
    }
}