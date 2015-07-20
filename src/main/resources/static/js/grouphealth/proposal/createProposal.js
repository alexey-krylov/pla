angular.module('createProposal', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives',
    'angularFileUpload', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices'])

    .controller('proposalCtrl', ['$scope', '$http', '$timeout', '$upload', 'provinces', 'getProvinceAndCityDetail', 'globalConstants',
        'agentDetails', 'stepsSaved', 'proposerDetails', 'proposalNumber', 'getQueryParameter', '$window', 'premiumData', 'documentList',
        function ($scope, $http, $timeout, $upload, provinces, getProvinceAndCityDetail, globalConstants, agentDetails, stepsSaved, proposerDetails, proposalNumber,
                  getQueryParameter, $window, premiumData, documentList) {

            var mode = getQueryParameter("mode");
            if (mode == 'view') {
                $scope.isViewMode = true;
                $scope.isEditMode = true;
            } else if (mode == 'edit') {
                $scope.isEditMode = true;
            }
            $scope.isReturnStatus = false;
            var status = getQueryParameter("status");
            if (status == 'return') {
                $scope.isReturnStatus = true;

                $http.get("/pla/grouphealth/proposal/getapprovercomments").success(function (data, status) {
                    console.log(data);
                    $scope.approvalCommentList=data;
                    });

            }
            var method = getQueryParameter("method");
            if (method == 'approval') {
                $scope.isViewMode = true;

                $http.get("/pla/grouphealth/proposal/getapprovercomments").success(function (data, status) {
                   // console.log(data);
                    $scope.approvalCommentList=data;
                });

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
            $scope.proposalId = getQueryParameter('proposalId') || null;

            $scope.versionNumber = getQueryParameter('version') || null;

            /*actual quotation number to be used in the view*/
            $scope.proposalNumber = proposalNumber;

            $scope.provinces = provinces;

            $scope.documentList = documentList;

            if (status == 'return') {
                $scope.stepsSaved["1"] =true;
            }
            $scope.uploadDocumentFiles = function () {
                // console.log($scope.documentList.length);
                for (var i = 0; i < $scope.documentList.length; i++) {
                    var document = $scope.documentList[i];
                    var files = document.documentAttached;
                    // console.log(files);
                    if (files) {
                        $upload.upload({
                            url: '/pla/grouphealth/proposal/uploadmandatorydocument',
                            file: files,
                            fields: {documentId: document.documentId, proposalId: $scope.proposalId},
                            method: 'POST'
                        }).progress(function (evt) {

                        }).success(function (data, status, headers, config) {
                            //console.log('file ' + config.file.name + 'uploaded. Response: ' +
                            // JSON.stringify(data));
                        });
                    }

                }

            };

            $scope.additionalDocumentList = [{}, {}];

            $scope.addAdditionalDocument = function () {
                $scope.additionalDocumentList.unshift({});
            };

            $scope.removeAdditionalDocument = function (index) {
                $scope.additionalDocumentList.splice(index, 1);
            };

            $scope.uploadAdditionalDocument = function () {
                for (var i = 0; i < $scope.additionalDocumentList.length; i++) {
                    var document = $scope.additionalDocumentList[i];
                    var files = document.documentAttached;
                    if (files) {
                        $upload.upload({
                            url: '/pla/grouphealth/proposal/uploadmandatorydocument',
                            file: files,
                            fields: {documentId: document.documentName, proposalId: $scope.proposalId},
                            method: 'POST'
                        }).progress(function (evt) {

                        }).success(function (data, status, headers, config) {
                            //console.log('file ' + config.file.name + 'uploaded. Response: ' +
                            // JSON.stringify(data));
                        });
                    }

                }
            };

            if ($scope.documentList) {
                if ($scope.documentList.documentAttached) {
                    if ($scope.documentList.documentAttached.length == $scope.documentList.documentName.length) {
                        $scope.disableUploadButton = true;
                        console.log($scope.documentList.documentAttached.length);
                        console.log($scope.documentList.documentName.length);
                    } else {
                        $scope.disableUploadButton = false;
                    }
                }
            }
            /* $scope.$watch('documentList.documentAttached',function(newVal,oldVal){
             if(newVal && newVal.length){
             $scope.selectedFiles.push(newVal);
             console.log($scope.selectedFiles.length);
             console.log($scope.documentList.length);
             if($scope.selectedFiles.length == $scope.documentList.length){
             $scope.disableUploadButton=true;
             }else{
             $scope.disableUploadButton=false;
             }
             }
             });*/


            $scope.proposalDetails = {
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


            $scope.proposalDetails.basic = agentDetails;

            $scope.proposalDetails.premium = premiumData || {};

            $scope.changeAgent = false;
            console.log($scope.proposalDetails.basic['active']);
            if (!$scope.proposalDetails.basic['active']) {
                $('#agentModal').modal('show');
                $scope.changeAgent = true;
                $scope.stepsSaved["1"] = !$scope.changeAgent;
            }
            if(!$scope.proposalDetails.basic['active'] && $scope.isReturnStatus==true && method == 'approval' ){
                $('#agentModal').modal('show');
                $scope.changeAgent = true;
                $scope.stepsSaved["2"] = !$scope.changeAgent;
            }
            console.log(' $scope.changeAgent ' + $scope.changeAgent);


            $scope.proposalDetails.proposer = proposerDetails;
            /*used for bs-dropdown*/
            $scope.dropdown = [
                {
                    "text": "<a><img src=\"/pla/images/xls-icon.png\">Ready Reckoner</a>",
                    "href": "/pla/grouphealth/proposal/downloadplandetail/" + $scope.proposalId
                },
                {
                    "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                    "href": "/pla/grouphealth/proposal/downloadinsuredtemplate/" + $scope.proposalId
                }
            ];

            $scope.$watch('proposalDetails.proposer.province', function (newVal, oldVal) {
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
                angular.extend($scope.proposalDetails.basic, {agentName: null, branchName: null, teamName: null});
            };

            $scope.isSaveDisabled = function (formName) {
                //return formName.$invalid || ($scope.stepsSaved[$scope.selectedItem] && !$scope.isEditMode)
            };

            $scope.searchAgent = function () {
                $http.get("/pla/quotation/grouplife/getagentdetail/" + $scope.proposalDetails.basic.agentId)
                    .success(function (data, status) {
                        if (data.status == "200") {
                            $scope.agentNotFound = false;
                            $scope.proposalDetails.basic = data.data;
                        } else {
                            $scope.agentNotFound = true;
                        }
                    })
                    .error(function (data, status) {

                    });
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

            $scope.$watch('proposalDetails.premium.policyTermValue', function (newVal, oldVal) {
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
            $scope.installments = $scope.proposalDetails.premium.installments;
            $scope.recalculatePremium = function () {
                $scope.premiumInstallment = {};
                $scope.proposalDetails.premium.premiumInstallment = $scope.selectedInstallment || null;
                $http.post('/pla/grouphealth/proposal/recalculatePremium', angular.extend({},
                    {premiumDetailDto: $scope.proposalDetails.premium},
                    {"proposalId": $scope.proposalId})).success(function (data) {
                    // console.log(data.data);
                    $scope.proposalDetails.premium = data.data;
                    $scope.proposalDetails.premium.totalPremium = data.data.totalPremium;
                    if (data.data.annualPremium) {
                        $scope.proposalDetails.premium.annualPremium = data.data.annualPremium;
                        $scope.proposalDetails.premium.semiannualPremium = data.data.semiannualPremium;
                        $scope.proposalDetails.premium.quarterlyPremium = data.data.quarterlyPremium;
                        $scope.proposalDetails.premium.monthlyPremium = data.data.monthlyPremium;
                    }
                    if (data.data.installments) {
                        $scope.selectedInstallment = null;
                        $scope.installments = _.sortBy(data.data.installments, 'installmentNo');
                    }
                });

            };

            $scope.submitAdditionalDocument = function(){
               $http.post('/pla/grouphealth/proposal/submit', angular.extend({},
                    {"proposalId": $scope.proposalId})).success(function (data) {
                   if (data.status == "200") {
                       saveStep();
                       $('#searchFormProposal').val($scope.proposalId);
                       $('#searchForm').submit();
                   }

                });

            }
            $scope.disableSaveButton=false;
            $scope.$watch( 'proposalDetails.premium.optedPremiumFrequency',function(newValue, oldValue){
                if(newValue){
                    $scope.disableSaveButton=true;
                }else{
                    $scope.disableSaveButton=false;
                    if($scope.isReturnStatus == false){
                        $scope.stepsSaved["4"]=false;
                    }else{
                        $scope.stepsSaved["5"]=false;
                    }


                }
                if (method == 'approval') {
                    $scope.stepsSaved["5"]=true;
                }

            });

            $scope.approveProposal = function(){
                var request = angular.extend({comment: $scope.comment},
                    {"proposalId": $scope.proposalId});

                $http.post('/pla/grouphealth/proposal/approve', request).success(function (data) {


                });
            }
            $scope.comment='';
            $scope.returnProposal = function(){
                var request = angular.extend({comment: $scope.comment},{"proposalId": $scope.proposalId});

                $http.post('/pla/grouphealth/proposal/return', request).success(function (data) {


                });
            }


            $scope.savePremiumDetails = function () {
                console.log("$scope.selectedInstallment " + JSON.stringify($scope.selectedInstallment));
                var premiumDetailDto = $scope.proposalDetails.premium;
                var request = angular.extend({premiumDetailDto: $scope.proposalDetails.premium},
                    {"proposalId": $scope.proposalId});
                request.premiumDetailDto["premiumInstallment"] = $scope.selectedInstallment;
                console.log(JSON.stringify(request.premiumDetailDto));
                $http.post('/pla/grouphealth/proposal/savepremiumdetail', request).success(function (data) {
                    /*$http.post("/pla/grouphealth/proposal/submit", angular.extend({},
                     {"proposalId": $scope.proposalId}))
                     .success(function (data) {
                     });*/
                   // console.log(data);
                  //  console.log("*******************"+$scope.selectedItem);
                    if(data.status== '200'){
                        saveStep();
                    }

                });

            };

            $scope.setSelectedInstallment = function (selectedInstallment) {
                $scope.selectedInstallment = selectedInstallment;
                console.log('setSelectedInstallment ***');
            };

            var setproposalNumberAndVersionNumber = function (proposalId) {
                $http.get("/pla/grouphealth/proposal/getproposalNumber/" + proposalId)
                    .success(function (data, status) {
                        $scope.proposalNumber = data.id;
                    });
                $http.get("/pla/grouphealth/proposal/getversionnumber/" + proposalId)
                    .success(function (data, status) {
                        $scope.versionNumber = data.id;
                    });
            };
            /*
             $scope.$on('actionclicked.fu.wizard', function (newval, oldval) {

             if (data.step == 1) {
             $http.post("/pla/grouphealth/proposal/", angular.extend($scope.proposalDetails.basic,
             {proposerName: $scope.proposalDetails.proposer.proposerName}))
             .success(function (agentDetails) {
             });
             }
             });*/

            $scope.saveBasicDetails = function () {

                $http.post("/pla/grouphealth/proposal/updatewithagentdetail", angular.extend($scope.proposalDetails.basic, {
                    proposerName: $scope.proposalDetails.proposer.proposerName,
                    proposalId: $scope.proposalId
                }))
                    .success(function (agentDetails) {
                        if (agentDetails.status == "200") {
                            $scope.proposalId = agentDetails.id;
                            setproposalNumberAndVersionNumber(agentDetails.id);
                            saveStep();
                        }
                    });
            };

            var saveStep = function () {
                $scope.stepsSaved[$scope.selectedItem] = true;
            };

            $scope.saveProposerDetails = function () {
                $http.post("/pla/grouphealth/proposal/updatewithproposerdetail", angular.extend({},
                    {proposerDto: $scope.proposalDetails.proposer},
                    {"proposalId": $scope.proposalId}))
                    .success(function (data) {
                        if (data.status == "200") {
                            saveStep();
                        }
                    });
            };

            $scope.savePlanDetails = function () {
                $upload.upload({
                    url: '/pla/grouphealth/proposal/uploadinsureddetail',
                    headers: {'Authorization': 'xxx'},
                    fields: $scope.proposalDetails.plan,
                    file: $scope.fileSaved
                }).success(function (data, status, headers, config) {
                    if (data.status = "200") {
                        saveStep();
                        $http.get("/pla/grouphealth/proposal/getpremiumdetail/" + $scope.proposalId)
                            .success(function () {

                            })
                    }
                });
            };

            $scope.back = function () {
                $window.location.href = 'listgrouphealthproposal';
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
            templateUrl: 'createProposalTpl.html',
            controller: 'proposalCtrl',
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
                    queryParam = getQueryParameter('proposalId');
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get('/pla/grouphealth/proposal/getagentdetailfromproposal/' + queryParam).success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        var status =getQueryParameter('status');
                        console.log("**************RETURN STATUS********************");
                       console.log(status);
                        if(status == 'return'){
                            stepsSaved["2"] = true;
                            stepsSaved["4"] = true;
                        }else{
                            stepsSaved["1"] = true;
                            stepsSaved["3"] = true;
                        }

                        return deferred.promise;
                    } else {
                        return {};
                    }
                }],
                proposerDetails: ['$q', '$http', 'getQueryParameter', function ($q, $http,getQueryParameter) {
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get('/pla/grouphealth/proposal/getproposerdetail/' + queryParam).success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        var status =getQueryParameter('status');
                        if(status == 'return'){
                            stepsSaved["3"] = true;
                        }else{
                            stepsSaved["2"] = true;
                        }

                        return deferred.promise;
                    } else {
                        return {};
                    }
                }],
                proposalNumber: ['$q', '$http', function ($q, $http) {
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get("/pla/grouphealth/proposal/getproposalnumber/" + queryParam)
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
                premiumData: ['$q', '$http','getQueryParameter', function ($q, $http,getQueryParameter) {
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get('/pla/grouphealth/proposal/getpremiumdetail/' + queryParam).success(function (response, status, headers, config) {
                            var status =getQueryParameter('status');
                            if(status == 'return'){
                                stepsSaved["5"] = true;
                            }else{
                                stepsSaved["4"] = true;
                            }

                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        return deferred.promise;
                    } else {
                        return false;
                    }
                }],
                documentList: ['$q', '$http', function ($q, $http) {
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get('/pla/grouphealth/proposal/getmandatorydocuments/' + queryParam).success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
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
