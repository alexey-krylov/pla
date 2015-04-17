var App = angular.module('updateMandatoryDocument', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('UpdateMandatoryDocumentsController',['$scope','$http','$rootScope','$window','$location','$alert',function($scope,$http,$rootScope,$window,$location,$alert){

          $scope.showOptionalCoverage=false;

          $scope.url = window.location.search.split('=')[1];
          $http.get('/pla/core/master/getdocument').success(function(data){
            $scope.mandatoryDocList=data;
          });

        $http.get('/pla/core/mandatorydocument/getmandatorydocumentdetail/'+$scope.url).success(function(data){

                  $scope.updateMandatoryDocument=data;

                  if($scope.updateMandatoryDocument.coverageId){

                       $scope.showOptionalCoverage=true;
                  }

          });

         $scope.updateMandatoryDoc = function(){
            console.log($scope.updateMandatoryDocument);
            $http.post('/pla/core/mandatorydocument/update',$scope.updateMandatoryDocument).success(function(data){
                  if(data.status==200){
                     $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
                     $scope.reset();
                  }else{
                      $scope.alert = {title:'Error Message! ', content:data.message, type: 'danger'};
                  }
            });

         }
         $scope.reset = function(){
            $scope.updateMandatoryDocument.definedFor='';
           // $scope.showPlan=false;
            $scope.showOptionalCoverage=false;
            $scope.updateMandatoryDocument.process='';
            $scope.updateMandatoryDocument.multiSelectList='';
         }

}]);
