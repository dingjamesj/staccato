import QtQuick
import QtQuick.Controls.Basic

Column {
    id: container
    spacing: height * 0.05
    leftPadding: 5
    rightPadding: 10
    topPadding: height * 0.048
    bottomPadding: 25

    FontLoader {
        id: interFont
        source: "qrc:/staccato/src/ui/resources/Inter-VariableFont_opsz,wght.ttf"
    }

    Row {
        id: playlistsHeader
        width: container.width - container.leftPadding - container.rightPadding
        height: 30
        spacing: 9
        
        Text {
            id: playlistsHeaderText
            width: playlistsHeader.width - playlistsHeader.spacing - viewButton.width
            height: 45
            text: "Your Playlists"
            font.family: interFont.name
            font.pointSize: 24
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: "#ffffff"
        }

        //Toggles between list & grid view
        Button {
            id: viewButton
            width: 35
            height: 35
            padding: 3
            anchors.verticalCenter: parent.verticalCenter
            background: Rectangle {
                width: parent.width
                height: parent.height
                radius: 8
                color: "#434343"
            }
            icon.color: "#ffffff"
            icon.source: "qrc:/staccato/src/ui/resources/list.svg"
        }
    }
}