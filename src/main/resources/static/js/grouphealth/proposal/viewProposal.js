var viewProposalModule = (function () {
    var services = {};
    services.selectedItem = "";
    services.status = '';
    services.getTheItemSelected = function (ele) {
        this.selectedItem = $(ele).val();
        this.status = $(ele).parent().find('.proposalStatus').val();
        console.log("***********************");
        console.log(this.status);
        $(".btn-disabled").attr("disabled", false);
        if (this.status == 'RETURN' || this.status == 'DRAFT'){
            $('#modifyProposal').attr('disabled', false);

        }else{
            $('#modifyProposal').attr('disabled', true);

        }
    };

    services.reload = function () {
        window.location.reload();
    };

   /* services.createQuotation = function () {
        window.location.href = "creategrouplifequotation"
    };*/

    services.modifyProposal = function () {
        var proposalId = this.selectedItem;
        window.location.href = "/pla/grouphealth/proposal/editProposal?proposalId=" + proposalId  + "&mode=edit";
       /* $.ajax({
            url: '/pla/grouphealth/proposal/editProposal?proposalId=' + proposalId,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            success: function (msg) {
                if (msg.status == '200') {
                 //   window.location.href = "/pla/proposal/grouphealth/editproposal?proposalId=" + proposalId + "&version=" + msg.data + "&mode=edit";
                } else if (msg.status == '500') {
                }
            }
        });*/
    };

    services.viewProposal = function () {
        var proposalId = this.selectedItem;
        window.location.href = "/pla/grouphealth/proposal/editProposal?proposalId=" + proposalId  + "&mode=view";
      /*  $.ajax({
            url: '/pla/quotation/grouplife/getversionnumber/' + quotationId,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            success: function (msg) {
                if (msg.status == '200') {
                    window.location.href = "/pla/quotation/grouplife/creategrouplifequotation?quotationId=" + quotationId + "&version=" + msg.data + "&mode=view";
                } else if (msg.status == '500') {
                }
            }
        });*/
    };

    return services;
})();
