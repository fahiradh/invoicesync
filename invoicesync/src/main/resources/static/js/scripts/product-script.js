var updateDeleteIcons = document.querySelectorAll('.update-delete-icon');
updateDeleteIcons.forEach(function(icon) {
    icon.addEventListener('click', function() {
        var productId = this.closest('tr').id;
        fetch('/api/v1/product/' + productId + '/delete', {
            method: 'POST'
        })
        .then(function(response) {
            if (!response.ok) {
                throw new Error('Error deleting product:', response.status);
            }
            console.log('Product deleted successfully');
            var productRow = document.getElementById(productId);
            if (productRow) {
                productRow.remove();
            }
        })
        .catch(function(error) {
            console.error(error);
        });
    });
});
