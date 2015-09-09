require(['jquery','bootstrap','datatables'],function() {
    $('#productClaim-table').dataTable();
});

var updateProductClaim = function(claimId){
    alert("Test..."+claimId);

    //window.location.href = "proposal/edit?proposalId=" + proposalId + "&mode=view";

    ///getproductclaimbyid/{productClaimId}

    window.location.href= "opencreateproductclaim?productClaimId="+claimId + "&mode=update";
}

/*
$('#productClaim-table').dataTable({

    "aoColumnDefs":[
        {"bSearchable": false, "aTargets": [8],"bSortable":false  }

    ]});
*/
