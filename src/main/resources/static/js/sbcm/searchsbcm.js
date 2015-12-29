
var searchsbcmModule = (function(){
    var sbcmServices = {};
    sbcmServices.sbcmSelected = undefined;
    this.planName = null;
    sbcmServices.getTheItemSelected = function(ele){
        this.sbcmSelected=$(ele).val();
//        $("#sbcm-update").prop("disabled","");
        $("#sbcm-view").prop("disabled","");

        this.sbcmName = $(ele).parent().find('input[type=hidden]').val();
       // alert(this.sbcmName);
    };
    sbcmServices.createsbcm = function(){
        window.location.href = "getsbcmview";
    };

    sbcmServices.createsbcm = function () {
        window.location.href = "pla/core/sbcm/getsbcmview";
    };

    sbcmServices.viewsbcm =  function(){//activate deactive when click radio button
        if (this.sbcmSelected) {
            if ('sbcm' === this.planName) {
               window.location.href = "/pla/core/sbcm/getsbcmview?planCode=" + this.sbcmSelected +"&mode=view";
           } else {
                window.location.href = "/pla/core/sbcm/getsbcmview?planCode=" + this.sbcmSelected + "&mode=view";

            }
        }
    };
    sbcmServices.inactivesbcm=function(){
     window.location.href = "/pla/core/sbcm/getsbcmview?planCode=" + this.sbcmSelected + "&mode=view";
    }
    sbcmServices.reload = function(){
        window.location.reload();
    };

    return sbcmServices;
})();
