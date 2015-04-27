 $('#branchManager-table').dataTable({
  "aoColumnDefs":[
   {"bSearchable": false, "aTargets": [8],"bSortable":false  }
  ]});
 var isNumeric = function (event){
  var charCode = (event.which) ? event.which : event.keyCode;
  if (charCode > 31 && charCode !=8 && charCode !=0  ) {
   event.preventDefault();
  }

 };