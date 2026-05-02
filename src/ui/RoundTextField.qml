import QtQuick
import QtQuick.Controls

pragma ComponentBehavior: Bound

TextField {
    property color textColor: Style.offWhite
    property color disabledTextColor: Qt.darker(textColor, 1.5)

    property color defaultPlaceholderTextColor: Style.darkWhite
    property color disabledPlaceholderTextColor: Qt.darker(defaultPlaceholderTextColor, 1.5)

    property color defaultColor: Style.lightBackground
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
        color: !container.readOnly ? (container.enabled ? defaultColor : disabledColor) : "#00000000"
    }
}