'use strict';
var app = angular.module('planSetup', ['common','ngTagsInput','checklist-model', 'ngRoute']);

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
                        planDetail: {},
                        policyTerm: {},
                        premiumTerm: {},
                        sumAssured: {},
                        coverages: []
                    }
                }
            }
        })
        .when('/viewplan/:planid', {
            templateUrl: "plan/viewplan/:planid",
            controller: 'PlanSetupController',
            resolve: {
                plan: ['$q', '$route', '$http', function ($q, $route, $http) {
                    var deferred = $q.defer();
                    console.log($route.current.params.planid);
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
                }]
            }
        });
    $routeProvider.otherwise({redirectTo: '/plan'});
});
app.controller('PlanSetupController', ['$scope', '$http', '$routeParams', 'plan',
        function ($scope, $http, $routeParams, plan) {
            /*$scope.$on('$viewContentLoaded', function () {
                console.log('view content loaded ');
                $('#planSetUpWizard').wizard();
             });*/
            $scope.plan = plan;
            $scope.steps = [{"title": "step-1"}, {"title": "step-2"}];
            $scope.currentStepIndex = 0;

            $scope.onlyNumbers = /^[1-9]+$/;
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
                {val: 'DAUGHTER', desc: 'Self'},
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
            $scope.$watch('clientType', function (val, old) {
                if (!angular.isUndefined(val))
                    $scope.plan.planDetail.clientType = val;
            });
            $scope.coverageList = [{"coverageId": '1', coverageName: 'Coverage 1'},
                {"coverageId": '2', coverageName: 'Coverage 2'},
                {"coverageId": '3', coverageName: 'Coverage 3'},
                {"coverageId": '4', coverageName: 'Coverage 4'},
                {"coverageId": '5', coverageName: 'Coverage 5'}];

            $scope.selectedItem =  function(iii){
                console.log(iii);
            }

            $scope.isPaymentTermByValue = function () {
                return $scope.plan.premiumTermType == 'SPECIFIED_VALUES';
            };

            $scope.isPolicyTermByValue = function () {
                var termType = $scope.plan.policyTermType || 'unknown';
                return termType == 'SPECIFIED_VALUES';
            };

            $scope.isSumAssuredTypeRange = function () {
                return $scope.plan.sumAssured.sumAssuredType == 'RANGE';
            };

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

            $scope.newCoverage = {
                coverage: undefined,
                coverageId: "",
                coverageCover: "",
                coverageType: "",
                deductibleType: "",
                waitingPeriod: "",
                minAge: "",
                maxAge: "",
                taxApplicable: "",
                sumAssured: {},
                coverageTerm: {},
                maturityAmounts: [],
                planCoverageBenefits: []
            };

            $scope.addCoverage = function () {
                var newCoverageStr;
                $scope.plan.coverages.push($scope.newCoverage);
                $scope.newCoverage = {
                    coverage: undefined,
                    coverageId: '',
                    coverageCover: '',
                    coverageType: '',
                    deductibleType: "",
                    waitingPeriod: "",
                    minAge: '',
                    maxAge: '',
                    taxApplicable: "",
                    sumAssured: {},
                    coverageTerm: {},
                    coverageTermType: "",
                    maturityAmounts: [],
                    planCoverageBenefits: []
                };
                $scope.coverageSumAssured = [];
                $scope.coverageTerm = [];
                $scope.coverageMaturityAge = [];
            };

            $scope.removeCoverage = function (idx) {
                $scope.plan.coverages.splice(idx, 1);
            };

            $scope.isCoverageSumAssuredDisabled = function (type) {
                return $scope.newCoverage.coverageSumAssuredType != type;
            };

            $scope.isCoverageTermDisabled = function (type) {
                return $scope.newCoverage.coverageTermType != type;
            };

            $scope.addMaturityRow = function () {
                $scope.newCoverage.maturityAmounts.push({});
            };

            $scope.removeMaturityRow = function (index) {
                $scope.newCoverage.maturityAmounts.splice(index, 1);
            };

            $scope.$watch('planSumAssuredType', function (newval) {
                if (!angular.isUndefined(newval)) {
                    $scope.planSumAssured = [];
                    $scope.plan.sumAssured.sumAssuredValue = [];
                }
            });

            $scope.$watch('newCoverage.coverageSumAssuredType', function (newval) {
                if (!angular.isUndefined(newval)) {
                    $scope.newCoverage.sumAssured.sumAssuredValue = [];
                    $scope.newCoverage.maxMaturityAge = '';
                    $scope.newCoverage.sumAssured.sumAssuredType = newval;
                }
                $scope.coverageSumAssured = [];
            });

            $scope.$watch('newCoverage.coverage', function (newval) {
                if (newval && !angular.isUndefined(newval.coverageId)) {
                    $scope.newCoverage.coverageId = newval.coverageId;
                }
            });

            $scope.editPlanCoverage = function (coverage) {
                alert('editingPlanCoverage' + coverage);
                $scope.newCoverage = coverage;
            }

            $scope.selectedCoverage = {};
            $scope.benefits = [{benefitName: " Accidental Benefit 1", benefitId: 1},
                {benefitName: " Accidental Benefit 2", benefitId: 2}];

            $scope.newPlanCoverageBenefit = {};
            $scope.addBenefit = function () {
                $scope.selectedCoverage.planCoverageBenefits.push($scope.newPlanCoverageBenefit);
                $scope.newPlanCoverageBenefit = {};
            };

            $scope.removeBenefit = function (index) {
                $scope.selectedCoverage.planCoverageBenefits.splice(index, 1);
            };


            $scope.createPlan = function () {
                console.log($scope.planSetupForm.$invalid);
                $http.post(angular.isUndefined($scope.plan.planId.planId) ? '/pla/core/plan/create' : '/pla/core/plan/update', $scope.plan).
                    success(function (data, status, headers, config) {
                        $scope.plan.planId = data.id;
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
                    $scope.plan.planDetail.launchDate = moment(newVal).format('YYYY-MM-DD');
                }
            });
            $scope.$watch('withdrawalDt', function (newVal) {
                if (!angular.isUndefined(newVal)) {
                    $scope.plan.planDetail.withdrawalDate = moment(newVal).format('YYYY-MM-DD');
                }
            });


            $scope.getAllBenefits = function () {
                var benefitList = [];
                var i = 0;
                for (; i < $scope.plan.coverages.length; i++) {
                    var j = 0;
                    for (j = 0; j < $scope.plan.coverages[i].planCoverageBenefits.length; j++) {
                        benefitList.push($scope.plan.coverages[i].planCoverageBenefits[j]);
                    }
                }
                return benefitList;
            }

            $scope.isValid = function (formField) {
                return formField.$dirty && formField.$invalid
            }

            $scope.$on('finished.fu.wizard', function (name, event, data) {
                console.log('Completed');
                $scope.createPlan();
            });

            $scope.$on('actionclicked.fu.wizard', function (name, event, data) {
                console.log('Step:: ' + data.step);
            });
        }]
);