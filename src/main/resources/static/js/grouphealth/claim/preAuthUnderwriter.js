
var searchPreAuthorizationModule = (function(){
    var PreAuthorizationServices = {};
    PreAuthorizationServices.PreAuthorizationSelected = undefined;
    this.PreAuthorizationName = null;
    PreAuthorizationServices.getTheItemSelected = function(ele){
        this.preAuthorizationId=$(ele).val();
        this.underwriterLevel=$(ele).attr("underwriterLevel");
        this.clientId=$(ele).attr("clientId");
        $("#PreAuthorization-view").prop("disabled","");
        $("#PreAuthorization-create").prop("disabled","");
        $(".btn-disabled").attr("disabled", false);
        this.PreAuthorizationName = $(ele).parent().find('input[type=hidden]').val();
    };

    PreAuthorizationServices.updatePreAuthorization = function () {
        var  underwriterLevel =this.underwriterLevel;
        var  preAuthorizationId =this.preAuthorizationId;
        var clientId=this.clientId;
        window.location.href = "/pla/grouphealth/claim/cashless/preauthorizationrequest/loadunderwriterviewforupdate?preAuthorizationId=" + preAuthorizationId +"&underwriterLevel="+ underwriterLevel+"&clientId="+clientId+"&mode=edit";
    };

    PreAuthorizationServices.viewPreAuthorization =  function(){
    //activate deactive when click radio button
         var preAuthorizationId = this.preAuthorizationId;
         var underwriterLevel=this.underwriterLevel;
        var clientId=this.clientId;
         window.location.href = "/pla/grouphealth/claim/cashless/preauthorizationrequest/loadunderwriterviewforview?preAuthorizationId=" + preAuthorizationId +"&underwriterLevel="+ underwriterLevel+"&clientId="+clientId+"&mode=view";


    };
    PreAuthorizationServices.emailPolicy = function () {
        var  preAuthorizationId =this.preAuthorizationId;
        //  window.location.href = "/pla/grouplife/policy/viewpolicy?policyId=" + policyId  + "&mode=view";
        window.open('/pla/grouphealth/claim/cashless/preauthorizationrequest/openemailpreAuthorization/'+preAuthorizationId,"_blank","toolbar=no,resizable=no," +
        "scrollable=no,menubar=no,personalbar=no,dependent=yes,dialog=yes,split=no,titlebar=no,resizable=no,location=no,left=100px");

    };

    PreAuthorizationServices.reload = function(){
        window.location.reload();
    };

    return PreAuthorizationServices;
})();
