import QtQuick
import QtQuick.Controls
import QtQuick.Layouts
import staccato
import "trackImporter.js" as Logic

Column {
    property alias searchQueryText: queryTextField.text
    property alias previewTitleText: resultsEditor.titleText
    property alias artistsContainer: resultsEditor.artistsContainer
    property alias previewAlbumText: resultsEditor.albumText
    property alias previewArtworkSource: resultsEditor.artworkSource
    property alias urlTextFieldText: urlTextField.text

    property int searchFieldMaxWidth: 300
    property int searchResultFieldsMaxWidth: 490
    property int extraParametersPanelHeight: 100

    property string forceMP3ParamText: "Force MP3"
    property string forceOpusParamText: "Force Opus (.ogg)"

    id: container
    spacing: Style.medSpacing

    Component.onCompleted: {
        Logic.startup(StaccatoInterface);
    }

    RowLayout {
        width: parent.width
        height: implicitHeight
        spacing: Style.smallSpacing

        //Text field to input the URL / file path
        RoundTextField {
            id: queryTextField
            font.pointSize: Style.normalTextSize
            placeholderText: "Search for a song"

            Layout.preferredWidth: 3
            Layout.fillWidth: true
            Layout.fillHeight: true
            Layout.maximumWidth: container.searchFieldMaxWidth
        }

        RoundButton {
            radius: Style.buttonRadius
            text: "Search"
            defaultColor: Style.purple
            enabled: searchQueryText.length > 0

            Layout.preferredWidth: 60
            Layout.fillHeight: true

            onClicked: {
                resultsPanel.enabled = !resultsPanel.enabled;
                urlTextFieldText = searchQueryText;
            }
        }

        Text {
            id: statusText
            text: ""
            font.family: Style.mainFontFamily
            font.pointSize: Style.normalTextSize
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: Style.red
            verticalAlignment: Text.AlignVCenter

            Layout.preferredWidth: 1
            Layout.fillWidth: true
            Layout.fillHeight: true
        }
    }

    Column {
        id: resultsPanel
        width: parent.width
        height: implicitHeight
        spacing: Style.smallSpacing
        enabled: false

        Text {
            text: "Search Results"
            font.family: Style.mainFontFamily
            font.pointSize: Style.h2TextSize
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: Style.white
            enabled: true
        }

        TrackInfoEditor {
            id: resultsEditor
            width: Math.min(parent.width, searchResultFieldsMaxWidth)
            readOnly: !overwriteMetadataCheckbox.checked
        }

        RowLayout {
            anchors.left: parent.left
            width: Math.min(parent.width, searchResultFieldsMaxWidth)
            spacing: Style.smallSpacing

            Text {
                text: "URL:"
                font.family: Style.mainFontFamily
                font.pointSize: Style.normalTextSize
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: enabled ? Style.white : Qt.darker(Style.white, 1.5)
            }

            RoundTextField {
                id: urlTextField
                leftPadding: 0
                rightPadding: 0
                topPadding: 0
                bottomPadding: 0
                font.family: Style.mainFontFamily
                font.pointSize: Style.smallTextSize
                wrapMode: Text.NoWrap
                color: enabled ? Style.white : Qt.darker(Style.white, 1.5)
                readOnly: true

                // Layout.alignment: Qt.AlignCenter
            }

            //Copy URL button
            RoundButton {
                radius: Style.smallButtonRadius

                Layout.leftMargin: width / 2
                Layout.preferredWidth: Style.smallButtonSize
                Layout.preferredHeight: Style.smallButtonSize
                Layout.alignment: Qt.AlignVCenter

                visible: enabled
            }

            //Empty component to shove everything to the left and right sides
            Item {
                Layout.fillWidth: true
                Layout.preferredHeight: 1
            }

            RoundButton {
                radius: Style.buttonRadius

                Layout.preferredWidth: Style.buttonSize
                Layout.preferredHeight: width
            }

            Text {
                text: "1 / 5"
                font.family: Style.mainFontFamily
                font.pointSize: Style.smallTextSize
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: enabled ? Style.white : Qt.darker(Style.white, 1.5)
                horizontalAlignment: Text.AlignHCenter

                Layout.minimumWidth: resultsEditor.height - Style.buttonSize * 2 - Style.smallSpacing * 2
            }

            RoundButton {
                radius: Style.buttonRadius

                Layout.preferredWidth: Style.buttonSize
                Layout.preferredHeight: width
            }
        }

        Row {
            spacing: Style.medSpacing

            RoundButton {
                width: 80
                height: Style.buttonSize
                radius: Style.buttonRadius
                text: "Download"
                defaultColor: Style.purple
            }

            RoundCheckBox {
                id: overwriteMetadataCheckbox
                text: "Overwrite metadata"
                spacing: Style.tinySpacing
                textColor: Style.offWhite
                textSize: Style.smallTextSize
            }
            
            Item {
                width: Style.smallButtonSize / 2 - Style.medSpacing * 2 + Style.tinySpacing
                height: 1
            }

            HelpToolTip {
                anchors.verticalCenter: parent.verticalCenter
                width: Style.smallButtonSize
                height: width
                tooltipText: "Set your own details instead of using automatic info found online."
            }

            RoundCheckBox {
                id: extraParametersCheckBox
                text: "Use advanced settings"
                spacing: Style.tinySpacing
                textColor: Style.offWhite
                textSize: Style.smallTextSize
            }
        }

        ExtraParametersEditor {
            id: extraParametersEditor
            width: parent.width
            height: extraParametersPanelHeight
        }
    }
}