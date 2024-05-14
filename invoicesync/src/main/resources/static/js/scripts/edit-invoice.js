// Ambil data tax dari backend
var taxes = [];
function fetchTaxes() {
    console.log("Masuk fetch taxes")
    fetch('/api/taxes')
        .then(response => response.json())
        .then(data => {
            data.forEach(tax => {
                taxes.push(tax);
            });
        })
        .catch(error => {
            console.error('Error fetching taxes:', error);
        });
}
fetchTaxes();

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
            getAllProduct();
        })
        .catch(function(error) {
            console.error(error);
        });
    });
});


function getSelectedTaxPercentage() {
    var array = []
    var checkboxes = document.querySelectorAll('input[name="taxOption"]:checked')
    checkboxes.forEach(checkbox => {
        const taxId = checkbox.value;
        const selectedTax = taxes.find(tax => tax.taxId === parseInt(taxId));
        if (selectedTax) {
            array.push(selectedTax.taxPercentage)
        }
    });

    return array;
}

function updateSubtotal(priceInput, quantityInput, subtotalInput) {
    var price = parseFloat(priceInput.value);
    var quantity = parseInt(quantityInput.value);
    var total = price * quantity;
    subtotalInput.value = total.toFixed(2);
}

document.getElementById('totalDiscount').addEventListener('input', function() {
    countTaxes();
    updateGrandTotalInvoice();
});

async function countTaxes() {
    // Ambil tax yang dipilih
    var selectedTaxPercentage = getSelectedTaxPercentage();

    // Ambil subtotal dan hitung dengan discountnya
    var subtotal = document.getElementById("subtotal").value;
    var discount = parseFloat(document.getElementById('totalDiscount').value || 0);
    var amount = subtotal - ((discount/100.0)*subtotal);

    // Hitung total taxes (Rp)
    var taxTotal = 0;
    selectedTaxPercentage.forEach(tax => {
        taxTotal += (tax*amount/100)
    })

    document.querySelector('input[name="taxTotal"]').value = taxTotal.toFixed(2);
}

function updateGrandTotalInvoice() {
    var subtotalElement = document.querySelector('input[name="subtotal"]').value;
    var subtotal = parseFloat(subtotalElement || 0);

    var discountElement = document.getElementById('totalDiscount').value
    var discount = parseFloat(discountElement || 0);

    var taxTotalElement = document.querySelector('input[name="taxTotal"]').value;
    var taxTotal = parseFloat(taxTotalElement || 0);

    var total = subtotal - ((discount/100.0)*subtotal) + taxTotal;

    document.querySelector('input[name="grandTotal"]').value = total.toFixed(2);
}

document.getElementById("addRowInvoice").addEventListener("click", function() {
    var tableBody = document.getElementById("invoiceTableBody");
    var rowCount = tableBody.rows.length;

    var newRow = tableBody.insertRow(rowCount);

    var cellNo = newRow.insertCell(0);
    var cellDescription = newRow.insertCell(1);
    var cellQuantity = newRow.insertCell(2);
    var cellPrice = newRow.insertCell(3);
    var cellTotalPrice = newRow.insertCell(4);
    var cellAction = newRow.insertCell(5);

    cellNo.innerHTML = rowCount+1;
    cellDescription.innerHTML = '<input class="form-control" type="text" name="productDescription">';
    cellQuantity.innerHTML = '<input class="form-control quantity" type="number" name="productQuantity" value="1">';
    cellPrice.innerHTML = '<input class="form-control price" type="number" name="productPrice">';
    cellTotalPrice.innerHTML = '<input class="form-control subtotal" type="number" name="productSubtotal" readonly>';
    cellAction.innerHTML = '<i class="fa fa-trash delete-icon" style="color:#dc3545; cursor: pointer; margin:4px;"></i>' +
                           '<i class="fa fa-check check-icon" style="color:green; cursor: pointer; margin:4px;"></i>';

    var quantityInput = cellQuantity.querySelector('input');
    var priceInput = cellPrice.querySelector('input');
    var subtotalInput = cellTotalPrice.querySelector('input');

    quantityInput.addEventListener('change', function() {
        updateSubtotal(priceInput, this, subtotalInput);
        countTaxes();
        updateGrandTotalInvoice();
    });

    priceInput.addEventListener('change', function() {
        updateSubtotal(this, quantityInput, subtotalInput);
        countTaxes();
        updateGrandTotalInvoice();
    });

    var deleteIcon = cellAction.querySelector('.delete-icon');
    deleteIcon.addEventListener('click', function() {
        var productId = newRow.getAttribute('data-product-id');
        if (productId !== null){
            deleteProduct(productId, newRow);
        }
        else{
            tableBody.removeChild(newRow);
        }
    });

    var checkIcon = cellAction.querySelector('.check-icon');
    checkIcon.addEventListener('click', function() {
        var inputs = newRow.querySelectorAll('input[type="text"], input[type="number"]');
        inputs.forEach(function(input) {
            input.disabled = true;
        });
        handleCheckClick(this);
    });
});

document.getElementById("taxListEdit").addEventListener("click", function(){
    countTaxes();
    updateGrandTotalInvoice();
})

document.getElementById("invoiceTableBody").addEventListener("change", function(event) {
    var target = event.target;
    if (target.matches('input[name="productQuantity"]') || target.matches('input[name="productPrice"]')) {
        var row = target.closest('tr');
        var quantityInput = row.querySelector('input[name="productQuantity"]');
        var priceInput = row.querySelector('input[name="productPrice"]');
        var subtotalInput = row.querySelector('input[name="productSubtotal"]');
        updateSubtotal(priceInput, quantityInput, subtotalInput);
    }
});

function handleCheckClick(checkIcon) {
    var invoiceId = document.getElementById("invoiceId").value;
    var cellAction = checkIcon.parentElement;
    var tableRow = cellAction.parentElement;
    var cellDescription = tableRow.cells[1].querySelector('input[name="productDescription"]');
    var cellQuantity = tableRow.cells[2].querySelector('input[name="productQuantity"]');
    var cellPrice = tableRow.cells[3].querySelector('input[name="productPrice"]');
    var cellTotalPrice = tableRow.cells[4].querySelector('input[name="productSubtotal"]');
    
    var productData = {
        description: cellDescription.value,
        quantity: cellQuantity.value,
        price: cellPrice.value,
        totalPrice: cellTotalPrice.value,
    };

    fetch('/api/v1/create-product/'+invoiceId, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(productData)
    })
    .then(response => {
        checkIcon.style.display = 'none';
        if (response.ok) {
            console.error('Success');
        } else {
            console.error('Failed');
        }
        getAllProduct();
    })
    .catch(error => {
        console.error('Error creating product:', error);
    });
}

function updateCustomerContact() {
    var selectedCustomerName = document.getElementById("customerName").value;
    var selectedOption = document.querySelector('option[value="' + selectedCustomerName + '"]');
    if (selectedOption) {
        var selectedCustomerContact = selectedOption.getAttribute('data-contact');
        var selectedCustomerAddress = selectedOption.getAttribute('data-address');
        document.getElementById("customerContact").value = selectedCustomerContact;
        document.getElementById("customerAddress").value = selectedCustomerAddress;
    } else {
        document.getElementById("customerContact").value = "";
        document.getElementById("customerAddress").value = "";
    }
}

var closeModalButton = document.getElementById('closeModalButton');
closeModalButton.addEventListener('click', function() {
    $('#errorModal').modal('hide');
    $('#successModal').modal('hide');
});

function getAllProduct() {
    var invoiceId = document.getElementById("invoiceId").value;
    console.log("invoice id: " + invoiceId);
    
    fetch('/api/v1/invoice/product/' + invoiceId, {
        method: 'GET'
    })
    .then(function(response) {
        if (!response.ok) {
            throw new Error('Error fetching products: ' + response.status);
        }
        return response.json();
    })
    .then(function(products) {
        var subtotal = 0;
        products.forEach(function(product) {
            subtotal += parseFloat(product.totalPrice);
        });
        document.querySelector('input[name="subtotal"]').value = subtotal.toFixed(2);
        countTaxes();
        updateGrandTotalInvoice();
    })
    .catch(function(error) {
        console.error('Error:', error);
    });
}

function updateProductDocument() {
    var invoiceId = document.getElementById("invoiceId").value; 
    var fileInput = document.querySelector('input[name="productDocument"]');
    var formData = new FormData();
    formData.append('productDocument', fileInput.files[0]);

    fetch('/api/v1/add-product/'+invoiceId, {
        method: 'POST',
        body: formData
    })
    .then(function(response) {
        if (!response.ok) {
            throw new Error('Error creating product document: ' + response.status);
        }
        return response.json();
    })
    .then(function(data) {
        updateProductList(data);
        getAllProduct();
    })
    .catch(function(error) {
        var modal = document.getElementById("errorReadProductList");
        modal.classList.add('show');
        modal.setAttribute('aria-hidden', 'false');
        modal.style.display = 'block';
        console.error(error);
    });
}

function deleteProduct(productId, tableRow) {
    fetch('/api/v1/product/' + productId + '/delete', {
        method: 'POST'
    })
    .then(function(response) {
        if (!response.ok) {
            throw new Error('Error deleting product:', response.status);
        }
        console.log('Product deleted successfully');
        tableRow.parentNode.removeChild(tableRow);
        getAllProduct();
    })
    .catch(function(error) {
        console.error(error);
    });
}

function updateProductList(listProduct){
    var tableBody = document.getElementById("invoiceTableBody");
    var rowCount = tableBody.rows.length;

    listProduct.forEach(function(product, index) {
        var newRow = tableBody.insertRow();
        newRow.setAttribute('data-product-id', product.productId);

        var count = newRow.insertCell(0);
        count.textContent = rowCount + index + 1;

        var description = newRow.insertCell(1);
        description.textContent = product.description;

        var quantity = newRow.insertCell(2);
        quantity.textContent = product.quantity;

        var price = newRow.insertCell(3);
        price.textContent = product.price;

        var totalPrice = newRow.insertCell(4);
        totalPrice.textContent = product.totalPrice;

        var action = newRow.insertCell(5);
        action.innerHTML = '<i class="fa fa-trash delete-icon" style="color:#dc3545; cursor: pointer; margin:4px;"></i>'

        var deleteIcon = action.querySelector('.delete-icon');
        deleteIcon.addEventListener('click', function() {
            var productId = newRow.getAttribute('data-product-id');
            if (productId !== null){
                deleteProduct(productId, newRow);
            }
            else{
                tableBody.removeChild(newRow);
            }
        });
    });
}

function hideErrorReadProductList() {
    var modal = document.getElementById("errorReadProductList");
    modal.classList.remove('show');
    modal.setAttribute('aria-hidden', 'true');
    modal.style.display = 'none';
}