alert('loaded');

function getHtml() {
    android.giveHtml(document.body.innerHTML);
}

function setHtml(html) {
    document.body.innerHTML = html;
}