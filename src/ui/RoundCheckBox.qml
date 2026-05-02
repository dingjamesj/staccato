import QtQuick
import QtQuick.Controls

CheckBox {
    property int boxSize: 13
    property int textSize: Style.normalTextSize
    
    property color textColor: Style.darkWhite
    property color disabledTextColor: Qt.darker(textColor, 1.5)
    
    property color defaultColor: Style.gray
    property color hoverColor: Qt.lighter(defaultColor, 1.2)
    property color pressedColor: Qt.lighter(defaultColor, 1.5)
    property color disabledColor: Qt.darker(defaultColor, 1.5)

    property color defaultColor2: Style.purple
    readonly property color hoverColor2: Qt.lighter(defaultColor2, 1.2)
    readonly property color pressedColor2: Qt.lighter(defaultColor2, 1.5)
    readonly property color disabledColor2: Qt.darker(defaultColor2, 1.5)

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
                    container.checked ? pressedColor2 : pressedColor
                ) : container.hovered ? (
                    container.checked ? hoverColor2: hoverColor
                ) : container.checked ? defaultColor2 : defaultColor
            ) : container.checked ? disabledColor2 : disabledColor
        )

        Text {
            text: "✔"
            height: implicitHeight
            verticalAlignment: Text.AlignVCenter
            horizontalAlignment: Text.AlignHCenter
            color: Style.white
            anchors.centerIn: parent
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