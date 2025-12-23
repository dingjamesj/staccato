import QtQuick
import QtQuick.Effects
import QtQuick.Controls.Basic

Column {
    id: container
    spacing: height * 0.05
    leftPadding: width * 5 / 85
    rightPadding: width * 5 / 85
    topPadding: height * 0.048 / 0.82

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
        width: parent.width - container.leftPadding - container.rightPadding
        height: width
        radius: 22
        color: "#2b2b2b"

        Image {
            id: artwork
            anchors.fill: parent
            visible: false
            // source: "qrc:/staccato/src/ui/resources/piggo58.jpg"
            // source: "image://audioFile/C:/Users/James/Music/rargb/Playboi Carti - R.I.P. but the intro transcends you.mp3"
            // source: "image://audioFile/C:/Users/James/Music/rargb/Rockstar.mp3"
            source: "image://audioFile/C:/Users/James/Music/file_example_MP3_2MG.mp3"
            // source: "image://audioFile/D:/Programming/C++/staccato/tracks/ffMfBDkmlz8.m4a"
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
            id: title
            width: container.width - container.leftPadding - container.rightPadding
            clip: true
            ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
            ScrollBar.vertical.policy: ScrollBar.AlwaysOff
            Component.onCompleted: {
                title.contentItem.boundsBehavior = Flickable.StopAtBounds
            }

            TextEdit {
                id: titleText
                width: container.width - container.leftPadding - container.rightPadding
                text: "After Hours"
                horizontalAlignment: TextEdit.AlignHCenter
                font.family: interFont.name
                font.pointSize: 33
                font.bold: true
                wrapMode: TextEdit.NoWrap
                readOnly: true
                color: "#ffffff"
            }
        }

        ScrollView {
            id: artists
            width: container.width - container.leftPadding - container.rightPadding
            clip: true
            ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
            ScrollBar.vertical.policy: ScrollBar.AlwaysOff
            Component.onCompleted: {
                artists.contentItem.boundsBehavior = Flickable.StopAtBounds
            }

            TextEdit {
                id: artistsText
                width: container.width - container.leftPadding - container.rightPadding
                text: "Track Artists"
                horizontalAlignment: TextEdit.AlignHCenter
                font.family: interFont.name
                font.pointSize: 15
                wrapMode: TextEdit.NoWrap
                readOnly: true
                color: "#9a9a9a"
            }
        }

        ScrollView {
            id: album
            width: container.width - container.leftPadding - container.rightPadding
            clip: true
            ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
            ScrollBar.vertical.policy: ScrollBar.AlwaysOff
            Component.onCompleted: {
                album.contentItem.boundsBehavior = Flickable.StopAtBounds
            }

            TextEdit {
                id: albumText
                width: container.width - container.leftPadding - container.rightPadding
                text: "Track Album"
                horizontalAlignment: TextEdit.AlignHCenter
                font.family: interFont.name
                font.italic: true
                font.pointSize: 15
                wrapMode: TextEdit.NoWrap
                readOnly: true
                color: "#9a9a9a"
            }
        }
    }

    //=========================================
    //             TRACK FILE INFO             
    //=========================================

    Column {
        id: trackFileInfoContainer
        spacing: 9

        Rectangle {
            id: fileType
            anchors.horizontalCenter: parent.horizontalCenter
            width: 67
            height: 36
            radius: height / 2
            color: "#d04ea5"

            TextEdit {
                id: fileTypeText
                width: parent.width
                height: parent.height
                text: "M4A"
                horizontalAlignment: TextEdit.AlignHCenter
                verticalAlignment: TextEdit.AlignVCenter
                font.family: interFont.name
                font.pointSize: 13
                font.weight: Font.ExtraBold
                wrapMode: TextEdit.NoWrap
                readOnly: true
                color: "#ffffff"
            }
        }

        TextEdit {
            id: advancedInfoText
            width: container.width - container.leftPadding - container.rightPadding
            text: "124 kbps   44.1 kHz"
            horizontalAlignment: TextEdit.AlignHCenter
            font.family: interFont.name
            font.pointSize: 13
            font.bold: true
            wrapMode: TextEdit.NoWrap
            readOnly: true
            color: "#ffffff"
        }
    }
}