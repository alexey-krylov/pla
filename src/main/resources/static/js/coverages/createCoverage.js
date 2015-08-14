var App = angular.module('createCoverage', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('CreateCoverageController',['$scope','$http','$rootScope',function($scope,$http,$rootScope){



          $scope.createCoverage={};
          $http.get('/pla/core/benefit/activebenefits').success(function(data){
                          $scope.getAllBenefits=data
          });

}]);
