//define(['jquery','bootstrap','bootstrap-tags-input'],function($,bootstrap,tags){

function bodyOnLoad() {
    var to = $("#to");
    var patt = new RegExp("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$");
    var tagsClass = {
        tagClass: function (item) {
            if (!patt.test(item)) {
                return 'label label-danger'
            }
            return 'label label-primary';
        }
    };
    //to.tagsinput(tagsClass);
    //to.tagsinput('add', "shivaraj@nthdimenzion.com");
    tinymce.init({
        selector: "textarea#mailContent",
        width: "100%",
        height: 260,
        menubar: false,
        statusbar: false,
        plugins: [
            "advlist lists charmap print preview hr anchor pagebreak",
            "searchreplace visualblocks visualchars nonbreaking",
            "save table contextmenu directionality template paste textcolor"
        ],
        toolbar: " undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | print preview   | forecolor backcolor "
    });
};

function sendEmail() {
    var toAddress = $('#to').val();
    var subject = $('#subject').val();
    var mailContent = $('#mailContent').val();
    var quotationId = $('#quotationId').val();
    var quotationNumber=$('#quotationNumber').val();



    $.ajax({
            url: '/pla/quotation/grouplife/emailQuotation',
            type: 'POST',
            data: JSON.stringify({
                recipientMailAddress: toAddress.split(';'), subject: subject, mailContent:mailContent,
                quotationId:quotationId,quotationNumber:quotationNumber
            }),
            contentType: 'application/json; charset=utf-8',
            success: function (msg) {
                if (msg.status == '200') {
                    $('#alert-modal-success').show();
                }
            }
        });
}
//});