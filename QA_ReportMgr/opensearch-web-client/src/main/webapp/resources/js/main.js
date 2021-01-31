function spbGetCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function spbGreaterThan(num1, num2) {
    return num1 > num2;
}

function spbGreaterThanOrEqual(num1, num2) {
    return num1 >= num2;
}

function spbLessThan(num1, num2) {
    return num1 < num2;
}

function spbLessThanOrEqual(num1, num2) {
    return num1 <= num2;
}

function spbAndOperator(exp1, exp2) {
    return (exp1 && exp2);
}

function spbEndsWith(str, searchStr) {
    if (typeof String.prototype.endsWith === 'function') {
        return str.endsWith(searchStr);
    } else {
        return str.indexOf(searchStr, str.length - searchStr.length) !== -1;
    }
}

function fedeoclient_webapp_isChrome() {
    var isChrome = false;

    var isChromium = window.chrome;
    var winNav = window.navigator;
    var vendorName = winNav.vendor;
    var isOpera = typeof window.opr !== "undefined";
    var isIEedge = winNav.userAgent.indexOf("Edge") > -1;
    var isIOSChrome = winNav.userAgent.match("CriOS");

    if (isIOSChrome) {
        // is Google Chrome on IOS
        isChrome = true;
    } else if (
            isChromium !== null &&
            typeof isChromium !== "undefined" &&
            vendorName === "Google Inc." &&
            isOpera === false &&
            isIEedge === false
            ) {
        // is Google Chrome
        isChrome = true;
    } else {
        // not Google Chrome 
    }
    return isChrome;
}

function fedeoclient_webapp_slideToggle(toggleIconElem) {
    $toggleIcon = $(toggleIconElem);
    $content = $toggleIcon.nextAll('.collapse-expand-contents:first');
    $content.slideToggle(100, function () {
        if ($content.is(":visible") === true) {
            $toggleIcon.removeClass("fa-plus-square-o");
            $toggleIcon.addClass("fa-minus-square-o");
            $toggleIcon.prop('title', 'Collapse');
        } else {
            $toggleIcon.removeClass("fa-minus-square-o");
            $toggleIcon.addClass("fa-plus-square-o");
            $toggleIcon.prop('title', 'View more details');
        }
    });
}
