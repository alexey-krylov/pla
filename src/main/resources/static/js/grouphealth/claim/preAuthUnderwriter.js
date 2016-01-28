
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
        window.location.href = "/pla/grouphealth/claim/cashless/preauthorizationrequest/loadunderwriterviewforupdate?preAuthorizationId=" + preAuthorizationId +"&clientId="+ clientId+"&mode=edit";
    };

    PreAuthorizationServices.viewPreAuthorization =  function(){
    //activate deactive when click radio button

         var preAuthorizationId = this.preAuthorizationId;
         var clientId=this.clientId;
         window.location.href = "/pla/grouphealth/claim/cashless/preauthorizationrequest/loadunderwriterviewforview?preAuthorizationId=" + preAuthorizationId +"&clientId="+ clientId+ "&mode=view";


    };


    PreAuthorizationServices.reload = function(){
        window.location.reload();
    };

    return PreAuthorizationServices;
})();
