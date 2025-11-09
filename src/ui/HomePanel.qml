import QtQuick

Column {
    id: container
    spacing: height * 0.05
    leftPadding: width * 0.006
    rightPadding: width * 0.012
    topPadding: height * 0.048
    bottomPadding: height * 0.02

    Rectangle {
        id: test
        width: container.width - container.leftPadding - container.rightPadding
        height: container.height - container.topPadding - container.bottomPadding
        anchors.left: container.left
        anchors.leftMargin: container.leftPadding
        anchors.right: container.right
        anchors.rightMargin: container.rightPadding
        color: "white"
    }
}