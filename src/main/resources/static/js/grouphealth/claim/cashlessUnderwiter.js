
var searchPreAuthorizationModule = (function(){
    var PreAuthorizationServices = {};
    PreAuthorizationServices.PreAuthorizationSelected = undefined;
    this.PreAuthorizationName = null;
    PreAuthorizationServices.getTheItemSelected = function(ele){
        this.preAuthorizationId=$(ele).val();
        this.clientId=$(ele).attr("clientId");
        $("#PreAuthorization-view").prop("disabled","");
        $("#PreAuthorization-create").prop("disabled","");
        $(".btn-disabled").attr("disabled", false);
        this.PreAuthorizationName = $(ele).parent().find('input[type=hidden]').val();
    };
    PreAuthorizationServices.updatePreAuthorization = function () {
        var  clientId =this.clientId;
        var  preAuthorizationId =this.preAuthorizationId;
        window.location.href = "/pla/grouphealth/claim/cashless/preauthorizationrequest/loadunderwriterviewforupdateview?preAuthorizationId=" + preAuthorizationId +"&clientId="+ clientId;
    };

    PreAuthorizationServices.viewPreAuthorization =  function(){//activate deactive when click radio button
        if (this.PreAuthorizationSelected) {
            if ('PreAuthorization' === this.PreAuthorizationName) {
                var  preAuthorizationId =this.PreAuthorizationSelected;
                window.location.href = "/pla/grouphealth/claim/cashless/preauthorization/loadpreauthorizationrequest?PreAuthorizationId=" + preAuthorizationId +"&mode=view";
            } else {var  preAuthorizationId =this.PreAuthorizationSelected;
                window.location.href = "/pla/grouphealth/claim/cashless/preauthorization/loadpreauthorizationrequest?PreAuthorizationId=" + preAuthorizationId + "&mode=view";

            }
        }
    };

    PreAuthorizationServices.emailPolicy = function () {
        var  preAuthorizationId =this.PreAuthorizationSelected;
        //  window.location.href = "/pla/grouplife/policy/viewpolicy?policyId=" + policyId  + "&mode=view";

        window.open('/pla/grouplife/policy/openemailpolicy/'+preAuthorizationId,"_blank","toolbar=no,resizable=no," +
        "scrollable=no,menubar=no,personalbar=no,dependent=yes,dialog=yes,split=no,titlebar=no,resizable=no,location=no,left=100px");

    };

    PreAuthorizationServices.reload = function(){
        window.location.reload();
    };

    return PreAuthorizationServices;
})();
