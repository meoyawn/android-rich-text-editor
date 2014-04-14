function getHtml() {
    android.giveHtml(document.body.innerHTML);
}

function setHtml(data) {
    document.body.innerHTML = data;
}

function toggle(what) {
    document.execCommand(what);
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