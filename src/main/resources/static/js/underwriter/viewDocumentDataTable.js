require(['jquery','bootstrap','datatables'],function(){
    $('#documentSetup-table').dataTable(
      {
        "bFilter": true,
        "aoColumns":[
            {"bSearchable": true  },
            {"bSearchable": true  },
            {"bSearchable": true },
            {"bSearchable": true },
            {"bSearchable": false ,"bSortable":false }

        ]});
   /* $('#updateDocumentSetup-table').dataTable(
        {
            "scrollY": "200px",
            "scrollCollapse": true,
            "paging": false


        });*/

});

