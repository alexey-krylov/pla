
var searchPreAuthorizationModule = (function(){
    var PreAuthorizationServices = {};
    PreAuthorizationServices.PreAuthorizationSelected = undefined;
    this.PreAuthorizationName = null;
    PreAuthorizationServices.getTheItemSelected = function(ele){
        this.batchNumber=$(ele).val();
        this.hcpCode=$(ele).attr("hcpCode");
        $("#PreAuthorization-view").prop("disabled","");
        $("#PreAuthorization-create").prop("disabled","");
        $(".btn-disabled").attr("disabled", false);
        this.PreAuthorizationName = $(ele).parent().find('input[type=hidden]').val();
    };
    PreAuthorizationServices.updatePreAuthorization = function () {
        var  batchNumber =this.batchNumber;
        var  hcpCode =this.hcpCode;

        window.location.href = "/pla/grouphealth/claim/cashless/claim/batchview?batchNumber=" + batchNumber +"&hcpCode="+ hcpCode +"&mode=edit";
    };

    PreAuthorizationServices.viewPreAuthorization =  function(){//activate deactive when click radio button
        var  batchNumber =this.batchNumber;
        var  hcpCode =this.hcpCode;

        window.location.href = "/pla/grouphealth/claim/cashless/batchview?batchNumber=" + batchNumber +"&hcpCode="+ hcpCode+"&mode=view";


    };
    return PreAuthorizationServices;
})();
