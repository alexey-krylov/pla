var viewQuotationModule = (function(){
    var services = {};
    services.selectedItem = "";
    services.getTheItemSelected = function(ele){
        this.selectedItem=$(ele).val();
        $(".btn-disabled").attr("disabled",false);
    };


    services.reload = function(){
        window.location.reload();
    };

    services.createQuotation = function(){
        window.location.href = "creategrouplifequotation"
    };

    services.modifyQuotation = function(){
        var quotationId = this.selectedItem;
        $.ajax({
            url: '/pla/quotation/grouplife/getversionnumber/'+quotationId,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            success: function(msg) {
                if(msg.status=='200'){
                    window.location.href = "/pla/quotation/grouplife/creategrouplifequotation?quotationId="+quotationId+"&version="+msg.data+"&mode=edit";
                }else if(msg.status=='500'){
                }
            }
        });
    };

    services.viewQuotation = function(){
        var quotationId = this.selectedItem;
        $.ajax({
            url: '/pla/quotation/grouplife/getversionnumber/'+quotationId,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            success: function(msg) {
                if(msg.status=='200'){
                    window.location.href = "/pla/quotation/grouplife/creategrouplifequotation?quotationId="+quotationId+"&version="+msg.data+"&mode=view";
                }else if(msg.status=='500'){
                }
            }
        });
    };

    return services ;
})();
