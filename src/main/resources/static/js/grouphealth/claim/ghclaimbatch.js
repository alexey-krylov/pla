(function (angular) {
    "use strict";
    var app= angular.module('createbatch', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover',
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
                    templateUrl: 'ghclaimbatch.html',
                    controller: 'createbatchCtrl',
                    resolve: {

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
    app.controller('createbatchCtrl', ['$scope','getQueryParameter','$http', 'bankDetails', function ($scope,getQueryParameter,$http, bankDetails) {

        //$scope.mode=getQueryParameter('mode');
        //$scope.hcpCode=getQueryParameter('hcpCode');
        //
        //$scope.hcpStatus = hcpStatus;
        //
        //$scope.createOrUpdateHCPCommand={};
        //
        //$scope.bankDetailsResponse=[];
        //$scope.bankDetailsResponse=bankDetails;
        //$scope.bankBranchDetails=[];
        //$scope.bankDetails={};
        //
        //$scope.launchActivatedDate = function ($event) {
        //    $event.preventDefault();
        //    $event.stopPropagation();
        //    $scope.submissionActivatedDate= true;
        //};
        //
        //$scope.saveBatchDetail= function() {
        //
        //    $scope.hcpdata=$scope.createOrUpdateHCPCommand;
        //    $scope.hcpdata.activatedOn = formatDate($scope.createOrUpdateHCPCommand.activatedOn);
        //    $scope.hcpdata = JSON.stringify($scope.hcpdata);
        //    console.log(JSON.stringify($scope.hcpdata ));
        //    $http({
        //        url : '/pla/core/hcp/createOrUpdateHCP',
        //        method : 'POST',
        //        data : $scope.hcpdata
        //    })
        //        .then(function(response) {
        //            if(response.status=400){
        //
        //            }
        //
        //        },
        //        function(response) {
        //
        //
        //        });
        //}
        //
        //
        //
        //
        //
        //$scope.$watch('createOrUpdateHCPCommand.bankName', function (newvalue, oldvalue) {
        //    if (newvalue) {
        //        var bankCode = _.findWhere($scope.bankDetailsResponse, {bankName: newvalue});
        //        if (bankCode) {
        //            $http.get('/pla/grouplife/claim/getAllBankBranchNames/' + bankCode.bankCode).success(function (response, status, headers, config) {
        //                $scope.bankBranchDetails = response;
        //            }).error(function (response, status, headers, config) {
        //            });
        //        }
        //    }
        //});
        //$scope.$watch('createOrUpdateHCPCommand.bankBranchCode', function (newvalue, oldvalue) {
        //    if (newvalue) {
        //        $scope.createOrUpdateHCPCommand.bankBranchSortCode = newvalue;
        //    }
        //});
        //$scope.back = function () {
        //    window.location.reload();
        //
        //}

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