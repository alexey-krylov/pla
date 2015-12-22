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
        .directive('sumassured',['$http', function ($http, $compile) {
            return {
                templateUrl: 'plan-sumassured.tpl',
                // element must have ng-model attribute.
                require: 'ngModel',
                link: function (scope, elem, attrs, ctrl) {
                    scope.$watch('planDetailDto.sumAssured', function (n,o) {
                        if(n){
                            scope.errorMessage='';
                            $http.get("/pla/individuallife/quotation/isSumAssuredGreaterThenThresholdLimit?sumAssured="+n).success(function(response){
                             //   alert(response.message);
                                if(response.status== 500){
                                    scope.errorMessage=response.message;
                                }
                            });
                            //console.log('value changed, new value is: ' + n);
                        }

                    });

                    //rider.sumAssured

                    $scope.$watch('rider.sumAssured', function (newval) {
                        if ($scope.coverage && $scope.coverage.coverageSumAssured.sumAssuredType === 'DERIVED') {
                            //$scope.rider.sumAssured=newval *($scope.coverage.coverageSumAssured.percentage/100);
                            var percentageValue=$scope.planDetailDto.sumAssured *($scope.coverage.coverageSumAssured.percentage/100);
                            if(percentageValue <= $scope.coverage.coverageSumAssured.maxLimit){
                                $scope.maxPercentage=percentageValue;
                                newval=percentageValue;
                                $scope.isMaximumReached=false;
                            }
                            else{
                                $scope.maxPercentage=$scope.coverage.coverageSumAssured.maxLimit;
                                newval=$scope.coverage.coverageSumAssured.maxLimit;
                                $scope.isMaximumReached=true;
                            }
                            console.log('Derived Type Came..');
                        }
                    });

                    if (!ctrl)return;

                }
            };
        }])
        .directive('viewEnabled', function () {
            return {
                link: function (scope, elem, attr, ctrl) {
                    var mode = scope.mode;
                    if (mode != 'view') {
                        return;
                    }
                    $(elem).attr('readonly', true);
                    $(elem).attr('disabled', true);

                }
            }

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
                            var ageNextBirthday = calculateAge($scope.proposedAssured.dateOfBirth);
                            return _.filter($scope.plan.policyTerm.maturityAges, function (term) {
                                return term.text > ageNextBirthday;
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
                            $scope.tempCoverages=coverage;
                            var ageNextBirthday = calculateAge($scope.proposedAssured.dateOfBirth);
                            if (coverage.coverageTermType === 'SPECIFIED_VALUES') {
                                var maxMaturityAge = coverage.coverageTerm.maxMaturityAge || 1000;
                                $scope.policyTerms = _.filter(coverage.coverageTerm.validTerms, function (term) {
                                    //return ageNextBirthday + term.text <= maxMaturityAge;
                                    return  term.text<=($scope.planDetailDto.policyTerm - ageNextBirthday)+1;
                                    //return ((term.text + ageNextBirthday) <= maxMaturityAge) && (term.text<=$scope.planDetailDto.policyTerm) ;
                                });
                            } else if (coverage.coverageTermType === 'AGE_DEPENDENT') {
                                $scope.policyTerms = _.filter(coverage.coverageTerm.maturityAges, function (term) {
                                    //return term.text > ageNextBirthday;
                                    return term.text <= (ageNextBirthday + $scope.planDetailDto.policyTerm) && (term.text > ageNextBirthday);
                                });
                            }
                            return coverage.coverageTermType;
                        } else {
                            return ""
                        }
                    }

                    $scope.$watch('planDetailDto.policyTerm', function (newval) {
                        if ($scope.tempCoverages && $scope.tempCoverages.coverageTermType === 'POLICY_TERM') {
                            $scope.rider.coverTerm = newval;
                        }

                    });

                }]
            };
        })
        .directive('validateOptsumassured', function () {
            return {
                // restrict to an attribute type.
                restrict: 'A',
                // element must have ng-model attribute.
                require: 'ngModel',
                link: function (scope, ele, attrs, ctrl) {
                    scope.$watch('rider.sumAssured', function (newval, oldval) {
                        if (newval == oldval)return;
                        if (newval) {
                            console.log('validating...***');
                            if (scope.coverage && scope.coverage.coverageSumAssured.sumAssuredType == 'RANGE') {
                                var multiplesOf = scope.coverage.coverageSumAssured.multiplesOf;
                                var modulus = parseInt(newval) % parseInt(multiplesOf);
                                var valid = modulus == 0;
                                ctrl.$setValidity('invalidMultiple', valid);
                            }
                        }
                        return valid ? newval : undefined;
                    });
                }
            }
        })
        .directive('coverageSumassured', function () {
            return {
                restrict: 'E',
                templateUrl: 'coverage-sumassured.tpl',
                controller: ['$scope', function ($scope) {
                    $scope.getSumAssuredType = function (riderDetail) {
                        if ($scope.plan) {
                            $scope.coverage = _.findWhere($scope.plan.coverages, {coverageId: riderDetail.coverageId});
                            // console.log('Coverage..'+JSON.stringify($scope.coverage));
                            return $scope.coverage.coverageSumAssured.sumAssuredType;
                        }
                    }
                    $scope.$watch('rider.sumAssured',function(newVal){
                        if(newVal && $scope.planDetailDto.sumAssured){
                            if ($scope.coverage && $scope.coverage.coverageSumAssured.sumAssuredType == 'RANGE') {
                                if($scope.coverage.coverageSumAssured.maxSumInsured <=$scope.planDetailDto.sumAssured){
                                    $scope.maxOptionalCoverage=$scope.coverage.coverageSumAssured.maxSumInsured;
                                }
                                else{
                                    $scope.maxOptionalCoverage=$scope.planDetailDto.sumAssured;
                                }
                            }

                        }
                    });

                    $scope.$watch('rider.sumAssured', function (newval) {
                        if ($scope.coverage && $scope.coverage.coverageSumAssured.sumAssuredType === 'DERIVED') {
                            //$scope.rider.sumAssured=newval *($scope.coverage.coverageSumAssured.percentage/100);
                            var percentageValue=$scope.planDetailDto.sumAssured *($scope.coverage.coverageSumAssured.percentage/100);
                            if(percentageValue <= $scope.coverage.coverageSumAssured.maxLimit){
                                $scope.maxPercentage=percentageValue;
                                newval=percentageValue;
                                $scope.isMaximumReached=false;
                            }
                            else{
                                $scope.maxPercentage=$scope.coverage.coverageSumAssured.maxLimit;
                                newval=$scope.coverage.coverageSumAssured.maxLimit;
                                $scope.isMaximumReached=true;
                            }
                            console.log('Derived Type Came..');
                        }
                    });

                    $scope.$watch('planDetailDto.sumAssured', function (newval) {
                        if ($scope.coverage && $scope.coverage.coverageSumAssured.sumAssuredType === 'DERIVED') {
                            //$scope.rider.sumAssured=newval *($scope.coverage.coverageSumAssured.percentage/100);
                            var percentageValue=newval *($scope.coverage.coverageSumAssured.percentage/100);
                            if(percentageValue <= $scope.coverage.coverageSumAssured.maxLimit){
                                $scope.maxPercentage=percentageValue;
                                $scope.rider.sumAssured=percentageValue;
                                $scope.isMaximumReached=false;
                            }
                            else{
                                $scope.maxPercentage=$scope.coverage.coverageSumAssured.maxLimit;
                                $scope.rider.sumAssured=$scope.coverage.coverageSumAssured.maxLimit;
                                $scope.isMaximumReached=true;
                            }
                            console.log('Derived Type Came..');
                        }
                        if ($scope.coverage && $scope.coverage.coverageSumAssured.sumAssuredType == 'RANGE') {
                             if($scope.coverage.coverageSumAssured.maxSumInsured <=newval){
                                 $scope.maxOptionalCoverage=$scope.coverage.coverageSumAssured.maxSumInsured;
                             }
                            else{
                                 $scope.maxOptionalCoverage=newval;
                             }
                        }

                    });

                    $scope.lessThanEqualTo = function (prop, val) {
                        return function (item) {
                            return item[prop] <= val;
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

                },
                controller: ['$scope', function ($scope) {
                    $scope.premiumTerms = function () {
                        var ageNextBirthday = calculateAge($scope.proposedAssured.dateOfBirth);
                        //console.log('plan*** '+JSON.stringify($scope.plan));
                        if ($scope.plan.premiumTermType === 'SPECIFIED_VALUES' || ($scope.plan.premiumTermType === 'SINGLE_SPECIFIED_VALUES' && $scope.planDetailDto.premiumPaymentType=='OTHER_PREMIUM')) {
                            var maxMaturityAge = $scope.plan.premiumTermType.maxMaturityAge || 1000;
                            if($scope.plan.policyTermType === 'SPECIFIED_VALUES'){
                                return _.filter($scope.plan.premiumTerm.validTerms, function (term) {
                                    return parseInt(term.text) <= $scope.planDetailDto.policyTerm;

                                });
                            }
                            else if($scope.plan.policyTermType === 'MATURITY_AGE_DEPENDENT'){
                                return _.filter($scope.plan.premiumTerm.validTerms, function (term) {
                                    return ageNextBirthday + parseInt(term.text) <= $scope.planDetailDto.policyTerm;
                                });
                            }
                           /* return _.filter($scope.plan.premiumTerm.validTerms, function (term) {
                                return ageNextBirthday + parseInt(term.text) <= $scope.planDetailDto.policyTerm;

                            });*/
                        } else if ($scope.plan.premiumTermType === 'SPECIFIED_AGES' || ($scope.plan.premiumTermType === 'SINGLE_SPECIFIED_AGES' && $scope.planDetailDto.premiumPaymentType=='OTHER_PREMIUM')) {
                             if($scope.plan.policyTermType === 'MATURITY_AGE_DEPENDENT'){
                                 return _.filter($scope.plan.premiumTerm.maturityAges, function (term) {
                                     return $scope.planDetailDto.policyTerm >= parseInt(term.text) && ageNextBirthday <=parseInt(term.text);
                                 });
                             }
                            else if($scope.plan.policyTermType === 'SPECIFIED_VALUES'){
                                //console.log('Plan SPecified Values.. ***'+JSON.stringify($scope.plan));
                                return _.filter($scope.plan.premiumTerm.maturityAges, function (term) {
                                    return $scope.planDetailDto.policyTerm + ageNextBirthday >= parseInt(term.text) && ageNextBirthday <= parseInt(term.text);
                                });
                            }
                            /*return _.filter($scope.plan.premiumTerm.maturityAges, function (term) {
                             console.log('ageNextBirthday'+ageNextBirthday+'policyTerm'+$scope.planDetailDto.policyTerm + '<='+ parseInt(term.text)+'&&'+'ageNextBirthday'+ageNextBirthday+'>='+'premiumTerm'+parseInt(term.text));
                             console.log((($scope.planDetailDto.policyTerm + ageNextBirthday) <= parseInt(term.text)) && (ageNextBirthday>=parseInt(term.text)))
                             return (($scope.planDetailDto.policyTerm + ageNextBirthday) <= parseInt(term.text)) && (ageNextBirthday>=parseInt(term.text)) ;
                             });*/
                        }
                    };

                    $scope.lessThanEqualTo = function (prop, val) {
                        return function (item) {
                            return item[prop] <= val;
                        }
                    }

                    $scope.$watch('planDetailDto.policyTerm', function (newval) {

                        if ($scope.plan && $scope.plan.premiumTermType === 'REGULAR') {
                            $scope.planDetailDto.premiumPaymentTerm = newval;
                        }else if ($scope.plan && $scope.plan.premiumTermType === 'SINGLE_REGULAR' && $scope.planDetailDto.premiumPaymentType=='OTHER_PREMIUM') {
                            $scope.planDetailDto.premiumPaymentTerm = newval;
                        }

                    })
                    $scope.$watch('planDetailDto.policyTerm', function (newval) {
                        if ($scope.plan && $scope.plan.premiumTermType === 'SINGLE') {
                            $scope.planDetailDto.premiumPaymentTerm = 1;
                        }else if ($scope.plan && $scope.plan.premiumTermType === 'SINGLE_REGULAR' && $scope.planDetailDto.premiumPaymentType=='SINGLE_PREMIUM') {
                            $scope.planDetailDto.premiumPaymentTerm = 1;
                        }else if ($scope.plan && $scope.plan.premiumTermType === 'SINGLE_SPECIFIED_VALUES' && $scope.planDetailDto.premiumPaymentType=='SINGLE_PREMIUM') {
                            $scope.planDetailDto.premiumPaymentTerm = 1;
                        }else if ($scope.plan && $scope.plan.premiumTermType === 'SINGLE_SPECIFIED_AGES' && $scope.planDetailDto.premiumPaymentType=='SINGLE_PREMIUM') {
                            $scope.planDetailDto.premiumPaymentTerm = 1;
                        }
                    });
                }]
            };
        })
        .directive('validateSumassured', function () {
            return {
                // restrict to an attribute type.
                restrict: 'A',
                // element must have ng-model attribute.
                require: 'ngModel',
                link: function (scope, ele, attrs, ctrl) {
                    scope.$watch('planDetailDto.sumAssured', function (newval, oldval) {
                        if (newval == oldval)return;
                        if (newval) {
                            console.log('validating...***');
                            var plan = scope.$eval('plan');
                            if (plan && plan.sumAssured.sumAssuredType == 'RANGE') {
                                var multiplesOf = plan.sumAssured.multiplesOf;
                                var modulus = parseInt(newval) % parseInt(multiplesOf);
                                var valid = modulus == 0;
                                ctrl.$setValidity('invalidMultiple', valid);
                            }
                        }
                        return valid ? newval : undefined;
                    });
                }
            }
        })

        .directive('validateProposedDob', function () {
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
                            ctrl.$setValidity('invalidProposedMinAge', age >= planDetail.minEntryAge);
                            ctrl.$setValidity('invalidProposedMaxAge', age <= planDetail.maxEntryAge);

                        }
                    });

                }
            }
        })
        .directive('validateProposerDob', function () {
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

                    scope.$watch('proposer.dateOfBirth', function (newval) {
                        if (!newval) return;
                        var age = calculateAge(newval);
                        ctrl.$setValidity('invalidMinAge', age >= 18);
                        ctrl.$setValidity('invalidMaxAge', age <= 60);
                    });
                }
            }
        })
        .directive('converterDecimal', function ($filter) {
            var FLOAT_REGEXP_1 = /^\$?\d+.(\d{3})*(\,\d*)$/; //Numbers like: 1.123,56
            var FLOAT_REGEXP_2 = /^\$?\d+,(\d{3})*(\.\d*)$/; //Numbers like: 1,123.56
            var FLOAT_REGEXP_3 = /^\$?\d+(\.\d*)?$/; //Numbers like: 1123.56
            var FLOAT_REGEXP_4 = /^\$?\d+(\,\d*)?$/; //Numbers like: 1123,56

            return {
                require: 'ngModel',
                link: function (scope, elm, attrs, ctrl) {
                    ctrl.$parsers.unshift(function (viewValue) {
                        if (FLOAT_REGEXP_1.test(viewValue)) {
                            ctrl.$setValidity('float', true);
                            return parseFloat(viewValue.replace('.', '').replace(',', '.'));
                        } else if (FLOAT_REGEXP_2.test(viewValue)) {
                            ctrl.$setValidity('float', true);
                            return parseFloat(viewValue.replace(',', ''));
                        } else if (FLOAT_REGEXP_3.test(viewValue)) {
                            ctrl.$setValidity('float', true);
                            return parseFloat(viewValue);
                        } else if (FLOAT_REGEXP_4.test(viewValue)) {
                            ctrl.$setValidity('float', true);
                            return parseFloat(viewValue.replace(',', '.'));
                        }else {
                            ctrl.$setValidity('float', false);
                            return undefined;
                        }
                    });

                    ctrl.$formatters.unshift(
                        function (modelValue) {
                            return $filter('number')(parseFloat(modelValue) , 2);
                        }
                    );
                }
            };
        })
        .controller('QuotationController', ['$scope', '$http', '$route', '$location', '$bsmodal', '$window',
            'globalConstants', 'getQueryParameter', '$timeout', '$filter',
            function ($scope, $http, $route, $location, $bsmodal, $window, globalConstants, getQueryParameter, $timeout, $filter) {

                $scope.tempCoverages = {};
                var absUrl = $location.absUrl();
                $scope.titleList = globalConstants.title;
                $scope.mode = absUrl.indexOf('view') != -1 ? 'view' : null;
                $scope.quotationId = getQueryParameter('quotationId');
                $scope.quotation = {};
                $scope.stepsSaved = {"1": false, "2": false, "3": false, "4": true, "5": false};
                $scope.selectedItem = 1;
                $http.get('/pla/individuallife/proposal/getAllOccupation').success(function (response, status, headers, config) {
                    $scope.occupations = response;
                });
                //Geting All ConfiguredPlan
                $scope.cfgPlanList=[];
                $scope.getBasicPlanDetails=function(){
                     $http.get('/pla/core/agent/searchplan?agentId='+$scope.quotation.agentId).success(function (response, status, headers, config) {
                     //console.log('SearchPlanScreen'+JSON.stringify(response));
                         $scope.cfgPlanList=response;
                     }).error(function (response, status, headers, config) {
                     });

                }

                $scope.action = function(){
                    alert("invoked");
                };
                $scope.todayDate = new Date();
                $scope.todayDate.setDate($scope.todayDate.getDate() - 1);

                $scope.onlyNumbers = /^[0-9]+$/;
                $scope.onlyText = /^[a-zA-Z ]*$/;
                $scope.planDetailDto = {};
                $scope.proposedAssured = {};
                $scope.uneditable = false;
                $scope.proposerSameAsProposedAssured = false;

                $scope.clearPolicyTerm=function(){
                    $scope.planDetailDto.policyTerm='';
                };

                $scope.isSaveDisabled = function (stepForm) {
                    var returnval = true;
                    if (stepForm.$dirty && stepForm.$valid) {
                        if($scope.proposerSameAsProposedAssured){
                            $scope.stepsSaved["2"] = true;
                            $scope.stepsSaved["3"] = true;
                            //$scope.stepsSaved[stepForm.$name == 'step2' ? "2" : stepForm.$name == 'step3' ? "3" : stepForm.$name == 'step4' ? "4" : "5"] = false;
                        }
                        else{
                            $scope.stepsSaved[stepForm.$name == 'step2' ? "2" : stepForm.$name == 'step3' ? "3" : stepForm.$name == 'step4' ? "4" : "5"] = false;
                        }
                        //$scope.stepsSaved[stepForm.$name == 'step2' ? "2" : stepForm.$name == 'step3' ? "3" : stepForm.$name == 'step4' ? "4" : "5"] = false;
                        returnval = false;
                    } else {
                        returnval = true;
                    }
                    if($scope.isRiderDeleted){
                        $scope.stepsSaved["4"] = false;
                        returnval=false;
                    }
                    //console.log('Form Name ' + stepForm.$name + returnval);
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
                $scope.isClientValid=false;

                $scope.resetClientInfo=function(){
                    $scope.proposedAssured.title='';
                    $scope.proposedAssured.surname='';
                    $scope.proposedAssured.firstName='';
                    $scope.proposedAssured.nrc='';
                    $scope.proposedAssured.opportunityId='';
                    $scope.proposedAssured.clientId='';
                    $scope.isClientValid=false;
                }
                $scope.searchProposedAssuredByClientId=function(){
                    if($scope.proposedAssured.clientId){
                        $http.get('/pla/individuallife/proposal/getclientid/' + $scope.proposedAssured.clientId)
                            .success(function (response) {
                                $scope.serverError = false;
                                $scope.proposedAssured.title=response.data.title;
                                $scope.proposedAssured.surname=response.data.surname;
                                $scope.proposedAssured.firstName=response.data.firstName;
                                $scope.proposedAssured.nrc=response.data.nrc;
                                $scope.proposedAssured.opportunityId=response.opportunityId;
                                $scope.isClientValid=true;
                            }).error(function (response, status, headers, config) {
                                $scope.serverError = true;
                                $scope.serverErrMsg = response.message;
                                //$scope.proposedAssured={};
                                $scope.proposedAssured.title='';
                                $scope.proposedAssured.surname='';
                                $scope.proposedAssured.firstName='';
                                $scope.proposedAssured.nrc='';
                                $scope.proposedAssured.opportunityId='';
                                $scope.proposedAssured.clientId='';
                                $scope.isClientValid=false;
                            });

                    }
                }
                /****
                 *
                 * Seraching ProposerClientId Related Data
                 */
                $scope.isClientProposerValid=false;
                $scope.searchProposerByClientId=function(){
                    if($scope.proposer.clientId){
                        $http.get('/pla/individuallife/proposal/getclientid/' + $scope.proposer.clientId)
                            .success(function (response) {
                                $scope.serverError = false;
                                $scope.proposer.title=response.data.title;
                                $scope.proposer.surname=response.data.surname;
                                $scope.proposer.firstName=response.data.firstName;
                                $scope.proposer.nrc=response.data.nrc;
                                $scope.proposer.dateOfBirth=response.data.dateOfBirth;
                                $scope.proposer.emailAddress=response.data.emailAddress;
                                $scope.proposer.mobileNumber=response.data.mobileNumber;
                                $scope.isClientProposerValid=true;
                            }).error(function (response, status, headers, config) {
                                $scope.serverError = true;
                                $scope.serverErrMsg = response.message;
                                $scope.proposer.title='';
                                $scope.proposer.surname='';
                                $scope.proposer.firstName='';
                                $scope.proposer.nrc='';
                                $scope.proposer.opportunityId='';
                                $scope.proposer.clientId='';
                                $scope.proposer.dateOfBirth='';
                                $scope.proposer.emailAddress='';
                                $scope.proposer.mobileNumber='';
                                $scope.isClientProposerValid=false;
                            });

                    }
                }
                $scope.resetProposerClientIdInfo=function(){
                    $scope.proposer.title='';
                    $scope.proposer.surname='';
                    $scope.proposer.firstName='';
                    $scope.proposer.nrc='';
                    $scope.proposer.opportunityId='';
                    $scope.proposer.clientId='';
                    $scope.proposer.dateOfBirth='';
                    $scope.proposer.emailAddress='';
                    $scope.proposer.mobileNumber='';
                    $scope.proposerAge='';
                    $scope.isClientProposerValid=false;
                }
                $scope.originalProposer = {};
                if ($scope.quotationId) {
                    $scope.uneditable = true;
                    $http.get('/pla/individuallife/quotation/getquotation/' + $scope.quotationId)
                        .success(function (response) {
                            $scope.quotation = response;
                            console.log('QuotationResponse..'+JSON.stringify(response));
                            $scope.proposedAssured = $scope.quotation.proposedAssured || {};
                            $scope.proposer = $scope.quotation.proposer || {};
                            $scope.originalProposer = $scope.quotation.proposer || {};
                            $scope.opportunityId=$scope.quotation.opportunityId;

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
                            //selectedPlan.title = response.planDetail.planDetail.planName || '';

                            //selectedPlan.description = response.planDetail;
                            selectedPlan=response.planDetail.planDetail;
                            //console.log('selectedPlan1'+JSON.stringify(selectedPlan));
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

                /**
                 *
                 * Retriving the Plan
                 *
                 */
                $scope.getPlanDetails=function(newval)
                {
                    $http.get('/pla/core/plan/getPlanById/' + newval)
                        .success(function (response) {
                            $scope.plan = response;
                        });
                }

                /*$scope.$watch('selectedPlan', function (newval, oldval) {
                 console.log('SelectedPlan'+JSON.stringify(newval));
                 if (newval && newval.description && newval.description.plan_id) {
                 var plan = newval.description;
                 $http.get('/pla/core/plan/getPlanById/' + newval.description.plan_id)
                 .success(function (response) {
                 $scope.plan = response;
                 });

                 }
                 });*/
                $scope.searchRiders=[];
                $scope.riderDetailCalling = function () {
                    //alert('calling Rider Details...');
                    $http.get('/pla/individuallife/quotation/getridersforplan/' + $scope.plan.planId + '/' + calculateAge($scope.proposedAssured.dateOfBirth))
                        .success(function (response) {
                            //$scope.planDetailDto.riderDetails = response;
                            $scope.searchRiders=response;

                        });
                }

                /*$scope.$watch('plan.planId', function (newval) {
                 if (newval && !$scope.quotationId) {
                 $http.get('/pla/individuallife/quotation/getridersforplan/' + newval +'/'+calculateAge($scope.proposedAssured.dateOfBirth))
                 .success(function (response) {
                 $scope.planDetailDto.riderDetails = response;
                 });
                 }
                 });*/

                $scope.$watchGroup(['proposedAssured.dateOfBirth', 'proposer.dateOfBirth'], function (newval, oldval) {
                    if (newval) {
                        if (newval[0]) {
                            $scope.proposedAssuredAge = calculateAge(newval[0]);
                            //$scope.proposer.dateOfBirth = newval[0];
                        }
                        if (newval[1]) {
                            $scope.proposerAge = calculateAge(newval[1]);
                        }
                    }
                });

                $scope.formatDate = function (date) {
                    return $filter('date')(date, "dd/MM/yyyy");
                }

                $scope.saveStep1 = function () {
                    if ($scope.quotationId) {
                        return;
                    }
                    $timeout(3);
                    $http.post('createquotation',
                        angular.extend($scope.proposedAssured, {
                            agentId: $scope.quotation.agentId,
                            planId: $scope.plan.planId,
                            opportunityId: $scope.opportunityId
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

                $scope.$watch('proposerSameAsProposedAssured', function (newval, oldval) {
                    if (newval == oldval)
                        return;
                    if (!newval) {
                        //$scope.proposer = angular.copy($scope.originalProposer);
                        $scope.proposer = null;
                        $scope.proposerAge = null;
                    } else {
                        $scope.proposer = $scope.proposedAssured;
                    }
                });

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
                            $scope.stepsSaved["5"] = true;
                            //$window.location = '/pla/individuallife/quotation/edit?quotationId=' + data.id;
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
                            $scope.stepsSaved["5"] = true;
                        });
                };

                $scope.saveStep4 = function (stepForm) {
                    $scope.isRiderDeleted=false;
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

                $scope.selectOptionalCover=function(riderSelected){

                    if($scope.isRiderDeleted){
                        $scope.isRiderDeleted=false;
                    }
                    if(riderSelected){
                        var riderChoose=_.findWhere($scope.searchRiders, {coverageName: riderSelected});
                        if(riderChoose){
                            if($scope.planDetailDto.riderDetails == null){
                                $scope.planDetailDto.riderDetails=[];
                            }
                            $scope.planDetailDto.riderDetails.push(riderChoose);
                        }
                        // ReArranging the searchRiders List
                        $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: riderSelected});
                        if(riderSelected == 'Cash & Security Optional Covers 1'){
                            $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 2'});
                            $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 3'});
                            $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 4'});
                        }
                        else if(riderSelected == 'Cash & Security Optional Covers 2'){
                            $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 1'});
                            $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 3'});
                            $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 4'});
                        }
                        else if(riderSelected == 'Cash & Security Optional Covers 3'){
                            $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 1'});
                            $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 2'});
                            $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 4'});
                        }
                        else if(riderSelected == 'Cash & Security Optional Covers 4'){
                            $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 1'});
                            $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 2'});
                            $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 3'});
                        }
                    }
                }

                $scope.searchRidersCopy=[];
                $scope.isRiderDeleted=false;
                $scope.deleteRider=function(coverageName){
                    $scope.isRiderDeleted=true;

                    // Deleting the Selected Rider From PlanDetail's Rider List
                    $scope.planDetailDto.riderDetails=_.reject($scope.planDetailDto.riderDetails, {coverageName:coverageName});

                    // ReArranging the Rider which has deleted into searchRiders List

                    if(coverageName == 'Cash & Security Optional Covers 1' ||coverageName == 'Cash & Security Optional Covers 2'
                        || coverageName == 'Cash & Security Optional Covers 3' || coverageName == 'Cash & Security Optional Covers 4'){

                        var coverage1=_.findWhere($scope.searchRidersCopy, {coverageName: 'Cash & Security Optional Covers 2'});
                        if(coverage1){
                            $scope.searchRiders.push(coverage1);
                        }
                        var coverage2=_.findWhere($scope.searchRidersCopy, {coverageName: 'Cash & Security Optional Covers 3'});
                        if(coverage2){
                            $scope.searchRiders.push(coverage2);
                        }
                        var coverage3=_.findWhere($scope.searchRidersCopy, {coverageName: 'Cash & Security Optional Covers 4'});
                        if(coverage3){
                            $scope.searchRiders.push(coverage3);
                        }
                        var coverage4=_.findWhere($scope.searchRidersCopy, {coverageName: 'Cash & Security Optional Covers 1'});
                        if(coverage4){
                            $scope.searchRiders.push(coverage4);
                        }
                    }
                    else{
                        var coverage=_.findWhere($scope.searchRidersCopy,{coverageName: coverageName});
                        if(coverage){
                            $scope.searchRiders.push(coverage);
                        }
                    }
                    $scope.planDetailDto.riderSelected='';
                }
                $scope.$on('changed.fu.wizard', function (name, event, data) {
                    $scope.selectedItem = data.step;
                    if (data && data.step == 5) {
                        $http.get('getpremiumdetail/' + $scope.quotationId)
                            .success(function (response) {
                                console.log('success ***');
                                if($scope.serverError){
                                    $scope.serverError = false;
                                }
                                $scope.premiumData = response;
                            }).error(function (response) {
                                $scope.stepsSaved["5"] = false;
                                $scope.serverError = true;
                                $scope.serverErrMsg = response.message;
                            });
                    }
                    if (data && data.step == 4) {
                        if($scope.plan.premiumTermType === 'SINGLE' || $scope.plan.premiumTermType === 'SPECIFIED_VALUES' || $scope.plan.premiumTermType === 'SPECIFIED_AGES' || $scope.plan.premiumTermType === 'REGULAR'){
                            $scope.planDetailDto.premiumPaymentType ='OTHER_PREMIUM';
                            $scope.premiumTypeReadOnly=true;
                        }
                        else{
                            $scope.premiumTypeReadOnly=false;
                            //$scope.planDetailDto.premiumPaymentType ='';
                        }
                        $http.get('/pla/individuallife/quotation/getridersforplan/' + $scope.planDetailDto.planId + '/' + calculateAge($scope.proposedAssured.dateOfBirth))
                            .success(function (response) {
                                angular.copy(response,$scope.searchRidersCopy);
                                $scope.searchRiders=response;
                                var i=0;
                                if($scope.planDetailDto.riderDetails != null && $scope.planDetailDto.riderDetails.length > 0){
                                    for(i;i< $scope.planDetailDto.riderDetails.length;i++){
                                        $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: $scope.planDetailDto.riderDetails[i].coverageName})
                                    }
                                }

                                //ReArranging in SearchRider Regarding  Specified Plan
                                //var coverage = _.findWhere($scope.planDetailDto.riderDetails, {coverageId: searchRider.coverageId});
                                var j=0;
                                for(j;j< $scope.planDetailDto.riderDetails.length;j++){
                                    if($scope.planDetailDto.riderDetails[j].coverageName == 'Cash & Security Optional Covers 1'){
                                        $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 2'});
                                        $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 3'});
                                        $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 4'});
                                        break;
                                    }
                                    else if($scope.planDetailDto.riderDetails[j].coverageName == 'Cash & Security Optional Covers 2'){
                                        $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 1'});
                                        $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 3'});
                                        $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 4'});
                                        break;
                                    }
                                    else if($scope.planDetailDto.riderDetails[j].coverageName == 'Cash & Security Optional Covers 3'){
                                        $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 2'});
                                        $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 1'});
                                        $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 4'});
                                        break;
                                    }
                                    else if($scope.planDetailDto.riderDetails[j].coverageName == 'Cash & Security Optional Covers 4'){
                                        $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 2'});
                                        $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 3'});
                                        $scope.searchRiders=_.reject($scope.searchRiders, {coverageName: 'Cash & Security Optional Covers 1'});
                                        break;
                                    }
                                }
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

                $scope.checkIfPlanSupportsSelf = function () {
                    if (!$scope.plan)return false;
                    var relation = _.find($scope.plan.planDetail.applicableRelationships, function (val) {
                        return val == 'SELF'
                    });
                    if (relation != 'SELF') {
                        $scope.proposerSameAsProposedAssured = false;
                    }
                    return relation != 'SELF';
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
        if (this.status == 'GENERATED' || this.status == 'SHARED') {
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
        /* window.location.href = '/pla/individuallife/quotation/emailQuotation/' + this.selectedItem;*/

        window.open('/pla/individuallife/quotation/emailQuotation/' + this.selectedItem, "_blank", "toolbar=no,resizable=no," +
        "scrollable=no,menubar=no,personalbar=no,dependent=yes,dialog=yes,split=no,titlebar=no,resizable=no,location=no,left=100px");

    }

    services.modifyQuotation = function () {
        var quotationId = this.selectedItem;
        window.location.href = "/pla/individuallife/quotation/edit?quotationId=" + quotationId;
    };

    services.viewQuotation = function () {
        var quotationId = this.selectedItem;
        window.location.href = "/pla/individuallife/quotation/view?quotationId=" + quotationId + "&mode=view";
    };

    return services;
})();

