import QtQuick
import QtQuick.Effects
import QtQuick.Controls

Column {
    id: container
    spacing: width * 4 / 85
    leftPadding: width * 6 / 85
    rightPadding: width * 6 / 85
    topPadding: width * 8 / 85

    FontLoader {
        id: interFont
        source: "qrc:/staccato/src/ui/resources/Inter-VariableFont_opsz,wght.ttf"
    }

    FontLoader {
        id: interItalicFont
        source: "qrc:/staccato/src/ui/resources/Inter-Italic-VariableFont_opsz,wght.ttf"
    }

    //=========================================
    //                COVER ART                
    //=========================================

    Rectangle {
        id: artworkBackground
        anchors.horizontalCenter: parent.horizontalCenter
        width: parent.width - container.leftPadding - container.leftPadding
        height: width
        radius: 22
        color: "#2b2b2b"

        Image {
            id: artwork
            anchors.fill: parent
            visible: false
            source: "qrc:/staccato/src/ui/resources/piggo58.jpg"
            fillMode: Image.PreserveAspectFit
        }

        MultiEffect {
            anchors.fill: artwork
            source: artwork
            maskEnabled: true
            maskSource: mask
        }

        Item {
            id: mask
            width: artwork.width
            height: artwork.height
            layer.enabled: true
            visible: false

            Rectangle {
                width: artwork.width
                height: artwork.height
                radius: 22
                color: "black"
            }
        }
    }

    //=========================================
    //               TRACK INFO               
    //=========================================

    Column {
        id: trackInfoContainer
        spacing: 3

        ScrollView {
            id: trackTitle
            width: container.width - container.leftPadding - container.leftPadding
            clip: true
            ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
            ScrollBar.vertical.policy: ScrollBar.AlwaysOff
            Component.onCompleted: {
                trackTitle.contentItem.boundsBehavior = Flickable.StopAtBounds
            }

            TextEdit {
                id: trackTitleText
                width: container.width - container.leftPadding - container.leftPadding
                text: "Track Title"
                horizontalAlignment: TextEdit.AlignHCenter
                font.family: interFont.name
                font.pointSize: 38
                font.bold: true
                wrapMode: Text.NoWrap
                readOnly: true
                color: "#ffffff"
            }
        }

        ScrollView {
            id: trackArtists
            width: container.width - container.leftPadding - container.leftPadding
            clip: true
            ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
            ScrollBar.vertical.policy: ScrollBar.AlwaysOff
            Component.onCompleted: {
                trackArtists.contentItem.boundsBehavior = Flickable.StopAtBounds
            }

            TextEdit {
                id: trackArtistsText
                width: container.width - container.leftPadding - container.leftPadding
                text: "Track Artists"
                horizontalAlignment: TextEdit.AlignHCenter
                font.family: interFont.name
                font.pointSize: 15
                wrapMode: Text.NoWrap
                readOnly: true
                color: "#9a9a9a"
            }
        }

        ScrollView {
            id: trackAlbum
            width: container.width - container.leftPadding - container.leftPadding
            clip: true
            ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
            ScrollBar.vertical.policy: ScrollBar.AlwaysOff
            Component.onCompleted: {
                trackAlbum.contentItem.boundsBehavior = Flickable.StopAtBounds
            }

            TextEdit {
                id: trackAlbumText
                width: container.width - container.leftPadding - container.leftPadding
                text: "Track Album"
                horizontalAlignment: TextEdit.AlignHCenter
                font.family: interFont.name
                font.italic: true
                font.pointSize: 15
                wrapMode: Text.NoWrap
                readOnly: true
                color: "#9a9a9a"
            }
        }
    }
}