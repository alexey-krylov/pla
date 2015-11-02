require(['jquery','bootstrap','datatables'],function(){
        $('#mandatoryDocument-table').dataTable({
          "bFilter": true,
          "fnDrawCallback": function ( oSettings ) {
               openPopover();
          },
         "aoColumns":[
             {"bSearchable": true  },
             {"bSearchable": true  },
             {"bSearchable": false ,"bSortable":false },
             {"bSearchable": true },
             {"bSearchable": false ,"bSortable":false}
         ]});

      openPopover();

     $('.next').click(function () {
          openPopover();
     });

     $('.paginate_button').click(function () {
          openPopover();
     });
});

var openPopover = function(){
        var box = $('.details-box');
        box.each(function() {
           var that = $(this);
           var text = that.text();

           var content='';
           for (var i=0; i<text.length; i++ ){
                content=content + text[i].replace(",","<br />");

           }
           content=content.replace("Mandatory-Documents","");
          //console.log("Main content-------->"+content);
           that.attr('data-content', content);
           that.popover({title :'Mandatory Documents',html : true,trigger: 'hover',placement:'left'}).css({'display':'block'});
       });
}
