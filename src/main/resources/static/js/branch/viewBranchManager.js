 $('#branchManager-table').dataTable({

  "aoColumnDefs":[
   {"bSearchable": false, "aTargets": [8],"bSortable":false  },
   { "sType": "date-uk", "aTargets": [ 3 ] },
   { "sType": "date-uk", "aTargets": [ 4 ] },
   { "sType": "date-uk", "aTargets": [ 6 ] },
   { "sType": "date-uk", "aTargets": [ 7 ] }

  ]});
 var isNumeric = function (event){
  var charCode = (event.which) ? event.which : event.keyCode;
  if (charCode > 31 && charCode !=8 && charCode !=0  ) {
   event.preventDefault();
  }

 };

 $.extend(jQuery.fn.dataTableExt.oSort, {
  "date-uk-pre": function (a) {
   var x;
   try {
    var dateA = a.replace(/ /g, '').split("/");
    var day = parseInt(dateA[0], 10);
    var month = parseInt(dateA[1], 10);
    var year = parseInt(dateA[2], 10);
    var date = new Date(year, month - 1, day)
    x = date.getTime();
   }
   catch (err) {
    x = new Date().getTime();
   }

   return x;
  },

  "date-uk-asc": function (a, b) {
   return a - b;
  },

  "date-uk-desc": function (a, b) {
   return b - a;
  }
 });