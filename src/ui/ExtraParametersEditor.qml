import QtQuick
import QtQuick.Controls
import QtQuick.Layouts
import staccato

RowLayout {
    property alias text: textArea.text

    property int textAreaMaxWidth: 400

    property string forceMP3ParamText: "Force MP3"
    property string forceOpusParamText: "Force Opus (.ogg)"

    id: container
    spacing: Style.medSpacing
    visible: extraParametersCheckBox.checked

    ButtonGroup {
        id: extraParametersButtonGroup
        exclusive: false
        onClicked: button => {
            if(checkedButton === button) {

                checkedButton = null;
                text = "";
                return;

            }

            checkedButton = button;
            if(checkedButton.text === forceMP3ParamText) {

                text = "mp3"

            } else if(checkedButton.text === forceOpusParamText) {

                text = "opus"

            }
        }
    }

    ScrollView {
        id: extraParametersScrollView
        contentHeight: height
        contentWidth: width
        clip: true
        
        ScrollBar.vertical.policy: ScrollBar.AsNeeded
        ScrollBar.horizontal.policy: ScrollBar.AlwaysOff

        Layout.fillWidth: true
        Layout.fillHeight: true
        Layout.maximumWidth: textAreaMaxWidth

        Component.onCompleted: {
            contentItem.boundsBehavior = Flickable.StopAtBounds;
        }

        TextArea {
            id: textArea
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