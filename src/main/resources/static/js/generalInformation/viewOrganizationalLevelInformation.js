$('#organizationalLevelInformation-table').dataTable();
var isNumeric= function (evt){

    var charCode = (evt.which) ? evt.which : evt.keyCode;

    if (charCode > 31 && charCode !=8 && charCode !=0 && charCode !=46 &&(charCode < 48 || charCode >57 ) )
        return false;
    return true;
}