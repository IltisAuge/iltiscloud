let welcome_titles = [];

function loadWelcomeTitles() {
    var arr = [];
    $.getJSON('welcome-titles.json', function (json) {
        var list = json["welcome-titles"];
        list.forEach(function(obj) {
            console.log("loaded title " + obj);
            welcome_titles.push(obj); 
        });
    });
    console.log("Finished loading welcome titles.");
}

var latestTitle = null;
function getWelcomeTitle() {
    var i = null;
    do {
        i = Math.floor(Math.random() * welcome_titles.length);
    } while (latestTitle != null && latestTitle == i);
    console.log("i=" + i);
    latestTitle = i;
    return welcome_titles[i];
}