/*global document,android,setInterval*/

function editor() {
    return document.getElementById('editor');
}

function getHtml() {
    android.giveHtml(editor().innerHTML);
}

function setHtml(data) {
    editor().innerHTML = data;
}

var styles = [
    'bold',
    'italic',
    'underline',
    'strikeThrough',
    'insertOrderedList',
    'insertUnorderedList'
];

function onSelectionChanged() {
    var i;
    for (i = 0; i < styles.length; i += 1) {
        android.onSelectionChanged(styles[i], document.queryCommandState(styles[i]));
    }
}

function toggle(what) {
    document.execCommand(what);
    onSelectionChanged();
}

function checkPendingJavascript() {
    var script = android.getPendingJavaScript();
    if (script) {
        eval(script);
    }
}

setInterval(checkPendingJavascript, 60);