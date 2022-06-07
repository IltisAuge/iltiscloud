$(document).ready(function() {
    loadTranslations();
    $('#page-header').load("navbar.html");
    setTimeout(function() {
        applyNavbarTranslations();
        fadeIn($('#page-header'));
        
        replaceWord("name");
        replaceWord("status");
        replaceWord("online");
        replaceWord("offline");
        replaceWord("edit");
        replaceWord("uniqueid");
        replaceWord("address");
        replaceWord("ipaddress");
        replaceWord("port");
        replaceWord("players");
        replaceWord("type");
        replaceWord("wrapper");
        replaceWord("uptime");
        replaceWord("start");
        replaceWord("stop");
        replaceWord("kill");
        replaceWord("static");
        replaceWord("template");
        replaceWord("savesettings");
        replaceWord("username");
        replaceWord("password");
        replaceWord("group");
        replaceWord("language");
        replaceWord("design");
        replaceWord("dark");
        replaceWord("light");
        replaceWord("maxram");
        replaceWord("platform");
    }, 100);
    setTimeout(function() {
        fadeIn($('#page-body'));
    }, 300);
	//connect('127.0.0.1', 7998);
	//connectToMaster('192.168.2.105', 7998);
});

function replaceWord(word) {
    var translation = getTranslation("word." + word);
    $('.word-' + word).each(function(i, obj) {
        $(obj).html(translation);
    });
}