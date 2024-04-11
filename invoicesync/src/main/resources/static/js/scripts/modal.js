$(document).ready(function() {
    var closeModalButton = document.getElementById('closeModalButton');
    closeModalButton.addEventListener('click', function() {
        $('#errorModal').modal('hide');
    });

    var successMessage = /*[[${successMessage}]]*/ null;
    var errorMessage = /*[[${errorMessage}]]*/ null;

    // Memeriksa jika ada pesan untuk ditampilkan
    if (successMessage && successMessage !== '') {
        $("#successMessage").text(successMessage); // Mengatur teks pesan sukses
        $("#successModal").modal('show'); // Menampilkan modal sukses
    } else if (errorMessage && errorMessage !== '') {
        $("#errorMessage").text(errorMessage); // Mengatur teks pesan error
        $("#errorModal").modal('show'); // Menampilkan modal error
    }
});
