
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

function IsValidEmail(email) {
    var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    return regex.test(email);
}
function sendEmail() {
    $('#loading').show();
    var toAddress = $('#to').val();
    var subject = $('#subject').val();
    var mailContent = $('#mailContent').val();
    var policyId = $('#policyId').val();
    var policyNumber=$('#policyNumber').val();
    if(toAddress == undefined || toAddress=='' || toAddress.length==0){
        alert('Please enter email address.');
        return;
    }
    var recipientMailAddress=toAddress.split(';');
    var i=0;
    for(;i<recipientMailAddress.length;i++){
        if(!IsValidEmail(recipientMailAddress[i])){
            alert('Please enter a valid email address.');
            return;
        }
    }

    $.ajax({
        url: '/pla/grouplife/policy/emailpolicy',
        type: 'POST',
        data: JSON.stringify({
            recipientMailAddress: toAddress.split(';'), subject: subject, mailContent:mailContent,
            policyId:policyId,policyNumber:policyNumber
        }),
        contentType: 'application/json; charset=utf-8',
        success: function (msg) {
            if (msg.status == '200') {
                $('#loading').hide();
                $('#alert-modal-success').modal('show');

                $('#alert-modal-success').on('hidden.bs.modal', function (){
                    window.close();
                });
            }
        }
    });
}
//});