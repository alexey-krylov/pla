(function (angular) {
    "use strict";
var app= angular.module('createEndorsement', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives',
    'angularFileUpload', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices','ui.bootstrap.modal','ngMessages']);

app.config(["$routeProvider", function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'createEndorsementTpl.html',
        controller: 'EndorsementCtrl',
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
    app.controller('EndorsementCtrl', ['$scope', '$http', '$location','getQueryParameter','globalConstants', function ($scope, $http, $location, getQueryParameter,globalConstants) {

            $scope.selectedItem = 1;
            $scope.provinces = [];
            $scope.townList=[];
            $scope.empTownList=[];

            alert('createEndorsement');
            $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                $scope.provinces = response;
            }).error(function (response, status, headers, config) {
            });

            $scope.updateLADOB = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob2 = true;
            };

        $scope.LADOB = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.launchdob= true;
        };

        $scope.getTownList = function (province) {
            //alert(province);
            var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
            if (provinceDetails)
                $scope.townList = provinceDetails.cities;
        }

        $scope.getEmpTownList = function (province) {
            //alert(province);
            var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
            if (provinceDetails)
                $scope.empTownList = provinceDetails.cities;
        }


        }])
})(angular);