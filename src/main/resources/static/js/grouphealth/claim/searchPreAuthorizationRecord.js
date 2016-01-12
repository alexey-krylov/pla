
var searchPreAuthorizationModule = (function(){
    var PreAuthorizationServices = {};
    PreAuthorizationServices.PreAuthorizationSelected = undefined;
    this.PreAuthorizationName = null;
    PreAuthorizationServices.getTheItemSelected = function(ele){
        this.preAuthorizationId=$(ele).val();
        this.clientId=$(ele).attr("clientId");
        $("#PreAuthorization-view").prop("disabled","");
        $("#PreAuthorization-create").prop("disabled","");
        this.PreAuthorizationName = $(ele).parent().find('input[type=hidden]').val();
    };

    PreAuthorizationServices.createPreAuthorization = function () {
        var  clientId =this.clientId;
        var  preAuthorizationId =this.preAuthorizationId;
        window.location.href = "/pla/grouphealth/claim/cashless/preauthorizationrequest/loadpreauthorizationrequest?clientId=" + clientId +"&preAuthorizationId="+preAuthorizationId+"&mode=new";
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

//    PreAuthorizationServices.updatePreAuthorization = function(){
//        if(this.PreAuthorizationSelected){
//            if ('BROKER' === this.PreAuthorizationName) {
//                window.location.href = "/pla/core/PreAuthorization/updatet?PreAuthorizationId=" + this.PreAuthorizationSelected + "&mode=edit";
//            } else {
//                window.location.href = "/pla/core/PreAuthorization/update?PreAuthorizationId=" + this.PreAuthorizationSelected +"&mode=edit";
//            }
//        }
//    };
    PreAuthorizationServices.reload = function(){
        window.location.reload();
    };

    return PreAuthorizationServices;
})();
