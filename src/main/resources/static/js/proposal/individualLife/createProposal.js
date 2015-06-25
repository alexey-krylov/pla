angular.module('createProposal', ['pla.individual.proposal', 'common', 'ngRoute', 'commonServices', 'ngMessages'])
    .controller('createProposalCtrl', ['$scope', 'resources', '$bsmodal', '$http',
        'globalConstants', 'ProposalService',
        function ($scope, resources,$bsmodal, $http, globalConstants, ProposalService) {

            console.log('create proposal');
            $scope.employmentTypes = [];
            $scope.occupations = [];
            $scope.provinces = [];

            $http.get('/pla/individuallife/proposal/getAllOccupation').success(function (response, status, headers, config) {
                $scope.occupations = response;
            }).error(function (response, status, headers, config) {
            });
            $http.get('/pla/individuallife/proposal/getAllEmploymentType').success(function (response, status, headers, config) {
                $scope.employmentTypes = response;
            }).error(function (response, status, headers, config) {
            });
            $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                $scope.provinces = response;
            }).error(function (response, status, headers, config) {
            });

            $scope.titles = globalConstants.title;
            $scope.part = {
                isPart: true
            };

            $scope.selectedWizard = 1;
            $scope.proposedAssured = {
                "title": "Mr.",
                "firstName": "Proposed Firstname",
                "surname": "Proposed Surname",
                "otherName": "OtherName",
                "nrc": "NRC0001",
                "dateOfBirth": null,
                "gender": "MALE",
                "mobileNumber": "9343044175",
                "emailAddress": "someAddress@gmail.com",
                "maritalStatus": "MARRIED",
                "isProposer": null
            };

            $scope.beneficiaries = [];
            $scope.titleList = globalConstants.title;
            $scope.genderList = globalConstants.gender;

            $scope.addBeneficiary = function (beneficiary){
                console.log('Inside addBeneficiary Method..');
                $scope.beneficiaries.unshift(beneficiary);
                $('#beneficiaryModal').modal('hide');
            };

            $scope.proposalPlanDetail ={};
            $scope.savePlanDetail=function()
            {
                console.log('Save');
                var request = {
                    "proposalPlanDetail": $scope.proposalPlanDetail,
                    "beneficiaries":$scope.beneficiaries
                }

                /*request = {proposalPlanDetail: request};*/
                console.log('proposalPlanDetail' + JSON.stringify(request));
            };

            $scope.$on('actionclicked.fu.wizard', function (name, event, data) {
                if (data.step == 1) {
                }
            });

            $scope.proposer = {
                "title": "Mr.",
                "firstName": "Proposed Firstname",
                "surname": "Proposed Surname",
                "otherName": "OtherName",
                "nrc": "NRC0001",
                "dateOfBirth": new Date('1978-12-12'),
                "gender": "MALE",
                "mobileNumber": "9343044175",
                "emailAddress": "someAddress@gmail.com",
                "maritalStatus": "MARRIED"
            };
            $scope.proposerSpouse = {};
            $scope.proposerEmployment = {};
            $scope.proposerResidential = {};

            $scope.spouse=
            {
                "firstName": "vffh",
                "surname": "hgd",
                "mobileNumber": "544545",
                "emailAddress": "drdy@jb.com"

            };
            $scope.employment=
            {
                "occupation": null,
                "employer": "Nth Dimenzion",
                "employmentDate": null,
                "employmentType": null,
                "address1": "trrtu",
                "address2": "tur",
                "province": null,
                "postalCode": "12345",
                "town": null,
                "workPhone": "000000000000000"
            };
            $scope.residentialAddress=
            {
                "address1": "ryye",
                "address2": "yry",
                "postalCode": "12345",
                "province": null,
                "town": null,
                "homePhone": "000000",
                "emailAddress": "rtreter@ggmmm.com"
            };

            $scope.familyPersonalDetail = {isPregnant: null, pregnancyMonth: null};
            $scope.familyHistory = {father: {}, mother: {}, brother: {}, sister: {}, question_16: {}};
            $scope.habit = {question_17: {}, question_18: {}, question_19: {}, question_20: {}};
            $scope.build = {question_21: {}};

            $scope.saveFamilyHistory = function () {
                console.log($scope.isPregnant);
                var request = {
                    "familyHistory": $scope.familyHistory,
                    "habit": $scope.habit,
                    "build": $scope.build
                };

                request = angular.extend($scope.familyPersonalDetail, request);
                request = {familyPersonalDetail: request};
                console.log('request ' + JSON.stringify(request));
                $http.post('proposal/createQuestion/55800602db324d0f4ae21254', request);
            }

            $scope.launchProposedAssuredeDate = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob1 = true;
            };

            $scope.launchProposedAssuredeDob = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob1 = true;
            };

            $scope.launchProposerDate = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob1 = true;
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

            $scope.agentDetails = [];
            $scope.addAgent = function (agent){
                console.log('Inside addagent Method..');
                $scope.agentDetails.unshift(agent);
                $('#agentModal').modal('hide');
                $scope.clear();
            };
            $scope.clear=function()
            {
                $scope.agent={};
            };

            $scope.searchAgent = function () {
                console.log('Testing In SearchCode..');
                $scope.agentId=$scope.agent.agentId;
                console.log('Value is: '+$scope.agentId);
                $http.get("getagentdetail/" +$scope.agentId).success(function (response, status, headers, config) {
                 $scope.agent = response;
                 }).error(function (response, status, headers, config) {

                 });

            };

            $scope.saveProposedAssuredDetails = function () {
                //ProposalService.saveProposedAssured($scope.proposedAssured, $scope.proposedAssuredSpouse, $scope.paemployment, $scope.paresidential, proposedAssuredAsProposer, null);

              /*if (proposedAssuredAsProposer) {
                    $scope.proposer = angular.copy($scope.proposedAssured);
                    $scope.proposerSpouse = angular.copy($scope.spouse);
                    $scope.proposerEmployment = angular.copy($scope.employment);
                    $scope.proposerResidential = angular.copy($scope.residentialAddress);


                  var prorequest={
                      "spouse": $scope.proposerSpouse,
                      "employment": $scope.proposerEmployment,
                      "residentialAddress": $scope.proposerResidential
                  };
                  prorequest=angular.extend($scope.proposer,prorequest);
                  prorequest={proposer:prorequest};
                  console.log('ProRequest' +JSON.stringify(prorequest));
                } */

                var request = {
                    "spouse": $scope.spouse,
                    "employment": $scope.employment,
                    "residentialAddress": $scope.residentialAddress
                };

                request = angular.extend($scope.proposedAssured, request);
                //request = {proposedAssured: request};
                console.log('request ' + JSON.stringify(request));

                var request1={
                    "proposedAssured":$scope.proposedAssured,
                    "agentCommissionDetails":$scope.agentDetails
                }

                console.log('Result ' + JSON.stringify(request1));
                /*$http.post('create', request1);
*/
                $http.post('create', request1).success(function (response, status, headers, config) {
                 $scope.proposal = response;
                 console.log('proposalId : '+$scope.proposalId );
                 }).error(function (response, status, headers, config) {
                 });
            };

            $scope.saveProposerDetails=function(){
                console.log('Save method of Proposer');

                var prorequest={
                    "spouse": $scope.proposerSpouse,
                    "employment": $scope.proposerEmployment,
                    "residentialAddress": $scope.proposerResidential
                };
                prorequest=angular.extend($scope.proposer,prorequest);
                prorequest={proposer:prorequest};
                console.log('ProRequest' +JSON.stringify(prorequest));
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
                    $scope.accordionStatus.generalDetails[tab] = true;
                } else {
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


