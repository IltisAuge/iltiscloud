function scrollToTop() {
    window.scroll({top: 0, left: 0, behavior: 'smooth'});
}
            
function fadeIn(element) {
    var opacity = 0;
    var i = setInterval(function() {
        if (opacity >= 1) {
            opacity = 1;
            clearInterval(i);
        }
        element.css('opacity', '' + opacity);
        opacity += 0.01;
    }, 10);
}

function fadeOut(element) {
    var opacity = 1;
    var i = setInterval(function() {
        if (opacity <= 0) {
            opacity = 0;
            clearInterval(i);
        }
        element.css('opacity', '' + opacity);
        opacity -= 0.01;
    }, 10);
}

var languages = {
    "german": JSON.parse('{"name": "german", "displayname": "Deutsch"}'),
    "english": JSON.parse('{"name": "english", "displayname": "English"}'),
    "spanish": JSON.parse('{"name": "spanish", "displayname": "Español"}')
};

language = null;

function initUserLanguage() {
    var cookie = getCookie("language");
    language = getLanguage(cookie == null ? "german" : cookie);
    console.log("Init user language: " + language.name);
}

initUserLanguage();

function getLanguage(name) {
    return languages[name];
}

function setCookie(key, value) {
	cookies.key = value;
}

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

var designs = {
    "dark": JSON.parse('{"name": "dark", "translation": "design.dark.displayname"}'),
    "bright": JSON.parse('{"name": "bright", "translation": "design.bright.displayname"}')
}

design = null;

function initUserDesign() {
    var cookie = getCookie("design");
    design = getDesign(cookie == null ? "dark" : cookie);
    console.log("Init user design: " + design.name);
}

function getDesign(name) {
    return designs[name];
}

initUserDesign();

// Init string formatting..
if (!String.prototype.format) {
    String.prototype.format = function() {
        var args = arguments;
        return this.replace(/{(\d+)}/g, function(match, number) { 
            return typeof args[number] != 'undefined' ? args[number] : match;
        });
    };
}

function storeInLS(key, value, ttl) {
	const now = new Date();
	// `item` is an object which contains the original value
	// as well as the time when it's supposed to expire
	const item = {
		value: value,
		expiry: now.getTime() + ttl,
	};
	localStorage.setItem(key, JSON.stringify(item));
}

function getFromLS(key) {
	const itemStr = localStorage.getItem(key)
	// if the item doesn't exist, return null
	if (!itemStr) {
		return null;
	}
	const item = JSON.parse(itemStr);
	const now = new Date();
	// compare the expiry time of the item with the current time
	if (now.getTime() > item.expiry) {
		// If the item is expired, delete the item from storage
		// and return null
		localStorage.removeItem(key);
		return null;
	}
	return item.value;
}

function randomUUID() {
  return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
    (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
  );
}

function msToTime(duration) {
  var seconds = Math.floor((duration / 1000) % 60),
    minutes = Math.floor((duration / (1000 * 60)) % 60),
    hours = Math.floor((duration / (1000 * 60 * 60)) % 24);

  hours = (hours < 10) ? "0" + hours : hours;
  minutes = (minutes < 10) ? "0" + minutes : minutes;
  seconds = (seconds < 10) ? "0" + seconds : seconds;

  return hours + "h " + minutes + "min " + seconds + "s";
}