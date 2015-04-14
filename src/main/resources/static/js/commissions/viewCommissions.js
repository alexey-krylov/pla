var viewCommission = (function(){
    var commissionService = {};
    var getCommissionMode = function(){
        if(window.location.href.indexOf("NORMAL")!=-1){
            return "NORMAL"
        }
        return "OVERRIDE"
    };
    commissionService.commissionMode = getCommissionMode();

    commissionService.openCreatePage = function(){
        window.location.href = "/pla/core/commission/opencreatepage?mode="+this.commissionMode;
    };

    return commissionService;
})();