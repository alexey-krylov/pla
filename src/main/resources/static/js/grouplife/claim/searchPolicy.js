var viewPolicyModule = (function () {
    var services = {};
    services.selectedItem = "";
    services.status = '';
    services.getTheItemSelected = function (ele) {
        this.selectedItem = $(ele).val();

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
    alert(this.selectedItem);
        var policyId = this.selectedItem;
        window.location.href = "/pla/grouplife/claim/openclaimintimationpage/?policyId=" + policyId  + "&mode=view";

    };
     services.createIntimation = function () {
           var policyId = this.selectedItem;

           window.location.href ="/pla/grouplife/claim/openclaimintimationpage/?policyId=" + policyId + "&mode=edit";
          //  window.location.href ="/pla/grouplife/claim/openclaimintimationpage";
       };


    return services;
})();
