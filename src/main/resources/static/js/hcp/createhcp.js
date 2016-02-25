(function (angular) {
    "use strict";
    var app= angular.module('createhcp', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover',
        'directives', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices','ui.bootstrap.modal','ngMessages']);

        app.config(['datepickerPopupConfig', function (datepickerPopupConfig) {
            datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
            datepickerPopupConfig.currentText = 'Today';
            datepickerPopupConfig.clearText = 'Clear';
            datepickerPopupConfig.closeText = 'Done';
            datepickerPopupConfig.closeOnDateSelection = true;
        }])
        .config(["$routeProvider", function ($routeProvider) {
            $routeProvider.when('/', {
                    templateUrl: 'createhcp.html',
                    controller: 'createHcpCtrl',
                    resolve: {
                        provinces: ['$q', '$http', function ($q, $http) {
                            var deferred = $q.defer();
                            $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                                deferred.resolve(response)

                            }).error(function (response, status, headers, config) {
                                deferred.reject();
                            });
                            return deferred.promise;
                        }],
                       hcpStatus: ['$q', '$http', function ($q, $http) {
                            var deferred = $q.defer();
                            $http.get('/pla/core/hcp/getAllHCPStatus').success(function (response, status, headers, config) {
                                deferred.resolve(response)
                            }).error(function (response, status, headers, config) {
                                deferred.reject();
                            });
                            return deferred.promise;
                        }],
                        hcpCategories: ['$q', '$http', function ($q, $http) {
                                                                  var deferred = $q.defer();
                                                                  $http.get('/pla/core/hcp/getAllHCPCategories').success(function (response, status, headers, config) {
                                                                      deferred.resolve(response)
                                                                  }).error(function (response, status, headers, config) {
                                                                      deferred.reject();
                                                                  });
                                                                  return deferred.promise;
                                                              }],
                        bankDetails: ['$q', '$http', function ($q, $http) {
                            var deferred = $q.defer();
                            $http.get('/pla/grouplife/claim/getAllBankNames').success(function (response, status, headers, config) {
                                deferred.resolve(response)
                            }).error(function (response, status, headers, config) {
                                deferred.reject();
                            });
                            return deferred.promise;
                        }]


                    }
                }
            )}]);
    app.controller('createHcpCtrl', ['$scope','getQueryParameter','$http', 'provinces', 'hcpStatus', 'hcpCategories','bankDetails', function ($scope,getQueryParameter,$http, provinces,hcpStatus, hcpCategories,bankDetails) {

        $scope.mode=getQueryParameter('mode');
        $scope.hcpCode=getQueryParameter('hcpCode');
        $scope.Town=[];
        $scope.hcpStatus = hcpStatus;
        $scope.hcpCategories = hcpCategories;
        $scope.town=[];
        $scope.createOrUpdateHCPCommand={};
        $scope.provinces = provinces;
        $scope.bankDetailsResponse=[];
        $scope.bankDetailsResponse=bankDetails;
        $scope.bankBranchDetails=[];
        $scope.bankDetails={};

        $scope.launchActivatedDate = function ($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    $scope.submissionActivatedDate= true;
                };

    $scope.savehcpDetail= function() {

       $scope.hcpdata=$scope.createOrUpdateHCPCommand;
       $scope.hcpdata.activatedOn = formatDate($scope.createOrUpdateHCPCommand.activatedOn);
       $scope.hcpdata = JSON.stringify($scope.hcpdata);
       console.log(JSON.stringify($scope.hcpdata ));
       $http({
        url : '/pla/core/hcp/createOrUpdateHCP',
        method : 'POST',
        data : $scope.hcpdata
       })
       .then(function(response) {
       if(response.status=400){

        }

       },
        function(response) {


        });
      }
       if($scope.hcpCode){
                  $http.get("/pla/core/hcp/getHCPByHCPCode?hcpCode="+$scope.hcpCode).success(function(data){
                     $scope.createOrUpdateHCPCommand = data;
                     console.log(data);
                  }).error(function(){});
              }
        $scope.gethcpProvinceValue = function (province) {
            var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
            if (provinceDetails)
                $scope.Town = provinceDetails.cities;
        }

        $scope.$watch('createOrUpdateHCPCommand.province',function(newVal,oldVal){
            if(newVal){
                var provinceDetails1 = _.findWhere($scope.provinces, {provinceId: newVal});
                if(provinceDetails1){
                    $scope.town=provinceDetails1.cities;
                }
            }

        });

        $scope.$watch('createOrUpdateHCPCommand.bankName', function (newvalue, oldvalue) {
            if (newvalue) {
                var bankCode = _.findWhere($scope.bankDetailsResponse, {bankName: newvalue});
                if (bankCode) {
                    $http.get('/pla/grouplife/claim/getAllBankBranchNames/' + bankCode.bankCode).success(function (response, status, headers, config) {
                        $scope.bankBranchDetails = response;
                    }).error(function (response, status, headers, config) {
                    });
                }
            }
        });
        $scope.$watch('createOrUpdateHCPCommand.bankBranchCode', function (newvalue, oldvalue) {
            if (newvalue) {
                $scope.createOrUpdateHCPCommand.bankBranchSortCode = newvalue;
            }
        });
        $scope.viewmode=false;
        if($scope.mode == 'view'){
            $scope.viewmode=true;
        }
        $scope.back = function () {
       window.location.reload();

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