var viewCommission = (function(){
    var commissionService = {};
    commissionService.selectedCommission=null;
    commissionService.getTheItemSelected = function(ele){
        this.selectedCommission=$(ele).val();
        $("#commission-view").prop("disabled","");
        $("#commission-update").prop("disabled","");
    };
    var getCommissionMode = function(){
        if(window.location.href.indexOf("NORMAL")!=-1){
            return "Normal"
        }
        return "Override"
    };
    commissionService.commissionMode = getCommissionMode();

    commissionService.openCreatePage = function(){
        window.location.href = "/pla/core/commission/opencreatepage/"+this.commissionMode;
    };

    commissionService.viewCommission  = function(){
        window.location.href = "/pla/core/commission/opencreatepage/"+this.commissionMode+"?commissionId="+this.selectedCommission+"&type=view";
    };
    commissionService.updateCommission  = function(){
        window.location.href = "/pla/core/commission/opencreatepage/"+this.commissionMode+"?commissionId="+this.selectedCommission+"&type=update";
    };
    return commissionService;
})();