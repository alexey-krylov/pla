
(function (angular) {
    "use strict";

    var app = angular.module('createProductClaim', ['ngRoute','common','commonServices','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert','ngMessages']);

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
        $scope.allSelect=true;
        //alert("claimId.."+$scope.productClaimId);
        $scope.getPlan=function()
        {
            //alert($scope.mapping.lineOfBusiness);
            $scope.coverageClaimType=[];
            if($scope.mapping.lineOfBusiness == 'INDIVIDUAL_LIFE')
            {
                $scope.allSelect=false;
            }
            else{
                $scope.allSelect=true;
            }
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
            $scope.coverageClaimType=[];
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
        /**
         *  Checking For Enabling Save and Update Button
          */
        $scope.checkSaveOrUpdateValid=function()
        {
            var checkLoopNameStatus = "true";

            for(var i=0;i<$scope.coverageClaimType.length;i++)
            {
                if($scope.coverageClaimType[i].claimTypes.length >0){
                    checkLoopNameStatus = "false";
                }
                else{
                    checkLoopNameStatus = "true";
                }
            }

            if(checkLoopNameStatus == "true") {
               return true;
            } else {
               return false;
            }
        }
        //$scope.coverageClaim={"claimTypes":[]};
        //Setting ClaimTypes INIL

        $scope.setClaimTypes=function(coverageClaim,claimTypesIL){
            $scope.arrayList=[];
            if(claimTypesIL){
                $scope.arrayList.push(claimTypesIL);
            }
            else{
                alert('Please Select ClaimType..');
            }
            coverageClaim.claimTypes=$scope.arrayList;
        };


        $scope.submitProductClaim=function()
        {
            //console.log('Submit Data:-'+JSON.stringify($scope.coverageClaimType));
            /**
             * "$scope.mapping.planCode":- Here plan Id has assigend
             */
             var provinceDetails = _.findWhere($scope.businessPlans, {planId: $scope.mapping.planCode});
            console.log('provinceDetails'+JSON.stringify(provinceDetails));

            var submitproductClaimReq={
                "coverageClaimType":$scope.coverageClaimType,
                "planCode":provinceDetails.planCode,
                //"planId":provinceDetails.planId,
                "lineOfBusiness":$scope.mapping.lineOfBusiness
            };
            console.log('Submit Data:-'+JSON.stringify(submitproductClaimReq));


            $http.post('/pla/core/productclaimmap/create',submitproductClaimReq).success(function (response, status, headers, config) {
                console.log(response);

                if(response.status == '200')
                {
                    window.location.href = "/pla/core/productclaimmap/openviewproductclaim" ;
                }

            }).error(function (response, status, headers, config) {
            });
        }


        $scope.$watch('mapping.lineOfBusiness',function(newVal,oldVal){
                if(newVal)
                {
                    if(newVal == 'INDIVIDUAL_LIFE')
                    {
                        $scope.allSelect=false;
                    }
                    else{
                        $scope.allSelect=true;
                    }
                }
        });

        // Retriving the ProductClaimDetails For Update..
        if($scope.productClaimId)
        {
            $http.get("/pla/core/productclaimmap/getproductclaimbyid/" + $scope.productClaimId + "?mode=view").success(function (productClaimresponse, status, headers, config) {
                console.log("ProductClaimDetails: "+JSON.stringify(productClaimresponse));

                if(productClaimresponse.lineOfBusiness == 'INDIVIDUAL_LIFE')
                {
                    $scope.mapping=productClaimresponse;
                    $scope.productClaimIdRcv=productClaimresponse.productClaimId;
                    $scope.mapping.planName=productClaimresponse.planName;
                    $scope.mapping.planCode=productClaimresponse.planCode;
                        for(var i=0;i< productClaimresponse.coverageClaimType.length;i++){
                            productClaimresponse.coverageClaimType[i].claimTypesIL =productClaimresponse.coverageClaimType[i].claimTypes[0];
                        }
                    $scope.coverageClaimType=productClaimresponse.coverageClaimType;
                }
                else{
                    $scope.mapping=productClaimresponse;
                    $scope.productClaimIdRcv=productClaimresponse.productClaimId;
                    $scope.mapping.planName=productClaimresponse.planName;
                    $scope.mapping.planCode=productClaimresponse.planCode;
                    $scope.coverageClaimType=productClaimresponse.coverageClaimType;
                }
                //$scope.claimTypes=productClaimresponse.

                $http.get('/pla/core/productclaimmap/getclaimtypebylob/' + $scope.mapping.lineOfBusiness).success(function (claimresponse, status, headers, config) {
                    $scope.claimTypes=claimresponse;

                    console.log('claimTypes'+JSON.stringify(claimresponse));
                }).error(function (response, status, headers, config) {
                });
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

                if(updateproductresponse.status == '200')
                {
                    window.location.href = "/pla/core/productclaimmap/openviewproductclaim" ;
                }
            }).error(function (updateproductresponse, status, headers, config) {
            });

        }

    }])
})(angular);
