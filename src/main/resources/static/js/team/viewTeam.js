$('#team-table').dataTable();

var openAssignTeam = function(teamId){
  // console.log("open Assign team" + teamId);
    var assignTeam ='';
    assignTeam=teamId;
    window.location.href="/pla/core/team/openAssignPage?teamId="+assignTeam;
}
