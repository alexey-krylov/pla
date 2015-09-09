
(function (angular) {
    "use strict";

    var app = angular.module('createProductClaim', ['ngRoute','commonServices','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

    app.controller('CreateProductClaimCtrl', ['$scope', '$http', '$location','getQueryParameter', function ($scope, $http, $location, getQueryParameter) {
        $scope.CoverageSample= "coverages..";

        $scope.example1model = [];
        //$scope.example1data = [{id: 1, label: "David"}, {id: 2, label: "Jhon"}, {id: 3, label: "Danny"}];
        $scope.benefitIds=[];
        $scope.mapping={};
        $scope.businessPlans=[];
        $scope.coveragesDetails=[];
        $scope.claimTypes=[];
        $scope.productClaimIdRcv={}; //meant to Hold Rcv prodcutClaimId
        $scope.productClaimId = getQueryParameter('productClaimId')
        $scope.mode = getQueryParameter('mode');

        //alert("claimId.."+$scope.productClaimId);

        $scope.getPlan=function()
        {
            //alert($scope.mapping.lineOfBusiness);

            //Call Http Method for getting Plan
            $http.get('/pla/core/productclaimmap/getplanbylob/' + $scope.mapping.lineOfBusiness).success(function (response, status, headers, config) {
                //console.log('businessPlans'+JSON.stringify(response));
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


        $scope.$watch('mapping.lineOfBusiness',function(newVal,oldVal){
                if(newVal)
                {
                    $http.get('/pla/core/productclaimmap/getclaimtypebylob/' + $scope.mapping.lineOfBusiness).success(function (claimresponse, status, headers, config) {
                        $scope.claimTypes=claimresponse;

                        //console.log('claimTypes'+JSON.stringify(claimresponse));
                    }).error(function (response, status, headers, config) {
                    });
                }
        });

        // Retriving the ProductClaimDetails For Update..
        if($scope.productClaimId)
        {
            $http.get("/pla/core/productclaimmap/getproductclaimbyid/" + $scope.productClaimId + "?mode=view").success(function (productClaimresponse, status, headers, config) {
                console.log("ProductClaimDetails: "+JSON.stringify(productClaimresponse));
                $scope.mapping=productClaimresponse;
                $scope.productClaimIdRcv=productClaimresponse.productClaimId;
                $scope.mapping.planName=productClaimresponse.planName;
                $scope.mapping.planCode=productClaimresponse.planCode;
                $scope.coverageClaimType=productClaimresponse.coverageClaimType;
                //$scope.claimTypes=productClaimresponse.
            });
        }
        //UpdateProductClaim
        $scope.updateProductClaim=function()
        {
            //alert('updateProductClaim..');
            var updateproductClaimReq={
                "coverageClaimType":$scope.coverageClaimType,
                "planCode":$scope.mapping.planCode,
                "lineOfBusiness":$scope.mapping.lineOfBusiness,
                "productClaimId":$scope.productClaimIdRcv
            };
            console.log('UpdateProductClaim:'+JSON.stringify(updateproductClaimReq));

            $http.post('/pla/core/productclaimmap/update',updateproductClaimReq).success(function (updateproductresponse, status, headers, config) {
            }).error(function (updateproductresponse, status, headers, config) {
            });

        }

    }])
})(angular);
