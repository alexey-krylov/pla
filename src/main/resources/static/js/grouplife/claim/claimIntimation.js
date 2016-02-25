
var App = angular.module('claimIntimation', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives',
                                               , 'ngSanitize', 'commonServices', 'ngMessages','angularFileUpload','ngAnimate','mgcrea.ngStrap']);

       App.config(['datepickerPopupConfig', function (datepickerPopupConfig) {
            datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
            datepickerPopupConfig.currentText = 'Today';
            datepickerPopupConfig.clearText = 'Clear';
            datepickerPopupConfig.closeText = 'Done';
            datepickerPopupConfig.closeOnDateSelection = true;
}
]);
App .directive('modal', function () {
    return {
        template: '<div class="modal fade">' +
        '<div class="modal-dialog modal-sm">' +
        '<div class="modal-content">' +
        '<div class="modal-header">' +
        '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
        '<h4 class="modal-title">{{ title }}</h4>' +
        '</div>' +
        '<div class="modal-body" ng-transclude></div>' +
        '</div>' +
        '</div>' +
        '</div>',
        restrict: 'E',
        transclude: true,
        replace: true,
        scope: true,
        link: function postLink(scope, element, attrs) {
            scope.title = attrs.title;

            scope.$watch(attrs.visible, function (value) {
                if (value == true)
                    $(element).modal('show');
                else
                    $(element).modal('hide');
            });

            $(element).on('shown.bs.modal', function () {
                scope.$apply(function () {
                    scope.$parent[attrs.visible] = true;
                });
            });

            $(element).on('hidden.bs.modal', function () {
                scope.$apply(function () {
                    scope.$parent[attrs.visible] = false;
                });
            });
        }
    };
});

App.controller('ClaimIntimationController', ['$scope', '$http','$window', '$upload','getQueryParameter','provinces','bankDetails','globalConstants','occupations',
        function ($scope, $http, $window,$upload,getQueryParameter,provinces,bankDetails,globalConstants,occupations) {
//alert("********CONTROLLER INCLUDED***********");
        $scope.schedule={};
        $scope.searchObj = {};
        $scope.referral={};
        $scope.agency={};
        $scope.selectedItem = 1;
        $scope.saveAccidentObject={};
        $scope.provinces = [];
        $scope.provinces = provinces;
        $scope.towns=[];
        $scope.bankDetailsResponse=[];
        $scope.bankDetailsResponse=bankDetails;
        $scope.bankBranchDetails=[];
        $scope.rcvPolicyId = getQueryParameter('policyId');
        $scope.rcvCriteria=getQueryParameter('criteria');
        $scope.minIntimationDate=moment().add(0,'days').format("YYYY-MM-DD");
        $scope.isAdvanceSearchEnable=false;
        //$scope.minIntimationDate=moment().add(1,'days').format("YYYY-MM-DD");
        $scope.titles = globalConstants.title;
        $scope.occupations = [];
        $scope.occupations=occupations;
        $scope.assuredDetails={};
        $scope.claimDetails={};
        $scope.claimType=[];
        $scope.assuredCriteria={};
        $scope.assuredSearchResult=[];
        $scope.documentList = [];
        $scope.additionalDocumentList = [{}];
        $scope.rcvClaimIdForRegistration = getQueryParameter('claimId');
        $scope.dailyTaskList=["Dressing","Using the Toilet","Walking","Feeding Him/Herself","Using Telephone","Bathing","Taking Medication"];
            $scope.comments={};
            $scope.approvalCommentList=[];
            /***
             *
             * @param $event for Claim intimation Date
             */
            $scope.open = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettings.isOpened = true;
            };
            $scope.datePickerSettings = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }
            /***
             *@param $event for Claim incidence Date
             */
            $scope.openClaimIncidenceDate = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForIncidence.isOpened = true;
            };
            $scope.datePickerSettingsForIncidence = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }
            /**
             * Claim Approve On Date setting
             * @param $event
             */
            $scope.openClaimApproveDate = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForClaimApproveDate.isOpened = true;
            };
            $scope.datePickerSettingsForClaimApproveDate = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }

            $scope.openInstrumentDate = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForInstrument.isOpened = true;
            };
            $scope.datePickerSettingsForInstrument = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }

            /**
             *
             * @param $event for Advance Search Assured DOB
             */
            $scope.openAssuredDOB = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForAssuredDOB.isOpened = true;
            };
            $scope.datePickerSettingsForAssuredDOB = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }

            /***
             *
             * @param $event for Assured Details Date Of Birth
             */
            $scope.openDob = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForDOB.isOpened = true;
            };
            $scope.datePickerSettingsForDOB = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }
            $scope.incidenceDetails={};  //Capturing Data For Incidence Details
            /**
             * Setting For Date of Death for Incidenct Details
             * @param $event
             */
            $scope.openDod = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForDOD.isOpened = true;
            };
            $scope.datePickerSettingsForDOD = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }

            /***
             * Date of First Consultation Date setting
             */
            $scope.openDeathConsult = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForDeathConsult.isOpened = true;
            };
            $scope.datePickerSettingsForDeathConsult = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }
            $scope.openFromDate = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForFromDate.isOpened = true;
            };
            $scope.datePickerSettingsForFromDate = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }
            $scope.openFromDate2 = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForFromDate2.isOpened = true;
            };
            $scope.datePickerSettingsForFromDate2 = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }
            $scope.openFromDate3 = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForFromDate3.isOpened = true;
            };
            $scope.datePickerSettingsForFromDate3 = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }
            /**
             * Date of Accident Date Setting
             */
            $scope.openDeathAccident = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForDeathAccident.isOpened = true;
            };
            $scope.datePickerSettingsForDeathAccident = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }

            /**
             * Disability Date Setting
             * @param $event
             */

            $scope.disabilityIncidentDetails={}; //Initialisation of disabiltyIncidentDetails

            $scope.openDisabilityDate = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForDisabilityDate.isOpened = true;
            };
            $scope.datePickerSettingsForDisabilityDate = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }
            /**
             * Date Of Diagnosis
             * @param $event
             */
            $scope.openDiagnosisDate = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForDiagnosisDate.isOpened = true;
            };
            $scope.datePickerSettingsForDiagnosisDate = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }

            /**
             * Referred to Re-Assurer on Date Setting
             * @param $event
             */
            $scope.openReassureDate = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForReassureDate.isOpened = true;
            };
            $scope.datePickerSettingsForReassureDate = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }
            /**
             * Response received on Date Setting
             * @param $event
             */
            $scope.openResponseDate = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForResponseDate.isOpened = true;
            };
            $scope.datePickerSettingsForResponseDate = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }

            /**
             *
             * @param province
             * Getting List of Related Cities
             */
            $scope.getProvinceValue=function(province){
                if(province){
                    var provinceDetails1 = _.findWhere($scope.provinces, {provinceId: province});
                    if(provinceDetails1){
                        $scope.towns=provinceDetails1.cities;
                    }
                }
            }
            $scope.$watch('claimantDetail.province',function(newVal,oldVal){
                if(newVal){
                    var provinceDetails1 = _.findWhere($scope.provinces, {provinceId: newVal});
                    if(provinceDetails1){
                        $scope.towns=provinceDetails1.cities;
                    }
                }
            });
            /***
             * Getting List of Related Branch
             */
            $scope.bankDetails={};
            $scope.$watch('bankDetails.bankName', function (newvalue, oldvalue) {
                if (newvalue) {
                    var bankCode = _.findWhere($scope.bankDetailsResponse, {bankName: newvalue});
                    if (bankCode) {
                        $http.get('/pla/grouplife/claim/getAllBankBranchNames/' + bankCode.bankCode).success(function (response, status, headers, config) {
                            $scope.bankBranchDetails = response;
                        }).error(function (response, status, headers, config) {
                        });
                    }
                }
            });
            $scope.bankBranchDetailsForClaimSettlement={}; //bankBranch Detail Contatiner for Settlement Tab
            $scope.claimSettlementDetails={};
            /**
             * Getting List of Related Branch Name
             */
            $scope.$watch('claimSettlementDetails.bankName', function (newvalue, oldvalue) {
                if (newvalue) {
                    var bankCode = _.findWhere($scope.bankDetailsResponse, {bankName: newvalue});
                    if (bankCode) {
                        $http.get('/pla/grouplife/claim/getAllBankBranchNames/' + bankCode.bankCode).success(function (response, status, headers, config) {
                            $scope.bankBranchDetailsForClaimSettlement = response;
                        }).error(function (response, status, headers, config) {
                        });
                    }
                }
            });


            /**
             * Retrival All Claim For Registraton
             */
            //$scope.incidenceDetails.timeOfDeath=moment("1969-12-31T18:42:00.000Z").format('YYYY-MM-DDTHH:mm:ss:00.000');
            $scope.claimApprovalPlanDetail={}; // To Store Claim Approval PlanDetails
            $scope.claimApprovalCoverageDetails=[];
            $scope.dEATHFUNERAL=false;
            if($scope.rcvClaimIdForRegistration){
                $http.get('/pla/grouplife/claim/getclaimdetail/' + $scope.rcvClaimIdForRegistration).success(function (response, status, headers, config) {
                    console.log(JSON.stringify(response));
                    $scope.retrivalPolicyId=response.policyId;
                    $scope.claimId=response.claimId;
                    $scope.claimNumber=response.claimNumber;
                    $scope.claimDetails=response;
                    $scope.claimantDetail=response.claimantDetail;
                    $scope.planDetail=response.planDetail;
                    $scope.bankDetails=response.bankDetails;
                    $scope.assuredDetails=response.claimAssuredDetail;
                    $scope.coverageList=response.coverageDetails;
                    $scope.underWritingLevelOneStatus = false;
                    $scope.underWritingLevelTwoStatus = false;

                    if (response.routingLevel != null) {
                        if (response.routingLevel == 'UnderWriting level 1') {
                            $scope.underWritingLevelOneStatus = true;
                        }
                        else if (response.routingLevel == 'UnderWriting level 2') {
                            //$scope.underWritingLevelTwoStatus = true;
                            $scope.underWritingLevelOneStatus = false;
                        }
                        else {
                            $scope.underWritingLevelOneStatus = false;
                            $scope.underWritingLevelTwoStatus = false;
                        }
                    }

                    $http.get("/pla/grouplife/claim/getmandatorydocuments/" + $scope.claimId)
                        .success(function (response) {
                            $scope.documentList = response;
                        });

                    $http.get("/pla/grouplife/claim/getadditionaldocuments/" + $scope.claimId).success(function (data, status) {
                        //console.log(data);
                        $scope.additionalDocumentList = data;
                        $scope.checkDocumentAttached = $scope.additionalDocumentList != null;
                    });

                    if(response.claimApprovalPlanDetail != null){
                        angular.copy(response.claimApprovalPlanDetail,$scope.claimApprovalPlanDetail);
                    }else{
                        angular.copy(response.planDetail,$scope.claimApprovalPlanDetail);
                        //$scope.claimApprovalPlanDetail=response.planDetail;
                    }
                    if(response.claimApprovalCoverageDetails != null){
                        angular.copy(response.claimApprovalCoverageDetails,$scope.claimApprovalCoverageDetails);
                    }else{
                        angular.copy(response.coverageDetails,$scope.claimApprovalCoverageDetails);
                        //$scope.claimApprovalCoverageDetails=response.coverageDetails;
                    }

                    if($scope.claimDetails.claimType == 'DEATH' || $scope.claimDetails.claimType == 'FUNERAL'){
                        $scope.dEATHFUNERAL=true;
                    }
                    else if($scope.claimDetails.claimType != 'DEATH' || $scope.claimDetails.claimType != 'FUNERAL'){
                        $scope.dEATHFUNERAL=false;
                    }
                    if(response.claimRegistrationDetails !=null){
                        $scope.incidenceDetails=response.claimRegistrationDetails;
                    }
                    if(response.disabilityRegistrationDetails != null){
                        $scope.disabilityIncidentDetails=response.disabilityRegistrationDetails;
                    }
                    if(response.claimSettlementDetails != null){
                        $scope.claimSettlementDetails=response.claimSettlementDetails;
                    }
                    if(response.approvalDetails !=null && response.approvalDetails.referredToReassuredOn !=null){
                        $scope.comments.reAssuredOn=response.approvalDetails.referredToReassuredOn;
                    }
                    if(response.approvalDetails !=null && response.approvalDetails.responseReceivedOn != null){
                        $scope.comments.reponseReceivedOn=response.approvalDetails.responseReceivedOn;
                    }
                    if(response.approvalDetails !=null && response.approvalDetails.reviewDetails != null){
                        $scope.approvalCommentList=response.approvalDetails.reviewDetails;
                        console.log('**** Test');
                        console.log(JSON.stringify($scope.approvalCommentList));
                    }
                    if(response.approvalDetails !=null && response.approvalDetails.totalApprovedAmount != null){
                        $scope.comments.totalApprovedAmount=response.approvalDetails.totalApprovedAmount;
                        console.log(JSON.stringify($scope.approvalDetails.totalApprovedAmount));
                    }
                }).error(function (response, status, headers, config) {
                });


            }

            /**
             * Incidence Detail Save Logic Implemented
             */
            $scope.saveIncidenceDetails=function(){
                console.log('*****************');
                //console.log(JSON.stringify($scope.incidenceDetails));
                if(($scope.dEATHFUNERAL)){
                    console.log('DeathFunearl...');
                    var createRegistration={
                        "incidentDetails":$scope.incidenceDetails,
                        "claimId":$scope.claimId
                    }
                    console.log(JSON.stringify(createRegistration));
                }else{
                    console.log('DISABILITY...');
                    var createdisabilityIncidentDetails={
                        "disabilityIncidentDetails":$scope.disabilityIncidentDetails,
                        "claimId":$scope.claimId
                    }
                    console.log(JSON.stringify(createdisabilityIncidentDetails));
                }

                if($scope.dEATHFUNERAL){
                    // Saving the Death and Funeral Calim Type
                    $http.post('/pla/grouplife/claim/claimregistration',createRegistration).success(function (response, status, headers, config) {

                    }).error(function (response, status, headers, config) {
                    });
                }else{
                    // Saving the DISABILITY Calim Type
                    $http.post('/pla/grouplife/claim/disabilityclaimregistration',createdisabilityIncidentDetails).success(function (response, status, headers, config) {

                    }).error(function (response, status, headers, config) {
                    });
                }
            }

            /**
             * Claim Registration
             */
            $scope.createRegistration=function(){
                var submitRegistration={
                    "claimId":$scope.claimId
                }

                $http.post('/pla/grouplife/claim/submit',submitRegistration).success(function (response, status, headers, config) {

                }).error(function (response, status, headers, config) {
                });
            }

            /***
             * Geating All Details Of Particular PolicyNumber and Populating in GL ClaimIntimation Screen
             */
            $scope.isAssuredDetailsShared=false; //to Enable or Disable the Advnce Search Button

            if($scope.rcvPolicyId){
                // Logic to Populate All The date to ClaimIntimation Class
                //$scope.policyNumber=$scope.rcvPolicyNumber;

               $http.get('/pla/grouplife/claim/getclaimant/' + $scope.rcvPolicyId).success(function (response, status, headers, config) {
                    console.log(JSON.stringify(response));
                   $scope.claimDetails.schemeName=response.schemeName;
                   $scope.claimDetails.policyNumber=response.policyNumber;
                   $scope.claimantDetail=response.claimantDetail;
                   $scope.categorySet=response.categorySet;
                   $scope.isAssuredDetailsShared=response.isAssuredDetailsShared;

                }).error(function (response, status, headers, config) {
                });

                //$scope.claimIntimationDate=moment(new Date()).format('YYYY-MM-DD');
                $scope.claimDetails.claimIntimationDate=moment().add(0,'days').format("YYYY-MM-DD");

            }
            /**
             * Openning Advance Search panel
             */
            $scope.openAdvaceSearch=function(){
                //alert('Advance Search');
                $scope.isAdvanceSearchEnable=true;
            }

            /**
             * Calculating Nex DOB
             */
            $scope.calculateAssuredDOB=function(){
                if ($scope.assuredDetails.dateOfBirthInDateTime) {
                    $scope.assuredDetails.nextDob = moment().diff(new moment(new Date($scope.assuredDetails.dateOfBirthInDateTime)), 'years') + 1;
                    //alert('dateof Birth');
                }
            }

            /**
             * Retrieving the RelationShip List
             */
            $scope.getRelationShipList=function(){
                $http.get('/pla/grouplife/claim/getrelationship/'+ $scope.rcvPolicyId +'/'+$scope.claimDetails.category).success(function (response, status, headers, config) {
                    //console.log(JSON.stringify(response));
                    $scope.reltaionShipList=response.relationship;
                }).error(function (response, status, headers, config) {
                });
            }
            /**
             * Retrieving PlanDetailWithClaimTYpeList
             */

           /* $scope.documentList=[
                {
                    "documentId": "ACTIVE_AT_WORK_DECLARATION_FORM",
                    "documentName": "Active At Work Declaration Form",
                    "file": null,
                    "content": null,
                    "submitted": false,
                    "fileName": null,
                    "contentType": null,
                    "gridFsDocId": null,
                    "requireForSubmission": false
                },
                {
                    "documentId": "ADDRESS_PROOF",
                    "documentName": "Address Proof",
                    "file": null,
                    "content": null,
                    "submitted": false,
                    "fileName": null,
                    "contentType": null,
                    "gridFsDocId": null,
                    "requireForSubmission": false
                }
            ]*/

            $scope.getPlanDetailWithClaimTypeList=function(){
                $http.get('/pla/grouplife/claim/getplandetail/'+ $scope.rcvPolicyId +'/'+$scope.claimDetails.category+'/'+$scope.claimDetails.relationship).success(function (response, status, headers, config) {
                    console.log(JSON.stringify(response));
                    $scope.claimTypes=response.claimTypes;
                    $scope.planDetail=response.planDetailDto;  //planDetailDto
                    $scope.coverageList=response.coverageDetailDtos;

                    if(!$scope.isAssuredDetailsShared){
                        $scope.assuredDetails.sumAssured=$scope.planDetail.sumAssured;
                    }

                    //Retriving the DocumentList
                     $http.get('/pla/grouplife/claim/getallrequiredmandatorydocuments/'+$scope.planDetail.planId).success(function (response, status, headers, config) {
                                        console.log('Document List...******');
                                        console.log(JSON.stringify(response));
                                        $scope.documentList=response;

                                       /*$scope.documentList=[
                                          {
                                            "documentId": "ACTIVE_AT_WORK_DECLARATION_FORM",
                                            "documentName": "Active At Work Declaration Form",
                                            "file": null,
                                            "content": null,
                                            "submitted": false,
                                            "fileName": null,
                                            "contentType": null,
                                            "gridFsDocId": null,
                                            "requireForSubmission": false
                                          },
                                          {
                                            "documentId": "ADDRESS_PROOF",
                                            "documentName": "Address Proof",
                                            "file": null,
                                            "content": null,
                                            "submitted": false,
                                            "fileName": null,
                                            "contentType": null,
                                            "gridFsDocId": null,
                                            "requireForSubmission": false
                                          }
                                        ]*/
                                    }).error(function (response, status, headers, config) {
                                    });


                }).error(function (response, status, headers, config) {
                });
            }
              /**
                         * claimSubmit Logic
                         */

            $scope.createClimIntimation=function(){
            // Submiting Claim
            var claimSubmitObj={
            "policyNumber":$scope.claimDetails.policyNumber,
            "schemeName":$scope.claimDetails.schemeName,
            "relationship":$scope.claimDetails.relationship,
            "category":$scope.claimDetails.category,
            "claimantDetail":$scope.claimantDetail,
            "planDetail":$scope.planDetail,
            "bankDetails":$scope.bankDetails,
            "claimType":$scope.claimDetails.claimType,
            "claimAssuredDetail":$scope.assuredDetails,
            "coverageDetails":$scope.coverageList,
            "claimIntimationDate":$scope.claimDetails.claimIntimationDate,
            "claimIncidenceDate":$scope.claimDetails.claimIncidenceDate};

                console.log('******************');
                console.log(JSON.stringify(claimSubmitObj));

            $http.post('/pla/grouplife/claim/createclaimintimation',claimSubmitObj).success(function (response, status, headers, config) {
                                console.log('generate Claim Intimation Response..');
                                console.log(JSON.stringify(response));
                                //$scope.claimId=response.id;
                                 $scope.claimId=response.data.claimId;
                                $scope.claimNumber=response.data.claimNumber;
                                 $scope.uploadDocumentFiles();  //Uplaoding Mandatory Documents
                                 $scope.uploadAdditionalDocument(); // Uplaoding Additional Documents
                                //$scope.showModal = true;
                                //$scope.errorMessage = 'Are You Want to Upload The Document?';
                                $window.location.href = '/pla/grouplife/claim/openpolicysearchpage';

                            }).error(function (response, status, headers, config) {
                            });

            }

            $scope.proceedToNext = function () {
                $scope.showModal = false;
            }

            $scope.backToSearchPolicy=function(){
                window.location.href = '/pla/grouplife/claim/openpolicysearchpage';
            }

            /**
             * Adding Contact Details..
             * @type {{contact: boolean}}
             */
            $scope.addContactDetail = function (contactDetails) {
                console.log(contactDetails);
                $scope.claimantDetail.contactPersonDetail.push(contactDetails);
                $scope.contactDetails = {};

            }
            /**
             * Edit ContactDetails..
             * @type {{contact: boolean}}
             */
            $scope.editCurrentRow = function (contactDetails, index) {
                $scope.contactDetails = contactDetails;
                $scope.claimantDetail.contactPersonDetail.splice(index, 1);
            }
            /**
             * Delete Contact Details..
             * @type {{contact: boolean}}
             */
            $scope.deleteCurrentRow = function (index) {
                $scope.claimantDetail.contactPersonDetail.splice(index, 1);
            }
            $scope.accordionStatus = {
                contact: false
            };

            /***
             * Assured Search Criteria
             */
            $scope.assuredSearch=function(){
                var searchRequestObj={
                    "category":$scope.claimDetails.category,
                    "relationShip":$scope.claimDetails.relationship,
                    "policyNumber":$scope.claimDetails.policyNumber,
                    "firstName":$scope.assuredCriteria.firstName,
                    "surName":$scope.assuredCriteria.surname,
                    "dateOfBirth":$scope.assuredCriteria.assuredDOB,
                    "clientId":$scope.assuredCriteria.clientId,
                    "nrcNumber":$scope.assuredCriteria.nrc,
                    "manNumber":$scope.assuredCriteria.manNumber,
                    "gender":$scope.assuredCriteria.gender
                };
                console.log('AssuredSearchObject '+JSON.stringify(searchRequestObj));

                // Calling Service to get AssuredSearch Related data
                $http.post('/pla/grouplife/claim/assuredsearch',searchRequestObj).success(function (response, status, headers, config) {
                    console.log('Assured Search Response..');
                    console.log(JSON.stringify(response));
                     $scope.assuredSearchResult=response;
                    if($scope.assuredSearchResult.length > 0){
                        $('#assuredSearchModal').modal('show');
                    }
                 //$('#assuredSearchModal').modal('show');

                }).error(function (response, status, headers, config) {
                });

                /*$scope.assuredSearchResult=[
                    {
                        "firstName": "ravi",
                        "surName": "kumar",
                        "dateOfBirth": "09/06/1990",
                        "gender": "MALE",
                        "nrcNumber": "",
                        "manNumber": "",
                        "clientId": "10012301"
                    },
                    {
                        "firstName": "ravi",
                        "surName": "kumar",
                        "dateOfBirth": "09/06/1990",
                        "gender": "MALE",
                        "nrcNumber": "",
                        "manNumber": "",
                        "clientId": "10012301"
                    }
                ];*/

                //$('#assuredSearchModal').modal('show');
                //console.log('JSON**'+JSON.stringify($scope.assuredSearchResult));
            }
            /**
             * closeAssuredSearchModal
             */
            $scope.closeAssuredSearchModal=function(){
                $('#assuredSearchModal').modal('toggle');
            }


            /**
             * Retriving the Selected Assured Details..
             */
            $scope.selectCurrentAssured=function(index,clientId){
                // Call server to Retrival of the Assured Details.
                $http.get('/pla/grouplife/claim/assureddetail/'+ $scope.rcvPolicyId +'/'+clientId).success(function (response, status, headers, config) {
                    console.log(JSON.stringify(response));
                    $scope.assuredDetails=response;
                }).error(function (response, status, headers, config) {
                });

                $scope.closeAssuredSearchModal();
            }
       $scope.launchclaimDate = function ($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    $scope.claimIntimationDate = true;
                };

            /**
             * FileUploading mandatoryDocument
             * @param $event
             */
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
                            url: '/pla/grouplife/claim/uploadmandatorydocument',
                            file: files,
                            fields: {
                                documentId: document.documentId,
                                claimId: $scope.claimId,
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

            $scope.addAdditionalDocument = function () {
                $scope.additionalDocumentList.unshift({});
                $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
            };
            $scope.isUploadEnabledForAdditionalDocument = function () {
                var enableAdditionalUploadButton = ($scope.additionalDocumentList != null);
                console.log("enable value" + enableAdditionalUploadButton);
                for (var i = 0; i < $scope.additionalDocumentList.length; i++) {
                    var document = $scope.additionalDocumentList[i];
                    var files = document.documentAttached;
                    //  alert(i+"--"+files)
                    //  alert(i+"--"+document.content);
                    if (!(files || document.content)) {
                        enableAdditionalUploadButton = false;
                        break;
                    }
                }
                return enableAdditionalUploadButton;
            }

            /**
             * Uploading Additional Document
             */
            $scope.uploadAdditionalDocument = function () {
                for (var i = 0; i < $scope.additionalDocumentList.length; i++) {
                    var document = $scope.additionalDocumentList[i];
                    var files = document.documentAttached;
                    if (files) {
                        $upload.upload({
                            url: '/pla/grouplife/claim/uploadmandatorydocument',
                            file: files,
                            fields: {documentId: document.documentId, claimId: $scope.claimId, mandatory: false},
                            method: 'POST'
                        }).progress(function (evt) {

                        }).success(function (data, status, headers, config) {
                            //console.log('file ' + config.file.name + 'uploaded. Response: ' +
                            // JSON.stringify(data));
                        });
                    }

                }
            };
            $scope.callAdditionalDoc = function (file) {
                if (file[0]) {
                    $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
                }
            }

            /**
             * Removal of Additional Document List
             * @param index
             * @param gridFsDocId
             */
            $scope.removeAdditionalDocument = function (index, gridFsDocId) {
                $scope.additionalDocumentList.splice(index, 1);
                $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();

               /* if (gridFsDocId) {
                    $http.post("/pla/grouphealth/proposal/removeGHProposalAdditionalDocument?proposalId=" + $scope.proposalId + "&gridFsDocId=" + gridFsDocId).success(function (data, status) {
                        console.log(data);
                        if (data.status == '200') {
                            $scope.additionalDocumentList.splice(index, 1);
                            $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
                        }
                    });
                } else {
                    $scope.additionalDocumentList.splice(index, 1);
                    $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();

                }*/
            };

            $scope.totalApprovedAmountCalculation=function(){
                var result=0;
                for (i in $scope.claimApprovalCoverageDetails) {
                    if($scope.claimApprovalCoverageDetails[i].approvedAmount){
                        result = parseFloat(result) + parseFloat($scope.claimApprovalCoverageDetails[i].approvedAmount);
                    }else{
                        result = parseFloat(result);
                    }
                    //result = parseFloat(result) + parseFloat($scope.claimApprovalCoverageDetails[i].approvedAmount);
                }

                /*for(var i=0;i< $scope.claimApprovalCoverageDetails.length;i++){
                    if($scope.claimApprovalCoverageDetails[i].approvedAmount != null){
                        result=parseFloat($scope.claimApprovalCoverageDetails[i].approvedAmount)+parseFloat(result);
                    }

                }*/
                //$scope.comments.totalApprovedAmount=parseFloat($scope.claimApprovalPlanDetail.approvedAmount);
                $scope.comments.totalApprovedAmount=parseFloat(result);
            }

            $scope.approveClaim=function(){
                var requestForApproveClaim={
                    "claimApprovalPlanDetail":$scope.claimApprovalPlanDetail,
                    "claimApprovalCoverageDetails":$scope.claimApprovalCoverageDetails,
                    "comments":$scope.comments.comment,
                    "referredToReassureOn":$scope.comments.reAssuredOn,
                    "responseReceivedOn":$scope.comments.reponseReceivedOn,
                    "claimId":$scope.claimId,
                    "totalApprovedAmount":$scope.comments.totalApprovedAmount,
                    "criteria":$scope.rcvCriteria
                }
                console.log('Approval Request *********');
                console.log(JSON.stringify(requestForApproveClaim));
                $http.post('/pla/grouplife/claim/approve',requestForApproveClaim).success(function (response, status, headers, config) {
                    window.location.href ="/pla/grouplife/claim/openapprovalclaim";

                }).error(function (response, status, headers, config) {
                });
            }

            $scope.rejectClaim=function(){
                var requestForRejectClaim={
                    "claimApprovalPlanDetail":$scope.claimApprovalPlanDetail,
                    "claimApprovalCoverageDetails":$scope.claimApprovalCoverageDetails,
                    "comments":$scope.comments.comment,
                    "referredToReassureOn":$scope.comments.reAssuredOn,
                    "responseReceivedOn":$scope.comments.reponseReceivedOn,
                    "claimId":$scope.claimId,
                    "criteria":$scope.rcvCriteria
                }
                console.log('Reject Request *********');
                console.log(JSON.stringify(requestForRejectClaim));
                $http.post('/pla/grouplife/claim/reject',requestForRejectClaim).success(function (response, status, headers, config) {
                    window.location.href ="/pla/grouplife/claim/openapprovalclaim";

                }).error(function (response, status, headers, config) {
                });
            }
            $scope.returnClaim=function(){
                var requestForReturnClaim={
                    "claimApprovalPlanDetail":$scope.claimApprovalPlanDetail,
                    "claimApprovalCoverageDetails":$scope.claimApprovalCoverageDetails,
                    "comments":$scope.comments.comment,
                    "referredToReassureOn":$scope.comments.reAssuredOn,
                    "responseReceivedOn":$scope.comments.reponseReceivedOn,
                    "claimId":$scope.claimId,
                    "criteria":$scope.rcvCriteria
                }
                console.log('Return Request *********');
                console.log(JSON.stringify(requestForReturnClaim));
                $http.post('/pla/grouplife/claim/return',requestForReturnClaim).success(function (response, status, headers, config) {
                    window.location.href ="/pla/grouplife/claim/openapprovalclaim";

                }).error(function (response, status, headers, config) {
                });
            }

            $scope.claimReferToSeniorUnderWriter=function(){
                var requestForReferToSeniorUnderWriterClaim={
                    "claimApprovalPlanDetail":$scope.claimApprovalPlanDetail,
                    "claimApprovalCoverageDetails":$scope.claimApprovalCoverageDetails,
                    "comments":$scope.comments.comment,
                    "referredToReassureOn":$scope.comments.reAssuredOn,
                    "responseReceivedOn":$scope.comments.reponseReceivedOn,
                    "claimId":$scope.claimId,
                    "criteria":$scope.rcvCriteria
                }
                console.log('Return Request *********');
                console.log(JSON.stringify(requestForReferToSeniorUnderWriterClaim));
                $http.post('/pla/grouplife/claim/routetonextlevel',requestForReferToSeniorUnderWriterClaim).success(function (response, status, headers, config) {
                    window.location.href ="/pla/grouplife/claim/openapprovalclaim";

                }).error(function (response, status, headers, config) {
                });
            }


            $scope.createClaimSettlement=function(){
                var requestForClaimSettlement={
                    "claimSettlementDetails":$scope.claimSettlementDetails,
                    "claimId":$scope.claimId
                }
                console.log('****** ClaimSettlement JSON ***');
                console.log(JSON.stringify(requestForClaimSettlement));

                $http.post('/pla/grouplife/claim/createclaimsettlement',requestForClaimSettlement).success(function (response, status, headers, config) {

                }).error(function (response, status, headers, config) {
                })
            }


           /* $scope.showDisabilityDate= function ($event){
            $event.preventDefault();
            $event.stopPropagation();
           $scope.openDisabilityDate=true;
       };*/
       $scope.showDob=function($event){
       $event.preventDefault();
       $event.stopPropagation();
       $event.openDob=true;
       };
        $scope.ShowDeathDate=function($event){
              $event.preventDefault();
              $event.stopPropagation();
              $event.openDeathDate=true;
              };
               $scope.ShowdateOfConsultation=function($event){
                            $event.preventDefault();
                            $event.stopPropagation();
                            $event.opendateOfConsultation=true;
                            };
                             $scope.ShowdateOfAccident=function($event){
                                                        $event.preventDefault();
                                                        $event.stopPropagation();
                                                        $event.opendateOfAccident=true;
                                                        };

       $scope.showAssureDob= function ($event){
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openAssureDob=true;
                 };

       $scope.showDignosisDate= function ($event){
            $event.preventDefault();
            $event.stopPropagation();
           $scope.openDignosisDate=true;
       };
      $scope.showFirstConsultanceDate= function ($event){
            $event.preventDefault();
            $event.stopPropagation();
           $scope.openFirstConsultanceDate=true;
       };

     $scope.showFromDate= function ($event){
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openFromDate=true;
        };
 /* $scope.showDisabilityDate= function ($event){
             $event.preventDefault();
             $event.stopPropagation();
             $scope.openDisabilityDate=true;
         };*/
  $scope.showDignosisDate= function ($event){
             $event.preventDefault();
             $event.stopPropagation();
             $scope.openDignosisDate=true;
         };
  $scope.showFirstConsultanceDate= function ($event){
              $event.preventDefault();
              $event.stopPropagation();
              $scope.openFirstConsultanceDate=true;
          };
   $scope.showFromDate= function ($event){
               $event.preventDefault();
               $event.stopPropagation();
               $scope.openFromDate=true;
 };
  $scope.retriveAssuredDetails=function(cid)
              {
              console.log(cid)
              angular.forEach($scope.items, function(eachData){
                    if(angular.equals(eachData.assuredNumber,cid)) {
                $scope.schedule.assuredField=eachData.assuredFirst;
                     $scope.schedule.assuredField=eachData.assuredFirst;
                                    $('#claimintimationModal').modal('hide');

                    }
              });

              }


                          $scope.showMe=function(){
                                $scope.show=true;
                          }
                         $scope.hideMe=function(){
                          $scope.show=false;
                          }



     $scope.showModal=function(){
           $('#claimintimationModal').modal('show');
         };

    }]);


 App.config(["$routeProvider", function ($routeProvider) {
                      $routeProvider.when('/', {
                            templateUrl: 'claimintimation.html',
                            controller: 'ClaimIntimationController',
                           resolve: {
                               provinces: ['$q', '$http', function ($q, $http) {
                                   var deferred = $q.defer();
                                   $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                                       deferred.resolve(response)
                                   }).error(function (response, status, headers, config) {
                                       deferred.reject();
                                   });
                                   return deferred.promise;
                               }],
                               bankDetails: ['$q', '$http', function ($q, $http) {
                                   var deferred = $q.defer();
                                   $http.get('/pla/grouplife/claim/getAllBankNames').success(function (response, status, headers, config) {
                                       deferred.resolve(response)
                                   }).error(function (response, status, headers, config) {
                                       deferred.reject();
                                   });
                                   return deferred.promise;
                               }],
                               occupations: ['$q', '$http', function ($q, $http) {
                                   var deferred = $q.defer();
                                   $http.get('/pla/individuallife/proposal/getAllOccupation').success(function (response, status, headers, config) {
                                       deferred.resolve(response)
                                   }).error(function (response, status, headers, config) {
                                       deferred.reject();
                                   });
                                   return deferred.promise;
                               }]

                                   }
                    }

       )}]);

