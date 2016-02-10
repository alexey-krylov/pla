
var  app = angular.module('CreatePreAuthorizationRequest', ['common', 'ngRoute','ngMessages', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover',
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
                templateUrl: 'preAuthorizationRequest.html',
                controller: 'createPreAuthorizationRequestCtrl',
                resolve:{

                    createUpdateDto: ['$q', '$http', 'getQueryParameter', function ($q, $http, getQueryParameter) {
                        var deferred = $q.defer();
                        var preAuthorizationId = getQueryParameter('preAuthorizationId');

                        $http.get('/pla/grouphealth/claim/cashless/preauthorizationrequest/getpreauthorizationclaimantdetailcommandfrompreauthorizationrequestid?preAuthorizationId='+preAuthorizationId).success(function (response, status, headers, config) {
                            deferred.resolve(response);

                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
//                        //console.log(deferred);

                        return deferred.promise;
                    }],
                    documentList: ['$q', '$http','getQueryParameter', function ($q, $http, getQueryParameter) {
                        var deferred = $q.defer();
                        var clientId = getQueryParameter('clientId');
                        var preAuthorizationId = getQueryParameter('preAuthorizationId');
                        if (clientId && !_.isEmpty(clientId)) {
                            var deferred = $q.defer();
                            $http.get("/pla/grouphealth/claim/cashless/preauthorizationrequest/getmandatorydocuments/" + clientId + "/"+preAuthorizationId).success(function (response, status, headers, config) {
                                deferred.resolve(response)
                            }).error(function (response, status, headers, config) {
                                deferred.reject();
                            });
                            return deferred.promise;
                        } else {
                            return false;
                        }
                    }],
                    preAuthorizationId : ['$q', '$http','getQueryParameter', function ($q, $http, getQueryParameter) {
                        var deferred = $q.defer();
                        var preAuthorizationId = getQueryParameter('preAuthorizationId');
                        deferred.resolve(preAuthorizationId)
                        return deferred.promise;
                    }],
                    clientId : ['$q', '$http','getQueryParameter', function ($q, $http, getQueryParameter) {
                        var deferred = $q.defer();
                        var clientId = getQueryParameter('clientId');
                        deferred.resolve(clientId)
                        return deferred.promise;
                    }]
                }

            }
        )}])

    .controller('createPreAuthorizationRequestCtrl', ['$scope', '$http','createUpdateDto','getQueryParameter','$window','documentList','$upload','preAuthorizationId','clientId',
        function ($scope, $http, createUpdateDto, getQueryParameter, $window, documentList, $upload, preAuthorizationId, clientId) {
            $scope.createUpdateDto = createUpdateDto;
            $scope.drugServicesDtoList = $scope.createUpdateDto.drugServicesDtos;
            $scope.treatmentDiagnosis = {};
            $scope.diagnosisTreatmentDtoToUpdate = {};
            $scope.index = null;
            $scope.treatmentDiagnosisIndex = null;
            $scope.isEditDrugTriggered = false;
            $scope.isEditDiagnosisTriggered = false;
            $scope.createUpdateCommand = {};
            $scope.diagnosisTreatmentDto = {};
            $scope.createUpdateCommand.claimantHCPDetailDto = $scope.createUpdateDto.claimantHCPDetailDto;
            $scope.createUpdateCommand.claimantHCPDetailDto = {};
            $scope.createUpdateCommand.claimantPolicyDetailDto = {};
            $scope.createUpdateCommand.diagnosisTreatmentDtos = [];
            $scope.createUpdateCommand.illnessDetailDto = {};
            $scope.createUpdateCommand.drugServicesDtos = [];
            $scope.documentList = documentList;
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

            $http.get("/pla/grouphealth/claim/cashless/preauthorizationrequest/getadditionaldocuments/" + preAuthorizationId).success(function (data, status, headers, config) {
                $scope.additionalDocumentList = data;
                $scope.checkDocumentAttached = $scope.additionalDocumentList != null;
            }).error(function (response, status, headers, config) {
                deferred.reject();
            });

            $scope.tratementdignosisnextbuttonfalse= function(){
                $scope.provisionaldignosisdiv = true;
                $scope.stepsSaved["4"] = true;
            };

            $scope.viewTreatmentDiagnosis = function (treatmentDiagnosis, treatmentDiagnosisIndex) {
                $scope.tratementdignosisnextbuttonfalse();
                $scope.isEditDiagnosisTriggered = true;
                $scope.treatmentDiagnosisIndex = treatmentDiagnosisIndex;
                $scope.diagnosisTreatmentDto = treatmentDiagnosis;
                $scope.drugServicesDto = {};
                $scope.diagnosisTreatmentDtosUpdate = {};
                $scope.createUpdateDto.drugServicesDtosSave = [];
                $scope.provisionaldignosisdiv=true;
            };

            $scope.updateTreatmentAndDiagnosis = function (diagnosisTreatmentDto) {  alert("");
                if ($scope.isEditDiagnosisTriggered) {
                    $scope.createUpdateDto.diagnosisTreatmentDtos[$scope.treatmentDiagnosisIndex] = diagnosisTreatmentDto;

                    $scope.savePreAuthorizationRequest();
                    $scope.isEditDiagnosisTriggered = false;
                    $scope.provisionaldignosisdiv=false;
                } else {
                    $scope.createUpdateDto.diagnosisTreatmentDtos.push(diagnosisTreatmentDto);
                    //console.log(JSON.stringify($scope.createUpdateDto));

                }
                $scope.provisionaldignosisdiv = false;
                $scope.stepsSaved["3"] = false;
            };

            $scope.create= function(){
                $scope.provisionaldignosisdiv = true;
                $scope.stepsSaved["3"] = true;
            };
            $scope.activenextbuttonforprovisional = function(){
                $scope.diagnosisTreatmentDto = {};
                $scope.diagnosisTreatmentDto.dateOfConsultation = $scope.createUpdateDto.diagnosisTreatmentDtos[0].dateOfConsultation;
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
                    $scope.createUpdateDto.drugServicesDtos[$scope.index] = diagnosisTreatmentDtoToUpdate;
                    $scope.savePreAuthorizationRequest();
                    $scope.isEditDrugTriggered = false;

                } else {
                    $scope.createUpdateDto.drugServicesDtos.push(diagnosisTreatmentDtoToUpdate);
                    $scope.savePreAuthorizationRequest();
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
                $scope.createUpdateDto.drugServicesDtos.splice(index, 1);
                $scope.savePreAuthorizationRequest();

            };

            /*Holds the indicator for steps in which save button is clicked*/

//end service drug availed section

            var saveStep = function () {
                $scope.stepsSaved[$scope.selectedItem] = true;
            };

            $scope.isSaveDisabled = function (formName) {
                return formName.$invalid;
            };

            $scope.savePreAuthorizationRequest = function () {
                console.log(JSON.stringify($scope.createUpdateDto));
                $http({
                    url: '/pla/grouphealth/claim/cashless/preauthorizationrequest/updatepreauthorization',
                    method: 'POST',
                    data: $scope.createUpdateDto
                }).success(function (response) {
                    $http.get('/pla/grouphealth/claim/cashless/preauthorizationrequest/getpreauthorizationclaimantdetailcommandfrompreauthorizationrequestid?preAuthorizationId='+preAuthorizationId)
                        .success(function (response) {
                            $scope.createUpdateDto = response;
                        }).error(function (response, status, headers, config) {
                    });
                }).error();
            };

            $scope.submitPreAuthorizationRequest = function () {
                $scope.createUpdateDto.submitEventFired = true;
                $http({
                    url: '/pla/grouphealth/claim/cashless/preauthorizationrequest/submitpreauthorization',
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
                window.location.href = '/pla/grouphealth/claim/cashless/preauthorizationrequest/getpreauthorizationfordefaultlist';
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
                            url: '/pla/grouphealth/claim/cashless/preauthorizationrequest/uploadmandatorydocument',
                            file: files,
                            fields: {
                                documentId: document.documentId,
                                preAuthorizationRequestId: $scope.createUpdateDto.preAuthorizationRequestId,
                                mandatory: true
                            },
                            method: 'POST'
                        }).progress(function (evt) {
                            //console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
                        }).success(function (data, status, headers, config) {
                            $http.get("/pla/grouphealth/claim/cashless/preauthorizationrequest/getmandatorydocuments/" + clientId + "/" + preAuthorizationId).success(function (response, status, headers, config) {
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
                            url: '/pla/grouphealth/claim/cashless/preauthorizationrequest/uploadmandatorydocument',
                            file: files,
                            fields: {
                                documentId: document.documentId,
                                preAuthorizationRequestId: $scope.createUpdateDto.preAuthorizationRequestId,
                                mandatory: false
                            },
                            method: 'POST'
                        }).progress(function (evt) {

                        }).success(function (data, status, headers, config) {
                            $http.get("/pla/grouphealth/claim/cashless/preauthorizationrequest/getadditionaldocuments/" + preAuthorizationId).success(function (response, status, headers, config) {
                                $scope.additionalDocumentList = response;
                            });
                        });
                    }

                }
            };

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
            };

            $scope.addAdditionalDocument = function () {
                $scope.additionalDocumentList.unshift({});
            };

            $scope.callAdditionalDoc = function (file) {
                if (file[0]) {
                    $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
                }
            };

            $scope.removeAdditionalDocumentCommand = {};
            $scope.removeAdditionalDocument = function (index, gridFsDocId) {
                $scope.removeAdditionalDocumentCommand.gridFsDocId = gridFsDocId;
                $scope.removeAdditionalDocumentCommand.preAuthorizationId = $scope.createUpdateDto.preAuthorizationId;
                if (gridFsDocId) {
                    $http({
                        url: '/pla/grouphealth/claim/cashless/preauthorizationrequest/removeadditionalDocument',
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
                $scope.diagnosisTreatmentDto.pregnancyDateOfDelivery = formatDate(ProbableDate);
                //console.log("qwqe############"+$scope.diagnosisTreatmentDto.pregnancyDateOfDelivery );
            };

            $scope.launchFirstConsultanceDate = function ($event){
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datepickerconsultance = {'opened': true};
            };

            $scope.changelaunchFirstConsultanceDate=function(consultationDate) {
                $scope.diagnosisTreatmentDto.dateOfConsultation = formatDate(consultationDate);
            };

            $scope.launchProbAdmissionDate = function ($event){
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datepickeradmission = {'opened': true};
            };

            $scope.changeProbAdmissionDate=function(probAdmissionDate) {
                $scope.diagnosisTreatmentDto.dateOfAdmission = formatDate(probAdmissionDate);
                //console.log("qwqe############"+$scope.diagnosisTreatmentDto.dateOfAdmission);
            };

            $scope.hcpServiceDetails = [];

            $scope.getHCPServiceDetails = function(){
                $http.get("/pla/grouphealth/claim/cashless/preauthorizationrequest/getallrelevantservices/" + preAuthorizationId).success(function (data, status, headers, config) {
                    $scope.hcpServiceDetails = data;
                    console.log(  $scope.data);
                }).error(function (response, status, headers, config) {
                });
            };


            var mode = getQueryParameter("mode");
            if (mode == 'view'){
                $scope.isViewMode=true;

            }


            $scope.createUpdateDto.illnessDetailDto.htndetail=$scope.createUpdateDto.illnessDetailDto.htndetails;
            $scope.disableHtn = true;
            $scope.$watch(
                function(){
                    return {htn: $scope.createUpdateDto.illnessDetailDto.htn, htnDetails: $scope.createUpdateDto.illnessDetailDto.htndetail};
                },
                function(newVal, oldVal) {
                    if(newVal.htn === 'No'){
                        $scope.isrequire = false;
                        $scope.disableHtn = true;
                        $scope.createUpdateDto.illnessDetailDto.htndetails='';

                    }
                    else {
                        if( !$scope.createUpdateDto.illnessDetailDto.htndetails){
                            $scope.isrequire = true;

                        }
                        $scope.disableHtn = false;
                        $scope.createUpdateDto.illnessDetailDto.htndetails=$scope.createUpdateDto.illnessDetailDto.htndetail;
                    }
                },
                true
            );
            $scope.createUpdateDto.illnessDetailDto.ihdhoddetail=$scope.createUpdateDto.illnessDetailDto.ihdhoddetails;
            $scope.disableIhd = true;
            $scope.$watch(
                function(){
                    return {idh: $scope.createUpdateDto.illnessDetailDto.idhHOD, htnDetails: $scope.createUpdateDto.illnessDetailDto.ihdhoddetail};
                },
                function(newVal, oldVal) {
                    if(newVal.idh === 'No'){
                        $scope.isrequireIhd = false;
                        $scope.disableIhd = true;
                        $scope.createUpdateDto.illnessDetailDto.ihdhoddetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.illnessDetailDto.ihdhoddetails){
                            $scope.isrequireIhd = true;}
                        $scope.disableIhd = false;
                        $scope.createUpdateDto.illnessDetailDto.ihdhoddetails=$scope.createUpdateDto.illnessDetailDto.ihdhoddetail;

                    }
                },
                true
            );
            $scope.disdibetes= true;
            $scope.createUpdateDto.illnessDetailDto.diabetesDetail=$scope.createUpdateDto.illnessDetailDto.diabetesDetails;

            $scope.$watch(
                function(){
                    return {Dibetes: $scope.createUpdateDto.illnessDetailDto.diabetes, diabetesDetails: $scope.createUpdateDto.illnessDetailDto.diabetesDetail};
                },
                function(newVal, oldVal) {
                    if(newVal.Dibetes === 'No'){
                        $scope.isrequireDibetes = false;
                        $scope.disdibetes = true;
                        $scope.createUpdateDto.illnessDetailDto.diabetesDetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.illnessDetailDto.diabetesDetails){
                            $scope.isrequireDibetes = true;}
                        $scope.disdibetes = false;
                        $scope.createUpdateDto.illnessDetailDto.diabetesDetails=$scope.createUpdateDto.illnessDetailDto.diabetesDetail;

                    }
                },
                true
            );
            $scope.disAsthma= true;
            $scope.createUpdateDto.illnessDetailDto.asthmaCOPDTBDetail=$scope.createUpdateDto.illnessDetailDto.asthmaCOPDTBDetails;

            $scope.$watch(
                function(){
                    return {Asthma: $scope.createUpdateDto.illnessDetailDto.asthmaCOPDTB, asthmaCOPDTBDetails: $scope.createUpdateDto.illnessDetailDto.asthmaCOPDTBDetail};
                },
                function(newVal, oldVal) {
                    if(newVal.Asthma === 'No'){
                        $scope.isrequireAsthma = false;
                        $scope.disAsthma = true;
                        $scope.createUpdateDto.illnessDetailDto.asthmaCOPDTBDetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.illnessDetailDto.asthmaCOPDTBDetails){
                            $scope.isrequireAsthma = true;}
                        $scope.disAsthma = false;
                        $scope.createUpdateDto.illnessDetailDto.asthmaCOPDTBDetails=$scope.createUpdateDto.illnessDetailDto.asthmaCOPDTBDetail;

                    }
                },
                true
            );
            $scope.disStd= true;
            $scope.createUpdateDto.illnessDetailDto.stdhivaidsdetail=$scope.createUpdateDto.illnessDetailDto.stdhivaidsdetails;

            $scope.$watch(
                function(){
                    return {Std: $scope.createUpdateDto.illnessDetailDto.stdhivaids, stdhivaidsdetails: $scope.createUpdateDto.illnessDetailDto.stdhivaidsdetail};
                },
                function(newVal, oldVal) {
                    if(newVal.Std === 'No'){
                        $scope.isrequireStd = false;
                        $scope.disStd = true;
                        $scope.createUpdateDto.illnessDetailDto.stdhivaidsdetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.illnessDetailDto.stdhivaidsdetails){
                            $scope.isrequireStd = true;}
                        $scope.disStd = false;
                        $scope.createUpdateDto.illnessDetailDto.stdhivaidsdetails=$scope.createUpdateDto.illnessDetailDto.stdhivaidsdetail;

                    }
                },
                true
            );
            $scope.disArthiritis= true;
            $scope.createUpdateDto.illnessDetailDto.arthritisDetail=$scope.createUpdateDto.illnessDetailDto.arthritisDetails;

            $scope.$watch(
                function(){
                    return {Arthiritis: $scope.createUpdateDto.illnessDetailDto.arthritis, stdhivaidsdetails: $scope.createUpdateDto.illnessDetailDto.arthritisDetail};
                },
                function(newVal, oldVal) {
                    if(newVal.Arthiritis === 'No'){
                        $scope.disArthiritis = true;
                        $scope.isrequireArthiritis = false;
                        $scope.createUpdateDto.illnessDetailDto.arthritisDetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.illnessDetailDto.arthritisDetails){
                            $scope.isrequireArthiritis = true;}
                        $scope.disArthiritis = false;
                        $scope.createUpdateDto.illnessDetailDto.arthritisDetail=$scope.createUpdateDto.illnessDetailDto.arthritisDetail;

                    }
                },
                true
            );
            $scope.disCancer= true;
            $scope.createUpdateDto.illnessDetailDto.cancerTumorCystDetail=$scope.createUpdateDto.illnessDetailDto.cancerTumorCystDetails;

            $scope.$watch(
                function(){
                    return {Cancer: $scope.createUpdateDto.illnessDetailDto.cancerTumorCyst, cancerTumorCystDetail: $scope.createUpdateDto.illnessDetailDto.cancerTumorCystDetail};
                },
                function(newVal, oldVal) {
                    if(newVal.Cancer === 'No'){
                        $scope.disCancer = true;
                        $scope.isrequireCancer = false;
                        $scope.createUpdateDto.illnessDetailDto.cancerTumorCystDetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.illnessDetailDto.cancerTumorCystDetails){
                            $scope.isrequireCancer = true;}
                        $scope.disCancer = false;
                        $scope.createUpdateDto.illnessDetailDto.cancerTumorCystDetails=$scope.createUpdateDto.illnessDetailDto.cancerTumorCystDetail;

                    }
                },
                true
            );
            $scope.dispshyciatric= true;
            $scope.createUpdateDto.illnessDetailDto.psychiatricConditionDetail=$scope.createUpdateDto.illnessDetailDto.psychiatricConditionDetails;

            $scope.$watch(
                function(){
                    return {pshyciatric: $scope.createUpdateDto.illnessDetailDto.psychiatricCondition, psychiatricConditionDetails: $scope.createUpdateDto.illnessDetailDto.psychiatricConditionDetail};
                },
                function(newVal, oldVal) {
                    if(newVal.pshyciatric === 'No'){
                        $scope.dispshyciatric = true;
                        $scope.isrequirepshyciatric= false;
                        $scope.createUpdateDto.illnessDetailDto.psychiatricConditionDetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.illnessDetailDto.psychiatricConditionDetails){
                            $scope.isrequirepshyciatric= true;}
                        $scope.createUpdateDto.illnessDetailDto.psychiatricConditionDetails=$scope.createUpdateDto.illnessDetailDto.psychiatricConditionDetail;

                        $scope.dispshyciatric = false;
                    }
                },
                true
            );



            $scope.disAlcohol= true;
            $scope.createUpdateDto.illnessDetailDto.alcoholDrugAbuseDetail=$scope.createUpdateDto.illnessDetailDto.alcoholDrugAbuseDetails;

            $scope.$watch(
                function(){
                    return {Alcohol: $scope.createUpdateDto.illnessDetailDto.alcoholDrugAbuse, alcoholDrugAbuseDetails: $scope.createUpdateDto.illnessDetailDto.alcoholDrugAbuseDetail};
                },
                function(newVal, oldVal) {
                    if(newVal.Alcohol === 'No'){
                        $scope.disAlcohol = true;
                        $scope.isrequireAlcohol= false;
                        $scope.createUpdateDto.illnessDetailDto.alcoholDrugAbuseDetails='';
                    }
                    else {
                        if( !$scope.createUpdateDto.illnessDetailDto.alcoholDrugAbuseDetails){
                            $scope.isrequireAlcohol= true;}
                        $scope.createUpdateDto.illnessDetailDto.alcoholDrugAbuseDetails=$scope.createUpdateDto.illnessDetailDto.alcoholDrugAbuseDetail;

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
