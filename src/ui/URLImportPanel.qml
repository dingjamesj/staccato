import QtQuick
import QtQuick.Controls
import QtQuick.Layouts
import staccato
import "trackImporter.js" as Logic

Column {
    property alias importStatusText: statusText.text
    property alias importURLText: urlTextField.text
    property alias previewArtworkSource: previewArtwork.source
    property alias previewTitleText: previewTitleField.text
    property alias previewArtistsContainer: artistsTextFieldRow
    property alias previewAlbumText: previewAlbumField.text

    property bool previewIsLoading: false
    property bool previewIsLoaded: false

    property int previewMetadataFieldsMaxWidth: 400
    property int urlFieldMaxWidth: 400
    property int extraParametersTextAreaMaxWidth: 400
    property int extraParametersPanelHeight: 100

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
                return;

            }
            checkedButton = button;
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
            }

            RoundButton {
                radius: Style.buttonRadius
                text: "Download"
                defaultColor: Style.purple
                enabled: urlTextField.text.length > 0

                Layout.preferredWidth: 80
                Layout.fillHeight: true
            }

            RoundButton {
                radius: Style.buttonRadius
                text: "Load Preview"
                defaultColor: Style.gray
                enabled: previewIsLoading ? false : (urlTextField.text.length > 0)
                
                Layout.preferredWidth: 105
                Layout.fillHeight: true

                onClicked: {
                    Logic.loadTrackInfo(container);
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
                    spacing: Style.smallSpacing
                    textColor: Style.darkWhite
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
                        wrapMode: TextArea.Wrap
                        selectByMouse: true
                        color: Style.offWhite
                        font.pointSize: Style.smallTextSize
                        font.weight: Font.DemiBold

                        background: Rectangle {
                            radius: Style.buttonRadius
                            color: Style.lightBackground
                        }
                    }
                }

                ColumnLayout {
                    Layout.fillWidth: false
                    Layout.fillHeight: true

                    Repeater {
                        model: ["Force MP3", "Force Opus (.ogg)"]

                        RoundRadioButton {
                            text: modelData
                            ButtonGroup.group: extraParametersButtonGroup
                            textColor: Style.darkWhite
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

    //Track preview contents
    GridLayout {
        rows: 5
        columns: 4
        rowSpacing: Style.smallSpacing
        columnSpacing: Style.smallSpacing
        width: parent.width
        height: implicitHeight

        //Track preview title text
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

        //Text that says "Title: "
        Text {
            text: "Title: "
            font.family: Style.mainFontFamily
            font.pointSize: Style.normalTextSize
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: Style.white

            Layout.row: 1
            Layout.column: 0
        }

        //Text field for the title
        RoundTextField {
            id: previewTitleField
            enabled: container.previewIsLoaded

            Layout.row: 1
            Layout.column: 1
            Layout.preferredHeight: Style.buttonSize
            Layout.fillWidth: true;
            Layout.maximumWidth: container.previewMetadataFieldsMaxWidth
        }

        //Text that says "Artists: "
        Text {
            text: "Artists: "
            font.family: Style.mainFontFamily
            font.pointSize: Style.normalTextSize
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: Style.white

            Layout.row: 2
            Layout.column: 0
        }

        //List of artists for the preview
        RowLayout {
            spacing: Style.tinySpacing

            Layout.row: 2
            Layout.column: 1
            Layout.preferredHeight: Style.buttonSize
            Layout.fillWidth: true;
            Layout.maximumWidth: container.previewMetadataFieldsMaxWidth

            ScrollView {
                id: previewArtistsScrollView
                contentHeight: height
                clip: true

                ScrollBar.horizontal.policy: ScrollBar.AsNeeded
                ScrollBar.vertical.policy: ScrollBar.AlwaysOff

                Layout.fillWidth: true
                Layout.fillHeight: true
                
                Component.onCompleted: {
                    contentItem.boundsBehavior = Flickable.StopAtBounds;
                }

                RowLayout {
                    id: artistsTextFieldRow
                    width: previewArtistsScrollView.width
                    height: previewArtistsScrollView.height
                    spacing: Style.tinySpacing

                    RoundTextField {
                        id: initialArtistPreviewField
                        enabled: container.previewIsLoaded

                        Layout.preferredWidth: 1
                        Layout.fillWidth: true
                        Layout.fillHeight: true
                    }
                }
            }

            //Button to add a text field
            RoundButton {
                radius: Style.buttonRadius
                defaultColor: Style.gray
                imageSource: "qrc:/staccato/src/ui/resources/plus.svg"
                onClicked: {
                    Logic.addArtistTextField(container);
                }
                enabled: container.previewIsLoaded
                
                Layout.preferredWidth: height
                Layout.fillHeight: true
            }

            //Button to remove the last text field
            RoundButton {
                radius: Style.buttonRadius
                defaultColor: Style.gray
                imageSource: "qrc:/staccato/src/ui/resources/minus.svg"
                onClicked: {
                    Logic.removeArtistTextField(container);
                }
                enabled: container.previewIsLoaded ? (container.previewArtistsContainer.children.length > 0 ? true : false) : false

                Layout.preferredWidth: height
                Layout.fillHeight: true
            }
        }

        //Text that just says "Album: "
        Text {
            text: "Album: "
            font.family: Style.mainFontFamily
            font.pointSize: Style.normalTextSize
            font.weight: Font.DemiBold
            wrapMode: Text.NoWrap
            color: Style.white

            Layout.row: 3
            Layout.column: 0
        }

        //Text field for the preview album name
        RoundTextField {
            id: previewAlbumField
            enabled: container.previewIsLoaded

            Layout.row: 3
            Layout.column: 1
            Layout.preferredHeight: Style.buttonSize
            Layout.fillWidth: true;
            Layout.maximumWidth: container.previewMetadataFieldsMaxWidth
        }

        //Cover art
        RoundImage {
            id: previewArtwork
            radius: Style.buttonRadius
            source: ""

            Layout.fillHeight: true
            Layout.row: 1
            Layout.column: 2
            Layout.rowSpan: 3
            Layout.preferredWidth: height
            Layout.leftMargin: Style.smallSpacing
        }

        //Overwrite metadata checkbox
        Row {
            spacing: Style.smallSpacing

            Layout.row: 4
            Layout.column: 0
            Layout.rowSpan: 1
            Layout.columnSpan: 4

            CheckBox {
                text: "Overwrite metadata"
                enabled: container.previewIsLoaded
            }
            
            Rectangle {
                width: height
                height: parent.height
                color: "#0080ff"

                MouseArea {
                    id: helpMouseArea
                    anchors.fill: parent
                    hoverEnabled: true
                }

                ToolTip.visible: helpMouseArea.containsMouse
                ToolTip.text: "testing"
                ToolTip.delay: 500
            }
        }

        //Empty component to shove everything to the left
        Item {
            Layout.row: 1
            Layout.column: 3
            Layout.rowSpan: 3
            Layout.fillWidth: true
        }
    }
}