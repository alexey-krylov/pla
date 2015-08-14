
(function(){
    preConfigurations();
})();

function preConfigurations(){
    $('#notificationPortlet-table').dataTable(
       {
            "aoColumnDefs": [
                { "sWidth": "20%","bSearchable": true, "bSortable":true, "aTargets": [ 0 ] },
                { "sWidth": "20%","bSearchable": true, "bSortable":true, "aTargets": [ 1 ] },
                { "sWidth": "20%","bSearchable": true, "bSortable":true, "aTargets": [ 2 ] },
                { "sWidth": "10%","bSearchable": true, "bSortable":true, "aTargets": [ 3 ] },
                { "sWidth": "30%","bSearchable": false, "bSortable":false, "aTargets": [ 4 ] }

            ] }
    );
    $('#notificationHistory-table').dataTable(
        {
            "aoColumnDefs": [
                { "sWidth": "20%","bSearchable": true, "bSortable":true, "aTargets": [ 0 ] },
                { "sWidth": "20%","bSearchable": true, "bSortable":true, "aTargets": [ 1 ] },
                { "sWidth": "20%","bSearchable": true, "bSortable":true, "aTargets": [ 2 ] },
                { "sWidth": "10%","bSearchable": true, "bSortable":true, "aTargets": [ 3 ] },
                { "sWidth": "30%","bSearchable": false, "bSortable":false, "aTargets": [ 4 ] }

            ] }
    );


}
