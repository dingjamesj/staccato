import QtQuick
import QtQuick.Controls
import QtQuick.Layouts
import staccato
import "trackImporter.js" as Logic

Column {
    property alias importStatusText: statusText.text
    property alias importURLText: urlTextField.text
    property alias importExtraParamsText: extraParametersEditor.text
    property alias previewEditor: previewEditor

    property bool previewIsLoading: false
    property bool previewIsLoaded: false

    property int urlFieldMaxWidth: 400
    property int extraParametersPanelHeight: 75

    id: container
    spacing: Style.medSpacing

    Component.onCompleted: {
        Logic.startup(StaccatoInterface);
    }

    //URL input & extra params.
    Column {
        width: parent.width
        height: implicitHeight
        spacing: Style.smallSpacing

        RowLayout {
            width: parent.width
            height: implicitHeight
            spacing: Style.smallSpacing

            //Text field to input the URL / file path
            RoundTextField {
                id: urlTextField
                font.pointSize: Style.smallTextSize
                placeholderText: "Paste a web URL or a file path"

                Layout.preferredWidth: 3
                Layout.fillWidth: true
                Layout.fillHeight: true
                Layout.maximumWidth: container.urlFieldMaxWidth

                onTextEdited: {
                    previewIsLoaded = false;
                }
            }

            RoundButton {
                radius: Style.buttonRadius
                text: "Download"
                defaultColor: Style.purple
                enabled: importURLText.length > 0

                Layout.preferredWidth: 80
                Layout.fillHeight: true
            }

            RoundButton {
                radius: Style.buttonRadius
                text: "Load Preview"
                defaultColor: Style.gray
                enabled: previewIsLoading ? false : (importURLText.length > 0)
                
                Layout.preferredWidth: 105
                Layout.fillHeight: true

                onClicked: Logic.loadPreview(container)
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
            width: parent.width
            spacing: Style.tinySpacing

            RoundCheckBox {
                id: extraParametersCheckBox
                text: "Use advanced settings"
                spacing: Style.tinySpacing
                textColor: Style.offWhite
                textSize: Style.smallTextSize
            }

            ExtraParametersEditor {
                id: extraParametersEditor
                width: parent.width
                height: extraParametersPanelHeight
            }
        }
    }

    //Track preview title text

    Column {
        width: parent.width
        height: implicitHeight
        spacing: Style.smallSpacing

        Text {
            text: "Preview"
            font.family: Style.mainFontFamily
            font.pointSize: Style.h2TextSize
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: Style.white
        }

        TrackInfoEditor {
            id: previewEditor
            anchors.left: parent.left
            width: parent.width
            readOnly: !overwriteMetadataCheckbox.checked
            enabled: previewIsLoaded || overwriteMetadataCheckbox.checked
        }

        //Overwrite metadata checkbox
        Row {
            spacing: Style.tinySpacing

            RoundCheckBox {
                id: overwriteMetadataCheckbox
                text: "Overwrite metadata"
                spacing: Style.tinySpacing
                textColor: Style.offWhite
                textSize: Style.smallTextSize
            }
            
            Item {
                width: Style.smallButtonSize / 2 - Style.tinySpacing
                height: 1
            }

            HelpToolTip {
                anchors.verticalCenter: parent.verticalCenter
                width: Style.smallButtonSize
                height: width
                tooltipText: "Set your own details instead of using automatic info found online."
            }
        }
    }    
}