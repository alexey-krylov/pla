(function (angular) {
    "use strict";
    var app= angular.module('CreatePreAuthorizationRequest', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives',
        'angularFileUpload', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices']);


    app.config(['datepickerPopupConfig', function (datepickerPopupConfig) {
        datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
        datepickerPopupConfig.currentText = 'Today';
        datepickerPopupConfig.clearText = 'Clear';
        datepickerPopupConfig.closeText = 'Done';
        datepickerPopupConfig.closeOnDateSelection = true;
    }])

    app.config(["$routeProvider", function ($routeProvider) {
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
                        if (clientId && !_.isEmpty(clientId)) {
                            var deferred = $q.defer();
                            $http.get('/pla/grouphealth/claim/cashless/preauthorizationrequest/getmandatorydocuments/' + clientId).success(function (response, status, headers, config) {
                                deferred.resolve(response)
                            }).error(function (response, status, headers, config) {
                                deferred.reject();
                            });
                            return deferred.promise;
                        } else {
                            return false;
                        }
                    }]
                }

            }
        )}]);

    app.controller('createPreAuthorizationRequestCtrl', ['$scope', '$http','createUpdateDto','$timeout','getQueryParameter','$window','documentList','$upload',
        function ($scope, $http,createUpdateDto, getQueryParameter,$window,documentList,$upload){
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
            $scope.documentList=[];
            $scope.additionalDocumentList = [{}];
            //$scope.documentList = documentList;

            $http.get('/pla/grouphealth/claim/cashless/preauthorizationrequest/getmandatorydocuments/' + 1000642).success(function (response, status, headers, config) {
                //deferred.resolve(response)
                $scope.documentList=response;
            }).error(function (response, status, headers, config) {
            });

            $scope.viewTreatmentDiagnosis = function(treatmentDiagnosis, treatmentDiagnosisIndex){
                $scope.treatmentDiagnosisIndex = treatmentDiagnosisIndex;
                $scope.diagnosisTreatmentDto = treatmentDiagnosis;
                $scope.drugServicesDto = {};
                $scope.diagnosisTreatmentDtosUpdate = {};
                $scope.createUpdateDto.drugServicesDtosSave = [];
            }

            $scope.updateTreatmentAndDiagnosis = function(diagnosisTreatmentDto){
                $scope.createUpdateDto.diagnosisTreatmentDtos[$scope.treatmentDiagnosisIndex] = diagnosisTreatmentDto;
            }

            $scope.updateDrugServicesDto = function(drugServicesDto,index){
                $scope.index = index;
                $scope.isEditMode = true;
                $scope.diagnosisTreatmentDtoToUpdate = drugServicesDto;
            }

            $scope.saveDiagnosisTreatmentDto = function(diagnosisTreatmentDtoToUpdate){
                if($scope.isEditMode){

                    $scope.createUpdateDto.drugServicesDtos[$scope.index] = diagnosisTreatmentDtoToUpdate;

                    $scope.isEditMode = false;

                } else{
                    $scope.drugServicesDtoList.push(diagnosisTreatmentDtoToUpdate);
                }
            }

            $scope.deleteTreatmentDiagnosis = function(index){
                $scope.createUpdateDto.drugServicesDtos.splice(index, 1);

            }

            $scope.savePreAuthorizationRequest= function(){

                    $http({
                        url: '/pla/grouphealth/claim/cashless/preauthorizationrequest/createorupdate',
                        method: 'POST',
                        data: $scope.createUpdateDto
                    }).then(function (response) {
                            console.log("first===" + JSON.stringify($scope.createUpdateDto));

                        },
                        function (response) {
                            console.log("Second" + JSON.stringify($scope.createUpdateDto));
                        });

             }


            $scope.back = function () {
                window.location.reload();
            }
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
            }



            $scope.$watch('fileSaved', function (n, o) {
                if (n && n.length) {
                    $scope.documentId = n[0].name
                }
            });






            $scope.uploadAdditionalDocument = function () {
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
            };

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
            //$scope.uploadDocumentFiles = function () {
            //    // //console.log($scope.documentList.length);
            //    for (var i = 0; i < $scope.documentList.length; i++) {
            //        var document = $scope.documentList[i];
            //        var files = document.documentAttached;
            //        //console.dir(files);
            //        // //alert(files.name);
            //        if (files) {
            //            //console.log('File Uploading....');
            //            $upload.upload({
            //                url: '/pla/grouphealth/policy/uploadmandatorydocument',
            //                file: files,
            //                fields: {
            //                    documentId: document.documentId,
            //                    policyId: $scope.policyId,
            //                    mandatory: true,
            //                    isApproved: true
            //                },
            //                method: 'POST'
            //            }).progress(function (evt) {
            //
            //            }).success(function (data, status, headers, config) {
            //                ////console.log('file ' + config.file.name + 'uploaded. Response: ' +
            //                // JSON.stringify(data));
            //            });
            //        }
            //
            //    }
            //
            //};



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
})(angular);

function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
}
