/**
Copyright (c) 2026 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/

/*
 * Keep the user's place on the page after liking, bookmarking, commenting,
 * following, or voting.
 *
 * Those buttons submit a form, and the server redirects back to the same
 * page, which normally reloads scrolled all the way to the top. Right before
 * a form is submitted we remember the scroll position, and when that same
 * page loads again we scroll back to it.
 */
var SCROLL_KEY = 'csx370-scroll-position';

function saveScrollPosition(form) {
    // Creating a new post or poll should land at the top, where the new post shows up.
    var action = (form && form.getAttribute('action')) || '';
    if (/\/create(post|poll)$/.test(action)) {
        return;
    }

    try {
        sessionStorage.setItem(SCROLL_KEY, JSON.stringify({
            page: location.pathname + location.search,
            y: window.scrollY,
            time: Date.now()
        }));
    } catch (e) {
        // Storage can be blocked (e.g. some private windows). Then we just don't restore.
    }
}

function takeSavedScrollPosition() {
    try {
        var saved = JSON.parse(sessionStorage.getItem(SCROLL_KEY));
        sessionStorage.removeItem(SCROLL_KEY);

        // Only restore on the same page, and only right after the form was sent.
        if (saved && saved.page === location.pathname + location.search
                && Date.now() - saved.time < 10000) {
            return saved.y;
        }
    } catch (e) {
        // Ignore unreadable or blocked storage.
    }
    return null;
}

// Remember the position for every form submission on the page.
document.addEventListener('submit', function (e) {
    saveScrollPosition(e.target);
}, true);

var restoreY = takeSavedScrollPosition();

// Make sure the dom is loaded.
document.addEventListener('DOMContentLoaded', function () {
    if (restoreY !== null) {
        window.scrollTo(0, restoreY);
    }

    // submittable is expected to be text fields.
    var submittables = document.getElementsByClassName('submittable');

    for (var submittable of submittables) {
        // Make text field submit the enclosing form when the enter key is pressed.
        submittable.addEventListener('keydown', function (e) {
            // Check if Enter was pressed without the Shift key
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault(); // Prevent new lines.
                saveScrollPosition(this.form); // form.submit() skips the submit event, so save here.
                this.form.submit(); // Submit the form.
                console.log(this.form + ' was submitted.');
            }
        });
    }
});

// Scroll again once icons and images have finished loading, in case the page height changed.
window.addEventListener('load', function () {
    if (restoreY !== null) {
        window.scrollTo(0, restoreY);
    }
});
