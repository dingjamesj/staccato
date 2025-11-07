import QtQuick
import QtQuick.Effects
import QtQuick.Controls

Rectangle {
    id: container
    color: "#1e1e1e"

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
        anchors.top: parent.top
        anchors.topMargin: parent.height * 8 / 165
        width: parent.width * 364 / 425
        height: parent.width * 364 / 425
        radius: 22
        color: "#2b2b2b"
    }

    Image {
        id: artwork
        anchors.horizontalCenter: parent.horizontalCenter
        anchors.top: parent.top
        anchors.topMargin: parent.height * 8 / 165
        width: parent.width * 364 / 425
        height: parent.width * 364 / 425
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

    //=========================================
    //               TRACK INFO               
    //=========================================

    ScrollView {
        id: trackTitle
        anchors.horizontalCenter: parent.horizontalCenter
        anchors.top: artworkBackground.bottom
        anchors.topMargin: parent.height * 4 / 165
        anchors.left: artworkBackground.left
        anchors.right: artworkBackground.right
        clip: true
        ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
        ScrollBar.vertical.policy: ScrollBar.AlwaysOff
        Component.onCompleted: {
            trackTitle.contentItem.boundsBehavior = Flickable.StopAtBounds
        }

        Text {
            id: trackTitleText
            anchors.fill: parent
            text: "Track Title aoludshauidhauisshd"
            font.family: interFont.name
            font.pointSize: 38
            font.bold: true
            color: "#ffffff"
        }
    }

    ScrollView {
        id: trackArtists
        anchors.horizontalCenter: parent.horizontalCenter
        anchors.top: trackTitle.bottom
        anchors.topMargin: 3
        Component.onCompleted: {
            trackTitle.contentItem.boundsBehavior = Flickable.StopAtBounds
        }

        Text {
            id: trackArtistsText
            anchors.fill: parent
            text: "Track Artists"
            font.family: interFont.name
            font.pointSize: 15
            color: "#9a9a9a"
        }
    }

    ScrollView {
        id: trackAlbum
        anchors.horizontalCenter: parent.horizontalCenter
        anchors.top: trackArtists.bottom
        anchors.topMargin: 5
        Component.onCompleted: {
            trackTitle.contentItem.boundsBehavior = Flickable.StopAtBounds
        }

        Text {
            id: trackAlbumText
            anchors.fill: parent
            text: "Track Album"
            font.family: interFont.name
            font.italic: true
            font.pointSize: 15
            color: "#9a9a9a"
        }
    }
}