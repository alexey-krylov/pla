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

                        batchDetails: ['$q', '$http','getQueryParameter', function ($q, $http,getQueryParameter) {
                            var batchNumber = getQueryParameter('batchNumber');
                            var deferred = $q.defer();
                            $http.get('/pla/grouphealth/claim/cashless/claim/getdataforbatchview?batchNumber='+ batchNumber ).success(function (response, status, headers, config) {
                                deferred.resolve(response)
                            }).error(function (response, status, headers, config) {
                                deferred.reject();
                            });
                            return deferred.promise;
                        }]


                    }
                }
            )}]);
    app.controller('createbatchCtrl', ['$scope','getQueryParameter','$http', 'batchDetails', function ($scope,getQueryParameter,$http, batchDetails) {

        $scope.mode = getQueryParameter('mode');
        $scope.hcpCode = getQueryParameter('hcpCode');
        $scope.batchDetails = batchDetails;
        $scope.showSaveButton = false;
        $scope.launchActivatedDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.submissionActivatedDate= true;
        };

        $scope.saveBatchDetail= function() {
        };

        $scope.back = function () {
            window.location.href = '/pla/grouphealth/claim/cashless/claim/getallbatchesforsettlement';
        };

        $scope.shouldSaveBeDisabled = function(){
            if($scope.batchDetails.batchClosedOnDate){
                $scope.showSaveButton = true;
            }
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