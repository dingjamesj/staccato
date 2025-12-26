import QtQuick
import QtQuick.Controls.Basic

Column {
    id: container
    spacing: 25
    leftPadding: width * 5 / 85
    rightPadding: width * 5 / 85
    topPadding: height * 0.048 / 0.82
    bottomPadding: 12

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

    RoundedImage {
        id: artwork
        anchors.horizontalCenter: parent.horizontalCenter
        width: parent.width - container.leftPadding - container.rightPadding
        height: width
        radius: 22
        source: ""
    }

    //=========================================
    //               TRACK INFO               
    //=========================================

    Column {
        id: trackInfoContainer
        anchors.horizontalCenter: parent.horizontalCenter
        height: parent.height - artwork.height - parent.spacing - parent.topPadding - parent.bottomPadding
        spacing: (height - basicInfoContainer.height - editingButtonsContainer.height - trackFileInfoContainer.height) / 2
        padding: 0

        //Basic info (title, artists, album)
        Column {
            id: basicInfoContainer
            height: implicitHeight
            spacing: 3

            ScrollView {
                id: title
                width: container.width - container.leftPadding - container.rightPadding
                clip: true
                ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
                ScrollBar.vertical.policy: ScrollBar.AlwaysOff
                Component.onCompleted: {
                    contentItem.boundsBehavior = Flickable.StopAtBounds;
                }

                TextEdit {
                    id: titleText
                    width: container.width - container.leftPadding - container.rightPadding
                    text: "Track Title"
                    horizontalAlignment: TextEdit.AlignHCenter
                    font.family: interFont.name
                    font.pointSize: 30
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
                    contentItem.boundsBehavior = Flickable.StopAtBounds;
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
                    contentItem.boundsBehavior = Flickable.StopAtBounds;
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

        //Edit button, redownload button
        Row {
            id: editingButtonsContainer
            anchors.horizontalCenter: parent.horizontalCenter
            width: implicitWidth
            height: implicitHeight
            spacing: 30

            Button {
                id: editButton
                width: 50
                height: 50
                text: "Edit"
            }

            Button {
                id: redownloadButton
                width: 50
                height: 50
                text: "Redownload"
            }
        }

        //Audio file properties (extension, bitrate, sample rate)
        Row {
            id: trackFileInfoContainer
            anchors.horizontalCenter: parent.horizontalCenter
            width: implicitWidth
            height: implicitHeight
            spacing: 15

            Rectangle {
                id: fileType
                anchors.verticalCenter: parent.verticalCenter
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
                id: bitrateText
                anchors.verticalCenter: parent.verticalCenter
                width: implicitWidth
                text: "124 kbps"
                horizontalAlignment: TextEdit.AlignHCenter
                font.family: interFont.name
                font.pointSize: 13
                font.bold: true
                wrapMode: TextEdit.NoWrap
                readOnly: true
                color: "#ffffff"
            }

            TextEdit {
                id: sampleRateText
                anchors.verticalCenter: parent.verticalCenter
                width: implicitWidth
                text: "44.1 kHz"
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
}