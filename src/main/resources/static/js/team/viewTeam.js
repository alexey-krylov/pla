$('#team-table').dataTable({
    "aoColumnDefs":[
        {"bSearchable": false, "aTargets": [ 7 ] ,"bSortable":false },
        { "sType": "date-uk", "aTargets": [ 5 ] },
        { "sType": "date-uk", "aTargets": [ 6 ] }



]});


var hideAlerts = function(){
    $('.alert-danger').hide();
    $('#alert').hide();
   };

var teamToInactivate = '';
var inactivate=function(value,flag){
    hideAlerts();
    $('#approveButton').show();
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
                    $('#approveButton').hide();
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