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
        var policyId = this.selectedItem;
        window.location.href = "/pla/individuallife/policy/viewpolicy?policyId=" + policyId  + "&mode=view";

    };


    return services;
})();
