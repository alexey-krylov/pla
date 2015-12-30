(function (angular) {
    "use strict";
    var app= angular.module('PreAuthorizationUpload', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover',
        'directives', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices','ui.bootstrap.modal','ngMessages','angularFileUpload']);
    app.config(['datepickerPopupConfig', function (datepickerPopupConfig) {
        datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
        datepickerPopupConfig.currentText = 'Today';
        datepickerPopupConfig.clearText = 'Clear';
        datepickerPopupConfig.closeText = 'Done';
        datepickerPopupConfig.closeOnDateSelection = true;
    }])

        .config(["$routeProvider", function ($routeProvider) {
            $routeProvider.when('/', {
                    templateUrl: 'preauthorizationupload.html',
                    controller: 'PreAuthorizationUploadController',
                    resolve: {
                                           hcps: ['$q', '$http', function ($q, $http) {
                                               var deferred = $q.defer();
                                               $http.get('/pla/grouphealth/claim/cashless/getAllHcpNameAndCode').success(function (response, status, headers, config) {
                                                   deferred.resolve(response)

                                               }).error(function (response, status, headers, config) {
                                                   deferred.reject();
                                               });
                                               return deferred.promise;
                                           }]


                }
            });
            }]);

    app.controller('PreAuthorizationUploadController', ['$scope', '$http','$upload','hcps' , function ($scope, $http, $upload,hcps) {

     $scope.hcps=hcps;
     $scope.hcpCode='';


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
