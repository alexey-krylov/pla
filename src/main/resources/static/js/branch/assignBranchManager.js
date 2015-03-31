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
                 $http.get('/pla/core/team/getteamleaders').success(function(data){
                                                 //  console.log(data);
                                                  $scope.teamLeaders=data;
                 });
                 $scope.url = window.location.search.split('=')[1];
                $http.get('/pla/core/branch/openAssignPage?branchId='+$scope.url).success(function(data){
                    // console.log(data);
                    $scope.assignBranchManager=data;


                });


}]);
