(function (angular) {
    "use strict";
var app= angular.module('createEndorsement', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives',
    'angularFileUpload', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices','ui.bootstrap.modal','ngMessages']);

app.config(["$routeProvider", function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'createEndorsementTpl.html',
        controller: 'EndorsementCtrl',
        resolve: {

        }
    })
}])
    .config(['datepickerPopupConfig', function (datepickerPopupConfig) {
        datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
        datepickerPopupConfig.currentText = 'Today';
        datepickerPopupConfig.clearText = 'Clear';
        datepickerPopupConfig.closeText = 'Done';
        datepickerPopupConfig.closeOnDateSelection = true;
    }]);
    app.controller('EndorsementCtrl', ['$scope', '$http', '$location','getQueryParameter','globalConstants', function ($scope, $http, $location, getQueryParameter,globalConstants) {

            $scope.selectedItem = 1;
            $scope.provinces = [];
            $scope.townList=[];
            $scope.empTownList=[];
            $scope.titleList = globalConstants.title;
            $scope.genderList = globalConstants.gender;
            $scope.beneficiariesList = [];
            $scope.agentDetails = [];
            $scope.bankDetailsResponse=[];
            $scope.additionalDocumentList = [];
            $scope.documentList = [];
            $scope.todayDate = new Date();

            $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                $scope.provinces = response;
            }).error(function (response, status, headers, config) {
            });

            $http.get('/pla/individuallife/endorsement/getAllBankNames').success(function (response, status, headers, config) {
                $scope.bankDetailsResponse = response;
                //console.log("Bank Details :"+JSON.stringify(response));
            }).error(function (response, status, headers, config) {
            });

            $scope.LADOB = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob= true;
            };

            $scope.updateLADOB = function ($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    $scope.launchdob2 = true;
                };

            $scope.launchBeneficiaryDob = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob4 = true;
            };
            $scope.getTownList = function (province) {
                //alert(province);
                var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
                if (provinceDetails)
                    $scope.townList = provinceDetails.cities;
            }

            $scope.getEmpTownList = function (province) {
                //alert(province);
                var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
                if (provinceDetails)
                    $scope.empTownList = provinceDetails.cities;
            }

            $scope.showBeneficiaryDob = function (dob) {
                    if (dob != null) {
                        $scope.newBeneficiary.age = moment().diff(new moment(new Date(dob)), 'years');
                    }
                };

            $scope.shareSumTest = function () {
                var sum = 0;
                for (var i=0; i< $scope.beneficiariesList.length;i++) {
                    sum = parseFloat(sum) + parseFloat($scope.beneficiariesList[i].share);
                }
                //console.log('sum: ' + sum);
                if (sum == 100.00) {
                    $scope.commisionStatus = false;
                    $scope.commisionMessage = false;
                }
                else {
                    $scope.commisionStatus = true;
                    $scope.commisionMessage = true;
                }
            };

        $scope.addBeneficiary = function (beneficiary) {
            if ($scope.beneficiariesList.length == 0) {
                $scope.beneficiariesList.push(beneficiary);
            }

            else {
                var checkLoopNameStatus = "true";
                for (var i=0;i< $scope.beneficiariesList.length;i++) {
                    if (beneficiary.nrc && $scope.beneficiariesList[i].nrc == beneficiary.nrc) {
                        checkLoopNameStatus = "false";
                        break;
                    } else if (($scope.beneficiariesList[i].firstName == beneficiary.firstName) &&
                        ($scope.beneficiariesList[i].gender == beneficiary.gender) && ((moment($scope.beneficiariesList[i].dateOfBirth).diff(moment(beneficiary.dateOfBirth), 'days')) == 0)) {
                        checkLoopNameStatus = "false";
                        break;
                    }
                }
                if (checkLoopNameStatus == "true") {
                    $scope.beneficiariesList.unshift(beneficiary);
                } else {
                    alert("This record is already existing");
                }
            }
            //$scope.clear();
            $scope.newBeneficiary={};
            $('#beneficialModal').modal('hide');
            console.log("BeneficiaryList:" + JSON.stringify($scope.beneficiariesList));
        };

        $scope.searchAgent = function () {
            $scope.check = false;
            $scope.checking = true;
            $scope.agentId = $scope.newAgent.agentId;

            $http.get("getagentdetail/" + $scope.agentId).success(function (response, status, headers, config) {
                $scope.newAgent = response;
                $scope.checking = false;
            }).error(function (response, status, headers, config) {
                var check = status;
                if (check == 500) {
                    $scope.check = true;
                    $scope.newAgent.firstName = null;
                    $scope.newAgent.lastName = null;
                }
            });

        };

        $scope.addAgent = function (agent) {
            if ($scope.agentDetails.length == 0) {
                $scope.agentDetails.unshift(agent);
            }
            else {
                var checkLoopNameStatus = "true";

                for (var i=0;i< $scope.agentDetails.length;i++) {
                    if ($scope.agentDetails[i].agentId == agent.agentId) {
                        checkLoopNameStatus = "false";
                        break;
                    }
                }

                if (checkLoopNameStatus == "true") {
                    $scope.agentDetails.unshift(agent);
                }
                else {
                    alert("Particular AgentId is Already Added..Please Choose different AgentId");
                }
            }
            $scope.newAgent={};
            $('#agentModal').modal('hide');
        };
        $scope.deleteAgent=function(agentId){
            for(var i=0;i< $scope.agentDetails.length;i++){
                if($scope.agentDetails[i].agentId == agentId){
                    $scope.agentDetails.splice(i,1);
                }
            }
            $scope.commisionSumTest();
        }

        $scope.commisionSumTest = function () {
            var sum = 0;
            for (var i=0;i< $scope.agentDetails.length;i++) {

                sum = parseFloat(sum) + parseFloat($scope.agentDetails[i].commission);
            }
            //console.log('sum: ' + sum);
            if (sum == 100.00) {
                $scope.agentMessage = false;
            }
            else {
                $scope.agentMessage = true;
            }
        };

        $scope.addAdditionalDocument = function () {
            $scope.additionalDocumentList.unshift({});
            $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();

        };

        $scope.removeAdditionalDocument = function (index) {
            $scope.additionalDocumentList.splice(index, 1);
            $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
        };

        $scope.callAdditionalDoc = function (file) {
            if (file[0]) {
                $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
            }
        }
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
        }

        $scope.documentNameLengthTest=function(){
            var enableAdditionalUploadButton= true;
            for(var i=0;i < $scope.documentList.length;i++){
                if($scope.documentList[i].documentAttached != null && $scope.documentList[i].documentAttached[0] != null){
                    //alert($scope.documentList[i].documentAttached[0].name.length);
                    if($scope.documentList[i].documentAttached[0].name.length >100)
                    {
                        enableAdditionalUploadButton=false;
                        break;
                    }
                }
            }
            if(enableAdditionalUploadButton){
                 return true;
            }
            else
            {
                return false
            }
        }

        $scope.documentList=[
            {
                "documentId":"BROUGHT_IN_DEAD_(BID)_CERTIFICATE",
                "documentName":"BROUGHT IN DEAD(BID) CERTIFICATE",
                "contentType":"application/x-javascript",
                "gridFsDocId":"56040bfe7c8508839f7c0f56",
                "mandatory":true,
                "requireForSubmission":true,
                "approved":true
            },
            {
                "documentId":"ANY_CLINICAL_ABSTRACTS_RECORDS_IF_AVAILABLE",
                "documentName":"ANY CLINICAL ABSTRACTS RECORDS IF AVAILABLE",
                "contentType":"application/octet-stream",
                "gridFsDocId":"56040bfe7c8508839f7c0f58",
                "mandatory":true,
                "requireForSubmission":true,
                "approved":true
            },
            {
                "documentId":"ADDRESS_PROOF",
                "documentName":"ADDRESS PROOF",
                "contentType":"application/octet-stream",
                "gridFsDocId":"56040bff7c8508839f7c0f5a",
                "mandatory":true,
                "requireForSubmission":true,
                "approved":true
            },
            {
                "documentId":"BURIAL_CERTIFICATE",
                "documentName":"BURIAL CERTIFICATE",
                "contentType":"application/octet-stream",
                "gridFsDocId":"56040bff7c8508839f7c0f5c",
                "mandatory":true,
                "requireForSubmission":true,
                "approved":true
            }
        ];

    }])
})(angular);