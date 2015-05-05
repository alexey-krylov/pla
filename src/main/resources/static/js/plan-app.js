'use strict';
var app = angular.module('planSetup', ['common', 'ngTagsInput', 'checklist-model', 'ngRoute']);

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
                            launchDate: moment().format('DD/MM/YYYY')
                        },
                        "policyTermType": "SPECIFIED_VALUES",
                        "premiumTermType": "SPECIFIED_VALUES",
                        "policyTerm": {groupTerm: 365},
                        "premiumTerm": {},
                        "sumAssured": {},
                        "coverages": []
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
app.controller('PlanListController', ['$scope', 'planList', function ($scope, planList) {
    $scope.planList = planList;
}
]);
app.controller('PlanSetupController', ['$scope', '$http', '$location', '$routeParams', '$templateCache', '$bsmodal', '$log', 'plan', 'activeCoverages',
        function ($scope, $http, $location, $routeParams, $templateCache, $modal, $log, plan, activeCoverages) {

            $scope.plan = plan;
            $scope.coverageList = activeCoverages;
            $scope.steps = [{"title": "step-1"}, {"title": "step-2"}];
            $scope.currentStepIndex = 0;

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
            $scope.endorsementTypes = [
                {val: 'IND_CHANGE_NAME', desc: 'Correction of Name', clientType: 'INDIVIDUAL'},
                {val: 'IND_CHANGE_ADDRESS', desc: 'Change of Address', category: 'INDIVIDUAL'},
                {val: 'IND_CHANGE_BENEFICIARY', desc: 'Change/Add Beneficiary', category: 'INDIVIDUAL'},
                {val: 'IND_CHANGE_PAYMENT_METHOD', desc: 'Change method of Payment', category: 'INDIVIDUAL'},
                {val: 'IND_CHANGE_AGENT', desc: 'Change Agent', category: 'INDIVIDUAL'},
                {val: 'IND_CHANGE_PAYER', desc: 'Change Payer', category: 'INDIVIDUAL'},
                {val: 'IND_CHANGE_SUM_ASSURED', desc: 'Change Sum Assured', category: 'INDIVIDUAL'},
                {val: 'IND_CHANGE_DOB', desc: 'Change Life Assured Date of Birth', category: 'INDIVIDUAL'},
                {val: 'GRP_MEMBER_ADDITION', desc: 'Member Addition', category: 'GROUP'},
                {val: 'GRP_MEMBER_DELETION', desc: 'Member Deletion', category: 'GROUP'},
                {val: 'GRP_NEW_COVER', desc: 'Introduction of New Cover', category: 'GROUP'},
                {val: 'GRP_PROMOTION', desc: 'Promotion', category: 'GROUP'}
            ];


            $scope.sumAssuredTypesOriginal = [{val: 'RANGE', desc: 'Specified Range'}, {val: 'SPECIFIED_VALUES', desc: 'Specified Values'}];
            $scope.sumAssuredTypes = [];

            $scope.resetPlanSumAssured = function () {
                $scope.plan.sumAssured.sumAssuredValue = [];
                $scope.plan.sumAssured.minSumInsured = null;
                $scope.plan.sumAssured.maxSumInsured = null;
                $scope.plan.sumAssured.multiplesOf = null;

            }


            $scope.clientType = $scope.plan.planDetail.clientType;

            $scope.$watch('clientType', function (val, old) {
                if (!angular.isUndefined(val)) {
                    $scope.plan.planDetail.clientType = val;
                    if ($scope.clientType == 'GROUP') {
                        $scope.sumAssuredTypes = angular.copy($scope.sumAssuredTypesOriginal);
                        $scope.sumAssuredTypes.push({val: 'INCOME_MULTIPLIER', desc: 'Income Multiplier'});
                    } else {
                        $scope.sumAssuredTypes = angular.copy($scope.sumAssuredTypesOriginal);
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

            $scope.showCoverageForm = function (coverage, isEditing) {
                $modal.open({
                    templateUrl: '/pla/core/plan/coverage-form.html',
                    backdrop: true,
                    windowClass: 'modal',
                    controller: function ($scope, $modalInstance, $log, newCoverage, plan, coverageList, isEditing, stepForm) {
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
                            $scope.stepForm.$error = false;
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
                            if (newCoverage.coverageTerm) {
                                $scope.newCoverage.coverageTerm.maturityAges = [];
                                $scope.newCoverage.coverageTerm.validTerms = [];
                                $scope.newCoverage.coverageTerm.maxMaturityAge = [];
                                $scope.newCoverage.coverageTerm.maturityAges = [];
                                $scope.newCoverage.coverageTerm.maturityAges = [];
                            }

                        }

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
                            return isEditing;
                        },
                        stepForm: function () {
                            return $scope.step5;
                        }
                    }
                });
            };

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
             * This returns the Benefit Name for displaying in the Existing Benefits table.
             * The BenefitId is only stored in the PlanCoverage Association. This benefitId
             * is then searched against the List of coverages that is injected.
             *
             * @param benefitId
             * @returns {*}
             */
            $scope.getCoverageNameFromBenefit = function (benefitId) {
                var i = 0;
                for (; i < $scope.plan.coverages.length; i++) {
                    var eachCoverage = $scope.plan.coverages[i];
                    var benefit = _.findWhere(eachCoverage.planCoverageBenefits, {'benefitId': benefitId});
                    if (benefit) {
                        var coverage = $scope.resolveCoverage(eachCoverage.coverageId);
                        return coverage.coverageName;
                    }
                }
                return '';
            }


            $scope.geBenefitNameFromBenefit = function (benefitId) {
                var i = 0;
                for (; i < $scope.coverageList.length; i++) {
                    var eachCoverage = $scope.coverageList[i];
                    var benefit = _.findWhere(eachCoverage.benefitDtos, {'benefitId': benefitId});
                    if (benefit) {
                        return benefit.benefitName;
                    }
                }
                return '';
            };

            /**
             * This returns the list of all the coverages that are attached
             * to the Plan. This is used for populating the Coverages drop
             * down in benefit section.
             *
             * @returns {*}
             */
            $scope.configuredCoverages = function () {
                var planCoverages = _.pluck($scope.plan.coverages, 'coverageId');
                var configuredCoverages = _.filter($scope.coverageList, function (coverage) {
                    return _.contains(planCoverages, coverage.coverageId);
                });
                return configuredCoverages;
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

                if ($scope.plan.coverages[index].planCoverageBenefits.length > 0) {
                    $modal.open({
                        backdrop: true,
                        windowClass: 'modal',
                        size: 'sm',
                        templateUrl: 'coverageAlert.html',
                        controller: function ($scope, $modalInstance) {
                            $scope.cancel = function () {
                                $modalInstance.dismiss('cancel');
                            };
                        }
                    });
                } else
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
                                $log.info('removing the coverage.');
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
                var benefits = [];
                var i = 0;
                for (i = 0; i < $scope.plan.coverages.length; i++) {
                    var coverage = $scope.plan.coverages[i];
                    var j = 0;
                    if (angular.isDefined(coverage.planCoverageBenefits)) {
                        for (; j < coverage.planCoverageBenefits.length; j++) {
                            benefits.push(coverage.planCoverageBenefits[j]);
                        }
                    }
                }
                return benefits;
            };


            /**
             * To populate the benefit drop down based on the selected coverage.
             *
             * @param selectedCoverage
             */
            $scope.populateBenefits = function (selectedCoverage) {
                var coverage = _.where($scope.coverageList, {coverageId: selectedCoverage.coverageId});
                if (coverage && coverage.length > 0) {
                    var benefits = coverage[0].benefitDtos;
                    /* var planCoverage= _.where($scope.plan.coverages,{coverageId:selectedCoverage.coverageId});
                     if (angular.isDefined(planCoverage.planCoverageBenefits)) {
                     var i=0;
                     for(;i<planCoverage.planCoverageBenefits.length;i++){
                     $log.info('Filtering benefits');
                     benefits= _.reject(benefits,{benefitId:planCoverage.planCoverageBenefits[i].benefitId});
                     }
                     }*/
                    $scope.benefits = benefits;
                }
            };

            /**
             * Remove the Benefits configured from the plan.
             *
             * @param coverage
             */
            $scope.removeBenefit = function (benefitId) {
                $modal.open({
                    backdrop: true,
                    windowClass: 'modal',
                    size: 'sm',
                    templateUrl: 'benefitConfirmation.html',
                    controller: function ($scope, $modalInstance, $log, plan) {
                        $scope.plan = plan;
                        $scope.cancel = function () {
                            $modalInstance.dismiss('cancel');
                        };
                        $scope.ok = function () {
                            $log.info(' removing the benefit');
                            angular.forEach($scope.plan.coverages, function (value, key) {
                                var i = 0;
                                for (; i < value.planCoverageBenefits.length; i++) {
                                    if (value.planCoverageBenefits[i].benefitId == benefitId) {
                                        value.planCoverageBenefits.splice(i, 1);
                                        break;
                                    }
                                }
                            });
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


            $scope.newPlanCoverageBenefit = {};

            $scope.addBenefit = function (selectedCoverage) {
                var planCoverage = _.find($scope.plan.coverages, function (planCoverage) {
                    return planCoverage.coverageId == selectedCoverage.coverageId;
                });
                if (angular.isUndefined(planCoverage.planCoverageBenefits)) {
                    planCoverage.planCoverageBenefits = [];
                }
                planCoverage.planCoverageBenefits.push($scope.newPlanCoverageBenefit);
                $scope.newPlanCoverageBenefit = angular.copy({benefitLimit: null, maxLimit: null});
                $scope.benefitForm.$setPristine();
                $scope.benefitForm.$setUntouched();
                $scope.coverageForm.$setValidity();
                $scope.step6.$error = false;
            };

            $scope.createPlan = function () {
                $scope.validationFailed = false;

                if ($scope.plan.planDetail.withdrawalDate == "Invalid date") {
                    $scope.plan.planDetail.withdrawalDate = null;
                }

                $http.post(angular.isUndefined($scope.plan.planId) ? '/pla/core/plan/create' : '/pla/core/plan/update', $scope.plan).
                    success(function (data, status, headers, config) {
                        $scope.$apply();
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
            $scope.launchDt = $scope.plan.planDetail.launchDate;
            $scope.withdrawalDt = $scope.plan.planDetail.withdrawalDate;
            $scope.$watch('launchDt', function (newVal) {
                if (!angular.isUndefined(newVal)) {
                    $scope.plan.planDetail.launchDate = moment(newVal).format('DD/MM/YYYY');
                }
            });
            $scope.$watch('withdrawalDt', function (newVal) {
                if (!angular.isUndefined(newVal) && newVal != "Invalid Date") {
                    $scope.plan.planDetail.withdrawalDate = moment(newVal).format('DD/MM/YYYY');
                }
            });

            $scope.isValid = function (formField) {
                return formField.$dirty && formField.$invalid
            }

            $scope.step5 = {};
            $scope.step6 = {};
            $scope.$on('actionclicked.fu.wizard', function (name, event, data) {
                if (data.step == 5) {
                    if ($scope.plan.coverages.length == 0) {
                        $scope.step5.$error = true;
                        $scope.stepErrorMsg = "Configure coverages.";
                        $scope.$apply();
                        event.preventDefault();
                    }
                }
            });

            $scope.$on('finished.fu.wizard', function (name, event, data) {
                if ($scope.getAllBenefits().length == 0) {
                    $scope.step6.$error = true;
                    $scope.step6_errorMsg = "Configure Benefits.";
                    $scope.$apply();
                    event.preventDefault();
                } else {
                    $scope.createPlan();
                }
            });
        }]
);
app.filter('getTrustedUrl', ['$sce', function ($sce) {
    return function (url) {
        return $sce.getTrustedResourceUrl(url);
    }
}]);



