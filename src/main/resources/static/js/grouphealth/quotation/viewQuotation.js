var viewQuotationModule = (function () {
    var services = {};
    services.selectedItem = "";
    services.status = '';
    services.getTheItemSelected = function (ele) {
        this.selectedItem = $(ele).val();
        this.status = $(ele).parent().find('input[type=hidden]').val();
        $(".btn-disabled").attr("disabled", false);
        if (this.status == 'GENERATED'){
            $('#emailaddress').attr('disabled', false);
            $('#print').attr('disabled', false);
        }else{
            $('#emailaddress').attr('disabled', true);
            $('#print').attr('disabled', true);
        }
    };

    services.reload = function () {
        window.location.reload();
    };

    services.createQuotation = function () {
        window.location.href = "creategrouphealthquotation"
    };

    services.printQuotation = function () {
        window.location.href='/pla/quotation/grouphealth/printquotation/'+this.selectedItem;
    }

    services.emailQuotation = function (){
        window.open('/pla/quotation/grouphealth/openemailquotation/'+this.selectedItem,"_blank","toolbar=no,resizable=no," +
        "scrollable=no,menubar=no,personalbar=no,dependent=yes,dialog=yes,titlebar=no,resizable=no,location=no,left=100px");
    }

    services.modifyQuotation = function () {
        var quotationId = this.selectedItem;
        $.ajax({
            url: '/pla/quotation/grouphealth/getversionnumber/' + quotationId,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            success: function (msg) {
                if (msg.status == '200') {
                    window.location.href = "/pla/quotation/grouphealth/creategrouphealthquotation?quotationId=" + quotationId + "&version=" + msg.data + "&mode=edit";
                } else if (msg.status == '500') {
                }
            }
        });
    };

    services.viewQuotation = function () {
        var quotationId = this.selectedItem;
        $.ajax({
            url: '/pla/quotation/grouphealth/getversionnumber/' + quotationId,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            success: function (msg) {
                if (msg.status == '200') {
                    window.location.href = "/pla/quotation/grouphealth/creategrouphealthquotation?quotationId=" + quotationId + "&version=" + msg.data + "&mode=view";
                } else if (msg.status == '500') {
                }
            }
        });
    };

    return services;
})();
