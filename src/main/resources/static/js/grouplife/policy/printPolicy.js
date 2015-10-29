
(function (angular) {
    "use strict";

    var app = angular.module('printPolicyGl', ['ngRoute','commonServices','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

    app.controller('printCtrl', ['$scope', '$http', '$location','getQueryParameter', function ($scope, $http, $location, getQueryParameter) {

        //alert('PrintJS...');
        $scope.policyId=getQueryParameter('policyId');
        $scope.mulSelect=[];
        $scope.approvedEndorsement=[];
        //getting Details:-
        if($scope.policyId)
        {
            // Call Server Details..
            $http.get("/pla/grouplife/policy/getpoilcydocument/"+$scope.policyId).success(function (policyDocumentResponse, status, headers, config) {
                console.log("policyDocumentResponse"+JSON.stringify(policyDocumentResponse));
                $scope.mulSelect=policyDocumentResponse.glPolicyDocument;
                $scope.policyNumber=policyDocumentResponse.policyNumber;
                //$scope.approvedEndorsement=policyDocumentResponse.approvedEndorsement;
                $scope.approvedEndorsement=[
                    {
                        "endorsementId":"56309a86b2796576552bf354",
                        "endorsementNumber":"3110000751015",
                        "policyNumber":"1-09-04-20000002",
                        "endorsementType":"Member Addition",
                        "endorsementCode":"ASSURED_MEMBER_ADDITION",
                        "effectiveDate":"2015-10-28T15:25:22.900+05:30",
                        "policyHolderName":"CTS-GL",
                        "aging":0,
                        "status":"Approved",
                        "hasNoOfAssured":true
                    }
                ]
            });
        }
       /* $scope.printPol.documents = [];
        $scope.documents = [];
        $scope.checkboxValues = false;*/

        $scope.documentsIdList=[];
        $scope.toggleSelection=function($event,influencingFactorCode) {
            //alert(influencingFactorCode);
            var checkbox = $event.target;
            if(checkbox.checked)
            {
                $scope.documentsIdList.push(influencingFactorCode);
            }
                else
                {
                    for (var i = 0; i < $scope.documentsIdList.length; i++) {

                        if($scope.documentsIdList[i] == influencingFactorCode)
                        {
                            $scope.documentsIdList.splice(i, 1);
                        }
                    }
                }
        }

        $scope.printPolicy=function(){
            //alert('PrintPolicy'+$scope.policyId);
            window.location.href = '/pla/grouplife/policy/printpolicy/' + $scope.policyId + '/' + $scope.documentsIdList;
        }

        $scope.printAllPolicy=function(){

            $scope.documentsIdList=[];
            angular.forEach($scope.mulSelect, function (allDoc) {
                allDoc.Selected=true;
                $scope.documentsIdList.push(allDoc.documentCode);
            });
            //alert('PrintPolicy'+$scope.policyId);
            window.location.href = '/pla/grouplife/policy/printpolicy/' + $scope.policyId + '/' + $scope.documentsIdList;
        }

        $scope.approvedDocumentsIdList=[];

        $scope.toggleApprovedSelection=function($event,influencingFactorCode,endorsementCode,hasNoOfAssured) {
            //alert(influencingFactorCode);
            var checkbox = $event.target;
            if(checkbox.checked)
            {
                $scope.approvedDocumentsIdList.push({"endorsementId":influencingFactorCode,"endorsementCode":endorsementCode,"hasNoOfAssured":hasNoOfAssured});
            }
            else
            {
                for (var i = 0; i < $scope.approvedDocumentsIdList.length; i++) {
                    if($scope.approvedDocumentsIdList[i].endorsementId == influencingFactorCode)
                    {
                        $scope.approvedDocumentsIdList.splice(i, 1);
                    }
                }
            }
        }

        $scope.printApprovedPolicy=function(){
            //alert('PrintPolicy'+$scope.policyId);
            window.location.href = '/pla/grouplife/policy/printpolicy/' + $scope.policyId + '/' + $scope.approvedDocumentsIdList;
        }

        $scope.printAllApprovedPolicy=function(){

            $scope.documentsIdList=[];
            angular.forEach($scope.approvedEndorsement, function (allDoc) {
                allDoc.Selected=true;
                $scope.approvedDocumentsIdList.push({"endorsementId":allDoc.influencingFactorCode,"endorsementCode":allDoc.endorsementCode,"hasNoOfAssured":allDoc.hasNoOfAssured});
            });
            //alert('PrintPolicy'+$scope.approvedDocumentsIdList);
            window.location.href = '/pla/grouplife/policy/printpolicy/' + $scope.policyId + '/' + $scope.approvedDocumentsIdList;
        }
    }])
})(angular);
