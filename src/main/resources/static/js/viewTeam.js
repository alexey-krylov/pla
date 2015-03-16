require(['jquery','bootstrap','datatables','bootstrap-datepicker'],function(){

    $('#team-table').dataTable();

    $('#datetimepicker2').datepicker({
            format: "dd/mm/yyyy",
            orientation: "top auto",
            clearBtn: true,
            autoclose: true
        });

});
var teamData = {
     };
var createTeamOne = function(){
   $('#createTeam *').filter(':text').each(function(key,value){
           teamData[$(value)[0].id]=$(value).val();

       });
        teamData["regionName"]= $('#regionName :selected').text();
        teamData["branchName"]= $('#branchName :selected').text();
        teamData["teamLeader"]= $('#teamLeader :selected').text();
             console.log(teamData);
             $.ajax({
                url: '/pla/core/team/create',
                type: 'POST',
                data: JSON.stringify(teamData),
                contentType: 'application/json; charset=utf-8',
                success: function(msg) {
                       if(msg=='success'){
                            $('#alert').text("Team Created successfully").show();
                        }
                }
             });


    };
