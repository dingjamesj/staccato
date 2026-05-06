import QtQuick
import QtQuick.Controls
import QtQuick.Dialogs
import QtQuick.Window
import QtQuick.Layouts
import staccato

pragma Singleton

Item {
    property var mainWindow: null

    id: container

    function openFileDialog(_title, _filter, _action) {
        let fileDialog = mainWindow.fileDialog;
        fileDialog.title = _title;
        if(_filter) {

            fileDialog.nameFilters = _filter;
            
        }
        fileDialog.fileChosenAction = _action;
        fileDialog.open();
    }

    function openDialog(_title, _modal, _includeCancel) {
        messageDialog.title = _title;
        messageDialog.modal = _modal;
        messageDialog.includeCancelOption = _includeCancel;
        
        messageDialog.open();
    }

    function openMessageDialog(_title, _header, _message, _modal, _includeCancel) {
        messageDialog.title = _title;
        messageDialog.header = _header;
        messageDialog.message = _message;
        messageDialog.modal = _modal;
        messageDialog.includeCancelOption = _includeCancel;

        messageDialog.open();
    }

    Window {
        id: messageDialog

        property bool includeCancelOption: false
        property bool modal: true
        property string message: ""
        property string header: ""

        title: ""
        width: 420
        height: 240
        visible: false
        transientParent: mainWindow
        modality: modal ? Qt.ApplicationModal : Qt.NonModal
        color: Style.background
        flags: Qt.Dialog | Qt.CustomizeWindowHint | Qt.WindowTitleHint | Qt.WindowCloseButtonHint

        function open() {
            if (mainWindow) {
                x = mainWindow.x + (mainWindow.width - width) / 2;
                y = mainWindow.y + (mainWindow.height - height) / 2;
            }

            visible = true;
            raise();
            requestActivate();
        }

        ColumnLayout {
            anchors.fill: parent
            anchors.margins: 14
            spacing: 12

            Text {
                text: messageDialog.header
                font.family: Style.mainFontFamily
                font.pointSize: Style.h2TextSize
                font.weight: Font.DemiBold
                wrapMode: Text.NoWrap
                color: Style.white
                verticalAlignment: Text.AlignLeft
            }

            ScrollView {
                clip: true

                ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
                ScrollBar.vertical.policy: ScrollBar.AsNeeded

                Layout.fillWidth: true
                Layout.fillHeight: true

                Component.onCompleted: {
                    contentItem.boundsBehavior = Flickable.StopAtBounds;
                }

                TextArea {
                    text: messageDialog.message
                    readOnly: true
                    wrapMode: TextEdit.Wrap
                    selectByMouse: true
                    color: Style.offWhite
                    font.family: Style.mainFontFamily
                    font.pointSize: Style.normalTextSize
                    topPadding: 0
                    bottomPadding: 0
                    leftPadding: 0
                    rightPadding: 0
                }
            }

            Row {
                spacing: Style.smallSpacing

                Layout.alignment: Qt.AlignRight

                RoundButton {
                    width: 70
                    height: Style.buttonSize
                    radius: Style.buttonRadius
                    text: "Cancel"
                    defaultColor: Style.gray
                    visible: messageDialog.includeCancelOption
                    onClicked: messageDialog.close()
                }

                RoundButton {
                    radius: Style.buttonRadius
                    width: 70
                    height: Style.buttonSize
                    text: "OK"
                    defaultColor: Style.purple
                    onClicked: messageDialog.close()
                }
            }
        }
    }
}