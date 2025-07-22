function confirmDelete(element) {
    const title = element.getAttribute('data-title');
    const url = element.getAttribute('data-url');
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    if (confirm(`Are you sure you want to delete the artwork "${title}"? The action cannot be undone.`)) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = url;
        form.style.display = 'none';

        // adds the token to the form
        const csrfInput = document.createElement('input');
        csrfInput.type = 'hidden';
        csrfInput.name = '_csrf';
        csrfInput.value = csrfToken;
        form.appendChild(csrfInput);

        // adds the form to the DOM
        document.body.appendChild(form);
        form.submit();
    }

    return false;
}