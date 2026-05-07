export var cpp;
export var dialogs;

export function startup(_cpp, _dialogs) {

    cpp = _cpp;
    dialogs = _dialogs;

}

//Does the equivalent of C++'s Track::string()
export function stringifyTrack(title, artists, album) {

    let artistsStr = "";
    if(artists.length === 0) {

        artistsStr = "Unknown Artists";

    } else if(artists.length === 1) {

        artistsStr = artists[0];

    } else if(artists.length === 2) {

        artistsStr = artists[0] + " and " + artists[1];

    } else {

        for(let i = 0; i < artists.length - 1; i++) {

            artistsStr += artists[i] + ", ";

        }

        artistsStr += "and " + artists[artists.length - 1];

    }

    return artistsStr + " – \"" + title + "\" from " + album;

}
