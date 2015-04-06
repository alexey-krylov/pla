var App = angular.module('createMandatoryDocument', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('CreateMandatoryDocumentController',['$scope','$http','$rootScope','$alert',function($scope,$http,$rootScope,$alert){
          $scope.showPlan=false;
          $scope.showOptionalCoverage=false;
          $http.get('/pla/core/mandatorydocument/getallmandatorydocument').success(function(data){
                          $scope.mandatoryDocList=data;
          });
          $http.get('/pla/core/mandatorydocument/getallplan').success(function(data){
                          $scope.planList=data;
          });
         $http.get('/pla/core/mandatorydocument/getalloptionalcoverage').success(function(data){
                          $scope.optionalCoverageList=data;
         });
         $http.get('/pla/core/mandatorydocument/getallprocess').success(function(data){
                         $scope.processList=data;
         });
         $scope.getDefinedValue = function(selectedValue){
                   if(selectedValue=="Plan"){
                        $scope.showPlan=true;
                        $scope.showOptionalCoverage=false;
                   }else if(selectedValue=="Optional Coverage"){
                       $scope.showPlan=false;
                       $scope.showOptionalCoverage=true;
                   }

         }
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
            $scope.showOptionalCoverage=false;
            $scope.createMandatoryDocument.process='';
            $scope.createMandatoryDocument.mandatoryDocuments='';
         }


}]);
