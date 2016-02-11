
var  app = angular.module('CashLessClaim', ['common', 'ngRoute','ngMessages', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover',
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
                templateUrl: 'ghcashlessclaim.html',
                controller: 'cashLessClaimCtrl',
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
                    }]
                }

            }
        )}])

    .controller('cashLessClaimCtrl', ['$scope', '$http','createUpdateDto','getQueryParameter','$window','documentList','$upload','clientId','groupHealthCashlessClaimId',
        function ($scope, $http, createUpdateDto, getQueryParameter, $window, documentList, $upload, clientId,groupHealthCashlessClaimId) {
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
            console.log(JSON.stringify($scope.createUpdateDto));
            $scope.additionalDocumentList = [{}];
            $scope.disableSubmit = false;
            $scope.documentmaster = [];
            $scope.selectedItem = 1;
            $scope.comment = {};
            $scope.fileSaved = null;
            $scope.isViewMode = false;
            $scope.stepsSaved = [];

            if ($scope.createUpdateDto.submitted) {
                $scope.isViewMode = true;
            }

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

            $http.get("/pla/grouphealth/claim/cashless/claim/getadditionaldocuments/" + groupHealthCashlessClaimId).success(function (data, status, headers, config) {
                $scope.additionalDocumentList = data;
                $scope.checkDocumentAttached = $scope.additionalDocumentList != null;
            }).error(function (response, status, headers, config) {
            });

            $scope.tratementdignosisnextbuttonfalse= function(){
                $scope.provisionaldignosisdiv = true;
                $scope.stepsSaved["3"] = true;
            };

            $scope.viewTreatmentDiagnosis = function (treatmentDiagnosis, treatmentDiagnosisIndex) {
                console.log($scope.treatmentDiagnosis);
                console.log($scope.treatmentDiagnosisIndex);
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
                    groupHealthCashlessClaimDiagnosisTreatmentDetails
                    $scope.createUpdateDto.groupHealthCashlessClaimDiagnosisTreatmentDetails.push(groupHealthCashlessClaimDiagnosisTreatmentDetails);
                    console.log("new btn crt insd btn####"+JSON.stringify($scope.createUpdateDto));

                }
                $scope.provisionaldignosisdiv = false;
                $scope.stepsSaved["3"] = false;
            };
            $scope.saveCashlessClaimRequest = function () {
                console.log(JSON.stringify($scope.createUpdateDto));
                $http({
                    url: '/pla/grouphealth/claim/cashless/claim/updategrouphealthcashlessclaim',
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
            $scope.create= function(){
                $scope.provisionaldignosisdiv = true;
                $scope.stepsSaved["3"] = true;
            };
            $scope.activenextbuttonforprovisional = function(){
                $scope.groupHealthCashlessClaimDiagnosisTreatmentDetails = {};
                $scope.groupHealthCashlessClaimDiagnosisTreatmentDetails.dateOfConsultation = $scope.createUpdateDto.groupHealthCashlessClaimDiagnosisTreatmentDetails[0].dateOfConsultation;
                $scope.provisionaldignosisdiv = true;
                $scope.stepsSaved["3"] = true;
            };
            $scope.activenextbuttonforprovisionalclickon= function(){
                $scope.provisionaldignosisdiv = true;
                $scope.stepsSaved["3"] = true;
            };

//for add sevice drug availed section start
            //create function detect Add Service/Drug button
            $scope.create = function(){
                $scope.showservicedrugdiv = true;
                $scope.diagnosisTreatmentDtoToUpdate='';
                $scope.stepsSaved["5"] = true;
            };
            //updateDrugServicesDto function detect update button
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
                $scope.stepsSaved["5"] = false;
            };
//cancel button
            $scope.activenextbuttonfordrugservice= function(){
                $scope.showservicedrugdiv = false;
                $scope.stepsSaved["5"] = false;
            };

            $scope.nextBtnActiveOnCancelButton= function(){
                $scope.provisionaldignosisdiv = false;
                $scope.stepsSaved["3"] = false;
            };
            $scope.deleteTreatmentDiagnosis = function (index) {
                $scope.createUpdateDto.groupHealthCashlessClaimDrugServices.splice(index, 1);
                $scope.saveCashlessClaimRequest();

            };

            /*Holds the indicator for steps in which save button is clicked*/

//end service drug availed section

            var saveStep = function () {
                $scope.stepsSaved[$scope.selectedItem] = true;
            };

            $scope.isSaveDisabled = function (formName) {
                return formName.$invalid;
            };



            $scope.submitPreAuthorizationRequest = function () {
                $scope.createUpdateDto.submitEventFired = true;
                $http({
                    url: '/pla/grouphealth/claim/cashless/claim/submitgrouphealthcashlessclaim',
                    method: 'POST',
                    data: $scope.createUpdateDto
                }).success(function (data) {
                    if (data.status == "200") {
                        setTimeout(function() {
                            window.location.reload();
                        }, 2000);
                    }

                }).error();

            };

            $scope.back = function () {
                //window.location.reload();
                window.location.href = '/pla/grouphealth/claim/cashless/claim/getcashlessclaimfordefaultlist';
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

            $scope.changeProbAdmissionDate=function(probAdmissionDate) {
                $scope.groupHealthCashlessClaimDiagnosisTreatmentDetails.dateOfAdmission = formatDate(probAdmissionDate);
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
            $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htndetail=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htndetails;

            $scope.$watch(
                function(){
                    return {htn: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htn, htnDetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htndetails};
                },
                function(newVal, oldVal) {
                    if(newVal.htn === 'No'){
                        $scope.isrequire = false;
                        $scope.disableHtn = true;
                        $scope.stepsSaved["4"] = false;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htndetails='';

                    }
                    else {
                        if(!$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htndetails){
                            $scope.isrequire = true;
                            $scope.stepsSaved["4"] = true;
                        } else {
                            $scope.stepsSaved["4"] = false;
                        }
                        //$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htndetails=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.htndetail;

                        $scope.disableHtn = false;
                    }
                },
                true
            );

            $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.ihdhoddetail=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.ihdhoddetails;
            $scope.disableIhd = true;
            $scope.$watch(
                function(){
                    return {idh: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.idhHOD, htnDetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.ihdhoddetail};
                },
                function(newVal, oldVal) {
                    if(newVal.idh === 'No'){
                        $scope.isrequireIhd = false;
                        $scope.disableIhd = true;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.ihdhoddetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.ihdhoddetails){
                            $scope.isrequireIhd = true;}
                        $scope.disableIhd = false;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.ihdhoddetails=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.ihdhoddetail;

                    }
                },
                true
            );
            $scope.disdibetes= true;
            $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.diabetesDetail=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.diabetesDetails;

            $scope.$watch(
                function(){
                    return {Dibetes: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.diabetes, diabetesDetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.diabetesDetail};
                },
                function(newVal, oldVal) {
                    if(newVal.Dibetes === 'No'){
                        $scope.isrequireDibetes = false;
                        $scope.disdibetes = true;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.diabetesDetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.diabetesDetails){
                            $scope.isrequireDibetes = true;}
                        $scope.disdibetes = false;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.diabetesDetails=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.diabetesDetail;

                    }
                },
                true
            );
            $scope.disAsthma= true;
            $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.asthmaCOPDTBDetail=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.asthmaCOPDTBDetails;

            $scope.$watch(
                function(){
                    return {Asthma: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.asthmaCOPDTB, asthmaCOPDTBDetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.asthmaCOPDTBDetail};
                },
                function(newVal, oldVal) {
                    if(newVal.Asthma === 'No'){
                        $scope.isrequireAsthma = false;
                        $scope.disAsthma = true;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.asthmaCOPDTBDetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.asthmaCOPDTBDetails){
                            $scope.isrequireAsthma = true;}
                        $scope.disAsthma = false;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.asthmaCOPDTBDetails=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.asthmaCOPDTBDetail;

                    }
                },
                true
            );
            $scope.disStd= true;
            $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.stdhivaidsdetail=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.stdhivaidsdetails;

            $scope.$watch(
                function(){
                    return {Std: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.stdhivaids, stdhivaidsdetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.stdhivaidsdetail};
                },
                function(newVal, oldVal) {
                    if(newVal.Std === 'No'){
                        $scope.isrequireStd = false;
                        $scope.disStd = true;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.stdhivaidsdetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.stdhivaidsdetails){
                            $scope.isrequireStd = true;}
                        $scope.disStd = false;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.stdhivaidsdetails=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.stdhivaidsdetail;

                    }
                },
                true
            );
            $scope.disArthiritis= true;
            $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.arthritisDetail=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.arthritisDetails;

            $scope.$watch(
                function(){
                    return {Arthiritis: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.arthritis, stdhivaidsdetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.arthritisDetail};
                },
                function(newVal, oldVal) {
                    if(newVal.Arthiritis === 'No'){
                        $scope.disArthiritis = true;
                        $scope.isrequireArthiritis = false;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.arthritisDetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.arthritisDetails){
                            $scope.isrequireArthiritis = true;}
                        $scope.disArthiritis = false;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.arthritisDetail=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.arthritisDetail;

                    }
                },
                true
            );
            $scope.disCancer= true;
            $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.cancerTumorCystDetail=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.cancerTumorCystDetails;

            $scope.$watch(
                function(){
                    return {Cancer: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.cancerTumorCyst, cancerTumorCystDetail: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.cancerTumorCystDetail};
                },
                function(newVal, oldVal) {
                    if(newVal.Cancer === 'No'){
                        $scope.disCancer = true;
                        $scope.isrequireCancer = false;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.cancerTumorCystDetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.cancerTumorCystDetails){
                            $scope.isrequireCancer = true;}
                        $scope.disCancer = false;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.cancerTumorCystDetails=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.cancerTumorCystDetail;

                    }
                },
                true
            );
            $scope.dispshyciatric= true;
            $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.psychiatricConditionDetail=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.psychiatricConditionDetails;

            $scope.$watch(
                function(){
                    return {pshyciatric: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.psychiatricCondition, psychiatricConditionDetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.psychiatricConditionDetail};
                },
                function(newVal, oldVal) {
                    if(newVal.pshyciatric === 'No'){
                        $scope.dispshyciatric = true;
                        $scope.isrequirepshyciatric= false;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.psychiatricConditionDetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.psychiatricConditionDetails){
                            $scope.isrequirepshyciatric= true;}
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.psychiatricConditionDetails=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.psychiatricConditionDetail;

                        $scope.dispshyciatric = false;
                    }
                },
                true
            );



            $scope.disAlcohol= true;
            $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.alcoholDrugAbuseDetail=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.alcoholDrugAbuseDetails;

            $scope.$watch(
                function(){
                    return {Alcohol: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.alcoholDrugAbuse, alcoholDrugAbuseDetails: $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.alcoholDrugAbuseDetail};
                },
                function(newVal, oldVal) {
                    if(newVal.Alcohol === 'No'){
                        $scope.disAlcohol = true;
                        $scope.isrequireAlcohol= false;
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.alcoholDrugAbuseDetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.alcoholDrugAbuseDetails){
                            $scope.isrequireAlcohol= true;}
                        $scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.alcoholDrugAbuseDetails=$scope.createUpdateDto.groupHealthCashlessClaimIllnessDetail.alcoholDrugAbuseDetail;

                        $scope.disAlcohol = false;
                    }
                },
                true
            );

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
