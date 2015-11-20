angular.module('createProposal', ['pla.individual.proposal', 'common', 'ngRoute', 'commonServices', 'ngMessages', 'angucomplete-alt', 'mgcrea.ngStrap.select', 'angularFileUpload', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives'])
    .directive('policyterm', function () {
        return {
            restrict: 'E',
            templateUrl: 'plan-policyterm.tpl',
            controller: ['$scope', function ($scope) {
                $scope.policyTerms = function () {
                    //console.log('PlanDetailsINsideDirective'+JSON.stringify($scope.plan));
                    if ($scope.plan.policyTermType === 'SPECIFIED_VALUES') {
                        var maxMaturityAge = $scope.plan.policyTerm.maxMaturityAge || 1000;
                        //var ageNextBirthday = calculateAge($scope.proposedAssured.dateOfBirth);
                        var ageNextBirthday = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                        return _.filter($scope.plan.policyTerm.validTerms, function (term) {
                            return ageNextBirthday + term.text <= maxMaturityAge;
                        });
                    } else if ($scope.plan.policyTermType === 'MATURITY_AGE_DEPENDENT') {
                        var ageNextBirthday = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                        return _.filter($scope.plan.policyTerm.maturityAges, function (term) {
                            //console.log('Term'+ JSON.stringify(term));
                            return term.text > ageNextBirthday;
                        });
                    }
                    return [];
                };
            }]
        };
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
                        var ageNextBirthday = moment().diff(new moment(new Date(dateOfBirth)), 'years') + 1;
                        //var age = calculateAge(dateOfBirth);
                        var valid = planDetail.minEntryAge <= ageNextBirthday && ageNextBirthday <= planDetail.maxEntryAge;
                        //ctrl.$setValidity('invalidMinAge', planDetail.minEntryAge <= age);
                    }
                    return valid ? value : undefined;
                });

                scope.$watch('proposedAssured.dateOfBirth', function (newval) {
                    var planDetail = scope.$eval('plan.planDetail');
                    if (planDetail) {
                        //var age = calculateAge(newval);
                        var ageNextBirthday = moment().diff(new moment(new Date(newval)), 'years') + 1;
                        ctrl.$setValidity('invalidProposedMinAge', ageNextBirthday >= planDetail.minEntryAge);
                        ctrl.$setValidity('invalidProposedMaxAge', ageNextBirthday <= planDetail.maxEntryAge);

                    }
                });

            }
        }
    })
    .directive('coverageTerm', function () {
        return {
            restrict: 'E',
            templateUrl: 'plan-coverage.tpl',
            controller: ['$scope', function ($scope) {
                $scope.policyTerms = [];
                $scope.getCoverageTermType = function (searchRider) {
                    ////alert(JSON.stringify(searchRider));
                    if ($scope.plan) {
                        //console.log("#########");
                        //console.log('Plan:***'+JSON.stringify($scope.plan));
                        var coverage = _.findWhere($scope.plan.coverages, {coverageId: searchRider.coverageId});
                        //console.log("Coverage Details in CoverageTermTd..."+JSON.stringify(coverage));
                        $scope.tempCoverages = coverage;
                        //var ageNextBirthday = calculateAge($scope.proposedAssured.dateOfBirth);
                        var ageNextBirthday = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                        if (coverage.coverageTermType === 'SPECIFIED_VALUES') {
                            var maxMaturityAge = coverage.coverageTerm.maxMaturityAge;
                            console.log('ageNextBirthdayCoverage *** SPECIFIED_VALUES***'+ageNextBirthday);
                            console.log('maxMaturityAgeTd***SPECIFIED_VALUES***'+JSON.stringify(maxMaturityAge));
                            $scope.policyTerms = _.filter(coverage.coverageTerm.validTerms, function (term) {
                                console.log("term>>>>>>>>"+"("+term.text+"+"+ageNextBirthday+")<="+maxMaturityAge);
                                console.log('>>>>>>>>'+((term.text + ageNextBirthday) <= maxMaturityAge));
                                return ((term.text + ageNextBirthday) <= maxMaturityAge) && (term.text <= $scope.proposalPlanDetail.policyTerm);
                            });

                        } else if (coverage.coverageTermType === 'AGE_DEPENDENT') {
                            $scope.policyTerms = _.filter(coverage.coverageTerm.maturityAges, function (term) {
                                //console.log('CoverageTerm$scope.policyTerms**'+JSON.stringify($scope.policyTerms));
                                //console.log('ageNextBirthdayCoverage'+ageNextBirthday);
                                //console.log('PolicTerm'+$scope.proposalPlanDetail.policyTerm);
                                //console.log('maturityAges'+term.text);
                                //console.log('***********');
                                //console.log(term.text + '<='+(ageNextBirthday +$scope.proposalPlanDetail.policyTerm) + term.text <= (ageNextBirthday +$scope.proposalPlanDetail.policyTerm));
                                //console.log("term <=" +"(ageNextBirthday +$scope.proposalPlanDetail.policyTerm)");
                                //console.log("term **"+ term.text +" <= "+ (ageNextBirthday )+" +"+($scope.proposalPlanDetail.policyTerm) +"--> "+ (term.text <= (ageNextBirthday +$scope.proposalPlanDetail.policyTerm)));
                                //console.log('condition'+ term.text <= (ageNextBirthday +$scope.proposalPlanDetail.policyTerm));
                                return (term.text <= (ageNextBirthday + $scope.proposalPlanDetail.policyTerm)) && (term.text> ageNextBirthday);
                            });
                        }
                        return coverage.coverageTermType;
                    } else {
                        return ""
                    }
                }

                $scope.$watch('proposalPlanDetail.policyTerm', function (newval) {
                    if ($scope.tempCoverages && $scope.tempCoverages.coverageTermType === 'POLICY_TERM') {
                        $scope.searchRider.coverTerm = newval;
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
                scope.$watch('proposalPlanDetail.sumAssured', function (newval, oldval) {
                    if (newval == oldval)return;
                    if (newval) {
                        //console.log('validating...***');
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

    .directive('premiumterm', function () {
        return {
            restrict: 'E',
            templateUrl: 'plan-premiumterm.tpl',
            link: function (scope) {

            },
            controller: ['$scope', function ($scope) {
                $scope.premiumTerms = function () {
                    //var ageNextBirthday = calculateAge($scope.proposedAssured.dateOfBirth);
                    var ageNextBirthday = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                    if ($scope.plan.premiumTermType === 'SPECIFIED_VALUES') {
                        var maxMaturityAge = $scope.plan.premiumTermType.maxMaturityAge || 1000;
                        return _.filter($scope.plan.premiumTerm.validTerms, function (term) {
                            return ageNextBirthday + parseInt(term.text) <= maxMaturityAge;
                        });
                    } else if ($scope.plan.premiumTermType === 'SPECIFIED_AGES') {
                        return _.filter($scope.plan.premiumTerm.maturityAges, function (term) {
                            return parseInt(term.text) > ageNextBirthday;
                        });
                    }
                };

                $scope.lessThanEqualTo = function (prop, val) {
                    return function (item) {
                        return item[prop] <= val;
                    }
                }

                $scope.$watch('proposalPlanDetail.policyTerm', function (newval) {
                    if ($scope.plan && $scope.plan.premiumTermType === 'REGULAR') {
                        $scope.proposalPlanDetail.premiumPaymentTerm = newval;
                    }

                });
            }]
        };
    })

    .directive('coverageSumassured', function () {
        return {
            restrict: 'E',
            templateUrl: 'coverage-sumassured.tpl',
            controller: ['$scope', function ($scope) {
                $scope.getSumAssuredType = function (searchRider) {
                    if ($scope.plan) {
                        //console.log('Plan Optional SumAssured Type..:-->'+JSON.stringify($scope.plan));
                        $scope.coverage = _.findWhere($scope.plan.coverages, {coverageId: searchRider.coverageId});
                        //console.log("COverage.."+JSON.stringify($scope.coverage));
                        //console.log('Coverage..'+JSON.stringify($scope.coverage));
                        return $scope.coverage.coverageSumAssured.sumAssuredType;

                    }
                }

               $scope.$watch('proposalPlanDetail.sumAssured', function (newval) {
                    if ($scope.coverage && $scope.coverage.coverageSumAssured.sumAssuredType === 'DERIVED') {
                        $scope.searchRider.sumAssured=newval *($scope.coverage.coverageSumAssured.percentage/100);
                        console.log('Derived Type Came..'+$scope.coverage.coverageSumAssured.percentage);
                    }

                });

            }]
        }
    })

    .directive('sumassured', function ($compile) {
        return {
            templateUrl: 'plan-sumassured.tpl',
            // element must have ng-model attribute.
            require: 'ngModel',
            link: function (scope, elem, attrs, ctrl) {
                if (!ctrl)return;

            }
        };
    })

    .config(['datepickerPopupConfig', function (datepickerPopupConfig) {
        datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
        datepickerPopupConfig.currentText = 'Today';
        datepickerPopupConfig.clearText = 'Clear';
        datepickerPopupConfig.closeText = 'Done';
        datepickerPopupConfig.closeOnDateSelection = true;
    }])
    .controller('createProposalCtrl', ['$scope', 'resources', 'getQueryParameter', '$bsmodal', '$http', '$window',
        'globalConstants', 'ProposalService', '$upload',
        function ($scope, resources, getQueryParameter, $bsmodal, $http, $window, globalConstants, ProposalService, $upload, $route) {

            //console.log('create proposal');
            $scope.stepsSaved = {"1": false, "2": false, "3": false, "4": false, "5": false,"6":false,"7":false,"8":false,"9":false,"10":false};

            $scope.employmentTypes = [];
            $scope.checkEmpTypes = null;
            $scope.occupations = [];
            $scope.provinces = [];
            $scope.bankDetailsResponse = [];
            $scope.documentList = [];
            $scope.proposal = {};
            $scope.searchRiders = [];
            $scope.proposalId = getQueryParameter('proposalId')
            $scope.quotationId = getQueryParameter('quotationId');
            $scope.proposalPlanDetail = {};
            $scope.searchRider = {};
            $scope.tempCoverages = {}; //meant For Temp
            $scope.premiumResponse = {};

            $scope.isBrowseDisable=function(document)
            {
                if(document.fileName == null && document.submitted)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }

            $scope.quotationIdDetails =
            {
                "quotationId": null
            };
            $scope.todayDate = new Date();
            //console.log("Proposal Id sent is:" + $scope.proposalId);
            $scope.mode = getQueryParameter('mode');
            $scope.method = getQueryParameter('method');
            //alert('Method Type is: '+$scope.method);

            //$scope.methodCheckStatus=false;

            $scope.returnStatusCheck = false;
            var statusReturn = getQueryParameter('status');

            // Below 6 variables are  meant for apperance status of Reject,Hold & Forward Buttons
            $scope.underWritingLevelOneStatus = false;
            $scope.pendingAcceptanceStatus = false;
            $scope.underWritingLevelTwoStatus = false;
            $scope.rejectStatus = false;
            $scope.forwardStatus = false;
            $scope.holdStatus = false;

            //console.log('modeType' + $scope.mode);
            $scope.quotationStatus = "GENERATED";
            $scope.isPlanTextField = false; // Plan DropDown

            if ($scope.quotationId) {
                //console.log('Navigate to quotation Window...');
                //alert("Quotation Id");
                $http.get("/pla/individuallife/quotation/getquotation/" + $scope.quotationId).success(function (response, status, headers, config) {
                    var viewQuotationOutput = response;
                    $scope.isPlanTextField = true;
                    if (response.planDetail) {
                        if (response.planDetail.planDetail) {
                            $scope.ProposerplanDetail = response.planDetail.planDetail;
                        }
                    }
                    if (response.assuredTheProposer) {
                        $scope.proposerEmployment.occupation = response.proposedAssured.occupation;
                    }
                    //alert("assuredTheProposer"+response.assuredTheProposer);
                    console.log("View Quotation " + JSON.stringify(response));
                    $scope.rcvProposalDetailQid = response;
                    //console.log('****** Checking quotationDetails...' + $scope.rcvProposalDetailQid.quotationId.quotationId);
                    $scope.quotationIdDetails = $scope.rcvProposalDetailQid.quotationId;
                    //console.log('viewQuotationOutput' + JSON.stringify(viewQuotationOutput));
                    $scope.proposedAssured = $scope.rcvProposalDetailQid.proposedAssured || {};
                    //$scope.proposerEmployment.occupation=$scope.proposedAssured.occupation;
                    $scope.proposedAssured.nrc = $scope.rcvProposalDetailQid.proposedAssured.nrc || {};
                    $scope.proposedAssured.isProposer = $scope.rcvProposalDetailQid.assuredTheProposer;

                    if ($scope.rcvProposalDetailQid.proposer != null) {
                        $scope.proposer = $scope.rcvProposalDetailQid.proposer || {};
                        $scope.proposer.isProposedAssured = response.assuredTheProposer
                        $scope.proposer.nrc = $scope.rcvProposalDetailQid.proposer.nrc || {};
                        if ($scope.proposer.dateOfBirth) {
                            $scope.proposer.nextDob = moment().diff(new moment(new Date($scope.proposer.dateOfBirth)), 'years') + 1;
                        }

                    }
                    if ($scope.rcvProposalDetailQid.proposalPlanDetail != null) {
                        $scope.proposalPlanDetail = $scope.rcvProposalDetailQid.proposalPlanDetail;
                    }
                    $scope.agent = $scope.rcvProposalDetailQid.agentDetail;
                    var selectedPlan = {};
                    $scope.planDetailDto = response.planDetailDto;

                    if (response.planDetail != null) {
                        selectedPlan.title = response.planDetail.planDetail.planName;
                        selectedPlan.description = response.planDetail;
                        $scope.selectedPlan = selectedPlan;
                        //$scope.proposalPlanDetail.planId=response.proposalPlanDetail.planId;
                    }
                    $scope.agentDetails.push($scope.agent);
                }).error(function (response, status, headers, config) {
                });
            }

            /*$scope.dateFormatValidator=function(rcvDate)
             {
             //alert(rcvDate);

             if (!moment(rcvDate,'DD/MM/YYYY').isValid()) {
             var newDateField = moment(rcvDate).format("DD/MM/YYYY");
             alert(newDateField);
             //$scope.row.date=newDateField;
             }
             }
             */
            $scope.getCoverageTermType = function (searchRider) {
                alert(JSON.stringify(searchRider));
                if ($scope.plan) {
                    //alert('Inside Plan..');
                    var coverage = _.findWhere($scope.plan.coverages, {coverageId: searchRider.coverageId});
                    // //console.log("Coverage Details..."+JSON.stringify(coverage));
                    //var ageNextBirthday = calculateAge($scope.proposedAssured.dateOfBirth);
                    var ageNextBirthday = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                    if (coverage.coverageTermType === 'SPECIFIED_VALUES') {
                        var maxMaturityAge = coverage.coverageTerm.maxMaturityAge || 1000;
                        $scope.policyTerms = _.filter(coverage.coverageTerm.validTerms, function (term) {
                            return ageNextBirthday + term.text <= maxMaturityAge;
                        });
                    } else if (coverage.coverageTermType === 'AGE_DEPENDENT') {
                        $scope.policyTerms = _.filter(coverage.coverageTerm.maturityAges, function (term) {
                            return term.text > ageNextBirthday;
                        });
                    }

                    /* else if (coverage.coverageTermType === 'POLICY_TERM') {
                     $scope.policyTerms = _.filter(coverage.coverageTerm.maturityAges, function (term) {
                     return term.text > ageNextBirthday;
                     });
                     }
                     */
                    return coverage.coverageTermType;
                } else {
                    return ""
                }
            }

            /*$scope.$watch('replacement.answer',function(newVal,oldVal){

             if(newVal)
             {
             //alert('true');
             $scope.replacement.answer = 'true';
             }
             else{
             $scope.replacement.answer = 'false';
             }
             });*/
            $scope.cancelAll = function () {
                //window.location.href = "/pla/individuallife/proposal/search"

                if($scope.method == 'approval'){
                 window.location.href = "/pla/individuallife/proposal/openapprovalproposal"
                 }
                 else
                 {
                 window.location.href = "/pla/individuallife/proposal/search"
                 }

            }
            if ($scope.proposalId) {
                $http.get("/pla/individuallife/proposal/getproposal/" + $scope.proposalId + "?mode=view").success(function (response, status, headers, config) {
                    $scope.selectedWizard = 1;
                    var result = response;
                    console.log("ProposalReceive.." + JSON.stringify(response));

                    if(response.hasQuotationNumber){
                        $scope.isPlanTextField=true;
                    }
                    else
                    {
                        $scope.isPlanTextField=false;
                    }

                    if (response.proposer.isProposedAssured) {
                        var age = response.proposedAssured.dateOfBirth;
                        var ageNextBirthday = moment().diff(new moment(new Date(age)), 'years') + 1;
                    }

                    if (response.proposalPlanDetail != null) {
                        //It is Only meant to show the PlanName to User
                        //$scope.ProposerplanDetail.planId=response.proposalPlanDetail.planName;
                        $scope.ProposerplanDetail = response.proposalPlanDetail;

                        $http.get('/pla/core/plan/getPlanById/' + response.proposalPlanDetail.planId)
                            .success(function (plandata) {
                                console.log('PlanDetails..' + JSON.stringify(plandata));
                                $scope.plan = plandata;
                                //console.log("Plan Details.."+JSON.stringify($scope.plan));

                                //$scope.applicableRelationships=plandata.planDetail.applicableRelationships;

                                if (response.proposer.isProposedAssured != true) {
                                    for (i in $scope.plan.planDetail.applicableRelationships) {
                                        if ($scope.plan.planDetail.applicableRelationships[i] == 'SELF') {
                                            console.log('SelfCheck..' + $scope.plan.planDetail.applicableRelationships[i]);
                                            $scope.plan.planDetail.applicableRelationships.splice(i, 1);
                                        }
                                    }
                                    $scope.relationshipList = $scope.plan.planDetail.applicableRelationships;
                                }
                                else {
                                    var applicableRelation = [];
                                    // Add Only Self on that list
                                    for (i in $scope.plan.planDetail.applicableRelationships) {
                                        if ($scope.plan.planDetail.applicableRelationships[i] == 'SELF') {
                                            //console.log('SelfCheck..'+$scope.plan.planDetail.applicableRelationships[i]);
                                            applicableRelation.push($scope.plan.planDetail.applicableRelationships[i]);
                                        }
                                    }
                                    $scope.relationshipList = applicableRelation;
                                    $scope.proposedAssured.relationshipId = applicableRelation[0];
                                }

                            });
                    }
                    if (response.proposalStatus != null) {
                        if (response.proposalStatus == 'UNDERWRITING_LEVEL_ONE') {
                            $scope.underWritingLevelOneStatus = true;
                        }
                        else if (response.proposalStatus == 'PENDING_ACCEPTANCE') {
                            $scope.pendingAcceptanceStatus = true;
                        }
                        else if (response.proposalStatus == 'UNDERWRITING_LEVEL_TWO') {
                            $scope.underWritingLevelTwoStatus = true;
                        }
                        else {
                            $scope.underWritingLevelOneStatus = false;
                            $scope.pendingAcceptanceStatus = false;
                            $scope.underWritingLevelTwoStatus = false;
                        }
                    }
                    if ($scope.pendingAcceptanceStatus) {
                        $scope.rejectStatus = false;
                        $scope.forwardStatus = false;
                        $scope.holdStatus = false;
                    }
                    else if ($scope.underWritingLevelOneStatus) {
                        $scope.rejectStatus = true;
                        $scope.forwardStatus = true;
                        $scope.holdStatus = true;
                    }
                    else if ($scope.underWritingLevelTwoStatus) {
                        $scope.rejectStatus = true;
                        $scope.forwardStatus = false;
                        $scope.holdStatus = true;
                    }
                    else {
                        $scope.rejectStatus = false;
                        $scope.forwardStatus = false;
                        $scope.holdStatus = false;
                    }
                    $scope.agentDetails = result.agentCommissionDetails;
                    $scope.rcvProposal = response;
                    $scope.proposalNumberDetails.proposalNumber = $scope.rcvProposal.proposalNumber;
                    $scope.proposal =
                    {
                        "msg": null,
                        "proposalId": null
                    };
                    $scope.proposal.proposalId = $scope.rcvProposal.proposalId;

                    $http.get("getmandatorydocuments/" + $scope.proposal.proposalId)
                        .success(function (response) {
                            $scope.documentList = response;
                        });

                    $http.get("getadditionaldocuments/" + $scope.proposal.proposalId).success(function (data, status) {
                        //console.log(data);
                        $scope.additionalDocumentList = data;
                        $scope.checkDocumentAttached = $scope.additionalDocumentList != null;
                    });
                    $scope.switchBool = function (value) {
                        $scope[value] = !$scope[value];
                    }
                    $scope.serverErrMsg = "";
                    $scope.selectedWizard = 1;
                    $scope.serverError=false;

                    if ($scope.selectedWizard == 1) {
                        $http.get('getpremiumdetail/' + $scope.proposal.proposalId)
                            .success(function (response) {
                                console.log('success ***');
                                $scope.premiumResponse = response;
                                $scope.serverErrMsg = response.message;
                            }).error(function (response) {
                                $scope.serverErrMsg = response.message;
                                $scope.serverErrMsg = response.message;
                                $scope.serverError=true;
                                $scope.stepsSaved["3"] = false;
                            });
                    }
                    if ($scope.rcvProposal.proposer != null) {
                        $scope.stepsSaved["1"] = true;
                        $scope.proposer = $scope.rcvProposal.proposer;
                        console.log($scope.rcvProposal);
                        if ($scope.rcvProposal.proposer.gender) {
                            var gender = $scope.rcvProposal.proposer.gender;
                            $scope.proposer.gender = gender;

                        }

                        $scope.proposerEmployment = $scope.proposer.employment;

                        $scope.proposerResidential = $scope.proposer.residentialAddress;
                        $scope.proposerSpouse = $scope.proposer.spouse;

                        if ($scope.proposer.dateOfBirth) {
                            $scope.proposer.nextDob = moment().diff(new moment(new Date($scope.proposer.dateOfBirth)), 'years') + 1;
                        }
                    }

                    if ($scope.rcvProposal.proposalPlanDetail != null) {
                        $scope.proposalPlanDetail = $scope.rcvProposal.proposalPlanDetail;
                    }
                    var selectedPlan = {};

                    if (response.proposalPlanDetail != null) {
                        $scope.selectPlanResponse = true;
                        selectedPlan.title = response.proposalPlanDetail.planName;
                        selectedPlan.description = response.planDetail;
                        $scope.selectedPlan = selectedPlan;
                        $scope.proposalPlanDetail.planId = response.proposalPlanDetail.planId;
                    }
                    if ($scope.proposalPlanDetail != null && $scope.proposalPlanDetail.riderDetails != null) {
                        $scope.searchRiders = $scope.rcvProposal.proposalPlanDetail.riderDetails;
                    }
                    if ($scope.rcvProposal.beneficiaries != null && !$scope.serverError) {
                        $scope.stepsSaved["3"] = true;
                        $scope.beneficiariesList = $scope.rcvProposal.beneficiaries;
                    }
                    if ($scope.rcvProposal.generalDetails != null) {
                        $scope.stepsSaved["4"] = true;
                        console.log("generalDetails:-->" + JSON.stringify($scope.rcvProposal.generalDetails));

                        if ($scope.rcvProposal.generalDetails.assuredByPLAL != null) {
                            if ($scope.rcvProposal.generalDetails.assuredByPLAL.answer) {
                                $scope.assuredByPlalList = "YES";
                            }
                            else {
                                $scope.assuredByPlalList = "NO";
                            }
                            $scope.policyDetails = $scope.rcvProposal.generalDetails.assuredByPLAL.answerResponse;
                        }

                        if ($scope.rcvProposal.generalDetails.assuredByOthers != null) {
                            if ($scope.rcvProposal.generalDetails.assuredByOthers.answer) {
                                $scope.assuredByOthers = "YES";
                            }
                            else {
                                $scope.assuredByOthers = "NO";
                            }
                            $scope.insurerDetails1 = $scope.rcvProposal.generalDetails.assuredByOthers.answerResponse;
                        }

                        if ($scope.rcvProposal.generalDetails.pendingInsuranceByOthers != null) {
                            if ($scope.rcvProposal.generalDetails.pendingInsuranceByOthers.answer) {
                                $scope.pendingInsuranceByOthers = "YES";
                            }
                            else {
                                $scope.pendingInsuranceByOthers = "NO";
                            }
                            $scope.insurerDetails2 = $scope.rcvProposal.generalDetails.pendingInsuranceByOthers.answerResponse;
                        }

                        if ($scope.rcvProposal.generalDetails.assuranceDeclined != null) {
                            if ($scope.rcvProposal.generalDetails.assuranceDeclined.answer) {
                                $scope.assuranceDeclined = "YES";
                            }
                            else {
                                $scope.assuranceDeclined = "NO";
                            }
                            $scope.insurerDetails3 = $scope.rcvProposal.generalDetails.assuranceDeclined.answerResponse;
                        }

                        if ($scope.rcvProposal.generalDetails.questionAndAnswers) {
                            for (i in $scope.rcvProposal.generalDetails.questionAndAnswers) {
                                if ($scope.rcvProposal.generalDetails.questionAndAnswers[i].answer) {
                                    $scope.rcvProposal.generalDetails.questionAndAnswers[i].answer = "true";
                                }
                                else {
                                    $scope.rcvProposal.generalDetails.questionAndAnswers[i].answer = "false";
                                }
                            }
                            $scope.generalQuestion = $scope.rcvProposal.generalDetails.questionAndAnswers;
                        }
                    }

                    if ($scope.rcvProposal.additionaldetails != null) {

                        $scope.stepsSaved["8"] = true;

                        $scope.medicalAttendant.medicalAttendantDetails = $scope.rcvProposal.additionaldetails.medicalAttendantDetails;
                        $scope.medicalAttendant.medicalAttendantDuration = $scope.rcvProposal.additionaldetails.medicalAttendantDuration;
                        $scope.medicalAttendant.dateAndReason = $scope.rcvProposal.additionaldetails.dateAndReason;

                        if ($scope.rcvProposal.additionaldetails.replacementDetails != null) {
                            $scope.replacement = $scope.rcvProposal.additionaldetails.replacementDetails;

                            if ($scope.rcvProposal.additionaldetails.replacementDetails.answer) {
                                $scope.replacement.answer = "true";
                            }
                            else {
                                $scope.replacement.answer = "false";
                            }
                        }
                    }

                    if ($scope.rcvProposal.premiumPaymentDetails != null) {

                        $scope.stepsSaved["9"] = true;

                        $scope.premiumPaymentDetails.premiumFrequency = $scope.rcvProposal.premiumPaymentDetails.premiumFrequency;
                        $scope.premiumPaymentDetails.premiumPaymentMethod = $scope.rcvProposal.premiumPaymentDetails.premiumPaymentMethod;
                        $scope.premiumPaymentDetails.proposalSignDate = $scope.rcvProposal.premiumPaymentDetails.proposalSignDate;
                        $scope.premiumPaymentDetails.premiumFrequencyPayable = $scope.rcvProposal.premiumPaymentDetails.premiumFrequencyPayable;

                        if ($scope.rcvProposal.premiumPaymentDetails.employerDetails != null) {
                            $scope.premiumEmployerDetails = $scope.rcvProposal.premiumPaymentDetails.employerDetails;
                        }

                        if ($scope.rcvProposal.premiumPaymentDetails.bankDetails != null) {
                            $scope.bankDetails = $scope.rcvProposal.premiumPaymentDetails.bankDetails;
                            if ($scope.bankDetails) {
                                var bankCode = _.findWhere($scope.bankDetailsResponse, {bankName: $scope.bankDetails.bankName});
                                if (bankCode) {
                                    $http.get('/pla/individuallife/proposal/getAllBankBranchNames/' + bankCode.bankCode).success(function (response, status, headers, config) {
                                        $scope.bankBranchDetails = response;
                                    }).error(function (response, status, headers, config) {
                                    });
                                }
                            }
                            $scope.bankDetails.bankBranchName = $scope.rcvProposal.premiumPaymentDetails.bankDetails.bankBranchName;
                        }
                    }

                    if ($scope.rcvProposal.proposedAssured != null) {

                        $scope.stepsSaved["2"] = true;

                        $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};
                        if ($scope.proposedAssured.dateOfBirth) {
                            $scope.proposedAssured.nextDob = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                        }

                        /**
                         * If riderDetails are not Coming Then retrive riderDetails
                         */

                        if ($scope.rcvProposal.proposalPlanDetail.riderDetails == null) {
                            //alert('Testing..');
                            //alert($scope.rcvProposal.proposalPlanDetail.riderDetails);
                            /**
                             *
                             * Calling RiderDetails...
                             */

                            var age = $scope.proposedAssured.dateOfBirth;
                            var ageNextBirthday = moment().diff(new moment(new Date(age)), 'years') + 1;
                            //alert(ageNextBirthday);

                            $http.get("getridersforplan/" + response.proposalPlanDetail.planId + "/" + ageNextBirthday).success(function (response, status, headers, config) {
                                $scope.searchRiders = response;
                                console.log('RiderDetails1..' + JSON.stringify($scope.searchRiders));
                                $scope.proposalPlanDetail.riderDetails = response;
                                //console.log('Riders Details From Db is:' +JSON.stringify(response));
                                //console.log($scope.searchRiders);

                            }).error(function (response, status, headers, config) {
                                var check = status;
                            });
                        }

                        $scope.spouse = $scope.rcvProposal.proposedAssured.spouse;
                        $scope.employment = $scope.rcvProposal.proposedAssured.employment;
                        $scope.residentialAddress = $scope.rcvProposal.proposedAssured.residentialAddress;
                        $scope.agentDetails = $scope.rcvProposal.agentCommissionDetails;
                    }
                    if ($scope.rcvProposal.familyPersonalDetail != null) {
                            $scope.stepsSaved["7"] = true;

                        if ($scope.rcvProposal.familyPersonalDetail.isPregnant) {
                            $scope.rcvProposal.familyPersonalDetail.isPregnant = "true";
                        }
                        else {
                            $scope.rcvProposal.familyPersonalDetail.isPregnant = "false";
                        }
                        $scope.familyPersonalDetail = $scope.rcvProposal.familyPersonalDetail;
                        $scope.familyHistory = $scope.rcvProposal.familyPersonalDetail.familyHistory;
                        if ($scope.familyHistory) {
                            if ($scope.familyHistory.closeRelative.answer) {
                                $scope.familyHistory.closeRelative.answer = 'true';
                            }
                            else {
                                $scope.familyHistory.closeRelative.answer = 'false';
                            }
                        }
                        $scope.habit = $scope.rcvProposal.familyPersonalDetail.habit;
                        $scope.habits = $scope.rcvProposal.familyPersonalDetail.habit;

                        if ($scope.rcvProposal.familyPersonalDetail.habit.questions) {
                            for (i in $scope.rcvProposal.familyPersonalDetail.habit.questions) {
                                if ($scope.rcvProposal.familyPersonalDetail.habit.questions[i].answer) {
                                    $scope.rcvProposal.familyPersonalDetail.habit.questions[i].answer = "true";
                                }
                                else {
                                    $scope.rcvProposal.familyPersonalDetail.habit.questions[i].answer = "false";
                                }
                            }
                            $scope.questionList = $scope.rcvProposal.familyPersonalDetail.habit.questions;
                        }
                        if ($scope.rcvProposal.familyPersonalDetail.build) {
                            if ($scope.rcvProposal.familyPersonalDetail.build.overWeightQuestion.answer) {
                                $scope.rcvProposal.familyPersonalDetail.build.overWeightQuestion.answer = "true";
                            }
                            else {
                                $scope.rcvProposal.familyPersonalDetail.build.overWeightQuestion.answer = "false";
                            }

                            $scope.build = $scope.rcvProposal.familyPersonalDetail.build;
                        }
                    }
                    if ($scope.rcvProposal.compulsoryHealthStatement != null) {
                        if($scope.rcvProposal.compulsoryHealthStatement.length <=7){
                            $scope.stepsSaved["5"] = true;
                        }
                        else
                        {
                            $scope.stepsSaved["5"] = true;
                            $scope.stepsSaved["6"] = true;
                        }

                        for (i in $scope.rcvProposal.compulsoryHealthStatement) {
                            if ($scope.rcvProposal.compulsoryHealthStatement[i].answer) {
                                $scope.rcvProposal.compulsoryHealthStatement[i].answer = "true";
                            }
                            else {
                                $scope.rcvProposal.compulsoryHealthStatement[i].answer = "false";
                            }
                        }

                        $scope.compulsoryHealthDetails = $scope.rcvProposal.compulsoryHealthStatement;
                    }

                }).error(function (response, status, headers, config) {
                });
            }

            $scope.getPolicyTermVal = function (policyTerm) {
                for (i in $scope.plan.coverages) {
                    if ($scope.plan.coverages[i].coverageTermType == 'POLICY_TERM') {
                        alert($scope.plan.coverages[i].coverageTermType);
                        $scope.riderDetails[i].coverTerm = policyTerm;
                    }
                }
            }


            $http.get('/pla/individuallife/proposal/getAllOccupation').success(function (response, status, headers, config) {
                $scope.occupations = response;
            }).error(function (response, status, headers, config) {
            });


            $http.get('/pla/individuallife/proposal/getAllEmploymentType').success(function (response, status, headers, config) {
                $scope.employmentTypes = response;
            }).error(function (response, status, headers, config) {
            });

            $scope.iLplanDetails = [];
            $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                $scope.provinces = response;
            }).error(function (response, status, headers, config) {
            });

            $http.get('/pla/individuallife/proposal/getAllBankNames').success(function (response, status, headers, config) {
                $scope.bankDetailsResponse = response;
            }).error(function (response, status, headers, config) {
            });

            $scope.bankCodeDetails = [];
            $scope.clearBankBranchName = function () {
                $scope.bankDetails.bankBranchName = null;
            }
            $scope.$watch('bankDetails.bankName', function (newvalue, oldvalue) {
                if (newvalue) {
                    var bankCode = _.findWhere($scope.bankDetailsResponse, {bankName: newvalue});
                    // //alert("Bank Details.."+JSON.stringify(bankCode));
                    if (bankCode) {
                        $http.get('/pla/individuallife/proposal/getAllBankBranchNames/' + bankCode.bankCode).success(function (response, status, headers, config) {
                            $scope.bankBranchDetails = response;
                            //console.log("Bank Details :"+JSON.stringify(response));
                        }).error(function (response, status, headers, config) {
                        });
                    }
                }
            });

            $scope.bankCodeDetails = [];

            $scope.$watch('bankDetails.bankBranchName', function (newvalue, oldvalue) {
                if (newvalue) {
                    var bankBranchNames = _.findWhere($scope.bankBranchDetails, {branchName: newvalue});
                    if (bankBranchNames) {
                        $scope.bankDetails.bankBranchSortCode = bankBranchNames.sortCode;
                    }
                }
            });

            $scope.titles = globalConstants.title;
            $scope.part = {
                isPart: true
            };

            $scope.selectedPlan = {};
            $scope.selectedWizard = 1;

            $scope.premiumEmployerDetails =
            {
                "employeeId": null,

                "manNumber": null,

                "companyNameAndPostalAddress": null,

                "basicSalary": null,

                "salaryPer": null

            };

            $scope.premiumPaymentDetails =
            {
                "premiumFrequency": null,
                "premiumPaymentMethod": null
            }

            $scope.bankDetails =
            {
                "bankBranchSortCode": null,

                "bankName": null,

                "bankBranchName": null,

                "bankAccountNumber": null,

                "bankAccountType": null
            };

            $scope.proposedAssured = {
                "title": null,
                "firstName": null,
                "surname": null,
                "otherName": null,
                "nrc": null,
                "dateOfBirth": null,
                "gender": null,
                "mobileNumber": null,
                "emailAddress": null,
                "maritalStatus": null,
                "nextDob": null,
                "isProposer": null
            };

            $scope.getTheItemSelected = function (ele) {
                viewILProposalModule.getTheItemSelected(ele);
            };

            $scope.titleList = globalConstants.title;
            $scope.genderList = globalConstants.gender;

            $scope.savePremiumDetail = function () {
                $scope.premiumResponse.planId = $scope.proposalPlanDetail.planId;
                var premiumRequest =
                {
                    //"premiumPaymentDetails":$scope.premiumPaymentDetails,
                    "premiumDetail": $scope.premiumResponse,
                    "employerDetails": $scope.premiumEmployerDetails,
                    "bankDetails": $scope.bankDetails
                }
                premiumRequest = angular.extend($scope.premiumPaymentDetails, premiumRequest);

                premiumRequest =
                {
                    "premiumPaymentDetails": premiumRequest,
                    "proposalId": $scope.proposal.proposalId
                };
                $http.post('updatepremiumpaymentdetails', premiumRequest).success(function (response, status, headers, config) {

                    $scope.stepsSaved[$scope.selectedWizard] = true;

                    $scope.proposal.proposalId = response.id;
                }).error(function (response, status, headers, config) {
                });
            }
            $scope.getpremiumFrequency = function (newVal) {
                if (newVal == 'QUARTERLY') {

                    $scope.premiumPaymentDetails.premiumFrequencyPayable = $scope.premiumResponse.quarterlyPremium;
                }
                else if (newVal == 'ANNUALLY') {
                    $scope.premiumPaymentDetails.premiumFrequencyPayable = $scope.premiumResponse.annualPremium;
                }
                else if (newVal == 'SEMI_ANNUALLY') {
                    $scope.premiumPaymentDetails.premiumFrequencyPayable = $scope.premiumResponse.semiannualPremium;
                }

                else if (newVal == 'MONTHLY') {
                    $scope.premiumPaymentDetails.premiumFrequencyPayable = $scope.premiumResponse.monthlyPremium;
                }

            }
            $scope.addAdditionalDocument = function () {
                $scope.additionalDocumentList.unshift({});
                $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();

            };

            $scope.removeAdditionalDocument = function (index) {
                $scope.additionalDocumentList.splice(index, 1);
                $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
            };

            $scope.callAdditionalDoc = function (file) {
                if (file[0]) {
                    $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
                }
            }
            $scope.additionalDocumentList = [{}];
            //$scope.documentList = documentList;
            $scope.documentList = [];

            $scope.isUploadEnabledForAdditionalDocument = function () {
                var enableAdditionalUploadButton = ($scope.additionalDocumentList != null);
                for (var i = 0; i < $scope.additionalDocumentList.length; i++) {
                    var document = $scope.additionalDocumentList[i];
                    var files = document.documentAttached;
                    //alert(i+"--"+files)
                    //alert(i+"--"+document.content);
                    if (!(files || document.content)) {
                        enableAdditionalUploadButton = false;
                        break;
                    }
                }
                return enableAdditionalUploadButton;
            }

            $scope.returnComment = '';
            $scope.showSubmitStatus = false;

            $scope.setComments = function (comments) {
                if (comments != null) {
                    $scope.showSubmitStatus = false;
                }
                else {
                    $scope.showSubmitStatus = true;
                }
                $scope.returnComment = comments;
            }
            $scope.submitAdditionalDocument = function () {
                $http.post('submit', angular.extend({},
                    {
                        "proposalId": $scope.proposal.proposalId,
                        "comment": $scope.returnComment
                    })).success(function (data) {
                });

            }

            $scope.updateFiles = function () {
                var requestObj = {
                    "waiverByApproved": $scope.waiverByApproved,
                    "proposalId": $scope.proposal.proposalId
                }
                console.log('FinalUpdateObj: ' + JSON.stringify(requestObj));

                if ($scope.waiverByApproved.length > 0) {
                    // call The URL to send the requestObject
                }
                else {
                    alert("Please Select the CheckBoxes...");
                }
            }

            $scope.waiverByApproved = [];
            $scope.getMandatoryDocumentDetials = function ($event, document) {
                var checkbox = $event.target;
                if (checkbox.checked) {
                    $scope.waiverByApproved.push({
                        documentId: document.documentId,
                        documentName: document.documentName,
                        mandatory: true,
                        isApproved: true
                    });
                }
                else {
                    for (i in $scope.waiverByApproved) {
                        if ($scope.waiverByApproved[i].documentName == document.documentName) {
                            $scope.waiverByApproved.splice(i, 1);
                        }
                    }
                }
            }

            $scope.updateMandatoryDocumentForApproval = function () {
                var requestForApproval =
                {
                    "waivedDocuments": $scope.waiverByApproved,
                    "proposalId": $scope.proposal.proposalId
                }

                $http.post('waivedocument', requestForApproval).success(function (response, status, headers, config) {
                }).error(function (response, status, headers, config) {
                });

            }
            $scope.uploadDocumentFiles = function () {
                // //console.log($scope.documentList.length);
                for (var i = 0; i < $scope.documentList.length; i++) {
                    var document = $scope.documentList[i];
                    var files = document.documentAttached;
                    //console.dir(files);
                    // //alert(files.name);
                    if (files) {
                        //console.log('File Uploading....');
                        $upload.upload({
                            url: '/pla/individuallife/proposal/uploadmandatorydocument',
                            file: files,
                            fields: {
                                documentId: document.documentId,
                                proposalId: $scope.proposal.proposalId,
                                mandatory: true,
                                isApproved: true
                            },
                            method: 'POST'
                        }).progress(function (evt) {

                        }).success(function (data, status, headers, config) {
                        });
                    }

                }

            };

            $scope.uploadAdditionalDocument = function () {
                for (var i = 0; i < $scope.additionalDocumentList.length; i++) {
                    var document = $scope.additionalDocumentList[i];
                    var files = document.documentAttached;
                    $scope.additional = true;
                    if (files) {
                        console.dir(files);
                        $upload.upload({
                            url: '/pla/individuallife/proposal/uploadmandatorydocument',
                            file: files,
                            fields: {
                                //documentId: document.documentName,
                                documentId: document.documentId,
                                proposalId: $scope.proposal.proposalId,
                                mandatory: false
                            },
                            method: 'POST'
                        }).progress(function (evt) {

                        }).success(function (data, status, headers, config) {
                        });
                    }
                }
            };

            if ($scope.documentList) {
                if ($scope.documentList.documentAttached) {
                    if ($scope.documentList.documentAttached.length == $scope.documentList.documentName.length) {
                        $scope.disableUploadButton = true;
                    } else {
                        $scope.disableUploadButton = false;
                    }
                }
            }

            var method = getQueryParameter("method");

            if (method == 'approval' || statusReturn == 'return') {
                $scope.isViewMode = true;

                if (statusReturn == 'return') {
                    $scope.returnStatusCheck = true;
                    $scope.showSubmitStatus = true;
                }
                $http.get("/pla/individuallife/proposal/getapprovercomments/" + $scope.proposalId).success(function (data, status) {
                    // //console.log(data);
                    $scope.approvalCommentList = data;
                });

            }

            $scope.comment = '';
            $scope.approveProposal = function (comment) {
                var request = angular.extend({comment: comment},
                    {"proposalId": $scope.proposalId}, {"status": "APPROVED"});

                $http.post('/pla/individuallife/proposal/approve', request).success(function (data) {
                    if (data.status == 200) {

                        $window.location.href = "/pla/individuallife/proposal/openapprovalproposal";

                    }

                }).error(function (response, status, headers, config) {
                });
                ;
            }

            $scope.returnProposal = function (comment) {
                var request = angular.extend({comment: comment}, {"proposalId": $scope.proposalId}, {"status": "RETURNED"});

                $http.post('/pla/individuallife/proposal/approve', request).success(function (data) {
                    if (data.status == 200) {
                        $window.location.href = "/pla/individuallife/proposal/openapprovalproposal";
                    }

                });
            }

            $scope.holdProposal = function (comment) {
                var request = angular.extend({"comment": comment}, {"proposalId": $scope.proposalId}, {"status": "PENDING_DECISION"});

                $http.post('/pla/individuallife/proposal/approve', request).success(function (data) {
                    if (data.status == 200) {
                        $window.location.href = "/pla/individuallife/proposal/openapprovalproposal";
                        //
                    }

                });
            }

            $scope.routeToNextLevel = function (comment) {
                var request = angular.extend({"comment": comment}, {"proposalId": $scope.proposalId}, {"status": "UNDERWRITING_LEVEL_TWO"});

                $http.post('/pla/individuallife/proposal/approve', request).success(function (data) {
                    if (data.status == 200) {
                        $window.location.href = "/pla/individuallife/proposal/openapprovalproposal";
                    }

                });
            }

            $scope.rejectProposal = function (comment) {
                var request = angular.extend({comment: comment}, {"proposalId": $scope.proposalId}, {"status": "DECLINED"});

                $http.post('/pla/individuallife/proposal/approve', request).success(function (data) {
                    if (data.status == 200) {
                        $window.location.href = "/pla/individuallife/proposal/openapprovalproposal";
                    }

                });
            }
            $scope.beneficiariesList = [];
            $scope.addBeneficiary = function (beneficiary) {
                if ($scope.beneficiariesList.length == 0) {
                    $scope.beneficiariesList.push(beneficiary);
                }

                else {
                    //alert('Length is greater Then 0');
                    var checkLoopNameStatus = "true";
                    for (i in $scope.beneficiariesList) {
                        if (beneficiary.nrc && $scope.beneficiariesList[i].nrc == beneficiary.nrc) {
                            checkLoopNameStatus = "false";
                            break;
                        } else if (($scope.beneficiariesList[i].firstName == beneficiary.firstName) &&
                            ($scope.beneficiariesList[i].gender == beneficiary.gender) && ((moment($scope.beneficiariesList[i].dateOfBirth).diff(moment(beneficiary.dateOfBirth), 'days')) == 0)) {
                            checkLoopNameStatus = "false";
                            break;
                        } else if (beneficiary.relationshipId == 'FATHER_IN_LAW' || beneficiary.relationshipId == 'MOTHER_IN_LAW' || beneficiary.relationshipId == 'FATHER' || beneficiary.relationshipId == 'MOTHER') {
                            if (($scope.beneficiariesList[i].relationshipId == beneficiary.relationshipId)) {
                                checkLoopNameStatus = "false";
                                break;
                            }
                        }
                    }

                    if (checkLoopNameStatus == "true") {
                        $scope.beneficiariesList.unshift(beneficiary);
                    } else {
                        alert("This record is already existing");
                    }
                }
                $scope.clear();
                $('#beneficialModal').modal('hide');
            };

            $scope.showDob = function (dob) {
                if (dob != null) {
                    $scope.proposedAssured.nextDob = moment().diff(new moment(new Date(dob)), 'years') + 1;
                }
            };

            $scope.addBeneficiaryStatusCheck = true;
            $scope.showBeneficiaryDob = function (dob) {
                if (dob != null) {
                    $scope.beneficiary.age = moment().diff(new moment(new Date(dob)), 'years');
                }
                $scope.addBeneficiaryStatusCheck = false;
                if (($scope.beneficiary.age >= 0) && ($scope.beneficiary.age < 18)) {
                    $scope.addBeneficiaryStatusCheck = false;
                }
                else {
                    $scope.addBeneficiaryStatusCheck = true;
                }
            };

            $scope.isTrusteeValid = true;
            $scope.showTrusteeAge = function (dob) {
                if (dob != null) {
                    $scope.beneficiary.trusteeDetail.age = moment().diff(new moment(new Date(dob)), 'years');
                }
                $scope.isTrusteeValid = false;
                //$scope.beneficiary.age = moment().diff(new moment(new Date(dob)), 'years');

                if ($scope.beneficiary.trusteeDetail.age < 18) {
                    $scope.isTrusteeValid = false;
                }
                else {
                    $scope.isTrusteeValid = true;
                }
            };


            /**
             *
             * Chceking the FirstName of Beneficiary and Trustee FirstName Same or Not
             */
            $scope.isBeneficiaryTrusteeNameSame = false;
            $scope.$watch('beneficiary.trusteeDetail.firstName', function (newVal, oldVal) {

                if (newVal) {
                    if (newVal == $scope.beneficiary.firstName) {
                        //alert('both are Same');
                        $scope.isBeneficiaryTrusteeNameSame = true;
                    }
                    else {
                        $scope.isBeneficiaryTrusteeNameSame = false;
                    }
                }

            });
            $scope.$watch('beneficiary.firstName', function (newVal, oldVal) {
                if (newVal) {

                    if (newVal == $scope.beneficiary.trusteeDetail.firstName) {
                        //alert('Both Are Same..');
                        $scope.isBeneficiaryTrusteeNameSame = true;
                    }
                    else {
                        $scope.isBeneficiaryTrusteeNameSame = false;
                    }
                }
            });
            /**
             *
             * Chceking the NRC Number of Beneficiary and Trustee are  Same or Not
             */
            $scope.isBeneficiaryTrusteeNRCSame = false;

            $scope.$watch('beneficiary.nrc', function (newVal, oldVal) {
                if (newVal) {

                    if (newVal == $scope.beneficiary.trusteeDetail.nrc) {
                        //alert('Both Are  Same..');
                        $scope.isBeneficiaryTrusteeNRCSame = true;
                    }
                    else {
                        $scope.isBeneficiaryTrusteeNRCSame = false;
                    }
                }
            });

            $scope.$watch('beneficiary.age', function (newVal, oldVal) {
                if (newVal) {
                    $http.get("getallrelations/" + newVal).success(function (response, status, headers, config) {
                        $scope.applicableRelationships = response;
                    }).error(function (response, status, headers, config) {
                    });
                }
            });
            $scope.$watch('beneficiary.trusteeDetail.nrc', function (newVal, oldVal) {
                if (newVal) {

                    if (newVal == $scope.beneficiary.nrc) {
                        //alert('Both Are Same..');
                        $scope.isBeneficiaryTrusteeNRCSame = true;
                    }
                    else {
                        $scope.isBeneficiaryTrusteeNRCSame = false;
                    }
                }
            })
            $scope.isBeneficiaryTrusteeDOBSame = false;
            $scope.$watch('beneficiary.trusteeDetail.dateOfBirth', function (newVal, oldVal) {
                if (newVal) {
                    if (((moment(newVal).diff(moment($scope.beneficiary.dateOfBirth), 'days')) == 0)) {
                        //alert('Both Are Same..');
                        $scope.isBeneficiaryTrusteeDOBSame = true;
                    }
                    else {
                        $scope.isBeneficiaryTrusteeDOBSame = false;
                    }
                }
            });
            $scope.$watch('beneficiary.dateOfBirth', function (newVal, oldVal) {

                if (newVal) {
                    if (newVal == $scope.beneficiary.trusteeDetail.dateOfBirth) {
                        $scope.isBeneficiaryTrusteeDOBSame = true;
                    }
                    else {
                        $scope.isBeneficiaryTrusteeDOBSame = false;
                    }
                }
            });

            $scope.showProposerDob = function (dob) {
                if (dob != null) {
                    $scope.proposer.nextDob = moment().diff(new moment(new Date(dob)), 'years') + 1;
                }
            };
            $scope.proposalPlanDetail =
            {
                "planId": null,
                "policyTerm": null,
                "premiumPaymentTerm": null,
                "sumAssured": null
            };

            $scope.isRiderValid=function()
            {
                var riderValid="true";

                /****
                 * Checking for Rider Details if rider details is coming from Server..
                 */
                 if($scope.proposalPlanDetail.riderDetails.length >0 ){

                     for(i in $scope.proposalPlanDetail.riderDetails){

                         if($scope.proposalPlanDetail.riderDetails[i].sumAssured == null && $scope.proposalPlanDetail.riderDetails[i].coverTerm == null){
                             riderValid="false";
                             break;
                         }
                         else if($scope.proposalPlanDetail.riderDetails[i].sumAssured != null && $scope.proposalPlanDetail.riderDetails[i].coverTerm != null){
                             riderValid="false";
                             break;
                         }
                         else{
                             riderValid="true";
                         }
                     }
                 }
                else{
                     riderValid="false";
                 }
                if(riderValid == "false"){
                    return false;
                }
                else
                {
                    return true;
                }

            }
            $scope.savePlanDetail = function () {
                if (($scope.proposedAssured.nextDob < $scope.plan.planDetail.minEntryAge) || ($scope.proposedAssured.nextDob > $scope.plan.planDetail.maxEntryAge)) {
                    alert("Please select proposedAssured date of birth in Between" + $scope.plan.planDetail.minEntryAge + "And" + $scope.plan.planDetail.maxEntryAge);
                    return;
                }
                var tempRequest = {
                    "riderDetails": $scope.searchRiders
                };
                tempRequest = angular.extend($scope.proposalPlanDetail, tempRequest);

                var request = {
                    "proposalPlanDetail": tempRequest,
                    "beneficiaries": $scope.beneficiariesList,
                    "proposalId": $scope.proposal.proposalId
                };
                //console.log('Final to Plan DB..' + JSON.stringify(request));
                $http.post('updateplan', request).success(function (response, status, headers, config) {
                    $scope.stepsSaved[$scope.selectedWizard] = true;
                    $scope.proposal = response;
                    $scope.proposal.proposalId = response.id;
                    $http.get('getpremiumdetail/' + $scope.proposal.proposalId)
                        .success(function (response) {
                            console.log('success ***');
                            $scope.premiumResponse = response;
                        }).error(function (response) {
                            $scope.stepsSaved[$scope.selectedWizard] = false;
                            $scope.serverErrMsg = response.message;
                            $scope.serverError=true;
                        });

                    $http.get("/pla/individuallife/proposal/getproposal/" + $scope.proposal.proposalId).success(function (planResponse, status, headers, config) {
                        var result = planResponse;
                        console.log('planResponse:' + JSON.stringify(planResponse));
                        $scope.rcvProposal = planResponse;
                        if ($scope.rcvProposal.proposer != null) {
                            $scope.stepsSaved["1"] = true;
                            $scope.proposer = $scope.rcvProposal.proposer;
                            if ($scope.rcvProposal.proposer.gender) {
                                var gender = $scope.rcvProposal.proposer.gender;
                                $scope.proposer.gender = gender;

                            }
                            $scope.proposerEmployment = $scope.proposer.employment;
                            $scope.proposerResidential = $scope.proposer.residentialAddress;
                            $scope.proposerSpouse = $scope.proposer.spouse;

                            if ($scope.proposer.dateOfBirth) {
                                $scope.proposer.nextDob = moment().diff(new moment(new Date($scope.proposer.dateOfBirth)), 'years') + 1;
                            }
                        }

                        if ($scope.rcvProposal.proposedAssured != null) {
                            $scope.stepsSaved["2"] = true;
                            $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};
                            if ($scope.proposedAssured.dateOfBirth) {
                                $scope.proposedAssured.nextDob = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                            }
                            $scope.spouse = $scope.rcvProposal.proposedAssured.spouse;
                            $scope.employment = $scope.rcvProposal.proposedAssured.employment;
                            $scope.residentialAddress = $scope.rcvProposal.proposedAssured.residentialAddress;
                            $scope.agentDetails = $scope.rcvProposal.agentCommissionDetails;
                        }

                        if ($scope.rcvProposal.proposalPlanDetail != null) {
                            $scope.proposalPlanDetail = $scope.rcvProposal.proposalPlanDetail;
                        }

                        var selectedPlan = {};

                        if (planResponse.proposalPlanDetail != null) {
                            $scope.selectPlanResponse = true;
                            $http.get('/pla/core/plan/getPlanById/' + planResponse.proposalPlanDetail.planId)
                                .success(function (plandata) {
                                    $scope.plan = plandata;
                                    $scope.applicableRelationships = plandata.planDetail.applicableRelationships;
                                });

                            selectedPlan.title = planResponse.proposalPlanDetail.planName;
                            selectedPlan.description = planResponse.planDetail;
                            $scope.selectedPlan = selectedPlan;
                            $scope.proposalPlanDetail.planId = planResponse.proposalPlanDetail.planId;
                        }

                        if ($scope.proposalPlanDetail != null && $scope.proposalPlanDetail.riderDetails != null) {
                            $scope.searchRiders = $scope.rcvProposal.proposalPlanDetail.riderDetails;

                        }
                        if ($scope.rcvProposal.beneficiaries != null) {
                            $scope.beneficiariesList = $scope.rcvProposal.beneficiaries;
                        }
                    });
                    $http.get("getmandatorydocuments/" + $scope.proposal.proposalId)
                        .success(function (response) {
                            $scope.documentList = response;
                        });

                }).error(function (response, status, headers, config) {
                });
            };

            $scope.$on('actionclicked.fu.wizard', function (name, event, data) {
                if (data.step == 1) {
                }
            });

            $scope.proposer = {
                "title": null,
                "firstName": null,
                "surname": null,
                "otherName": null,
                "nrc": null,
                "dateOfBirth": null,
                "gender": null,
                "mobileNumber": null,
                "emailAddress": null,
                "maritalStatus": null
            };
            $scope.proposerSpouse = {};
            $scope.proposerEmployment = {};
            $scope.proposerResidential = {};

            $scope.spouse =
            {
                "firstName": null,
                "surname": null,
                "mobileNumber": null,
                "emailAddress": null

            };
            $scope.employment =
            {
                "occupation": null,
                "employer": null,
                "employmentDate": null,
                "employmentType": null,
                "address1": null,
                "address2": null,
                "province": null,
                "postalCode": null,
                "town": null,
                "employmentType": null,
                "workPhone": null
            };
            $scope.residentialAddress =
            {
                "address1": null,
                "address2": null,
                "postalCode": null,
                "province": null,
                "town": null,
                "homePhone": null,
                "emailAddress": null
            };
            $scope.familyPersonalDetail = {isPregnant: null, pregnancyMonth: null};
            $scope.familyHistory = {father: {}, mother: {}, brother: {}, sister: {}, closeRelative: {}};
            $scope.build = {overWeightQuestion: {}};
            $scope.compulsoryHealthDetails = [];
            $scope.saveCompulsoryQuestionDetails = function () {
                var request =
                {
                    "compulsoryHealthDetails": $scope.compulsoryHealthDetails,
                    "proposalId": $scope.proposal.proposalId
                };
                console.log('Length..'+$scope.compulsoryHealthDetails.length);
                $http.post('/pla/individuallife/proposal/updatecompulsoryhealthstatement', request).success(function (response, status, headers, config) {
                    $scope.stepsSaved[$scope.selectedWizard] = true;

                }).error(function (response, status, headers, config) {
                });

            };
            $scope.questionList = [];
            $scope.habits = {};
            $scope.generalQuestion = [];
            $scope.saveGeneralDetails = function () {
                var dateConvert = null;

                for (i in $scope.insurerDetails1) {
                    //console.log('Testing..' + moment($scope.insurerDetails1[i].date, moment.ISO_8601));
                    //console.log('Date' + $scope.insurerDetails1[i].date);
                    if (moment($scope.insurerDetails1[i].date, 'dd/mm/yyyy').isValid()) {
                        $scope.insurerDetails1[i].date = moment($scope.insurerDetails1[i].date).format('YYYY-MM-DDTHH:mm:ss+0530')
                    }
                    else {
                    }
                }

                for (i in $scope.insurerDetails2) {
                    console.log('Testing..' + moment($scope.insurerDetails2[i].date, moment.ISO_8601));
                    console.log('Date' + $scope.insurerDetails2[i].date);

                    if (moment($scope.insurerDetails2[i].date, 'dd/mm/yyyy').isValid()) {
                        $scope.insurerDetails2[i].date = moment($scope.insurerDetails2[i].date).format('YYYY-MM-DDTHH:mm:ss+0530')
                    }
                    else {
                    }
                }

                var assuredByPLAL =
                {
                    "answerResponse": $scope.policyDetails,
                    "questionId": "1",
                    "answer": $scope.generalAnswerList[0]
                }
                var assuredByOthers =
                {
                    "answerResponse": $scope.insurerDetails1,
                    "questionId": "2",
                    "answer": $scope.generalAnswerList[1]
                }

                var pendingInsuranceByOthers =
                {
                    "answerResponse": $scope.insurerDetails2,
                    "questionId": "3",
                    "answer": $scope.generalAnswerList[2]
                }

                var assuranceDeclined =
                {
                    "answerResponse": $scope.insurerDetails3,
                    "questionId": "4",
                    "answer": $scope.generalAnswerList[3]
                }
                var generalQuestion = $scope.generalQuestion;
                var req =
                {
                    "assuredByPLAL": assuredByPLAL,
                    "assuredByOthers": assuredByOthers,
                    "pendingInsuranceByOthers": pendingInsuranceByOthers,
                    "assuranceDeclined": assuranceDeclined,
                    "generalQuestion": generalQuestion,
                    "proposalId": $scope.proposal.proposalId
                }
                console.log('Final General is' + JSON.stringify(req));
                $http.post('/pla/individuallife/proposal/updategeneraldetails', req).success(function (response, status, headers, config) {
                    $scope.stepsSaved[$scope.selectedWizard] = true;
                    $scope.occupations = response;
                    $scope.proposal.proposalId = response.id;

                }).error(function (response, status, headers, config) {
                });

            };

            $scope.saveFamilyHistory = function () {
                var listA = $scope.questionList;
                $scope.habit =
                {
                    "wine": $scope.habits.wine,
                    "beer": $scope.habits.beer,
                    "spirit": $scope.habits.spirit,
                    "smokePerDay": $scope.habits.smokePerDay,
                    "questions": listA
                };

                var request = {
                    "familyHistory": $scope.familyHistory,
                    "habit": $scope.habit,
                    "build": $scope.build
                };

                var listA = $scope.questionList;
                request = angular.extend($scope.familyPersonalDetail, request);
                request = {
                    "familyPersonalDetail": request,
                    "proposalId": $scope.proposal.proposalId
                };
                $http.post('/pla/individuallife/proposal/updatefamily', request).success(function (response, status, headers, config) {
                    $scope.stepsSaved[$scope.selectedWizard] = true;
                    $scope.proposal.proposalId = response.id;
                }).error(function (response, status, headers, config) {
                });

            }

            $scope.signDateLaunchPremium = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.signDateA = true;
            };

            $scope.launchassuredByOthersDate = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchinceptionDate = true;
            };

            $scope.pendingInsuranceByOthersTplDate = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.pendingDate = true;
            };

            $scope.launchProposedAssuredeDate = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob1 = true;
            };


            $scope.launchinceptionDateADob = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.inceptionDateA = true;
            };

            $scope.launchProposalDateADob = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.dateProposalA = true;
            };


            $scope.launchProposedAssuredeDob = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob = true;
            };

            $scope.launchProposerDate = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob2 = true;
            };

            $scope.launchProposerEmpDate = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob3 = true;
            };

            $scope.launchBeneficiaryDob = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob4 = true;
            };

            $scope.launchTrusteeDob = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.trusteeDobLaunch = true;
            };

            $scope.selectPlanResponse = false;

            $scope.printValue = function () {
                //console.log($scope.selectedPlan);
            }

            $scope.applicableRelationships = [];

            $scope.getMax = function () {
                return 999999.99;
            }
            $scope.getBeneficiaryMinAge = function () {
                return 1;
            }
            $scope.getBeneficiaryMaxAge = function () {
                return 16;
            }
            $scope.planSelected = function (newValue) {
                if (newValue && newValue.description && newValue.description.plan_id) {
                    $scope.selectPlanResponse = true

                    $http.get('/pla/core/plan/getPlanById/' + newValue.description.plan_id)
                        .success(function (response) {
                            $scope.plan = response;
                            $scope.applicableRelationships = response.planDetail.applicableRelationships;

                        });


                    $scope.proposalPlanDetail.planId = newValue.description.plan_id;
                    $http.get("getridersforplan/" + newValue.description.plan_id + "/" + $scope.proposedAssured.nextDob).success(function (response, status, headers, config) {
                        $scope.searchRiders = response;
                        //alert('planSelected..');
                        $scope.proposalPlanDetail.riderDetails = response;
                    }).error(function (response, status, headers, config) {
                        var check = status;
                    });
                }
            };

            $scope.termTypeCover = {};
            $scope.$watch('proposalPlanDetail.policyTerm', function (newValue, oldValue) {
                if (newValue) {

                    $scope.termTypeCover = newValue;
                    $scope.searchRider.coverTerm = $scope.termTypeCover;

                }
            });


            $scope.getTrusteeProvinceValue = function (province) {
                var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
                if (provinceDetails)
                    $scope.trusteeCities = provinceDetails.cities;
            }

            $scope.getpolicytermTypeValue = function () {
                return $scope.termTypeCover;
            }

            $scope.getProposerEmpProvinceValue = function (province) {
                var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
                if (provinceDetails)
                    $scope.proposerEmploymentCities = provinceDetails.cities;
            }

            $scope.getProposerResProvinceValue = function (province) {
                var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
                if (provinceDetails)
                    $scope.proposerResidentialCities = provinceDetails.cities;
            }

            $scope.getProposedAssuredEmpProvinceValue = function (province) {
                var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
                if (provinceDetails)
                    $scope.proposedAssuredEmploymentCities = provinceDetails.cities;
            }

            $scope.getProposedAssuredResProvinceValue = function (province) {
                var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
                if (provinceDetails)
                    $scope.proposedAssuredResidentialCities = provinceDetails.cities;
            }

            $scope.getProposeAssuredEmpType = function (empType) {
                var employeeTypes = _.findWhere($scope.employmentTypes, {code: empType});
                if (employeeTypes)
                    $scope.employment.employmentType = employeeTypes.employment_id;
            }

            $scope.getProposerEmpType = function (empType) {
                var employeeTypes = _.findWhere($scope.employmentTypes, {code: empType});
                if (employeeTypes)
                    $scope.proposerEmployment.employmentType = employeeTypes.employment_id;
            }
            $scope.selectSpouse = function (maritalStatusCheck) {
                if (maritalStatusCheck == "MARRIED") {
                    $scope.spouseChoice = true;
                }
                else {
                    $scope.spouseChoice = false;
                }
            };

            $scope.agentDetails = [];
            $scope.flag = false;
            $scope.countCheck = false;
            $scope.policyDetails = [];
            $scope.insurerDetails1 = [];
            $scope.insurerDetails2 = [];
            $scope.insurerDetails3 = [];

            $scope.isPolicyDetailsValid = false;

            /***
             * Testing if 1stPanel of General Details  is Empty or Not After Adding
             */

            $scope.policyDetailsListCheck = function () {
                //alert('Testing..');
                var checkLoopNameStatus = false;
                for (i in $scope.policyDetails) {
                    if ($scope.policyDetails[i].policyOrProposalNumber == '' || $scope.policyDetails[i].amount == '') {
                        checkLoopNameStatus = true;
                        break;
                    }
                    else {
                        checkLoopNameStatus = false;
                    }
                }
                if (checkLoopNameStatus) {
                    $scope.isPolicyDetailsValid = true;
                }
                else {
                    $scope.isPolicyDetailsValid = false;
                }
            }

            $scope.isInsurerDetails1Valid = false;

            /***
             * Testing if 2ndPanel of General Details  is Empty or Not After Adding
             */

            $scope.insurerDetails1Check = function () {
                //alert('Testing..');
                var checkLoopNameStatus = false;
                for (i in $scope.insurerDetails1) {
                    if ($scope.insurerDetails1[i].policyOrProposalNumber == '' || $scope.insurerDetails1[i].amount == null || $scope.insurerDetails1[i].date == null) {
                        checkLoopNameStatus = true;
                        //alert('Testing..');
                        break;
                    }
                    else {
                        checkLoopNameStatus = false;
                    }
                }
                if (checkLoopNameStatus) {
                    $scope.isInsurerDetails1Valid = true;
                }
                else {
                    $scope.isInsurerDetails1Valid = false;
                }
            }

            /***
             * Testing if 3rdPanel of General Details  is Empty or Not After Adding
             */
            $scope.isInsurerDetails2Valid = false;
            $scope.insurerDetails2Check = function () {
                //alert('Testing..');
                var checkLoopNameStatus = false;
                for (i in $scope.insurerDetails2) {
                    if ($scope.insurerDetails2[i].policyOrProposalNumber == '' || $scope.insurerDetails2[i].amount == null || $scope.insurerDetails2[i].date == null) {
                        checkLoopNameStatus = true;
                        //alert('Testing..');
                        break;
                    }
                    else {
                        checkLoopNameStatus = false;
                    }
                }
                if (checkLoopNameStatus) {
                    $scope.isInsurerDetails2Valid = true;
                }
                else {
                    $scope.isInsurerDetails2Valid = false;
                }
            }

            /***
             * Testing if 4thPanel of General Details  is Empty or Not After Adding
             */
            $scope.isInsurerDetails3Valid = false;
            $scope.insurerDetails3Check = function () {
                //alert('Testing..');
                var checkLoopNameStatus = false;
                for (i in $scope.insurerDetails3) {
                    if ($scope.insurerDetails3[i].policyOrProposalNumber == '') {
                        checkLoopNameStatus = true;
                        //alert('Testing..');
                        break;
                    }
                    else {
                        checkLoopNameStatus = false;
                    }
                }
                if (checkLoopNameStatus) {
                    $scope.isInsurerDetails3Valid = true;
                }
                else {
                    $scope.isInsurerDetails3Valid = false;
                }
            }

            $scope.generalDetailAllPanelValid = function () {
                if ($scope.isPolicyDetailsValid || $scope.isInsurerDetails1Valid || $scope.isInsurerDetails2Valid || $scope.isInsurerDetails3Valid) {
                    return true;
                }
                else {
                    return false;
                }
            }

            $scope.addPolicyDetails = function (policy) {
                if ($scope.policyDetails.length == 0) {
                    $scope.policyDetails.push(policy);
                }
                else {
                    var checkLoopNameStatus = "true";
                    for (i in $scope.policyDetails) {
                        if ($scope.policyDetails[i].policyOrProposalNumber == policy.policyOrProposalNumber) {
                            checkLoopNameStatus = false;
                            break;
                        }

                    }

                    if (checkLoopNameStatus == "true") {
                        $scope.policyDetails.unshift(policy);
                    }
                    else {
                        alert("Please Select Different Proposal Number");
                    }
                }

                if ($scope.policyDetails.length > 0) {
                    $scope.tab1GeneralDetailsStatus = false;
                }
                else {
                    $scope.tab1GeneralDetailsStatus = true;
                }

                $('#policyModal').modal('hide');
                $scope.clear();
            };

            $scope.allowSave = function () {
                if ($scope.tab1GeneralDetailsStatus || $scope.tab2GeneralDetailsStatus || $scope.tab3GeneralDetailsStatus || $scope.tab4GeneralDetailsStatus) {
                    return true;
                }
                else {
                    return false;
                }
            };

            $scope.policyInceptionDateModifier = function (date, row) {
                console.log('date' + date);
                var date1 = null;
                if (!moment(date, 'DD/MM/YYYY').isValid()) {
                    date1 = moment(date).format('DD/MM/YYYY');
                    for (i in $scope.insurerDetails1) {
                        if ($scope.insurerDetails1[i].policyOrProposalNumber == row.policyOrProposalNumber) {
                            $scope.insurerDetails1[i].date = date1;
                        }
                    }
                }
            }

            $scope.proposalDateModifier = function (date, row) {
                var date1 = null;
                if (!moment(date, 'DD/MM/YYYY').isValid()) {
                    date1 = moment(date).format('DD/MM/YYYY');
                    for (i in $scope.insurerDetails2) {
                        if ($scope.insurerDetails2[i].policyOrProposalNumber == row.policyOrProposalNumber) {
                            $scope.insurerDetails2[i].date = date1;
                        }
                    }
                }
            }

            $scope.cancelModal = function () {
                $('#policyModal').modal('hide');
                $('#assuredByOthersModal').modal('hide');
                $('#pendingInsuranceByOthersTpl').modal('hide');
                $('#assuranceDeclinedTpl').modal('hide');
                $scope.clear();
            }

            $scope.addAssuredByOthers = function (insurer1) {
                if ($scope.insurerDetails1.length == 0) {
                    $scope.insurerDetails1.unshift(insurer1);
                }
                else {
                    var checkLoopNameStatus = "true";
                    for (i in $scope.insurerDetails1) {
                        if ($scope.insurerDetails1[i].policyOrProposalNumber == insurer1.policyOrProposalNumber) {
                            checkLoopNameStatus = "false";
                            break;
                        }
                    }

                    if (checkLoopNameStatus == "true") {
                        $scope.insurerDetails1.unshift(insurer1);
                    }
                    else {
                        alert("Please Select Different Proposal Number");
                    }
                }

                if ($scope.insurerDetails1.length) {
                    $scope.tab2GeneralDetailsStatus = false;
                }
                else {
                    $scope.tab2GeneralDetailsStatus = true;
                }

                $('#assuredByOthersModal').modal('hide');
                $scope.clear();
            };


            $scope.addPendingInsuranceByOthersTpl = function (insurer2) {
                if ($scope.insurerDetails2.length == 0) {
                    $scope.insurerDetails2.unshift(insurer2);
                }
                else {
                    var checkLoopNameStatus = "true";

                    for (i in $scope.insurerDetails2) {
                        if ($scope.insurerDetails2[i].policyOrProposalNumber == insurer2.policyOrProposalNumber) {
                            checkLoopNameStatus = "false";
                            break;
                        }
                    }

                    if (checkLoopNameStatus == "true") {
                        $scope.insurerDetails2.unshift(insurer2);
                    }
                    else {
                        alert("Please Select Different Proposal Number");
                    }
                }
                if ($scope.insurerDetails2.length > 0) {

                    $scope.tab3GeneralDetailsStatus = false;
                }
                else {
                    $scope.tab3GeneralDetailsStatus = true;
                }
                $('#pendingInsuranceByOthersTpl').modal('hide');
                $scope.clear();
            };

            $scope.addaAssuranceDeclinedTpl = function (insurer3) {
                if ($scope.insurerDetails3.length == 0) {
                    $scope.insurerDetails3.push(insurer3);
                }
                else {
                    var checkLoopNameStatus = "true";
                    for (i in $scope.insurerDetails3) {
                        if ($scope.insurerDetails3[i].policyOrProposalNumber == insurer3.policyOrProposalNumber) {
                            checkLoopNameStatus = "false";
                            break;
                        }
                    }

                    if (checkLoopNameStatus == "true") {
                        $scope.insurerDetails3.unshift(insurer3);
                    }
                    else {
                        alert("Please Select Different Proposal Number");
                    }
                }


                if ($scope.insurerDetails3.length > 0) {
                    $scope.tab4GeneralDetailsStatus = false;
                }
                else {
                    $scope.tab4GeneralDetailsStatus = true;
                }
                $('#assuranceDeclinedTpl').modal('hide');
                $scope.clear();
            };
            $scope.addAgent = function (agent) {
                if ($scope.agentDetails.length == 0) {
                    $scope.agentDetails.unshift(agent);
                }
                else {
                    var checkLoopNameStatus = "true";

                    for (i in $scope.agentDetails) {
                        if ($scope.agentDetails[i].agentId == agent.agentId) {
                            checkLoopNameStatus = "false";
                            break;
                        }
                    }

                    if (checkLoopNameStatus == "true") {
                        $scope.agentDetails.unshift(agent);
                    }
                    else {
                        alert("Please Select Different AgentId");
                    }
                }
                $('#agentModal').modal('hide');
                $scope.clear();
            };

            $scope.statusCount = true;
            $scope.agentMessage = false;
            $scope.agentIdListForPlan = [];
            $scope.proposalPlanList = [];
            $scope.isProposerPlanValid = false;

            $scope.commisionSumTest = function () {
                var sum = 0;
                for (i in $scope.agentDetails) {

                    sum = parseFloat(sum) + parseFloat($scope.agentDetails[i].commission);
                }
                if (sum == 100.00) {
                    $scope.statusCount = false;
                    $scope.agentMessage = false;
                    $scope.isProposerPlanValid = true;
                    $scope.getProposerPlan();
                }
                else {
                    $scope.statusCount = true;
                    $scope.agentMessage = true;
                    $scope.isProposerPlanValid = false;
                }
            };
            $scope.commisionStatus = true;
            $scope.commisionMessage = false;

            $scope.shareSumTest = function () {
                var sum = 0;
                for (i in $scope.beneficiariesList) {

                    sum = parseFloat(sum) + parseFloat($scope.beneficiariesList[i].share);
                }
                if (sum == 100.00) {
                    $scope.commisionStatus = false;
                    $scope.commisionMessage = false;
                }
                else {
                    $scope.commisionStatus = true;
                    $scope.commisionMessage = true;
                }
            };

            /*$scope.countStatus = function () {
             var count = 0;

             for (i in $scope.agentDetails) {

             count = parseInt(count) + parseInt($scope.agentDetails[i].commission);
             }
             //console.log('count: ' + JSON.stringify(count));
             if (count == 100) {
             $scope.statusCount = true;
             }
             else {
             $scope.statusCount = false;
             }

             return count;
             };*/

            $scope.riderDetails = [];

            /*$scope.testPlan = function (searchRider) {
             //console.log('Testing...');
             //console.log('Pass..' + JSON.stringify(searchRider));

             for (i in $scope.searchRiders) {
             if ($scope.searchRiders[i].coverageName == searchRider.coverageName) {
             $scope.searchRiders[i] = searchRider;
             }
             }
             };*/


            $scope.clear = function () {
                //$scope.agent = {};
                $scope.abc = {};
                $scope.policy = {};
                $scope.insurer1 = {};
                $scope.insurer2 = {};
                $scope.insurer3 = {};
                $scope.beneficiary = {};
            };

            $scope.myFunction = function () {
                //alert('Testing For Riders..');
            }

            $scope.searchRiders = function (proposalPlanId) {
                //console.log('Search Riders Function..' + $scope.planId);
            }

            $scope.searchAgent = function () {
                $scope.check = false;
                $scope.checking = true;
                //console.log('Testing In SearchCode..');
                $scope.agentId = $scope.abc.agentId;
                //console.log('Value is: ' + $scope.agentId);
                $http.get("getagentdetail/" + $scope.agentId).success(function (response, status, headers, config) {
                    $scope.abc = response;
                    $scope.checking = false;
                }).error(function (response, status, headers, config) {
                    var check = status;
                    if (check == 500) {
                        $scope.check = true;
                        $scope.abc.firstName = null;
                        $scope.abc.lastName = null;
                    }
                });

            };

            $scope.proposal =
            {
                "msg": null,
                "proposalId": null
            };

            $scope.additionalDetail = {};
            $scope.medicalAttendant = {};
            $scope.replacement = {};


            $scope.saveAdditionalDetail = function () {
                //console.log('Inside SaveAdditionalDetail');

                var requestAddDetails =
                {
                    "medicalAttendantDetails": $scope.medicalAttendant.medicalAttendantDetails,
                    "medicalAttendantDuration": $scope.medicalAttendant.medicalAttendantDuration,
                    "dateAndReason": $scope.medicalAttendant.dateAndReason,
                    "replacementDetails": $scope.replacement,
                    "proposalId": $scope.proposal.proposalId
                }
                //console.log('RequiredJson:' + JSON.stringify(requestAddDetails));

                $http.post('/pla/individuallife/proposal/updateadditionaldetails', requestAddDetails).success(function (response, status, headers, config) {
                    $scope.stepsSaved[$scope.selectedWizard] = true;
                    $scope.proposal.proposalId = response.id;
                }).error(function (response, status, headers, config) {
                });

            };
            $scope.proposalNumberDetails =
            {
                "proposalNumber": null
            };

            $scope.saveProposedAssuredDetails = function () {
                //console.log('Save method of Proposer1');

                var prorequest = {
                    "spouse": $scope.proposerSpouse,
                    "employment": $scope.proposerEmployment,
                    "residentialAddress": $scope.proposerResidential
                };
                //console.log("************** Save in Proposer....");
                //console.log(JSON.stringify(prorequest));

                prorequest = angular.extend($scope.proposedAssured, prorequest);
                prorequest =
                {
                    "proposedAssured": prorequest,
                    "proposalId": $scope.proposal.proposalId
                };
                //console.log('ProRequest' + JSON.stringify(prorequest));

                var request1 = {
                    "proposedAssured": $scope.proposedAssured,
                    "proposalId": $scope.proposal.proposalId
                }

                ////console.log('Save Proposer'+JSON.stringify(request1));

                $http.post('updateproposedassuredandagent', prorequest).success(function (response, status, headers, config) {
                    $scope.stepsSaved[$scope.selectedWizard] = true;
                    $scope.proposal = response;
                    $scope.proposal.proposalId = response.id;

                    //Calling RIderDetails..

                    $http.get("getridersforplan/" + $scope.ProposerplanDetail.planId + "/" + $scope.proposedAssured.nextDob).success(function (response, status, headers, config) {
                        $scope.searchRiders = response;
                        $scope.proposalPlanDetail.riderDetails = response;
                        //console.log('Riders Details From Db is:' +JSON.stringify(response));
                        //console.log($scope.searchRiders);

                    }).error(function (response, status, headers, config) {
                        var check = status;
                        //console.log('Checking Status:'+JSON.stringify(check));
                    });


                }).error(function (response, status, headers, config) {
                });
            };
            $scope.clearPlanId = function () {
                $scope.ProposerplanDetail.planId = null;

            }
            $scope.getProposerPlan = function () {
                if (($scope.proposer.nextDob && $scope.proposer.isProposedAssured) || ($scope.proposer.nextDob && $scope.proposer.isProposedAssured == false)) {
                    //Call PlanUrl For Getting PlanDetails
                    if ($scope.isProposerPlanValid) {
                        for (i in $scope.agentDetails) {
                            $scope.agentIdListForPlan.push($scope.agentDetails[i].agentId);
                        }
                        console.log('Array is:' + JSON.stringify($scope.agentIdListForPlan));
                        if ($scope.proposer.isProposedAssured) {
                            $http.get("/pla/individuallife/proposal/searchplan/?agentIds=" + $scope.agentIdListForPlan + "&proposedAssuredAge=" + $scope.proposer.nextDob).success(function (ilPlanResponse, status, headers, config) {

                                console.log(JSON.stringify(ilPlanResponse))
                                $scope.proposalPlanList = ilPlanResponse;
                            });
                        }
                        else {
                            $http.get("/pla/individuallife/proposal/searchplan/?agentIds=" + $scope.agentIdListForPlan).success(function (ilPlanResponse, status, headers, config) {

                                console.log(JSON.stringify(ilPlanResponse))
                                $scope.proposalPlanList = ilPlanResponse;
                            });
                        }

                    }
                }
                else {
                    console.log('Dont get Any Plan..');
                    $scope.proposalPlanList = [];
                }
            }

            $scope.$watch('proposer.dateOfBirth', function (newvalue, oldvalue) {
                if (newvalue) {
                    ////alert(newvalue);
                    $scope.proposer.nextDob = moment().diff(new moment(new Date(newvalue)), 'years') + 1;
                    $scope.getProposerPlan();
                }
            });

            $scope.ageCalculateStatus = false;
            $scope.$watch('proposer.nextDob', function (newvalue, oldvalue) {
                if (newvalue) {
                    if ((parseInt(newvalue) < 18 ) || (parseInt(newvalue) > 60)) {
                        $scope.ageCalculateStatus = true;
                    }
                    else {
                        $scope.ageCalculateStatus = false;
                    }
                }
            });

            $scope.beneficiary = {"trusteeDetail": {}};
            $scope.$watch('beneficiary.title', function (newVal, oldVal) {
                if (newVal) {
                    if (newVal == 'Mr.') {
                        $scope.beneficiary.gender = 'MALE';
                    }
                    else if ((newVal == 'Miss') || (newVal == 'Mrs.')) {
                        $scope.beneficiary.gender = 'FEMALE';
                    }
                    else {
                        $scope.beneficiary.gender = '';
                    }
                }
            });


            $scope.getProposedAssuredGender = function (newVal) {
                if (newVal) {
                    if (newVal == 'Mr.') {
                        $scope.proposedAssured.gender = 'MALE';
                    }
                    else if ((newVal == 'Miss') || (newVal == 'Mrs.')) {
                        $scope.proposedAssured.gender = 'FEMALE';
                    }
                    else {
                        $scope.proposedAssured.gender = '';
                    }
                }
            }
            $scope.getProposerGender = function (newVal) {
                if (newVal) {
                    if (newVal == 'Mr.') {
                        $scope.proposer.gender = 'MALE';
                    }
                    else if ((newVal == 'Miss') || (newVal == 'Mrs.')) {
                        $scope.proposer.gender = 'FEMALE';
                    }
                    else {
                        $scope.proposer.gender = '';
                    }
                }
            }

            /**
             * Checking of  Occupation filed of  ProposedAssured Detail  to decide whether to display Employment Type Field
             * Address1, Address2 ,Province ,Work Phone & Postal CodeFields etc
             * */

            $scope.employmentTypeStaus = true;
            $scope.$watch('employment.occupation', function (newVal, oldVal) {
                if (newVal) {
                    if ((newVal == 'Student') || (newVal == 'Housewives')) {
                        $scope.employmentTypeStaus = false;
                    }
                    else {
                        $scope.employmentTypeStaus = true;
                    }
                }
            });
            $scope.proposerEmploymentTypeStaus = true;
            $scope.$watch('proposerEmployment.occupation', function (newVal, oldVal) {
                if (newVal) {
                    if ((newVal == 'Student') || (newVal == 'Housewives')) {
                        $scope.proposerEmploymentTypeStaus = false;
                    }
                    else {
                        $scope.proposerEmploymentTypeStaus = true;
                    }
                }
            });

            /**
             * If  "Is Proposer the Proposed Assured"  True then ProposedAssured details filed are Readonly
             * Else it is Editable
             * */
            $scope.readOnlyStatusProposedAssured = false;
            $scope.$watch('proposer.isProposedAssured', function (newVal, oldVal) {
                if (newVal) {
                    $scope.readOnlyStatusProposedAssured = true;
                    $scope.getProposerPlan();
                }
                else {
                    $scope.readOnlyStatusProposedAssured = false;
                    $scope.getProposerPlan();
                }
            });


            /**
             * Checking of  Occupation filed of  Proposer Detail  to decide whether to display Employment Type Field
             * Address1, Address2 ,Province ,Work Phone & Postal CodeFields
             * */

            $scope.proposerEmploymentTypeStaus = true;
            $scope.$watch('proposerEmployment.occupation', function (newVal, oldVal) {
                if (newVal) {
                    //alert(newVal);
                    if ((newVal == 'Student') || (newVal == 'Housewives')) {
                        $scope.proposerEmploymentTypeStaus = false;
                    }
                    else {
                        $scope.proposerEmploymentTypeStaus = true;
                    }
                }
            });

            $scope.toggleAnimation = function () {
                $scope.animationsEnabled = !$scope.animationsEnabled;
            };
            $scope.ProposerplanDetail = {}; //For Collecting ProposerPlan
            $scope.setProposerPlanName = function (proposalPlanId) {
                var planDetails = _.findWhere($scope.proposalPlanList, {plan_id: proposalPlanId});
                if (planDetails)
                    $scope.ProposerplanDetail.planName = planDetails.plan_name;
            }
            $scope.saveProposerDetails = function () {
                var request = {
                    "spouse": $scope.proposerSpouse,
                    "employment": $scope.proposerEmployment,
                    "residentialAddress": $scope.proposerResidential
                };

                request = angular.extend($scope.proposer, request);

                var request1 = {
                    "proposer": $scope.proposer,
                    "agentCommissionDetails": $scope.agentDetails,
                    "planDetail": $scope.ProposerplanDetail
                }


                var request3 =
                {
                    "proposer": $scope.proposer,
                    "agentCommissionDetails": $scope.agentDetails,
                    "planDetail": $scope.ProposerplanDetail,
                    "proposalId": $scope.proposal.proposalId
                }
                if ($scope.quotationIdDetails.quotationId != null && $scope.quotationIdDetails.quotationId != " ") {
                    var request2 = {
                        "spouse": $scope.proposerSpouse,
                        "employment": $scope.proposerEmployment,
                        "residentialAddress": $scope.proposerResidential
                    }
                    request2 = angular.extend($scope.proposer, request2);
                    request2 =
                    {
                        "proposer": $scope.proposer,
                        "agentCommissionDetails": $scope.agentDetails,
                        "quotationId": $scope.quotationIdDetails.quotationId
                    }
                    $http.post('create', request2).success(function (response, status, headers, config) {

                        $scope.proposal.ProposalId = response.id;
                        if (response.status == '500') {
                            return;
                        }

                        else {
                            $http.get("/pla/individuallife/proposal/getproposal/" + $scope.proposal.ProposalId + "?mode=view").success(function (response, status, headers, config) {
                                window.location.href = "/pla/individuallife/proposal/edit?proposalId=" + $scope.proposal.ProposalId + "&mode=edit";
                                var result = response;
                                $scope.proposal = response;
                                $scope.rcvProposal = response;
                                $scope.agentDetails = result.agentCommissionDetails;
                                $scope.proposalNumberDetails.proposalNumber = $scope.rcvProposal.proposalNumber;
                                $scope.proposal.proposalId = $scope.rcvProposal.proposalId;
                                if ($scope.rcvProposal.proposedAssured != null) {
                                    $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};
                                    $scope.spouse = $scope.rcvProposal.proposedAssured.spouse;
                                    $scope.employment = $scope.rcvProposal.proposedAssured.employment;
                                    $scope.residentialAddress = $scope.rcvProposal.proposedAssured.residentialAddress;
                                }
                                else {
                                    $scope.proposedAssured = {};
                                    $scope.employment = {};
                                    $scope.residentialAddress = {};
                                    $scope.spouse = {};
                                }


                                if ($scope.rcvProposal.proposer != null) {
                                    $scope.proposer = $scope.rcvProposal.proposer || {};
                                    $scope.proposerEmployment = $scope.proposer.employment;
                                    $scope.proposerResidential = $scope.proposer.residentialAddress;
                                    $scope.proposerSpouse = $scope.proposer.spouse;
                                }
                                else {
                                    $scope.proposer = {};
                                    $scope.proposerEmployment = {};
                                    $scope.proposerResidential = {};
                                    $scope.proposerSpouse = {};
                                }

                                $scope.proposalPlanDetail = $scope.rcvProposal.proposalPlanDetail;
                                $scope.searchRiders = $scope.rcvProposal.proposalPlanDetail.riderDetails;
                                $scope.beneficiaries = $scope.rcvProposal.beneficiaries;

                                if ($scope.proposedAssured.dateOfBirth) {
                                    $scope.proposedAssured.nextDob = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                                }
                                if ($scope.proposer.dateOfBirth) {
                                    $scope.proposer.nextDob = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                                }
                                $scope.spouse = $scope.rcvProposal.proposedAssured.spouse;
                                $scope.employment = $scope.rcvProposal.proposedAssured.employment;
                                $scope.residentialAddress = $scope.rcvProposal.proposedAssured.residentialAddress;
                            }).error(function (response, status, headers, config) {

                                $scope.message = response.message;

                            });
                        }
                    }).error(function (response, status, headers, config) {
                        alert(status);
                    });

                }

                else {
                    if ($scope.mode == "edit") {
                        $http.post('updateproposer', request3).success(function (response, status, headers, config) {
                            window.location.reload();
                        })
                            .error(function (response, status, headers, config) {
                            });
                    }
                    else {
                        //console.log('New Fresh Proposer Saving..' + JSON.stringify(request1));
                        $http.post('create', request1).success(function (response, status, headers, config) {
                            //console.log('Fresh Proposer Created..');
                            if (response.status == '500') {
                                return;
                            }
                            else {
                                console.log(response);
                                $scope.proposal.ProposalId = response.id;
                                window.location.href = "/pla/individuallife/proposal/edit?proposalId=" + $scope.proposal.ProposalId + "&mode=edit";
                                $scope.proposal = response;
                                $scope.proposal.proposalId = response.id;
                                $http.get("/pla/individuallife/proposal/getproposal/" + $scope.proposal.proposalId).success(function (response, status, headers, config) {
                                    var result = response;
                                    $scope.rcvProposal = response;

                                    /**
                                     * Calling  RiderDetails Url
                                     */
                                    if (response.proposer.isProposedAssured) {
                                        var age = response.proposedAssured.dateOfBirth;
                                        var ageNextBirthday = moment().diff(new moment(new Date(age)), 'years') + 1;
                                        $http.get("getridersforplan/" + response.proposalPlanDetail.planId + "/" + ageNextBirthday).success(function (response, status, headers, config) {
                                            $scope.searchRiders = response;
                                            console.log('RiderDetails..' + JSON.stringify($scope.searchRiders));
                                            $scope.proposalPlanDetail.riderDetails = response;
                                        }).error(function (response, status, headers, config) {
                                            var check = status;
                                        });
                                    }
                                    console.log('AFterSave' + JSON.stringify(response));

                                    /**
                                     * Plan Details For Calling RelationSHip
                                     */
                                    if (response.proposalPlanDetail != null) {
                                        $scope.ProposerplanDetail = response.proposalPlanDetail;

                                        $http.get('/pla/core/plan/getPlanById/' + response.proposalPlanDetail.planId)
                                            .success(function (plandata) {
                                                console.log('PlanDetails..' + JSON.stringify(plandata));
                                                $scope.plan = plandata;
                                                if (response.proposer.isProposedAssured != true) {
                                                    for (i in $scope.plan.planDetail.applicableRelationships) {
                                                        if ($scope.plan.planDetail.applicableRelationships[i] == 'SELF') {
                                                            console.log('SelfCheck..' + $scope.plan.planDetail.applicableRelationships[i]);
                                                            $scope.plan.planDetail.applicableRelationships.splice(i, 1);
                                                        }
                                                    }
                                                    $scope.relationshipList = $scope.plan.planDetail.applicableRelationships;
                                                }
                                                else {
                                                    var applicableRelation = [];
                                                    for (i in $scope.plan.planDetail.applicableRelationships) {
                                                        if ($scope.plan.planDetail.applicableRelationships[i] == 'SELF') {
                                                            applicableRelation.push($scope.plan.planDetail.applicableRelationships[i]);
                                                        }
                                                    }
                                                    $scope.relationshipList = applicableRelation;
                                                    $scope.proposedAssured.relationshipId = applicableRelation[0];
                                                }

                                            });
                                    }


                                    $scope.proposalNumberDetails.proposalNumber = $scope.rcvProposal.proposalNumber;
                                    $scope.proposal.proposalId = $scope.rcvProposal.proposalId;

                                    if ($scope.rcvProposal.proposedAssured != null) {
                                        $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};
                                        $scope.spouse = $scope.rcvProposal.proposedAssured.spouse;
                                        $scope.employment = $scope.rcvProposal.proposedAssured.employment;
                                        $scope.residentialAddress = $scope.rcvProposal.proposedAssured.residentialAddress;
                                    }
                                    else {
                                        ////alert("No");
                                        $scope.proposedAssured = {};
                                        $scope.employment = {};
                                        $scope.residentialAddress = {};
                                        $scope.spouse = {};
                                    }

                                    if ($scope.rcvProposal.proposer != null) {
                                        $scope.proposer = $scope.rcvProposal.proposer || {};
                                        $scope.proposerEmployment = $scope.proposer.employment;
                                        $scope.proposerResidential = $scope.proposer.residentialAddress;
                                        $scope.proposerSpouse = $scope.proposer.spouse;
                                    }
                                    else {
                                        $scope.proposer = {};
                                        $scope.proposerEmployment = {};
                                        $scope.proposerResidential = {};
                                        $scope.proposerSpouse = {};
                                    }
                                    if ($scope.rcvProposal.familyPersonalDetail != null) {
                                        $scope.familyHistory = $scope.rcvProposal.familyPersonalDetail.familyHistory;
                                        $scope.habit = $scope.rcvProposal.familyPersonalDetail.habit;
                                        $scope.habits = $scope.rcvProposal.familyPersonalDetail.habit;
                                        $scope.questionList = $scope.rcvProposal.familyPersonalDetail.habit.questions;
                                        $scope.build = $scope.rcvProposal.familyPersonalDetail.build;
                                    }
                                    if ($scope.rcvProposal.compulsoryHealthStatement != null) {
                                        $scope.compulsoryHealthDetails = $scope.rcvProposal.compulsoryHealthStatement;
                                    }

                                    if ($scope.proposedAssured.dateOfBirth) {
                                        $scope.proposedAssured.nextDob = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                                    }

                                    if ($scope.proposer.dateOfBirth) {
                                        $scope.proposer.nextDob = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                                    }
                                    $scope.agentDetails = [];
                                    $scope.spouse = $scope.rcvProposal.proposedAssured.spouse;
                                    $scope.employment = $scope.rcvProposal.proposedAssured.employment;
                                    $scope.residentialAddress = $scope.rcvProposal.proposedAssured.residentialAddress;
                                    $scope.agentDetails = $scope.rcvProposal.agentCommissionDetails;

                                }).error(function (response, status, headers, config) {
                                });
                            }
                        }).error(function (response, status, headers, config) {
                        });
                    }
                }
            };
            $scope.$watch('proposedAssuredAsProposer', function (newval, oldval) {
            });

            $scope.resources = resources;
            $scope.agentDetails = [];
            $scope.accordionStatus = {
                proposerDetails: {agents: true},
                proposedAssuredDetails: {proposedAssured: false},
                planDetails: {plan: true},
                generalDetails: {tab1: false},
                healthDetailsPart1: {tab1: false},
                healthDetailsPart2: {tab1: false}
            };

            $scope.openAgentModal = function () {
                var agentModalInstance = $bsmodal.open({
                    templateUrl: resources.agentModal,
                    controller: 'addAgentCtrl',
                    backdrop: 'static'
                });

                agentModalInstance.result.then(function (agent) {
                    $scope.isAgentEmpty = false;
                    $scope.agentDetails.push(agent);
                });

            };
            $scope.generalAnswer;
            $scope.generalAnswerList = [];

            $scope.generalDetailsSaveStatus = false; //meant for  saveButton Enable or Disable4

            $scope.tab1GeneralDetailsStatus = false;
            $scope.tab2GeneralDetailsStatus = false;
            $scope.tab3GeneralDetailsStatus = false;
            $scope.tab4GeneralDetailsStatus = false;

            $scope.openAccordion = function (status, tab) {
                //alert(tab);
                //console.log(status);
                if (status === 'YES') {
                    $scope.generalAnswer = true;
                    $scope.accordionStatus.generalDetails[tab] = true;
                    if (tab == 'tab1') {
                        $scope.tab1GeneralDetailsStatus = true;
                        $scope.generalAnswerList[0] = $scope.generalAnswer;
                    }
                    else if (tab == 'tab2') {
                        $scope.tab2GeneralDetailsStatus = true;
                        $scope.generalAnswerList[1] = $scope.generalAnswer;
                    }
                    else if (tab == 'tab3') {
                        $scope.tab3GeneralDetailsStatus = true;
                        $scope.generalAnswerList[2] = $scope.generalAnswer;
                    }
                    else if (tab == 'tab4') {
                        $scope.tab4GeneralDetailsStatus = true;
                        $scope.generalAnswerList[3] = $scope.generalAnswer;
                    }

                } else {
                    $scope.generalAnswer = false;
                    if (tab == 'tab1') {
                        $scope.tab1GeneralDetailsStatus = false;
                        $scope.generalAnswerList[0] = $scope.generalAnswer;
                    }
                    else if (tab == 'tab2') {
                        $scope.tab2GeneralDetailsStatus = false;
                        $scope.generalAnswerList[1] = $scope.generalAnswer;
                    }
                    else if (tab == 'tab3') {
                        $scope.tab3GeneralDetailsStatus = false;
                        $scope.generalAnswerList[2] = $scope.generalAnswer;
                    }
                    else if (tab == 'tab4') {
                        $scope.tab4GeneralDetailsStatus = false;
                        $scope.generalAnswerList[3] = $scope.generalAnswer;
                    }
                    $scope.accordionStatus.generalDetails[tab] = false;
                    $scope.clearPopoUp(tab); //removing all the modal to  space
                }
            };

            $scope.clearPopoUp = function (tab) {
                if (tab == 'tab1') {
                    $scope.policyDetails = [];
                }
                else if (tab == 'tab2') {
                    $scope.insurerDetails1 = [];
                }
                else if (tab == 'tab3') {
                    $scope.insurerDetails2 = [];
                }

                else if (tab == 'tab4') {
                    $scope.insurerDetails3 = [];
                }

            }

            $scope.openAccordionCompulsoryQuestion1 = function (status, tab, arrySeq, qId) {
                $scope.compulsoryHealthDetails[arrySeq].questionId = qId;
                if (status == 'true') {
                    $scope.accordionStatus.healthDetailsPart1[tab] = true;
                }
                else {
                    $scope.compulsoryHealthDetails[arrySeq].answerResponse = null;
                    $scope.accordionStatus.healthDetailsPart1[tab] = false;
                }
            };

            $scope.openAccordionCompulsoryQuestion2 = function (status, tab, arrySeq, qId) {
                $scope.compulsoryHealthDetails[arrySeq].questionId = qId;
                if (status == 'true') {
                    $scope.accordionStatus.healthDetailsPart2[tab] = true;
                }
                else {
                    $scope.compulsoryHealthDetails[arrySeq].answerResponse = null;
                    $scope.accordionStatus.healthDetailsPart2[tab] = false;
                }
            };

            $scope.openAccordionoveradditionalDetail = function (qId, status) {
                $scope.replacement.questionId = qId;
                if (status == 'true') {

                }
                else {
                    $scope.replacement.answerResponse1 = null;
                    $scope.replacement.answerResponse2 = null;
                }
            }
            $scope.openAccordionFamilyQuestion = function (status, tab, arrySeq, qId) {
                $scope.questionList[arrySeq].questionId = qId;
                if (status == 'true') {
                    $scope.accordionStatus.familyHabitAndBuild[tab] = true;
                }
                else {
                    $scope.questionList[arrySeq].answerResponse = null;
                    $scope.accordionStatus.familyHabitAndBuild[tab] = false;
                }
            };

            $scope.openAccordionoverWeightQuestion = function (status, tab) {
                $scope.build.overWeightQuestion.questionId = '21';
                if (status == 'true') {
                    $scope.accordionStatus.familyHabitAndBuild[tab] = true;
                }
                else {
                    $scope.build.overWeightQuestion.answerResponse = null;
                    $scope.accordionStatus.familyHabitAndBuild[tab] = false;
                }
            };

            $scope.openAccordionoverFamilyQuestion = function (status) {
                $scope.familyHistory.closeRelative.questionId = '16';
                if (status == 'true') {

                }
                else {
                    $scope.familyHistory.closeRelative.answerResponse = null;
                }
            };
            $scope.openModalWindow = function (templateName) {
                var modalInstance = $bsmodal.open({
                    templateUrl: templateName,
                    controller: 'modalCtrl',
                    backdrop: 'static'
                });
            };

            $scope.hasAccordionError = function (form) {
                return false;
            };

            function isFormValidated() {
                /* if (_.isEmpty($scope.agentDetails)) {
                 $scope.isAgentEmpty = true;
                 return false;
                 }

                 if ($scope.step1.isProposed.$invalid) {
                 return false;
                 }*/
                return true;
            };

            $scope.openBeneficiaryModal

        }])
    .controller('modalCtrl', ['$scope', '$modalInstance', function ($scope, $modalInstance) {
        $scope.addAgent = function () {
            $modalInstance.close([]);
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }])
    .controller('addAgentCtrl', ['$scope', '$modalInstance', function ($scope, $modalInstance) {
        $scope.addAgent = function (agent) {
            $modalInstance.close(agent);
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }])
    .constant('resources', {
        agentModal: "/pla/individuallife/proposal/getPage/agentDetailModal",
        proposedAssuredUrl: "/pla/individuallife/proposal/getPage/proposedAssuredDetails",
        proposerDetails: "/pla/individuallife/proposal/getPage/proposerDetails",
        planDetails: "/pla/individuallife/proposal/getPage/planDetails",
        beneficiaryModal: "/pla/individuallife/proposal/getPage/beneficiaryDetailModal",
        generalDetails: "/pla/individuallife/proposal/getPage/generalDetails",
        compulsoryHealthDetailsPart1: "/pla/individuallife/proposal/getPage/compulsoryHealthDetailsPart1",
        compulsoryHealthDetailsPart2: "/pla/individuallife/proposal/getPage/compulsoryHealthDetailsPart2",
        familyHabitAndBuild: "/pla/individuallife/proposal/getPage/familyHabitAndBuild",
        additionalDetail: "/pla/individuallife/proposal/getPage/additionalDetail",
        premiumDetail: "/pla/individuallife/proposal/getPage/premiumDetail",
        mandatoryDocumentDetails: "/pla/individuallife/proposal/getPage/mandatoryDocumentDetails",
        approvalDetails: "/pla/individuallife/proposal/getPage/approvalDetail",
        proposerDetailsForApproval: "/pla/individuallife/proposal/getPage/proposerDetailsForApproval",
        proposedAssuredUrlForApproval: "/pla/individuallife/proposal/getPage/proposedAssuredDetailsForApproval",
        planDetailsForApproval: "/pla/individuallife/proposal/getPage/planDetailsForApproval",
        generalDetailsForApproval: "/pla/individuallife/proposal/getPage/generalDetailsForApproval",
        compulsoryHealthDetailsPart1ForApproval: "/pla/individuallife/proposal/getPage/compulsoryHealthDetailsPart1ForApproval",
        compulsoryHealthDetailsPart2ForApproval: "/pla/individuallife/proposal/getPage/compulsoryHealthDetailsPart2ForApproval",
        familyHabitAndBuildForApproval: "/pla/individuallife/proposal/getPage/familyHabitAndBuildForApproval",
        additionalDetailForApproval: "/pla/individuallife/proposal/getPage/additionalDetailForApproval",
        premiumDetailForApproval: "/pla/individuallife/proposal/getPage/premiumDetailForApproval",
        mandatoryDocumentDetailsForApproval: "/pla/individuallife/proposal/getPage/mandatoryDocumentDetailsForApproval"
    })
    .filter('getTrustedUrl', ['$sce', function ($sce) {
        return function (url) {
            //console.log('getTrustedUrl' + url);
            return $sce.getTrustedResourceUrl(url);
        }
    }]);


var viewILQuotationModule = (function () {
    var services = {};
    services.selectedItem = "";
    services.status = '';
    services.getTheItemSelected = function (ele) {
        this.selectedItem = $(ele).val();
        this.status = $(ele).parent().find('input[type=hidden]').val();
        $(".btn-disabled").attr("disabled", false);
        /*  if (this.status == 'GENERATED') {
         $('#emailaddress').attr('disabled', false);
         $('#print').attr('disabled', false);
         } else {
         $('#emailaddress').attr('disabled', true);
         $('#print').attr('disabled', true);
         }*/

        if (this.status == 'Draft' || this.status == 'Returned') {
            $('#modify').attr('disabled', false);
        }
        else {
            $('#modify').attr('disabled', true);
        }
    };

    services.reload = function () {
        window.location.reload();
    };

    services.printQuotation = function () {
        window.location.href = '/pla/individuallife/quotation/printquotation/' + this.selectedItem;
    }

    services.emailQuotation = function () {
        window.location.href = '/pla/individuallife/quotation/emailQuotation/' + this.selectedItem;
    }

    services.modifyProposal = function () {
        var proposalId = this.selectedItem;

        if (this.status == 'Returned') {

            window.location.href = "/pla/individuallife/proposal/viewApprovalProposal?proposalId=" + proposalId + "&status=return" + "&mode=edit";
        }
        else {
            window.location.href = "proposal/edit?proposalId=" + proposalId + "&mode=edit";

        }
    };

    services.viewProposal = function () {
        var proposalId = this.selectedItem;
        window.location.href = "proposal/edit?proposalId=" + proposalId + "&mode=view";
    };

    services.createUpdateProposal = function () {
        var quotationId = this.selectedItem;
        window.location.href = "edit?quotationId=" + quotationId + "&mode=update";
    };

    services.viewApprovalProposal = function () {
        var proposalId = this.selectedItem;
        window.location.href = "/pla/individuallife/proposal/viewApprovalProposal?proposalId=" + proposalId + "&method=approval" + "&mode=view";

    };
    return services;
})();