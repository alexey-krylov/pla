var App = angular.module('createMandatoryDocument', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('CreateMandatoryDocumentController',['$scope','$http','$rootScope','$alert',function($scope,$http,$rootScope,$alert){

          $scope.newPlanList = [];
         $scope.showOptionalCoverage= false;
          $http.get('/pla/core/master/getdocument').success(function(data){
                          $scope.mandatoryDocList=data;
          });
         $http.get('/pla/core/plan/getallplan').success(function(data){
              for(var i=0;i<data.length;i++) {
                  $scope.planList=data[i];
                  $scope.newPlanList.push({
                        planName: $scope.planList.planDetail.planName,
                        planId: $scope.planList.planId.planId,
                        coverages: $scope.planList.coverages
                 });
              }
         });
         $scope.getDefinedOption = function(){
             if($scope.createMandatoryDocument.definedFor == "plan"){
                   $scope.showOptionalCoverage= false;
             }else{
                 $scope.showOptionalCoverage= true;
             }

         }

         $scope.$watch('mandatorydocument.planId',function(newValue, oldValue){
           if(newValue){
              var planId=$scope.mandatorydocument.planId;
              $scope.optionalCoverageData =_.findWhere($scope.newPlanList,{planId:planId});
              $scope.optionalCoverageList = _.where($scope.optionalCoverageData.coverages, {coverageType: "OPTIONAL"});
           }
         });
         $scope.$watch('mandatorydocument.coverageCode',function(newValue, oldValue){
           if(newValue){
                $scope.createMandatoryDocument.coverageId=$scope.createMandatoryDocument.coverageCode.coverageId;
           }
         });

         $http.get('/pla/core/mandatorydocument/getallprocess').success(function(data){
                         $scope.processList=data;
         });

         $scope.saveMandatoryDoc = function(){
            console.log($scope.createMandatoryDocument);
            $http.get('/pla/core/mandatorydocument/create').success(function(data){
                  if(data.status==200){
                     $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
                     $scope.reset();
                  }else{
                      $scope.alert = {title:'Error Message! ', content:data.message, type: 'danger'};
                  }
            });

         }
         $scope.reset = function(){
            $scope.createMandatoryDocument.definedFor='';
             $scope.showPlan=false;
            //$scope.showOptionalCoverage=false;
            $scope.createMandatoryDocument.process='';
            $scope.createMandatoryDocument.mandatoryDocuments='';
         }


}]);
