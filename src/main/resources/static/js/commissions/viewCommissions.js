var viewCommission = (function(){
    var commissionService = {};
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

    return commissionService;
})();