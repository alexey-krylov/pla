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
$.extend(jQuery.fn.dataTableExt.oSort, {
    "date-uk-pre": function (a) {
        var x;
        try {
            var dateA = a.replace(/ /g, '').split("/");
            var day = parseInt(dateA[0], 10);
            var month = parseInt(dateA[1], 10);
            var year = parseInt(dateA[2], 10);
            var date = new Date(year, month - 1, day)
            x = date.getTime();
        }
        catch (err) {
            x = new Date().getTime();
        }

        return x;
    },

    "date-uk-asc": function (a, b) {
        return a - b;
    },

    "date-uk-desc": function (a, b) {
        return b - a;
    }
});