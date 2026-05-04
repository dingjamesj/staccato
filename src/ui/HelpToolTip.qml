import QtQuick
import QtQuick.Controls

Rectangle {
    property string tooltipText: ""

    property color defaultColor: Style.purple
    property color disabledColor: Qt.darker(defaultColor, 1.5)

    property color iconColor: Style.white
    property color disabledIconColor: Qt.darker(iconColor, 1.5)

    id: container
    color: enabled ? defaultColor : disabledColor
    radius: width / 2

    ToolTip.visible: helpMouseArea.containsMouse
    ToolTip.text: container.tooltipText
    ToolTip.delay: 500

    Text {
        anchors.verticalCenter: parent.verticalCenter
        anchors.horizontalCenter: parent.horizontalCenter
        scale: 0.6
        text: "?"
        font.family: Style.mainFontFamily
        font.weight: Font.Bold
        fontSizeMode: Text.Fit
        color: container.enabled ? iconColor : disabledIconColor
        verticalAlignment: Text.AlignVCenter
        horizontalAlignment: Text.AlignHCenter
    }

    MouseArea {
        id: helpMouseArea
        anchors.fill: parent
        hoverEnabled: true
    }
}