import QtQuick

pragma Singleton

//This is for global styling, so that FontLoader doesn't need to be created multiple times
Item {
    readonly property string mainFontFamily: interFont.name

    FontLoader {
        id: interFont
        source: "qrc:/staccato/src/ui/resources/Inter-VariableFont_opsz,wght.ttf"
    }
}