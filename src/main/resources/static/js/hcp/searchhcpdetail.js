
var searchhcpModule = (function(){
    var hcpServices = {};
    hcpServices.hcpSelected = undefined;
    this.hcpName = null;
    hcpServices.getTheItemSelected = function(ele){
        this.hcpSelected=$(ele).val();
        $("#hcp-update").prop("disabled","");
        $("#hcp-view").prop("disabled","");

        this.hcpName = $(ele).parent().find('input[type=hidden]').val();
       // alert(this.hcpName);
    };
    hcpServices.createhcp = function(){
        window.location.href = "loadcreatepage";
    };

//    hcpServices.createhcp = function () {
//        window.location.href = "pla/core/hcp/create";
//    };

    hcpServices.viewhcp =  function(){//activate deactive when click radio button
        if (this.hcpSelected) {
            if ('hcp' === this.hcpName) {
               window.location.href = "/pla/core/hcp/loadcreatepage?hcpCode=" + this.hcpSelected +"&mode=view";
           } else {
                window.location.href = "/pla/core/hcp/loadcreatepage?hcpCode=" + this.hcpSelected + "&mode=view";

            }
        }
    };

    hcpServices.updatehcp = function(){
        if(this.hcpSelected){
            if ('hcp' === this.hcpName) {
                window.location.href = "/pla/core/hcp/loadcreatepage?hcpCode=" + this.hcpSelected + "&mode=edit";
            } else {
                window.location.href = "/pla/core/hcp/loadcreatepage?hcpCode=" + this.hcpSelected +"&mode=edit";
            }
        }
    };
    hcpServices.reload = function(){
        window.location.reload();
    };

    return hcpServices;
})();
