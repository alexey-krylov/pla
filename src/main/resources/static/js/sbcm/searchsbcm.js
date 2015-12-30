(function (angular){
    "use strict";
    var app=angular.module('searchSbcm', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover',
        'directives', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices','ui.bootstrap.modal','ngMessages']);

    app.config(["$routeProvider", function ($routeProvider) {
        $routeProvider.when('/', {
                templateUrl: 'searchsbcm.html',
                controller: 'searchSbcmController',
                resolve: {
                    allSBCM : ['$q', '$http', function ($q, $http) {
                        var deferred = $q.defer();
                        $http.get('/pla/core/sbcm/getSBCMForGivenPage?pageNo=0').success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        return deferred.promise;
                    }],
                    numberOfSBCM : ['$q', '$http', function ($q, $http) {
                        var deferred = $q.defer();
                        $http.get('/pla/core/sbcm/numberOfSBCMAvailable').success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        return deferred.promise;
                    }]
                }
            }
        )}]);

    app.filter('startFrom', function() {
        return function(input, start) {
            start = +start; //parse to int
            return input.slice(start);
        }
    });

    app.controller('searchSbcmController',['$scope','$http','allSBCM', 'numberOfSBCM', function ($scope, $http, allSBCM, numberOfSBCM){
        $scope.updateStatusCommand = {};
        $scope.allSBCM = allSBCM;
        $scope.currentPage = 0;
        $scope.pageSize = 10;
        $scope.numberOfSBCM = numberOfSBCM;

        $scope.numberOfPages=function(){
            console.log($scope.allSBCM.length);
            //alert($scope.allSBCM.length);
            return Math.ceil($scope.allSBCM.length/$scope.pageSize);
        }
        $scope.viewsbcm = function(sbcm) {
            window.location.href = "/pla/core/sbcm/getsbcmview?serviceBenefitCoverageMappingId="+ sbcm.serviceBenefitCoverageMappingId + "&mode=view";
        };

        $scope.updatesbcm = function(sbcm) {
            sbcm.status = 'INACTIVE';
            $http({
                url: '/pla/core/sbcm/updateSBCMStatus',
                method: 'POST',
                data: JSON.stringify(sbcm)
            }).success(function(response) {
                if (response.status === "200" && response.message==='Status Updated Successfully') {
                    console.log(sbcm);
                    console.log($scope.allSBCM);
                    $scope.allSBCM.splice($scope.allSBCM.indexOf(sbcm), 1);
                }
            }).error(function(response) {});
        };

        $scope.$watchCollection("viewsbcm", function(newValue, oldValue) {});

        $scope.incrementCurrentPage = function(){
            $scope.allSBCM = [];
            $scope.currentPage = $scope.currentPage+1;
            $http.get('/pla/core/sbcm/getSBCMForGivenPage?pageNo='+$scope.currentPage).success(function (data) {
                $scope.allSBCM = data;
            }).error(function (response, status, headers, config) {
            });
        }

        $scope.decrementCurrentPage = function(){
            $scope.allSBCM = [];
            $scope.currentPage = $scope.currentPage-1;
            $http.get('/pla/core/sbcm/getSBCMForGivenPage?pageNo='+$scope.currentPage).success(function (data) {
                $scope.allSBCM = data;
            }).error(function (response, status, headers, config) {
            });
        }

        $scope.$watchCollection("allSBCM", function(newValue, oldValue) {});
    }

    ]);
})(angular);