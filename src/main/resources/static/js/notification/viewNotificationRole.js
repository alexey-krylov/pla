(function(){
    preConfigurations();
})();


function preConfigurations(){
    $('#roleMapping-table').dataTable(
        {
            "aoColumnDefs": [
                { "bSearchable": false, "bSortable":false, "aTargets": [ 3 ] }
            ] }
    );


}