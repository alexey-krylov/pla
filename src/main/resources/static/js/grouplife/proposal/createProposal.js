angular.module('createProposal', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives',
    'angularFileUpload', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices'])
    .directive('modal', function () {
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
    })

    .directive('converterDecimal', function ($filter) {
        var FLOAT_REGEXP_1 = /^\$?\d+.(\d{3})*(\,\d*)$/; //Numbers like: 1.123,56
        var FLOAT_REGEXP_2 = /^\$?\d+,(\d{3})*(\.\d*)$/; //Numbers like: 1,123.56
        var FLOAT_REGEXP_3 = /^\$?\d+(\.\d*)?$/; //Numbers like: 1123.56
        var FLOAT_REGEXP_4 = /^\$?\d+(\,\d*)?$/; //Numbers like: 1123,56

        return {
            require: 'ngModel',
            link: function (scope, elm, attrs, ctrl) {
                ctrl.$parsers.unshift(function (viewValue) {
                    if (FLOAT_REGEXP_1.test(viewValue)) {
                        ctrl.$setValidity('float', true);
                        return parseFloat(viewValue.replace('.', '').replace(',', '.'));
                    } else if (FLOAT_REGEXP_2.test(viewValue)) {
                        ctrl.$setValidity('float', true);
                        return parseFloat(viewValue.replace(',', ''));
                    } else if (FLOAT_REGEXP_3.test(viewValue)) {
                        ctrl.$setValidity('float', true);
                        return parseFloat(viewValue);
                    } else if (FLOAT_REGEXP_4.test(viewValue)) {
                        ctrl.$setValidity('float', true);
                        return parseFloat(viewValue.replace(',', '.'));
                    }else {
                        ctrl.$setValidity('float', false);
                        return undefined;
                    }
                });

                ctrl.$formatters.unshift(
                    function (modelValue) {
                        return $filter('number')(parseFloat(modelValue) , 2);
                    }
                );
            }
        };
    })

    .controller('proposalCtrl', ['$scope', '$http', '$timeout', '$upload', 'provinces','industries', 'getProvinceAndCityDetail', 'globalConstants',
        'agentDetails', 'stepsSaved', 'proposerDetails', 'proposalNumber', 'getQueryParameter', '$window', 'premiumData', 'documentList',
        function ($scope, $http, $timeout, $upload, provinces,industries, getProvinceAndCityDetail, globalConstants, agentDetails, stepsSaved, proposerDetails, proposalNumber,
                  getQueryParameter, $window, premiumData, documentList) {

            var mode = getQueryParameter("mode");
            $scope.showModal = false;

            if (mode == 'view') {
                $scope.isViewMode = true;
                $scope.isEditMode = true;
            } else if (mode == 'edit') {
                $scope.isEditMode = false;
            }
            $scope.isReturnStatus = false;

            /*This scope holds the list of installments from which user can select one */
            $scope.numberOfInstallmentsDropDown = [];
            $scope.industries = industries;

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

                $http.get("/pla/grouplife/proposal/getapprovercomments/"+ $scope.proposalId).success(function (data, status) {
                    console.log(data);
                    $scope.approvalCommentList=data;
                });

            }
            var method = getQueryParameter("method");
            if (method == 'approval') {
                $scope.isViewMode = true;
                $scope.stepsSaved["1"] =true;
                $http.get("/pla/grouplife/proposal/getapprovercomments/" + $scope.proposalId).success(function (data, status) {
                    // console.log(data);
                    $scope.approvalCommentList=data;
                });

            }


            if (status == 'return') {
                $scope.stepsSaved["1"] =true;
            }

            $scope.toggleSelection=function($event){
                var checkbox = $event.target;
                if(!checkbox.checked){
                    $scope.proposalDetails.basic.agentCommissionPercentage='';
                }
            }
            $scope.uploadDocumentFiles = function () {
                // console.log($scope.documentList.length);
                for (var i = 0; i < $scope.documentList.length; i++) {
                    var document = $scope.documentList[i];
                    var files = document.documentAttached;
                    // console.log(files);
                    if (files) {
                        $upload.upload({
                            url: '/pla/grouplife/proposal/uploadmandatorydocument',
                            file: files,
                            fields: {documentId: document.documentId, proposalId: $scope.proposalId,mandatory:true},
                            method: 'POST'
                        }).progress(function (evt) {

                        }).success(function (data, status, headers, config) {
                            //console.log('file ' + config.file.name + 'uploaded. Response: ' +
                            // JSON.stringify(data));
                        });
                    }

                }

            };
            $scope.isSaveDisabled = function (formName) {
                return formName.$invalid ;
            };
            $scope.getCurrentVal=function(val){

                $scope.proposalDetails.proposer.town='';

            }

            $scope.additionalDocumentList = [{}];
            $http.get("/pla/grouplife/proposal/getadditionaldocuments/"+ $scope.proposalId).success(function (data, status) {
                console.log(data);
                $scope.additionalDocumentList=data;
                $scope.checkDocumentAttached=$scope.additionalDocumentList!=null;

            });

            $scope.addAdditionalDocument = function () {
                $scope.additionalDocumentList.unshift({});
                $scope.checkDocumentAttached=$scope.isUploadEnabledForAdditionalDocument();

            };

            $scope.removeAdditionalDocument = function (index,gridFsDocId) {
                if(gridFsDocId) {
                    $http.post("/pla/grouplife/proposal/removeGLProposalAdditionalDocument?proposalId=" + $scope.proposalId + "&gridFsDocId=" + gridFsDocId).success(function (data, status) {
                        console.log(data);
                        if (data.status == '200') {
                            $scope.additionalDocumentList.splice(index, 1);
                            $scope.checkDocumentAttached=$scope.isUploadEnabledForAdditionalDocument();

                        }
                    });
                }else{
                    $scope.additionalDocumentList.splice(index, 1);
                    $scope.checkDocumentAttached=$scope.isUploadEnabledForAdditionalDocument();

                }


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
                   // alert(i+"--"+document.content);
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
                    $scope.additional=true;
                    if (files) {
                        $upload.upload({
                            url: '/pla/grouplife/proposal/uploadmandatorydocument',
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

            $scope.documentNameLengthTest=function(){
                var enableUploadButton= true;
                for(var i=0;i < $scope.documentList.length;i++){
                    if($scope.documentList[i].documentAttached != null && $scope.documentList[i].documentAttached[0] != null){
                        if($scope.documentList[i].documentAttached[0].name.length >100)
                        {
                            enableUploadButton=false;
                            break;
                        }
                    }
                }
                if(enableUploadButton){
                    return true;
                }
                else
                {
                    return false
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
            $scope.showDownload = true;


            $scope.changeAgent = false;
            console.log($scope.proposalDetails.basic['active']);
            if (!$scope.proposalDetails.basic['active'] && status != 'return' && method != 'approval' && mode!='view') {
                if(!$scope.isViewMode){
                    $('#agentModal').modal('show');
                }
                $scope.changeAgent = true;
                $scope.stepsSaved["1"] = !$scope.changeAgent;
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
            $scope.proposalDetails.plan.samePlanForAllRelation=proposerDetails.samePlanForAllRelation;
            $scope.proposalDetails.plan.samePlanForAllCategory=proposerDetails.samePlanForAllCategory;
            if (mode == 'view') {
                $scope.dropdown = [
                    {
                        "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                        "href": "/pla/grouplife/proposal/downloadinsuredtemplate/" + $scope.proposalId
                    }
                ];

            }/*else {
             $scope.dropdown = [
                    {
                        "text": "<a><img src=\"/pla/images/xls-icon.png\">Ready Reckoner</a>",
                        "href": "/pla/grouplife/proposal/downloadplandetail/" + $scope.proposalId
                    },
                    {
                        "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                        "href": "/pla/grouplife/proposal/downloadinsuredtemplate/" + $scope.proposalId
                    }
                ];
            }*/
            $scope.$watchCollection('[proposalId,showDownload]', function (n) {
                if (n[0]) {
                    $scope.pId = n[0];
                      console.log(n[0]);
                    console.log(n[1]);
               //     alert($scope.showDownload);
                    if (n[1]) {
                        $scope.dropdown = [
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Ready Reckoner</a>",
                                "href": "/pla/grouplife/proposal/downloadplandetail/" + $scope.pId
                            },
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                                "href": "/pla/grouplife/proposal/downloadinsuredtemplate/" + $scope.pId
                            }
                        ];
                    } else {
                        $scope.dropdown = [
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Ready Reckoner</a>",
                                "href": "/pla/grouplife/proposal/downloadplandetail/" + $scope.pId
                            },
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                                "href": "/pla/grouplife/proposal/downloadinsuredtemplate/" + $scope.pId
                            },
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Error File</a>",
                                "href": "/pla/grouplife/proposal/downloaderrorinsuredtemplate/" + $scope.pId
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
                console.log(' 1 ' + newVal);

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
                $http.post('/pla/grouplife/proposal/recalculatePremium', angular.extend({},
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
               $http.post('/pla/grouplife/proposal/submit', angular.extend({},
                    {"proposalId": $scope.proposalId})).success(function (data) {
                   if (data.status == "200") {
                       saveStep();
                       $('#searchFormProposal').val($scope.proposalId);
                       $('#searchForm').submit();
                   }

                });

            }
            $scope.submitComments = function(comment){
                $http.post('/pla/grouplife/proposal/submit', angular.extend({},
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
                var enableSaveButton = $scope.proposalDetails.premium.optedPremiumFrequency!=null || $scope.selectedInstallment!=null;
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

            $scope.approveProposal = function(){
                var request = angular.extend({comment: $scope.comment},
                    {"proposalId": $scope.proposalId});

                $http.post('/pla/grouplife/proposal/approve', request).success(function (data) {
                    if(data.status==200){

                        $window.location.href="/pla/grouplife/proposal/openapprovalproposal";

                    }

                });
            }
            $scope.comment='';
            $scope.returnProposal = function(){
                var request = angular.extend({comment: $scope.comment},{"proposalId": $scope.proposalId});

                $http.post('/pla/grouplife/proposal/return', request).success(function (data) {
                    if(data.status==200){


                        $window.location.href="/pla/grouplife/proposal/openapprovalproposal";

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
                $http.post('/pla/grouplife/proposal/savepremiumdetail', request).success(function (data) {
                    /*$http.post("/pla/grouphealth/proposal/submit", angular.extend({},
                     {"proposalId": $scope.proposalId}))
                     .success(function (data) {
                     });*/
                   // console.log(data);
                  //  console.log("*******************"+$scope.selectedItem);
                    if(data.status== '200'){
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
                $http.get("/pla/grouplife/proposal/getproposalNumber/" + proposalId)
                    .success(function (data, status) {
                        $scope.proposalNumber = data.id;
                    });
                $http.get("/pla/grouplife/proposal/getversionnumber/" + proposalId)
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

                $http.post("/pla/grouplife/proposal/updatewithagentdetail", angular.extend($scope.proposalDetails.basic, {
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
                $http.post("/pla/grouplife/proposal/updatewithproposerdetail", angular.extend({},
                    {proposerDto: $scope.proposalDetails.proposer},
                    {"proposalId": $scope.proposalId}))
                    .success(function (data) {
                        if (data.status == "200") {
                            saveStep();
                        }
                    });
            };
            $scope.proceedToNext = function () {
                saveStep();
                $scope.showModal = false;


            }
            $scope.errorMessage='';
            $scope.$watch('selectedItem', function (newVal, oldVal) {
                //  console.log("STEP"+newVal);
                //  console.log(!$scope.stepsSaved[newVal]);
                if (mode == 'view' && status == 'return') {
                    if (newVal == 4) {

                        $http.get("/pla/grouplife/proposal/isValidPremiumAndPersons/" + $scope.proposalId)
                            .success(function (response) {
                                console.log(response);
                                // $scope.validateGLQuotation = data;
                                if (response.data) {
                                    $scope.showModal = true;
                                    $scope.errorMessage = response.message;

                                }
                            });
                    }
                }else if(mode == 'edit' && status == 'return'){
                    if (newVal == 4) {

                        $http.get("/pla/grouplife/proposal/isValidPremiumAndPersons/" + $scope.proposalId)
                            .success(function (response) {
                                console.log(response);
                                // $scope.validateGLQuotation = data;
                                if (response.data) {
                                    $scope.showModal = true;
                                    $scope.errorMessage = response.message;

                                }
                            });
                    }

                } else if (newVal == 3) {
                    $http.get("/pla/grouplife/proposal/isValidPremiumAndPersons/" + $scope.proposalId)
                        .success(function (response) {
                            console.log(response);
                            // $scope.validateGLQuotation = data;
                            if (response.data) {
                                $scope.showModal = true;
                                $scope.errorMessage=response.message;

                            }
                        });
                }
            });


            $scope.savePlanDetails = function () {
                $upload.upload({
                    url: '/pla/grouplife/proposal/uploadinsureddetail?proposalId=' + $scope.proposalId,
                    headers: {'Authorization': 'xxx'},
                    fields: $scope.proposalDetails.plan,
                    file: $scope.fileSaved
                }).success(function (data, status, headers, config) {
                  //  console.log(data.status);
                    if (data.status == "200") {
                        if (data.id) {
                            $scope.proposalId = data.id;
                            $timeout($scope.updatePremiumDetail($scope.proposalId), 500);
                            $http.get("/pla/grouplife/proposal/isValidPremiumAndPersons/" + $scope.proposalId)
                                .success(function (response) {
                                    console.log(response);
                                    // $scope.validateGLQuotation = data;
                                    if (response.data) {
                                        $scope.showModal = true;
                                        $scope.errorMessage=response.message;

                                    } else {
                                        saveStep();

                                    }

                                });

                        }
                    } else{
                        if(data.data){
                            $scope.showDownload = false;

                        }else{
                            $scope.showDownload = true;
                        }
                    }

                });
            };
            $scope.updatePremiumDetail = function (proposalId) {
                $http.get("/pla/grouplife/proposal/getpremiumdetail/" + proposalId)
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
                $window.location.href = 'listgrouplifeproposal';
            }

            $scope.backToApproverPortlet = function () {
                //$window.location.href = 'viewApprovalProposal';
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
                industries: ['$q', '$http', function ($q, $http) {
                    var deferred = $q.defer();
                    $http.get('/pla/core/master/getindustry').success(function (response, status, headers, config) {
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
                        $http.get('/pla/grouplife/proposal/getagentdetailfromproposal/' + queryParam).success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        var status =getQueryParameter('status');
                       // console.log("**************RETURN STATUS********************");
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
                        $http.get('/pla/grouplife/proposal/getproposerdetail/' + queryParam).success(function (response, status, headers, config) {
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
                        $http.get("/pla/grouplife/proposal/getproposalnumber/" + queryParam)
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
                        $http.get('/pla/grouplife/proposal/getpremiumdetail/' + queryParam).success(function (response, status, headers, config) {
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
                        $http.get('/pla/grouplife/proposal/getmandatorydocuments/' + queryParam).success(function (response, status, headers, config) {
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
