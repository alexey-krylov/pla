/**
 * Created by pradyumna on 26-05-2015.
 */
(function (angular) {
    "use strict";
    angular.module('individualQuotation', ['common', 'ngRoute', 'commonServices', 'ngMessages', 'angucomplete-alt'])
        .config(['datepickerPopupConfig', function (datepickerPopupConfig) {
        datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
        datepickerPopupConfig.currentText = 'Today';
        datepickerPopupConfig.clearText = 'Clear';
        datepickerPopupConfig.closeText = 'Done';
        datepickerPopupConfig.closeOnDateSelection = true;
    }])
        .config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'quotation/searchForm'
            })
            .when('/new', {
                templateUrl: 'quotation/new',
                controller: 'QuotationController',
                controllerAs: 'ctrl',
                resolve: {
                    occupations: ['$q', '$http', function ($q, $http) {
                        var deferred = $q.defer();
                        $http.get('/pla/individuallife/proposal/getAllOccupation').success(function (response, status, headers, config) {
                            deferred.resolve(response);
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        return deferred.promise;
                        ;
                    }]
                }
            })
            .when('/edit/:quotationId', {
                templateUrl: 'quotation/new',
                controller: 'QuotationController',
                controllerAs: 'ctrl',
                resolve: {
                    occupations: ['$q', '$http', function ($q, $http) {
                        var deferred = $q.defer();
                        $http.get('/pla/individuallife/proposal/getAllOccupation').success(function (response, status, headers, config) {
                            deferred.resolve(response);
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        return deferred.promise;
                    }]
                }
            })
        $routeProvider.otherwise({
            templateUrl: 'quotation/searchForm'
        });
        }])
        .directive('sumassured', function () {
            return {
                restrict: 'E',
                templateUrl: 'plan-sumassured.tpl'
            };
        })
        .directive('policyterm', function () {
            return {
                restrict: 'E',
                templateUrl: 'plan-policyterm.tpl',
                controller: ['$scope', function ($scope) {
                    $scope.policyTerms = function () {
                        if ($scope.plan.policyTermType === 'SPECIFIED_VALUES') {
                            var maxMaturityAge = $scope.plan.policyTerm.maxMaturityAge || 1000;
                            if (typeof $scope.proposedAssured.dateOfBirth === 'object') {
                                var ageNextBirthday = moment().diff(new moment($scope.proposedAssured.dateOfBirth.toDateString()), 'years') + 1;
                                return _.filter($scope.plan.policyTerm.validTerms, function (term) {
                                    return ageNextBirthday + term <= maxMaturityAge;
                                });
                            } else if ($scope.plan.policyTermType === 'MATURITY_AGE_DEPENDENT') {
                                var ageNextBirthday = moment().diff(new moment($scope.proposedAssured.dateOfBirth.toDateString()), 'years') + 1;
                                return _.filter($scope.plan.policyTerm.maturityAges, function (term) {
                                    return term > ageNextBirthday;
                                });
                            }
                        }
                        return [];
                    };
                }]
            };
        })
        .directive('premiumterm', function () {
            return {
                restrict: 'E',
                templateUrl: 'plan-premiumterm.tpl',
                controller: ['$scope', function ($scope) {
                    $scope.premiumTerm = function () {
                        if ($scope.plan.premiumTermType === 'SPECIFIED_VALUES') {
                            var maxMaturityAge = $scope.plan.premiumTermType.maxMaturityAge || 1000;
                            var ageNextBirthday = moment().diff(new moment($scope.proposedAssured.dateOfBirth.toDateString()), 'years') + 1;
                            return _.filter($scope.plan.premiumTermType.validTerms, function (term) {
                                return ageNextBirthday + term <= maxMaturityAge;
                            });
                        } else if ($scope.plan.premiumTermType === 'SPECIFIED_AGES') {
                            var ageNextBirthday = moment().diff(new moment($scope.proposedAssured.dateOfBirth.toDateString()), 'years') + 1;
                            return _.filter($scope.plan.premiumTermType.maturityAges, function (term) {
                                return term > ageNextBirthday;
                            });
                        } else if ($scope.plan.premiumTermType === 'REGULAR') {
                            $scope.quotation.premiumTerm = $scope.quotation.policyTerm;
                        }
                    };
                }]
            };
        })
        .controller('QuotationController', ['$scope', '$http', '$route', '$bsmodal',
        'globalConstants', 'occupations',
            function ($scope, $http, $route, $bsmodal, globalConstants, occupations) {

            $scope.titleList = globalConstants.title;
            $scope.occupations = occupations;
            $scope.quotation = {};
            $scope.selectedItem = 1;
            $scope.onlyNumbers = /^[0-9]+$/;

                var quotationId = $route.current.params.quotationId;
                if (quotationId) {
                    $http.get('/pla/individuallife/quotation/getquotation/' + quotationId).success(function (response, status, headers, config) {
                        $scope.quotation = response;
                        $scope.proposedAssured = $scope.quotation.proposedAssured || {};
                        $scope.proposer = $scope.quotation.proposer || {};
                        $scope.selectedAgent = {};
                        $scope.selectedAgent.title = response.agentDetail.firstName || '';
                        $scope.selectedAgent.title = $scope.selectedAgent.title + ' ' + response.agentDetail.lastName || '';
                        $scope.selectedAgent.description = response.agentDetail;


                        $scope.selectedPlan = {};
                        $scope.selectedPlan.title = response.planDetail.planDetail.planName || '';
                        $scope.selectedPlan.description = response.planDetail;
                    }).error(function (response, status, headers, config) {
                    });
            }

            $scope.$watch('selectedAgent', function (newval) {
                console.log('selectedAgent' + JSON.stringify(newval));
                if (newval) {
                    $scope.agent = newval.description;
                    $scope.quotation.agentId = $scope.agent["agent_id"];
                }
            });

            $scope.$watch('selectedPlan', function (newval, oldval) {
                console.log(JSON.stringify(newval));
                if (newval && newval !== oldval) {
                    $scope.plan = newval.description;
                    $scope.quotation.planId = $scope.plan.plan_id;
                }
            });

            $scope.$watch('proposerSameAsProposedAssured', function (newval, oldval) {
                if (newval) {
                    $scope.proposer = $scope.proposedAssured;
                }
            });

            $scope.launchProposerDOB = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob2 = true;
            };

            $scope.launchProposedAssuredDOB = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob1 = true;
            };

            $scope.$watchGroup(['proposedAssured.dateOfBirth', 'proposer.dateOfBirth'], function (newval, oldval) {
                if (newval) {
                    if (newval[0] && typeof newval[0] === 'object') {
                        $scope.proposedAssuredAge = moment().diff(new moment(newval[0].toDateString()), 'years') + 1;
                        console.log('$scope.proposedAssuredAge ' + $scope.proposedAssuredAge);
                    }
                    if (newval[1] && typeof newval[1] === 'object') {
                        $scope.proposerAge = moment().diff(new moment(newval[1].toDateString()), 'years') + 1;
                    }
                    console.log($scope.proposedAssured);
                }
            });

            $scope.saveStep1 = function () {
                $http.post('quotation/createquotation',
                    angular.extend($scope.proposedAssured, {
                        agentId: $scope.quotation.agentId,
                        planId: $scope.quotation.planId,
                        isAssuredTheProposer: false
                    }))
                    .success(function (data) {
                        $scope.quotation.quotationId = data.id;
                        $http.get('quotation/getquotationnumber/' + data.id).success(function (data) {
                            $scope.quotation.quotationNumber = data.id;
                        });
                    });
            };

            $scope.saveStep2 = function () {
                $scope.proposedAssured.dateOfBirth = new moment($scope.proposedAssured.dateOfBirth).format('DD/MM/YYYY');
                var request = {proposedAssured: $scope.proposedAssured};
                $http.post('quotation/updatewithassureddetail',
                    angular.extend(request, {
                        quotationId: $scope.quotation.quotationId,
                        isAssuredTheProposer: $scope.proposerSameAsProposedAssured
                    }))
                    .success(function (data) {
                    });
            };

            $scope.saveStep3 = function () {
                console.log('saving step 3 form data');
                $scope.proposer.dateOfBirth = new moment($scope.proposer.dateOfBirth).format('DD/MM/YYYY');
                var request = {proposer: $scope.proposer};
                $http.post('quotation/updatewithproposerdetail',
                    angular.extend(request, {
                        quotationId: $scope.quotation.quotationId
                    }))
                    .success(function (data) {
                    });
            };


            $scope.remoteUrlRequestFn = function (str) {
                console.log($scope.quotation.agentId);
                return {agentId: $scope.quotation.agentId};
            };
        }]
);
})(angular);


var viewILQuotationModule = (function () {
    var services = {};
    services.selectedItem = "";
    services.status = '';
    services.getTheItemSelected = function (ele) {
        this.selectedItem = $(ele).val();
        this.status = $(ele).parent().find('input[type=hidden]').val();
        $(".btn-disabled").attr("disabled", false);
        if (this.status == 'GENERATED') {
            $('#emailaddress').attr('disabled', false);
            $('#print').attr('disabled', false);
        } else {
            $('#emailaddress').attr('disabled', true);
            $('#print').attr('disabled', true);
        }
    };

    services.reload = function () {
        window.location.reload();
    };

    services.printQuotation = function () {
        window.location.href = '/pla/individuallife/quotation/printquotation/' + this.selectedItem;
    }

    services.emailQuotation = function () {
        window.location.href = '/pla/individuallife/quotation/openemailquotation/' + this.selectedItem;
    }

    services.modifyQuotation = function () {
        var quotationId = this.selectedItem;
        window.location.href = "/pla/individuallife/quotation#/edit/" + quotationId;
    };

    services.viewQuotation = function () {
        var quotationId = this.selectedItem;
        window.location.href = "/pla/individuallife/quotation/creategrouplifequotation?quotationId=" + quotationId + "&version=" + msg.data + "&mode=view";
    };

    return services;
})();

/*function QuotationController(){
 this.selectedItem=5;
 this.proposedAssured={
 title:'MR'
 };
 }
 */
