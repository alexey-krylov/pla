angular.module('createEndorsement', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives',
    'angularFileUpload', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices'])

    .controller('EndorsementCtrl', ['$scope', '$http', '$timeout', '$upload', 'provinces', 'getProvinceAndCityDetail', 'globalConstants',
        'agentDetails', 'stepsSaved', 'policyDetails', 'endorsementNumber', 'getQueryParameter', '$window', 'premiumData', 'documentList',
        function ($scope, $http, $timeout, $upload, provinces, getProvinceAndCityDetail, globalConstants, agentDetails, stepsSaved, policyDetails, endorsementNumber,
                  getQueryParameter, $window, premiumData, documentList) {

            var mode = getQueryParameter("mode");
            if (mode == 'view') {
                $scope.isViewMode = true;

            }
            $scope.isEnablePolicyHolderMode = false;
            $scope.isEnableContactMode = false;
            $scope.isEnablePlanMode = false;
            $scope.isEnable = false;
            $scope.disableProposerSaveButton = true;
            $scope.endorsementType=getQueryParameter("endorsementType");
            var enableTab = getQueryParameter("endorsementType");
            if (enableTab == 'CHANGE_POLICY_HOLDER_NAME') {
                $scope.isEnablePolicyHolderMode = true;
                $scope.isEnable = true;
                $scope.disableProposerSaveButton = true;
            } else if (enableTab == 'CHANGE_POLICY_HOLDER_CONTACT_DETAIL') {
                $scope.isEnableContactMode = true;
                $scope.isEnable = true;
                $scope.disableProposerSaveButton = true;
            } else {
                $scope.isEnablePlanMode = true;
                $scope.isEnable = false;
            }
            /*This scope holds the list of installments from which user can select one */
            $scope.numberOfInstallmentsDropDown = [];

            /*regex for number pattern for more details see commonModule.js*/
            $scope.numberPattern = globalConstants.numberPattern;

            $scope.fileSaved = null;
            $scope.disableUploadButton = false;

            /*This scope value is binded to fueluxWizard directive and hence it changes as and when next button is clicked*/
            $scope.selectedItem = 1;

            /*Holds the indicator for steps in which save button is clicked*/
            $scope.stepsSaved = stepsSaved;

            /*Inter id used for programmatic purpose*/
            $scope.endorsementId = getQueryParameter('endorsementId') || null;
            alert($scope.endorsementId);

            $scope.versionNumber = getQueryParameter('version') || null;

            /*actual quotation number to be used in the view*/
            $scope.endorsementNumber = endorsementNumber;
            // console.log(endorsementNumber);
            $scope.provinces = provinces;

            $scope.documentList = documentList;

            $scope.uploadDocumentFiles = function () {
                // console.log($scope.documentList.length);
                for (var i = 0; i < $scope.documentList.length; i++) {
                    var document = $scope.documentList[i];
                    var files = document.documentAttached;
                    // console.log(files);
                    if (files) {
                        $upload.upload({
                            url: '/pla/grouplife/endorsement/uploadmandatorydocument',
                            file: files,
                            fields: {documentId: document.documentId, endorsementId: $scope.endorsementId,mandatory:true},
                            method: 'POST'
                        }).progress(function(evt) {
                            console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
                        }).success(function (data, status, headers, config) {
                            console.log('file ' + config.file.name );
                            console.log(data);

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
                            url: '/pla/grouplife/endorsement/uploadmandatorydocument',
                            file: files,
                            fields: {documentId: document.documentId, endorsementId: $scope.endorsementId,mandatory:false},
                            method: 'POST'
                        }).progress(function (evt) {

                        }).success(function (data, status, headers, config) {
                            //console.log('file ' + config.file.name + 'uploaded. Response: ' +
                            // JSON.stringify(data));
                        });
                    }

                }
            };



            $scope.additionalDocumentList = [{}];
            $http.get("/pla/grouplife/endorsement/getadditionaldocuments/"+ $scope.endorsementId).success(function (data, status) {
                console.log(data);
                $scope.additionalDocumentList=data;
                $scope.checkDocumentAttached=$scope.additionalDocumentList!=null;

            });

            $scope.addAdditionalDocument = function () {
                $scope.additionalDocumentList.unshift({});
                $scope.checkDocumentAttached=$scope.isUploadEnabledForAdditionalDocument();

            };

            $scope.removeAdditionalDocument = function (index) {
                $scope.additionalDocumentList.splice(index, 1);
                $scope.checkDocumentAttached=$scope.isUploadEnabledForAdditionalDocument();
            };
            $scope.callAdditionalDoc = function(file){
                if(file[0]){
                    $scope.checkDocumentAttached=$scope.isUploadEnabledForAdditionalDocument();
                }
            }

            $scope.isUploadEnabledForAdditionalDocument = function(){
                var enableAdditionalUploadButton= ($scope.additionalDocumentList!=null);
                for (var i = 0; i < $scope.additionalDocumentList.length; i++) {
                    var document = $scope.additionalDocumentList[i];
                    var files = document.documentAttached;
                    //  alert(i+"--"+files)
                    //  alert(i+"--"+document.content);
                    if(!(files || document.content)){
                        enableAdditionalUploadButton=false;
                        break;
                    }
                }
                return enableAdditionalUploadButton;
            }

            $scope.submitAdditionalDocument = function(){
                $http.post('/pla/grouplife/endorsement/submit', angular.extend({},
                    {"endorsementId": $scope.endorsementId})).success(function (data) {
                    if (data.status == "200") {
                        saveStep();
                        $('#searchFormEndorsement').val($scope.endorsementId);
                        $('#searchForm').submit();
                    }

                });

            }
            $scope.saveProposerDetails = function () {
                $http.post("/pla/grouplife/endorsement/updatewithproposerdetail", angular.extend({},
                    {proposerDto: $scope.policyDetails.proposer},
                    {"endorsementId": $scope.endorsementId}))
                    .success(function (data) {
                        if (data.status == "200") {
                            saveStep();
                        }
                    });
            };

            $scope.savePlanDetails = function () {
                $upload.upload({
                    url: '/pla/grouplife/endorsement/uploadinsureddetail?endorsementId=' + $scope.endorsementId,
                    headers: {'Authorization': 'xxx'},
                    fields: $scope.policyDetails.plan,
                    file: $scope.fileSaved
                }).success(function (data, status, headers, config) {
                    if (data.status = "200") {
                        saveStep();
                       /* $http.get("/pla/grouphealth/proposal/getpremiumdetail/" + $scope.proposalId)
                            .success(function () {

                            })*/
                    }
                });
            };


            $scope.$watch('policyDetails.proposer.proposerName', function (newVal, oldVal) {
                //   console.log("****************************OLD VALUE------------>"+oldVal);
                //    console.log("****************************NEW VALUE------------>"+newVal);
                if (newVal) {
                  //  console.log(newVal);
                    $scope.disableProposerSaveButton = true;
                } else {
                    $scope.disableProposerSaveButton = false;
                }
            }, true);
            $scope.isSaveDisabled = function (formName) {
                return formName.$invalid ;
            };
            $scope.getCurrentVal=function(val){

                $scope.policyDetails.proposer.town='';

            }




            $scope.$watchGroup(['policyDetails.proposer.contactPersonName', 'policyDetails.proposer.contactPersonMobileNumber', 'policyDetails.proposer.contactPersonWorkPhoneNumber'], function (newValues, oldValues) {
                 if(newValues[0]){
                    $scope.disableProposerSaveButton = true;
                }else if(newValues[1]){
                    $scope.disableProposerSaveButton = true;
                }else if(newValues[2]){
                    $scope.disableProposerSaveButton = true;
                }else{
                    $scope.disableProposerSaveButton = false;
                }
                if(!newValues[0] || !newValues[1] || !newValues[2]){
                    $scope.disableProposerSaveButton = false;
                }

            });


            if ($scope.documentList) {
                if ($scope.documentList.documentAttached) {
                    if ($scope.documentList.documentAttached.length == $scope.documentList.documentName.length) {
                        $scope.disableUploadButton = true;
                    } else {
                        $scope.disableUploadButton = false;
                    }
                }
            }
            $http.get("/pla/grouplife/endorsement/getpolicydetail/" + $scope.endorsementId).success(function (data, status) {
                //  console.log(data);
                $scope.policyDetails.basicDetails = data;
                $scope.policyDetails.basicDetails.inceptionDate = moment(data.inceptionDate).format("DD/MM/YYYY");
                $scope.policyDetails.basicDetails.expiryDate = moment(data.expiryDate).format("DD/MM/YYYY");


            });

            $scope.policyDetails = {
                /*initialize with default values*/
                plan: {
                    samePlanForAllRelation: false,
                    samePlanForAllCategory: false
                },
                premium: {
                    addOnBenefit: 20,
                    profitAndSolvencyLoading: 0,
                    discounts: 0
                }
            };


            $scope.policyDetails.basic = agentDetails;

            $scope.policyDetails.premium = premiumData || {};

            $scope.changeAgent = false;
            console.log($scope.policyDetails.basic['active']);
            if (!$scope.policyDetails.basic['active']) {
                $('#agentModal').modal('show');
                $scope.changeAgent = true;
                $scope.stepsSaved["2"] = !$scope.changeAgent;
            }

            $scope.policyDetails.proposer = policyDetails;
            /*used for bs-dropdown*/
            $scope.dropdown = [
                {
                    "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                    "href": "/pla/grouplife/endorsement/downloadtemplatebyendorsementtype/" +  $scope.endorsementType + "/" + $scope.endorsementId
                }
            ];

             $scope.$watch('policyDetails.proposer.province', function (newVal, oldVal) {
             if (newVal) {
             $scope.getProvinceDetails(newVal);
             }
             });

            $scope.getProvinceDetails = function (provinceCode) {
                var provinceDetails = getProvinceAndCityDetail(provinces, provinceCode);
                if (provinceDetails) {
                    $scope.cities = provinceDetails.cities;
                }
            };
            $scope.accordionStatus = {
                contact: false,
                proposer: true
            };
            $scope.accordionStatusDocuments = {
                documents: true,
                additionalDocuments: false
            };
            $scope.$watch('fileSaved', function (n, o) {
                if (n && n.length) {
                    $scope.fileName = n[0].name
                }
            });
            $scope.openNewTab = function (event) {
                /*keyCode 9 is tab key*/
                if (event && event.keyCode == 9) {
                    $scope.accordionStatus.contact = true;
                    $scope.accordionStatus.proposer = false;
                }
            };

            /*clear all fields in the agent details except agentId*/
            $scope.clearAgentDetails = function () {
                angular.extend($scope.policyDetails.basic, {agentName: null, branchName: null, teamName: null});
            };


            function isInteger(x) {
                return Math.round(x) === x;
            }

            function generateListOfInstallments(numberOfInstallments) {
                $scope.numberOfInstallmentsDropDown = [];
                for (var installment = 1; installment <= numberOfInstallments; installment++) {
                    $scope.numberOfInstallmentsDropDown.push(installment);
                }
            }

            $scope.$watch('policyDetails.premium.policyTermValue', function (newVal, oldVal) {
                /*TODO check for the minimum amd maximum value for the policy term value*/
                if (newVal && newVal != 365 && newVal >= 30 && newVal <= 9999) {
                    /*used to toggle controls between dropdown and text*/
                    $scope.isPolicyTermNot365 = true;
                    /*used to show the error message when inappropriate value is entered*/
                    $scope.inappropriatePolicyTerm = false;
                } else {
                    console.log(' 2 ' + newVal);
                    if (newVal < 30 || newVal > 9999) {
                        $scope.inappropriatePolicyTerm = true;
                    } else {
                        $scope.inappropriatePolicyTerm = false;
                        $scope.isPolicyTermNot365 = false;
                    }
                }
            });

            $scope.selectedInstallment = premiumData.premiumInstallment;
            $scope.installments = $scope.policyDetails.premium.installments;
            $scope.disableSaveButton = false;

            $scope.setSelectedInstallment = function (selectedInstallment) {
                $scope.selectedInstallment = selectedInstallment;
                console.log('setSelectedInstallment ***');
            };


            var saveStep = function () {
                $scope.stepsSaved[$scope.selectedItem] = true;
            };


            $scope.back = function () {
                $window.location.href = 'openpolicysearchpage';
            }

        }])
    .config(['$dropdownProvider', function ($dropdownProvider) {
        angular.extend($dropdownProvider.defaults, {
            html: true
        });
    }])
    .config(["$routeProvider", function ($routeProvider) {
        var stepsSaved = {};
        var queryParam = null;
        $routeProvider.when('/', {
            templateUrl: 'createEndorsementTpl.html',
            controller: 'EndorsementCtrl',
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
                agentDetails: ['$q', '$http', 'getQueryParameter', function ($q, $http, getQueryParameter) {
                    queryParam = getQueryParameter('endorsementId');
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get('/pla/grouplife/endorsement/getagentdetailfrompolicy/' + queryParam).success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        stepsSaved["2"] = true;


                        return deferred.promise;
                    } else {
                        return {};
                    }
                }],
                policyDetails: ['$q', '$http', 'getQueryParameter', function ($q, $http, getQueryParameter) {
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get('/pla/grouplife/endorsement/getproposerdetail/' + queryParam).success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        stepsSaved["1"] = true;
                        stepsSaved["3"] = true;
                        stepsSaved["4"] = true;
                        return deferred.promise;
                    } else {
                        return {};
                    }
                }],
                endorsementNumber: ['$q', '$http', function ($q, $http) {
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get("/pla/grouplife/endorsement/getendorsementnumber/" + queryParam)
                            .success(function (response) {
                                deferred.resolve(response.id)
                            })
                            .error(function () {
                                deferred.reject();
                            });
                        return deferred.promise;
                    } else {
                        return 1;
                    }
                }],
                premiumData: ['$q', '$http', 'getQueryParameter', function ($q, $http, getQueryParameter) {
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get('/pla/grouplife/endorsement/getpremiumdetail/' + queryParam).success(function (response, status, headers, config) {


                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        stepsSaved["5"] = true;
                        return deferred.promise;
                    } else {
                        return false;
                    }
                }],
                documentList: ['$q', '$http', function ($q, $http) {
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get('/pla/grouplife/endorsement/getmandatorydocuments/' + queryParam).success(function (response, status, headers, config) {

                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        stepsSaved["6"] = true;

                        return deferred.promise;
                    } else {
                        return false;
                    }
                }],
                stepsSaved: function () {
                    return stepsSaved;
                }
            }
        })
    }]);
