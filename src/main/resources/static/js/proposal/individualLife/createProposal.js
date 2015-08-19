angular.module('createProposal', ['pla.individual.proposal', 'common', 'ngRoute', 'commonServices', 'ngMessages', 'angucomplete-alt','mgcrea.ngStrap.select','angularFileUpload'])
    .directive('policyterm', function () {
        return {
            restrict: 'E',
            templateUrl: 'plan-policyterm.tpl',
            controller: ['$scope', function ($scope) {
                $scope.policyTerms = function () {
                    if ($scope.plan.policyTermType === 'SPECIFIED_VALUES') {
                        var maxMaturityAge = $scope.plan.policyTerm.maxMaturityAge || 1000;
                        //var ageNextBirthday = calculateAge($scope.proposedAssured.dateOfBirth);
                        var ageNextBirthday = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
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
                $scope.getCoverageTermType = function (searchRider) {
                    ////alert(JSON.stringify(searchRider));
                    if ($scope.plan) {
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
                        $scope.coverage = _.findWhere($scope.plan.coverages, {coverageId: searchRider.coverageId});
                        //alert("COverage.."+JSON.stringify($scope.coverage));
                        //console.log('Coverage..'+JSON.stringify($scope.coverage));
                        return $scope.coverage.coverageSumAssured.sumAssuredType;

                    }
                }
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
        'globalConstants', 'ProposalService','$upload',
        function ($scope, resources, getQueryParameter, $bsmodal, $http, $window, globalConstants, ProposalService,$upload,$route) {

            //console.log('create proposal');

            $scope.employmentTypes = [];
            $scope.checkEmpTypes = null;
            $scope.occupations = [];
            $scope.provinces = [];
            $scope.bankDetailsResponse=[];
            $scope.documentList=[];
            $scope.proposal = {};
            $scope.searchRiders = [];
            $scope.proposalId = getQueryParameter('proposalId')
            $scope.quotationId = getQueryParameter('quotationId');
            $scope.proposalPlanDetail={};
            $scope.searchRider={};

            $scope.premiumResponse={};

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

            $scope.returnStatusCheck=false;
            var statusReturn= getQueryParameter('status');

            // Below 6 variables are  meant for apperance status of Reject,Hold & Forward Buttons
            $scope.underWritingLevelOneStatus=false;
            $scope.pendingAcceptanceStatus=false;
            $scope.underWritingLevelTwoStatus= false;
            $scope.rejectStatus=false;
            $scope.forwardStatus=false;
            $scope.holdStatus=false;

            //console.log('modeType' + $scope.mode);
            $scope.quotationStatus = "GENERATED";
            if ($scope.quotationId) {
                //console.log('Navigate to quotation Window...');
                //alert("Quotation Id");
                $http.get("/pla/individuallife/quotation/getquotation/" + $scope.quotationId).success(function (response, status, headers, config) {
                    var viewQuotationOutput = response;

                    //alert("assuredTheProposer"+response.assuredTheProposer);
                    console.log("View Quotation "+JSON.stringify(response));
                    $scope.rcvProposalDetailQid = response;
                    //console.log('****** Checking quotationDetails...' + $scope.rcvProposalDetailQid.quotationId.quotationId);
                    $scope.quotationIdDetails = $scope.rcvProposalDetailQid.quotationId;
                    //console.log('viewQuotationOutput' + JSON.stringify(viewQuotationOutput));

                    $scope.proposedAssured = $scope.rcvProposalDetailQid.proposedAssured || {};
                    $scope.proposedAssured.nrc = $scope.rcvProposalDetailQid.proposedAssured.nrc || {};
                    $scope.proposedAssured.isProposer = $scope.rcvProposalDetailQid.assuredTheProposer;

                    if($scope.rcvProposalDetailQid.proposer != null)
                    {
                        $scope.proposer = $scope.rcvProposalDetailQid.proposer || {};
                        $scope.proposer.isProposedAssured= response.assuredTheProposer
                        $scope.proposer.nrc = $scope.rcvProposalDetailQid.proposer.nrc || {};

                        /*if($scope.rcvProposalDetailQid.proposer.employment != null)
                         {
                         $scope.proposerEmployment = $scope.rcvProposalDetailQid.proposer.employment;
                         }
                         else
                         {
                         $scope.proposerEmployment =" ";
                         }

                         //$scope.proposerEmployment = $scope.rcvProposalDetailQid.proposer.employment;
                         $scope.proposerResidential = $scope.rcvProposalDetailQid.proposer.residentialAddress;
                         $scope.proposerSpouse = $scope.rcvProposalDetailQid.proposer.spouse;*/

                        if ($scope.proposer.dateOfBirth) {
                            $scope.proposer.nextDob = moment().diff(new moment(new Date($scope.proposer.dateOfBirth)), 'years') + 1;
                        }

                    }
                    if($scope.rcvProposalDetailQid.proposalPlanDetail != null)
                    {
                        $scope.proposalPlanDetail = $scope.rcvProposalDetailQid.proposalPlanDetail;
                    }
                    $scope.agent = $scope.rcvProposalDetailQid.agentDetail;
                    var selectedPlan = {};
                    $scope.planDetailDto = response.planDetailDto;

                    if(response.planDetail !=null)
                    {
                        selectedPlan.title = response.planDetail.planDetail.planName;
                        selectedPlan.description = response.planDetail;
                        $scope.selectedPlan = selectedPlan;
                    }
                    $scope.agentDetails.push($scope.agent);
                }).error(function (response, status, headers, config) {
                });
            }

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

            if ($scope.proposalId) {
                $http.get("/pla/individuallife/proposal/getproposal/" + $scope.proposalId + "?mode=view").success(function (response, status, headers, config) {
                    var result = response;
                    console.log("called********************");
                    console.log(response.proposer);
                    console.log('After Proposer.. ****');
                    console.log(response);
                    //alert(response.proposer.gender);
                    //$scope.proposer.gender=response.proposer.gender;
                    console.log('proposalStatus***: '+ response.proposalStatus);
                    if(response.proposalStatus !=null)
                    {
                        if(response.proposalStatus == 'UNDERWRITING_LEVEL_ONE')
                        {
                            $scope.underWritingLevelOneStatus= true;
                        }
                        else if(response.proposalStatus == 'PENDING_ACCEPTANCE')
                        {
                            $scope.pendingAcceptanceStatus=true;
                        }
                        else if(response.proposalStatus == 'UNDERWRITING_LEVEL_TWO')
                        {
                            $scope.underWritingLevelTwoStatus= true;
                        }
                        else
                        {
                            $scope.underWritingLevelOneStatus= false;
                            $scope.pendingAcceptanceStatus=false;
                            $scope.underWritingLevelTwoStatus= false;
                        }
                    }
                    if($scope.pendingAcceptanceStatus )
                    {
                        $scope.rejectStatus=false;
                        $scope.forwardStatus=false;
                        $scope.holdStatus=false;
                    }
                    else  if($scope.underWritingLevelOneStatus )
                    {
                        $scope.rejectStatus=true;
                        $scope.forwardStatus=true;
                        $scope.holdStatus=true;
                    }
                    else  if($scope.underWritingLevelTwoStatus)
                    {
                        $scope.rejectStatus=true;
                        $scope.forwardStatus=false;
                        $scope.holdStatus=true;
                    }
                    else
                    {
                        $scope.rejectStatus=false;
                        $scope.forwardStatus=false;
                        $scope.holdStatus=false;
                    }

                    console.log("Response***");
                    //console.log(JSON.stringify(response));

                    $scope.agentDetails=result.agentCommissionDetails;
                    $scope.rcvProposal = response;

                    /* if($scope.rcvProposal.premiumPaymentDetails.premiumDetail != null){
                     $scope.premiumResponse=$scope.rcvProposal.premiumPaymentDetails.premiumDetail;
                     }*/

                    ////console.log('Proposal Number....');
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
                            console.log('DcoumentList:'+JSON.stringify(response));
                            // //alert("documentResponse: "+JSON.stringify(response))
                            //console.log('documentResponse:'+JSON.stringify(response));
                            //console.log("documentList: "+JSON.stringify($scope.documentList))
                            //console.log('DocumentList Details..'+JSON.stringify(response));
                        });
                    $http.get("getallrelations/").success(function (response, status, headers, config) {
                        $scope.relationshipList=response;
                    }).error(function (response, status, headers, config) {
                    });


                    $http.get("getadditionaldocuments/"+ $scope.proposal.proposalId).success(function (data, status) {
                        //console.log(data);
                        $scope.additionalDocumentList=data;
                        $scope.checkDocumentAttached=$scope.additionalDocumentList!=null;


                    });

                    $http.get("getpremiumdetail/"+$scope.proposal.proposalId).success(function (response, status, headers, config) {
                        $scope.premiumResponse=response;
                        if($scope.premiumResponse != null)
                        {
                            ////alert('Premium is Present');

                        }
                    }).error(function (response, status, headers, config) {
                    });
                    if($scope.rcvProposal.proposer != null)
                    {
                        //console.log('Proposer: '+JSON.stringify($scope.rcvProposal.proposer || {}));
                        $scope.proposer = $scope.rcvProposal.proposer;
                        console.log($scope.rcvProposal);
                        if($scope.rcvProposal.proposer.gender){
                            var gender=$scope.rcvProposal.proposer.gender;
                            $scope.proposer.gender=gender;

                        }

                        $scope.proposerEmployment = $scope.proposer.employment;

                        $scope.proposerResidential = $scope.proposer.residentialAddress;
                        $scope.proposerSpouse = $scope.proposer.spouse;

                        if ($scope.proposer.dateOfBirth) {
                            $scope.proposer.nextDob = moment().diff(new moment(new Date($scope.proposer.dateOfBirth)), 'years') + 1;
                        }
                        // $scope.proposer.gender=$scope.rcvProposal.proposer.gender;
                    }

                    if($scope.rcvProposal.proposalPlanDetail !=null)
                    {
                        $scope.proposalPlanDetail = $scope.rcvProposal.proposalPlanDetail;
                    }

                    //console.log('Response..'+JSON.stringify(response.proposalPlanDetail));
                    //$scope.selectedPlan.title=response.proposalPlanDetail.planName;
                    var selectedPlan = {};

                    if(response.proposalPlanDetail !=null)
                    {
                        $scope.selectPlanResponse=true;
                        $http.get('/pla/core/plan/getPlanById/' + response.proposalPlanDetail.planId)
                            .success(function (plandata) {
                                $scope.plan = plandata;
                                console.log("Plan Details.."+JSON.stringify($scope.plan));
                                $scope.applicableRelationships=plandata.planDetail.applicableRelationships;
                            });

                        selectedPlan.title = response.proposalPlanDetail.planName;
                        selectedPlan.description = response.planDetail;
                        $scope.selectedPlan = selectedPlan;
                        $scope.proposalPlanDetail.planId=response.proposalPlanDetail.planId;
                    }
                    //$scope.selectedPlan.title=response.proposalPlanDetail.planName;
                    if($scope.proposalPlanDetail != null && $scope.proposalPlanDetail.riderDetails != null)
                    {
                        $scope.searchRiders = $scope.rcvProposal.proposalPlanDetail.riderDetails;

                        //$scope.proposalPlanDetail.riderDetails=$scope.rcvProposal.proposalPlanDetail.riderDetails;
                    }

                    //$scope.selectedPlan=$scope.rcvProposal.proposalPlanDetail;
                    if($scope.rcvProposal.beneficiaries != null)
                    {
                        $scope.beneficiariesList = $scope.rcvProposal.beneficiaries;
                    }

                    if($scope.rcvProposal.generalDetails != null)
                    {
                        //console.log("generalDetails:-->"+JSON.stringify($scope.rcvProposal.generalDetails));
                        if($scope.rcvProposal.generalDetails.assuredByPLAL !=null)
                        {
                            if($scope.rcvProposal.generalDetails.assuredByPLAL.answer)
                            {
                                $scope.assuredByPlalList="YES";
                            }
                            else
                            {
                                $scope.assuredByPlalList="NO";
                            }
                            $scope.policyDetails=$scope.rcvProposal.generalDetails.assuredByPLAL.answerResponse;
                        }

                        if($scope.rcvProposal.generalDetails.assuredByOthers !=null)
                        {
                            if($scope.rcvProposal.generalDetails.assuredByOthers.answer)
                            {
                                ////alert("Ok..");
                                $scope.assuredByOthers="YES";
                            }
                            else
                            {
                                ////alert("Not Ok");
                                $scope.assuredByOthers="NO";
                            }
                            $scope.insurerDetails1=$scope.rcvProposal.generalDetails.assuredByOthers.answerResponse;
                        }

                        if($scope.rcvProposal.generalDetails.pendingInsuranceByOthers !=null)
                        {
                            if($scope.rcvProposal.generalDetails.pendingInsuranceByOthers.answer)
                            {
                                $scope.pendingInsuranceByOthers="YES";
                            }
                            else
                            {
                                $scope.pendingInsuranceByOthers="NO";
                            }
                            $scope.insurerDetails2=$scope.rcvProposal.generalDetails.pendingInsuranceByOthers.answerResponse;
                        }

                        if($scope.rcvProposal.generalDetails.assuranceDeclined !=null)
                        {
                            if($scope.rcvProposal.generalDetails.assuranceDeclined.answer)
                            {
                                $scope.assuranceDeclined="YES";
                            }
                            else
                            {
                                $scope.assuranceDeclined="NO";
                            }
                            $scope.insurerDetails3=$scope.rcvProposal.generalDetails.assuranceDeclined.answerResponse;
                        }

                        $scope.generalQuestion=$scope.rcvProposal.generalDetails.questionAndAnswers;
                    }

                    if($scope.rcvProposal.additionaldetails != null)
                    {
                        $scope.medicalAttendant.medicalAttendantDetails=$scope.rcvProposal.additionaldetails.medicalAttendantDetails;
                        $scope.medicalAttendant.medicalAttendantDuration=$scope.rcvProposal.additionaldetails.medicalAttendantDuration;
                        $scope.medicalAttendant.dateAndReason=$scope.rcvProposal.additionaldetails.dateAndReason;

                        if($scope.rcvProposal.additionaldetails.replacementDetails !=null)
                        {
                            $scope.replacement=$scope.rcvProposal.additionaldetails.replacementDetails;
                        }
                    }

                    if($scope.rcvProposal.premiumPaymentDetails != null)
                    {
                        $scope.premiumPaymentDetails.premiumFrequency=$scope.rcvProposal.premiumPaymentDetails.premiumFrequency;
                        $scope.premiumPaymentDetails.premiumPaymentMethod=$scope.rcvProposal.premiumPaymentDetails.premiumPaymentMethod;
                        $scope.premiumPaymentDetails.proposalSignDate=$scope.rcvProposal.premiumPaymentDetails.proposalSignDate;

                        if($scope.rcvProposal.premiumPaymentDetails.employerDetails !=null)
                        {
                            $scope.premiumEmployerDetails=$scope.rcvProposal.premiumPaymentDetails.employerDetails;
                        }

                        if($scope.rcvProposal.premiumPaymentDetails.bankDetails !=null)
                        {
                            $scope.bankDetails= $scope.rcvProposal.premiumPaymentDetails.bankDetails;
                        }
                    }

                    if($scope.rcvProposal.proposedAssured !=null) {
                        $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};
                        if ($scope.proposedAssured.dateOfBirth) {
                            $scope.proposedAssured.nextDob = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                        }
                        /*if ($scope.proposer.dateOfBirth) {
                         $scope.proposer.nextDob = moment().diff(new moment(new Date($scope.proposer.dateOfBirth)), 'years') + 1;
                         }*/
                        //$scope.agentDetails=[];
                        $scope.spouse = $scope.rcvProposal.proposedAssured.spouse;
                        ////alert($scope.rcvProposal.proposedAssured.employment);
                        $scope.employment = $scope.rcvProposal.proposedAssured.employment;
                        $scope.residentialAddress = $scope.rcvProposal.proposedAssured.residentialAddress;
                        $scope.agentDetails = $scope.rcvProposal.agentCommissionDetails;
                    }
                    if($scope.rcvProposal.familyPersonalDetail != null)
                    {
                        $scope.familyPersonalDetail = $scope.rcvProposal.familyPersonalDetail;
                        //console.log('FamilyHistory..' + $scope.rcvProposal.familyPersonalDetail.familyHistory.father);
                        $scope.familyHistory = $scope.rcvProposal.familyPersonalDetail.familyHistory;
                        $scope.habit = $scope.rcvProposal.familyPersonalDetail.habit;
                        $scope.habits = $scope.rcvProposal.familyPersonalDetail.habit;
                        $scope.questionList = $scope.rcvProposal.familyPersonalDetail.habit.questions;
                        $scope.build = $scope.rcvProposal.familyPersonalDetail.build;
                    }
                    if($scope.rcvProposal.compulsoryHealthStatement !=null)
                    {
                        $scope.compulsoryHealthDetails = $scope.rcvProposal.compulsoryHealthStatement;
                    }

                }).error(function (response, status, headers, config) {
                });
            }

            $scope.getPolicyTermVal=function(policyTerm){
                alert(policyTerm);
                for(i in $scope.plan.coverages )
                {
                    if ($scope.plan.coverages[i].coverageTermType == 'POLICY_TERM')
                    {
                        alert($scope.plan.coverages[i].coverageTermType);
                        $scope.riderDetails[i].coverTerm=policyTerm;
                    }
                }
                /*if($scope.plan.coverage.coverageTermType=='POLICY_TERM'){
                 $scope.searchRider.coverTerm=policyTerm;

                 }*/


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
                $scope.bankDetailsResponse= response;
                //console.log("Bank Details :"+JSON.stringify(response));
            }).error(function (response, status, headers, config) {
            });

            $scope.bankCodeDetails=[];
            $scope.$watch('bankDetails.bankName',function(newvalue,oldvalue){
                if(newvalue){
                    //console.log(newvalue);
                    var bankCode = _.findWhere($scope.bankDetailsResponse, {bankName: newvalue});
                    // //alert("Bank Details.."+JSON.stringify(bankCode));
                    if (bankCode){
                        $http.get('/pla/individuallife/proposal/getAllBankBranchNames/'+bankCode.bankCode).success(function (response, status, headers, config) {
                            $scope.bankBranchDetails= response;
                            //console.log("Bank Details :"+JSON.stringify(response));
                        }).error(function (response, status, headers, config) {
                        });

                        // http://localhost:6443/pla/individuallife/proposal/getAllBankBranchNames/BAN
                    }
                    //$scope.bankDetails.bankBranchSortCode = bankCode.BANK_CODE;

                }
            });
            $scope.bankCodeDetails=[];

            $scope.$watch('bankDetails.bankBranchName',function(newvalue,oldvalue){
                if(newvalue){
                    //alert("//alert in bankCode"+newvalue);
                    var bankBranchNames = _.findWhere($scope.bankBranchDetails, {branchName: newvalue});
                    //$scope.bankDetails.bankBranchSortCode=bankBranchNames.sortCode;
                    //$scope.bankBranchDetails=bankBranchNames;
                    ////alert("bankBranchName"+JSON.stringify($scope.bankBranchNames));
                    //console.log("Branch Details.."+JSON.stringify($scope.bankBranchNames))
                    if(bankBranchNames)
                    {
                        $scope.bankDetails.bankBranchSortCode=bankBranchNames.sortCode;
                    }
                }
            });


            $scope.titles = globalConstants.title;
            $scope.part = {
                isPart: true
            };

            $scope.selectedPlan = {};
            $scope.selectedWizard = 7;

            $scope.premiumEmployerDetails=
            {
                "employeeId": null,

                "manNumber": null,

                "companyNameAndPostalAddress": null,

                "basicSalary": null,

                "salaryPer":null

            };

            $scope. premiumPaymentDetails=
            {
                "premiumFrequency":null,
                "premiumPaymentMethod":null
            }

            $scope.bankDetails=
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
                //console.log('Radio Button');
                viewILProposalModule.getTheItemSelected(ele);
            };

            /*$scope.beneficiariesList =[];*/

            $scope.titleList = globalConstants.title;
            $scope.genderList = globalConstants.gender;


            $scope.savePremiumDetail=function()
            {
                $scope.premiumResponse.planId=$scope.proposalPlanDetail.planId;
                console.log("PremiumResponse.."+JSON.stringify($scope.premiumResponse));


                var premiumRequest=
                {
                    //"premiumPaymentDetails":$scope.premiumPaymentDetails,
                    "premiumDetail":$scope.premiumResponse,
                    "employerDetails":$scope.premiumEmployerDetails,
                    "bankDetails":$scope.bankDetails
                }
                premiumRequest=angular.extend($scope.premiumPaymentDetails, premiumRequest);

                premiumRequest=
                {
                    "premiumPaymentDetails": premiumRequest,
                    "proposalId": $scope.proposal.proposalId
                };
                //console.log('premiumRequest' +JSON.stringify(premiumRequest));

                $http.post('updatepremiumpaymentdetails', premiumRequest).success(function (response, status, headers, config) {
                    $scope.proposal.proposalId=response.id;
                }).error(function (response, status, headers, config) {
                });
            }

            /*$http.get("getadditionaldocuments/"+ $scope.proposal.proposalId).success(function (data, status) {
             //console.log(data);
             $scope.additionalDocumentList=data;
             $scope.checkDocumentAttached=$scope.additionalDocumentList!=null;

             });*/


            $scope.addAdditionalDocument = function () {
                $scope.additionalDocumentList.unshift({});
                $scope.checkDocumentAttached=$scope.isUploadEnabledForAdditionalDocument();

            };

            $scope.removeAdditionalDocument = function (index) {
                $scope.additionalDocumentList.splice(index, 1);
                $scope.checkDocumentAttached=$scope.isUploadEnabledForAdditionalDocument();
            };

            $scope.callAdditionalDoc = function(file){
                if(file[0]){
                    $scope.checkDocumentAttached=$scope.isUploadEnabledForAdditionalDocument();
                }
            }
            $scope.additionalDocumentList = [{}];
            //$scope.documentList = documentList;
            $scope.documentList=[];

            $scope.isUploadEnabledForAdditionalDocument = function(){
                var enableAdditionalUploadButton= ($scope.additionalDocumentList!=null);
                for (var i = 0; i < $scope.additionalDocumentList.length; i++) {
                    var document = $scope.additionalDocumentList[i];
                    var files = document.documentAttached;
                    //alert(i+"--"+files)
                    //alert(i+"--"+document.content);
                    if(!(files || document.content)){
                        enableAdditionalUploadButton=false;
                        break;
                    }
                }
                return enableAdditionalUploadButton;
            }

            $scope.returnComment='';
            $scope.showSubmitStatus=false;

            $scope.setComments=function(comments)
            {
                if(comments != null)
                {
                    $scope.showSubmitStatus=false;
                }
                else
                {
                    $scope.showSubmitStatus=true;
                }
                //alert('Set Comments'+JSON.stringify(comments));
                $scope.returnComment=comments;
            }
            $scope.submitAdditionalDocument = function(){
                //$scope.comments=comments;
                //alert('Comments: '+ JSON.stringify($scope.returnComment));

                $http.post('submit', angular.extend({},
                    {"proposalId": $scope.proposal.proposalId,
                        "comment":$scope.returnComment})).success(function (data) {
                    /*if (data.status == "200") {
                     saveStep();
                     $('#searchFormProposal').val($scope.proposalId);
                     $('#searchForm').submit();
                     }*/

                }) ;

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
                            fields: {documentId: document.documentId,proposalId: $scope.proposal.proposalId,mandatory:true,isApproved:true},
                            method: 'POST'
                        }).progress(function (evt) {

                        }).success(function (data, status, headers, config) {
                            ////console.log('file ' + config.file.name + 'uploaded. Response: ' +
                            // JSON.stringify(data));
                        });
                    }

                }

            };

            $scope.uploadAdditionalDocument = function () {
                for (var i = 0; i < $scope.additionalDocumentList.length; i++) {
                    var document = $scope.additionalDocumentList[i];
                    var files = document.documentAttached;
                    $scope.additional=true;
                    if (files) {
                        $upload.upload({
                            url: '/pla/individuallife/proposal/uploadmandatorydocument',
                            file: files,
                            fields: {documentId: document.documentName, proposalId:  $scope.proposal.proposalId,mandatory:false},
                            method: 'POST'
                        }).progress(function (evt) {

                        }).success(function (data, status, headers, config) {
                            ////console.log('file ' + config.file.name + 'uploaded. Response: ' +
                            // JSON.stringify(data));
                        });
                    }

                }
            };

            if ($scope.documentList) {
                if ($scope.documentList.documentAttached) {
                    if ($scope.documentList.documentAttached.length == $scope.documentList.documentName.length) {
                        $scope.disableUploadButton = true;
                        //console.log($scope.documentList.documentAttached.length);
                        //console.log($scope.documentList.documentName.length);
                    } else {
                        $scope.disableUploadButton = false;
                    }
                }
            }

            var method = getQueryParameter("method");

            if ( method == 'approval' || statusReturn == 'return') {
                $scope.isViewMode = true;

                if(statusReturn == 'return')
                {
                    $scope.returnStatusCheck=true;
                    $scope.showSubmitStatus=true;
                }
                //$scope.returnStatusCheck=true;
                //alert("approval");
                //alert("ProposalID: "+$scope.proposalId);

                $http.get("/pla/individuallife/proposal/getapprovercomments/" + $scope.proposalId).success(function (data, status) {
                    // //console.log(data);
                    $scope.approvalCommentList=data;
                    //alert(JSON.stringify(data));
                    //console.log("Response of Approval"+JSON.stringify(data));
                });

            }

            $scope.comment='';
            $scope.approveProposal = function(comment){
                var request = angular.extend({comment: comment},
                    {"proposalId": $scope.proposalId},{"status":"APPROVED"});

                $http.post('/pla/individuallife/proposal/approve', request).success(function (data) {
                    if(data.status==200){

                        $window.location.href="/pla/individuallife/proposal/openapprovalproposal";

                    }

                });
            }

            $scope.returnProposal = function(comment){
                var request = angular.extend({comment: comment},{"proposalId": $scope.proposalId},{"status":"RETURNED"});

                $http.post('/pla/individuallife/proposal/approve', request).success(function (data) {
                    if(data.status==200){
                        $window.location.href="/pla/individuallife/proposal/openapprovalproposal";
                    }

                });
            }

            $scope.holdProposal = function(comment){
                // alert(comment);
                var request = angular.extend({"comment": comment},{"proposalId": $scope.proposalId},{"status":"PENDING_DECISION"});

                $http.post('/pla/individuallife/proposal/approve', request).success(function (data) {
                    if(data.status==200){
                        $window.location.href="/pla/individuallife/proposal/openapprovalproposal";
                        //
                    }

                });
            }

            $scope.routeToNextLevel = function(comment){
                // alert(comment);
                var request = angular.extend({"comment": comment},{"proposalId": $scope.proposalId},{"status":"UNDERWRITING_LEVEL_TWO"});

                $http.post('/pla/individuallife/proposal/approve', request).success(function (data) {
                    if(data.status==200){
                        $window.location.href="/pla/individuallife/proposal/openapprovalproposal";
                    }

                });
            }

            $scope.rejectProposal = function(comment){
                var request = angular.extend({comment: comment},{"proposalId": $scope.proposalId},{"status":"DECLINED"});

                $http.post('/pla/individuallife/proposal/approve', request).success(function (data) {
                    if(data.status==200){


                        $window.location.href="/pla/individuallife/proposal/openapprovalproposal";

                    }

                });
            }
            $scope.beneficiariesList =[];
            $scope.addBeneficiary = function (beneficiary) {
                alert('Inside addBeneficiary Method..');
                alert("Length is:"+$scope.beneficiariesList.length);
                //console.log("List..: "+JSON.stringify(beneficiariesList));
                /*if ($scope.beneficiariesList.length == 0) {
                 //console.log('Object Is..' + beneficiary);
                 $scope.beneficiariesList.push(beneficiary);
                 //$scope.test(agent);
                 //   $scope.clear();
                 }*/
                if ($scope.beneficiariesList.length == 0) {
                    ////console.log('Length is Null..'+$scope.agentDetails.length);
                    //$scope.beneficiariesList.unshift(beneficiary);
                    $scope.beneficiariesList.push(beneficiary);
                    //console.log("List..: "+JSON.stringify(beneficiariesList));
                    alert("Object Is.." + JSON.stringify(beneficiary));
                }

                else {
                    alert('Length is greater Then 0');
                    var checkLoopNameStatus = "true";
                    for (i in $scope.beneficiariesList) {
                        if ($scope.beneficiariesList[i].nrc == beneficiary.nrc) {
                            checkLoopNameStatus = "false";
                            break;
                        } else if( ($scope.beneficiariesList[i].firstName == beneficiary.firstName) &&
                            ($scope.beneficiariesList[i].gender == beneficiary.gender) && ($scope.beneficiariesList[i].dateOfBirth == beneficiary.dateOfBirth) ) {
                            checkLoopNameStatus = "false";
                            break;
                        }
                    }

                    if(checkLoopNameStatus == "true") {
                        $scope.beneficiariesList.unshift(beneficiary);
                        //alert("Object Is.." + JSON.stringify(beneficiary));
                        //$scope.clear();
                        // $('#beneficiaryModal').modal('hide');
                    } else {
                        alert("All Are Same...");
                        //$scope.clear();
                        // $('#beneficiaryModal').modal('hide');
                    }
                }
                $scope.clear();
                $('#beneficialModal').modal('hide');
                //$scope.clear();

            };

            $scope.showDob = function (dob) {
                //console.log('Dob Calculation..');
                //console.log('DOB is..' + JSON.stringify(dob));
                if(dob != null)
                {
                    //console.log('Not Null..');
                    $scope.proposedAssured.nextDob = moment().diff(new moment(new Date(dob)), 'years') + 1;
                }
                //$scope.proposedAssured.nextDob = moment().diff(new moment(new Date(dob)), 'years') + 1;
                ////console.log('DOB Show is'+$scope.proposedAssured.nextDob);
            };

            $scope.showBeneficiaryDob = function (dob) {
                //console.log('Dob Calculation..');
                //console.log('DOB' + JSON.stringify(dob));
                if(dob != null)
                {
                    $scope.beneficiary.age = moment().diff(new moment(new Date(dob)), 'years');
                }
                $scope.addBeneficiaryStatusCheck=false;
                //$scope.beneficiary.age = moment().diff(new moment(new Date(dob)), 'years');

                if(($scope.beneficiary.age <1) && ($scope.beneficiary.age >16))
                {
                    alert('Less then  Condition..');
                    $scope.addBeneficiaryStatusCheck=true;
                }
                else
                {
                    $scope.addBeneficiaryStatusCheck=false;
                }
            };
            /*$scope.addBeneficiaryStatusCheck=false;

             $scope.$watch('beneficiary.age',function(newVal,oldVal)
             {
             if(newVal)
             {
             if( (newVal >=1) &&(newVal<=16))
             {

             }
             }
             });
             */
            $scope.showProposerDob = function (dob) {
                //console.log('Dob Calculation..');
                //console.log('DOB' + JSON.stringify(dob));
                if(dob != null)
                {
                    $scope.proposer.nextDob = moment().diff(new moment(new Date(dob)), 'years') + 1;
                }
                //$scope.proposer.nextDob = moment().diff(new moment(new Date(dob)), 'years') + 1;

            };
            $scope.proposalPlanDetail =
            {
                "planId": null,
                "policyTerm": null,
                "premiumPaymentTerm": null,
                "sumAssured": null
            };

            $scope.savePlanDetail = function () {
                //console.log('Save Plan');
                console.log("Minimum Age:" + $scope.plan.planDetail.minEntryAge);
                console.log("Maximum Age:" + $scope.plan.planDetail.maxEntryAge);
                console.log("Next DOb:" + $scope.proposedAssured.nextDob);

                if(($scope.proposedAssured.nextDob < $scope.plan.planDetail.minEntryAge) || ($scope.proposedAssured.nextDob > $scope.plan.planDetail.maxEntryAge))
                {
                    alert("Please select proposedAssured date of birth in Between" +$scope.plan.planDetail.minEntryAge + "And"+ $scope.plan.planDetail.maxEntryAge);
                    return;
                }
                var tempRequest = {
                    "riderDetails": $scope.searchRiders
                };
                tempRequest = angular.extend($scope.proposalPlanDetail, tempRequest);

                var request = {
                    "proposalPlanDetail": tempRequest,
                    //"proposalPlanDetail":$scope.proposalPlanDetail,
                    //"riderDetails": $scope.searchRiders,
                    "riderDetails":$scope.proposalPlanDetail.riderDetails,
                    "beneficiaries": $scope.beneficiariesList,
                    "proposalId": $scope.proposal.proposalId
                };
                //console.log('Final to Plan DB..' + JSON.stringify(request));

                $http.post('updateplan', request).success(function (response, status, headers, config) {
                    $scope.proposal = response;
                    $scope.proposal.proposalId=response.id;
                    //console.log("Response of PLan is:"+JSON.stringify(response));
                    //alert('proposalId : ' + $scope.proposal.proposalId);

                    //alert("Before GetPremium...");
                    //Getting PremiumDetails Documents
                    $http.get("getpremiumdetail/"+$scope.proposal.proposalId).success(function (response, status, headers, config) {
                        $scope.premiumResponse=response;
                        if($scope.premiumResponse != null)
                        {
                            ////alert('Premium is Present');

                        }
                    }).error(function (response, status, headers, config) {
                    });


                    // for generating document Details
                    //alert("Before GetDocument: "+$scope.proposal.proposalId);
                    $http.get("getmandatorydocuments/" + $scope.proposal.proposalId)
                        .success(function (response) {
                            $scope.documentList = response;
                            // //alert("documentResponse: "+JSON.stringify(response))
                            //console.log('documentResponse:'+JSON.stringify(response));
                            //console.log("documentList: "+JSON.stringify($scope.documentList))
                            //console.log('DocumentList Details..'+JSON.stringify(response));
                        });

                }).error(function (response, status, headers, config) {
                });
                //console.log('proposalPlanDetail' + JSON.stringify(request));

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
            //$scope.familyHistory = {father: {}, mother: {}, brother: {}, sister: {},question_16: {}};
            $scope.familyHistory = {father: {}, mother: {}, brother: {}, sister: {}, closeRelative: {}};
            /*$scope.habit = {question_17: {}, question_18: {}, question_19: {}, question_20: {}};*/
            //$scope.questions=[];

            $scope.build = {overWeightQuestion: {}};
            $scope.compulsoryHealthDetails = [];
            //$scope.questions=[];

            $scope.saveCompulsoryQuestionDetails = function () {
                //console.log('SaveMethod ofCompulsoryQuestionDetails');
                var request =
                {
                    "compulsoryHealthDetails": $scope.compulsoryHealthDetails,
                    "proposalId": $scope.proposal.proposalId
                };
                //console.log('Json to save to DB is:' + JSON.stringify(request));
                $http.post('/pla/individuallife/proposal/updatecompulsoryhealthstatement', request).success(function (response, status, headers, config) {
                    //$scope.proposal.proposalId=response.id;
                    //alert("ProposalId"+$scope.proposal.proposalId);
                }).error(function (response, status, headers, config) {
                });

            };
            //console.log('Question in Habit is' + $scope.habit);
            $scope.questionList = [];
            $scope.habits = {};
            $scope.generalQuestion = [];
            $scope.saveGeneralDetails = function () {
                //console.log('Inside saveGeneraLDetails Method..');
                //console.log('Checking GeneralStatus:--> '+$scope.generalAnswer);

                var assuredByPLAL =
                {
                    "answerResponse": $scope.policyDetails,
                    "questionId": "1",
                    //"answer": $scope.generalAnswer
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

                //console.log('generalQuestion is: ' + JSON.stringify(generalQuestion));
                //console.log('Final General is' + JSON.stringify(req));
                $http.post('/pla/individuallife/proposal/updategeneraldetails',req).success(function (response, status, headers, config) {
                    $scope.occupations = response;
                    //alert(JSON.stringify($scope.occupations));
                    $scope.proposal.proposalId=response.id;

                }).error(function (response, status, headers, config) {
                });

            };

            $scope.saveFamilyHistory = function () {
                //console.log($scope.isPregnant);
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
                //console.log('List..' + JSON.stringify(listA));

                request = angular.extend($scope.familyPersonalDetail, request);
                request = {
                    "familyPersonalDetail": request,
                    "proposalId": $scope.proposal.proposalId
                };
                //console.log('request ' + JSON.stringify(request));
                $http.post('/pla/individuallife/proposal/updatefamily', request).success(function (response, status, headers, config) {
                    $scope.proposal.proposalId=response.id;
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

            $scope.selectPlanResponse=false;

            $scope.printValue = function (){
                //console.log($scope.selectedPlan);
            }

            $scope.applicableRelationships=[];

            $scope.getMax = function () {
                console.log('getMax');
                return 999999.99;
            }

            $scope.getPregnencymonth=function()
            {
                console.log('getPregnencymonth');
                return 10;
            }
            /*$scope.getBuildHeightMax=function()
            {
                console.log('getBuildHeightMax');
                return 999.99;
            }*/
            $scope.getBeneficiaryMinAge = function () {
                console.log('getBeneficiaryMinAge');
                return 1;
            }
            $scope.getBeneficiaryMaxAge = function () {
                console.log('getBeneficiaryMaxAge');
                return 16;
            }
            $scope.planSelected = function (newValue) {
                //console.log('Watching is:'+JSON.stringify(newValue));
                if (newValue && newValue.description && newValue.description.plan_id) {

                    ////alert('Looking in Plan');
                    //console.log('Plan ID Is'+newValue.description.plan_id);
                    /*newValue.description.plan_id*/
                    //55925543e4b01ecacd0635c4
                    //newValue.description.plan_id

                    $scope.selectPlanResponse=true

                    $http.get('/pla/core/plan/getPlanById/' + newValue.description.plan_id)
                        .success(function (response) {
                            $scope.plan = response;
                            $scope.applicableRelationships=response.planDetail.applicableRelationships;
                            console.log('PLanDetails..'+JSON.stringify(response));
                            //alert("applicableRelationships"+JSON.stringify($scope.applicableRelationships));
                        });


                    $scope.proposalPlanDetail.planId=newValue.description.plan_id;
                    $http.get("getridersforplan/" + newValue.description.plan_id).success(function (response, status, headers, config) {
                        $scope.searchRiders = response;
                        $scope.proposalPlanDetail.riderDetails = response;
                        //console.log('Riders Details From Db is:' +JSON.stringify(response));
                        //console.log($scope.searchRiders);

                    }).error(function (response, status, headers, config) {
                        var check = status;
                        //console.log('Checking Status:'+JSON.stringify(check));
                    });
                }
            };
            $scope.termTypeCover={};

            $scope.$watch('proposalPlanDetail.policyTerm',function(newValue,oldValue){
                if(newValue)
                {

                    $scope.termTypeCover=newValue;
                    $scope.searchRider.coverTerm=$scope.termTypeCover;

                }
            });
            /* $scope.setpolicytermTypeValue=function(termType)
             {
             alert("Test Change..*");
             $scope.termTypeCover=termType;
             }*/


            $scope.getpolicytermTypeValue=function()
            {


                //$scope.termTypeCover=termType;
                return $scope.termTypeCover;
            }
            $scope.getProposerEmpProvinceValue = function(province){
                //alert(province);
                var provinceDetails = _.findWhere($scope.provinces, {provinceId:province});
                if (provinceDetails)
                    $scope.proposerEmploymentCities = provinceDetails.cities;
            }

            $scope.getProposerResProvinceValue = function(province){
                //alert(province);
                var provinceDetails = _.findWhere($scope.provinces, {provinceId:province});
                if (provinceDetails)
                    $scope.proposerResidentialCities = provinceDetails.cities;
            }

            $scope.getProposedAssuredEmpProvinceValue = function(province){
                //alert(province);
                var provinceDetails = _.findWhere($scope.provinces, {provinceId:province});
                if (provinceDetails)
                    $scope.proposedAssuredEmploymentCities = provinceDetails.cities;
            }

            $scope.getProposedAssuredResProvinceValue = function(province){
                //alert(province);
                var provinceDetails = _.findWhere($scope.provinces, {provinceId:province});
                if (provinceDetails)
                    $scope.proposedAssuredResidentialCities = provinceDetails.cities;
            }

            $scope.getProposeAssuredEmpType = function(empType){
                //alert(empType);
                var employeeTypes = _.findWhere($scope.employmentTypes, {code:empType});
                if (employeeTypes)
                    $scope.employment.employmentType = employeeTypes.employment_id;
            }

            $scope.getProposerEmpType = function(empType){
                //alert(empType);
                var employeeTypes = _.findWhere($scope.employmentTypes, {code:empType});
                if (employeeTypes)
                    $scope.proposerEmployment.employmentType = employeeTypes.employment_id;
            }

            /* $scope.$watchGroup(['employment.province', 'residentialAddress.province', 'proposerEmployment.province', 'proposerResidential.province', 'employment.employmentType', 'employment.province','proposerEmployment.employmentType'], function (newVal, oldVal) {
             // if (!newVal) return;
             console.log(newVal[2]);
             if (newVal[0]) {
             var provinceDetails = _.findWhere($scope.provinces, {provinceId: newVal[0]});
             if (provinceDetails)
             $scope.proposedAssuredEmploymentCities = provinceDetails.cities;
             }
             if (newVal[1]) {
             var provinceDetails = _.findWhere($scope.provinces, {provinceId: newVal[1]});
             if (provinceDetails)
             $scope.proposedAssuredResidentialCities = provinceDetails.cities;
             }
             if (newVal[2]) {
             alert(proposerEmployment.province);
             console.log('employment ' + newVal[2]);
             var provinceDetails = _.findWhere($scope.provinces, {provinceId: newVal[2]});
             if (provinceDetails)
             $scope.proposerEmploymentCities = provinceDetails.cities;
             }
             if (newVal[3]) {
             //console.log('residential ' + newVal[3]);
             var provinceDetails = _.findWhere($scope.provinces, {provinceId: newVal[3]});
             if (provinceDetails)
             $scope.proposerResidentialCities = provinceDetails.cities;

             }

             if (newVal[4]) {
             var employeeTypes = _.findWhere($scope.employmentTypes, {code: newVal[4]});
             if (employeeTypes) {
             //console.log(employeeTypes.employment_id);
             $scope.employment.employmentType = employeeTypes.employment_id;
             }
             }

             if (newVal[5]) {
             //console.log('Town: ' + newVal[5]);
             }

             if (newVal[6]) {
             var proposeremployeeTypes = _.findWhere($scope.employmentTypes, {code: newVal[6]});
             if (proposeremployeeTypes) {
             //alert("proposeremployeeTypes"+proposeremployeeTypes.employment_id);
             $scope.proposerEmployment.employmentType = proposeremployeeTypes.employment_id;
             }
             }
             });
             */
            $scope.selectSpouse = function (maritalStatusCheck) {
                //console.log("MaritialStatus:" + maritalStatusCheck);
                //var choice = $scope.proposedAssured.maritalStatus;
                ////alert("Maritial Status"+maritalStatusCheck);

                if(maritalStatusCheck == "MARRIED")
                {
                    $scope.spouseChoice=true;
                }
                else{
                    $scope.spouseChoice=false;
                }
                /*if (choice != "MARRIED") {
                 $scope.choose = true;
                 }
                 else {
                 $scope.choose = false;
                 }*/
            };

            $scope.agentDetails = [];
            $scope.flag = false;
            $scope.countCheck = false;
            $scope.policyDetails = [];
            $scope.insurerDetails1 = [];
            $scope.insurerDetails2 = [];
            $scope.insurerDetails3 = [];


            $scope.addPolicyDetails = function (policy) {
                //console.log('Inside Add PolicyDetails..');
                //console.log(JSON.stringify(policy));
                //$scope.policyDetails.unshift(policy)
                //$scope.policyDetails.push(policy);

                if ($scope.policyDetails.length == 0) {
                    //console.log('Lenght is Null..');
                    $scope.policyDetails.push(policy);
                }
                else
                {
                    var checkLoopNameStatus = "true";
                    for (i in $scope.policyDetails) {
                        if ($scope.policyDetails[i].policyOrProposalNumber == policy.policyOrProposalNumber) {
                            //console.log('Failure..');
                            checkLoopNameStatus=false;
                            break;
                        }

                    }

                    if(checkLoopNameStatus == "true")
                    {
                        $scope.policyDetails.unshift(policy);
                    }
                    else
                    {
                        alert("Please Select Different Proposal Number");
                    }
                }

                /*if ($scope.policyDetails.length == 0) {
                 //console.log('Lenght is Null..');
                 $scope.policyDetails.unshift(policy);
                 }
                 else {

                 for (i in $scope.policyDetails) {
                 if ($scope.policyDetails[i].policyOrProposalNumber == policy.policyOrProposalNumber) {
                 //console.log('Failure..');
                 alert("Particular PolicyNumber is Already Added..Please Choose different PolicyNumber");
                 }
                 else {
                 $scope.policyDetails.unshift(policy);
                 }
                 }

                 }*/
                $('#policyModal').modal('hide');
                $scope.clear();
            };

            $scope.cancelModal=function()
            {
                $('#policyModal').modal('hide');
                $('#assuredByOthersModal').modal('hide');
                $('#pendingInsuranceByOthersTpl').modal('hide');
                $('#assuranceDeclinedTpl').modal('hide');
                $scope.clear();
            }

            $scope.addAssuredByOthers = function (insurer1) {
                //console.log('Inside Add addAssuredByOthers..');
                //console.log(JSON.stringify(insurer1));

                if ($scope.insurerDetails1.length == 0) {
                    //console.log('Lenght is Null..');
                    $scope.insurerDetails1.unshift(insurer1);
                }
                else {
                    var checkLoopNameStatus = "true";
                    for (i in $scope.insurerDetails1) {
                        if ($scope.insurerDetails1[i].policyOrProposalNumber == insurer1.policyOrProposalNumber) {
                            //console.log('Failure..');
                            //alert("Particular PolicyNumber is Already Added..Please Choose different PolicyNumber");
                            checkLoopNameStatus = "false";
                            break;
                        }
                    }

                    if(checkLoopNameStatus == "true")
                    {
                        $scope.insurerDetails1.unshift(insurer1);
                    }
                    else
                    {
                        alert("Please Select Different Proposal Number");
                    }
                    /* for (i in $scope.insurerDetails1) {
                     if ($scope.insurerDetails1[i].policyOrProposalNumber == insurer1.policyOrProposalNumber) {
                     //console.log('Failure..');
                     //alert("Particular PolicyNumber is Already Added..Please Choose different PolicyNumber");
                     }
                     else {
                     $scope.insurerDetails1.unshift(insurer1);
                     }
                     }*/

                }
                $('#assuredByOthersModal').modal('hide');
                $scope.clear();
            };


            $scope.addPendingInsuranceByOthersTpl = function (insurer2) {
                //console.log('Inside Add addPendingInsuranceByOthersTpl..');
                //console.log(JSON.stringify(insurer2));
                //$scope.policyDetails.unshift(policy)
                //$scope.insurerDetails2.push(insurer2);

                if ($scope.insurerDetails2.length == 0) {
                    //console.log('Lenght is Null..');
                    $scope.insurerDetails2.unshift(insurer2);
                }
                else
                {
                    var checkLoopNameStatus = "true";

                    for (i in $scope.insurerDetails2) {
                        if ($scope.insurerDetails2[i].policyOrProposalNumber == insurer2.policyOrProposalNumber) {
                            //console.log('Failure..');
                            //alert("Particular PolicyNumber is Already Added..Please Choose different PolicyNumber");
                            checkLoopNameStatus = "false";
                            break;
                        }
                    }

                    if(checkLoopNameStatus == "true")
                    {
                        $scope.insurerDetails2.unshift(insurer2);
                    }
                    else
                    {
                        alert("Please Select Different Proposal Number");
                    }
                }
                /* if ($scope.insurerDetails2.length == 0) {
                 //console.log('Lenght is Null..');
                 $scope.insurerDetails2.unshift(insurer2);
                 }
                 else {

                 for (i in $scope.insurerDetails2) {
                 if ($scope.insurerDetails2[i].policyOrProposalNumber == insurer2.policyOrProposalNumber) {
                 //console.log('Failure..');
                 //alert("Particular PolicyNumber is Already Added..Please Choose different PolicyNumber");
                 }
                 else {
                 $scope.insurerDetails2.unshift(insurer2);
                 }
                 }

                 }*/
                $('#pendingInsuranceByOthersTpl').modal('hide');
                $scope.clear();
            };

            $scope.addaAssuranceDeclinedTpl = function (insurer3) {
                //console.log('Inside  Add AssuranceDeclinedTpl..');
                //console.log(JSON.stringify(insurer3));

                if ($scope.insurerDetails3.length == 0) {
                    //console.log('Lenght is Null..');
                    //$scope.insurerDetails3.unshift(insurer3);
                    $scope.insurerDetails3.push(insurer3);
                }
                else {
                    var checkLoopNameStatus = "true";
                    for (i in $scope.insurerDetails3) {
                        if ($scope.insurerDetails3[i].policyOrProposalNumber == insurer3.policyOrProposalNumber) {
                            //console.log('Failure..');
                            //alert("Particular ProposalNumber is Already Added..Please Choose different PolicyNumber");
                            checkLoopNameStatus = "false";
                            break;
                        }
                        /* else {
                         $scope.insurerDetails3.unshift(insurer3);
                         }*/
                    }

                    if(checkLoopNameStatus == "true")
                    {
                        $scope.insurerDetails3.unshift(insurer3);
                    }
                    else
                    {
                        alert("Please Select Different Proposal Number");
                    }
                }
                $('#assuranceDeclinedTpl').modal('hide');
                $scope.clear();
            };

            /* $scope.addBeneficiaryList=function(beneficiary)
             {
             alert('Test');
             }*/

            $scope.addAgent = function (agent) {
                //console.log('Inside addagent Method..');

                if ($scope.agentDetails.length == 0) {
                    ////console.log('Length is Null..'+$scope.agentDetails.length);
                    $scope.agentDetails.unshift(agent);
                    //$scope.test(agent);
                }
                else {

                    for (i in $scope.agentDetails) {
                        if ($scope.agentDetails[i].agentId == agent.agentId) {
                            //console.log('Failure..');
                            //alert("Particular AgentId is Already Added..Please Choose different AgentId");
                        }
                        else {
                            $scope.agentDetails.unshift(agent);
                        }
                    }
                    //$scope.test(agent);

                }
                $('#agentModal').modal('hide');
                $scope.clear();
            };

            $scope.statusCount = true;
            $scope.agentMessage=false;

            $scope.commisionSumTest = function () {
                var sum = 0;
                for (i in $scope.agentDetails) {

                    sum = parseFloat(sum) + parseFloat($scope.agentDetails[i].commission);
                }
                //console.log('sum: ' + sum);
                if (sum == 100.00) {
                    $scope.statusCount = false;
                    $scope.agentMessage=false;
                }
                else {
                    $scope.statusCount = true;
                    $scope.agentMessage=true;
                }
            };
            $scope.commisionStatus=true;
            $scope.commisionMessage=false;


            $scope.shareSumTest = function () {
                var sum = 0;
                for (i in $scope.beneficiariesList) {

                    sum = parseFloat(sum) + parseFloat($scope.beneficiariesList[i].share);
                }
                //console.log('sum: ' + sum);
                if (sum == 100.00) {
                    $scope.commisionStatus = false;
                    //$scope.commisionMessage=false;
                }
                else {
                    $scope.commisionStatus = true;
                    //$scope.commisionMessage=true;
                }
            };


            /* $scope.test = function (row) {
             //console.log('Testing...');
             //console.log('Pass..' + JSON.stringify(row));
             for (i in $scope.agentDetails) {
             if ($scope.agentDetails[i].agentId == row.agentId) {
             $scope.agentDetails[i] = row;
             }
             }

             var countCheck = $scope.countStatus();
             //console.log('Checking-->' + JSON.stringify(countCheck));
             if (countCheck != 100) {
             //console.log('Not 100');
             $scope.statusCount = true;
             }
             else {
             $scope.statusCount = false;
             }

             };*/

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
                $scope.agent = {};
                $scope.policy = {};
                $scope.insurer1 = {};
                $scope.insurer2 = {};
                $scope.insurer3 = {};
                $scope.beneficiary={};
            };

            $scope.myFunction=function()
            {
                //alert('Testing For Riders..');
            }

            $scope.searchRiders = function (proposalPlanId) {
                //console.log('Search Riders Function..' + $scope.planId);
            }

            $scope.searchAgent = function () {
                $scope.check = false;
                $scope.checking = true;
                //console.log('Testing In SearchCode..');
                $scope.agentId = $scope.agent.agentId;
                //console.log('Value is: ' + $scope.agentId);
                $http.get("getagentdetail/" + $scope.agentId).success(function (response, status, headers, config) {
                    $scope.agent = response;
                    $scope.checking = false;
                }).error(function (response, status, headers, config) {
                    var check = status;
                    if (check == 500) {
                        $scope.check = true;
                        $scope.agent.firstName = null;
                        $scope.agent.lastName = null;
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
                    $scope.proposal.proposalId=response.id;
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

                var request1={
                    "proposedAssured":$scope.proposedAssured,
                    "proposalId":$scope.proposal.proposalId
                }

                ////console.log('Save Proposer'+JSON.stringify(request1));

                $http.post('updateproposedassuredandagent', prorequest).success(function (response, status, headers, config) {
                    $scope.proposal = response;
                    $scope.proposal.proposalId=response.id;

                }).error(function (response, status, headers, config) {
                });
            };

            /*$scope.saveProposedAssuredDetails = function () {
             //ProposalService.saveProposedAssured($scope.proposedAssured, $scope.proposedAssuredSpouse, $scope.paemployment, $scope.paresidential, proposedAssuredAsProposer, null);

             ////console.log('modeType' + $scope.mode);
             ////alert("modeType"+$scope.mode);
             var request = {
             "spouse": $scope.spouse,
             "employment": $scope.employment,
             "residentialAddress": $scope.residentialAddress
             };

             request = angular.extend($scope.proposedAssured, request);

             var request1 = {
             "proposedAssured": $scope.proposedAssured,
             "agentCommissionDetails": $scope.agentDetails
             }


             var request3=
             {
             "proposedAssured": $scope.proposedAssured,
             "agentCommissionDetails": $scope.agentDetails,
             "proposalId": $scope.proposal.proposalId
             }

             //console.log('Final Result ' + JSON.stringify(request1));
             ////console.log('Update Result ' + JSON.stringify(request3));

             if ($scope.quotationIdDetails.quotationId != null && $scope.quotationIdDetails.quotationId != " ") {
             ////alert('Checking...');
             var request2 = {
             "proposedAssured": $scope.proposedAssured,
             "agentCommissionDetails": $scope.agentDetails,
             "quotationId": $scope.quotationIdDetails.quotationId
             }
             //console.log('**** Saving QuotationId..');
             //console.log(JSON.stringify(request2));

             // QuotationID saving

             $http.post('create', request2).success(function (response, status, headers, config) {
             $scope.proposal = response;
             $scope.proposal.ProposalId=response.id;
             //alert("ProposalId Check"+JSON.stringify($scope.proposal.ProposalId));
             //console.log('Retrieving Proposal Number@###..');
             $http.get("/pla/individuallife/proposal/getproposal/" +$scope.proposal.proposalId + "?mode=view").success(function (response, status, headers, config) {
             window.location.href = "/pla/individuallife/proposal/edit?proposalId=" + $scope.proposal.ProposalId + "&mode=edit";
             var result = response;
             //console.log('Result:' + JSON.stringify(result));
             $scope.rcvProposal = response;
             $scope.proposalNumberDetails.proposalNumber = $scope.rcvProposal.proposalNumber;
             $scope.proposal.proposalId = $scope.rcvProposal.proposalId;
             $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};
             if($scope.rcvProposal.proposer !=null)
             {
             ////alert("Yes");
             $scope.proposer = $scope.rcvProposal.proposer || {};
             $scope.proposerEmployment = $scope.proposer.employment;
             $scope.proposerResidential = $scope.proposer.residentialAddress;
             $scope.proposerSpouse = $scope.proposer.spouse;
             }
             else
             {
             ////alert("No");
             $scope.proposer={};
             $scope.proposerEmployment={};
             $scope.proposerResidential={};
             $scope.proposerSpouse={};
             }

             *//* $scope.proposer = $scope.rcvProposal.proposer || {};
             $scope.proposerEmployment = $scope.proposer.employment;
             $scope.proposerResidential = $scope.proposer.residentialAddress;
             $scope.proposerSpouse = $scope.proposer.spouse;*//*

             $scope.proposalPlanDetail = $scope.rcvProposal.proposalPlanDetail;
             $scope.searchRiders = $scope.rcvProposal.proposalPlanDetail.riderDetails;
             $scope.beneficiaries = $scope.rcvProposal.beneficiaries;
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
             //$scope.familyPersonalDetail = $scope.rcvProposal.familyPersonalDetail;
             ////console.log('FamilyHistory..' + $scope.rcvProposal.familyPersonalDetail.familyHistory.father);
             //$scope.familyHistory = $scope.rcvProposal.familyPersonalDetail.familyHistory;
             //$scope.habit = $scope.rcvProposal.familyPersonalDetail.habit;
             //$scope.habits = $scope.rcvProposal.familyPersonalDetail.habit;
             //$scope.questionList = $scope.rcvProposal.familyPersonalDetail.habit.questions;
             //$scope.build = $scope.rcvProposal.familyPersonalDetail.build;
             //$scope.compulsoryHealthDetails = $scope.rcvProposal.compulsoryHealthStatement;


             }).error(function (response, status, headers, config) {
             });

             //Testing
             }).error(function (response, status, headers, config) {
             });

             }

             else {
             ////console.log('Checking in SaveProposer');
             ////alert("No QID..");

             if($scope.mode == "edit")
             {
             ////alert("Mode is.."+$scope.mode);
             //console.log('For Edit Mode Test Is ##########');
             //console.log(JSON.stringify(request3));
             $http.post('updateproposedassuredandagent', request3).success(function (response, status, headers, config) {
             window.location.reload();
             })

             .error(function (response, status, headers, config) {
             });
             }
             ////alert("modeType"+$scope.mode);

             else
             {
             // For New Proposal Save...
             $http.post('create', request1).success(function (response, status, headers, config) {
             $scope.proposal.ProposalId=response.id;
             // //alert("ProposalId Check"+JSON.stringify($scope.proposal.ProposalId));
             window.location.href = "/pla/individuallife/proposal/edit?proposalId=" + $scope.proposal.ProposalId + "&mode=edit";
             $scope.proposal = response;
             $scope.proposal.proposalId=response.id;
             //console.log('Retrieving Proposal Number..');
             $http.get("/pla/individuallife/proposal/getproposal/" + $scope.proposal.proposalId).success(function (response, status, headers, config) {
             var result = response;
             //console.log('Result:' + JSON.stringify(result));
             //window.location.href = "/pla/individuallife/proposal/edit?proposalId=" + response.proposalId + "&mode=edit";
             $scope.rcvProposal = response;
             *//* $scope.proposal=
             {
             "msg":null,
             "proposalId":null
             };*//*

             //var proposerDetails=$scope.rcvProposal.proposer;
             ////console.log("*** Show Proposer Details....");
             ////console.log(JSON.stringify(proposerDetails));

             $scope.proposalNumberDetails.proposalNumber = $scope.rcvProposal.proposalNumber;
             $scope.proposal.proposalId = $scope.rcvProposal.proposalId;
             $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};

             if($scope.rcvProposal.proposer !=null)
             {
             ////alert("Yes");
             $scope.proposer = $scope.rcvProposal.proposer || {};
             $scope.proposerEmployment = $scope.proposer.employment;
             $scope.proposerResidential = $scope.proposer.residentialAddress;
             $scope.proposerSpouse = $scope.proposer.spouse;
             }
             else
             {
             ////alert("No");
             $scope.proposer={};
             $scope.proposerEmployment={};
             $scope.proposerResidential={};
             $scope.proposerSpouse={};
             }

             if($scope.rcvProposal.familyPersonalDetail != null)
             {
             //console.log('FamilyHistory..' + $scope.rcvProposal.familyPersonalDetail.familyHistory.father);
             $scope.familyHistory = $scope.rcvProposal.familyPersonalDetail.familyHistory;
             $scope.habit = $scope.rcvProposal.familyPersonalDetail.habit;
             $scope.habits = $scope.rcvProposal.familyPersonalDetail.habit;
             $scope.questionList = $scope.rcvProposal.familyPersonalDetail.habit.questions;
             $scope.build = $scope.rcvProposal.familyPersonalDetail.build;
             }
             if($scope.rcvProposal.compulsoryHealthStatement !=null)
             {
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

             //Testing
             }).error(function (response, status, headers, config) {
             });
             }
             *//*                        $http.post('create', request1).success(function (response, status, headers, config) {
             window.location.href = "/pla/individuallife/proposal/edit?proposalId=" + response.proposalId + "&mode=edit";
             $scope.proposal = response;
             //console.log('Retrieving Proposal Number..');
             $http.get("/pla/individuallife/proposal/getproposal/" + $scope.proposal.proposalId).success(function (response, status, headers, config) {
             var result = response;
             //console.log('Result:' + JSON.stringify(result));
             //window.location.href = "/pla/individuallife/proposal/edit?proposalId=" + response.proposalId + "&mode=edit";
             $scope.rcvProposal = response;
             *//**//* $scope.proposal=
             {
             "msg":null,
             "proposalId":null
             };*//**//*

             //var proposerDetails=$scope.rcvProposal.proposer;
             ////console.log("*** Show Proposer Details....");
             ////console.log(JSON.stringify(proposerDetails));

             $scope.proposalNumberDetails.proposalNumber = $scope.rcvProposal.proposalNumber;
             $scope.proposal.proposalId = $scope.rcvProposal.proposalId;
             $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};

             if($scope.rcvProposal.proposer !=null)
             {
             ////alert("Yes");
             $scope.proposer = $scope.rcvProposal.proposer || {};
             $scope.proposerEmployment = $scope.proposer.employment;
             $scope.proposerResidential = $scope.proposer.residentialAddress;
             $scope.proposerSpouse = $scope.proposer.spouse;
             }
             else
             {
             ////alert("No");
             $scope.proposer={};
             $scope.proposerEmployment={};
             $scope.proposerResidential={};
             $scope.proposerSpouse={};
             }

             if($scope.rcvProposal.familyPersonalDetail != null)
             {
             //console.log('FamilyHistory..' + $scope.rcvProposal.familyPersonalDetail.familyHistory.father);
             $scope.familyHistory = $scope.rcvProposal.familyPersonalDetail.familyHistory;
             $scope.habit = $scope.rcvProposal.familyPersonalDetail.habit;
             $scope.habits = $scope.rcvProposal.familyPersonalDetail.habit;
             $scope.questionList = $scope.rcvProposal.familyPersonalDetail.habit.questions;
             $scope.build = $scope.rcvProposal.familyPersonalDetail.build;
             }
             if($scope.rcvProposal.compulsoryHealthStatement !=null)
             {
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

             //Testing
             }).error(function (response, status, headers, config) {
             });*//*
             }
             *//* }*//*
             *//*else {
             //console.log('False..');
             $scope.updateFlag = true;
             $scope.agent//alert = true;
             }*//*
             };*/

            $scope.$watch('proposer.dateOfBirth',function(newvalue,oldvalue){
                if(newvalue){
                    ////alert(newvalue);
                    $scope.proposer.nextDob = moment().diff(new moment(new Date(newvalue)), 'years') + 1;
                    ////alert($scope.proposer.nextDob);
                }
            });

            $scope.ageCalculateStatus=false;
            $scope.$watch('proposer.nextDob',function(newvalue,oldvalue){
                //$scope.ageCalculate=0;
                if(newvalue){

                    //alert(newvalue);

                    if((parseInt(newvalue) <= 18 ) || (parseInt(newvalue) >=60)){
                        $scope.ageCalculateStatus=true;
                        alert("Same1....");
                        //alert(parseInt(newvalue));
                    }
                    else {
                        $scope.ageCalculateStatus=false;
                        //alert("Not Same..");
                        //alert(newvalue);
                    }
                }
            });

            $scope.beneficiary={};
            $scope.$watch('beneficiary.title', function (newVal, oldVal) {
                if (newVal) {
                    //alert(newVal);
                    if(newVal == 'Mr.')
                    {
                        $scope.beneficiary.gender='MALE';
                    }
                    else if((newVal == 'Miss') || (newVal == 'Mrs.') )
                    {
                        $scope.beneficiary.gender ='FEMALE';
                    }
                    else
                    {
                        $scope.beneficiary.gender ='';
                    }
                }
            });


            $scope.getProposedAssuredGender=function(newVal)
            {
                if (newVal) {
                    //alert(newVal);
                    if(newVal == 'Mr.')
                    {
                        $scope.proposedAssuredDetails.gender='MALE';
                    }
                    else if((newVal == 'Miss') || (newVal == 'Mrs.') )
                    {
                        $scope.proposedAssuredDetails.gender ='FEMALE';
                    }
                    else
                    {
                        $scope.proposedAssuredDetails.gender ='';
                    }
                }
            }
            $scope.getProposerGender=function(newVal)
            {
                if (newVal) {
                    //alert(newVal);
                    if(newVal == 'Mr.')
                    {
                        $scope.proposer.gender='MALE';
                    }
                    else if((newVal == 'Miss') || (newVal == 'Mrs.') )
                    {
                        $scope.proposer.gender ='FEMALE';
                    }
                    else
                    {
                        $scope.proposer.gender ='';
                    }
                }
            }
      /*      $scope.$watch('proposer.title', function (newVal, oldVal) {
                if (newVal) {
                    //alert(newVal);
                    if(newVal == 'Mr.')
                    {
                        $scope.proposer.gender='MALE';
                    }
                    else if((newVal == 'Miss') || (newVal == 'Mrs.') )
                    {
                        $scope.proposer.gender ='FEMALE';
                    }
                    else
                    {
                        $scope.proposer.gender ='';
                    }
                }
            });*/

            /**
             * Checking of  Occupation filed of  ProposedAssured Detail  to decide whether to display Employment Type Field
             * Address1, Address2 ,Province ,Work Phone & Postal CodeFields
             * */

            $scope.employmentTypeStaus=true;
            $scope.$watch('employment.occupation', function (newVal, oldVal) {
                if (newVal) {
                    //alert(newVal);
                    if((newVal == 'Student') || (newVal == 'Housewives'))
                    {
                        $scope.employmentTypeStaus=false;
                    }
                    else
                    {
                        $scope.employmentTypeStaus=true;
                    }
                }
            });

            /**
             * Checking of  Occupation filed of  Proposer Detail  to decide whether to display Employment Type Field
             * Address1, Address2 ,Province ,Work Phone & Postal CodeFields
             * */

            $scope.proposerEmploymentTypeStaus=true;
            $scope.$watch('proposerEmployment.occupation', function (newVal, oldVal) {
                if (newVal) {
                    //alert(newVal);
                    if((newVal == 'Student') || (newVal == 'Housewives'))
                    {
                        $scope.proposerEmploymentTypeStaus=false;
                    }
                    else
                    {
                        $scope.proposerEmploymentTypeStaus=true;
                    }
                }
            });


            /*$scope.$watch('proposerEmployment.employmentType',function(newvalue,oldvalue){
             if(newvalue){
             //alert(newvalue);
             //$scope.proposer.nextDob = moment().diff(new moment(new Date(newvalue)), 'years') + 1;
             ////alert($scope.proposer.nextDob);

             if(newvalue == 1)
             {
             $scope.proposerEmployment.employmentType="Permanent";
             }
             else
             {
             $scope.proposerEmployment.employmentType="Temporary";
             }
             }
             });*/

            $scope.saveProposerDetails = function () {

                //ProposalService.saveProposedAssured($scope.proposedAssured, $scope.proposedAssuredSpouse, $scope.paemployment, $scope.paresidential, proposedAssuredAsProposer, null);

                ////console.log('modeType' + $scope.mode);
                ////alert("modeType"+$scope.mode);
                var request = {
                    "spouse": $scope.proposerSpouse,
                    "employment": $scope.proposerEmployment,
                    "residentialAddress": $scope.proposerResidential
                };

                request = angular.extend($scope.proposer, request);

                var request1 = {
                    "proposer": $scope.proposer,
                    "agentCommissionDetails": $scope.agentDetails
                }


                var request3=
                {
                    "proposer": $scope.proposer,
                    "agentCommissionDetails": $scope.agentDetails,
                    "proposalId": $scope.proposal.proposalId
                }

                //console.log('Final Result ' + JSON.stringify(request1));
                ////console.log('Update Result ' + JSON.stringify(request3));

                if ($scope.quotationIdDetails.quotationId != null && $scope.quotationIdDetails.quotationId != " ") {
                    //alert("Quotation Save...");
                    /* var request2 = {
                     "proposedAssured": $scope.proposedAssured,
                     "agentCommissionDetails": $scope.agentDetails,
                     "quotationId": $scope.quotationIdDetails.quotationId
                     }*/
                    alert('employment'+JSON.stringify($scope.proposerResidential));

                    var request2 = {
                        //"proposer": $scope.proposer,
                        //"agentCommissionDetails": $scope.agentDetails,
                        //"quotationId": $scope.quotationIdDetails.quotationId,
                        "spouse": $scope.proposerSpouse,
                        "employment": $scope.proposerEmployment,
                        "residentialAddress": $scope.proposerResidential
                    }

                    request2 = angular.extend($scope.proposer, request2);
                    request2=
                    {
                        "proposer": $scope.proposer,
                        "agentCommissionDetails": $scope.agentDetails,
                        "quotationId": $scope.quotationIdDetails.quotationId
                    }

                    console.log('**** Saving QuotationId..');
                    console.log(JSON.stringify(request2));

                    // QuotationID saving

                    $http.post('create', request2).success(function (response, status, headers, config) {
                        //$scope.proposal = response;
                        $scope.proposal.ProposalId=response.id;
                        //alert("ProposalId Check"+JSON.stringify(response));
                        //alert('Proposal..'+JSON.stringify($scope.proposal.ProposalId));
                        //    console.log('Retrieving Proposal Number@###..');
                        $http.get("/pla/individuallife/proposal/getproposal/" +response.id + "?mode=view").success(function (response, status, headers, config) {
                            window.location.href = "/pla/individuallife/proposal/edit?proposalId=" + $scope.proposal.ProposalId + "&mode=edit";
                            var result = response;
                            $scope.rcvProposal = response;
                            $scope.agentDetails=result.agentCommissionDetails;
                            $scope.proposalNumberDetails.proposalNumber = $scope.rcvProposal.proposalNumber;
                            $scope.proposal.proposalId = $scope.rcvProposal.proposalId;
                            //$scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};
                            if($scope.rcvProposal.proposedAssured !=null)
                            {
                                ////alert("Yes");
                                $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};
                                $scope.spouse = $scope.rcvProposal.proposedAssured.spouse;
                                $scope.employment = $scope.rcvProposal.proposedAssured.employment;
                                $scope.residentialAddress = $scope.rcvProposal.proposedAssured.residentialAddress;
                            }
                            else
                            {
                                ////alert("No");
                                $scope.proposedAssured={};
                                $scope.employment={};
                                $scope.residentialAddress={};
                                $scope.spouse={};
                            }


                            if($scope.rcvProposal.proposer !=null)
                            {
                                ////alert("Yes");
                                $scope.proposer = $scope.rcvProposal.proposer || {};
                                $scope.proposerEmployment = $scope.proposer.employment;
                                $scope.proposerResidential = $scope.proposer.residentialAddress;
                                $scope.proposerSpouse = $scope.proposer.spouse;
                            }
                            else
                            {
                                ////alert("No");
                                $scope.proposer={};
                                $scope.proposerEmployment={};
                                $scope.proposerResidential={};
                                $scope.proposerSpouse={};
                            }

                            /* $scope.proposer = $scope.rcvProposal.proposer || {};
                             $scope.proposerEmployment = $scope.proposer.employment;
                             $scope.proposerResidential = $scope.proposer.residentialAddress;
                             $scope.proposerSpouse = $scope.proposer.spouse;*/

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
                            //$scope.agentDetails = $scope.rcvProposal.agentCommissionDetails;
                            //$scope.familyPersonalDetail = $scope.rcvProposal.familyPersonalDetail;
                            ////console.log('FamilyHistory..' + $scope.rcvProposal.familyPersonalDetail.familyHistory.father);
                            //$scope.familyHistory = $scope.rcvProposal.familyPersonalDetail.familyHistory;
                            //$scope.habit = $scope.rcvProposal.familyPersonalDetail.habit;
                            //$scope.habits = $scope.rcvProposal.familyPersonalDetail.habit;
                            //$scope.questionList = $scope.rcvProposal.familyPersonalDetail.habit.questions;
                            //$scope.build = $scope.rcvProposal.familyPersonalDetail.build;
                            //$scope.compulsoryHealthDetails = $scope.rcvProposal.compulsoryHealthStatement;


                        }).error(function (response, status, headers, config) {
                        });

                        //Testing
                    }).error(function (response, status, headers, config) {
                    });

                }

                else {
                    ////console.log('Checking in SaveProposer');
                    ////alert("No QID..");

                    if($scope.mode == "edit")
                    {
                        ////alert("Mode is.."+$scope.mode);
                        //console.log('For Edit Mode Test Is ##########');
                        //console.log(JSON.stringify(request3));
                        $http.post('updateproposer', request3).success(function (response, status, headers, config) {
                            window.location.reload();
                        })

                            .error(function (response, status, headers, config) {
                            });
                    }
                    ////alert("modeType"+$scope.mode);

                    else
                    {
                        // For New Proposal Save...
                        $http.post('create', request1).success(function (response, status, headers, config) {
                            $scope.proposal.ProposalId=response.id;
                            // //alert("ProposalId Check"+JSON.stringify($scope.proposal.ProposalId));
                            window.location.href = "/pla/individuallife/proposal/edit?proposalId=" + $scope.proposal.ProposalId + "&mode=edit";
                            $scope.proposal = response;
                            $scope.proposal.proposalId=response.id;
                            //console.log('Retrieving Proposal Number..');
                            $http.get("/pla/individuallife/proposal/getproposal/" + $scope.proposal.proposalId).success(function (response, status, headers, config) {
                                var result = response;
                                //console.log('Result:' + JSON.stringify(result));
                                //window.location.href = "/pla/individuallife/proposal/edit?proposalId=" + response.proposalId + "&mode=edit";
                                $scope.rcvProposal = response;
                                /* $scope.proposal=
                                 {
                                 "msg":null,
                                 "proposalId":null
                                 };*/

                                //var proposerDetails=$scope.rcvProposal.proposer;
                                ////console.log("*** Show Proposer Details....");
                                ////console.log(JSON.stringify(proposerDetails));

                                $scope.proposalNumberDetails.proposalNumber = $scope.rcvProposal.proposalNumber;
                                $scope.proposal.proposalId = $scope.rcvProposal.proposalId;

                                if($scope.rcvProposal.proposedAssured !=null)
                                {
                                    ////alert("Yes");
                                    $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};
                                    $scope.spouse = $scope.rcvProposal.proposedAssured.spouse;
                                    $scope.employment = $scope.rcvProposal.proposedAssured.employment;
                                    $scope.residentialAddress = $scope.rcvProposal.proposedAssured.residentialAddress;
                                }
                                else
                                {
                                    ////alert("No");
                                    $scope.proposedAssured={};
                                    $scope.employment={};
                                    $scope.residentialAddress={};
                                    $scope.spouse={};
                                }

                                if($scope.rcvProposal.proposer !=null)
                                {
                                    ////alert("Yes");
                                    $scope.proposer = $scope.rcvProposal.proposer || {};
                                    $scope.proposerEmployment = $scope.proposer.employment;
                                    $scope.proposerResidential = $scope.proposer.residentialAddress;
                                    $scope.proposerSpouse = $scope.proposer.spouse;
                                }
                                else
                                {
                                    ////alert("No");
                                    $scope.proposer={};
                                    $scope.proposerEmployment={};
                                    $scope.proposerResidential={};
                                    $scope.proposerSpouse={};
                                }

                                /*  if($scope.rcvProposal.proposedAssured !=null)
                                 {
                                 ////alert("Yes");
                                 $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};
                                 $scope.proposerEmployment = $scope.proposedAssured.employment;
                                 $scope.proposerResidential = $scope.proposedAssured.residentialAddress;
                                 $scope.proposerSpouse = $scope.proposedAssured.spouse;
                                 }
                                 else
                                 {
                                 ////alert("No");
                                 $scope.proposedAssured={};
                                 $scope.proposerEmployment={};
                                 $scope.proposerResidential={};
                                 $scope.proposerSpouse={};
                                 }
                                 */
                                if($scope.rcvProposal.familyPersonalDetail != null)
                                {
                                    //console.log('FamilyHistory..' + $scope.rcvProposal.familyPersonalDetail.familyHistory.father);
                                    $scope.familyHistory = $scope.rcvProposal.familyPersonalDetail.familyHistory;
                                    $scope.habit = $scope.rcvProposal.familyPersonalDetail.habit;
                                    $scope.habits = $scope.rcvProposal.familyPersonalDetail.habit;
                                    $scope.questionList = $scope.rcvProposal.familyPersonalDetail.habit.questions;
                                    $scope.build = $scope.rcvProposal.familyPersonalDetail.build;
                                }
                                if($scope.rcvProposal.compulsoryHealthStatement !=null)
                                {
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

                            //Testing
                        }).error(function (response, status, headers, config) {
                        });
                    }
                    /*                        $http.post('create', request1).success(function (response, status, headers, config) {
                     window.location.href = "/pla/individuallife/proposal/edit?proposalId=" + response.proposalId + "&mode=edit";
                     $scope.proposal = response;
                     //console.log('Retrieving Proposal Number..');
                     $http.get("/pla/individuallife/proposal/getproposal/" + $scope.proposal.proposalId).success(function (response, status, headers, config) {
                     var result = response;
                     //console.log('Result:' + JSON.stringify(result));
                     //window.location.href = "/pla/individuallife/proposal/edit?proposalId=" + response.proposalId + "&mode=edit";
                     $scope.rcvProposal = response;
                     *//* $scope.proposal=
                     {
                     "msg":null,
                     "proposalId":null
                     };*//*

                     //var proposerDetails=$scope.rcvProposal.proposer;
                     ////console.log("*** Show Proposer Details....");
                     ////console.log(JSON.stringify(proposerDetails));

                     $scope.proposalNumberDetails.proposalNumber = $scope.rcvProposal.proposalNumber;
                     $scope.proposal.proposalId = $scope.rcvProposal.proposalId;
                     $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};

                     if($scope.rcvProposal.proposer !=null)
                     {
                     ////alert("Yes");
                     $scope.proposer = $scope.rcvProposal.proposer || {};
                     $scope.proposerEmployment = $scope.proposer.employment;
                     $scope.proposerResidential = $scope.proposer.residentialAddress;
                     $scope.proposerSpouse = $scope.proposer.spouse;
                     }
                     else
                     {
                     ////alert("No");
                     $scope.proposer={};
                     $scope.proposerEmployment={};
                     $scope.proposerResidential={};
                     $scope.proposerSpouse={};
                     }

                     if($scope.rcvProposal.familyPersonalDetail != null)
                     {
                     //console.log('FamilyHistory..' + $scope.rcvProposal.familyPersonalDetail.familyHistory.father);
                     $scope.familyHistory = $scope.rcvProposal.familyPersonalDetail.familyHistory;
                     $scope.habit = $scope.rcvProposal.familyPersonalDetail.habit;
                     $scope.habits = $scope.rcvProposal.familyPersonalDetail.habit;
                     $scope.questionList = $scope.rcvProposal.familyPersonalDetail.habit.questions;
                     $scope.build = $scope.rcvProposal.familyPersonalDetail.build;
                     }
                     if($scope.rcvProposal.compulsoryHealthStatement !=null)
                     {
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

                     //Testing
                     }).error(function (response, status, headers, config) {
                     });*/
                }
                /* }*/
                /*else {
                 //console.log('False..');
                 $scope.updateFlag = true;
                 $scope.agent//alert = true;
                 }*/
            };

            /*$scope.saveProposerDetails = function () {
             //console.log('Save method of Proposer1');

             var prorequest = {
             "spouse": $scope.proposerSpouse,
             "employment": $scope.proposerEmployment,
             "residentialAddress": $scope.proposerResidential
             };
             //console.log("************** Save in Proposer....");
             //console.log(JSON.stringify(prorequest));

             prorequest = angular.extend($scope.proposer, prorequest);
             prorequest =
             {
             "proposer": prorequest,
             "proposalId": $scope.proposal.proposalId
             };
             //console.log('ProRequest' + JSON.stringify(prorequest));

             var request1={
             "proposer":$scope.proposedAssured,
             "proposalId":$scope.proposal.proposalId
             }

             ////console.log('Save Proposer'+JSON.stringify(request1));

             $http.post('updateproposer', prorequest).success(function (response, status, headers, config) {
             $scope.proposal = response;
             $scope.proposal.proposalId=response.id;

             }).error(function (response, status, headers, config) {
             });
             };*/

            $scope.$watch('proposedAssuredAsProposer', function (newval, oldval) {
                //console.log(' proposedAssuredAsProposer ' + newval);
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
            $scope.generalAnswerList=[];

            $scope.openAccordion = function (status, tab) {
                //console.log(status);
                if (status === 'YES') {
                    $scope.generalAnswer = true;
                    $scope.generalAnswerList.push($scope.generalAnswer);
                    //console.log('Checking Status is ' + $scope.generalAnswer);
                    $scope.accordionStatus.generalDetails[tab] = true;
                } else {
                    $scope.generalAnswer = false;
                    $scope.generalAnswerList.push($scope.generalAnswer);
                    //console.log('Checking Status is ' + $scope.generalAnswer);
                    $scope.accordionStatus.generalDetails[tab] = false;
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
        premiumDetail:"/pla/individuallife/proposal/getPage/premiumDetail",
        mandatoryDocumentDetails:"/pla/individuallife/proposal/getPage/mandatoryDocumentDetails",
        approvalDetails:"/pla/individuallife/proposal/getPage/approvalDetail"
    })
    .filter('getTrustedUrl', ['$sce', function ($sce) {
        return function (url) {
            //console.log('getTrustedUrl' + url);
            return $sce.getTrustedResourceUrl(url);
        }
    }]);


var viewILQuotationModule = (function ($http) {
    var services = {};
    services.selectedItem = "";
    services.status = '';
    services.getTheItemSelected = function (ele) {
        this.selectedItem = $(ele).val();
        this.status = $(ele).parent().find('input[type=hidden]').val();
        //alert(JSON.stringify(this.status));
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
        //alert('print quotation...' + this.selectedItem);
        window.location.href = '/pla/individuallife/quotation/printquotation/' + this.selectedItem;
    }

    services.emailQuotation = function () {
        window.location.href = '/pla/individuallife/quotation/emailQuotation/' + this.selectedItem;
    }

    services.modifyProposal = function () {
        var proposalId = this.selectedItem;
        if(this.status == 'Returned')
        {

            window.location.href = "/pla/individuallife/proposal/viewApprovalProposal?proposalId=" + proposalId  + "&status=return" +"&mode=edit";
        }
        else
        {
            window.location.href = "proposal/edit?proposalId=" + proposalId + "&mode=edit";

        }
        /*window.location.href = "/pla/individuallife/quotation/edit?quotationId=" + quotationId;*/

    };

    services.viewProposal = function () {
        var proposalId = this.selectedItem;
        //  //console.log('ID:' + JSON.stringify(proposalId));

        //window.location.href="/pla/individuallife/proposal/getproposal/"+ pid +"?mode=view";
        window.location.href = "proposal/edit?proposalId=" + proposalId + "&mode=view";
    };

    services.createUpdateProposal = function () {
        ////alert("createUpdateProposal");
        var quotationId = this.selectedItem;
        window.location.href = "edit?quotationId=" + quotationId + "&mode=update";
    };

    services.viewApprovalProposal = function () {
        //alert("View Approval..");
        var proposalId = this.selectedItem;
        ////alert("proposalId"+proposalId);
        window.location.href = "/pla/individuallife/proposal/viewApprovalProposal?proposalId=" + proposalId  + "&method=approval" + "&mode=view" ;

    };

    return services;
})();


