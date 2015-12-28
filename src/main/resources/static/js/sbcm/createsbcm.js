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
                    }]
                }
            }
        )}]);

    app.controller('createSbcmController',['$scope','$http','plans' , function ( $scope, $http,plans ){
        $scope.plans=plans;
        $scope.coverages = [];
        $scope.benefits = [];
        $scope.createsbcmcommand = {};
        console.log(JSON.stringify($scope.plans));

        $scope.$watch('createsbcmcommand.planId',function(newVal,oldVal){
            if(newVal){
                var plan = _.findWhere($scope.plans, {planCode: newVal});
                if(plan){
                    $scope.coverages=plan.coverages;
                }
            }
        });
        $scope.$watch('createsbcmcommand.coverage',function(newVal,oldVal){
            if(newVal){
                var coverage = _.findWhere($scope.coverages, {coverageId: newVal});
                if(coverage){
                    $scope.benefits=coverage.benefits;
                }
            }
        });
























    }
    ]);


})(angular);