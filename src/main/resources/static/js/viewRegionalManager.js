require(['jquery','bootstrap','datatables','bootstrap-datepicker'],function(){

    $('#regionalManager-table').dataTable();

    $('#datetimepicker4').datepicker({
            format: "dd/mm/yyyy",
            orientation: "top auto",
            clearBtn: true,
            autoclose: true
        });
});
