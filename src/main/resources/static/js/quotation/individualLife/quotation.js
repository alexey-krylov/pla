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
        .controller('QuotationController', ['$scope', '$http', '$bsmodal',
        'globalConstants', 'occupations',
        function ($scope, $http, $bsmodal, globalConstants, occupations) {
            console.log('Quoation controlelrererer************8');
            $scope.titleList = globalConstants.title;
            $scope.occupations = occupations;
            $scope.quotation = {};
            $scope.selectedItem = 1;
            $scope.onlyNumbers = /^[0-9]+$/;

            $scope.proposedAssured = {
                dateOfBirth: '1978-02-17',
                firstName: "ewrretre",
                "nrcNumber": "retretret",
                surname: "ertretre",
                title: "Mrs."
            }

            $scope.plan = {
                "_class": "com.pla.core.domain.model.plan.Plan",
                "planDetail": {
                    "planName": "GL Self",
                    "planCode": "1001",
                    "launchDate": new Date("21-5-2015 00:00:00"),
                    "withdrawalDate": new Date("31-5-2015 00:00:00"),
                    "freeLookPeriod": 15,
                    "minEntryAge": 25,
                    "maxEntryAge": 60,
                    "taxApplicable": false,
                    "funeralCover": false,
                    "surrenderAfter": 0,
                    "applicableRelationships": ["SELF"],
                    "endorsementTypes": [{
                        "description": "Change of Contact Details- Life Assured"
                    }],
                    "lineOfBusinessId": "GROUP_LIFE",
                    "planType": "NON_INVESTMENT",
                    "clientType": "GROUP",
                    "_class": "com.pla.core.domain.model.plan.PlanDetail"
                },
                "status": "DRAFT",
                "sumAssured": {
                    "sumAssuredValue": ["100000", "200000", "300000", "400000", "500000"],
                    "minSumInsured": 1000,
                    "maxSumInsured": 2000,
                    "incomeMultiplier": 10,
                    "percentage": 0,
                    "multiplesOf": 0,
                    "sumAssuredType": "RANGE",//SPECIFIED_VALUES//RANGE//INCOME_MULTIPLIER
                    "_class": "com.pla.core.domain.model.plan.SumAssured"
                },
                "policyTermType": "SPECIFIED_VALUES",
                "premiumTermType": "REGULAR",
                "premiumTerm": {
                    "validTerms": [35, 40, 45, 50, 55],
                    "maturityAges": [],
                    "maxMaturityAge": 0,
                    "groupTerm": 365,
                    "_class": "com.pla.core.domain.model.plan.Term"
                },
                "policyTerm": {
                    "validTerms": [35, 90, 25, 50, 55],
                    "maturityAges": [],
                    "maxMaturityAge": 40,
                    "groupTerm": 365,
                    "_class": "com.pla.core.domain.model.plan.Term"
                },
                "coverages": [{
                    "coverageId": {
                        "coverageId": "90CB7D65-33A9-4E71-8A86-81C1F4F2A98E"
                    },
                    "coverageCover": "ACCELERATED",
                    "coverageType": "OPTIONAL",
                    "deductibleType": "PERCENTAGE",
                    "waitingPeriod": 0,
                    "minAge": 30,
                    "maxAge": 50,
                    "taxApplicable": false,
                    "coverageSumAssured": {
                        "sumAssuredValue": ["10000", "20000", "30000"],
                        "percentage": 0,
                        "multiplesOf": 0,
                        "sumAssuredType": "SPECIFIED_VALUES"
                    },
                    "coverageTermType": "POLICY_TERM",
                    "maturityAmounts": [],
                    "planCoverageBenefits": [{
                        "benefitId": {
                            "benefitId": "DC72FA2D-A2B9-4301-AEBC-D9AEE69B6DE1"
                        },
                        "definedPer": "INCIDENCE",
                        "coverageBenefitType": "AMOUNT",
                        "benefitLimit": "10000",
                        "maxLimit": "30000"
                    }],
                    "_class": "com.pla.core.domain.model.plan.PlanCoverage"
                }]
            };

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
        $.ajax({
            url: '/pla/individuallife/quotation/getversionnumber/' + quotationId,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            success: function (msg) {
                if (msg.status == '200') {
                    window.location.href = "/pla/quotation/grouplife/creategrouplifequotation?quotationId=" + quotationId + "&version=" + msg.data + "&mode=edit";
                } else if (msg.status == '500') {
                }
            }
        });
    };

    services.viewQuotation = function () {
        var quotationId = this.selectedItem;
        $.ajax({
            url: '/pla/individuallife/quotation/getversionnumber/' + quotationId,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            success: function (msg) {
                if (msg.status == '200') {
                    window.location.href = "/pla/individuallife/quotation/creategrouplifequotation?quotationId=" + quotationId + "&version=" + msg.data + "&mode=view";
                } else if (msg.status == '500') {
                }
            }
        });
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
