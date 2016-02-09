
var searchPreAuthorizationModule = (function(){
    var PreAuthorizationServices = {};
    PreAuthorizationServices.PreAuthorizationSelected = undefined;
    this.PreAuthorizationName = null;
    PreAuthorizationServices.getTheItemSelected = function(ele){
        this.groupHealthCashlessClaimId=$(ele).val();
        this.clientId=$(ele).attr("clientId");
        $("#PreAuthorization-view").prop("disabled","");
        $("#PreAuthorization-create").prop("disabled","");
        $(".btn-disabled").attr("disabled", false);
        this.PreAuthorizationName = $(ele).parent().find('input[type=hidden]').val();
    };
    PreAuthorizationServices.updatePreAuthorization = function () {
        var  clientId =this.clientId;
   var groupHealthCashlessClaimId=this.groupHealthCashlessClaimId;
        window.location.href = "/pla/grouphealth/claim/cashless/claim/loadpageforghcashlessclaimupdateview?groupHealthCashlessClaimId=" + groupHealthCashlessClaimId +"&clientId="+ clientId +"&mode=edit" ;
    };

    PreAuthorizationServices.viewPreAuthorization =  function(){//activate deactive when click radio button
        var  clientId =this.clientId;

        //window.location.href = "/pla/grouphealth/claim/cashless/preauthorizationrequest/loadpreauthorizationviewforupdateview?preAuthorizationId=" + preAuthorizationId +"&clientId="+ clientId +"&mode=view";
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
