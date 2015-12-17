
var searchPayPointModule = (function(){
    var payPointServices = {};
    payPointServices.payPointSelected = undefined;
    this.payPointName = null;
    payPointServices.getTheItemSelected = function(ele){
        this.payPointSelected=$(ele).val();
        $("#payPoint-update").prop("disabled","");
        $("#payPoint-view").prop("disabled","");

        this.payPointName = $(ele).parent().find('input[type=hidden]').val();
       // alert(this.payPointName);
    };
    payPointServices.createPayPoint = function(){
        window.location.href = "openpaypointpage";
    };

//    payPointServices.createPayPoint = function () {
//        window.location.href = "pla/core/paypoint/create";
//    };

    payPointServices.viewPayPoint =  function(){//activate deactive when click radio button
        if (this.payPointSelected) {
            if ('PAYPOINT' === this.payPointName) {
               window.location.href = "/pla/core/paypoint/openpaypointpage?payPointId=" + this.payPointSelected +"&mode=view";
           } else {
                window.location.href = "/pla/core/paypoint/openpaypointpage?payPointId=" + this.payPointSelected + "&mode=view";

            }
        }
    };

    payPointServices.updatePayPoint = function(){
        if(this.payPointSelected){
            if ('BROKER' === this.payPointName) {
                window.location.href = "/pla/core/paypoint/updatet?payPointId=" + this.payPointSelected + "&mode=edit";
            } else {
                window.location.href = "/pla/core/paypoint/update?payPointId=" + this.payPointSelected +"&mode=edit";
            }
        }
    };
    payPointServices.reload = function(){
        window.location.reload();
    };

    return payPointServices;
})();
