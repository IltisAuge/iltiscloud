let translations = { };

function loadTranslations() {
    var start = new Date().getTime();
    console.log("Loading translations...");
    var cache = getFromLS("translations");
    if (cache != null) {
        console.log("Using cached translations.");
        cacheFromJSON(JSON.parse(cache));
    } else {
       console.log("Get translations from server.");
        $.getJSON('translations.json', function (json) {
            cacheFromJSON(json);
            var s = JSON.stringify(translations);
            storeInLS("translations", s, 1000 * 10);
            console.log("Finished loading translations.");
        }); 
    }
    console.log("Translation loading took " + (new Date().getTime() - start) + "ms");
}

function cacheFromJSON(json) {
    for (var k in json) {
        if (json.hasOwnProperty(k)) {
            var value = json[k];
            var temp = { };
            for (var lang in value) {
                temp[lang] = value[lang];
            }
            translations[k] = temp;
        }
    }
}

function getTranslation(key, values) {
    var t = translations[key];
    var e = null;
    try {
        e = t[language.name];
    } catch (ex) { }
    return e == null ? "No translation found for " + key + "!" : e.format(values);
}

function applyNavbarTranslations() {
    $('#navbar-link-home').html(getTranslation("navbar.link.home", language));
    $('#navbar-link-wrappers').html(getTranslation("navbar.link.wrappers", language));
    $('#navbar-link-servers').html(getTranslation("navbar.link.servers", language));
    $('#navbar-link-settings').html(getTranslation("navbar.link.settings", language));
}