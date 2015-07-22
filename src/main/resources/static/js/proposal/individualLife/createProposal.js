angular.module('createProposal', ['pla.individual.proposal', 'common', 'ngRoute', 'commonServices', 'ngMessages', 'angucomplete-alt','mgcrea.ngStrap.select'])
    .config(['datepickerPopupConfig', function (datepickerPopupConfig) {
        datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
        datepickerPopupConfig.currentText = 'Today';
        datepickerPopupConfig.clearText = 'Clear';
        datepickerPopupConfig.closeText = 'Done';
        datepickerPopupConfig.closeOnDateSelection = true;
    }])
    .controller('createProposalCtrl', ['$scope', 'resources', 'getQueryParameter', '$bsmodal', '$http', '$window',
        'globalConstants', 'ProposalService',
        function ($scope, resources, getQueryParameter, $bsmodal, $http, $window, globalConstants, ProposalService) {

            console.log('create proposal');

            $scope.employmentTypes = [];
            $scope.checkEmpTypes = null;
            $scope.occupations = [];
            $scope.provinces = [];
            $scope.bankDetailsResponse=[];
            $scope.proposal = [];
            $scope.searchRiders = [];
            $scope.proposalId = getQueryParameter('proposalId')
            $scope.quotationId = getQueryParameter('quotationId');

            $scope.premiumResponse={};

            $scope.quotationIdDetails =
            {
                "quotationId": null
            };

            console.log("Proposal Id sent is:" + $scope.proposalId);
            $scope.mode = getQueryParameter('mode');
            console.log('modeType' + $scope.mode);
            $scope.quotationStatus = "GENERATED";

            if ($scope.quotationId) {
                console.log('Navigate to quotation Window...');
                $http.get("/pla/individuallife/quotation/getquotation/" + $scope.quotationId).success(function (response, status, headers, config) {
                    var viewQuotationOutput = response;
                    $scope.rcvProposalDetailQid = response;
                    console.log('****** Checking quotationDetails...' + $scope.rcvProposalDetailQid.quotationId.quotationId);
                    $scope.quotationIdDetails = $scope.rcvProposalDetailQid.quotationId;
                    console.log('viewQuotationOutput' + JSON.stringify(viewQuotationOutput));

                    $scope.proposedAssured = $scope.rcvProposalDetailQid.proposedAssured || {};
                    $scope.proposedAssured.nrc = $scope.rcvProposalDetailQid.proposedAssured.nrcNumber || {};
                    $scope.proposedAssured.isProposer = $scope.rcvProposalDetailQid.assuredTheProposer;
                    $scope.proposer = $scope.rcvProposalDetailQid.proposer || {};
                    $scope.proposer.nrc = $scope.rcvProposalDetailQid.proposer.nrcNumber || {};

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

            if ($scope.proposalId) {

                $http.get("/pla/individuallife/proposal/getproposal/" + $scope.proposalId + "?mode=view").success(function (response, status, headers, config) {
                    var result = response;
                    console.log('Result:' + JSON.stringify(result));
                    $scope.rcvProposal = response;
                    //console.log('Proposal Number....');
                    $scope.proposalNumberDetails.proposalNumber = $scope.rcvProposal.proposalNumber;
                    $scope.proposal =
                    {
                        "msg": null,
                        "proposalId": null
                    };

                    $scope.proposal.proposalId = $scope.rcvProposal.proposalId;
                    $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};
                    $scope.proposer = $scope.rcvProposal.proposer || {};
                    $scope.proposerEmployment = $scope.proposer.employment;
                    $scope.proposerResidential = $scope.proposer.residentialAddress;
                    $scope.proposerSpouse = $scope.proposer.spouse;
                    if($scope.rcvProposal.proposalPlanDetail !=null)
                    {
                        $scope.proposalPlanDetail = $scope.rcvProposal.proposalPlanDetail;
                    }
                    console.log('Response..'+JSON.stringify(response.proposalPlanDetail));
                    //$scope.selectedPlan.title=response.proposalPlanDetail.planName;
                    var selectedPlan = {};

                    if(response.proposalPlanDetail !=null)
                    {
                        selectedPlan.title = response.proposalPlanDetail.planName;
                        selectedPlan.description = response.planDetail;
                        $scope.selectedPlan = selectedPlan;
                        $scope.proposalPlanDetail.planId=response.proposalPlanDetail.planId;
                    }
                    //$scope.selectedPlan.title=response.proposalPlanDetail.planName;
                    if($scope.proposalPlanDetail != null && $scope.proposalPlanDetail.riderDetails != null)
                    {
                        $scope.searchRiders = $scope.rcvProposal.proposalPlanDetail.riderDetails;
                    }

                    //$scope.selectedPlan=$scope.rcvProposal.proposalPlanDetail;
                    if($scope.rcvProposal.beneficiaries != null)
                    {
                        $scope.beneficiariesList = $scope.rcvProposal.beneficiaries;
                    }

                    if($scope.rcvProposal.generalDetails != null)
                    {
                        console.log("generalDetails:-->"+JSON.stringify($scope.rcvProposal.generalDetails));
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
                                //alert("Ok..");
                                $scope.assuredByOthers="YES";
                            }
                            else
                            {
                                //alert("Not Ok");
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

                    if ($scope.proposedAssured.dateOfBirth) {
                        $scope.proposedAssured.nextDob = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                    }
                    if ($scope.proposer.dateOfBirth) {
                        $scope.proposer.nextDob = moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                    }
                    //$scope.agentDetails=[];
                    $scope.spouse = $scope.rcvProposal.proposedAssured.spouse;
                    //alert($scope.rcvProposal.proposedAssured.employment);
                    $scope.employment = $scope.rcvProposal.proposedAssured.employment;
                    $scope.residentialAddress = $scope.rcvProposal.proposedAssured.residentialAddress;
                    $scope.agentDetails = $scope.rcvProposal.agentCommissionDetails;

                    if($scope.rcvProposal.familyPersonalDetail != null)
                    {
                        $scope.familyPersonalDetail = $scope.rcvProposal.familyPersonalDetail;
                        console.log('FamilyHistory..' + $scope.rcvProposal.familyPersonalDetail.familyHistory.father);
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
            }).error(function (response, status, headers, config) {
            });

            $scope.titles = globalConstants.title;
            $scope.part = {
                isPart: true
            };

            $scope.selectedPlan = {};
            $scope.selectedWizard = 1;

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
                console.log('Radio Button');
                viewILProposalModule.getTheItemSelected(ele);
            };

            $scope.beneficiariesList =[];

            $scope.titleList = globalConstants.title;
            $scope.genderList = globalConstants.gender;


            $scope.savePremiumDetail=function()
            {
               var premiumRequest=
                {
                    //"premiumPaymentDetails":$scope.premiumPaymentDetails,
                    "employerDetails":$scope.premiumEmployerDetails,
                    "bankDetails":$scope.bankDetails
                }
                premiumRequest=angular.extend($scope.premiumPaymentDetails, premiumRequest);

                premiumRequest=
                {
                    "premiumPaymentDetails": premiumRequest,
                    "proposalId": $scope.proposal.proposalId
                };
                console.log('premiumRequest' +JSON.stringify(premiumRequest));

               $http.post('updatepremiumpaymentdetails', premiumRequest).success(function (response, status, headers, config) {
                 }).error(function (response, status, headers, config) {
            });
            }


            $scope.addBeneficiary = function (beneficiary) {
                console.log('Inside addBeneficiary Method..');
                //console.log("Length is:"+$scope.beneficiariesList.length);

                if ($scope.beneficiariesList.length == 0) {
                 console.log('Object Is..' + beneficiary);
                 $scope.beneficiariesList.push(beneficiary);
                 //$scope.test(agent);
                 }

                else {

                    for (i in $scope.beneficiariesList) {
                        if (($scope.beneficiariesList[i].firstName == beneficiary.firstName)||($scope.beneficiaries[i].nrc == beneficiary.nrc)) {
                            console.log('Failure..');
                            alert("Particular Beneficiary is Already Added..Please Choose different AgentId");
                        }
                        else {
                            $scope.beneficiariesList.unshift(beneficiary);
                        }
                    }
                    //$scope.test(agent);

                }


                $('#beneficiaryModal').modal('hide');
            };

            $scope.showDob = function (dob) {
                console.log('Dob Calculation..');
                console.log('DOB is..' + JSON.stringify(dob));
                if(dob != null)
                {
                    console.log('Not Null..');
                    $scope.proposedAssured.nextDob = moment().diff(new moment(new Date(dob)), 'years') + 1;
                }
                //$scope.proposedAssured.nextDob = moment().diff(new moment(new Date(dob)), 'years') + 1;
                //console.log('DOB Show is'+$scope.proposedAssured.nextDob);
            };

            $scope.showBeneficiaryDob = function (dob) {
                console.log('Dob Calculation..');
                console.log('DOB' + JSON.stringify(dob));
                if(dob != null)
                {
                    $scope.beneficiary.age = moment().diff(new moment(new Date(dob)), 'years');
                }

                //$scope.beneficiary.age = moment().diff(new moment(new Date(dob)), 'years');

            };

            $scope.showProposerDob = function (dob) {
                console.log('Dob Calculation..');
                console.log('DOB' + JSON.stringify(dob));
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
                console.log('Save Plan');

                var tempRequest = {
                    "riderDetails": $scope.searchRiders
                };
                tempRequest = angular.extend($scope.proposalPlanDetail, tempRequest);

                var request = {
                    "proposalPlanDetail": tempRequest,
                    //"proposalPlanDetail":$scope.proposalPlanDetail,
                    "riderDetails": $scope.searchRiders,
                    "beneficiaries": $scope.beneficiariesList,
                    "proposalId": $scope.proposal.proposalId
                };
                console.log('Final to Plan DB..' + JSON.stringify(request));

               $http.post('updateplan', request).success(function (response, status, headers, config) {
                    $scope.proposal = response;
                    console.log('proposalId : ' + $scope.proposal.proposalId);


                   //CallingPremiumDetail to fill Premiumtab in PremiumDetail.html

                 /*  $http.get("pla/individuallife/proposal/getpremiumdetail/"+$scope.proposal.proposalId).success(function (response, status, headers, config) {

                       $scope.premiumResponse=response;
                   }).error(function (response, status, headers, config) {
                   });*/

                }).error(function (response, status, headers, config) {
                });
                console.log('proposalPlanDetail' + JSON.stringify(request));

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
                console.log('SaveMethod ofCompulsoryQuestionDetails');
                var request =
                {
                    "compulsoryHealthDetails": $scope.compulsoryHealthDetails,
                    "proposalId": $scope.proposal.proposalId
                };
                console.log('Json to save to DB is:' + JSON.stringify(request));
                $http.post('/pla/individuallife/proposal/updatecompulsoryhealthstatement', request).success(function (response, status, headers, config) {

                }).error(function (response, status, headers, config) {
                });

            };
            console.log('Question in Habit is' + $scope.habit);
            $scope.questionList = [];
            $scope.habits = {};
            $scope.generalQuestion = [];
            $scope.saveGeneralDetails = function () {
                console.log('Inside saveGeneraLDetails Method..');
                console.log('Checking GeneralStatus:--> '+$scope.generalAnswer);

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

                console.log('generalQuestion is: ' + JSON.stringify(generalQuestion));
                console.log('Final General is' + JSON.stringify(req));
                 $http.post('/pla/individuallife/proposal/updategeneraldetails',req).success(function (response, status, headers, config) {
                 $scope.occupations = response;
                 }).error(function (response, status, headers, config) {
                 });

            };

            $scope.saveFamilyHistory = function () {
                console.log($scope.isPregnant);
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
                console.log('List..' + JSON.stringify(listA));

                request = angular.extend($scope.familyPersonalDetail, request);
                request = {
                    "familyPersonalDetail": request,
                    "proposalId": $scope.proposal.proposalId
                };
                console.log('request ' + JSON.stringify(request));
                $http.post('/pla/individuallife/proposal/updatefamily', request).success(function (response, status, headers, config) {

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

            $scope.printValue = function (){
                console.log($scope.selectedPlan);
            }

            $scope.planSelected = function (newValue) {
                 console.log('Watching is:'+JSON.stringify(newValue));

                if (newValue && newValue.description && newValue.description.plan_id) {

                    //alert('Looking in Plan');
                    console.log('Plan ID Is'+newValue.description.plan_id);

                    $scope.proposalPlanDetail.planId=newValue.description.plan_id;
                    $http.get("getridersforplan/" + newValue.description.plan_id).success(function (response, status, headers, config) {
                     $scope.searchRiders = response;
                     console.log('Riders Details From Db is:' +JSON.stringify(response));
                     console.log($scope.searchRiders);

                     }).error(function (response, status, headers, config) {
                     var check = status;
                        console.log('Checking Status:'+JSON.stringify(check));
                     });
                }
            };

            $scope.$watchGroup(['employment.province', 'residentialAddress.province', 'proposerEmployment.province', 'proposerResidential.province', 'employment.employmentType', 'employment.province','proposerEmployment.employmentType'], function (newVal, oldVal) {
                if (!newVal) return;

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
                        $scope.employment.employmentType = employeeTypes.employment_id;
                    }
                }

              /*  if (newVal[5]) {
                    console.log('Town: ' + newVal[5]);
                }

                if (newVal[6]) {
                    var proposeremployeeTypes = _.findWhere($scope.employmentTypes, {code: newVal[6]});
                    if (proposeremployeeTypes) {
                        $scope.proposerEmployment.employmentType = proposeremployeeTypes.employment_id;
                    }
                }*/
            });

            $scope.selectSpouse = function (maritalStatusCheck) {
                console.log("MaritialStatus:" + maritalStatusCheck);
                //var choice = $scope.proposedAssured.maritalStatus;
                //alert("Maritial Status"+maritalStatusCheck);

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
                console.log('Inside Add PolicyDetails..');
                console.log(JSON.stringify(policy));
                //$scope.policyDetails.unshift(policy)

                if ($scope.policyDetails.length == 0) {
                    console.log('Lenght is Null..');
                    $scope.policyDetails.unshift(policy);
                }
                else {

                    for (i in $scope.policyDetails) {

                        if ($scope.policyDetails[i].policyOrProposalNumber == policy.policyOrProposalNumber) {
                            console.log('Failure..');
                            alert("Particular PolicyNumber is Already Added..Please Choose different PolicyNumber");
                        }
                        else {
                            $scope.policyDetails.unshift(policy);
                        }
                    }

                }
                $('#policyModal').modal('hide');
                $scope.clear();
            };

            $scope.addAssuredByOthers = function (insurer1) {
                console.log('Inside Add addAssuredByOthers..');
                console.log(JSON.stringify(insurer1));

                if ($scope.insurerDetails1.length == 0) {
                    console.log('Lenght is Null..');
                    $scope.insurerDetails1.unshift(insurer1);
                }
                else {

                    for (i in $scope.insurerDetails1) {
                        if ($scope.insurerDetails1[i].policyOrProposalNumber == insurer1.policyOrProposalNumber) {
                            console.log('Failure..');
                            alert("Particular PolicyNumber is Already Added..Please Choose different PolicyNumber");
                        }
                        else {
                            $scope.insurerDetails1.unshift(insurer1);
                        }
                    }

                }
                $('#assuredByOthersModal').modal('hide');
                $scope.clear();
            };


            $scope.addPendingInsuranceByOthersTpl = function (insurer2) {
                console.log('Inside Add addPendingInsuranceByOthersTpl..');
                console.log(JSON.stringify(insurer2));
                //$scope.policyDetails.unshift(policy)

                if ($scope.insurerDetails2.length == 0) {
                    console.log('Lenght is Null..');
                    $scope.insurerDetails2.unshift(insurer2);
                }
                else {

                    for (i in $scope.insurerDetails2) {
                        if ($scope.insurerDetails2[i].policyOrProposalNumber == insurer2.policyOrProposalNumber) {
                            console.log('Failure..');
                            alert("Particular PolicyNumber is Already Added..Please Choose different PolicyNumber");
                        }
                        else {
                            $scope.insurerDetails2.unshift(insurer2);
                        }
                    }

                }
                $('#pendingInsuranceByOthersTpl').modal('hide');
                $scope.clear();
            };

            $scope.addaAssuranceDeclinedTpl = function (insurer3) {
                console.log('Inside  Add AssuranceDeclinedTpl..');
                console.log(JSON.stringify(insurer3));

                if ($scope.insurerDetails3.length == 0) {
                    console.log('Lenght is Null..');
                    $scope.insurerDetails3.unshift(insurer3);
                }
                else {

                    for (i in $scope.insurerDetails3) {
                        if ($scope.insurerDetails3[i].policyOrProposalNumber == insurer3.policyOrProposalNumber) {
                            console.log('Failure..');
                            alert("Particular ProposalNumber is Already Added..Please Choose different PolicyNumber");
                        }
                        else {
                            $scope.insurerDetails3.unshift(insurer3);
                        }
                    }

                }
                $('#assuranceDeclinedTpl').modal('hide');
                $scope.clear();
            };


            $scope.addAgent = function (agent) {
                console.log('Inside addagent Method..');

                if ($scope.agentDetails.length == 0) {
                    //console.log('Length is Null..'+$scope.agentDetails.length);
                    $scope.agentDetails.unshift(agent);
                    //$scope.test(agent);
                }
                else {

                    for (i in $scope.agentDetails) {
                        if ($scope.agentDetails[i].agentId == agent.agentId) {
                            console.log('Failure..');
                            alert("Particular AgentId is Already Added..Please Choose different AgentId");
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
                console.log('sum: ' + sum);
                if (sum == 100.00) {
                    $scope.statusCount = false;
                    $scope.agentMessage=false;
                }
                else {
                    $scope.statusCount = true;
                    $scope.agentMessage=true;
                }
            };

           /* $scope.test = function (row) {
                console.log('Testing...');
                console.log('Pass..' + JSON.stringify(row));
                for (i in $scope.agentDetails) {
                    if ($scope.agentDetails[i].agentId == row.agentId) {
                        $scope.agentDetails[i] = row;
                    }
                }

                var countCheck = $scope.countStatus();
                console.log('Checking-->' + JSON.stringify(countCheck));
                if (countCheck != 100) {
                    console.log('Not 100');
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
                console.log('count: ' + JSON.stringify(count));
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
                console.log('Testing...');
                console.log('Pass..' + JSON.stringify(searchRider));

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
                alert('Testing For Riders..');
            }

            $scope.searchRiders = function (proposalPlanId) {
                console.log('Search Riders Function..' + $scope.planId);
            }

            $scope.searchAgent = function () {
                $scope.check = false;
                $scope.checking = true;
                console.log('Testing In SearchCode..');
                $scope.agentId = $scope.agent.agentId;
                console.log('Value is: ' + $scope.agentId);
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
                console.log('Inside SaveAdditionalDetail');

                var requestAddDetails =
                {
                    "medicalAttendantDetails": $scope.medicalAttendant.medicalAttendantDetails,
                    "medicalAttendantDuration": $scope.medicalAttendant.medicalAttendantDuration,
                    "dateAndReason": $scope.medicalAttendant.dateAndReason,
                    "replacementDetails": $scope.replacement,
                    "proposalId": $scope.proposal.proposalId
                }
                console.log('RequiredJson:' + JSON.stringify(requestAddDetails));

                $http.post('/pla/individuallife/proposal/updateadditionaldetails', requestAddDetails).success(function (response, status, headers, config) {

                }).error(function (response, status, headers, config) {
                });

            };
            $scope.proposalNumberDetails =
            {
                "proposalNumber": null
            };

            $scope.saveProposedAssuredDetails = function () {
                //ProposalService.saveProposedAssured($scope.proposedAssured, $scope.proposedAssuredSpouse, $scope.paemployment, $scope.paresidential, proposedAssuredAsProposer, null);
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

                console.log('Final Result ' + JSON.stringify(request1));
               /* var count1 = $scope.countStatus();
                console.log("Count In Save Method is:" + JSON.stringify(count1));*/

                /*if (count1 == 100) {
                    console.log('Sucess..');
                    $scope.countCheckFlag = true;*/

                    if ($scope.quotationIdDetails.quotationId != null && $scope.quotationIdDetails.quotationId != " ") {
                        //alert('Checking...');
                        var request2 = {
                            "proposedAssured": $scope.proposedAssured,
                            "agentCommissionDetails": $scope.agentDetails,
                            "quotationId": $scope.quotationIdDetails.quotationId
                        }
                        console.log('**** Saving QuotationId..');
                        console.log(JSON.stringify(request2));

                        // QuotationID saving

                        $http.post('create', request2).success(function (response, status, headers, config) {
                            $scope.proposal = response;
                            console.log('Retrieving Proposal Number@###..');
                            $http.get("/pla/individuallife/proposal/getproposal/" + $scope.proposal.proposalId + "?mode=view").success(function (response, status, headers, config) {
                                var result = response;
                                console.log('Result:' + JSON.stringify(result));
                                $scope.rcvProposal = response;
                                $scope.proposalNumberDetails.proposalNumber = $scope.rcvProposal.proposalNumber;
                                $scope.proposal.proposalId = $scope.rcvProposal.proposalId;
                                $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};
                                if($scope.rcvProposal.proposer !=null)
                                {
                                    //alert("Yes");
                                    $scope.proposer = $scope.rcvProposal.proposer || {};
                                    $scope.proposerEmployment = $scope.proposer.employment;
                                    $scope.proposerResidential = $scope.proposer.residentialAddress;
                                    $scope.proposerSpouse = $scope.proposer.spouse;
                                }
                                else
                                {
                                    //alert("No");
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
                                $scope.agentDetails = [];
                                $scope.spouse = $scope.rcvProposal.proposedAssured.spouse;
                                $scope.employment = $scope.rcvProposal.proposedAssured.employment;
                                $scope.residentialAddress = $scope.rcvProposal.proposedAssured.residentialAddress;
                                $scope.agentDetails = $scope.rcvProposal.agentCommissionDetails;
                                //$scope.familyPersonalDetail = $scope.rcvProposal.familyPersonalDetail;
                                //console.log('FamilyHistory..' + $scope.rcvProposal.familyPersonalDetail.familyHistory.father);
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
                        console.log('Checking in SaveProposer');
                        $http.post('create', request1).success(function (response, status, headers, config) {
                            $scope.proposal = response;
                            console.log('Retrieving Proposal Number..');
                            $http.get("/pla/individuallife/proposal/getproposal/" + $scope.proposal.proposalId + "?mode=view").success(function (response, status, headers, config) {
                                var result = response;
                                console.log('Result:' + JSON.stringify(result));
                                $scope.rcvProposal = response;
                                /* $scope.proposal=
                                 {
                                 "msg":null,
                                 "proposalId":null
                                 };*/

                                //var proposerDetails=$scope.rcvProposal.proposer;
                                //console.log("*** Show Proposer Details....");
                                //console.log(JSON.stringify(proposerDetails));

                                $scope.proposalNumberDetails.proposalNumber = $scope.rcvProposal.proposalNumber;
                                $scope.proposal.proposalId = $scope.rcvProposal.proposalId;
                                $scope.proposedAssured = $scope.rcvProposal.proposedAssured || {};

                                if($scope.rcvProposal.proposer !=null)
                                {
                                    //alert("Yes");
                                    $scope.proposer = $scope.rcvProposal.proposer || {};
                                    $scope.proposerEmployment = $scope.proposer.employment;
                                    $scope.proposerResidential = $scope.proposer.residentialAddress;
                                    $scope.proposerSpouse = $scope.proposer.spouse;
                                }
                                else
                                {
                                    //alert("No");
                                    $scope.proposer={};
                                    $scope.proposerEmployment={};
                                    $scope.proposerResidential={};
                                    $scope.proposerSpouse={};
                                }

                                if($scope.rcvProposal.familyPersonalDetail != null)
                                {
                                    console.log('FamilyHistory..' + $scope.rcvProposal.familyPersonalDetail.familyHistory.father);
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
               /* }*/
                /*else {
                    console.log('False..');
                    $scope.updateFlag = true;
                    $scope.agentAlert = true;
                }*/
            };

            $scope.saveProposerDetails = function () {
                console.log('Save method of Proposer1');

                var prorequest = {
                    "spouse": $scope.proposerSpouse,
                    "employment": $scope.proposerEmployment,
                    "residentialAddress": $scope.proposerResidential
                };
                console.log("************** Save in Proposer....");
                console.log(JSON.stringify(prorequest));

                prorequest = angular.extend($scope.proposer, prorequest);
                prorequest =
                {
                    "proposer": prorequest,
                    "proposalId": $scope.proposal.proposalId
                };
                console.log('ProRequest' + JSON.stringify(prorequest));

                /* var request1={
                 "proposer":$scope.proposedAssured,
                 "proposalId":$scope.proposal.proposalId
                 }*/

                //console.log('Save Proposer'+JSON.stringify(request1));

                $http.post('updateproposer', prorequest);
            };


            $scope.$watch('proposedAssuredAsProposer', function (newval, oldval) {
                console.log(' proposedAssuredAsProposer ' + newval);
            });

            $scope.resources = resources;
            $scope.agentDetails = [];
            $scope.accordionStatus = {
                proposedAssuredDetails: {agents: true},
                proposerDetails: {proposedAssured: true},
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
                console.log(status);
                if (status === 'YES') {
                    $scope.generalAnswer = true;
                    $scope.generalAnswerList.push($scope.generalAnswer);
                    console.log('Checking Status is ' + $scope.generalAnswer);
                    $scope.accordionStatus.generalDetails[tab] = true;
                } else {
                    $scope.generalAnswer = false;
                    $scope.generalAnswerList.push($scope.generalAnswer);
                    console.log('Checking Status is ' + $scope.generalAnswer);
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
        mandatoryDocumentDetails:"/pla/individuallife/proposal/getPage/mandatoryDocumentDetails"
    })
    .filter('getTrustedUrl', ['$sce', function ($sce) {
        return function (url) {
            console.log('getTrustedUrl' + url);
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

    services.modifyProposal = function () {
        var proposalId = this.selectedItem;
        /*window.location.href = "/pla/individuallife/quotation/edit?quotationId=" + quotationId;*/
        window.location.href = "proposal/edit?proposalId=" + proposalId + "&mode=edit";
    };

    services.viewProposal = function () {
        var proposalId = this.selectedItem;
        console.log('ID:' + JSON.stringify(proposalId));

        //window.location.href="/pla/individuallife/proposal/getproposal/"+ pid +"?mode=view";
        window.location.href = "proposal/edit?proposalId=" + proposalId + "&mode=view";
    };

    services.createUpdateProposal = function () {
        //alert("createUpdateProposal");
        var quotationId = this.selectedItem;
        window.location.href = "edit?quotationId=" + quotationId + "&mode=update";
    };

    return services;
})();


