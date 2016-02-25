
var searchPreAuthorizationModule = (function(){
    var PreAuthorizationServices = {};
    PreAuthorizationServices.PreAuthorizationSelected = undefined;
    this.PreAuthorizationName = null;
    PreAuthorizationServices.getTheItemSelected = function(ele){
        this.hcpCode=$(ele).val();
        $("#PreAuthorization-view").prop("disabled","");
        $("#PreAuthorization-create").prop("disabled","");
        $(".btn-disabled").attr("disabled", false);
        this.PreAuthorizationName = $(ele).parent().find('input[type=hidden]').val();
    };
    PreAuthorizationServices.updatePreAuthorization = function () {

        var  hcpCode =this.hcpCode;

      window.location.href = "/pla/core/hcp/viewupdatepage?hcpCode="+hcpCode +"&mode=edit";
    };

    PreAuthorizationServices.viewPreAuthorization =  function(){//activate deactive when click radio button
        var  hcpCode =this.hcpCode;

        window.location.href = "/pla/core/hcp/viewupdatepage?hcpCode=" + hcpCode +"&mode=view";


    };




    return PreAuthorizationServices;
})();
