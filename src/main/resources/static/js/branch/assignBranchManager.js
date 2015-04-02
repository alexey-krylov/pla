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
                        $scope.branchBDEList=data;
                 });
                 $http.get('/pla/core/branch/getallbranchmanager').success(function(data){
                       $scope.branchManagers=data;
                 });
                 $scope.submitAssign = function(){
                    if($scope.assignBranchManager.branchBDEFromDate){
                         if (!moment($scope.assignBranchManager.branchBDEFromDate,'DD/MM/YYYY').isValid()) {
                            $scope.newDateField.fromDate = moment($scope.assignBranchManager.branchBDEFromDate).format("DD/MM/YYYY");
                            $scope.assignBranchManager.branchBDEFromDate=$scope.newDateField.fromDate ;
                         }
                     }
                     if($scope.assignBranchManager.branchManagerFromDate){
                         if (!moment($scope.assignBranchManager.branchManagerFromDate,'DD/MM/YYYY').isValid()) {
                                $scope.newDateField.fromDate = moment($scope.assignBranchManager.branchManagerFromDate).format("DD/MM/YYYY");
                                $scope.assignBranchManager.branchManagerFromDate=$scope.newDateField.fromDate ;
                         }
                     }
                     var bmId=$scope.assignBranchManager.employeeId
                     var bDeId=$scope.assignBranchManager.branchBDEEmployeeId;
                    $scope.newBranchManager =_.findWhere($scope.branchManagers,{employeeId:bmId});
                    $scope.newBranchBDE =_.findWhere($scope.branchBDE,{employeeId:bDeId});
                    $scope.assignBranchManager.branchManagerFirstName= $scope.newBranchManager.firstName;
                    $scope.assignBranchManager.branchManagerLastName=$scope.newBranchManager.lastName;
                    $scope.assignBranchManager.branchManagerEmployeeId=$scope.newBranchManager.employeeId;
                    $scope.assignBranchManager.branchBDEFirstName=$scope.newBranchBDE.firstName;
                    $scope.assignBranchManager.branchBDELastName= $scope.newBranchBDE.lastName;
                    $scope.assignBranchManager.branchBDEEmployeeId=$scope.newBranchBDE.employeeId;
                    //console.log($scope.newBranchManager);
                   // console.log($scope.newBranchBDE);
                     console.log($scope.assignBranchManager);
                    $http.post('/pla/core/branch/assign', $scope.assignBranchManager).success(function(data){
                         if(data.status==200){
                             $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
                             $scope.reset();
                         }else if(data.status==500){
                              $scope.alert = {title:'Error Message! ', content:data.message, type: 'danger'};
                         }else{
                              $scope.alert = {title:'Info Message! ', content:data.message, type: 'info'};
                         }
                    });

                 }
             $scope.reset = function(){
               $scope.assignBranchManager.branchName='';
               $scope.assignBranchManager.regionName='';
               $scope.assignBranchManager.employeeId='';
               $scope.assignBranchManager.branchManagerFromDate='';
               $scope.assignBranchManager.branchBDEeEmployeeId='';
               $scope.assignBranchManager.branchBDEFromDate='';
               $scope.assignBranchManager.currentBranchManagerFirstName='';
               $scope.assignBranchManager.currentBranchManagerFromDate='';
               $scope.assignBranchManager.currentBranchBDEFirstName='';
               $scope.assignBranchManager.currentBranchBDEFromDate='';
             }

}]);
