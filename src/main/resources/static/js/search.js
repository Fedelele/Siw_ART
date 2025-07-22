document.addEventListener('DOMContentLoaded', function () {
    const searchBar = document.querySelector('.search-bar');
    const listContainer = document.querySelector('.list-container');

    if (!searchBar || !listContainer) {
        return;
    }

    const items = listContainer.querySelectorAll('.card-item');

    searchBar.addEventListener('input', function (e) {
        const searchTerm = e.target.value.toLowerCase();

        items.forEach(item => {
            // Searches title
            const title = item.querySelector('.card-title')?.textContent.toLowerCase() || '';

            if (title.includes(searchTerm)) {
                item.style.display = '';
            } else {
                item.style.display = 'none';
            }
        });
    });
});