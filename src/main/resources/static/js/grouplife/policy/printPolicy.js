
(function (angular) {
    "use strict";

    var app = angular.module('printPolicyGl', ['ngRoute','commonServices','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

    app.controller('printCtrl', ['$scope', '$http', '$location','getQueryParameter', function ($scope, $http, $location, getQueryParameter) {

        //alert('PrintJS...');
        $scope.policyId=getQueryParameter('policyId');
        $scope.mulSelect=[];

        //getting Details:-
        if($scope.policyId)
        {
            // Call Server Details..
            $http.get("/pla/grouplife/policy/getpoilcydocument/").success(function (policyDocumentResponse, status, headers, config) {
                console.log(JSON.stringify(policyDocumentResponse));
                $scope.mulSelect=policyDocumentResponse;

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

    }])
})(angular);
