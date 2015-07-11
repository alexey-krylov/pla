(function(){
    preConfigurations();
})();

function preConfigurations(){
    $('#uploadTemplateList-table').dataTable(
        {
            "aoColumnDefs": [
                { "bSearchable": false, "bSortable":false, "aTargets": [ 4 ] },
                { "bSearchable": false, "bSortable":false, "aTargets": [ 5 ] }
            ] }
    );


}