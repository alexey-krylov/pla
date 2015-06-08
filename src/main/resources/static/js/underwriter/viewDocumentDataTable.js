require(['jquery','bootstrap','datatables'],function(){
    $('#documentSetup-table').dataTable(
      {
        "bFilter": true,
        "aoColumns":[
            {"bSearchable": true  },
            {"bSearchable": true  },
            {"bSearchable": true },
            {"bSearchable": true }
            /*{"bSearchable": false ,"bSortable":false }*/

        ]});

});

