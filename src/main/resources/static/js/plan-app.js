'use strict';
var app = angular.module('planSetup', ['common', 'ngTagsInput', 'checklist-model', 'ngRoute','mgcrea.ngStrap.select','ngSanitize','mgcrea.ngStrap']);

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
        .when('/viewplan/:planid', {
            controller: 'PlanViewController',
            templateUrl: 'plan/viewplan',
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
        })
        .when('/newplan', {
            templateUrl: "plan/newplan",
            controller: 'PlanSetupController',
            resolve: {
                plan: function () {
                    return {
                        "planDetail": {
                            freeLookPeriod: 15
                        },
                        "policyTermType": "SPECIFIED_VALUES",
                        "premiumTermType": "SPECIFIED_VALUES",
                        "policyTerm": {groupTerm: 365},
                        "premiumTerm": {},
                        "sumAssured": {},
                        "coverages": []
                    };
                },
                endorsementTypes: ['$q', '$http', function ($q, $http) {
                    var deferred = $q.defer();
                    $http.get('/pla/core/plan/getAllEndorsements').success(function (response, status, headers, config) {
                        deferred.resolve(response);
                    }).error(function (response, status, headers, config) {
                        deferred.reject();
                    });
                    return deferred.promise;
                    ;
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
        })
        .when('/editplan/:planid', {
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
                endorsementTypes: ['$q', '$http', function ($q, $http) {
                    var deferred = $q.defer();
                    $http.get('/pla/core/plan/getAllEndorsements').success(function (response, status, headers, config) {
                        deferred.resolve(response);
                    }).error(function (response, status, headers, config) {
                        deferred.reject();
                    });
                    return deferred.promise;
                    ;
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
app.controller('PlanListController', ['$scope', 'planList', function ($scope, planList) {
    $scope.planList = planList;
}
]);

app.directive('sumassuredCheck', function () {
    return {
        // restrict to an attribute type.
        restrict: 'A',
        // element must have ng-model attribute.
        require: 'ngModel',
        link: function (scope, ele, attrs, ctrl) {
            var multipleSelected = null;
            ctrl.$parsers.unshift(function (value) {
                var sumAssured = scope.$eval('plan.sumAssured');
                if (value && sumAssured) {
                    multipleSelected = parseInt(value);
                    var sumAssuredAmt = parseInt(sumAssured.minSumInsured) + parseInt(value);
                    var valid = sumAssuredAmt <= parseInt(sumAssured.maxSumInsured);
                    ctrl.$setValidity('invalidMultiple', valid);
                }
                return valid ? value : undefined;
            });

            scope.$watchGroup(['plan.sumAssured.maxSumInsured', 'plan.sumAssured.minSumInsured'], function (newval) {
                var sumAssured = scope.$eval('plan.sumAssured');
                if (multipleSelected == null) {
                    multipleSelected = sumAssured.multiplesOf;
                }
                var sumAssuredAmt = parseInt(sumAssured.minSumInsured) + parseInt(multipleSelected);
                var valid = sumAssuredAmt <= parseInt(sumAssured.maxSumInsured);
                ctrl.$setValidity('invalidMultiple', valid);
            });


        }
    }
});

app.directive('validateTerm', function () {
    return {
        // restrict to an attribute type.
        restrict: 'A',
        require: 'ngModel',
        link: function (scope, ele, attrs, ctrl) {
            scope.$watch('newCoverage.coverageTerm.maxMaturityAge', function (newval, oldval) {
                if (newval == oldval)return;
                var maxMaturityAge = 0;
                var valid = true;
                var policyTermType = scope.$eval('plan.policyTermType');
                if (policyTermType == 'MATURITY_AGE_DEPENDENT') {
                    console.log(JSON.stringify(scope.$eval('plan.policyTerm')));
                    var maturityAges = scope.$eval('plan.policyTerm.maturityAges');
                    maturityAges = _.sortBy(maturityAges, 'text');
                    maxMaturityAge = maturityAges[maturityAges.length - 1].text;
                    console.log(maxMaturityAge);
                    valid = newval <= maxMaturityAge;
                    console.log(valid);
                } else {
                    //maxMaturityAge = scope.$eval('plan.policyTerm.maxMaturityAge');
                    maxMaturityAge = scope.$eval('plan.planDetail.maxEntryAge');
                    valid = newval <= maxMaturityAge +1 ;
                }
                ctrl.$setValidity('max', valid);
            });
        }
    }
});

app.directive('coverageCheck', function () {
    return {
        // restrict to an attribute type.
        restrict: 'A',
        // element must have ng-model attribute.
        require: 'ngModel',
        link: function (scope, ele, attrs, ctrl) {
            var multipleSelected = null;
            ctrl.$parsers.unshift(function (value) {
                var sumAssured = scope.$eval('newCoverage.coverageSumAssured');
                if (value && sumAssured) {
                    multipleSelected = parseInt(value);
                    var sumAssuredAmt = parseInt(sumAssured.minSumInsured) + parseInt(value);
                    var valid = sumAssuredAmt <= parseInt(sumAssured.maxSumInsured);
                    ctrl.$setValidity('invalidMultiple', valid);
                }
                return valid ? value : undefined;
            });

            scope.$watchGroup(['newCoverage.coverageSumAssured.maxSumInsured', 'newCoverage.coverageSumAssured.minSumInsured'], function (newval) {

                var sumAssured = scope.$eval('newCoverage.coverageSumAssured');
                var sumAssuredAmt = parseInt(sumAssured.minSumInsured) + parseInt(sumAssured.multiplesOf);
                var valid = sumAssuredAmt <= parseInt(sumAssured.maxSumInsured);
                ctrl.$setValidity('invalidMultiple', valid);
            });


        }
    }
});

app.controller('PlanViewController', ['$scope', 'plan', 'activeCoverages',
    function ($scope, plan, activeCoverages) {
        $scope.plan = plan;

        $scope.coverages = activeCoverages;
        $scope.getCoverageName = function (coverageId) {
            var coverage = _.findWhere($scope.coverages, {coverageId: coverageId});
            if (coverage) {
                return coverage.coverageName;
            }
            return '';
        };


        $scope.getBenefitName = function (coverageId, benefitId) {
            var coverage = _.findWhere($scope.coverages, {coverageId: coverageId});
            if (coverage) {
                var benefit = _.findWhere(coverage.benefitDtos, {benefitId: benefitId});
                if (benefit) {
                    return benefit.benefitName;
                }
            }
            return '';
        };

        var relations = [
            {val: 'BROTHER', desc: 'Brother'},
            {val: 'DAUGHTER', desc: 'Daughter'},
            {val: 'DEPENDENTS', desc: 'Dependents'},
            {val: 'FATHER', desc: 'Father'},
            {val: 'FATHER_IN_LAW', desc: 'Father-in-law'},
            {val: 'MOTHER', desc: 'Mother'},
            {val: 'MOTHER_IN_LAW', desc: 'Mother-in-law'},
            {val: 'SELF', desc: 'Self'},
            {val: 'SISTER', desc: 'Sister'},
            {val: 'SON', desc: 'Son'},
            {val: 'SPOUSE', desc: 'Spouse'},
            {val: 'STEP_DAUGHTER', desc: 'Step Daughter'},
            {val: 'STEP_SON', desc: 'Step Son'}];

        $scope.resolveRelationship = function (relation) {
            return _.findWhere(relations, {val: relation}).desc;
        }
    }
])
app.controller('PlanSetupController', ['$scope', '$http', '$location', '$routeParams', '$templateCache', '$bsmodal', '$log', 'plan', 'activeCoverages', 'endorsementTypes',
        function ($scope, $http, $location, $routeParams, $templateCache, $modal, $log, plan, activeCoverages, endorsementTypes) {

            $scope.plan = plan;
            $scope.coverageList = activeCoverages;
            $scope.steps = [{"title": "step-1"}, {"title": "step-2"}];
            $scope.currentStepIndex = 0;

            $scope.expiryDate = null;
            $scope.onlyNumbers = /^[0-9]+$/;

            $scope.minDate = new Date();
            $scope.productName;

            $scope.planSetUpForm = {};
            $scope.relations = [
                {val: 'BROTHER', desc: 'Brother'},
                {val: 'DAUGHTER', desc: 'Daughter'},
                {val: 'DEPENDENTS', desc: 'Dependents'},
                {val: 'FATHER', desc: 'Father'},
                {val: 'FATHER_IN_LAW', desc: 'Father-in-law'},
                {val: 'MOTHER', desc: 'Mother'},
                {val: 'MOTHER_IN_LAW', desc: 'Mother-in-law'},
                {val: 'SELF', desc: 'Self'},
                {val: 'SISTER', desc: 'Sister'},
                {val: 'SON', desc: 'Son'},
                {val: 'SPOUSE', desc: 'Spouse'},
                {val: 'STEP_DAUGHTER', desc: 'Step Daughter'},
                {val: 'STEP_SON', desc: 'Step Son'}];

/*
            $scope.premiumTermTypeList = [{
                premiumTermType: "REGULAR",
                description: "Regular"
            }, {premiumTermType: "SINGLE", description: "Single"}, {
                premiumTermType: "SPECIFIED_VALUES",
                description: "Specified Values"
            }, {premiumTermType: "SPECIFIED_AGES", description: "Specified Ages"}];
*/



            $scope.initEndorsements = function () {
                var list = _.filter(endorsementTypes, function (each) {
                    return each['category'] == 'GROUP';
                });
                $scope.individualEndorsementTypes = _.pluck(list, "description");

                list = _.filter(endorsementTypes, function (each) {
                    return each['category'] == 'INDIVIDUAL';
                });
                $scope.groupEndorsementTypes = _.pluck(list, "description");
            }

            $scope.initEndorsements();

            $scope.sumAssuredTypesOriginal = [{val: 'RANGE', desc: 'Specified Range'}, {
                val: 'SPECIFIED_VALUES',
                desc: 'Specified Values'
            }];
            $scope.sumAssuredTypes = [];

            $scope.resetPlanSumAssured = function () {
                $scope.plan.sumAssured.sumAssuredValue = [];
                $scope.plan.sumAssured.minSumInsured = null;
                $scope.plan.sumAssured.maxSumInsured = null;
                $scope.plan.sumAssured.multiplesOf = null;
            };

            $scope.clientType = $scope.plan.planDetail.clientType;

            $scope.$watch('plan.planDetail.lineOfBusinessId', function (newval, oldval) {
                if (newval != undefined) {
                    if (newval != 'INDIVIDUAL_LIFE') {
                        $scope.clientType = 'GROUP';
                    } else {
                        $scope.clientType = 'INDIVIDUAL';
                    }
                }
            });

            $scope.$watch('clientType', function (val, old) {
                if (!angular.isUndefined(val)) {
                    $scope.plan.planDetail.clientType = val;

                    if (val != old) {
                        $scope.plan.sumAssured = {sumAssuredType: ""};
                    }

                    if ($scope.clientType == 'GROUP') {
                        $scope.plan.premiumTermType = 'REGULAR';
                        $scope.sumAssuredTypes = angular.copy($scope.sumAssuredTypesOriginal);
                        $scope.sumAssuredTypes.push({val: 'INCOME_MULTIPLIER', desc: 'Income Multiplier'});
                        $scope.premiumPaymentTermReadonly = true;

                    } else {
                        $scope.sumAssuredTypes = angular.copy($scope.sumAssuredTypesOriginal);
                        $scope.premiumPaymentTermReadonly = false;
                    }
                }
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

            $scope.newCoverage = {maturityAmounts: [], coverageTerm: {}};
            $scope.emptyCoverage = {maturityAmounts: []};

            //var myOtherModal = $modal({scope: $scope, template: '/pla/plan/coverage-form.html', show: false});
            $scope.showCoverageForm = function (coverage, isEditing, $event) {
                if ($scope.currentStep == 5) {
                    $modal.open({
                        templateUrl: '/pla/core/plan/coverage-form.html',
                        backdrop: true,
                        windowClass: 'modal',
                        controller: function ($scope, $modalInstance, $log, newCoverage, plan, coverageList, isEditing, stepForm, benefitList) {
                            $scope.plan = plan;
                            $scope.coverageList = coverageList;
                            $scope.newCoverage = newCoverage;
                            $scope.editFlag = isEditing;
                            $scope.stepForm = stepForm;

                            $scope.$emit('COVERAGE_ADDED', {});

                            $scope.cancel = function () {
                                $modalInstance.dismiss('cancel');
                            };

                            $scope.addMaturityRow = function () {
                                if ($scope.newCoverage && !$scope.newCoverage.maturityAmounts) {
                                    console.log('newCoverage *** ');
                                    $scope.newCoverage.maturityAmounts = [];
                                }
                                $scope.newCoverage.maturityAmounts.push({});
                            };

                            $scope.removeMaturityRow = function (index) {
                                $scope.newCoverage.maturityAmounts.splice(index, 1);
                            };

                            $scope.addCoverage = function (newCoverage) {
                                $scope.plan.coverages.push(newCoverage);
                                $scope.$emit('COVERAGE_ADDED', {test: 'test'});
                                $scope.$broadcast('COVERAGE_ADDED', {test: 'test'});
                                console.log('newCoverage ' + JSON.stringify(newCoverage));
                                console.log('benefitList ' + benefitList.length);
                                $scope.newCoverage = angular.copy($scope.emptyCoverage);
                                $scope.coverageForm.$setValidity();
                                $scope.coverageSumAssured = [];
                                $scope.coverageTerm = [];
                                $scope.coverageMaturityAge = [];
                                $scope.stepForm.hasError = false;
                                $scope.cancel();

                            };

                            /**
                             *
                             * Reset the coverage sum assured as the Sum Assured Changes.
                             */
                            $scope.resetPlanCoverageSumAssured = function () {
                                $scope.newCoverage.coverageSumAssured.sumAssuredValue = [];
                                $scope.newCoverage.coverageSumAssured.percentage = null;
                                $scope.newCoverage.coverageSumAssured.maxLimit = null;
                                $scope.newCoverage.coverageSumAssured.minSumInsured = null;
                                $scope.newCoverage.coverageSumAssured.maxSumInsured = null;
                                $scope.newCoverage.coverageSumAssured.multiplesOf = null;

                            }

                            /**
                             *
                             * Reset the coverage coverageTerm as the Sum Assured Changes.
                             */
                            $scope.resetPlanCoverageTerm = function () {
                                if ($scope.newCoverage.coverageTerm) {
                                    $scope.newCoverage.coverageTerm.validTerms = [];
                                    $scope.newCoverage.coverageTerm.maturityAges = [];
                                }

                            }
                            $scope.removeCoverage = function (idx) {
                                if ($scope.plan.coverages)
                                    $scope.plan.coverages.splice(idx, 1);
                            };

                        },
                        resolve: {
                            newCoverage: function () {
                                return angular.isUndefined(coverage) ? {
                                    maturityAmounts: [],
                                    coverageTerm: {}
                                } : coverage;
                            },
                            coverageList: function () {
                                if (isEditing)
                                    return $scope.coverageList;
                                else {
                                    var listOfCoverageIds = _.pluck($scope.plan.coverages, "coverageId");
                                    var unUsedCoverageList = _.reject($scope.coverageList, function (coverage) {
                                        return _.contains(listOfCoverageIds, coverage.coverageId);
                                    });
                                    return unUsedCoverageList;
                                }
                            },
                            plan: function () {
                                return $scope.plan;
                            },
                            isEditing: function () {
                                return isEditing;
                            },
                            stepForm: function () {
                                return $scope.step5;
                            },
                            benefitList: function () {
                                return $scope.benefits;
                            }
                        }
                    });
                }
            };


            $scope.$watchCollection('plan.coverages', function (newCollection, oldCollection) {

                var addEvent = newCollection.length > oldCollection.length;
                var difference = _.difference(newCollection, oldCollection);
                console.log(addEvent + ' difference ' + JSON.stringify(difference));
                if (difference.length == 1 && addEvent) {
                    var coverage = $scope.resolveCoverage(difference[0].coverageId);
                    angular.forEach(coverage.benefitDtos, function (benefit) {
                        $scope.benefits.unshift({
                            coverageId: coverage.coverageId,
                            "coverageName": coverage.coverageName,
                            "benefitId": benefit.benefitId,
                            "benefitName": benefit.benefitName,
                            "definedPer": null,
                            "coverageBenefitType": null,
                            "benefitLimit": null,
                            "maxLimit": null
                        });
                    });
                } else {
                    console.log('remove event ');
                    var difference = _.difference(oldCollection, newCollection);
                    console.log(addEvent + ' difference ' + JSON.stringify(difference));
                    if (difference.length == 1) {
                        $scope.benefits = _.reject($scope.benefits, function (each) {
                            return each.coverageId == difference[0].coverageId;
                        });
                    }
                }
            });

            /**
             * This is used to nullify the planSumAssured on toggling the
             * Plan Sum Assured Type.
             *
             */
            $scope.$watch('planSumAssuredType', function (newval) {
                if (!angular.isUndefined(newval)) {
                    $scope.planSumAssured = [];
                    $scope.plan.sumAssured.sumAssuredValue = [];
                }
            });

            /**
             * Used for displaying the coverages in List of Coverages.
             * Plan only stores the coverageId. The coverageId is then searched
             * against the Coverage List that is injected in to the Controller.
             *
             * @param coverageId
             */
            $scope.resolveCoverage = function (coverageId) {
                var coverage = _.findWhere($scope.coverageList, {'coverageId': coverageId});
                return coverage;
            };

            /**
             * Open the Coverage Modal Form for editing the selected Plan Coverage details.
             *
             * @param coverage
             */
            $scope.editPlanCoverage = function (coverage) {
                $scope.showCoverageForm(coverage, true);
            };

            /**
             * Remove the coverage from the plan.
             *
             * @param coverage
             */
            $scope.removePlanCoverage = function (index) {
                $modal.open({
                    backdrop: true,
                    windowClass: 'modal',
                    size: 'sm',
                    templateUrl: 'coverageConfirmation.html',
                    controller: function ($scope, $modalInstance, $log, plan) {
                        $scope.plan = plan;

                        $scope.cancel = function () {
                            $modalInstance.dismiss('cancel');
                        };
                        $scope.ok = function () {
                            if ($scope.plan.coverages)
                                $scope.plan.coverages.splice(index, 1);
                            $scope.cancel();
                        };
                    },
                    resolve: {
                        plan: function () {
                            return $scope.plan;
                        }
                    }
                });
            };

            /**
             * To display the list of benefits attached to the Plan.
             *
             * @returns {Array}
             */
            $scope.getAllBenefits = function () {
                $scope.benefits = [];
                if ($scope.plan.planId) {
                    var i = 0;
                    for (i = 0; i < $scope.plan.coverages.length; i++) {
                        var coverage = $scope.plan.coverages[i];
                        var j = 0;
                        if (angular.isDefined(coverage.planCoverageBenefits)) {
                            for (; j < coverage.planCoverageBenefits.length; j++) {
                                $scope.benefits.push(coverage.planCoverageBenefits[j]);
                            }
                        }
                    }

                } else {
                    angular.forEach($scope.plan.coverages, function (selectedCoverage) {
                        var coverage = $scope.resolveCoverage(selectedCoverage.coverageId);
                        angular.forEach(coverage.benefitDtos, function (benefit) {
                            $scope.benefits.push({
                                coverageId: coverage.coverageId,
                                "coverageName": coverage.coverageName,
                                "benefitId": benefit.benefitId,
                                "benefitName": benefit.benefitName,
                                "definedPer": null,
                                "coverageBenefitType": null,
                                "benefitLimit": null,
                                "maxLimit": null
                            });
                        });
                    });
                }
            };
            $scope.getAllBenefits();

            $scope.$on('COVERAGE_ADDED', function (event, data) {
                console.log(JSON.stringify(data));
            });

            $scope.createPlan = function () {
                $scope.validationFailed = false;
                angular.extend($scope.plan, {planCoverageBenefits: $scope.benefits})
                $http.post(angular.isUndefined($scope.plan.planId) ? '/pla/core/plan/create' : '/pla/core/plan/update', $scope.plan).
                    success(function (data, status, headers, config) {
                        $scope.plan.planId = data.id;
                        $location.path('/plan');
                        $scope.successMsg = data.message;
                        $templateCache.remove('plan/list');
                    }).
                    error(function (data, status, headers, config) {
                        $scope.validationFailed = true;
                        $scope.serverErrMsg = data.message;
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

            $scope.isValid = function (formField) {
                return formField.$dirty && formField.$invalid
            }

            $scope.step5 = {};
            $scope.currentStep = 1;


            $scope.$on('changed.fu.wizard', function (name, sevent, data) {
                $scope.currentStep = data.step;
                console.log('changed.fu.wizard' + $scope.currentStep);
            });

            $scope.$on('actionclicked.fu.wizard', function (name, event, data) {
                if (data.step == 5) {
                    if ($scope.plan.coverages.length == 0) {
                        $scope.step5.hasError = true;
                        $scope.stepErrorMsg = "Configure coverages.";
                        $scope.$apply();
                        event.preventDefault();
                    }
                }
            });


            $scope.$on('finished.fu.wizard', function (name, event, data) {
                if ($scope.step6.$invalid) {
                    $scope.step6_errorMsg = "Configure Benefits.";
                    $scope.$digest();
                    event.preventDefault();
                } else {
                    $scope.createPlan();
                }
            });


            $scope.camelize = function (str) {
                return str.replace(/(?:^\w|[A-Z]|\b\w)/g, function (letter, index) {
                    return index == 0 ? letter.toLowerCase() : letter.toUpperCase();
                }).replace(/\s+/g, '');
            }

        }]
);
app.filter('resolveEnum', function () {
    return function (input) {
        input = input || '';
        var out = "";
        if ('SPECIFIED_VALUES' == input) {
            out = 'Specified Values';
        } else if ('RANGE' === input) {
            out = 'Specified Range';
        } else if ('DERIVED' === input) {
            out = '% of the Plan Sum Assured';
        }
        return out;
    };
});
app.filter('getTrustedUrl', ['$sce', function ($sce) {
    return function (url) {
        return $sce.getTrustedResourceUrl(url);
    }
}]);



