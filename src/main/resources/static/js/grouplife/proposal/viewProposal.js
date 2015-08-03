var viewProposalModule = (function () {
    var services = {};
    services.selectedItem = "";
    services.status = '';
    services.getTheItemSelected = function (ele) {
        this.selectedItem = $(ele).val();
        this.status = $(ele).parent().find('.proposalStatus').val();
       // console.log("***********************");
       // console.log(this.status);
        $(".btn-disabled").attr("disabled", false);
        if (this.status == 'RETURNED' || this.status == 'DRAFT'){
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
        console.log(this.status);
        if(this.status == 'RETURNED' || this.status == 'APPROVED'){
            window.location.href = "/pla/grouplife/proposal/editProposalReturnStatus?proposalId=" + proposalId  + "&mode=edit" + "&status=return";
        }else{
            window.location.href = "/pla/grouplife/proposal/editProposal?proposalId=" + proposalId  + "&mode=edit";
        }


    };

    services.viewProposal = function () {
        var proposalId = this.selectedItem;
        window.location.href = "/pla/grouplife/proposal/editProposal?proposalId=" + proposalId  + "&mode=view";

    };
    services.viewApprovalProposal = function () {
        var proposalId = this.selectedItem;

        window.location.href = "/pla/grouplife/proposal/viewApprovalProposal?proposalId=" + proposalId  + "&method=approval";

    };

    return services;
})();
