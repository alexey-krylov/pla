/*
 define(["angular"],
 function(){
 angular.module("myApp",[])
 .controller('viewAgentCtrl',function($scope){

 })
 });*/
var viewQuotationModule = (function(){
    var services = {};
    services.selectedItem = "";
    services.getTheItemSelected = function(ele){
        this.selectedItem=$(ele).val();
        $(".btn-disabled").attr("disabled",false);
    };


    services.reload = function(){
        window.location.reload();
    };


    return services ;
})();

$(document).ready(function(){
    $('#quotation-table').dataTable();
    $( "input[type=radio]" ).on( "click", function(){
        viewQuotationModule.getTheItemSelected(this)
    });
});