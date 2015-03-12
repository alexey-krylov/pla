require(['jquery','bootstrap','datatables','bootstrap-datepicker'],function(){

    $('#branchManager-table').dataTable();

    $('#datetimepicker3').datepicker({
            format: "dd/mm/yyyy",
            orientation: "top auto",
            clearBtn: true,
            autoclose: true
        });
});
