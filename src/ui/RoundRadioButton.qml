import QtQuick
import QtQuick.Controls
import QtQuick.Shapes

RadioButton {
    property int boxSize: 15
    property int textSize: Style.normalTextSize
    property double ringWidthProportion: 0.45

    property color backgroundColor: Style.gray
    property color backgroundHoverColor: Qt.lighter(backgroundColor, 1.2)
    property color backgroundPressedColor: Qt.lighter(backgroundColor, 1.5)
    property color backgroundDisabledColor: Qt.darker(backgroundColor, 1.5)
    
    property color defaultColor: Style.purple
    property color hoverColor: Qt.lighter(defaultColor, 1.2)
    property color pressedColor: Qt.lighter(defaultColor, 1.5)
    property color disabledColor: Qt.darker(defaultColor, 1.5)

    property color textColor: Style.white
    property color disabledTextColor: Qt.darker(textColor, 1.5)

    id: container
    hoverEnabled: true

    indicator: Shape {
        anchors.verticalCenter: parent.verticalCenter
        width: container.boxSize
        height: container.boxSize
        layer.enabled: true
        layer.samples: 4 // Anti-aliasing for smooth edges

        ShapePath {
            fillColor: "#00000000"
            strokeColor: (
                container.enabled ? (
                    container.pressed ? (
                        container.checked ? pressedColor : backgroundPressedColor
                    ) : container.hovered ? (
                        container.checked ? hoverColor : backgroundHoverColor
                    ) : container.checked ? defaultColor : backgroundColor
                ) : container.checked ? disabledColor : backgroundDisabledColor
            )
            strokeWidth: boxSize * ringWidthProportion / 2
            capStyle: ShapePath.RoundCap // Rounded ends for the ring

            PathAngleArc {
                centerX: boxSize / 2
                centerY: boxSize / 2
                radiusX: boxSize * (1.0 - ringWidthProportion) / 2
                radiusY: boxSize * (1.0 - ringWidthProportion) / 2
                startAngle: 0
                sweepAngle: 360 // Change this to 270 for a 3/4 ring
            }
        }
    }

    contentItem: Text {
        anchors.verticalCenter: parent.verticalCenter
        anchors.left: indicator.right
        anchors.leftMargin: container.spacing
        text: container.text
        font.family: Style.mainFontFamily
        font.pointSize: container.textSize
        color: container.enabled ? container.textColor : container.disabledTextColor
        verticalAlignment: Text.AlignVCenter
    }
}