'use strict';
var app = angular.module('planSetup', ['common', 'ngTagsInput', 'checklist-model', 'ngRoute', 'ui.bootstrap']);

app.config(function (tagsInputConfigProvider) {
    tagsInputConfigProvider.setDefaults('tagsInput', {
        placeholder: 'Add a number',
        removeTagSymbol: 'x',
        minLength: 1
    });
});
app.config(['datepickerPopupConfig', function (datepickerPopupConfig) {
    datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
    datepickerPopupConfig.currentText = 'Today';
    datepickerPopupConfig.clearText = 'Clear';
    datepickerPopupConfig.closeText = 'Done';
    datepickerPopupConfig.closeOnDateSelection = true;
}]);
app.config(function ($routeProvider, $locationProvider) {
    $routeProvider
        .when('/plan', {
            templateUrl: 'plan/list'
        })
        .when('/newplan', {
            templateUrl: "plan/newplan",
            controller: 'PlanSetupController',
            resolve: {
                plan: function () {
                    return {
                        "planDetail": {
                            "planName": "JEEVAN ANAND",
                            "planCode": "JEEVEN-001",
                            "launchDate": "2015-05-14",
                            "withdrawalDate": "2015-12-29",
                            "freeLookPeriod": 15,
                            "minEntryAge": 19,
                            "maxEntryAge": 65,
                            "taxApplicable": true,
                            "surrenderAfter": 5,
                            "applicableRelationships": [
                                "SELF",
                                "SISTER",
                                "BROTHER"
                            ],
                            "endorsementTypes": [
                                "NAME",
                                "DATE_OF_BIRTH",
                                "ADDRESS"
                            ],
                            "lineOfBusinessId": "INDIVIDUAL_INSURANCE",
                            "planType": "INVESTMENT",
                            "clientType": "INDIVIDUAL"
                        },
                        "policyTermType": "SPECIFIED_VALUES",
                        "premiumTermType": "SPECIFIED_VALUES",
                        "policyTerm": {
                            "validTerms": [
                                {
                                    "text": 10
                                },
                                {
                                    "text": 20
                                },
                                {
                                    "text": 30
                                },
                                {
                                    "text": "45"
                                },
                                {
                                    "text": "50"
                                }
                            ],
                            "maturityAges": [],
                            "maxMaturityAge": 65
                        },
                        "premiumTerm": {
                            "validTerms": [
                                {
                                    "text": 10
                                },
                                {
                                    "text": 20
                                },
                                {
                                    "text": 30
                                },
                                {
                                    "text": "35"
                                }
                            ],
                            "maturityAges": [],
                            "maxMaturityAge": 65
                        },
                        "sumAssured": {
                            "sumAssuredValue": [
                                {
                                    "text": "1000000"
                                },
                                {
                                    "text": "2000000"
                                },
                                {
                                    "text": "300000"
                                }
                            ],
                            "percentage": 0,
                            "multiplesOf": 0,
                            "sumAssuredType": "SPECIFIED_VALUES"
                        },
                        coverages: [{
                            "maturityAmounts": [{"maturityYear": 5, "guaranteedSurvivalBenefitAmount": "5"}],
                            "coverageId": "306B8DF2-8298-4801-93C8-23D26E7018B7",
                            "coverageCover": "ACCELERATED",
                            "deductibleType": "AMOUNT",
                            "coverageSumAssured": {"sumAssuredType": "RANGE", "minSumInsured": "1", "maxSumInsured": "2", "multiplesOf": "1000"},
                            "taxApplicable": "true",
                            "coverageTermType": "SPECIFIED_VALUES",
                            "coverageTerm": {"validTerms": [{"text": "45"}], "maxMaturityAge": 65},
                            "coverageType": "OPTIONAL",
                            "minAge": 19,
                            "maxAge": 65
                        }]
                    }
                },
                activeCoverages: ['$q', '$http', function ($q, $http) {
                    var deferred = $q.defer();
                    $http.get('/pla/core/coverages/activecoverage').success(function (response, status, headers, config) {
                        deferred.resolve(response);
                    }).error(function (response, status, headers, config) {
                        deferred.reject();
                    });
                    return deferred.promise;
                }]
            }
        })
        .when('/viewplan/:planid', {
            templateUrl: "plan/viewplan/:planid",
            controller: 'PlanSetupController',
            resolve: {
                plan: ['$q', '$route', '$http', function ($q, $route, $http) {
                    var deferred = $q.defer();
                    if (angular.isDefined($route.current.params.planid)) {
                        $http({
                            method: 'GET',
                            url: '/pla/core/plan/getPlanById/' + $route.current.params.planid
                        }).success(function (data) {
                            deferred.resolve(data);
                        }).error(function (msg) {
                            deferred.reject(msg);
                        });
                    } else {
                        deferred.resolve();
                    }
                    return deferred.promise;
                }],
                activeCoverages: ['$q', '$http', function ($q, $http) {
                    var deferred = $q.defer();
                    $http.get('/pla/core/coverages/activecoverage').success(function (response, status, headers, config) {
                        deferred.resolve(response);
                    }).error(function (response, status, headers, config) {
                        deferred.reject();
                    });
                    return deferred.promise;
                }]
            }
        });
    $routeProvider.otherwise({redirectTo: '/plan'});
});
app.controller('PlanSetupController', ['$scope', '$http', '$location', '$routeParams', '$bsmodal', 'plan', 'activeCoverages',
        function ($scope, $http, $location, $routeParams, $modal, plan, activeCoverages) {

            $scope.plan = plan;
            $scope.coverageList = activeCoverages;
            $scope.steps = [{"title": "step-1"}, {"title": "step-2"}];
            $scope.currentStepIndex = 0;

            $scope.onlyNumbers = /^[0-9]+$/;
            $scope.today = function () {
                $scope.dt = new Date();
            };
            $scope.today();
            $scope.productName;
            $scope.planSetUpForm = {};
            $scope.relations = [
                {val: 'SELF', desc: 'Self'},
                {val: 'SISTER', desc: 'Sister'},
                {val: 'BROTHER', desc: 'Brother'},
                {val: 'WIFE', desc: 'Wife'},
                {val: 'FATHER', desc: 'Father'},
                {val: 'MOTHER', desc: 'Mother'},
                {val: 'SON', desc: 'Son'},
                {val: 'DAUGHTER', desc: 'Daughter'},
                {val: 'STEP_DAUGHTER', desc: 'Step Daughter'},
                {val: 'FATHER_IN_LAW', desc: 'Father-in-law'},
                {val: 'MOTHER_IN_LAW', desc: 'Mother-in-law'},
                {val: 'DEPENDENTS', desc: 'Dependents'}];
            $scope.endorsementTypes = [
                {val: 'NAME', desc: 'Correction of Name', clientType: 'INDIVIDUAL'},
                {val: 'ADDRESS', desc: 'Change of Address', category: 'INDIVIDUAL'},
                {val: 'BENEFICIARY', desc: 'Change/Add Beneficiary', category: 'INDIVIDUAL'},
                {val: 'PAYMENT', desc: 'Change method of Payment', category: 'INDIVIDUAL'},
                {val: 'AGENT', desc: 'Change Agent', category: 'INDIVIDUAL'},
                {val: 'CHANGE_PAYER', desc: 'Change Sum Assured', category: 'INDIVIDUAL'},
                {val: 'SUM_ASSURED', desc: 'Change Sum Assured', category: 'INDIVIDUAL'},
                {val: 'DATE_OF_BIRTH', desc: 'Change Life Assured Date of Birth', category: 'INDIVIDUAL'},
                {val: 'MEMBER_ADDITION', desc: 'Member Addition', category: 'GROUP'},
                {val: 'MEMBER_DELETION', desc: 'Member Deletion', category: 'GROUP'},
                {val: 'PROMOTION', desc: 'Promotion', category: 'GROUP'},
                {val: 'NEW_COVER', desc: 'Introduction of New Cover', category: 'GROUP'}
            ];

            $scope.resetPlanSumAssured = function () {
                $scope.plan.sumAssured.sumAssuredValue = [];
                $scope.plan.sumAssured.minSumInsured = null;
                $scope.plan.sumAssured.maxSumInsured = null;
                $scope.plan.sumAssured.multiplesOf = null;

            }

            $scope.clientType = $scope.plan.planDetail.clientType;

            $scope.$watch('clientType', function (val, old) {
                if (!angular.isUndefined(val))
                    $scope.plan.planDetail.clientType = val;
            });

            $scope.isSurrenderDisabled = function () {
                if ($scope.clientType == 'GROUP') {
                    $scope.surrenderAfter = '';
                    return true;
                }
            };

            $scope.isSurrenderReq = function () {
                if ($scope.clientType == 'INDIVIDUAL')
                    return 'required';
            };

            $scope.newCoverage = {maturityAmounts: [{name: "from parent"}]};
            $scope.emptyCoverage = {maturityAmounts: []};

            //var myOtherModal = $modal({scope: $scope, template: '/pla/plan/coverage-form.html', show: false});

            $scope.showCoverageForm = function (coverage, isEditing) {
                $modal.open({
                    templateUrl: '/pla/plan/coverage-form.html',
                    backdrop: true,
                    windowClass: 'modal',
                    controller: function ($scope, $modalInstance, $log, newCoverage, plan, coverageList, isEditing) {
                        $scope.plan = plan;
                        $scope.coverageList = coverageList;
                        $scope.newCoverage = newCoverage;
                        $scope.editFlag = isEditing;

                        $scope.cancel = function () {
                            $modalInstance.dismiss('cancel');
                        };

                        $scope.addMaturityRow = function () {
                            $scope.newCoverage.maturityAmounts.push({});
                        };

                        $scope.removeMaturityRow = function (index) {
                            $scope.newCoverage.maturityAmounts.splice(index, 1);
                        };

                        $scope.addCoverage = function (newCoverage) {
                            $scope.plan.coverages.push(newCoverage);
                            $scope.newCoverage = angular.copy($scope.emptyCoverage);
                            $scope.coverageForm.$setPristine();
                            $scope.coverageForm.$setUntouched();
                            $scope.coverageForm.$setValidity();
                            $scope.coverageSumAssured = [];
                            $scope.coverageTerm = [];
                            $scope.coverageMaturityAge = [];
                            $scope.cancel();
                        };

                        $scope.removeCoverage = function (idx) {
                            $scope.plan.coverages.splice(idx, 1);
                        };

                    },
                    resolve: {
                        newCoverage: function () {
                            return angular.isUndefined(coverage) ? {maturityAmounts: []} : coverage;
                        },
                        coverageList: function () {
                            var listOfCoverageIds = _.pluck($scope.plan.coverages, "coverageId");
                            var unUsedCoverageList = _.reject($scope.coverageList, function (coverage) {
                                return _.contains(listOfCoverageIds, coverage.coverageId);
                            });
                            return unUsedCoverageList;
                        },
                        plan: function () {
                            return $scope.plan;
                        },
                        isEditing: function () {
                            return isEditing
                        }
                    }
                });
            };


            $scope.$watch('planSumAssuredType', function (newval) {
                if (!angular.isUndefined(newval)) {
                    $scope.planSumAssured = [];
                    $scope.plan.sumAssured.sumAssuredValue = [];
                }
            });

            $scope.resolveCoverage = function (coverageId) {
                console.log('resolveCoverage :: coverageId ' + coverageId);
                console.log('resolveCoverage :: $scope.coverageList ' + JSON.stringify($scope.coverageList));
                var coverage = _.findWhere($scope.coverageList, {'coverageId': coverageId});
                console.log('resolveCoverage :: resolved coverage ' + JSON.stringify(coverage));
                return coverage;
            }

            $scope.getCoverageNameFromBenefit = function (benefitId) {
                console.log('getCoverageNameFromBenefit :: benefitId ' + benefitId);
                var i = 0;
                for (; i < $scope.plan.coverages.length; i++) {
                    var eachCoverage = $scope.plan.coverages[i];
                    console.log('getCoverageNameFromBenefit :: found coverage ' + JSON.stringify(eachCoverage));
                    var benefit = _.findWhere(eachCoverage.planCoverageBenefits, {'benefitId': benefitId});
                    if (benefit) {
                        var coverage = $scope.resolveCoverage(eachCoverage.coverageId);
                        console.log(' getCoverageNameFromBenefit :: coverage name from Benefit' + coverage.coverageName);
                        return coverage.coverageName;
                    }
                }
                return '';
            }


            $scope.geBenefitNameFromBenefit = function (benefitId) {
                console.log('geBenefitNameFromBenefit :: benefitId ' + benefitId);
                var i = 0;
                for (; i < $scope.coverageList.length; i++) {
                    var eachCoverage = $scope.coverageList[i];
                    console.log('geBenefitNameFromBenefit :: found coverage ' + JSON.stringify(eachCoverage));
                    var benefit = _.findWhere(eachCoverage.benefitDtos, {'benefitId': benefitId});
                    if (benefit) {
                        console.log(' geBenefitNameFromBenefit :: benefit name from Benefit' + benefit.benefitName);
                        return benefit.benefitName;
                    }
                }
                return '';
            }

            $scope.configuredCoverages = function () {
                var planCoverages = _.pluck($scope.plan.coverages, 'coverageId');
                var configuredCoverages = _.filter($scope.coverageList, function (coverage) {
                    return _.contains(planCoverages, coverage.coverageId);
                });
                return configuredCoverages;
            };

            $scope.editPlanCoverage = function (coverage) {
                $scope.showCoverageForm(coverage, true);
            };

            $scope.getAllBenefits = function () {
                var benefits = [];
                var i = 0;
                for (i = 0; i < $scope.plan.coverages.length; i++) {
                    var coverage = $scope.plan.coverages[i];
                    var j = 0;
                    if (angular.isDefined(coverage.planCoverageBenefits)) {
                        for (; j < coverage.planCoverageBenefits.length; j++) {
                            console.log(" getAllBenefits :: adding " + JSON.stringify(coverage.planCoverageBenefits[j]));
                            benefits.push(coverage.planCoverageBenefits[j]);
                        }
                    }
                }
                return benefits;
            };

            $scope.populateBenefits = function (selectedCoverage) {
                var coverage = _.where($scope.coverageList, {coverageId: selectedCoverage.coverageId});
                if (coverage && coverage.length > 0) {
                    $scope.benefits = coverage[0].benefitDtos;
                }
            };


            $scope.newPlanCoverageBenefit = {};

            $scope.addBenefit = function (selectedCoverage) {
                var planCoverage = _.find($scope.plan.coverages, function (planCoverage) {
                    console.log(JSON.stringify(planCoverage));
                    return planCoverage.coverageId == selectedCoverage.coverageId;
                });
                if (angular.isUndefined(planCoverage.planCoverageBenefits)) {
                    planCoverage.planCoverageBenefits = [];
                }
                planCoverage.planCoverageBenefits.push($scope.newPlanCoverageBenefit);
                $scope.newPlanCoverageBenefit = angular.copy({});
            };

            $scope.removeBenefit = function (index) {
                $scope.selectedCoverage.planCoverageBenefits.splice(index, 1);
            };


            $scope.createPlan = function () {
                $http.post(angular.isUndefined($scope.plan.planId) ? '/pla/core/plan/create' : '/pla/core/plan/update', $scope.plan).
                    success(function (data, status, headers, config) {
                        $scope.plan.planId = data.id;
                        $location.path('/plan#/viewplan/' + data.id);
                    }).
                    error(function (data, status, headers, config) {
                    });
            };


            $scope.datePickerSettings = {
                isOpened: false,
                dateOptions: {
                    formatYear: 'yyyy',
                    startingDay: 1
                }
            };
            $scope.openLaunchDate = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchDateOpen = true;

            };
            $scope.openWithdrawalDate = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.withdrawalDateOpen = true;

            };
            $scope.launchDt = $scope.plan.planDetail.launchDate;
            $scope.withdrawalDt = $scope.plan.planDetail.withdrawalDate;
            $scope.$watch('launchDt', function (newVal) {
                if (!angular.isUndefined(newVal)) {
                    $scope.plan.planDetail.launchDate = moment(newVal).format('DD/MM/YYYY');
                }
            });
            $scope.$watch('withdrawalDt', function (newVal) {
                if (!angular.isUndefined(newVal)) {
                    $scope.plan.planDetail.withdrawalDate = moment(newVal).format('DD/MM/YYYY');
                }
            });

            $scope.isValid = function (formField) {
                return formField.$dirty && formField.$invalid
            }

            $scope.$on('finished.fu.wizard', function (name, event, data) {
                $scope.createPlan();
            });

            $scope.$on('actionclicked.fu.wizard', function (name, event, data) {
                console.log('Step:: ' + data.step);
            });
        }]
);
app.filter('getTrustedUrl', ['$sce', function ($sce) {
    return function (url) {
        return $sce.getTrustedResourceUrl(url);
    }
}]);
