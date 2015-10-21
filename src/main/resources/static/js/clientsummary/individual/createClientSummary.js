
(function (angular) {
    "use strict";

    var app = angular.module('createProductClaim', ['ngRoute','ui.bootstrap.modal','common','commonServices','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert','ngMessages','directives']);

    app.config(["$routeProvider", function ($routeProvider) {
        $routeProvider.when('/', {
                templateUrl: 'viewUpdateClientSummary.html',
                controller: 'individualClientSummaryController',
                resolve: {

                }
            }

        )}])
        .config(['datepickerPopupConfig', function (datepickerPopupConfig) {
            datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
            datepickerPopupConfig.currentText = 'Today';
            datepickerPopupConfig.clearText = 'Clear';
            datepickerPopupConfig.closeText = 'Done';
            datepickerPopupConfig.closeOnDateSelection = true;
        }]);

    app.controller('individualClientSummaryController', ['$scope', '$http', '$location','getQueryParameter','globalConstants','$upload', function ($scope, $http, $location, getQueryParameter,globalConstants) {
        $scope.provinces = [];
        $scope.proposerEmploymentCities=[];
        $scope.selectedItem = 1;
        $scope.titles = globalConstants.title;
        $scope.todayDate = new Date();

        $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
            $scope.provinces = response;
            //console.log('Provinces..'+JSON.stringify($scope.provinces));
        }).error(function (response, status, headers, config) {
        });

        $scope.getProvinceValue= function (province) {
            console.log('Provinces..'+JSON.stringify($scope.provinces));
            var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
            if (provinceDetails)
                $scope.proposerEmploymentCities = provinceDetails.cities;
        }

        $scope.launchClientDob = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.launchdob2 = true;
        };


        $scope.cancel=function(){
            window.location.href = "/pla/core/clientsummary/individualclientsummary"
        }

        var clientSummary= {
            "title":"Mrs.",
            "firstName":"Raj",
            "surname":"Nair",
            "otherName":null,
            "nrc":"234567/33/6",
            "dateOfBirth":"1983-08-30T00:00:00.000+05:30",
            "gender":"FEMALE",
            "mobileNumber":"6576475644",
            "clientCode":"121219888888",
            "emailAddress":"prasant_nayak@nthdimenzion.com",
            "maritalStatus":"MARRIED",
            "contactDetail":{
                "address1":"sahakar nagar",
                "address2":null,
                "province":"PR-LUA",
                "town":"CI-MWE",
                "postalCode":1234567,
                "homePhone":1234567890,
                "emailAddress":null
            },
            "clientDocumentDetailDtoList":[
                {
                    "documentId":"12345",
                    "gridFsDocId":"feehu263khjksf84tr43209",
                    "documentName":"documentName1",
                    "documentType":"documentType1",
                    "routingLevel":"routingLevel1",
                    "documentContent":"documentContent1"
                },
                {
                    "documentId":"123456",
                    "gridFsDocId":"feehu263khjksf84tr432092",
                    "documentName":"documentName2",
                    "documentType":"documentType2",
                    "routingLevel":"routingLevel2",
                    "documentContent":"documentContent2"
                },
                {
                    "documentId":"1234567",
                    "gridFsDocId":"feehu263khjksf84tr432093",
                    "documentName":"documentName3",
                    "documentType":"documentType3",
                    "routingLevel":"routingLevel3",
                    "documentContent":"documentContent3"
                }
            ],
            "clientPolicyDetailDtoList":[
                {
                    "number":"12345",
                    "clientType":"Per",
                    "underWriterDecision":"underWriterDecision1",
                    "underWriterComments":"underWriterComments1"
                },
                {
                    "number":"123456",
                    "clientType":"Temp",
                    "underWriterDecision":"underWriterDecision2",
                    "underWriterComments":"underWriterComments2"
                }
            ],
            "clientPropsalDetailDtoList":[
                {
                    "number":"1234",
                    "clientType":"Per",
                    "underWriterDecision":"underWriterDecision1",
                    "underWriterComments":"underWriterComments1"
                },
                {
                    "number":"123",
                    "clientType":"Temp",
                    "underWriterDecision":"underWriterDecision2",
                    "underWriterComments":"underWriterComments2"
                }
            ]
        }
        $scope.clientDetail=clientSummary;

    }])

})(angular);
