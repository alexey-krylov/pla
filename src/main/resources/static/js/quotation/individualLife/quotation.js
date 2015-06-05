/**
 * Created by pradyumna on 26-05-2015.
 */
(function (angular) {
    "use strict";

    function calculateAge(dob) {
        var age = 0;
        if (moment.isMoment(dob))
            age = moment().diff(dob, 'years') + 1;
        else if (moment.isDate(dob))
            age = moment().diff(new moment(dob.toDateString()), 'years') + 1;
        return age;
    };

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
                link: function (scope, element, attr, ctrl) {
                    scope.$watch('planDetailDto.policyTerm', function (newval) {
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
        .controller('QuotationController', ['$scope', '$http', '$route', '$bsmodal',
        'globalConstants', 'occupations',
            function ($scope, $http, $route, $bsmodal, globalConstants, occupations) {

            $scope.titleList = globalConstants.title;
            $scope.occupations = occupations;
            $scope.quotation = {};
            $scope.selectedItem = 1;
            $scope.onlyNumbers = /^[0-9]+$/;
                $scope.planDetailDto = {};
                $scope.proposedAssured = {
                    title: "Mr.",
                    'dateOfBirth': moment("17/02/1978", "DD/MM/YYYY"),
                    'emailAddress': "pradyumna.mohapatra@gmail.com",
                    'firstName': "PRADYUMNA",
                    'gender': "MALE",
                    'mobileNumber': "1234567890",
                    'nrcNumber': "nrc555",
                    'occupation': "Accountants",
                    'surname': "MOHAPATRA"
                };

                var quotationId = $route.current.params.quotationId;
                if (quotationId) {
                    $http.get('/pla/individuallife/quotation/getquotation/' + quotationId).success(function (response, status, headers, config) {
                        $scope.quotation = response;
                        $scope.proposedAssured = $scope.quotation.proposedAssured || {};
                        $scope.proposer = $scope.quotation.proposer || {};

                        if ($scope.proposedAssured.dateOfBirth) {
                            $scope.paDOB = $scope.proposedAssured.dateOfBirth;
                            $scope.proposedAssured.dateOfBirth = moment($scope.proposedAssured.dateOfBirth, 'DD/MM/YYYY');
                            $scope.proposedAssuredAge = calculateAge($scope.proposedAssured.dateOfBirth);
                        }

                        if ($scope.proposer.dateOfBirth) {
                            $scope.pDOB = $scope.proposer.dateOfBirth;
                            $scope.proposer.dateOfBirth = moment($scope.proposer.dateOfBirth, 'DD/MM/YYYY');
                            $scope.proposerAge = calculateAge($scope.proposedAssured.dateOfBirth);
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
                        $http.get('/pla/core/plan/getPlanById/' + response.planId)
                            .success(function (plandata) {
                                $scope.plan = plandata;
                                console.log(JSON.stringify($scope.plan));
                            });
                    }).error(function (response, status, headers, config) {
                    });
            }

            $scope.$watch('selectedAgent', function (newval) {
                if (newval) {
                    $scope.agent = newval.description;
                    $scope.quotation.agentId = $scope.agent["agent_id"];
                }
            });

            $scope.$watch('selectedPlan', function (newval, oldval) {
                console.log('plan selected event ');
                if (newval && newval.description && newval.description.plan_id) {
                    $http.get('/pla/core/plan/getPlanById/' + newval.description.plan_id)
                        .success(function (response) {
                            $scope.plan = response;
                        });
                }
            });

                $scope.$watch('plan.planId', function (newval) {
                    if (newval && !quotationId) {
                        console.log('getPlanById***********');
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

                $scope.$watchGroup(['paDOB', 'pDOB'], function (newval, oldval) {
                if (newval) {
                    if (newval[0]) {
                        $scope.proposedAssuredAge = calculateAge(newval[0]);
                    }
                    if (newval[1] && typeof newval[1] === 'object') {
                        $scope.proposerAge = calculateAge(newval[1]);
                    }
                }
            });


                $scope.saveStep1 = function () {
                $http.post('quotation/createquotation',
                    angular.extend($scope.proposedAssured, {
                        agentId: $scope.quotation.agentId,
                        planId: $scope.plan.planId,
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
                var request = {proposer: $scope.proposer};
                $http.post('quotation/updatewithproposerdetail',
                    angular.extend(request, {
                        quotationId: $scope.quotation.quotationId
                    }))
                    .success(function (data) {
                    });
            };

                $scope.saveStep4 = function () {
                    $scope.planDetailDto.planId = $scope.plan.planId;
                    var request = angular.extend($scope.planDetailDto, {
                        quotationId: quotationId
                    });
                    console.log('saving step 4 form data' + JSON.stringify(request));

                    $http.post('quotation/updatewithplandetail', {
                        planDetailDto: request,
                        quotationId: $scope.quotation.quotationId
                    })
                        .success(function (data) {
                        });
                };

                $scope.$on('changed.fu.wizard', function (name, event, data) {
                    //4 denotes we are in Premium Page, hence fetching the data
                    if (data && data.step == 4) {
                        $http.get('quotation/getpremiumdetail/' + quotationId).success(function (response) {
                            console.log(JSON.stringify(response))
                            $scope.premiumData = response;
                        });
                    }
                });

                $scope.$on('finished.fu.wizard', function (name, event, data) {
                    $http.post('quotation/generatequotation/', {quotationId: quotationId}).success(function (response) {

                    });
                });

            $scope.remoteUrlRequestFn = function (str) {
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
