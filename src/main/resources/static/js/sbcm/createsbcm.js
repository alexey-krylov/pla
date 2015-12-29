(function (angular){
    "use strict";
    var app=angular.module('createSbcm', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover',
        'directives', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices','ui.bootstrap.modal','ngMessages']);

    app.config(["$routeProvider", function ($routeProvider) {
        $routeProvider.when('/', {
                templateUrl: 'createsbcm.html',
                controller: 'createSbcmController',
                resolve:{
                    plans: ['$q', '$http', function ($q, $http) {
                        var deferred = $q.defer();
                        $http.get('/pla/core/sbcm/getAllPlanWithRelatedBenefitCoverages').success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        return deferred.promise;
                    }],
                    services: ['$q', '$http', function ($q, $http) {
                        var deferred = $q.defer();
                        $http.get('/pla/core/sbcm/getAllServicesFromHCPRate').success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        return deferred.promise;
                    }]

                }
            }
        )}]);

    app.controller('createSbcmController',['$scope','$http','plans' ,'services','getQueryParameter', function ( $scope, $http, plans, services,getQueryParameter ){
        $scope.plans=plans;
        $scope.services=services;
        $scope.coverages = [];
        $scope.benefits = [];
        $scope.createsbcmcommand = {};
        $scope.mode=getQueryParameter('mode');
        $scope.serviceBenefitCoverageMappingId = getQueryParameter("serviceBenefitCoverageMappingId");

        if($scope.serviceBenefitCoverageMappingId){
            $http.get("/pla/core/sbcm/getSBCMBySBCMId?serviceBenefitCoverageMappingId="+$scope.serviceBenefitCoverageMappingId).success(function(data){
                $scope.createsbcmcommand = data;
            }).error(function(){});
        }

        $scope.$watch('createsbcmcommand.planCode',function(newVal,oldVal){
            if(newVal){
                var plan = _.findWhere($scope.plans, {planCode: newVal});
                if(plan){
                    $scope.coverages=plan.coverages;
                }
            }
        });

        $scope.$watch('createsbcmcommand.coverageId',function(newVal,oldVal){
            if(newVal){
                var coverage = _.findWhere($scope.coverages, {coverageId: newVal});
                if(coverage){
                    $scope.benefits=coverage.benefits;
                }
            }
        });

        $scope.saveHCPRates=function(){
            console.log($scope.createsbcmcommand);
            $http({
                url: '/pla/core/sbcm/createServiceBenefitCoverageMapping',
                method: 'POST',
                data: $scope.createsbcmcommand
            }).then(function(response) {
                },
                function(response) {
                });

        }
    }
    ]);
})(angular);