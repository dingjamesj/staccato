import QtQuick

pragma Singleton

//Global styling
Item {
    readonly property string mainFontFamily: interFont.name
    readonly property double mainFontHeight: interFontMetrics.height
    readonly property string monospaceFamily: cascadiaCodeFont.name
    readonly property string monospaceHeight: cascadiaCodeFontMetrics.height

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

    //Misc colors
    readonly property color m4aColor: "#d04ea5"

    //Sizing & spacing
    readonly property int tinySpacing: 4
    readonly property int smallSpacing: 8
    readonly property int medSpacing: 17
    readonly property int bigSpacing: 45
    readonly property int buttonSize: 25
    readonly property int buttonRadius: 6
    readonly property int smallButtonSize: 15
    readonly property int smallButtonRadius: 4
    readonly property int h1TextSize: 24
    property int h2TextSize: 16
    property int normalTextSize: 10
    property int smallTextSize: 8

    FontLoader {
        id: interFont
        source: "qrc:/staccato/src/ui/resources/Inter-VariableFont_opsz,wght.ttf"
    }

    FontMetrics {
        id: interFontMetrics
        font: interFont
    }

    FontLoader {
        id: cascadiaCodeFont
        source: "qrc:/staccato/src/ui/resources/CascadiaCode-VariableFont_wght.ttf"
    }

    FontMetrics {
        id: cascadiaCodeFontMetrics
        font: cascadiaCodeFont
    }
}