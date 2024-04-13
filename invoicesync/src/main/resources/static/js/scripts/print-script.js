document.getElementById("printButton").addEventListener("click", function(event) {    
    event.preventDefault();
    generatePdf();
});

function generatePdf() {
    var inv = document.getElementById("invoiceId").value;
    console.log("invoice id nya "+inv);
    fetch('/api/v1/download-invoice/'+inv, {
        method: 'GET'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to generate PDF');
        }
        return response.blob();
    })
    .then(blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'invoice.pdf';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
    })
    .catch(error => {
        console.error('Error generating PDF:', error);
    });
}