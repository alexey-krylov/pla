//(function (angular) {
//    "use strict";
//var app= angular.module('SearchPayPoint', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices','ui.bootstrap.modal','ngMessages']);
//
//app.config(["$routeProvider", function ($routeProvider) {
//    $routeProvider.when('/', {
//        templateUrl: 'searchpaypoint.html',
//        controller: 'searchPayPointCtrl',
//        resolve: {
//
//        }
//    })
//}])
//    .config(['datepickerPopupConfig', function (datepickerPopupConfig) {
//        datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
//        datepickerPopupConfig.currentText = 'Today';
//        datepickerPopupConfig.clearText = 'Clear';
//        datepickerPopupConfig.closeText = 'Done';
//        datepickerPopupConfig.closeOnDateSelection = true;
//    }]);
//    app.controller('searchPayPointCtrl', ['$scope', function ($scope) {
//
//            $scope.selectedItem = 1;
//    }])
//})(angular);

/*
 define(["angular"],
 function(){
 angular.module("myApp",[])
 .controller('viewAgentCtrl',function($scope){

 })
 });*/
var searchPayPointModule = (function(){
    var payPointServices = {};
    payPointServices.payPointSelected = undefined;
    this.payPointName = null;
    payPointServices.getTheItemSelected = function(ele){
        this.payPointSelected=$(ele).val();
        $("#payPoint-update").prop("disabled","");
        $("#payPoint-view").prop("disabled","");

        this.payPointName = $(ele).parent().find('input[type=hidden]').val();
       // alert(this.payPointName);
    };
    payPointServices.createPayPoint = function(){
        window.location.href = "create";
    };

//    payPointServices.createPayPoint = function () {
//        window.location.href = "pla/core/paypoint/create";
//    };

    payPointServices.viewPayPoint =  function(){
        if (this.payPointSelected) {
            if ('PAYPOINT' === this.payPointName) {
                window.location.href = "/pla/core/paypoint/view?payPointId=" + this.payPointSelected + "&mode=view";
            } else {
                window.location.href = "/pla/core/paypoint/view?payPointId=" + this.payPointSelected;

            }
        }
    };

    payPointServices.updatePayPoint = function(){
        if(this.payPointSelected){
            if ('BROKER' === this.payPointName) {
                window.location.href = "/pla/core/paypoint/updatet?payPointId=" + this.payPointSelected + "&mode=edit";
            } else {
                window.location.href = "/pla/core/paypoint/update?payPointId=" + this.payPointSelected;
            }
        }
    };
    payPointServices.reload = function(){
        window.location.reload();
    };


    return payPointServices;
})();
