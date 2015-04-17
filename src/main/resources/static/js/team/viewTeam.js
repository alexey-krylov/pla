$('#team-table').dataTable({
    "aoColumnDefs":[
        {"bSearchable": false, "aTargets": [ 7 ]  }

]});

var hideAlerts = function(){
    $('#alert-danger').hide();
    $('#alert').hide();
};

var teamToInactivate = '';
var inactivate=function(value,flag){
    if(flag=='save'){
        teamToInactivate =  value;
     }else{
        $.ajax({
            url: '/pla/core/team/inactivate',
            type: 'POST',
            data: JSON.stringify({'teamId':teamToInactivate}),
            contentType: 'application/json; charset=utf-8',
            success: function(msg) {
                if(msg.status=='200'){
                    window.location.reload();
                }else{
                    alert("Error inactivating coverage");
                }
            }
        });
    }
};

