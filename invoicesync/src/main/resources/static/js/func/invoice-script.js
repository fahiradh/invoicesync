const dropArea = document.querySelector(".drag-area");
const dragText = dropArea.querySelector("header");
const fileInput = document.getElementById("image");

const browseFileLink = document.getElementById("browseFileLink");

browseFileLink.addEventListener("click", function(event) {
    event.preventDefault();
    fileInput.click();
});

fileInput.addEventListener("change", function(){
    file = this.files[0];
    dropArea.classList.add("active");
    showFile();
});

dropArea.addEventListener("dragover", (event)=>{
    event.preventDefault();
    dropArea.classList.add("active");
    dragText.textContent = "Release to Upload File";
});

dropArea.addEventListener("dragleave", ()=>{
    dropArea.classList.remove("active");
    dragText.textContent = "Drag & Drop to Upload File";
});

dropArea.addEventListener("drop", (event)=>{
    event.preventDefault();
    file = event.dataTransfer.files[0];
    showFile();
});

function showFile(){
    let fileType = file.type;
    let validExtensions = ["image/jpeg", "image/jpg", "image/png"];
    if (validExtensions.includes(fileType)){
        let fileReader = new FileReader();
        fileReader.onload = ()=>{
            let fileUrl = fileReader.result;
            let imgTag = `<img src="${fileUrl}" alt="image">`;
            dropArea.innerHTML = imgTag;
        }
        fileReader.readAsDataURL(file);
    }else{
        alert("This is not an image file!");
        dropArea.classList.remove("active");
        dragText.textContent = "Drag & Drop to Upload File"
    }
}

function getAllTableData() {
    var tableBody = document.getElementById("invoiceTableBody");
    var tableDataList = [];

    for (var i = 0; i < tableBody.rows.length; i++) {
        var row = tableBody.rows[i];
        var rowData = {};

        rowData.name = row.cells[1].querySelector('input[name="productName"]').value;
        rowData.description = row.cells[2].querySelector('input[name="productDescription"]').value;
        rowData.quantity = row.cells[3].querySelector('input[name="productQuantity"]').value;
        rowData.price = row.cells[4].querySelector('input[name="productPrice"]').value;
        rowData.totalPrice = row.cells[5].querySelector('input[name="productSubtotal"]').value;

        tableDataList.push(rowData);
    }

    return tableDataList;
}

function updateSubtotal(priceInput, quantityInput, subtotalInput) {
    var price = parseFloat(priceInput.value);
    var quantity = parseInt(quantityInput.value);
    var total = price * quantity;
    subtotalInput.value = total.toFixed(2);
}

function updateSubtotalInvoice() {
    var subtotalElements = document.querySelectorAll('.subtotal');
    var total = 0;
    subtotalElements.forEach(element => {
        total += parseFloat(element.value || 0);
    });
    document.querySelector('input[name="subtotal"]').value = total.toFixed(2);
}

function updateRowNumbers(tableBody) {
    var rows = tableBody.querySelectorAll('tr');
    for (var i = 0; i < rows.length; i++) {
        var cells = rows[i].querySelectorAll('td');
        cells[0].innerHTML = i + 1;
    }
}

document.getElementById("addRowInvoice").addEventListener("click", function() {
    var tableBody = document.getElementById("invoiceTableBody");
    var rowCount = tableBody.rows.length;

    var newRow = tableBody.insertRow(rowCount);

    var cellNo = newRow.insertCell(0);
    var cellProduct = newRow.insertCell(1);
    var cellDescription = newRow.insertCell(2);
    var cellQuantity = newRow.insertCell(3);
    var cellPrice = newRow.insertCell(4);
    var cellTotalPrice = newRow.insertCell(5);
    var cellAction = newRow.insertCell(6);

    cellNo.innerHTML = rowCount+1;
    cellProduct.innerHTML = '<input class="form-control" type="text" name="productName">';
    cellDescription.innerHTML = '<input class="form-control" type="text" name="productDescription">';
    cellQuantity.innerHTML = '<input class="form-control quantity" type="number" name="productQuantity" value="1">';
    cellPrice.innerHTML = '<input class="form-control price" type="number" name="productPrice">';
    cellTotalPrice.innerHTML = '<input class="form-control subtotal" type="number" name="productSubtotal" readonly>';
    cellAction.innerHTML = '<i class="fa fa-trash delete-icon" style="color:#dc3545; cursor: pointer;"></i>' +
                           '<i class="fa fa-check check-icon" style="color:green; cursor: pointer;"></i>';

    var quantityInput = cellQuantity.querySelector('input');
    var priceInput = cellPrice.querySelector('input');
    var subtotalInput = cellTotalPrice.querySelector('input');

    quantityInput.addEventListener('change', function() {
        updateSubtotal(priceInput, this, subtotalInput);
        updateSubtotalInvoice();
    });

    priceInput.addEventListener('change', function() {
        updateSubtotal(this, quantityInput, subtotalInput);
        updateSubtotalInvoice();
    });

    var deleteIcon = cellAction.querySelector('.delete-icon');
    deleteIcon.addEventListener('click', function() {
        tableBody.removeChild(newRow);
        updateRowNumbers(tableBody);
        updateSubtotalInvoice();
    });

    var checkIcon = cellAction.querySelector('.check-icon');
    checkIcon.addEventListener('click', handleCheckClick);
});

function handleCheckClick() {
    var checkIcon = this;
    var cellAction = checkIcon.parentElement;
    var tableRow = cellAction.parentElement;
    var cellProduct = tableRow.cells[1].querySelector('input[name="productName"]');
    var cellDescription = tableRow.cells[2].querySelector('input[name="productDescription"]');
    var cellQuantity = tableRow.cells[3].querySelector('input[name="productQuantity"]');
    var cellPrice = tableRow.cells[4].querySelector('input[name="productPrice"]');
    var cellTotalPrice = tableRow.cells[5].querySelector('input[name="productSubtotal"]');
    
    var productData = {
        name: cellProduct.value,
        description: cellDescription.value,
        quantity: cellQuantity.value,
        price: cellPrice.value,
        totalPrice: cellTotalPrice.value
    };

    fetch('/api/v1/create-product', {
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