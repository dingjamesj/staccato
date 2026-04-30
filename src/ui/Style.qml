import QtQuick

pragma Singleton

//Global styling
Item {
    readonly property string mainFontFamily: interFont.name

    //Background colors
    readonly property color background: "#1e1e1e"
    readonly property color lightBackground: "#303030"

    //Text colors
    readonly property color white: "#ffffff"
    readonly property color offWhite: "#d4d4d4"
    readonly property color darkWhite: "#9a9a9a"

    //Accent colors
    readonly property color gray: "#434343"
    readonly property color purple: "#80005d"
    readonly property color lightPurple: "#93006b"
    readonly property color pink: "#ffb9ff"
    readonly property color red: "#cc3333"

    //Misc
    readonly property color m4aColor: "#d04ea5"

    FontLoader {
        id: interFont
        source: "qrc:/staccato/src/ui/resources/Inter-VariableFont_opsz,wght.ttf"
    }
}