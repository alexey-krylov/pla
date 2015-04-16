var App = angular.module('createMandatoryDocument', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('CreateMandatoryDocumentController',['$scope','$http','$rootScope','$alert','$window',function($scope,$http,$rootScope,$alert,$window){

          $scope.newPlanList = [];
          $scope.showOptionalCoverage= false;
          $scope.boolVal=false;
          $http.get('/pla/core/master/getdocument').success(function(data){
               $scope.documentList=data;

          });

         $http.get('/pla/core/plan/getallplan').success(function(data){
              for(var i=0;i<data.length;i++) {
                  $scope.planList=data[i];
                  $scope.newPlanList.push({
                        planName: $scope.planList.planDetail.planName,
                        planId: $scope.planList.planId,
                        coverages: $scope.planList.coverages
                 });
              }
         });
         $scope.getDefinedOption = function(){
             if($scope.createMandatoryDocument.definedFor == "plan"){
                   $scope.showOptionalCoverage= false;
                   $scope.boolVal=true;
             }else{
                 $scope.showOptionalCoverage= true;
                 $scope.boolVal=false;
             }

         }

         $scope.$watch('createMandatoryDocument.planId',function(newValue, oldValue){

           if(newValue){
              var planId=$scope.createMandatoryDocument.planId;
              $scope.optionalCoverageData =_.findWhere($scope.newPlanList,{planId:planId});
              $scope.optionalCoverageList = _.where($scope.optionalCoverageData.coverages, {coverageType: "OPTIONAL"});

           }
         });
         $scope.$watch('createMandatoryDocument.coverageCode',function(newValue, oldValue){
           if(newValue){
                $scope.createMandatoryDocument.coverageId=$scope.createMandatoryDocument.coverageCode.coverageId;
               $scope.boolVal=true;
           }else{
               $scope.boolVal=false;
           }
         });

         $http.get('/pla/core/mandatorydocument/getallprocess').success(function(data){
                         $scope.processList=data;
            /* $scope.processList= {
                 "CLAIM": "Claim",
                 "ENDORSEMENT": "Endorsement",
                 "MATURITY": "Maturity",
                 "PROPOSAL": "Proposal",
                 "REINSTATEMENT": "Reinstatement",
                 "SURRENDER": "Surrender"
             }*/
         });

         $scope.saveMandatoryDoc = function(){
           // console.log($scope.createMandatoryDocument);
            $http.post('/pla/core/mandatorydocument/create', $scope.createMandatoryDocument).success(function(data){
                  if(data.status==200){
                     $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
                   //  $scope.reset();
                     $window.location.reload();
                  }else{
                      $scope.alert = {title:'Error Message! ', content:data.message, type: 'danger'};
                  }
            });

         }
         $scope.reset = function(){
            $scope.createMandatoryDocument.definedFor='';
            $scope.createMandatoryDocument.process='';
            $scope.createMandatoryDocument.documents='';
            $scope.createMandatoryDocument.planId='';
            $scope.createMandatoryDocument.coverageCode='';

         }


}]);
