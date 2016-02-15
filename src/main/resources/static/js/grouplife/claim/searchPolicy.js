var viewPolicyModule = (function () {
    var services = {};
    services.selectedItem = "";
    services.status = '';
    services.getTheItemSelected = function (ele) {
        this.selectedItem = $(ele).val();
        this.status = $(ele).parent().find('.claimStatus').val();
        //$(".btn-disabled").attr("disabled", false);

        if(this.status == 'Claim Registered'){
            $('#modify').attr('disabled', false);
            $('#view').attr('disabled', false);
            $('#register').attr('disabled', true);
        }else if(this.status == 'Claim Intimated'){
            $('#register').attr('disabled', false);
            $('#view').attr('disabled', false);
            $('#modify').attr('disabled', true);
        }else{
            $('#create').attr('disabled', false);
            $('#proceed').attr('disabled', false);
            $('#view').attr('disabled', false);
            $('#register').attr('disabled', true);
            $('#modify').attr('disabled', true);
            //$(".btn-disabled").attr("disabled", false);
        }
    };

    services.reload = function () {
        window.location.reload();
    };
    services.printPolicy = function () {
        var policyId = this.selectedItem;
      //  window.location.href = "/pla/grouphealth/policy/viewpolicy?policyId=" + policyId  + "&mode=view";

    };
    services.emailPolicy = function () {
        var policyId = this.selectedItem;
      //  window.location.href = "/pla/grouphealth/policy/viewpolicy?policyId=" + policyId  + "&mode=view";

    };
    services.viewPolicy = function () {
    alert(this.selectedItem);
        var policyId = this.selectedItem;
        window.location.href = "/pla/grouplife/claim/openclaimintimationpage/?policyId=" + policyId  + "&mode=view";

    };
     services.createIntimation = function () {
           var policyId = this.selectedItem;

           window.location.href ="/pla/grouplife/claim/openclaimintimationpage/?policyId=" + policyId + "&mode=edit";
          //  window.location.href ="/pla/grouplife/claim/openclaimintimationpage";
       };
    services.createRegistration = function () {
        var claimId = this.selectedItem;
        window.location.href ="/pla/grouplife/claim/openclaimregistrationpage/?claimId=" + claimId + "&mode=edit";
    };

    services.modifyRegistration = function () {
        var claimId = this.selectedItem;
        window.location.href ="/pla/grouplife/claim/openclaimregistrationpage/?claimId=" + claimId + "&mode=edit";
    };

    services.viewClaim = function () {
        //alert(this.selectedItem);
        var claimId = this.selectedItem;
        var claimStatus=this.status;
        if(claimStatus == 'Claim Intimated'){
            window.location.href ="/pla/grouplife/claim/openclaimintimationpage/?claimId=" + claimId + "&mode=view";
        }else{
            window.location.href ="/pla/grouplife/claim/openclaimregistrationpage/?claimId=" + claimId + "&mode=view";
        }
    };

    services.viewApprovalClaimRegistration=function(){
        var claimId = this.selectedItem;
        var claimStatus=this.status;
        window.location.href ="/pla/grouplife/claim/viewapprovalclaim/?claimId=" + claimId + "&mode=edit";
        //window.location.href = "/pla/individuallife/proposal/viewApprovalProposal?proposalId=" + proposalId + "&status=return" + "&mode=edit";
    }
    return services;
})();
