require(['jquery','bootstrap','datatables'],function(){
        $('#premium-table').dataTable({
          "bFilter": true,
          "fnDrawCallback": function ( oSettings ) {
               openPopover();
          }
         });

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
           content=content.replace("Influencing-Factors","");
          // console.log("Main content-------->"+content);
           that.attr('data-content', content);
           that.popover({title :'Influencing Factors',html : true,trigger: 'hover',placement:'top'}).css({'display':'block'});
       });
}
