var App = angular.module('assignBranchManager', ['common','commonServices','ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('AssignBranchManagerController',['$scope','$http','$window','$location',function($scope,$http,$window,$location){

                   $scope.bmBool=false;
                   $scope.disableCurrentBranchBDE=true;
                   $scope.disableCurrentBranchManager=true;
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
                   /* if($scope.assignBranchManager.currentBranchManagerToDate){
                        $scope.disableCurrentBranchManager=false;


                    }else{
                        $scope.disableCurrentBranchManager=true;
                    }
                    if($scope.assignBranchManager.currentBranchBDEToDate){
                        $scope.disableCurrentBranchBDE=false;


                    }else{
                        $scope.disableCurrentBranchBDE=true;

                    }*/

                   });
                $scope.$watch( 'assignBranchManager.employeeId',function(newValue, oldValue){
                       if(newValue){
                         $http.get('/pla/core/branch/getallbranchmanager').success(function(data){
                            $scope.branchManagers=data;
                            var bmId=$scope.assignBranchManager.employeeId;
                            if(bmId){
                                $scope.newBranchManager =_.findWhere($scope.branchManagers,{employeeId:bmId});
                                $scope.assignBranchManager.branchManagerFirstName=$scope.newBranchManager.firstName;
                                $scope.assignBranchManager.branchManagerLastName=$scope.newBranchManager.lastName;
                                $scope.assignBranchManager.branchManagerEmployeeId=$scope.newBranchManager.employeeId;
                                $scope.assignBranchManager.onlyBranchManager=true;

                            }
                         });
                       }

                   });
                $scope.$watch( 'assignBranchManager.branchBDEEmployeeId',function(newValue, oldValue){
                         if(newValue){
                         $http.get('/pla/core/branch/getallbranchbde').success(function(data){
                              $scope.branchBDEList=data;
                              var bDeId=$scope.assignBranchManager.branchBDEEmployeeId;
                              if(bDeId){
                                  $scope.newBranchBDE =_.findWhere($scope.branchBDEList,{employeeId:bDeId});
                                  $scope.assignBranchManager.branchBDEFirstName=$scope.newBranchBDE.firstName;
                                  $scope.assignBranchManager.branchBDELastName= $scope.newBranchBDE.lastName;
                                  $scope.assignBranchManager.branchBDEEmployeeId=$scope.newBranchBDE.employeeId;
                                  $scope.assignBranchManager.onlyBde=true;

                              }
                         });
                        }
                });

          $scope.$watchGroup(['assignBranchManager.employeeId', 'assignBranchManager.branchManagerFromDate','assignBranchManager.branchBDEEmployeeId', 'assignBranchManager.branchBDEFromDate'], function(newValues, oldValues, scope) {
               // console.log(newValues[0]+newValues[1]+ newValues[2] + newValues[3] );
              if(newValues[0] &&  newValues[1] && newValues[2] &&  newValues[3] ){
                  $scope.bmBool = true;
              }else if(newValues[0] &&  newValues[1] &&  newValues[3]){
                         $scope.bmBool = false;
              }else if(newValues[0] &&  newValues[1] &&  newValues[2]){
                  $scope.bmBool = false;
              }else if(newValues[2] &&  newValues[3] &&  newValues[0]){
                  $scope.bmBool = false;
              }else if(newValues[2] &&  newValues[3] &&  newValues[1]){
                  $scope.bmBool = false;
              }else if(newValues[2] &&  newValues[3] ){
                     $scope.bmBool = true;
              }else if(newValues[0] &&  newValues[1] ){
                  $scope.bmBool = true;
              }else{
                  $scope.bmBool = false;
              }
            });
             $http.get('/pla/core/branch/getallbranchbde').success(function(data){
                        $scope.branchBDEList=data;
             });
             $http.get('/pla/core/branch/getallbranchmanager').success(function(data){
                     $scope.branchManagers=data;
             });
             $scope.submitAssign = function(){
                    if($scope.assignBranchManager.branchBDEEmployeeId){
                      $scope.assignBranchManager.onlyBde=true;

                    }else{
                      $scope.assignBranchManager.branchBDEFirstName="";
                      $scope.assignBranchManager.branchBDELastName= "";
                      $scope.assignBranchManager.branchBDEEmployeeId="";
                      $scope.assignBranchManager.onlyBde=false;
                    }
                    if($scope.assignBranchManager.employeeId){
                      $scope.assignBranchManager.onlyBranchManager=true;
                    }else{
                      $scope.assignBranchManager.branchManagerFirstName="";
                      $scope.assignBranchManager.branchManagerLastName="";
                      $scope.assignBranchManager.branchManagerEmployeeId="";
                      $scope.assignBranchManager.onlyBranchManager=false;
                    }

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
                    $http.post('/pla/core/branch/assign', $scope.assignBranchManager).success(function(data){
                                                  if(data.status==200){
                                                  //  $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
                                                  //  $scope.reset();
                                                      $window.location.href = "/pla/core/branch/view";
                                                 }else if(data.status==500){
                                                 //   $scope.alert = {title:'Error Message! ', content:data.message, type: 'danger'};
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
