var viewProposalModule = (function(){
    var services = {};
    services.selectedItem = "";
    services.getTheItemSelected = function(ele){
        this.selectedItem=$(ele).val();
        $(".btn-disabled").attr("disabled",false);
    };


    services.reload = function(){
        window.location.reload();
    };

    services.createProposal = function(){
        window.location.href = "creategrouplifeproposal"
    };

    services.modifyProposal = function(){
        var proposalId = this.selectedItem;
        $.ajax({
            url: '/pla/proposal/grouplife/getversionnumber/'+proposalId,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            success: function(msg) {
                if(msg.status=='200'){
                    window.location.href = "/pla/proposal/grouplife/creategrouplifeproposal?quotationId="+quotationId+"&version="+msg.data+"&mode=edit";
                }else if(msg.status=='500'){
                }
            }
        });
    };

    services.viewProposal = function(){
        var proposalId = this.selectedItem;
        $.ajax({
            url: '/pla/proposal/grouplife/getversionnumber/'+proposalId,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            success: function(msg) {
                if(msg.status=='200'){
                    window.location.href = "/pla/proposal/grouplife/creategrouplifeproposal?proposalId="+proposalId+"&version="+msg.data+"&mode=view";
                }else if(msg.status=='500'){
                }
            }
        });
    };

    return services ;
})();
