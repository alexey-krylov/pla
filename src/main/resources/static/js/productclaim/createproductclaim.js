
(function (angular) {
    "use strict";

    var app = angular.module('createProductClaim', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

    app.controller('CreateProductClaimCtrl', ['$scope', '$http', '$location', function ($scope, $http, $location) {
        //alert('Hi..');

        $scope.CoverageSample= "coverages..";

        $scope.example1model = [];
        $scope.example1data = [{id: 1, label: "David"}, {id: 2, label: "Jhon"}, {id: 3, label: "Danny"}];
        $scope.benefitIds=[];


        $scope.getPlan=function()
        {
            alert('getPlan..');
        }
        $scope.getCoverageClaimType=function()
        {
            alert('getCoverageClaimType');
        }

    }])
})(angular);
