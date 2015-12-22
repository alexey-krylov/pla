(function (angular) {
    "use strict";
    var app= angular.module('searchHcpRate', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover',
        'directives', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices','ui.bootstrap.modal','ngMessages']);
        app.config(['datepickerPopupConfig', function (datepickerPopupConfig) {
            datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
            datepickerPopupConfig.currentText = 'Today';
            datepickerPopupConfig.clearText = 'Clear';
            datepickerPopupConfig.closeText = 'Done';
            datepickerPopupConfig.closeOnDateSelection = true;
        }])
.config(['$dropdownProvider', function ($dropdownProvider) {
        angular.extend($dropdownProvider.defaults, {
            html: true
        });
    }])
 .config(["$routeProvider", function ($routeProvider) {
            $routeProvider.when('/', {
                    templateUrl: 'searchhcprate.html',
                    controller: 'searchHcpRateController',
                }
            )}]);
app.controller('searchHcpRateController', ['$scope', '$http', function ($scope, $http) {

    $scope.downloadTemplate = function(){
    alert("hi");
    $http.get("/pla/core/hcp/downloadhcpratetemplate/"+1);
    }
   $scope.downloadFile = function () {

           $http({method: 'GET', url:'/pla/core/hcp/downloadhcpratetemplate'}).
            success(function(result) {
                           console.log("ok");
             }).
        error(function(data, status, headers, config) {
               console.log("oops");
            });
    };
$scope.$watchCollection('[hcpCode,showDownload]', function (n) {

                   $scope.hcpCode ="CHI0015L2";

                        $scope.dropdown = [
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">download</a>",
                                "href": "/pla/core/hcp/downloadhcpratetemplate/" + $scope.hcpCode
                            }

                        ];
            });
}])
})(angular);
