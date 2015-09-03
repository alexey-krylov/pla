
(function (angular) {
    "use strict";

    var app = angular.module('createProductClaim', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

    app.controller('CreateProductClaimCtrl', ['$scope', '$http', '$location', function ($scope, $http, $location) {
        $scope.CoverageSample= "coverages..";

        $scope.example1model = [];
        $scope.example1data = [{id: 1, label: "David"}, {id: 2, label: "Jhon"}, {id: 3, label: "Danny"}];
        $scope.benefitIds=[];
        $scope.mapping={};
        $scope.businessPlans=[];
        $scope.coveragesDetails=[];
        $scope.claimTypes=[];

        $scope.getPlan=function()
        {
            //alert($scope.mapping.lineOfBusiness);

            //Call Http Method for getting Plan
            $http.get('/pla/core/productclaimmap/getplanbylob/' + $scope.mapping.lineOfBusiness).success(function (response, status, headers, config) {
                $scope.businessPlans=response;
            }).error(function (response, status, headers, config) {
            });

            $http.get('/pla/core/productclaimmap/getclaimtypebylob/' + $scope.mapping.lineOfBusiness).success(function (claimresponse, status, headers, config) {
                $scope.claimTypes=claimresponse;

                console.log('claimTypes'+JSON.stringify(claimresponse));
            }).error(function (response, status, headers, config) {
            });
        }
        $scope.coverageClaimType=[];

        $scope.getCoverageClaimType=function()
        {
            //alert('getCoverageClaimType');
            //alert('planId..'+$scope.mapping.planCode);

            $http.get('/pla/core/plan/getcoveragebyplanid/' + $scope.mapping.planCode).success(function (response, status, headers, config) {
                $scope.coveragesDetails=response;
                //console.log('coveragesDetails'+JSON.stringify($scope.coveragesDetails));
                for(var i=0; i < $scope.coveragesDetails.length; i++)
                {
                    $scope.coverageClaimType.push({"coverageId":$scope.coveragesDetails[i].coverageId,"coverageName":$scope.coveragesDetails[i].coverageName});
                }
                //$scope.coverageClaimType.push({});
                //console.log('FinalClaimType:'+JSON.stringify( $scope.coverageClaimType));
            }).error(function (response, status, headers, config) {
            });
        }

        $scope.submitProductClaim=function()
        {
            //console.log('Submit Data:-'+JSON.stringify($scope.coverageClaimType));
            var submitproductClaimReq={
                "coverageClaimType":$scope.coverageClaimType,
                "planCode":$scope.mapping.planCode,
                "lineOfBusiness":$scope.mapping.lineOfBusiness
            };
            //console.log('Submit Data:-'+JSON.stringify(submitproductClaimReq));

            $http.post('/pla/core/productclaimmap/create',submitproductClaimReq).success(function (claimresponse, status, headers, config) {
            }).error(function (response, status, headers, config) {
            });
        }


    }])
})(angular);
