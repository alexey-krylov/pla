var App = angular.module('createCoverage', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('CreateCoverageController',['$scope','$http',function($scope,$http){
          // console.log("CreateCoverageController called ***********************");
          $http.get('/pla/core/benefit/activebenefits').success(function(data){
                               //  console.log(data);
                          $scope.getAllBenefits=data

          });
          /*$scope.getAllBenefits =[
               {
                 "benefitId": "3",
                 "STATUS": "ACTIVE",
                 "benefitName": "benefit four"
               },
               {
                  "benefitId": "4",
                  "STATUS": "ACTIVE",
                  "benefitName": "benefit five"
               },
               {
                   "benefitId": "5",
                   "STATUS": "ACTIVE",
                   "benefitName": "benefit two"
               }]
*/
             /*$scope.submitCoverage = function(){
                 console.log($scope.createCoverage);
                 $http.post('http://localhost:6443/pla/core/coverages/create', $scope.createCoverage).success(function(data){
                      console.log(data);

                 });
             }  */

}]);
