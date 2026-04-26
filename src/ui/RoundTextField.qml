import QtQuick
import QtQuick.Controls

pragma ComponentBehavior: Bound

TextField {
    property color textColor: "#d4d4d4"
    property color disabledTextColor: Qt.darker(textColor, 1.5)

    property color defaultPlaceholderTextColor: "#7f7f7f"
    property color disabledPlaceholderTextColor: Qt.darker(placeholderTextColor, 1.5)

    property color defaultColor: "#303030"
    property color disabledColor: Qt.darker(defaultColor, 1.3)

    property int backgroundRadius: 6

    id: container
    color: enabled ? textColor : disabledTextColor
    placeholderTextColor: enabled ? defaultPlaceholderTextColor : disabledPlaceholderTextColor
    
    font.family: Style.mainFontFamily
    font.pointSize: 10
    padding: 5

    background: Rectangle {
        radius: container.backgroundRadius
        color: container.enabled ? defaultColor : disabledColor
    }
}