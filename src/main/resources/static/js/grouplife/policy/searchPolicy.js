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
        //alert(policyId);
      //  window.location.href = "/pla/grouplife/policy/viewpolicy?policyId=" + policyId  + "&mode=view";

        window.open('/pla/grouplife/policy/openprintpolicy?policyId='+policyId,"_blank","toolbar=no,resizable=no," +
        "scrollable=no,menubar=no,personalbar=no,dependent=yes,dialog=yes,split=no,titlebar=no,resizable=no,location=no,left=100px");

    };
    services.emailPolicy = function () {
        var policyId = this.selectedItem;
      //  window.location.href = "/pla/grouplife/policy/viewpolicy?policyId=" + policyId  + "&mode=view";

        window.open('/pla/grouplife/policy/openemailpolicy/'+policyId,"_blank","toolbar=no,resizable=no," +
        "scrollable=no,menubar=no,personalbar=no,dependent=yes,dialog=yes,split=no,titlebar=no,resizable=no,location=no,left=100px");

    };
    services.viewPolicy = function () {
        var policyId = this.selectedItem;
        window.location.href = "/pla/grouplife/policy/viewpolicy?policyId=" + policyId  + "&mode=view";

    };


    return services;
})();
