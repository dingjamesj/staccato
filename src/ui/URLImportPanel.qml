import QtQuick
import QtQuick.Controls
import QtQuick.Layouts
import staccato
import "trackImporter.js" as Logic

Column {
    property alias importStatusText: statusText.text
    property alias importURLText: urlTextField.text
    property alias importExtraParamsText: extraParametersTextArea.text
    property alias previewTitleText: previewEditor.titleText
    property alias artistsContainer: previewEditor.artistsContainer
    property alias previewAlbumText: previewEditor.albumText
    property alias previewArtworkSource: previewEditor.artworkSource

    property bool previewIsLoading: false
    property bool previewIsLoaded: false

    property int previewMetadataFieldsMaxWidth: 400
    property int urlFieldMaxWidth: 400
    property int extraParametersTextAreaMaxWidth: 400
    property int extraParametersPanelHeight: 100

    property string forceMP3ParamText: "Force MP3"
    property string forceOpusParamText: "Force Opus (.ogg)"

    id: container
    spacing: Style.medSpacing

    Component.onCompleted: {
        Logic.startup(StaccatoInterface);
    }

    ButtonGroup {
        id: extraParametersButtonGroup
        exclusive: false
        onClicked: button => {
            if(checkedButton === button) {

                checkedButton = null;
                importExtraParamsText = "";
                return;

            }

            checkedButton = button;
            if(checkedButton.text === forceMP3ParamText) {

                importExtraParamsText = "mp3"

            } else if(checkedButton.text === forceOpusParamText) {

                importExtraParamsText = "opus"

            }
        }
    }

    //URL input text box & download button
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

                onClicked: {
                    // Logic.loadTrackInfo(container);
                    container.previewIsLoaded = !container.previewIsLoaded;
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
            width: parent.width
            spacing: Style.tinySpacing

            Row {
                spacing: 0

                Rectangle {
                    width: Style.tinySpacing
                    height: 1
                    color: "#00000000"
                }

                RoundCheckBox {
                    id: extraParametersCheckBox
                    text: "Use advanced settings"
                    spacing: Style.tinySpacing
                    textColor: Style.offWhite
                    textSize: Style.smallTextSize
                }
            }

            RowLayout {
                width: parent.width
                height: extraParametersPanelHeight
                spacing: Style.medSpacing
                visible: extraParametersCheckBox.checked

                ScrollView {
                    id: extraParametersScrollView
                    contentHeight: height
                    clip: true
                    
                    ScrollBar.vertical.policy: ScrollBar.AsNeeded
                    ScrollBar.horizontal.policy: ScrollBar.AlwaysOff

                    Layout.fillWidth: true
                    Layout.fillHeight: true
                    Layout.maximumWidth: extraParametersTextAreaMaxWidth

                    Component.onCompleted: {
                        contentItem.boundsBehavior = Flickable.StopAtBounds;
                    }

                    TextArea {
                        id: extraParametersTextArea
                        wrapMode: TextArea.Wrap
                        selectByMouse: true
                        color: Style.offWhite
                        font.family: Style.monospaceFamily
                        font.pointSize: Style.smallTextSize
                        font.weight: Font.DemiBold

                        background: Rectangle {
                            radius: Style.buttonRadius
                            color: Style.lightBackground
                        }

                        onTextEdited: {
                            extraParametersButtonGroup.checkedButton = null;
                        }
                    }
                }

                ColumnLayout {
                    Layout.fillWidth: false
                    Layout.fillHeight: true

                    Repeater {
                        model: [container.forceMP3ParamText, container.forceOpusParamText]

                        RoundRadioButton {
                            text: modelData
                            ButtonGroup.group: extraParametersButtonGroup
                            textColor: Style.offWhite
                        }
                    }
                }

                //Empty component to shove everything to the left
                Item {
                    Layout.fillWidth: true
                    Layout.preferredHeight: 1
                }
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

            Layout.row: 0
            Layout.column: 0
            Layout.rowSpan: 1
            Layout.columnSpan: 4
        }

        TrackInfoEditor {
            id: previewEditor
            width: parent.width
            contentMaxWidth: previewMetadataFieldsMaxWidth
            readOnly: !overwriteMetadataCheckbox.checked
            enabled: previewIsLoaded
        }

        //Overwrite metadata checkbox
        RowLayout {
            spacing: Style.tinySpacing
            enabled: previewIsLoaded

            Layout.row: 4
            Layout.column: 0
            Layout.rowSpan: 1
            Layout.columnSpan: 4

            RoundCheckBox {
                id: overwriteMetadataCheckbox
                text: "Overwrite metadata"
                spacing: Style.tinySpacing
                textColor: Style.offWhite
                textSize: Style.smallTextSize

                Layout.leftMargin: Style.tinySpacing
                Layout.alignment: Qt.AlignVCenter
            }
            
            HelpToolTip {
                tooltipText: "Set your own details instead of using automatic info found online."

                Layout.leftMargin: width / 2
                Layout.preferredWidth: 15
                Layout.preferredHeight: 15
                Layout.alignment: Qt.AlignVCenter
            }
        }
    }    
}