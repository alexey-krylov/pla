angular.module('CreatePreAuthorizationRequest', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover',
    'directives', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices','ui.bootstrap.modal','ngMessages','angularFileUpload'])
    .config(['datepickerPopupConfig', function (datepickerPopupConfig) {
        datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
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
//                        console.log(deferred);

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
                        var deferred = $q.defer();
                        var preAuthorizationId = getQueryParameter('preAuthorizationId');
                        deferred.resolve(preAuthorizationId)
                        return deferred.promise;
                    }],

                    clientId : ['$q', '$http','getQueryParameter', function ($q, $http, getQueryParameter) {
                        var deferred = $q.defer();
                        var deferred = $q.defer();
                        var clientId = getQueryParameter('clientId');
                        deferred.resolve(clientId)
                        return deferred.promise;
                    }]
                }

            }
        )}])

    .controller('createPreAuthorizationRequestCtrl', ['$scope', '$http','createUpdateDto','$timeout','getQueryParameter','$window','documentList','$upload','preAuthorizationId','clientId',
        function ($scope, $http, createUpdateDto, $timeout, getQueryParameter, $window, documentList, $upload, preAuthorizationId, clientId){
            $scope.createUpdateDto = createUpdateDto;
            $scope.drugServicesDtoList = $scope.createUpdateDto.drugServicesDtos;
            $scope.treatmentDiagnosis={};
            $scope.diagnosisTreatmentDtoToUpdate={};
            $scope.index = null;
            $scope.treatmentDiagnosisIndex = null;
            $scope.isEditMode = false;
            $scope.createUpdateCommand={};
            $scope.diagnosisTreatmentDtos={};
            $scope.createUpdateCommand.claimantHCPDetailDto=$scope.createUpdateDto.claimantHCPDetailDto;
            $scope.createUpdateCommand.claimantHCPDetailDto={};
            $scope.createUpdateCommand.claimantPolicyDetailDto={};
            $scope.createUpdateCommand.diagnosisTreatmentDtos=[];
            $scope.createUpdateCommand.illnessDetailDto={};
            $scope.createUpdateCommand.drugServicesDtos=[];
            $scope.documentList = documentList;
            $scope.additionalDocumentList = [{}];
            $scope.disableSubmit = false;
            $scope.isViewMode = false;
            //alert(preAuthorizationId+" "+clientId);
            if($scope.createUpdateDto.submitted){
                $scope.isViewMode = true;
            }
            $scope.$watch('documentList', function(newCollection, oldCollection){
                console.log("watching");
                $scope.disableSubmit = $scope.shouldSubmitBeDisabled(newCollection);
            });

            $scope.shouldSubmitBeDisabled = function(documentList){
                for (var i = 0; i < documentList.length; i++) {
                    var document = documentList[i];
                    console.log(document);
                    if(document.fileName == null || document.content == null){
                        return true;
                    }
                }
                return false;
            };

            $scope.viewTreatmentDiagnosis = function(treatmentDiagnosis, treatmentDiagnosisIndex){
                $scope.treatmentDiagnosisIndex = treatmentDiagnosisIndex;
                $scope.diagnosisTreatmentDto = treatmentDiagnosis;
                $scope.drugServicesDto = {};
                $scope.diagnosisTreatmentDtosUpdate = {};
                $scope.createUpdateDto.drugServicesDtosSave = [];
            };

            $scope.updateTreatmentAndDiagnosis = function(diagnosisTreatmentDto){
                $scope.createUpdateDto.diagnosisTreatmentDtos[$scope.treatmentDiagnosisIndex] = diagnosisTreatmentDto;
            };

            $scope.updateDrugServicesDto = function(drugServicesDto,index){
                $scope.index = index;
                $scope.isEditMode = true;
                $scope.diagnosisTreatmentDtoToUpdate = drugServicesDto;
            };

            $scope.saveDiagnosisTreatmentDto = function(diagnosisTreatmentDtoToUpdate){
                if($scope.isEditMode){

                    $scope.createUpdateDto.drugServicesDtos[$scope.index] = diagnosisTreatmentDtoToUpdate;

                    $scope.isEditMode = false;

                } else{
                    $scope.drugServicesDtoList.push(diagnosisTreatmentDtoToUpdate);
                }
            };

            $scope.deleteTreatmentDiagnosis = function(index){
                $scope.createUpdateDto.drugServicesDtos.splice(index, 1);

            };

            $scope.savePreAuthorizationRequest= function(){

                $http({
                    url: '/pla/grouphealth/claim/cashless/preauthorizationrequest/updatepreauthorization',
                    method: 'POST',
                    data: $scope.createUpdateDto
                }).then(function (response) {
                        console.log("first===" + JSON.stringify($scope.createUpdateDto));

                    },
                    function (response) {
                        console.log("Second" + JSON.stringify($scope.createUpdateDto));
                    });

            };

            $scope.submitPreAuthorizationRequest= function(){
                $scope.createUpdateDto.submitEventFired = true;
                $http({
                    url: '/pla/grouphealth/claim/cashless/preauthorizationrequest/submitpreauthorization',
                    method: 'POST',
                    data: $scope.createUpdateDto
                }).success(function(){
                    window.location.reload();
                }).error();

            };


            $scope.back = function () {
                window.location.reload();
            };
            $scope.fileSaved = null;



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
            };



            $scope.$watch('fileSaved', function (n, o) {
                if (n && n.length) {
                    $scope.documentId = n[0].name
                }
            });






            /*$scope.uploadAdditionalDocument = function () {
             //alert('Upload');
             for (var i = 0; i < $scope.additionalDocumentList.length; i++) {
             var document = $scope.additionalDocumentList[i];
             var files = document.documentAttached;
             //alert($scope.proposal.proposalId);

             $scope.additional = true;
             if (files) {
             console.dir(files);
             $upload.upload({
             url: '/pla/grouphealth/claim/cashless/preauthorizationrequest/uploadmandatorydocument',
             file: files,
             fields: {
             //documentId: document.documentName,
             documentId: document.documentId,
             policyId: $scope.policyId,
             mandatory: false
             },
             method: 'POST'
             }).progress(function (evt) {

             }).success(function (data, status, headers, config) {
             ////console.log('file ' + config.file.name + 'uploaded. Response: ' +
             // JSON.stringify(data));
             });
             }
             }
             };*/

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
            //$scope.additionalDocumentList = [{}];
            //$http.get("/pla/grouphealth/policy/getadditionaldocuments/"+ $scope.policyId).success(function (data, status) {
            //    console.log(data);
            //    $scope.additionalDocumentList=data;
            //    $scope.checkDocumentAttached=$scope.additionalDocumentList!=null;
            //
            //});


            /*  $scope.addAdditionalDocument = function () {
             $scope.additionalDocumentList.unshift({});
             };

             $scope.removeAdditionalDocument = function (index) {
             $scope.additionalDocumentList.splice(index, 1);
             };
             */
            /*if ($scope.documentList) {
             if ($scope.documentList.documentAttached) {
             if ($scope.documentList.documentAttached.length == $scope.documentList.documentName.length) {
             $scope.disableUploadButton = true;
             } else {
             $scope.disableUploadButton = false;
             }
             }
             }*/

            /* $scope.callAdditionalDoc = function (file) {
             if (file[0]) {
             $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
             }
             }
             */
            $scope.uploadDocumentFiles = function () {
                for (var i = 0; i < $scope.documentList.length; i++) {
                    var document = $scope.documentList[i];
                    var files = document.documentAttached;
                    console.log(files);
                    if (files) {
                        $upload.upload({
                            url: '/pla/grouphealth/claim/cashless/preauthorizationrequest/uploadmandatorydocument',
                            file: files,
                            fields: {documentId: document.documentId, preAuthorizationRequestId: $scope.createUpdateDto.preAuthorizationRequestId, mandatory: true},
                            method: 'POST'
                        }).progress(function (evt) {
                            console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
                        }).success(function (data, status, headers, config) {
                            $http.get("/pla/grouphealth/claim/cashless/preauthorizationrequest/getmandatorydocuments/" + clientId + "/"+preAuthorizationId).success(function (response, status, headers, config) {
                                $scope.documentList = response;
                            });
                        });

                    }

                }
            };



            /*if ($scope.documentList) {
             if ($scope.documentList.documentAttached) {
             if ($scope.documentList.documentAttached.length == $scope.documentList.documentName.length) {
             $scope.disableUploadButton = true;
             } else {
             $scope.disableUploadButton = false;
             }
             }
             }*/


            /*$scope.callAdditionalDoc = function (file) {
             if (file[0]) {
             $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
             }
             }*/


            //$http.get("/pla/grouphealth/policy/getadditionaldocuments/"+ $scope.policyId).success(function (data, status) {
            //    console.log(data);
            //    $scope.additionalDocumentList=data;
            //    $scope.checkDocumentAttached=$scope.additionalDocumentList!=null;
            //
            //});

            //$scope.isUploadEnabledForAdditionalDocument = function () {
            //    var enableAdditionalUploadButton = ($scope.additionalDocumentList != null);
            //    for (var i = 0; i < $scope.additionalDocumentList.length; i++) {
            //        var document = $scope.additionalDocumentList[i];
            //        var files = document.documentAttached;
            //        //alert(i+"--"+files)
            //        //alert(i+"--"+document.content);
            //        if (!(files || document.content)) {
            //            enableAdditionalUploadButton = false;
            //            break;
            //        }
            //    }
            //    return enableAdditionalUploadButton;
            //}





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
