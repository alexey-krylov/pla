(function (angular) {
    "use strict";

    var createProposalApp = angular.module('createProposal', ['pla.individual.proposal', 'common', 'ngRoute', 'commonServices', 'ngMessages'])

    createProposalApp.config(['datepickerPopupConfig', function (datepickerPopupConfig) {
        datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
        datepickerPopupConfig.currentText = 'Today';
        datepickerPopupConfig.clearText = 'Clear';
        datepickerPopupConfig.closeText = 'Done';
        datepickerPopupConfig.closeOnDateSelection = true;
    }]);
    createProposalApp.controller('createProposalCtrl', ['$scope', 'resources', '$bsmodal',
        'globalConstants', 'ProposalService', 'employmentType', 'occupations', 'provinces',
        function ($scope, resources, $bsmodal, globalConstants, ProposalService, employmentTypes, occupations, provinces) {

            $scope.launchProposerDOB = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.dob2 = true;
            };

            $scope.launchProposedDOB = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.dob1 = true;
            };

            $scope.employmentTypes = employmentTypes;
            $scope.occupations = occupations;
            $scope.provinces = provinces;

            $scope.titles = globalConstants.title;
            $scope.part = {
                isPart: true
            };
            $scope.selectedWizard = 1;
            $scope.proposedAssured = {
                title: "Mr.",
                firstName: "Proposed Firstname",
                surname: "Proposed Surname",
                otherName: "OtherName",
                nrc: "NRC0001",
                dateOfBirth: new Date('1978-12-12'),
                gender: "MALE",
                mobileNumber: "9343044175",
                emailAddress: "someAddress@gmail.com",
                maritalStatus: "MARRIED"
            };
            $scope.proposedAssuredSpouse = {
                "firstName": "Proposed Spouse Firstname",
                "surname": "Proposed Spouse Surname",
                "mobileNumber": "9343044175",
                "emailAddress": 'spouse@mail.com'
            };
            $scope.paemployment = {
                "occupation": "Advocates",
                "employer": "Nthdimenzion Solutions Pvt Limited",
                "employmentDate": "12/12/008",
                "employmentType": 1,
                "address1": "Address Line 1",
                "province": "PR-CEN",
                "town": "CI-CHIB"
            };

            $scope.paresidential = {
                "address1": "Address Line 1",
                "province": "PR-CEN",
                "town": "CI-CHIB"
            };

            $scope.$on('actionclicked.fu.wizard', function (name, event, data) {
                if (data.step == 1) {
                }
            });

            $scope.proposer = {};
            $scope.proposerSpouse = {};
            $scope.proposerEmployment = {};
            $scope.proposerResidential = {};

            $scope.$watchGroup(['paemployment.province', 'paresidential.province', 'proposerEmployment.province', 'proposerResidential.province'], function (newVal, oldVal) {
                if (!newVal) return;

                if (newVal[0]) {
                    var provinceDetails = _.findWhere(provinces, {provinceId: newVal[0]});
                    $scope.proposedAssuredEmploymentCities = provinceDetails.cities;
                }
                if (newVal[1]) {
                    var provinceDetails = _.findWhere(provinces, {provinceId: newVal[1]});
                    $scope.proposedAssuredResidentialCities = provinceDetails.cities;
                }
                if (newVal[2]) {
                    console.log('employment ' + newVal[2]);
                    var provinceDetails = _.findWhere(provinces, {provinceId: newVal[2]});
                    $scope.proposerEmploymentCities = provinceDetails.cities;
                }
                if (newVal[3]) {
                    console.log('residential ' + newVal[3]);
                    var provinceDetails = _.findWhere(provinces, {provinceId: newVal[3]});
                    $scope.proposerResidentialCities = provinceDetails.cities;
                }
            });


            $scope.saveProposedAssuredDetails = function (proposedAssuredAsProposer) {
                //ProposalService.saveProposedAssured($scope.proposedAssured, $scope.proposedAssuredSpouse, $scope.paemployment, $scope.paresidential, proposedAssuredAsProposer, null);
                if (proposedAssuredAsProposer) {
                    $scope.proposer = angular.copy($scope.proposedAssured);
                    $scope.proposerSpouse = angular.copy($scope.proposedAssuredSpouse);
                    $scope.proposerEmployment = angular.copy($scope.paemployment);
                    $scope.proposerResidential = angular.copy($scope.paresidential);
                }
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

            $scope.openBeneficiaryModal = function () {
                var beneficiaryModalInstance = $bsmodal.open({
                    templateUrl: resources.beneficiaryModal,
                    controller: 'addBeneficiaryCtrl',
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
            }


        }]);
    createProposalApp.controller('modalCtrl', ['$scope', '$modalInstance', function ($scope, $modalInstance) {
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
        }]);
    createProposalApp.controller('addBeneficiaryCtrl', ['$scope', '$modalInstance', 'globalConstants', function ($scope, $modalInstance, globalConstants) {
        $scope.titleList = globalConstants.title;
        $scope.genderList = globalConstants.gender;
        $scope.addBeneficiary = function (beneficiary) {
            $modalInstance.close(beneficiary);
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);
    createProposalApp.config(["$routeProvider", "$provide", function ($routeProvider, $provide) {
        $routeProvider.when('/', {
            templateUrl: 'proposal/createProposalForm',
            controller: 'createProposalCtrl',
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
                }],
                employmentType: ['$q', '$http', function ($q, $http) {
                    var deferred = $q.defer();
                    $http.get('/pla/individuallife/proposal/getAllEmploymentType').success(function (response, status, headers, config) {
                        deferred.resolve(response);
                    }).error(function (response, status, headers, config) {
                        deferred.reject();
                    });
                    return deferred.promise;
                    ;
                }],
                provinces: ['$q', '$http', function ($q, $http) {
                    var deferred = $q.defer();
                    $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                        deferred.resolve(response)
                    }).error(function (response, status, headers, config) {
                        deferred.reject();
                    });
                    return deferred.promise;
                }],
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

    }]);
    createProposalApp.constant('resources', {
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
    });
    createProposalApp.filter('getTrustedUrl', ['$sce', function ($sce) {
        return function (url) {
            return $sce.getTrustedResourceUrl(url);
        }
    }]);
})(angular);
