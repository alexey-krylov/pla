$('#team-table').dataTable({
    "aoColumnDefs":[
        {"bSearchable": false, "aTargets": [ 7 ]  }

]});


var hideAlerts = function(){
    $('.alert-danger').hide();
    $('#alert').hide();
   };

var teamToInactivate = '';
var inactivate=function(value,flag){
    hideAlerts();
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
                    hideAlerts();
                    window.location.reload();
                }else{
                    hideAlerts();
                    $('#inactivate-alert-danger').text(msg.message).show();
                }
            }
        });
    }
};
var isNumeric = function (event){
    var charCode = (event.which) ? event.which : event.keyCode;
    if (charCode > 31 && charCode !=8 && charCode !=0  ) {
        event.preventDefault();
    }

};

