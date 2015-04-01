var App = angular.module('assignBranchManager', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('AssignBranchManagerController',['$scope','$http','$window','$location',function($scope,$http,$window,$location){

                   $scope.selectedDate =moment().add(1,'days').format("YYYY-MM-DD");
                       $scope.newDateField={};
                       $scope.datePickerSettings = {
                           isOpened:false,
                            dateOptions:{
                                formatYear:'yyyy' ,
                                startingDay:1

                            }
                       }
                       $scope.open = function($event) {
                            $event.preventDefault();
                            $event.stopPropagation();
                            $scope.datePickerSettings.isOpened = true;
                       };
                   $scope.datePickerSettingsBDE = {
                            isOpened:false,
                            dateOptions:{
                               formatYear:'yyyy' ,
                               startingDay:1
                            }
                   }
                   $scope.openBDE = function($event) {
                                     $event.preventDefault();
                                     $event.stopPropagation();
                                     $scope.datePickerSettingsBDE.isOpened = true;
                   };
                   $scope.url = window.location.search.split('=')[1];
                   $http.get('/pla/core/branch/getbranchdetail?branchId='+$scope.url).success(function(data){
                        // console.log(data);
                        $scope.assignBranchManager=data;
                        /*if($scope.assignBranchManager.branchManagerFromDate){
                            $scope.currentFromDate=moment($scope.assignBranchManager.branchManagerFromDate).format("DD/MM/YYYY");
                            $scope.assignBranchManager.branchManagerFromDate= $scope.currentFromDate;
                       }else if($scope.assignBranchManager.branchBDEFromDate){
                           $scope.currentBDEFromDate=moment($scope.assignBranchManager.branchBDEFromDate).format("DD/MM/YYYY");
                           $scope.assignBranchManager.branchBDEFromDate= $scope.currentBDEFromDate;
                       }  */
                   });
                 $http.get('/pla/core/branch/getallbranchbde').success(function(data){
                        $scope.branchBDE=data;
                 });
                 $http.get('/pla/core/branch/getallbranchmanager').success(function(data){
                       $scope.branchManagers=data;
                 });
                 $scope.submitAssign = function(){



                     console.log($scope.assignBranchManager);
                    $http.post('/pla/core/branch/assign', $scope.assignBranchManager).success(function(data){
                         if(data.status==200){
                             $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
                             $scope.reset();
                         }else{
                              $scope.alert = {title:'Error Message! ', content:data.message, type: 'danger'};
                         }
                    });

                 }
             $scope.reset = function(){


             }



}]);
