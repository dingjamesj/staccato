import QtQuick
import QtQuick.Controls

pragma ComponentBehavior: Bound

TabButton {
    property int insets: 3
    property int underlineRectangleHeight: 4
    property int underlineSeparation: 4
    property int underlineExtraWidth: 1
    property color underlineColor: Style.purple

    id: container
    hoverEnabled: true

    contentItem: Column {
        spacing: container.underlineSeparation
        anchors.verticalCenter: parent.verticalCenter

        Text {
            id: tabButtonText
            anchors.horizontalCenter: parent.horizontalCenter
            width: implicitWidth
            color: container.checked ? Style.white : (container.hovered ? Qt.lighter(Style.darkWhite, 1.2) : Style.darkWhite)
            text: container.text
            font.family: Style.mainFontFamily
            font.pointSize: Style.normalTextSize
            font.weight: Font.DemiBold
        }

        Rectangle {
            anchors.horizontalCenter: parent.horizontalCenter
            width: tabButtonText.width + container.underlineExtraWidth * 2
            height: container.underlineRectangleHeight
            color: container.checked ? container.underlineColor : "#00000000"
        }
    }

    background: Rectangle {
        anchors.horizontalCenter: parent.horizontalCenter
        anchors.verticalCenter: parent.verticalCenter
        width: container.contentItem.width + container.insets * 2
        height: container.contentItem.height + container.insets * 2

        color: '#00000000'
    }
}