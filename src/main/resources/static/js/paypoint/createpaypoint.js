                (function (angular) {
                    "use strict";
                var app= angular.module('createPayPoint', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover',
                 'directives', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices','ui.bootstrap.modal','ngMessages']);

                app.config(["$routeProvider", function ($routeProvider) {
                    $routeProvider.when('/', {
                        templateUrl: 'createpaypoint.html',
                        controller: 'createPayPointCtrl',
                        resolve: {

                        }
                    })
                }])
                    .config(['datepickerPopupConfig', function (datepickerPopupConfig) {
                        datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
                        datepickerPopupConfig.currentText = 'Today';
                        datepickerPopupConfig.clearText = 'Clear';
                        datepickerPopupConfig.closeText = 'Done';
                        datepickerPopupConfig.closeOnDateSelection = true;
                    }]);
    app.controller('createPayPointCtrl', ['$scope','getQueryParameter','$http','$timeout', function ($scope,getQueryParameter,$http,$timeout) {
             $scope.paypointCommand = {};
             $scope.bankDetails={};
             $scope.selectedItem = 1;
             $scope.payPointPhysicalAddress={};
             $scope.payPointContactAddress={};
             $scope.payPointProfileDto={};
             $scope.payPointPaymentDto={};
             $scope.mode=getQueryParameter('mode');//it fIRST fetch the mode value from url. and check in html mode value if its then ng-disabel active
             $scope.paypointStatusSet = [];
             $scope.payPointId = getQueryParameter("payPointId");
             $scope.bankBranchDetails={};
             $scope.town=[];
             $scope.physicalTown=[];
            if($scope.payPointId){
                $http.get("/pla/core/paypoint/getPayPointByPayPointId?payPointId="+$scope.payPointId).success(function(data){
                    $scope.paypointCommand = data;
                     console.log(data);
                    $scope.payPointPhysicalAddress = data.payPointPhysicalAddress;
                    $scope.payPointContactAddress = data.payPointContactAddress;
                    $scope.payPointProfileDto = data.payPointProfileDto;
                    $scope.payPointPaymentDto = data.payPointPaymentDto;
                }).error(function(){});
            }

              $scope.launchPromptDate = function ($event) {
                            $event.preventDefault();
                            $event.stopPropagation();
                            $scope.submissionPromptDate= true;
                        };
                 $scope.launchPremiumDate = function ($event) {
                           $event.preventDefault();
                           $event.stopPropagation();
                           $scope.premiumDate = true;
                       };

              $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                                                        $scope.provinces = response;
                                             }).error(function (response, status, headers, config) {
                                           });
//                 $scope.populateStatus = function(){

                $http.get("/pla/core/paypoint/getallpaypointstatus")
                       .success(function (response, status, headers, config) {
                                $scope.paypointStatusResponse= response;
                                })
                       .error(function (response, status, headers, config) { });
//                     }

            $http.get("/pla/core/paypoint/getallpaypointgrade").success(function (response, status, headers, config) {
                                         $scope.paypointGradeResponse= response;

                                     })
                                     .error(function (response, status, headers, config) {
                                     });
             $scope.bankDetailsResponse=[];
             $http.get("/pla/core/paypoint/getallbankdetail").success(function (response, status, headers, config) {
                                          $scope.bankDetailsResponse= response;
                                      })
                                      .error(function (response, status, headers, config) {
                                      });
                              $scope.$watch('payPointPaymentDto.bankName',function(newvalue,oldvalue){
//                              alert('Testing'+newvalue);

                                               if(newvalue){
                                                   var bankCode = _.findWhere($scope.bankDetailsResponse, {bankName: newvalue});
                                                      if (bankCode){
                                                       $http.get('/pla/core/paypoint/getbankbranchname/'+bankCode.bankCode).success(function (response, status, headers, config) {
                                                           $scope.bankBranchDetails= response;

                                                       }).error(function (response, status, headers, config) {
                                                       });
                                                   }
                                               }
                                           });

                                      $scope.$watch('payPointPaymentDto.bankBranchName',function(newvalue,oldvalue){
                                                         /*var bankBranchNames = _.findWhere($scope.bankBranchDetails, {branchName: newvalue});
                                                         if(bankBranchNames)
                                                         {
                                                             $scope.payPointPaymentDto.bankBranchSortCode=bankBranchNames.sortCode;
                                                         }*/
                                                         if(newvalue){
                                                         $scope.payPointPaymentDto.bankBranchSortCode=newvalue;
                                                         }
                                                     }
                                                 );

                      $scope.getPaypointProvinceValue = function (province) {
                                     var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
                                     if (provinceDetails)
                                        $scope.physicalTown = provinceDetails.cities;
                                }

                                 $scope.$watch('payPointContactAddress.province',function(newVal,oldVal){
                                                                                                        if(newVal){
                                                                                                              var provinceDetails1 = _.findWhere($scope.provinces, {provinceId: newVal});
                                                                                                              if(provinceDetails1){
                                                                                                              $scope.town=provinceDetails1.cities;
//                                                                                                              alert("town - "+$scope.town);
                                                                                                              }
                                                                                                        }

                                                                                                });

             $scope.getPaypoint = function (province) {
                                     var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
                                     if (provinceDetails)
                                        $scope.physicalTown = provinceDetails.cities;
                                           }

                                $scope.$watch('payPointPhysicalAddress.province',function(newVal,oldVal){
                                        if(newVal){
                                              var provinceDetails1 = _.findWhere($scope.provinces, {provinceId: newVal});
                                              if(provinceDetails1){
                                              $scope.physicalTown=provinceDetails1.cities;
//                                              alert("physicalTown - "+$scope.physicalTown);
                                              }
                                        }

                                });

          $scope.savePaypointDetail = function () {
             $scope.paypointCommand.promptDatePremium = formatDate($scope.paypointCommand.promptDatePremium);
             $scope.paypointCommand.promptDateSchedules = formatDate($scope.paypointCommand.promptDateSchedules);
             $scope.paypointCommand.payPointPhysicalAddress = $scope.payPointPhysicalAddress;
             $scope.paypointCommand.payPointContactAddress = $scope.payPointContactAddress;
             $scope.paypointCommand.payPointProfileDto = $scope.payPointProfileDto;
             $scope.paypointCommand.payPointPaymentDto = $scope.payPointPaymentDto;
             $scope.paypointCommand.payPointPhysicalAddress = $scope.payPointPhysicalAddress;
             $scope.data = JSON.stringify($scope.paypointCommand);

             console.log('Save Data'+JSON.stringify());
            $http({
                url: '/pla/core/paypoint/create',
                method: 'POST',
                data: $scope.data
            })
            .then(function(response) {

                    // success
            },
            function(response) { // optional
                    // failed
            });

            }

    }])
})(angular);

            function formatDate(date) {
                var d = new Date(date),
                    month = '' + (d.getMonth() + 1),
                    day = '' + d.getDate(),
                    year = d.getFullYear();

                if (month.length < 2) month = '0' + month;
                if (day.length < 2) day = '0' + day;

                return [year, month, day].join('-');
            }