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
    $('#datepicker5').datepicker({
        format: "dd/mm/yyyy",
        orientation: "top auto",
        clearBtn: true,
        autoclose: true
    });
});

/* var openAssignTeam = function(teamId){
    var assignTeam ='';
    assignTeam=teamId;
    window.location.href="/pla/core/team/openAssignPage?teamId="+assignTeam;
}*/
