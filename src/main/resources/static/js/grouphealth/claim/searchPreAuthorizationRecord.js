
var searchPreAuthorizationModule = (function(){
    var PreAuthorizationServices = {};
    PreAuthorizationServices.PreAuthorizationSelected = undefined;
    this.PreAuthorizationName = null;
    PreAuthorizationServices.getTheItemSelected = function(ele){
        this.PreAuthorizationSelected=$(ele).val();
        $("#PreAuthorization-view").prop("disabled","");

        this.PreAuthorizationName = $(ele).parent().find('input[type=hidden]').val();
       // alert(this.PreAuthorizationName);
    };
//    PreAuthorizationServices.createPreAuthorization = function(){
//        window.location.href = "openPreAuthorizationpage";
//    };

//    PreAuthorizationServices.createPreAuthorization = function () {
//        window.location.href = "pla/core/PreAuthorization/create";
//    };

//    PreAuthorizationServices.viewPreAuthorization =  function(){//activate deactive when click radio button
//        if (this.PreAuthorizationSelected) {
//            if ('PreAuthorization' === this.PreAuthorizationName) {
//               window.location.href = "/pla/core/PreAuthorization/openPreAuthorizationpage?PreAuthorizationId=" + this.PreAuthorizationSelected +"&mode=view";
//           } else {
//                window.location.href = "/pla/core/PreAuthorization/openPreAuthorizationpage?PreAuthorizationId=" + this.PreAuthorizationSelected + "&mode=view";
//
//            }
//        }
//    };

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
