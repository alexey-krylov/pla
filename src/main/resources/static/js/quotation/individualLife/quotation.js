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
        .directive('validateSumassured', function ($compile) {
            return {}
        })
        .directive('sumassured', function ($compile) {
            return {
                restrict: 'E',
                templateUrl: 'plan-sumassured.tpl',
                // element must have ng-model attribute.
                require: 'ngModel',
                link: function (scope, elem, attrs, ctrl) {
                    console.log('validateSumassured = ' + ctrl);
                    if (!ctrl)return;
                    scope.$watch('planDetailDto.sumAssured', function (newval, oldval) {
                        if (newval == oldval)return;
                        var plan = scope.$eval('plan');
                        console.log(JSON.stringify(plan.sumAssured));
                        if (plan.sumAssured.sumAssuredType !== 'RANGE')
                            return;
                        console.log(plan.sumAssured.multiplesOf);
                        if (newval && plan) {
                            var multiplesOf = plan.sumAssured.multiplesOf;
                            console.log('multiples ' + multiplesOf);
                            var modulus = parseInt(newval) % parseInt(multiplesOf);
                            console.log('modulus = ' + modulus);
                            var valid = modulus == 0;
                            console.log('valid = ' + valid);
                            ctrl.$setValidity('invalidMultiple', valid);
                        }
                        return valid ? newval : undefined;
                    });
                }
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
                    $scope.premiumTerms = function () {
                        var ageNextBirthday = calculateAge($scope.proposedAssured.dateOfBirth);
                        if ($scope.plan.premiumTermType === 'SPECIFIED_VALUES') {
                            var maxMaturityAge = $scope.plan.premiumTermType.maxMaturityAge || 1000;
                            return _.filter($scope.plan.premiumTerm.validTerms, function (term) {
                                return ageNextBirthday + parseInt(term.text) <= maxMaturityAge;
                            });
                        } else if ($scope.plan.premiumTermType === 'SPECIFIED_AGES') {
                            return _.filter($scope.plan.premiumTerm.maturityAges, function (term) {
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
        .directive('validateDob', function () {
            return {
                // restrict to an attribute type.
                restrict: 'A',
                // element must have ng-model attribute.
                require: 'ngModel',
                link: function (scope, ele, attrs, ctrl) {
                    ctrl.$parsers.unshift(function (value) {
                        var planDetail = scope.$eval('plan.planDetail');
                        if (value && planDetail) {
                            var dateOfBirth = scope.$eval('proposedAssured.dateOfBirth');
                            var age = calculateAge(dateOfBirth);
                            var valid = planDetail.minEntryAge <= age && age <= planDetail.maxEntryAge;
                            //ctrl.$setValidity('invalidMinAge', planDetail.minEntryAge <= age);
                        }
                        return valid ? value : undefined;
                    });

                    scope.$watch('proposedAssured.dateOfBirth', function (newval) {
                        var planDetail = scope.$eval('plan.planDetail');
                        if (planDetail) {
                            var age = calculateAge(newval);
                            ctrl.$setValidity('invalidMinAge', age >= planDetail.minEntryAge);
                            ctrl.$setValidity('invalidMaxAge', age <= planDetail.maxEntryAge);

                        }
                    });

                    scope.$watch('proposer.dateOfBirth', function (newval) {
                        if (!newval) return;
                        var age = calculateAge(newval);
                        ctrl.$setValidity('invalidMinAge', age >= 18);
                        ctrl.$setValidity('invalidMaxAge', age <= 60);
                    });
                }
            }
        })
        .controller('QuotationController', ['$scope', '$http', '$route', '$location', '$bsmodal', '$window',
            'globalConstants', 'getQueryParameter',
            function ($scope, $http, $route, $location, $bsmodal, $window, globalConstants, getQueryParameter) {

                $scope.titleList = globalConstants.title;
                $scope.quotation = {};
                $scope.stepsSaved = {"1": false, "2": false, "3": false, "4": true, "5": false};
                $scope.selectedItem = 1;
                $http.get('/pla/individuallife/proposal/getAllOccupation').success(function (response, status, headers, config) {
                    $scope.occupations = response;
                });

                $scope.todayDate = new Date();
                $scope.todayDate.setDate($scope.todayDate.getDate() - 1);

                $scope.onlyNumbers = /^[0-9]+$/;
                $scope.onlyText = /^[a-zA-Z ]*$/;
                $scope.planDetailDto = {};
                $scope.proposedAssured = {};
                $scope.uneditable = false;
                $scope.proposerSameAsProposedAssured = false;

                $scope.isSaveDisabled = function (stepForm) {
                    var returnval = true;
                    if (stepForm.$dirty && stepForm.$valid) {
                        $scope.stepsSaved[stepForm.$name == 'step2' ? "2" : stepForm.$name == 'step3' ? "3" : stepForm.$name == 'step4' ? "4" : "5"] = false;
                        returnval = false;
                    } else {
                        returnval = true;
                    }
                    console.log('Form Name ' + stepForm.$name + returnval);
                    return returnval;
                };


                $scope.$watchGroup(['proposedAssured.title', 'proposer.title'], function (newval, oldval) {

                    if (newval[0]) {
                        if (newval[0] == 'Mr.')
                            $scope.proposedAssured.gender = 'MALE';
                        if (newval[0] == 'Mrs.')
                            $scope.proposedAssured.gender = 'FEMALE';
                    }

                    if (newval[1]) {
                        if (newval[1] == 'Mr.')
                            $scope.proposer.gender = 'MALE';
                        if (newval[1] == 'Mrs.')
                            $scope.proposer.gender = 'FEMALE';
                    }

                });


                $scope.quotationId = getQueryParameter('quotationId');
                if ($scope.quotationId) {
                    $scope.uneditable = true;
                    $http.get('/pla/individuallife/quotation/getquotation/' + $scope.quotationId)
                        .success(function (response) {
                            $scope.quotation = response;
                            $scope.proposedAssured = $scope.quotation.proposedAssured || {};
                            $scope.proposer = $scope.quotation.proposer || {};

                            if ($scope.proposedAssured.dateOfBirth) {
                                $scope.proposedAssuredAge = calculateAge($scope.proposedAssured.dateOfBirth);
                            }

                            if ($scope.proposer.dateOfBirth) {
                                $scope.proposerAge = calculateAge($scope.proposer.dateOfBirth);
                            }

                            $scope.proposerSameAsProposedAssured = response.assuredTheProposer;

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
                            if ($scope.proposedAssured)
                                $scope.stepsSaved["2"] = true;

                            if ($scope.proposer) {
                                $scope.stepsSaved["3"] = true;
                            }

                            if ($scope.planDetailDto.sumAssured != null)
                                $scope.stepsSaved["4"] = true;

                            $http.get('/pla/core/plan/getPlanById/' + response.planId)
                                .success(function (plandata) {
                                    $scope.plan = plandata;
                                });
                        })
                        .error(function (response, status, headers, config) {
                        });
                    $scope.stepsSaved["5"] = true;
                }
                ;

                $scope.$watch('quotation.quotationStatus', function (newval, oldval) {
                    if (newval != oldval) {
                        $scope.stepsSaved["5"] = false;
                    }
                });

                $scope.$watch('selectedAgent', function (newval) {
                    if (newval) {
                        $scope.agent = newval.description;
                        $scope.quotation.agentId = $scope.agent["agent_id"];
                    }
                });

                $scope.$watch('selectedPlan', function (newval, oldval) {
                    if (newval && newval.description && newval.description.plan_id) {
                        var plan = newval.description;
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
                    if (newval != oldval) {
                        if (newval) {
                            $scope.proposer = $scope.proposedAssured;
                            $scope.proposer.dateOfBirth = $scope.proposedAssured.dateOfBirth;
                        } else
                            $scope.proposer = {};
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

                $scope.saveStep2 = function (stepForm) {
                    stepForm.$setPristine();
                    var request = {proposedAssured: $scope.proposedAssured};
                    $http.post('updatewithassureddetail',
                        angular.extend(request, {
                            quotationId: $scope.quotationId,
                            assuredTheProposer: $scope.proposerSameAsProposedAssured
                        }))
                        .success(function (data) {
                            $scope.stepsSaved[$scope.selectedItem] = true;
                            $scope.quotationId = data.id;
                        });
                };

                $scope.saveStep3 = function (stepForm) {
                    stepForm.$setPristine();
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

                $scope.saveStep4 = function (stepForm) {
                    stepForm.$setPristine();
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
                    $http.post('generatequotation/', {quotationId: $scope.quotationId}).success(function (response, status) {
                        $('#wizardStep').attr('disabled', true);
                        $('#quotationSearchForm').submit();
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

