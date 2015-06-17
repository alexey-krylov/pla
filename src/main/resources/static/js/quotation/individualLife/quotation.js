/**
 * Created by pradyumna on 26-05-2015.
 */
(function (angular) {
    "use strict";

    function calculateAge(dob) {
        var age = moment().diff(new moment(new Date(dob)), 'years') + 1;
        return age;
    }

    angular.module('individualQuotation', ['common', 'ngRoute', 'commonServices', 'ngMessages', 'angucomplete-alt'])
        .config(['datepickerPopupConfig', function (datepickerPopupConfig) {
        datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
        datepickerPopupConfig.currentText = 'Today';
        datepickerPopupConfig.clearText = 'Clear';
        datepickerPopupConfig.closeText = 'Done';
        datepickerPopupConfig.closeOnDateSelection = true;
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
                            var ageNextBirthday = calculateAge($scope.proposedAssured.dateOfBirth);
                                return _.filter($scope.plan.policyTerm.validTerms, function (term) {
                                    return ageNextBirthday + term.text <= maxMaturityAge;
                                });
                            } else if ($scope.plan.policyTermType === 'MATURITY_AGE_DEPENDENT') {
                                return _.filter($scope.plan.policyTerm.maturityAges, function (term) {
                                    return term > ageNextBirthday;
                                });
                            }
                        return [];
                    };
                }]
            };
        })
        .directive('coverageTerm', function () {
            return {
                restrict: 'E',
                templateUrl: 'plan-coverage.tpl',
                controller: ['$scope', function ($scope) {
                    $scope.policyTerms = [];
                    $scope.getCoverageTermType = function (riderDetail) {
                        if ($scope.plan) {
                            var coverage = _.findWhere($scope.plan.coverages, {coverageId: riderDetail.coverageId});
                            var ageNextBirthday = calculateAge($scope.proposedAssured.dateOfBirth);
                            if (coverage.coverageTermType === 'SPECIFIED_VALUES') {
                                var maxMaturityAge = coverage.coverageTerm.maxMaturityAge || 1000;
                                $scope.policyTerms = _.filter(coverage.coverageTerm.validTerms, function (term) {
                                    return ageNextBirthday + term.text <= maxMaturityAge;
                                });
                            } else if (coverage.coverageTermType === 'MATURITY_AGE_DEPENDENT') {
                                $scope.policyTerms = _.filter(coverage.coverageTerm.maturityAges, function (term) {
                                    return term > ageNextBirthday;
                                });
                            }
                            return coverage.coverageTermType;
                        } else {
                            return ""
                        }
                    }

                }]
            };
        })
        .directive('coverageSumassured', function () {
            return {
                restrict: 'E',
                templateUrl: 'coverage-sumassured.tpl',
                controller: ['$scope', function ($scope) {
                    $scope.getSumAssuredType = function (riderDetail) {
                        if ($scope.plan) {
                            $scope.coverage = _.findWhere($scope.plan.coverages, {coverageId: riderDetail.coverageId});
                            return $scope.coverage.coverageSumAssured.sumAssuredType;
                        }
                    }
                }]
            }
        })
        .directive('premiumterm', function () {
            return {
                restrict: 'E',
                templateUrl: 'plan-premiumterm.tpl',
                link: function (scope) {
                    scope.$watch('planDetailDto.policyTerm', function (newval) {
                        if (newval)
                        scope.planDetailDto.premiumPaymentTerm = newval;
                    })
                },
                controller: ['$scope', function ($scope) {
                    $scope.premiumTerm = function () {
                        if ($scope.plan.premiumTermType === 'SPECIFIED_VALUES') {
                            var maxMaturityAge = $scope.plan.premiumTermType.maxMaturityAge || 1000;
                            var ageNextBirthday = moment().diff($scope.proposedAssured.dateOfBirth.toDateString(), 'years') + 1;
                            return _.filter($scope.plan.premiumTermType.validTerms, function (term) {
                                return ageNextBirthday + term <= maxMaturityAge;
                            });
                        } else if ($scope.plan.premiumTermType === 'SPECIFIED_AGES') {
                            var ageNextBirthday = moment().diff($scope.proposedAssured.dateOfBirth.toDateString(), 'years') + 1;
                            return _.filter($scope.plan.premiumTermType.maturityAges, function (term) {
                                return term > ageNextBirthday;
                            });
                        } else if ($scope.plan.premiumTermType === 'REGULAR') {
                            $scope.$on('planDetailDto.policyTerm', function (newval) {
                                $scope.planDetailDto.premiumPaymentTerm = newval;
                            });
                        }
                    };
                }]
            };
        })
        .controller('QuotationController', ['$scope', '$http', '$route', '$location', '$bsmodal', '$window',
            'globalConstants', 'getQueryParameter',
            function ($scope, $http, $route, $location, $bsmodal, $window, globalConstants, getQueryParameter) {

            $scope.titleList = globalConstants.title;
            $scope.quotation = {};
                $scope.stepsSaved = {"1": true, "2": false, "3": false, "4": false, "5": false};
            $scope.selectedItem = 1;
                $http.get('/pla/individuallife/proposal/getAllOccupation').success(function (response, status, headers, config) {
                    $scope.occupations = response;
                });
            $scope.onlyNumbers = /^[0-9]+$/;
            $scope.planDetailDto = {};
            $scope.proposedAssured = {};
            $scope.uneditable = false;
            $scope.proposerSameAsProposedAssured = false;

            $scope.quotationId = getQueryParameter('quotationId');
            if ($scope.quotationId) {
                $scope.uneditable = true;
                $http.get('/pla/individuallife/quotation/getquotation/' + $scope.quotationId)
                    .success(function (response) {
                        $scope.quotation = response;
                        $scope.proposedAssured = $scope.quotation.proposedAssured || {};
                        $scope.proposer = $scope.quotation.proposer || {};
                        $scope.proposerSameAsProposedAssured = $scope.quotation.proposedAssured.isAssuredTheProposer;

                        if ($scope.proposedAssured.dateOfBirth) {
                            $scope.proposedAssuredAge = calculateAge($scope.proposedAssured.dateOfBirth);
                        }

                        if ($scope.proposer.dateOfBirth) {
                            $scope.proposerAge = calculateAge($scope.proposer.dateOfBirth);
                        }
                        //This is for making the default selection during edit
                        $scope.selectedAgent = {};
                        $scope.selectedAgent.title = response.agentDetail.firstName || '';
                        $scope.selectedAgent.title = $scope.selectedAgent.title + ' ' + response.agentDetail.lastName || '';
                        $scope.selectedAgent.description = response.agentDetail;

                        //This is for making the default selection during edit
                        var selectedPlan = {};
                        selectedPlan.title = response.planDetail.planDetail.planName || '';
                        selectedPlan.description = response.planDetail;
                        $scope.selectedPlan = selectedPlan;
                        $scope.planDetailDto = response.planDetailDto;
                        $scope.selectedItem = 1;
                        $scope.stepsSaved["1"] = true;
                        $http.get('/pla/core/plan/getPlanById/' + response.planId)
                            .success(function (plandata) {
                                $scope.plan = plandata;
                            });
                    })
                    .error(function (response, status, headers, config) {
                    });
            }

            $scope.$watch('selectedAgent', function (newval) {
                if (newval) {
                    $scope.agent = newval.description;
                    $scope.quotation.agentId = $scope.agent["agent_id"];
                }
            });

            $scope.$watch('selectedPlan', function (newval, oldval) {
                if (newval && newval.description && newval.description.plan_id) {
                    $http.get('/pla/core/plan/getPlanById/' + newval.description.plan_id)
                        .success(function (response) {
                            $scope.plan = response;
                        });
                }
            });

            $scope.$watch('plan.planId', function (newval) {
                if (newval && !$scope.quotationId) {
                    $http.get('/pla/individuallife/quotation/getridersforplan/' + newval)
                        .success(function (response) {
                            $scope.planDetailDto.riderDetails = response;
                        });
                }
            });

            $scope.$watch('proposerSameAsProposedAssured', function (newval, oldval) {
                if (newval) {
                    $scope.proposer = $scope.proposedAssured;
                }
            });

            $scope.$watchGroup(['proposedAssured.dateOfBirth', 'proposer.dateOfBirth'], function (newval, oldval) {
                if (newval) {
                    if (newval[0]) {
                        $scope.proposedAssuredAge = calculateAge(newval[0]);
                    }
                    if (newval[1]) {
                        $scope.proposerAge = calculateAge(newval[1]);
                }
                }
            });

            $scope.saveStep1 = function () {
                if ($scope.quotationId) {
                    return;
                }
                $http.post('createquotation',
                    angular.extend($scope.proposedAssured, {
                        agentId: $scope.quotation.agentId,
                        planId: $scope.plan.planId,
                        isAssuredTheProposer: false
                    }))
                    .success(function (data) {
                        if (data.id)
                            $window.location = '/pla/individuallife/quotation/edit?quotationId=' + data.id;
                    });
            };

            $scope.$watch('quotationId', function (newval, oldval) {
                if (newval != oldval) {
                    $window.location = '/pla/individuallife/quotation/edit?quotationId=' + newval;
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

            $scope.saveStep2 = function () {
                var request = {proposedAssured: $scope.proposedAssured};
                $http.post('updatewithassureddetail',
                    angular.extend(request, {
                        quotationId: $scope.quotationId,
                        isAssuredTheProposer: $scope.proposerSameAsProposedAssured
                    }))
                    .success(function (data) {
                        $scope.stepsSaved[$scope.selectedItem] = true;
                        $scope.quotationId = data.id;
                    });
            };

            $scope.saveStep3 = function () {
                var request = {proposerDto: $scope.proposer};
                $http.post('updatewithproposerdetail',
                    angular.extend(request, {
                        quotationId: $scope.quotationId
                    }))
                    .success(function (data) {
                        $scope.stepsSaved[$scope.selectedItem] = true;
                        $scope.quotationId = data.id;
                    });
            };

            $scope.saveStep4 = function () {
                $scope.planDetailDto.planId = $scope.plan.planId;
                var request = angular.extend($scope.planDetailDto, {
                    quotationId: $scope.quotationId
                });
                $http.post('updatewithplandetail', {
                    planDetailDto: request,
                    quotationId: $scope.quotationId
                })
                    .success(function (data) {
                        $scope.stepsSaved[$scope.selectedItem] = true;
                        $scope.stepsSaved["5"] = true;
                        $scope.quotationId = data.id;

                    });
            };

            $scope.$on('changed.fu.wizard', function (name, event, data) {
                $scope.selectedItem = data.step;
                if (data && data.step == 4) {
                    $http.get('getpremiumdetail/' + $scope.quotationId).success(function (response) {
                        $scope.premiumData = response;
                    });
                }
            });

            $scope.$on('finished.fu.wizard', function (name, event, data) {
                $http.post('generatequotation/', {quotationId: $scope.quotationId}).success(function (response) {
                    $('#wizardStep').attr('disabled', true);
                });
            });

            $scope.remoteUrlRequestFn = function (str) {
                return {agentId: $scope.quotation.agentId};
            };

        }]
    )
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
        alert('print quotation...' + this.selectedItem);
        window.location.href = '/pla/individuallife/quotation/printquotation/' + this.selectedItem;
    }

    services.emailQuotation = function () {
        window.location.href = '/pla/individuallife/quotation/emailQuotation/' + this.selectedItem;
    }

    services.modifyQuotation = function () {
        var quotationId = this.selectedItem;
        window.location.href = "/pla/individuallife/quotation/edit?quotationId=" + quotationId;
    };

    services.viewQuotation = function () {
        var quotationId = this.selectedItem;
        window.location.href = "/pla/individuallife/quotation/creategrouplifequotation?quotationId=" + quotationId + "&version=" + msg.data + "&mode=view";
    };

    return services;
})();

