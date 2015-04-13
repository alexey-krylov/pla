var App = angular.module('createCommission', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert','angularFileUpload']);

App.controller('CreateCommissionController',['$scope',function($scope){
           console.log("called CreateCommissionController................");
           var uid = 1;
           $scope.commissions = [];
           $scope.showtable  = false;
           $scope.showToYear  = false;
               $scope.addCommissionDetails = function() {
                   $scope.showtable  = true;
                   if($scope.addCommission.id == null) {
                        $scope.addCommission.id = uid++;
                        $scope.commissions.push($scope.addCommission);
                   } else {

                        for(i in $scope.commissions) {
                               if($scope.commissions[i].id == $scope.addCommission.id) {
                                   $scope.commissions[i] = $scope.addCommission;
                               }
                        }
                   }
                   $scope.addCommission = {};
               }

            $scope.getPolicyYearExpressed = function(policyYearExpressed){
                    // alert(policyYearExpressed);
                     if(policyYearExpressed == "Range"){
                       $scope.showToYear  = true;
                     }else{
                       $scope.showToYear  = false;
                     }

            }
            $scope.saveCommission = function(){
                    console.log($scope.createCommission);
                    console.log($scope.commissions);
            }
            $scope.fromDatePickerSettings = {
                   isOpened:false,
                   dateOptions:{
                      formatYear:'yyyy' ,
                      startingDay:1
                   }
              }
              $scope.toDatePickerSettings = {
                                 isOpened:false,
                                 dateOptions:{
                                    formatYear:'yyyy' ,
                                    startingDay:1
                                 }
                            }
              $scope.open = function($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    $scope.fromDatePickerSettings.isOpened = true;
              };
              $scope.openToDate = function($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    $scope.toDatePickerSettings.isOpened = true;
              };

    }]);
