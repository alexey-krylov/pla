
var  app = angular.module('CashLessClaimService', ['common', 'ngRoute','ngMessages', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover',
        'directives', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices','ui.bootstrap.modal','angularFileUpload', 'angucomplete-alt'])
    .config(['datepickerPopupConfig', function (datepickerPopupConfig) {
        datepickerPopupConfig.datepickerPopup = 'MM/dd/yyyy';
        datepickerPopupConfig.currentText = 'Today';
        datepickerPopupConfig.clearText = 'Clear';
        datepickerPopupConfig.closeText = 'Done';
        datepickerPopupConfig.closeOnDateSelection = true;
    }])
    .config(["$routeProvider", function ($routeProvider) {
        $routeProvider.when('/', {
                templateUrl: 'ghcashlessclaimservicemismatch.html',
                controller: 'CashLessClaimServiceCtrl',
                resolve:{
                    createUpdateDto: ['$q', '$http', 'getQueryParameter', function ($q, $http, getQueryParameter) {
                        var deferred = $q.defer();
                        var groupHealthCashlessClaimId = getQueryParameter('groupHealthCashlessClaimId');
                        $http.get('/pla/grouphealth/claim/cashless/claim/getgrouphealthcashlessclaimdtobygrouphealthcashlessclaimid?groupHealthCashlessClaimId='+groupHealthCashlessClaimId).success(function (response, status, headers, config) {
                            deferred.resolve(response);

                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
//

                        return deferred.promise;
                    }],
                    documentList: ['$q', '$http','getQueryParameter', function ($q, $http, getQueryParameter) {
                        var deferred = $q.defer();
                        var clientId = getQueryParameter('clientId');
                        var groupHealthCashlessClaimId = getQueryParameter('groupHealthCashlessClaimId');
                        if (clientId && !_.isEmpty(clientId)) {
                            var deferred = $q.defer();
                            $http.get("/pla/grouphealth/claim/cashless/claim/getmandatorydocuments/" + clientId + "/"+groupHealthCashlessClaimId).success(function (response, status, headers, config) {
                                deferred.resolve(response)
                            }).error(function (response, status, headers, config) {
                                deferred.reject();
                            });
                            return deferred.promise;
                        } else {
                            return false;
                        }
                    }],
                    groupHealthCashlessClaimId : ['$q', '$http','getQueryParameter', function ($q, $http, getQueryParameter) {
                        var deferred = $q.defer();
                        var groupHealthCashlessClaimId = getQueryParameter('groupHealthCashlessClaimId');
                        deferred.resolve(groupHealthCashlessClaimId);
                        return deferred.promise;
                    }],
                    clientId : ['$q', '$http','getQueryParameter', function ($q, $http, getQueryParameter) {
                        var deferred = $q.defer();
                        var clientId = getQueryParameter('clientId');
                        deferred.resolve(clientId);
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
                    }]
                }

            }
        )}])

    .controller('CashLessClaimServiceCtrl', ['$scope', '$http','createUpdateDto','getQueryParameter','$window','documentList','$upload','clientId','groupHealthCashlessClaimId','bankDetails',
        function ($scope, $http, createUpdateDto, getQueryParameter, $window, documentList, $upload, clientId,groupHealthCashlessClaimId,bankDetails) {
            $scope.createUpdateDto = createUpdateDto;
            $scope.drugServicesDtoList = $scope.createUpdateDto.groupHealthCashlessClaimDrugServices;
            $scope.treatmentDiagnosis = {};
            $scope.diagnosisTreatmentDtoToUpdate = {};
            $scope.index = null;
            $scope.treatmentDiagnosisIndex = null;
            $scope.isEditDrugTriggered = false;
            $scope.isEditDiagnosisTriggered = false;
            $scope.createUpdateCommand = {};
            $scope.groupHealthCashlessClaimDiagnosisTreatmentDetails = {};
            $scope.createUpdateCommand.groupHealthCashlessClaimHCPDetail = $scope.createUpdateDto.groupHealthCashlessClaimHCPDetail;
            $scope.createUpdateCommand.groupHealthCashlessClaimHCPDetail = {};
            $scope.createUpdateCommand.groupHealthCashlessClaimPolicyDetail = {};
            $scope.createUpdateCommand.groupHealthCashlessClaimDiagnosisTreatmentDetails = [];
            $scope.createUpdateCommand.groupHealthCashlessClaimIllnessDetail = {};
            $scope.createUpdateCommand.groupHealthCashlessClaimDrugServices = [];
            $scope.documentList = documentList;
            $scope.additionalDocumentList = [{}];
            $scope.disableSubmit = false;
            $scope.selectedItem = 1;
            $scope.comment = {};
            $scope.fileSaved = null;
            $scope.isViewMode = false;
            $scope.stepsSaved = [];
            $scope.bankDetailsResponse=[];
            $scope.bankDetailsResponse=bankDetails;
            $scope.bankBranchDetails=[];
            $scope.bankDetails={};

            if ($scope.createUpdateDto.submitted) {
                $scope.isViewMode = true;
            }

            $scope.$watch('createUpdateDto.statusName', function(newVal, oldVal){
                if (newVal !== 'SERVICE_MISMATCHED') {
                    $scope.isViewMode = true;
                }
            });

            $scope.$watch('documentList', function (newCollection, oldCollection) {
                $scope.disableSubmit = $scope.shouldSubmitBeDisabled(newCollection);
            });

            $scope.shouldSubmitBeDisabled = function (documentList) {
                for (var i = 0; i < documentList.length; i++) {
                    var document = documentList[i];
                    if (document.fileName == null || document.content == null) {
                        return true;
                    }
                }
                return false;
            };

            $scope.tratementdignosisnextbuttonfalse= function(){
                $scope.provisionaldignosisdiv = true;
                $scope.stepsSaved["4"] = true;
            };

            $scope.viewTreatmentDiagnosis = function (treatmentDiagnosis, treatmentDiagnosisIndex) {
                $scope.tratementdignosisnextbuttonfalse();
                $scope.isEditDiagnosisTriggered = true;
                $scope.treatmentDiagnosisIndex = treatmentDiagnosisIndex;
                $scope.groupHealthCashlessClaimDiagnosisTreatmentDetails = treatmentDiagnosis;
                $scope.drugServicesDto = {};
                $scope.diagnosisTreatmentDtosUpdate = {};
                $scope.createUpdateDto.drugServicesDtosSave = [];
                $scope.provisionaldignosisdiv=true;
            };

            $scope.updateTreatmentAndDiagnosis = function (groupHealthCashlessClaimDiagnosisTreatmentDetails) {
                if ($scope.isEditDiagnosisTriggered) {
                    console.log("update insd btn"+JSON.stringify(groupHealthCashlessClaimDiagnosisTreatmentDetails));
                    $scope.createUpdateDto.groupHealthCashlessClaimDiagnosisTreatmentDetails[$scope.treatmentDiagnosisIndex] = groupHealthCashlessClaimDiagnosisTreatmentDetails;
                    $scope.saveCashlessClaimRequest();
                    $scope.isEditDiagnosisTriggered = false;
                    $scope.provisionaldignosisdiv=false;
                } else {
                    $scope.createUpdateDto.groupHealthCashlessClaimDiagnosisTreatmentDetails.push(groupHealthCashlessClaimDiagnosisTreatmentDetails);
                    console.log("new btn crt insd btn####"+JSON.stringify($scope.createUpdateDto));
                }
                $scope.provisionaldignosisdiv = false;
                $scope.stepsSaved["4"] = false;
            };

            $scope.saveCashlessClaimRequest = function () {
                console.log(JSON.stringify($scope.createUpdateDto));
                $http({
                    url: '/pla/grouphealth/claim/cashless/claim/underwriter/update',
                    method: 'POST',
                    data: $scope.createUpdateDto
                }).success(function (response) {
                    $http.get('/pla/grouphealth/claim/cashless/claim/getgrouphealthcashlessclaimdtobygrouphealthcashlessclaimid?groupHealthCashlessClaimId='+groupHealthCashlessClaimId)
                        .success(function (response) {
                            $scope.createUpdateDto = response;
                        }).error(function (response, status, headers, config) {
                    });
                }).error();
            };

            $scope.activenextbuttonforprovisional = function(){
                $scope.groupHealthCashlessClaimDiagnosisTreatmentDetails = {};
                $scope.groupHealthCashlessClaimDiagnosisTreatmentDetails.dateOfConsultation = $scope.createUpdateDto.groupHealthCashlessClaimDiagnosisTreatmentDetails[0].dateOfConsultation;
                $scope.provisionaldignosisdiv = true;
                $scope.stepsSaved["4"] = true;
            };

            $scope.create = function(){
                $scope.showservicedrugdiv = true;
                $scope.diagnosisTreatmentDtoToUpdate='';
                $scope.stepsSaved["1"] = true;
            };

            $scope.updateDrugServicesDto = function (drugServicesDto, index) {
                $scope.create();
                $scope.index = index;
                $scope.isEditDrugTriggered = true;
                $scope.diagnosisTreatmentDtoToUpdate = drugServicesDto;
                $scope.showservicedrugdiv=true;
            };

            $scope.saveDiagnosisTreatmentDto = function (diagnosisTreatmentDtoToUpdate) {
                if ($scope.isEditDrugTriggered) {
                    $scope.createUpdateDto.groupHealthCashlessClaimDrugServices[$scope.index] = diagnosisTreatmentDtoToUpdate;
                    $scope.saveCashlessClaimRequest();
                    $scope.isEditDrugTriggered = false;

                } else {
                    diagnosisTreatmentDtoToUpdate.status = 'PROCESS';
                    $scope.createUpdateDto.groupHealthCashlessClaimDrugServices.push(diagnosisTreatmentDtoToUpdate);
                    $scope.saveCashlessClaimRequest();
                }
                $scope.showservicedrugdiv = false;
                $scope.stepsSaved["1"] = false;
            };
//cancel button
            $scope.activenextbuttonfordrugservice= function(){
                $scope.showservicedrugdiv = false;
                $scope.stepsSaved["1"] = false;
            };

            $scope.nextBtnActiveOnCancelButton= function(){
                $scope.provisionaldignosisdiv = false;
                $scope.stepsSaved["4"] = false;
            };
            $scope.deleteTreatmentDiagnosis = function (index) {
                $scope.createUpdateDto.groupHealthCashlessClaimDrugServices.splice(index, 1);
                $scope.saveCashlessClaimRequest();

            };

            var saveStep = function () {
                $scope.stepsSaved[$scope.selectedItem] = true;
            };

            $scope.isSaveDisabled = function (formName) {
                return formName.$invalid;
            };

            $scope.back = function () {
                //window.location.reload();
                window.location.href = '/pla/grouphealth/claim/cashless/claim/getallservicemismatchedgrouphealthcashlessclaims';
            };

            $scope.isBrowseDisable = function (document) {
                if (document.fileName == null && document.submitted) {
                    return true;
                }
                else {
                    return false;
                }
            };

            $scope.uploadDocumentFiles = function () {
                for (var i = 0; i < $scope.documentList.length; i++) {
                    var document = $scope.documentList[i];
                    var files = document.documentAttached;
                    //console.log(files);
                    if (files) {
                        $upload.upload({
                            url: '/pla/grouphealth/claim/cashless/claim/uploadmandatorydocument',
                            file: files,
                            fields: {
                                documentId: document.documentId,
                                groupHealthCashlessClaimId: $scope.createUpdateDto.groupHealthCashlessClaimId,
                                mandatory: true
                            },
                            method: 'POST'
                        }).progress(function (evt) {
                            //console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
                        }).success(function (data, status, headers, config) {
                            $http.get("/pla/grouphealth/claim/cashless/claim/getmandatorydocuments/" + clientId + "/" + groupHealthCashlessClaimId).success(function (response, status, headers, config) {
                                $scope.documentList = response;
                            });
                        });

                    }

                }
            };

            $scope.uploadAdditionalDocument = function () {
                for (var i = 0; i < $scope.additionalDocumentList.length; i++) {
                    var document = $scope.additionalDocumentList[i];
                    var files = document.documentAttached;
                    if (files) {
                        $upload.upload({
                            url: '/pla/grouphealth/claim/cashless/claim/uploadmandatorydocument',
                            file: files,
                            fields: {
                                documentId: document.documentId,
                                groupHealthCashlessClaimId: $scope.createUpdateDto.groupHealthCashlessClaimId,
                                mandatory: false
                            },
                            method: 'POST'
                        }).progress(function (evt) {

                        }).success(function (data, status, headers, config) {
                            $http.get("/pla/grouphealth/claim/cashless/claim/getadditionaldocuments/" + groupHealthCashlessClaimId).success(function (response, status, headers, config) {
                                $scope.additionalDocumentList = response;
                            });
                        });
                    }

                }
            };
            $scope.removeAdditionalDocumentCommand = {};
            $scope.removeAdditionalDocument = function (index, gridFsDocId) {
                $scope.removeAdditionalDocumentCommand.gridFsDocId = gridFsDocId;
                $scope.removeAdditionalDocumentCommand.groupHealthCashlessClaimId = $scope.createUpdateDto.groupHealthCashlessClaimId;
                if (gridFsDocId) {
                    $http({
                        url: '/pla/grouphealth/claim/cashless/claim/removeadditionalDocument',
                        method: 'POST',
                        data: $scope.removeAdditionalDocumentCommand
                    }).success(function () {
                        $scope.additionalDocumentList.splice(index, 1);
                        $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
                    }).error();
                } else {
                    $scope.additionalDocumentList.splice(index, 1);
                    $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();

                }
            };

            $scope.isUploadEnabledForAdditionalDocument = function () {
                var enableAdditionalUploadButton = ($scope.additionalDocumentList != null);
                for (var i = 0; i < $scope.additionalDocumentList.length; i++) {
                    var document = $scope.additionalDocumentList[i];
                    var files = document.documentAttached;
                    if (!(files || document.content)) {
                        enableAdditionalUploadButton = false;
                        break;
                    }
                }
                return enableAdditionalUploadButton;
            };

            $scope.addAdditionalDocument = function () {
                $scope.additionalDocumentList.unshift({});
            };

            $scope.callAdditionalDoc = function (file) {
                if (file[0]) {
                    $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
                }
            };



            $scope.launchClaimDate = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datepicker = {'opened': true};

            };

            $scope.changeClaimDate = function(iem){
                $scope.createUpdateDto.claimIntimationDate = formatDate(iem);

                $scope.createUpdateDto.claimIntimationDate=$scope.createUpdateDto.claimIntimationDate;
                //console.log("qwqe############"+$scope.createUpdateDto.claimIntimationDate );
            };

            $scope.launchPreAuthDate = function ($event){
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datepickerpreauth = {'opened': true};
            };

            $scope.changePreAuthDate=function(preAuthDate) {
                $scope.createUpdateDto.preAuthorizationDate = formatDate(preAuthDate);
                //console.log("qwqe############"+$scope.createUpdateDto.preAuthorizationDate );
            };

            $scope.launchProbableDate = function ($event){
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datepickerprobable = {'opened': true};
            };

            $scope.changeProbableDate=function(ProbableDate) {
                $scope.groupHealthCashlessClaimDiagnosisTreatmentDetails.pregnancyDateOfDelivery = formatDate(ProbableDate);
                //console.log("qwqe############"+$scope.groupHealthCashlessClaimDiagnosisTreatmentDetails.pregnancyDateOfDelivery );
            };

            $scope.launchFirstConsultanceDate = function ($event){
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datepickerconsultance = {'opened': true};
            };

            $scope.changelaunchFirstConsultanceDate=function(consultationDate) {
                $scope.groupHealthCashlessClaimDiagnosisTreatmentDetails.dateOfConsultation = formatDate(consultationDate);
            };

            $scope.launchProbAdmissionDate = function ($event){
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datepickeradmission = {'opened': true};
            };
            $scope.launchdateOfDischarge =   function ($event){
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datepickerdateOfDischarge = {'opened': true};
            };
            $scope.changeProbAdmissionDate=function(probAdmissionDate) {
                $scope.groupHealthCashlessClaimDiagnosisTreatmentDetails.dateOfAdmission = formatDate(probAdmissionDate);
                //console.log("qwqe############"+$scope.groupHealthCashlessClaimDiagnosisTreatmentDetails.dateOfAdmission);
            };

            $scope.changedateOfDischarge=function(dateOfDischarge) {
                $scope.groupHealthCashlessClaimDiagnosisTreatmentDetails.dateOfDischarge = formatDate(dateOfDischarge);
                //console.log("qwqe############"+$scope.groupHealthCashlessClaimDiagnosisTreatmentDetails.dateOfAdmission);
            };

            $scope.hcpServiceDetails = [];

            $scope.getHCPServiceDetails = function(){
                $http.get("/pla/grouphealth/claim/cashless/claim/getallrelevantservices/" + groupHealthCashlessClaimId).success(function (data, status, headers, config) {
                    $scope.hcpServiceDetails = data;
                }).error(function (response, status, headers, config) {
                });
            };


            var mode = getQueryParameter("mode");
            if (mode == 'view'){
                $scope.isViewMode=true;
            }
            $scope.disableHtn = true;
            //$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htndetail=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htndetails;
            $scope.$watch(
                function(){
                    return {htn: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htn, htnDetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htndetails};
                },
                function(newVal, oldVal) {
                    if(newVal.htn === 'No'){
                        $scope.disableHtn = true;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htndetails='';

                    }
                    else {
                        //$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htndetails=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htndetail;
                        $scope.disableHtn = false;
                    }
                },
                true
            );

            $scope.disableIhd = true;
            $scope.$watch(
                function(){
                    return {idh: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.idhHOD, htnDetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.ihdhoddetails};
                },
                function(newVal, oldVal) {
                    if(newVal.idh === 'No'){
                        $scope.disableIhd = true;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.ihdhoddetails='';
                    }
                    else {
                        $scope.disableIhd = false;
                    }
                },
                true
            );

            $scope.disdibetes= true;
            $scope.$watch(
                function(){
                    return {Dibetes: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.diabetes, diabetesDetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.diabetesDetails};
                },
                function(newVal, oldVal) {
                    if(newVal.Dibetes === 'No'){
                        $scope.disdibetes = true;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.diabetesDetails='';
                    }
                    else {
                        $scope.disdibetes = false;
                    }
                },
                true
            );

            $scope.disAsthma= true;
            $scope.$watch(
                function(){
                    return {Asthma: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.asthmaCOPDTB, asthmaCOPDTBDetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.asthmaCOPDTBDetails};
                },
                function(newVal, oldVal) {
                    if(newVal.Asthma === 'No'){
                        $scope.disAsthma = true;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.asthmaCOPDTBDetails='';
                    }
                    else {
                        $scope.disAsthma = false;
                    }
                },
                true
            );

            $scope.disStd= true;
            $scope.$watch(
                function(){
                    return {Std: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.stdhivaids, stdhivaidsdetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.stdhivaidsdetails};
                },
                function(newVal, oldVal) {
                    if(newVal.Std === 'No'){
                        $scope.disStd = true;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.stdhivaidsdetails='';
                    }
                    else {
                        $scope.disStd = false;
                    }
                },
                true
            );

            $scope.disArthiritis= true;
            $scope.$watch(
                function(){
                    return {Arthiritis: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.arthritis, stdhivaidsdetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.arthritisDetails};
                },
                function(newVal, oldVal) {
                    if(newVal.Arthiritis === 'No'){
                        $scope.disArthiritis = true;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.arthritisDetails='';
                    }
                    else {
                        $scope.disArthiritis = false;
                    }
                },
                true
            );

            $scope.disCancer= true;
            $scope.$watch(
                function(){
                    return {Cancer: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.cancerTumorCyst, cancerTumorCystDetail: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.cancerTumorCystDetails};
                },
                function(newVal, oldVal) {
                    if(newVal.Cancer === 'No'){
                        $scope.disCancer = true;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.cancerTumorCystDetails='';
                    }
                    else {
                        $scope.disCancer = false;
                    }
                },
                true
            );

            $scope.dispshyciatric= true;
            $scope.$watch(
                function(){
                    return {pshyciatric: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.psychiatricCondition, psychiatricConditionDetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.psychiatricConditionDetails};
                },
                function(newVal, oldVal) {
                    if(newVal.pshyciatric === 'No'){
                        $scope.dispshyciatric = true;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.psychiatricConditionDetails='';
                    }
                    else {
                        $scope.dispshyciatric = false;
                    }
                },
                true
            );

            $scope.disAlcohol= true;
            $scope.$watch(
                function(){
                    return {Alcohol: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.alcoholDrugAbuse, alcoholDrugAbuseDetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.alcoholDrugAbuseDetails};
                },
                function(newVal, oldVal) {
                    if(newVal.Alcohol === 'No'){
                        $scope.disAlcohol = true;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.alcoholDrugAbuseDetails='';
                    }
                    else {
                        $scope.disAlcohol = false;
                    }
                },
                true
            );

            $scope.returnGHCashlessClaim = function () {
                if (!$scope.comment.comments) {
                    $scope.message = "Comment is mandatory to return Cashless Claim.";
                    $scope.toggleModal();
                } else{
                    $scope.returnCashlessClaim();
                }
            };

            $scope.rejectGHCashlessClaim = function () {
                if (!$scope.comment.comments) {
                    $scope.message = "Comment is mandatory to reject Cashless Claim.";
                    $scope.toggleModal();
                } else {
                    $http.get('/pla/grouphealth/claim/cashless/claim/underwriter/checkifgrouphealthcashlessclaimrejectionemailsent/' + groupHealthCashlessClaimId)
                        .success(function (response) {
                            if (response.data === true) {
                                $scope.rejectPreAuthorization();
                            }
                            else{
                                var win = window.open('/pla/grouphealth/claim/cashless/claim/underwriter/getgrouphealthcashlessclaimrejectionletter/' + groupHealthCashlessClaimId, "_blank", "toolbar=no,resizable=no," +
                                    "scrollable=no,menubar=no,personalbar=no,dependent=yes,dialog=yes,split=no,titlebar=no,resizable=no,location=no,left=100px");
                                var timer = setInterval(function () {
                                    if (win.closed) {
                                        clearInterval(timer);
                                        $http.get('/pla/grouphealth/claim/cashless/claim/underwriter/checkifgrouphealthcashlessclaimrejectionemailsent/' + groupHealthCashlessClaimId)
                                            .success(function (response) {
                                                if (response.data === true) {
                                                    $scope.rejectPreAuthorization();
                                                } else{
                                                    $scope.message = " Please email the rejection letter.";
                                                    $scope.toggleModal();
                                                }
                                            }).error(function (response) {
                                        });
                                    }
                                }, 500);
                            }
                        }).error()
                }
            };

            $scope.approveGHCashlessClaim = function () {
                $scope.createUpdateDto.submitEventFired = true;
                $.when($scope.constructCommentDetails()).done(function () {
                    $http({
                        url: '/pla/grouphealth/claim/cashless/claim/servicemismatch/approve',
                        method: 'POST',
                        data: $scope.createUpdateDto
                    }).success(function (response, status, headers, config) {
                        if (status === 200) {
                            $http.get('/pla/grouphealth/claim/cashless/claim/getgrouphealthcashlessclaimdtobygrouphealthcashlessclaimid?groupHealthCashlessClaimId=' + groupHealthCashlessClaimId)
                                .success(function (response, status, headers, config) {
                                    $scope.createUpdateDto = response;
                                    if (status == "200") {
                                        setTimeout(function () {
                                            window.location.href = '/pla/grouphealth/claim/cashless/claim/getallservicemismatchedgrouphealthcashlessclaims';
                                        }, 3000);
                                    }
                                }).error(function (response, status, headers, config) {
                            });
                        }
                    }).error(
                        function (status) {
                            //console.log(status);
                        }
                    );
                });
            };

            $scope.myModal = false;
            $scope.toggleModal = function(){
                $("#myModal").modal('show');
            };
            $scope.approveModal = false;
            $scope.approvepopupModal = function(){
                $("#approveModal").modal('show');
            };

            $scope.constructCommentDetails = function() {
                if ($scope.comment) {
                    if ($scope.createUpdateDto.commentDetails) {
                        $scope.createUpdateDto.commentDetails.push($scope.comment);
                    } else {
                        $scope.createUpdateDto.commentDetails = new Array($scope.comment);
                    }
                }
            };

            $scope.returnCashlessClaim = function(){
                $.when($scope.constructCommentDetails()).done(function () {
                    $http({
                        url: '/pla/grouphealth/claim/cashless/claim/return',
                        method: 'POST',
                        data: $scope.createUpdateDto
                    }).success(function (response, status, headers, config) {
                        if (status === 200) {
                            $http.get('/pla/grouphealth/claim/cashless/claim/getgrouphealthcashlessclaimdtobygrouphealthcashlessclaimid?groupHealthCashlessClaimId=' + groupHealthCashlessClaimId)
                                .success(function (response, status, headers, config) {
                                    $scope.createUpdateDto = response;
                                    if (status == 200) {
                                        setTimeout(function () {
                                            window.location.href = '/pla/grouphealth/claim/cashless/claim/getallservicemismatchedgrouphealthcashlessclaims';
                                        }, 3000);
                                    }
                                }).error(function (response, status, headers, config) {
                            });
                        }
                    }).error(
                        function (status) {
                            //console.log(status);
                        }
                    );
                });
            };

            $scope.rejectPreAuthorization = function(){
                $.when($scope.constructCommentDetails()).done(function () {
                    $http({
                        url: '/pla/grouphealth/claim/cashless/claim/reject',
                        method: 'POST',
                        data: $scope.createUpdateDto
                    }).success(function (response, status, headers, config) {
                        if (status === 200) {
                            $http.get('/pla/grouphealth/claim/cashless/claim/getgrouphealthcashlessclaimdtobygrouphealthcashlessclaimid?groupHealthCashlessClaimId=' + groupHealthCashlessClaimId)
                                .success(function (response, status, headers, config) {
                                    $scope.createUpdateDto = response;
                                    if (status == 200) {
                                        setTimeout(function () {
                                            window.location.href = '/pla/grouphealth/claim/cashless/claim/getallservicemismatchedgrouphealthcashlessclaims';
                                        }, 3000);
                                    }
                                }).error(function (response, status, headers, config) {
                            });
                        }
                    }).error(
                        function (status) {
                            //console.log(status);
                        }
                    );
                });
            };

            $scope.rejectionEmailSent = false;
            $scope.checkRejectionEmailSent = function(){
                $http.get('/pla/grouphealth/claim/cashless/claim/underwriter/checkifgrouphealthcashlessclaimrejectionemailsent/' + groupHealthCashlessClaimId)
                    .success(function(response) {
                        if(response.data === true){
                            $scope.rejectionEmailSent = true;
                        }
                    }).error(function(response){
                    $scope.rejectionEmailSent = false;
                });
            };

            $scope.$watch('createUpdateDto.groupHealthCashlessClaimBankDetailDto.bankName', function (newvalue, oldvalue) {
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
        }]);

function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
}
