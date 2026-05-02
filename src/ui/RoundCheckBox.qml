import QtQuick
import QtQuick.Controls

CheckBox {
    property int boxSize: 13
    property int textSize: Style.normalTextSize
    
    property color textColor: Style.white
    property color disabledTextColor: Qt.darker(textColor, 1.5)
    
    property color defaultColor: Style.gray
    property color hoverColor: Qt.lighter(defaultColor, 1.2)
    property color pressedColor: Qt.lighter(defaultColor, 1.5)
    property color disabledColor: Qt.darker(defaultColor, 1.5)

    property color checkedColor: Style.purple
    readonly property color checkedHoverColor: Qt.lighter(checkedColor, 1.2)
    readonly property color checkedPressedColor: Qt.lighter(checkedColor, 1.5)
    readonly property color checkedDisabledColor: Qt.darker(checkedColor, 1.5)

    id: container
    hoverEnabled: true

    indicator: Rectangle {
        anchors.left: parent.left
        anchors.verticalCenter: parent.verticalCenter
        width: boxSize
        height: boxSize
        radius: Style.smallButtonRadius
        color: (
            container.enabled ? (
                container.pressed ? (
                    container.checked ? checkedPressedColor : pressedColor
                ) : container.hovered ? (
                    container.checked ? checkedHoverColor: hoverColor
                ) : container.checked ? checkedColor : defaultColor
            ) : container.checked ? checkedDisabledColor : disabledColor
        )

        Text {
            text: "✔"
            height: implicitHeight
            verticalAlignment: Text.AlignVCenter
            horizontalAlignment: Text.AlignHCenter
            color: Style.white
            anchors.verticalCenter: parent.verticalCenter
            anchors.horizontalCenter: parent.horizontalCenter
            visible: container.checked
            font.family: Style.mainFontFamily
            font.pointSize: Style.normalTextSize
            font.weight: Font.Bold
        }
    }

    contentItem: Text {
        anchors.verticalCenter: parent.verticalCenter
        anchors.left: indicator.right
        anchors.leftMargin: container.spacing
        text: container.text
        font.family: Style.mainFontFamily
        font.pointSize: container.textSize
        color: container.enabled ? container.textColor : container.disabledTextColor
        verticalAlignment: Text.AlignVCenter
    }
}