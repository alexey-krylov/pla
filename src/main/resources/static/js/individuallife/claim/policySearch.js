var viewPolicyModule = (function () {
    var services = {};
    services.selectedItem = "";
    services.PolicyNumber="";services.clientId="";
    services.status = '';
    services.getTheItemSelected = function (ele) {
        this.selectedItem = $(ele).val();
        this.policyNumber=$(ele).parent().find('input[type=hidden]').val();
          this.clientId=$(ele).parent().find('input[type=hidden]').val();
        $(".btn-disabled").attr("disabled", false);

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

        var policyId = this.selectedItem;
        window.location.href = "/pla/grouplife/claim/openclaimintimationpage/?policyId=" + policyId  + "&mode=view";

    };
     services.createPolicy = function () {
           var policyId = this.selectedItem;
           var policyNumber=this.policyNumber;
           var clientId=this.clientId;
           window.location.href ="/pla/grouplife/claim/openclaimintimationpage/?policyId=" + policyId + "&mode=edit"+"&policyNumber="+policyNumber+"&familyId="+clientId;          //  window.location.href ="/pla/grouplife/claim/openclaimintimationpage";
       };


    return services;
})();
