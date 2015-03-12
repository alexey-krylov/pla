require(['jquery','bootstrap','datatables','bootstrap-datepicker'],function(){

    $('#team-table').dataTable();

    $('#datetimepicker2').datepicker({
            format: "dd/mm/yyyy",
            orientation: "top auto",
            clearBtn: true,
            autoclose: true
        });

});
var assignData = {

     };
var assignTeamOne = function(){
    $('#assignTeam1').each(function () {
                         assignData["regionName"] = $(this).closest("tr").find('td:eq(0)').text();
                         assignData["branchName"]  = $(this).closest("tr").find('td:eq(1)').text();
                         assignData["teamName"] = $(this).closest("tr").find('td:eq(2)').text();
                         assignData["teamCode"]= $(this).closest("tr").find('td:eq(3)').text();
                         assignData["teamLeader"] = $(this).closest("tr").find('td:eq(4)').text();
                         assignData["teamLeaderFrom"] = $(this).closest("tr").find('td:eq(5)').text();
                         console.log(assignData);

                         $.ajax({
                                 url: '/pla/core/team/assign',
                                 type: 'POST',
                                 data: JSON.stringify(assignData),
                                 contentType: 'application/json; charset=utf-8',
                                 success: function(msg) {
                                     if(msg=='success'){
                                       // $('#alert').text("Team Assigned successfully").show();
                                     }
                                 }
                             });
                        //alert(id);
                    });
    };
