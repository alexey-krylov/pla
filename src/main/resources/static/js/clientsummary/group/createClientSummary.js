
(function (angular) {
    "use strict";

    var app = angular.module('createProductClaim', ['ngRoute','ui.bootstrap.modal','common','commonServices','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert','ngMessages','directives']);

    app.config(["$routeProvider", function ($routeProvider) {
        $routeProvider.when('/', {
                templateUrl: 'viewUpdateClientSummary.html',
                controller: 'groupClientSummaryController',
                resolve: {

                }
            }

        )}])

    app.controller('groupClientSummaryController', ['$scope', '$http', '$location','getQueryParameter','globalConstants', function ($scope, $http, $location, getQueryParameter,globalConstants) {
        $scope.provinces = [];
        $scope.proposerEmploymentCities=[];
        $scope.selectedItem = 1;
        $scope.titles = globalConstants.title;
        $scope.todayDate = new Date();

        $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
            $scope.provinces = response;
        }).error(function (response, status, headers, config) {
        });

        $scope.getProposerEmpProvinceValue = function (province) {
            //alert('Hi..');
            //alert(province);
            var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
            if (provinceDetails)
                $scope.proposerEmploymentCities = provinceDetails.cities;
        }

        $scope.getProposerEmpProvinceValue = function (province) {
            //alert(province);
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
            window.location.href = "/pla/core/clientsummary/groupclientsummary"
        }

        var clientSummary= {
            "proposerName":"Raj",
            "workPhone":"6576475644",
            "clientCode":"121219888888",
            "emailAddress":"prasant_nayak@nthdimenzion.com",
            "contactDetail":{
                "address1":"sahakar nagar",
                "address2":"sahakar nagar2",
                "province":"PR-LUA",
                "town":"CI-MWE",
                "postalCode":1234567
            },
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
