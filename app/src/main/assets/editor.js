function editor() {
    return document.getElementById('editor');
}

function getHtml() {
    android.giveHtml(editor().innerHTML);
}

function setHtml(data) {
    editor().innerHTML = data;
}

function toggle(what) {
    document.execCommand(what);
    onSelectionChanged();
}

function query(what) {
    return document.queryCommandState(what)
}

function onSelectionChanged() {
    var bold = query('bold');
    var italic = query('italic');
    var underline = query('underline');
    var strikeThrough = query('strikeThrough');
    var ordered = query('insertOrderedList');
    var unordered = query('insertUnorderedList');
    android.onSelectionChanged(bold, italic, underline, strikeThrough, ordered, unordered);
}

function checkPendingJavascript() {
    var script = android.getPendingJavaScript();
    if (script) {
        eval(script);
    }
}

setInterval(checkPendingJavascript, 60);