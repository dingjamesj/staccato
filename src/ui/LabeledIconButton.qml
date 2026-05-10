import QtQuick
import QtQuick.Layouts
import QtQuick.Controls.Basic

pragma ComponentBehavior: Bound

Rectangle {
    required property string source
    property string text: ""
    property string subtext: ""
    property color defaultColor: "#00000000"
    property color hoverColor: '#27ffffff'
    property color pressedColor: '#4fffffff'
    property bool clickable: true

    id: container
    color: enabled ? (mouseArea.pressed ? pressedColor : (mouseArea.containsMouse ? hoverColor : defaultColor)) : defaultColor

    signal clicked()

    MouseArea {
        id: mouseArea
        anchors.fill: parent
        hoverEnabled: true
        onClicked: container.clicked()
        enabled: container.enabled && container.clickable
    }

    RowLayout {
        spacing: Style.smallSpacing

        RoundImage {
            source: container.source

            Layout.preferredHeight: container.height
            Layout.preferredWidth: height
        }

        //Main & sub texts
        ColumnLayout {
            spacing: 0

            Layout.fillWidth: true

            TextArea {
                text: container.text
                readOnly: true
                padding: 0
                wrapMode: TextArea.NoWrap
                background: null

                Layout.fillWidth: true
            }

            TextArea {
                text: container.subtext
                readOnly: true
                padding: 0
                wrapMode: TextArea.NoWrap
                background: null
                visible: container.subtext !== ""

                Layout.fillWidth: true
            }
        }
    }
}