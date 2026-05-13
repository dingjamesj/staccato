import QtQuick
import QtQuick.Layouts
import QtQuick.Controls
import staccato
import "libraryPanel.mjs" as Logic

pragma ComponentBehavior: Bound

//==========================================
// Panel for accessing tracks and playlists
//==========================================

ColumnLayout {
    property int playlistButtonHeight: 35

    id: container
    spacing: Style.smallSpacing

    ListModel {
        id: playlistsModel
    }

    Instantiator {
        model: playlistsModel
        delegate: LabeledIconButton {
            required property var model

            source: model.artworkSource
            text: model.playlistName
            subtext: ""
            width: playlistsColumn.width
            height: container.playlistButtonHeight
        }
        onObjectAdded: (index, object) => {
            object.parent = playlistsColumn
        }
    }

    ScrollView {
        id: scrollView
        contentHeight: height
        clip: true

        ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
        ScrollBar.vertical.policy: ScrollBar.AlwaysOff

        Layout.fillWidth: true
        Layout.fillHeight: true

        Component.onCompleted: {
            contentItem.boundsBehavior = Flickable.StopAtBounds;
        }

        Column {
            id: playlistsColumn
            width: scrollView.width
            height: scrollView.height
            spacing: 0
        }
    }
}