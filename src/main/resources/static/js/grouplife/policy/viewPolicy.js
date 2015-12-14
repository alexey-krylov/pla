angular.module('viewPolicy', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives',
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
                    } else {
                        ctrl.$setValidity('float', false);
                        return undefined;
                    }
                });

                ctrl.$formatters.unshift(
                    function (modelValue) {
                        return $filter('number')(parseFloat(modelValue), 2);
                    }
                );
            }
        };
    })

    .controller('policyCtrl', ['$scope', '$http', '$timeout', '$upload', 'provinces', 'industries', 'getProvinceAndCityDetail', 'globalConstants',
        'agentDetails', 'stepsSaved', 'policyDetails', 'policyNumber', 'getQueryParameter', '$window', 'premiumData', 'documentList',
        function ($scope, $http, $timeout, $upload, provinces, industries, getProvinceAndCityDetail, globalConstants, agentDetails, stepsSaved, policyDetails, policyNumber,
                  getQueryParameter, $window, premiumData, documentList) {

            var mode = getQueryParameter("mode");
            if (mode == 'view') {
                $scope.isViewMode = true;

            }
            $scope.showModal = false;

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
            $scope.policyId = getQueryParameter('policyId') || null;

            $scope.versionNumber = getQueryParameter('version') || null;

            /*actual quotation number to be used in the view*/
            console.log(policyNumber);
            $scope.policyNumber = policyNumber;

            $scope.provinces = provinces;

            $scope.documentList = documentList;

            $scope.industries = industries;
            $scope.additionalDocumentList = [{}];
            $http.get("/pla/grouplife/policy/getadditionaldocuments/" + $scope.policyId).success(function (data, status) {
                console.log(data);
                $scope.additionalDocumentList = data;
                //   $scope.checkDocumentAttached=$scope.additionalDocumentList!=null;

            });

            $scope.uploadDocumentFiles = function () {
                // //console.log($scope.documentList.length);
                for (var i = 0; i < $scope.documentList.length; i++) {
                    var document = $scope.documentList[i];
                    var files = document.documentAttached;
                    //console.dir(files);
                    // //alert(files.name);
                    if (files) {
                        //console.log('File Uploading....');
                        $upload.upload({
                            url: '/pla/grouplife/policy/uploadmandatorydocument',
                            file: files,
                            fields: {
                                documentId: document.documentId,
                                policyId: $scope.policyId,
                                mandatory: true,
                                isApproved: true
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
            $scope.proceedToNext = function () {
                saveStep();
                $scope.showModal = false;


            }
            $scope.errorMessage = '';
            $scope.$watch('selectedItem', function (newVal, oldVal) {
                //  console.log("STEP"+newVal);
                //  console.log(!$scope.stepsSaved[newVal]);
                if (newVal == 4) {
                    $http.get("/pla/grouplife/policy/getproposalid/" + $scope.policyId).success(function (data, status) {
                        //console.log(data);
                        $scope.proposalId = data.proposalId;
                        $http.get("/pla/grouplife/proposal/isValidPremiumAndPersons/" + $scope.proposalId)
                            .success(function (response) {
                                console.log(response);
                                // $scope.validateGLQuotation = data;
                                if (response.data) {
                                    $scope.showModal = true;
                                    $scope.errorMessage = response.message;

                                }
                            });
                    });
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
                            url: '/pla/grouplife/policy/uploadmandatorydocument',
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

            $scope.addAdditionalDocument = function () {
                $scope.additionalDocumentList.unshift({});
            };

            $scope.removeAdditionalDocument = function (index) {
                $scope.additionalDocumentList.splice(index, 1);
            };

            $scope.callAdditionalDoc = function (file) {
                if (file[0]) {
                    $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
                }
            }

            $scope.isBrowseDisable = function (document) {
                if (document.fileName == null && document.submitted) {
                    return true;
                }
                else {
                    return false;
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

            if ($scope.documentList) {
                if ($scope.documentList.documentAttached) {
                    if ($scope.documentList.documentAttached.length == $scope.documentList.documentName.length) {
                        $scope.disableUploadButton = true;
                    } else {
                        $scope.disableUploadButton = false;
                    }
                }
            }
            $http.get("/pla/grouplife/policy/getpolicydetail/" + $scope.policyId).success(function (data, status) {
                //  console.log(data);
                $scope.policyDetails.basicDetails = data;
                $scope.policyDetails.basicDetails.inceptionDate = moment(data.inceptionDate).format("DD/MM/YYYY");
                $scope.policyDetails.basicDetails.expiryDate = moment(data.expiryDate).format("DD/MM/YYYY");


            });

            $http.get("/pla/grouplife/policy/getproposalid/" + $scope.policyId).success(function (data, status) {
                //console.log(data);
                $scope.proposalId = data.proposalId;
                console.log('ProposalIDRCV' + JSON.stringify($scope.proposalId));
                $http.get("/pla/grouplife/proposal/getapprovercomments/" + $scope.proposalId).success(function (data, status) {
                    //  console.log(data);
                    $scope.approvalCommentList = data;
                });
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
                // $('#agentModal').modal('show');
                // $scope.changeAgent = true;
                //$scope.stepsSaved["2"] = !$scope.changeAgent;
                $scope.stepsSaved["2"] = true;

            }

            $scope.policyDetails.proposer = policyDetails;
            $scope.policyDetails.plan.samePlanForAllRelation = policyDetails.samePlanForAllRelation;
            $scope.policyDetails.plan.samePlanForAllCategory = policyDetails.samePlanForAllCategory;

            if (policyDetails) {
                if (policyDetails.contactPersonDetail) {
                    $scope.contactPersonDetail = policyDetails.contactPersonDetail;
                }
            }

            /*used for bs-dropdown*/
            $scope.dropdown = [
                {
                    "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                    "href": "/pla/grouplife/policy/downloadinsuredtemplate/" + $scope.policyId
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
            templateUrl: 'viewPolicyTpl.html',
            controller: 'policyCtrl',
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
                    queryParam = getQueryParameter('policyId');
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get('/pla/grouplife/policy/getagentdetailfrompolicy/' + queryParam).success(function (response, status, headers, config) {
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
                        $http.get('/pla/grouplife/policy/getproposerdetail/' + queryParam).success(function (response, status, headers, config) {
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
                policyNumber: ['$q', '$http', function ($q, $http) {
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get("/pla/grouplife/policy/getpolicynumber/" + queryParam)
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
                        $http.get('/pla/grouplife/policy/getpremiumdetail/' + queryParam).success(function (response, status, headers, config) {


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
                        $http.get('/pla/grouplife/policy/getmandatorydocuments/' + queryParam).success(function (response, status, headers, config) {

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
