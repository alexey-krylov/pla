require(['jquery','bootstrap','datatables'],function() {
    $('#productClaim-table').dataTable();
});


var viewClientSummary = function(clientId){
    window.location.href= "opencreategrouplclientsummary?ClientId="+clientId + "&mode=view";
}
/*
$('#productClaim-table').dataTable({

    "aoColumnDefs":[
        {"bSearchable": false, "aTargets": [8],"bSortable":false  }

    ]});
*/
