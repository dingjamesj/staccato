import QtQuick
import QtQuick.Controls

pragma ComponentBehavior: Bound

TextField {
    id: container
    width: 200
    height: 25
    color: "#d4d4d4"
    font.family: interFont.name
    font.pointSize: 10
    padding: 5

    background: Rectangle {
        radius: 6
        color: "#303030"
    }
    
    FontLoader {
        id: interFont
        source: "qrc:/staccato/src/ui/resources/Inter-VariableFont_opsz,wght.ttf"
    }
}