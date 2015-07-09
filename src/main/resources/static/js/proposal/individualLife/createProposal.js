angular.module('createProposal', ['pla.individual.proposal', 'common', 'ngRoute', 'commonServices', 'ngMessages'])
    .controller('createProposalCtrl', ['$scope', 'resources','getQueryParameter','$bsmodal', '$http','$window',
        'globalConstants', 'ProposalService',
        function ($scope, resources,getQueryParameter,$bsmodal, $http,$window, globalConstants, ProposalService) {

            console.log('create proposal');
            $scope.employmentTypes = [];
            $scope.occupations = [];
            $scope.provinces = [];
            $scope.proposal=[];
            $scope.searchRiders=[];
            $scope. proposalId = getQueryParameter('proposalId')
            $scope.quotationId=getQueryParameter('quotationId');
            console.log("Proposal Id sent is:" + $scope. proposalId);
           $scope.mode= getQueryParameter('mode');
            console.log('modeType' + $scope.mode );
            $scope.quotationStatus="GENERATED";

            if($scope.quotationId)
            {
                alert("Navigate to Proposer Window...")
            }
             if($scope. proposalId)
            {

                $http.get("/pla/individuallife/proposal/getproposal/"+ $scope. proposalId +"?mode=view").success(function (response, status, headers, config) {
                 var result = response;
                 console.log('Result:'+JSON.stringify(result));
                    $scope.rcvProposal = response;
                    //console.log('Proposal Number....');
                    $scope.proposalNumberDetails.proposalNumber=$scope.rcvProposal.proposalNumber;
                    $scope.proposal=
                    {
                        "msg":null,
                        "proposalId":null
                    };

                    $scope.proposal.proposalId=$scope.rcvProposal.proposalId;
                    $scope.proposedAssured=$scope.rcvProposal.proposedAssured || {};
                    $scope.proposer=$scope.rcvProposal.proposer || {};
                    $scope.proposerEmployment=$scope.proposer.employment;
                    $scope.proposerResidential=$scope.proposer.residentialAddress;
                    $scope.proposerSpouse=$scope.proposer.spouse;
                    $scope.proposalPlanDetail=$scope.rcvProposal.proposalPlanDetail;
                    $scope.searchRiders=$scope.rcvProposal.proposalPlanDetail.riderDetails;
                    $scope.beneficiaries=$scope.rcvProposal.beneficiaries;

                    if ($scope.proposedAssured.dateOfBirth) {
                        $scope.proposedAssured.nextDob= moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                    }

                    if ($scope.proposer.dateOfBirth) {
                        $scope.proposer.nextDob= moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                    }
                    //$scope.agentDetails=[];
                    $scope.spouse= $scope.rcvProposal.proposedAssured.spouse;
                    $scope.employment=$scope.rcvProposal.proposedAssured.employment;
                    $scope.residentialAddress=$scope.rcvProposal.proposedAssured.residentialAddress;
                    $scope.agentDetails=$scope.rcvProposal.agentCommissionDetails;
                    $scope.familyPersonalDetail=$scope.rcvProposal.familyPersonalDetail;
                    console.log('FamilyHistory..'+$scope.rcvProposal.familyPersonalDetail.familyHistory.father);
                    $scope.familyHistory=$scope.rcvProposal.familyPersonalDetail.familyHistory;
                    $scope.habit=$scope.rcvProposal.familyPersonalDetail.habit;
                    $scope.habits=$scope.rcvProposal.familyPersonalDetail.habit;
                    $scope.questionList=$scope.rcvProposal.familyPersonalDetail.habit.questions;
                    $scope.build=$scope.rcvProposal.familyPersonalDetail.build;
                    $scope.compulsoryHealthDetails=$scope.rcvProposal.compulsoryHealthStatement;



                    //$scope.proposerEmployment=$scope.proposer

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

            $scope.iLplanDetails=[];
            $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                $scope.provinces = response;
            }).error(function (response, status, headers, config) {
            });

            $scope.titles = globalConstants.title;
            $scope.part = {
                isPart: true
            };

            $scope.selectedPlan=[];

            $scope.selectedWizard = 1;
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
                "nextDob":null,
                "isProposer": null
            };

            $scope.getTheItemSelected=function(ele)
            {
                console.log('Radio Button');
                viewILProposalModule.getTheItemSelected(ele);
            };

            $scope.beneficiaries = [];
            $scope.titleList = globalConstants.title;
            $scope.genderList = globalConstants.gender;

            $scope.addBeneficiary = function (beneficiary){
                console.log('Inside addBeneficiary Method..');
                $scope.beneficiaries.unshift(beneficiary);
                $('#beneficiaryModal').modal('hide');
                var show=$scope.beneficiaries;
                console.log('Showing is:'+show);
            };

            $scope.showDob=function(dob)
            {
                console.log('Dob Calculation..');
                console.log('DOB' + JSON.stringify(dob));
                $scope.proposedAssured.nextDob= moment().diff(new moment(new Date(dob)), 'years') + 1;

            };

            $scope.showBeneficiaryDob=function(dob)
            {
                console.log('Dob Calculation..');
                console.log('DOB' + JSON.stringify(dob));
                $scope.beneficiary.age= moment().diff(new moment(new Date(dob)), 'years');

            };

            $scope.showProposerDob=function(dob)
            {
                console.log('Dob Calculation..');
                console.log('DOB' + JSON.stringify(dob));
                $scope.proposer.nextDob= moment().diff(new moment(new Date(dob)), 'years') + 1;

            };
            $scope.proposalPlanDetail ={};
            $scope.savePlanDetail=function()
            {
                console.log('Save Plan');

                 var tempRequest={
                     "riderDetails":$scope.searchRiders
                 }
                tempRequest=angular.extend($scope.proposalPlanDetail,tempRequest);

                var request = {
                    "proposalPlanDetail":tempRequest,
                    "beneficiaries":$scope.beneficiaries,
                    "proposalId":$scope.proposal.proposalId
                }
                console.log('Final to Plan DB..'+JSON.stringify(request));

                $http.post('updateplan', request).success(function (response, status, headers, config) {
                    $scope.proposal = response;
                    console.log('proposalId : '+$scope.proposal.proposalId );
                }).error(function (response, status, headers, config) {
                });


                /*request = {proposalPlanDetail: request};*/
                //$http.post('updateproposer', prorequest);
                console.log('proposalPlanDetail' + JSON.stringify(request));

            };

            $scope.$on('actionclicked.fu.wizard', function (name, event, data) {
                if (data.step == 1) {
                }
            });

            $scope.proposer = {
                "title": null,
                "firstName":null,
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

            $scope.spouse=
            {
                "firstName": null,
                "surname": null,
                "mobileNumber": null,
                "emailAddress": null

            };
            $scope.employment=
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
                "workPhone": null
            };
            $scope.residentialAddress=
            {
                "address1": null,
                "address2": null,
                "postalCode":null,
                "province": null,
                "town": null,
                "homePhone": null,
                "emailAddress":null
            };

            $scope.familyPersonalDetail = {isPregnant: null, pregnancyMonth: null};
            //$scope.familyHistory = {father: {}, mother: {}, brother: {}, sister: {},question_16: {}};
            $scope.familyHistory = {father: {}, mother: {}, brother: {}, sister: {}, closeRelative:{}};
            /*$scope.habit = {question_17: {}, question_18: {}, question_19: {}, question_20: {}};*/
            //$scope.questions=[];
           console.log("****************************");
           // console.log($scope.questionList);

            $scope.build = {overWeightQuestion: {}};
            $scope.compulsoryHealthDetails=[];
            //$scope.questions=[];

            $scope.saveCompulsoryQuestionDetails=function()
            {
                console.log('SaveMethod ofCompulsoryQuestionDetails');
                var request=
                {
                    "compulsoryHealthDetails":$scope.compulsoryHealthDetails,
                    "proposalId":$scope.proposal.proposalId
                };
                console.log('Json to save to DB is:'+JSON.stringify(request));
                $http.post('/pla/individuallife/proposal/updatecompulsoryhealthstatement',request).success(function (response, status, headers, config) {

                }).error(function (response, status, headers, config) {
                });

            };
            /*var req=$scope.questions;
            angular.`($scope.habit,req);*/
            console.log('Question in Habit is' +$scope.habit);
            $scope.questionList=[];
            $scope.habits={};
            $scope.generalQuestion=[];

            $scope.assuredByPLAL={};

            $scope.saveGeneraLDetails=function()
            {
                console.log('Inside saveGeneraLDetails Method..');
                console.log($scope.generalAnswer);
                var assuredByPLAL=
                {
                    "questions":$scope.policyDetails,
                    "questionId":"1",
                    "answer": $scope.generalAnswer
                }

                var assuredByOthers=
                {
                    "questions":$scope.insurerDetails1,
                    "questionId":"2",
                    "answer": $scope.generalAnswer
                }

                var pendingInsuranceByOthers=
                {
                    "questions":$scope.insurerDetails2,
                    "questionId":"3",
                    "answer": $scope.generalAnswer
                }

                var assuranceDeclined=
                {
                    "questions":$scope.insurerDetails3,
                    "questionId":"4",
                    "answer": $scope.generalAnswer
                }
                var generalQuestion=$scope.generalQuestion;
                var req=
                {
                    "assuredByPLAL":assuredByPLAL,
                    "assuredByOthers":assuredByOthers,
                    "pendingInsuranceByOthers":pendingInsuranceByOthers,
                    "assuranceDeclined":assuranceDeclined,
                    "generalQuestion":generalQuestion
                }

                //console.log('generalQuestion is: '+JSON.stringify(generalQuestion));
                //$scope.assuredByPLAL=angular.extend($scope.assuredByPLAL,request);
               /* console.log('To Test1'+JSON.stringify(assuredByPLAL));
                console.log('To Test2'+JSON.stringify(assuredByOthers));
                console.log('To Test3'+JSON.stringify(pendingInsuranceByOthers));
                console.log('To Test4'+JSON.stringify(assuranceDeclined));*/
                console.log('Final General is'+JSON.stringify(req));
            };

            $scope.saveFamilyHistory = function () {
                console.log($scope.isPregnant);
                var listA=$scope.questionList;

                $scope.habit =
                 {
                     "wine":$scope.habits.wine,
                     "beer":$scope.habits.beer,
                     "spirit":$scope.habits.spirit,
                     "smokePerDay":$scope.habits.smokePerDay,
                 "questions":listA
                 };
                /*var req=angular.extend($scope.habit,listA);*/

                var request = {
                    "familyHistory": $scope.familyHistory,
                    "habit": $scope.habit,
                    "build": $scope.build
                };

                var listA=$scope.questionList;
                console.log('List..'+JSON.stringify(listA));

                request = angular.extend($scope.familyPersonalDetail, request);
                request = {
                    "familyPersonalDetail": request,
                    "proposalId":$scope.proposal.proposalId
                };
                console.log('request ' + JSON.stringify(request));
                $http.post('/pla/individuallife/proposal/updatefamily',request).success(function (response, status, headers, config) {

                }).error(function (response, status, headers, config) {
                });

            }

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

            $scope.launchProposerEmpDate=function($event)
            {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob3 = true;
            };

            $scope.launchBeneficiaryDob=function($event)
            {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob4 = true;
            };

            $scope.$watchGroup(['employment.province', 'residentialAddress.province', 'proposerEmployment.province', 'proposerResidential.province'], function (newVal, oldVal) {
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
                    console.log('residential ' + newVal[3]);
                    var provinceDetails = _.findWhere($scope.provinces, {provinceId: newVal[3]});
                    if (provinceDetails)
                        $scope.proposerResidentialCities = provinceDetails.cities;
                }
            });

            $scope.selectSpouse=function()
            {
                console.log("MaritialStatus:"+ $scope.proposedAssured.maritalStatus);
                var choice=$scope.proposedAssured.maritalStatus;
                if(choice != "MARRIED")
                {
                    $scope.choose=true;
                }
                else
                {
                    $scope.choose=false;
                }
            };

            $scope.agentDetails = [];
            $scope.flag=false;
            $scope.countCheck=false;
            $scope.policyDetails = [];
            $scope.insurerDetails1=[];
            $scope.insurerDetails2=[];
            $scope.insurerDetails3=[];


            $scope.addPolicyDetails=function(policy)
            {
                console.log('Inside Add PolicyDetails..');
                console.log(JSON.stringify(policy));
                //$scope.policyDetails.unshift(policy)

                if($scope.policyDetails.length == 0)
                {
                    console.log('Lenght is Null..');
                    $scope.policyDetails.unshift(policy);
                }
                else{

                    for(i in $scope.policyDetails) {
                        if($scope.policyDetails[i].policyNumber == policy.policyNumber)
                        {
                            console.log('Failure..');
                            alert("Particular PolicyNumber is Already Added..Please Choose different PolicyNumber");
                        }
                        else
                        {
                            $scope.policyDetails.unshift(policy);
                        }
                    }

                }
                $('#policyModal').modal('hide');
                $scope.clear();
            };

            $scope.addAssuredByOthers=function(insurer1)
            {
                console.log('Inside Add addAssuredByOthers..');
                console.log(JSON.stringify(insurer1));
                //$scope.policyDetails.unshift(policy)

                if($scope.insurerDetails1.length == 0)
                {
                    console.log('Lenght is Null..');
                    $scope.insurerDetails1.unshift(insurer1);
                }
                else{

                    for(i in $scope.insurerDetails1) {
                        if($scope.insurerDetails1[i].policyNumber == insurer1.policyNumber)
                        {
                            console.log('Failure..');
                            alert("Particular PolicyNumber is Already Added..Please Choose different PolicyNumber");
                        }
                        else
                        {
                            $scope.insurerDetails1.unshift(insurer1);
                        }
                    }

                }
                $('#assuredByOthersModal').modal('hide');
                $scope.clear();
            };


            $scope.addPendingInsuranceByOthersTpl=function(insurer2)
            {
                console.log('Inside Add addPendingInsuranceByOthersTpl..');
                console.log(JSON.stringify(insurer2));
                //$scope.policyDetails.unshift(policy)

                if($scope.insurerDetails2.length == 0)
                {
                    console.log('Lenght is Null..');
                    $scope.insurerDetails2.unshift(insurer2);
                }
                else{

                    for(i in $scope.insurerDetails2) {
                        if($scope.insurerDetails2[i].policyNumber == insurer2.policyNumber)
                        {
                            console.log('Failure..');
                            alert("Particular PolicyNumber is Already Added..Please Choose different PolicyNumber");
                        }
                        else
                        {
                            $scope.insurerDetails2.unshift(insurer2);
                        }
                    }

                }
                $('#pendingInsuranceByOthersTpl').modal('hide');
                $scope.clear();
            };

            $scope.addaAssuranceDeclinedTpl=function(insurer3)
            {
                console.log('Inside  Add AssuranceDeclinedTpl..');
                console.log(JSON.stringify(insurer3));
                //$scope.policyDetails.unshift(policy)

                if($scope.insurerDetails3.length == 0)
                {
                    console.log('Lenght is Null..');
                    $scope.insurerDetails3.unshift(insurer3);
                }
                else{

                    for(i in $scope.insurerDetails3) {
                        if($scope.insurerDetails3[i].proposalNumber == insurer3.proposalNumber)
                        {
                            console.log('Failure..');
                            alert("Particular ProposalNumber is Already Added..Please Choose different PolicyNumber");
                        }
                        else
                        {
                            $scope.insurerDetails3.unshift(insurer3);
                        }
                    }

                }
                $('#assuranceDeclinedTpl').modal('hide');
                $scope.clear();
            };


            $scope.addAgent = function (agent){
                console.log('Inside addagent Method..');

                if($scope.agentDetails.length == 0)
                {
                    console.log('Lenght is Null..');
                    $scope.agentDetails.unshift(agent);
                }
                else{

                    for(i in $scope.agentDetails) {
                        if($scope.agentDetails[i].agentId == agent.agentId)
                        {
                            console.log('Failure..');
                            alert("Particular AgentId is Already Added..Please Choose different AgentId");
                        }
                        else
                        {
                            $scope.agentDetails.unshift(agent);
                        }
                    }

                }

               /* $scope.agentDetails.unshift(agent);*/
                $('#agentModal').modal('hide');
                $scope.clear();
            };

            $scope.test=function(row)
            {
                console.log('Testing...');
                console.log('Pass..'+JSON.stringify(row));

                for(i in $scope.agentDetails)
                {
                    if($scope.agentDetails[i].agentId == row.agentId )
                    {
                        $scope.agentDetails[i]=row;
                    }
                }
            };


            $scope.countStatus=function()
            {
                var count=0;

                    for(i in $scope.agentDetails) {

                        count = parseInt(count)+parseInt($scope.agentDetails[i].commission);
                    }
                    console.log('count: '+ JSON.stringify(count));
                return count;
                };

            $scope.riderDetails=[];

            $scope.testPlan=function(searchRider)
            {
                console.log('Testing...');
                console.log('Pass..'+JSON.stringify(searchRider));

                for(i in $scope.searchRiders)
                {
                    if($scope.searchRiders[i].coverageName == searchRider.coverageName )
                    {
                        $scope.searchRiders[i]=searchRider;
                    }
                }
            };


            $scope.clear=function()
            {
                $scope.agent={};
                $scope.policy={};
                $scope.insurer1={};
                $scope.insurer2={};
                $scope.insurer3={};

            };

            $scope.searchRiders=function()
            {
                $scope.planId=$scope.proposalPlanDetail.planId;
                console.log('Search Riders Function..' +$scope.planId);
                $http.get("getridersforplan/" +$scope.planId).success(function (response, status, headers, config) {
                    $scope.searchRiders = response;
                    console.log('Riders Details From Db is:');
                    console.log($scope.searchRiders);
                }).error(function (response, status, headers, config) {
                    var check=status;
                    if(check == 500)
                    {

                    }
                });


            }
            $scope.searchAgent = function () {
                $scope.check=false;
                $scope.checking=true;
                console.log('Testing In SearchCode..');
                $scope.agentId=$scope.agent.agentId;
                console.log('Value is: '+$scope.agentId);
                $http.get("getagentdetail/" +$scope.agentId).success(function (response, status, headers, config) {
                 $scope.agent = response;
                    $scope.checking=false;
                 }).error(function (response, status, headers, config) {
                    var check=status;
                    if(check == 500)
                    {
                        $scope.check=true;
                        $scope.agent.firstName=null;
                        $scope.agent.lastName=null;
                    }
                 });

            };

            $scope.proposal=
            {
                "msg":null,
                "proposalId":null
            };

            $scope.additionalDetail={};
            $scope.medicalAttendant={};
            $scope.replacement={};


            $scope.saveAdditionalDetail=function()
            {
                console.log('Inside SaveAdditionalDetail');
                var request=
                {
                    "medicalAttendant":$scope.medicalAttendant,
                    "replacement":$scope.replacement
                };
                //request=angular.extend($scope.additionalDetail,request);
                var request1=
                {
                    "additionalDetail":request,
                    "proposalId":$scope.proposal.proposalId
                }
                console.log('RequiredJson:'+JSON.stringify(request1));

                $http.post('/pla/individuallife/proposal/updateadditionaldetails',request1).success(function (response, status, headers, config) {

                }).error(function (response, status, headers, config) {
                });

            };

            /*$http.get('/pla/individuallife/proposal/getproposalnumber/'+$scope.proposal.proposalId).success(function (response, status, headers, config) {
                console.log('Retrieving Proposal Number..');
                $scope.proposalNumber = response;
            }).error(function (response, status, headers, config) {
                console.log('status',+JSON.stringify(status));
            });*/

            $scope.proposalNumberDetails=
            {
                "proposalNumber":null
            };

            $scope.saveProposedAssuredDetails = function () {
                //ProposalService.saveProposedAssured($scope.proposedAssured, $scope.proposedAssuredSpouse, $scope.paemployment, $scope.paresidential, proposedAssuredAsProposer, null);
                var request = {
                    "spouse": $scope.spouse,
                    "employment": $scope.employment,
                    "residentialAddress": $scope.residentialAddress
                };

                request = angular.extend($scope.proposedAssured, request);
                //request = {proposedAssured: request};
                //console.log('request ' + JSON.stringify(request));

                var request1={
                    "proposedAssured":$scope.proposedAssured,
                    "agentCommissionDetails":$scope.agentDetails
                }

                console.log('Final Result ' + JSON.stringify(request1));
                var count1=$scope.countStatus();
                console.log("Count In Save Method is:"+ JSON.stringify(count1));

                if(count1 == 100)
                {
                    console.log('Sucess..');

                    $http.post('create', request1).success(function (response, status, headers, config) {
                        $scope.proposal = response;
                        console.log('Retrieving Proposal Number..');

                        /*$http.get('/pla/individuallife/proposal/getproposalnumber/'+ $scope.proposal.proposalId).success(function (response, status, headers, config) {
                         console.log('Retrieving Proposal Number..');
                            $scope.proposalNumberDetails.proposalNumber = response;
                            console.log('************* Start ********');
                            console.log('Finding Number..'+$scope.proposalNumberDetails);
                         }).error(function (response, status, headers, config) {
                         console.log('status',+JSON.stringify(status));
                         });
*/

                        //Testing
                        $http.get('/pla/individuallife/proposal/searchplan?proposalId='+$scope.proposal.proposalId).success(function (response, status, headers, config) {
                            console.log('Retrieving PlanDetails..');
                            $scope.iLplanDetails = response;
                        }).error(function (response, status, headers, config) {
                            console.log('status',+JSON.stringify(status));
                        });

                        $http.get("/pla/individuallife/proposal/getproposal/"+$scope.proposal.proposalId +"?mode=view").success(function (response, status, headers, config) {
                            var result = response;
                            console.log('Result:'+JSON.stringify(result));
                            $scope.rcvProposal = response;
                           /* $scope.proposal=
                            {
                                "msg":null,
                                "proposalId":null
                            };*/

                            $scope.proposalNumberDetails.proposalNumber=$scope.rcvProposal.proposalNumber;
                            $scope.proposal.proposalId=$scope.rcvProposal.proposalId;
                            $scope.proposedAssured=$scope.rcvProposal.proposedAssured || {};
                            $scope.proposer=$scope.rcvProposal.proposer || {};
                            $scope.proposerEmployment=$scope.proposer.employment;
                            $scope.proposerResidential=$scope.proposer.residentialAddress;
                            $scope.proposerSpouse=$scope.proposer.spouse;


                            if ($scope.proposedAssured.dateOfBirth) {
                                $scope.proposedAssured.nextDob= moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                            }

                            if ($scope.proposer.dateOfBirth) {
                                $scope.proposer.nextDob= moment().diff(new moment(new Date($scope.proposedAssured.dateOfBirth)), 'years') + 1;
                            }
                            $scope.agentDetails=[];
                            $scope.spouse= $scope.rcvProposal.proposedAssured.spouse;
                            $scope.employment=$scope.rcvProposal.proposedAssured.employment;
                            $scope.residentialAddress=$scope.rcvProposal.proposedAssured.residentialAddress;
                            $scope.agentDetails=$scope.rcvProposal.agentCommissionDetails;

                            //$scope.proposerEmployment=$scope.proposer

                        }).error(function (response, status, headers, config) {
                        });

                        //Testing
                    }).error(function (response, status, headers, config) {
                    });
                }
                else
                {
                    console.log('False..');
                    $scope.updateFlag=true;
                    $scope.agentAlert=true;
                }
            };

            $scope.saveProposerDetails=function(){
                console.log('Save method of Proposer');

                var prorequest={
                    "spouse": $scope.proposerSpouse,
                    "employment": $scope.proposerEmployment,
                    "residentialAddress": $scope.proposerResidential
                };
                prorequest=angular.extend($scope.proposer,prorequest);
                prorequest=
                {
                    "proposer":prorequest,
                    "proposalId":$scope.proposal.proposalId
                };
                console.log('ProRequest' +JSON.stringify(prorequest));

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
            $scope.openAccordion = function (status, tab) {
                console.log(status);
                if (status === 'YES') {
                    $scope.generalAnswer=true;
                    console.log('Checking Status is '+ $scope.generalAnswer);
                    $scope.accordionStatus.generalDetails[tab] = true;
                    $scope.changeStatus();
                } else {
                    $scope.generalAnswer=false;
                    console.log('Checking Status is '+ $scope.generalAnswer);
                    $scope.accordionStatus.generalDetails[tab] = false;
                    $scope.changeStatus();
                }
            };
            $scope.changeStatus=function()
            {
                $scope.generalAnswer={};
            }

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

    .config(["$routeProvider", "$provide", function ($routeProvider, $provide) {
        $routeProvider.when('', {
            templateUrl: 'editsdasfsa',
            controller: 'createProposalCtrl',
            resolve: {

                planList: ['$q', '$http', function ($q, $http) {
                    var deferred = $q.defer();
                    /* $http.get('/pla/individuallife/proposal/getAllindividuallifePlans').success(function (response, status, headers, config) {
                     deferred.resolve(response)
                     }).error(function (response, status, headers, config) {
                     deferred.reject();
                     });
                     return deferred.promise;*/
                    return [];
                }]
            }
        });
        $routeProvider.otherwise({redirectTo: '/plan'});

        /*$provide.decorator('accordionGroupDirective', ['$delegate', function($delegate) {
         var ngModel = $delegate[0];
         ngModel.templateUrl = 'accordionGroup.html';
         ngModel.$$isolateBindings.isPart = {
         attrName: 'isPart',
         mode: '=',
         optional: true
         };
         console.log(ngModel);
         return $delegate;
         }]);*/

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
        additionalDetail: "/pla/individuallife/proposal/getPage/additionalDetail"
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
        window.location.href="proposal/edit?proposalId=" + proposalId + "&mode=edit";
    };

    services.viewProposal = function () {
        var proposalId = this.selectedItem;
        console.log('ID:'+JSON.stringify(proposalId));

         //window.location.href="/pla/individuallife/proposal/getproposal/"+ pid +"?mode=view";
        window.location.href="proposal/edit?proposalId=" + proposalId + "&mode=view";
    };

    services.createUpdateProposal=function()
    {
        alert("createUpdateProposal");
        var quotationId = this.selectedItem;
        window.location.href="edit?quotationId=" + quotationId + "&mode=update";
    };

    return services;
})();


