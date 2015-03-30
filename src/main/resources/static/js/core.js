/**
 * Created by pradyumna on 02-02-2015.
 */
require(["bootstrap-datepicker"], function() {
    $('#datetimepicker1').datepicker({
        format: "dd/mm/yyyy",
        orientation: "top auto",
        clearBtn: true,
        autoclose: true
    });
});
require(['jquery','bootstrap','datatables'], function() {
    //$('#example').dataTable();
    //$('#team-table').dataTable();
   // $('#branchManager-table').dataTable();
    $('#regionalManager-table').dataTable();
    $('#commission-table').dataTable();
    $('#healthCareProvider-table').dataTable();
    $('#overRideCommission-table').dataTable();
    $('#premium-table').dataTable();
    $('#mandatoryDocument-table').dataTable();



});
 var openAssignTeam = function(teamId){
    var assignTeam ='';
    assignTeam=teamId;
    window.location.href="/pla/core/team/openAssignPage?teamId="+assignTeam;
}
