/**
 * Created by pradyumna on 25-06-2015.
 */

(function (angular) {
    "use strict";

    var app = angular.module('NotificationRoleModule', []);

    app.controller('CreateEditRoleController', ['$scope', '$http', '$location', function ($scope, $http, $location) {

        $scope.original = {processType: null, lineOfBusiness: null, roleType: null};
        $scope.roleMapping = {};

        $scope.save = function () {
            console.log('saving role mapping....');
            $http.post('/pla/core/notification/createnotificationrolemapping', $scope.roleMapping)
                .success(function (response, message, status) {
                    console.log('SUCCESS *** ' + JSON.stringify(response));
                })
                .error(function (response, message, status) {
                    console.log('ERROR *** ' + JSON.stringify(response));
                });

            //$('#newRoleMapping').modal('hide');
        };


        $scope.newRoleMapping = function () {
            $scope.roleMapping = angular.copy($scope.original);
            $('#newRoleMapping').modal('show');
        };

    }])
})(angular);
