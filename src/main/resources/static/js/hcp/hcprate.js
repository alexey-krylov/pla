(function (angular) {
    "use strict";
    var app= angular.module('searchHcpRate', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover',
        'directives', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices','ui.bootstrap.modal','ngMessages','angularFileUpload', 'angucomplete-alt']);
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
                    controller: 'searchHcpRateController'
                }
            )}]);
    app.controller('searchHcpRateController', ['$scope', '$http','$upload',  function ($scope, $http, $upload) {
        $scope.uploadHCPServiceRatesDto = {};
        $scope.showDownload = true;
        $scope.fileSaved = null;
        $scope.fileName = null;
        $scope.$watchCollection('[uploadHCPServiceRatesDto.hcpCode,showDownload]', function (n) {
            $scope.qId = n[0];
            if (n[1]) {
                $scope.dropdown = [{
                    "text": "<a><img src=\"/pla/images/xls-icon.png\">download</a>",
                    "href": "/pla/core/hcprate/downloadhcpratetemplate/" + $scope.qId
                }
                ];
            } else {
                $scope.dropdown = [{
                    "text": "<a><img src=\"/pla/images/xls-icon.png\">download</a>",
                    "href": "/pla/core/hcprate/downloadhcpratetemplate/" + $scope.hcpCode
                },{
                    "text": "<a><img src=\"/pla/images/xls-icon.png\">Error File</a>",
                    "href": "/pla/core/hcprate/downloaderrorhcpratetemplate/" + $scope.qId
                }
                ];
            }
        });

        $scope.launchFromDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.submissionfromdate= true;
        };

        $scope.launchToDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.submissiontodate= true;
        };

        $scope.$watch('fileSaved', function (n, o) {
            if (n && n.length) {
                $scope.fileName = n[0].name
            }
        });

        $scope.getAllHCPByHCPCode = function(data){
            console.log(data);
            if(data){
                $http.get("/pla/core/hcp/getAllHCPByHCPCode?hcpCode="+data.title).success(function(data){
                    var hcp = data[0];
                    $scope.$broadcast('angucomplete-alt:changeInput', 'hcpName', hcp.hcpName);
                }).error(function(){});
            }
        };
        $scope.getAllHCPByHCPName = function(data){
            if(data){
                $http.get("/pla/core/hcp/getAllHCPByHCPName?hcpName="+data.title).success(function(data){
                    var hcp = data[0];
                    $scope.$broadcast('angucomplete-alt:changeInput', 'hcpCode', hcp.hcpCode);
                }).error(function(){});
            }
        };

        $scope.clearHcpCode= function(){
            $scope.$broadcast('angucomplete-alt:clearInput', 'hcpCode');
        };

        $scope.clearHcpName= function(){
            $scope.$broadcast('angucomplete-alt:clearInput', 'hcpName');
        };

        $scope.uploadHCPRates = function () {
            console.log($scope.uploadHCPServiceRatesDto);
            $scope.uploadHCPServiceRatesDto.fromDate = formatDate($scope.uploadHCPServiceRatesDto.fromDate);
            $scope.uploadHCPServiceRatesDto.toDate = formatDate($scope.uploadHCPServiceRatesDto.toDate);
            $upload.upload({
                url: '/pla/core/hcprate/uploadhcpratedetails',
                headers: {'Authorization': 'xxx'},
                fields: $scope.uploadHCPServiceRatesDto,
                file: $scope.fileSaved
            }).success(function (data, status, headers, config) {
                if (data.status == "200") {
                } else{
                    if(data.data){
                        $scope.showDownload = false;

                    }else{
                        $scope.showDownload = true;
                    }
                }
            });
        };

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
