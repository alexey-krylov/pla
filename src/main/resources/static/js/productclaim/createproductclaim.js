
(function (angular) {
    "use strict";

    var app = angular.module('createProductClaim', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

    app.controller('CreateProductClaimCtrl', ['$scope', '$http', '$location', function ($scope, $http, $location) {
        //alert('Hi..');

        $scope.CoverageSample= "coverages..";

        $scope.example1model = [];
        $scope.example1data = [{id: 1, label: "David"}, {id: 2, label: "Jhon"}, {id: 3, label: "Danny"}];
        $scope.benefitIds=[];
        $scope.mapping={};
        $scope.businessPlans=[];
        $scope.getPlan=function()
        {
            //alert('getPlan..');
            //alert($scope.mapping.lineOfBusiness);

            //Call Http Method for getting Plan
            $http.get('/pla/core/productclaimmap/getplanbylob/' + $scope.mapping.lineOfBusiness).success(function (response, status, headers, config) {
                $scope.businessPlans=response;
            }).error(function (response, status, headers, config) {
            });

        }
        $scope.getCoverageClaimType=function()
        {
            alert('getCoverageClaimType');
        }

    }])
})(angular);
