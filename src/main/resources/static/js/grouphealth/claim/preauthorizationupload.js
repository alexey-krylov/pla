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
       .config(['$dropdownProvider', function ($dropdownProvider) {
                   angular.extend($dropdownProvider.defaults, {
                       html: true
                   });
               }])

        .config(["$routeProvider", function ($routeProvider) {
            $routeProvider.when('/', {
                templateUrl: 'preauthorizationupload.html',
                controller: 'PreAuthorizationUploadController',
                resolve: {
                    hcps: ['$q', '$http', function ($q, $http) {
                        var deferred = $q.defer();
                        $http.get('/pla/grouphealth/claim/cashless/preauthorization/getAllHcpNameAndCode').success(function (response, status, headers, config) {
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
          $scope.uploadPreAuthorizationDto = {};
          $scope.uploadPreAuthorizationDto.batchDate = formatDate(new Date);
//        console.log($scope.uploadPreAuthorizationDto.batchDate);
        $scope.hcps = hcps;
       $scope.fileSaved = null;
        $scope.fileName = null;

        $scope.showDownload = true;
        $scope.$watchCollection('[uploadPreAuthorizationDto.hcpCode, showDownload]', function (n) {
            $scope.qId = n[0];
            if (n[1]) {
                $scope.dropdown = [{
                    "text": "<a><img src=\"/pla/images/xls-icon.png\">download</a>",
                    "href": "/pla/grouphealth/claim/cashless/preauthorization/downloadGHCashlessClaimPreAuthtemplate/" + $scope.qId
                }
                ];
            } else {
                $scope.dropdown = [{
                    "text": "<a><img src=\"/pla/images/xls-icon.png\">download</a>",
                    "href": "/pla/grouphealth/claim/cashless/preauthorization/downloadGHCashlessClaimPreAuthtemplate/" + $scope.uploadPreAuthorizationDto.hcpCode
                },{
                    "text": "<a><img src=\"/pla/images/xls-icon.png\">Error File</a>",
                    "href": "/pla/grouphealth/claim/cashless/preauthorization/downloaderrorpreauthtemplate/" + $scope.qId
                }
                ];
            }
        });

        $scope.launchbatchDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.submissionbatchDate= true;
        };

        $scope.$watch('fileSaved', function (n, o) {
            if (n && n.length) {
                $scope.fileName = n[0].name
            }
        });

        $scope.uploadPreAuthorizationTemplate = function () {
            console.log($scope.uploadPreAuthorizationDto);
            $scope.uploadPreAuthorizationDto.batchDate = formatDate($scope.uploadPreAuthorizationDto.batchDate);
            $upload.upload({
                url: '/pla/grouphealth/claim/cashless/preauthorization/uploadPreAuthorizationTemplate',
                headers: {'Authorization': 'xxx'},
                fields: $scope.uploadPreAuthorizationDto,
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
