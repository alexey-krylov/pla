 $('#regionalManager-table').dataTable({
  "aoColumnDefs":[
   {"bSearchable": false, "aTargets": [4 ],"bSortable":false  },
   { "sType": "date-uk", "aTargets": [ 2 ] },
   { "sType": "date-uk", "aTargets": [ 3 ] }

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