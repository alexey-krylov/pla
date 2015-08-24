

var App = angular.module('PolicySearch',['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives',
                                               'ngSanitize', 'commonServices']);

       App.config(['datepickerPopupConfig', function (datepickerPopupConfig) {
            datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
            datepickerPopupConfig.currentText = 'Today';
            datepickerPopupConfig.clearText = 'Clear';
            datepickerPopupConfig.closeText = 'Done';
            datepickerPopupConfig.closeOnDateSelection = true;
}]);
App.controller('PolicySearchController',['$scope','$http','$window','$location',function($scope,$http,$window,$location) {

   $scope.schedule={};
   $scope.searchObj = {};
$scope.policy={};
 $scope.items={

           "assuredNumber":"a",
            "assuredFirst":"s",
            "assuredSurname":"N",
            "assuredDOB":"N"
         }


 }]);




 App.config(["$routeProvider", function ($routeProvider) {

                        $routeProvider.when('/', {
                            templateUrl: 'searchPolicy.html',
                            controller: 'PolicySearchController',
                           resolve: {

                                   }
                    }

       )}]);

