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
                $scope.isEditMode = false;
            }
            $scope.isReturnStatus = false;

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
            var status = getQueryParameter("status");
            if (status == 'return') {
                $scope.isReturnStatus = true;

                $http.get("/pla/grouphealth/proposal/getapprovercomments/"+ $scope.proposalId).success(function (data, status) {
                    console.log(data);
                    $scope.approvalCommentList=data;
                });

            }
            var method = getQueryParameter("method");
            if (method == 'approval') {
                $scope.isViewMode = true;
                $scope.stepsSaved["1"] =true;


                $http.get("/pla/grouphealth/proposal/getapprovercomments/" + $scope.proposalId).success(function (data, status) {
                    // console.log(data);
                    $scope.approvalCommentList=data;
                });

            }
            $scope.isSaveDisabled = function (formName) {
                return formName.$invalid ;
              };
            $scope.getCurrentVal=function(val){

                $scope.proposalDetails.proposer.town='';

            }


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
                            fields: {documentId: document.documentId, proposalId: $scope.proposalId,mandatory:true},
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

            $scope.additionalDocumentList = [{}];
            $http.get("/pla/grouphealth/proposal/getadditionaldocuments/"+ $scope.proposalId).success(function (data, status) {
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
                console.log("enable value"+ enableAdditionalUploadButton);
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


            $scope.uploadAdditionalDocument = function () {
                for (var i = 0; i < $scope.additionalDocumentList.length; i++) {
                    var document = $scope.additionalDocumentList[i];
                    var files = document.documentAttached;
                    if (files) {
                        $upload.upload({
                            url: '/pla/grouphealth/proposal/uploadmandatorydocument',
                            file: files,
                            fields: {documentId: document.documentId, proposalId: $scope.proposalId,mandatory:false},
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
                    samePlanForAllCategory: false,
                    considerMoratoriumPeriod: false
                },
                premium: {
                    addOnBenefit: 20,
                    profitAndSolvencyLoading: 0,
                    discounts: 0
                }
            };


            $scope.proposalDetails.basic = agentDetails;

            $scope.proposalDetails.premium = premiumData || {}
            $scope.showDownload = true;

            $scope.changeAgent = false;
            console.log($scope.proposalDetails.basic['active']);
            if (!$scope.proposalDetails.basic['active'] && status != 'return' && method != 'approval' && mode!='view' ) {
                if(!$scope.isViewMode){
                    $('#agentModal').modal('show');
                }

                $scope.changeAgent = true;
                $scope.stepsSaved["1"] = !$scope.changeAgent;
               // alert("hi");
            }else if (!$scope.proposalDetails.basic['active'] && mode=='view' ) {

                $scope.stepsSaved["1"] = true;
                // alert("hi");
            }
            if(!$scope.proposalDetails.basic['active'] && $scope.isReturnStatus==true && method == 'approval' ){

                if(!$scope.isViewMode){
                    $('#agentModal').modal('show');
                }
               $scope.changeAgent = true;
                $scope.stepsSaved["2"] = !$scope.changeAgent;
            }
            console.log(' $scope.changeAgent ' + $scope.changeAgent);


            $scope.proposalDetails.proposer = proposerDetails;
            $scope.proposalDetails.plan.considerMoratoriumPeriod = proposerDetails.considerMoratoriumPeriod;
            $scope.proposalDetails.plan.samePlanForAllRelation=proposerDetails.samePlanForAllRelation;
            $scope.proposalDetails.plan.samePlanForAllCategory=proposerDetails.samePlanForAllCategory;
            if (mode == 'view') {
                $scope.dropdown = [
                    {
                        "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                        "href": "/pla/grouphealth/proposal/downloadinsuredtemplate/" + $scope.proposalId
                    }
                ];

            }/*else {
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
            }*/


            $scope.$watchCollection('[proposalId,showDownload]', function (n) {
                if (n[0]) {
                    $scope.qId = n[0];
                    //  console.log(n[0]);
                    // console.log(n[1]);
                    if (n[1]) {
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
                    } else {
                        $scope.dropdown = [
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Ready Reckoner</a>",
                                "href": "/pla/grouphealth/proposal/downloadplandetail/" + $scope.proposalId
                            },
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                                "href": "/pla/grouphealth/proposal/downloadinsuredtemplate/" + $scope.proposalId
                            },
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Error File</a>",
                                "href": "/pla/grouphealth/proposal/downloaderrorinsuredtemplate/" + $scope.proposalId
                            }
                        ];
                    }
                }
            });

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
            $scope.installment = $scope.proposalDetails.premium.installments;
            $scope.installments = _.sortBy($scope.installment, 'installmentNo');
            $scope.recalculatePremium = function () {
                $scope.premiumInstallment = {};
                $scope.proposalDetails.premium.premiumInstallment = $scope.selectedInstallment || null;
                $http.post('/pla/grouphealth/proposal/recalculatePremium', angular.extend({},
                    {premiumDetailDto: $scope.proposalDetails.premium},
                    {"proposalId": $scope.proposalId})).success(function (data) {
                    // console.log(data.data);
                    var enableSaveButton = $scope.proposalDetails.premium.optedPremiumFrequency!=null|| $scope.selectedInstallment!=null;
                    console.log("enableSaveButton--"+enableSaveButton);
                        $scope.disableSaveButton=false;
                    $scope.stepsSaved["4"]=$scope.disableSaveButton;
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
            $scope.submitComments = function(comment){
                $http.post('/pla/grouphealth/proposal/submit', angular.extend({},
                    {"proposalId": $scope.proposalId,comment:comment})).success(function (data) {
                    if (data.status == "200") {
                        saveStep();
                        $('#searchFormProposal').val($scope.proposalId);
                        $('#searchForm').submit();
                    }

                });

            }

            $scope.disableSaveButton=false;
            $scope.$watch( 'proposalDetails.premium.optedPremiumFrequency',function(newValue, oldValue){
                console.log("$scope.proposalDetails.premium.optedPremiumFrequency::"+$scope.proposalDetails.premium.optedPremiumFrequency);
                console.log("$scope.selectedInstallment::"+$scope.selectedInstallment);
                var enableSaveButton = $scope.proposalDetails.premium.optedPremiumFrequency!=null|| $scope.selectedInstallment!=null;
                console.log("enableSaveButton--"+enableSaveButton)
                if(newValue){
                    $scope.disableSaveButton=enableSaveButton;

                }else{
                    $scope.disableSaveButton=enableSaveButton;
                    if($scope.isReturnStatus == false){
                        $scope.stepsSaved["4"]=enableSaveButton;
                    }else{
                        $scope.stepsSaved["5"]=enableSaveButton;
                    }


                }
                if (method == 'approval') {
                    $scope.stepsSaved["5"]=true;
                }

            });
            $scope.comment='';
            $scope.approveProposal = function(){
                var request = angular.extend({comment: $scope.comment},
                    {"proposalId": $scope.proposalId});

                $http.post('/pla/grouphealth/proposal/approve', request).success(function (data) {
                    if(data.status=="200"){

                        $window.location.href="/pla/grouphealth/proposal/openapprovalproposal";

                    }

                });
            }

            $scope.returnProposal = function(){
                var request = angular.extend({comment: $scope.comment},{"proposalId": $scope.proposalId});

                $http.post('/pla/grouphealth/proposal/return', request).success(function (data) {
                    if(data.status=="200"){


                        $window.location.href="/pla/grouphealth/proposal/openapprovalproposal";

                    }

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
                    if(data.status== "200"){
                        $scope.stepsSaved["4"]=true;
                        saveStep();
                    }

                });

            };

            $scope.setSelectedInstallment = function (selectedInstallment) {
                $scope.selectedInstallment = selectedInstallment;
                $scope.proposalDetails.premium.optedPremiumFrequency=null;
                var enableSaveButton = $scope.proposalDetails.premium.optedPremiumFrequency!=null|| $scope.selectedInstallment!=null;
                console.log("enableSaveButton--"+enableSaveButton);
                    $scope.disableSaveButton=enableSaveButton;
               // $scope.stepsSaved["4"]=enableSaveButton;



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
                    url: '/pla/grouphealth/proposal/uploadinsureddetail?proposalId=' + $scope.proposalId,
                    headers: {'Authorization': 'xxx'},
                    fields: $scope.proposalDetails.plan,
                    file: $scope.fileSaved
                }).success(function (data, status, headers, config) {
                    if (data.status == "200") {
                        $scope.proposalId = data.id;
                        $timeout($scope.updatePremiumDetail($scope.proposalId), 500);

                        saveStep();
                        $scope.showDownload = true;
                    }else{
                        $scope.showDownload = false;
                    }
                });
            };
            $scope.updatePremiumDetail = function (proposalId) {
                $http.get("/pla/grouphealth/proposal/getpremiumdetail/" + proposalId)
                    .success(function (data) {
                        console.log('received data' + JSON.stringify(data));
                        $scope.proposalDetails.premium = data;
                        $scope.premiumData = data;
                        $scope.proposalDetails.premium.policyTermValue = data.policyTermValue;
                        $scope.proposalDetails.premium.profitAndSolvencyLoading = $scope.proposalDetails.premium.profitAndSolvencyLoading || 0;
                        $scope.proposalDetails.premium.discounts = $scope.proposalDetails.premium.discounts || 0;


                    });
            }

            $scope.back = function () {
                $window.location.href = 'listgrouphealthproposal';
            }
            $scope.backToApproverPortlet = function () {

                $window.location.href = 'openapprovalproposal';
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
