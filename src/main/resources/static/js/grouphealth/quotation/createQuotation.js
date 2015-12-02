angular.module('createQuotation', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives', 'angularFileUpload',
    'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices'])

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

    .controller('quotationCtrl', ['$scope', '$http', '$timeout', '$location', '$route', '$upload', 'provinces', 'getProvinceAndCityDetail', 'globalConstants', 'agentDetails', 'stepsSaved', 'proposerDetails',
        'quotationNumber', 'getQueryParameter', '$window', 'checkIfInsuredUploaded', 'premiumData',
        function ($scope, $http, $timeout, $location, $route, $upload, provinces, getProvinceAndCityDetail, globalConstants, agentDetails, stepsSaved, proposerDetails, quotationNumber, getQueryParameter,
                  $window, checkIfInsuredUploaded, premiumData) {
            var mode = getQueryParameter("mode");
            $scope.mode = mode;
            $scope.showModal = false;

            $scope.qId = null;
            if (mode == 'view') {
                $scope.isViewMode = true;
                $scope.isEditMode = true;
            } else if (mode == 'edit') {
                $scope.isEditMode = true;
            }

            $scope.premiumData = premiumData;

            $scope.showDownload = true;
            /*This scope holds the list of installments from which user can select one */
            $scope.numberOfInstallmentsDropDown = [];

            /*regex for number pattern for more details see commonModule.js*/
            $scope.numberPattern = globalConstants.numberPattern;

            $scope.fileSaved = null;

            /*This scope value is binded to fueluxWizard directive and hence it changes as and when next button is clicked*/
            $scope.selectedItem = 1;

            /*Holds the indicator for steps in which save button is clicked*/
            $scope.stepsSaved = stepsSaved;
            $scope.stepsSaved["3"] = checkIfInsuredUploaded;
            /*Inter id used for programmatic purpose*/
            $scope.quotationId = getQueryParameter('quotationId') || null;

            $scope.versionNumber = 0;

            /*actual quotation number to be used in the view*/
            $scope.quotationNumber = quotationNumber;

            $scope.provinces = provinces;
            // console.log($scope.premiumData);
            $scope.quotationDetails = {
                /*initialize with default values*/
                plan: {
                    samePlanForAllRelation: false,
                    samePlanForAllCategory: false,
                    considerMoratoriumPeriod: false
                },


                premium: $scope.premiumData
            };

            if ($scope.premiumData)
                $scope.selectedInstallment = $scope.premiumData.premiumInstallment;

            $scope.quotationDetails.basic = agentDetails;
            $scope.quotationDetails.proposer = proposerDetails;
            $scope.quotationDetails.plan.considerMoratoriumPeriod = proposerDetails.considerMoratoriumPeriod;
            $scope.quotationDetails.plan.samePlanForAllRelation=proposerDetails.samePlanForAllRelation;
            $scope.quotationDetails.plan.samePlanForAllCategory=proposerDetails.samePlanForAllCategory;

            // console.log(getQueryParameter('quotationId'));
            // console.log($scope.quotationId);


            $scope.$watchCollection('[quotationId,showDownload]', function (n) {
                if (n[0]) {
                    $scope.qId = n[0];
                    //  console.log(n[0]);
                    // console.log(n[1]);
                    if (n[1]) {
                        $scope.dropdown = [
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Ready Reckoner</a>",
                                "href": "/pla/quotation/grouphealth/downloadplandetail/" + $scope.qId
                            },
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                                "href": "/pla/quotation/grouphealth/downloadinsuredtemplate/" + $scope.qId
                            }
                        ];
                    } else {
                        $scope.dropdown = [
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Ready Reckoner</a>",
                                "href": "/pla/quotation/grouphealth/downloadplandetail/" + $scope.qId
                            },
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                                "href": "/pla/quotation/grouphealth/downloadinsuredtemplate/" + $scope.qId
                            },
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Error File</a>",
                                "href": "/pla/quotation/grouphealth/downloaderrorinsuredtemplate/" + $scope.qId
                            }
                        ];
                    }
                }
            });

            $scope.$watch('quotationDetails.proposer.province', function (newVal, oldVal) {
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

            $scope.populateProposerDetailFromClientRepository = function () {
                $http.get("/pla/quotation/grouphealth/getproposerdetailfromclient/" + $scope.quotationDetails.proposer.proposerCode + "/" + $scope.quotationId)
                    .success(function (data) {
                        $scope.quotationDetails.proposer = data;
                    });
            }

            $scope.accordionStatus = {
                contact: false,
                proposer: true
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
                angular.extend($scope.quotationDetails.basic, {agentName: null, branchName: null, teamName: null});
            };

            $scope.isSaveDisabled = function (formName) {
                return formName.$invalid || ($scope.stepsSaved[$scope.selectedItem] && !mode == 'new')
            };

            $scope.searchAgent = function () {
                $http.get("/pla/quotation/grouphealth/getagentdetail/" + $scope.quotationDetails.basic.agentId)
                    .success(function (data) {
                        if (data.status === "200") {
                            $scope.agentNotFound = false;
                            $scope.quotationDetails.basic = data.data;
                            var agentName = $scope.quotationDetails.basic.agentName.replace("null", '').replace('null', '').trim();
                            $scope.quotationDetails.basic.agentName = agentName;
                        } else {
                            $scope.agentNotFound = true;
                            $scope.errorMessage=data.message;
                        }
                    })
                    .error(function (data, status) {
                    });
            };

            function isInteger(x) {
                return Math.round(x) === x;
            }

            $scope.quotationDetails.premium.addOnBenefit = $scope.quotationDetails.premium.addOnBenefit || 20;
            $scope.quotationDetails.premium.profitAndSolvencyLoading = $scope.quotationDetails.premium.profitAndSolvencyLoading || 0;
            $scope.quotationDetails.premium.discounts = $scope.quotationDetails.premium.discounts || 0;
            $scope.quotationDetails.premium.waiverOfExcessLoading = $scope.quotationDetails.premium.waiverOfExcessLoading || 15;

            $scope.inappropriatePolicyTerm = false;
            $scope.$watch('quotationDetails.premium.policyTermValue', function (newVal, oldVal) {
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

            var setQuotationNumberAndVersionNumber = function (quotationId) {
                $http.get("/pla/quotation/grouphealth/getquotationnumber/" + quotationId)
                    .success(function (data, status) {
                        $scope.quotationNumber = data.id;
                    });
                $http.get("/pla/quotation/grouphealth/getversionnumber/" + quotationId)
                    .success(function (data, status) {
                        $scope.versionNumber = data.id;
                    });
            };


            $scope.$watch('quotationId', function (newval, oldval) {
                if (newval && newval != oldval) {
                    $window.location.href = '/pla/quotation/grouphealth/creategrouphealthquotation?quotationId=' + $scope.quotationId + '&mode=edit';
                }
            });

            $scope.saveBasicDetails = function () {
                if ($scope.quotationId) {
                    $http.post("/pla/quotation/grouphealth/updatewithagentdetail", angular.extend($scope.quotationDetails.basic, {
                        proposerName: $scope.quotationDetails.proposer.proposerName
                        , quotationId: $scope.quotationId
                    }))
                        .success(function (agentDetails) {
                            if (agentDetails.status == "200") {
                                $scope.quotationId = agentDetails.id;
                                setQuotationNumberAndVersionNumber(agentDetails.id);
                                saveStep();
                            }
                        });
                } else {
                    $http.post("/pla/quotation/grouphealth/createquotation", angular.extend($scope.quotationDetails.basic, {proposerName: $scope.quotationDetails.proposer.proposerName}))
                        .success(function (agentDetails) {
                            if (agentDetails.status == "200") {
                                $window.location.href = '/pla/quotation/grouphealth/creategrouphealthquotation?quotationId=' + agentDetails.id + '&mode=new';
                                saveStep();
                            }
                        });
                }
            };
            var saveStep = function () {
                $scope.stepsSaved[$scope.selectedItem] = true;
            };

            $scope.saveProposerDetails = function () {
                $http.post("/pla/quotation/grouphealth/updatewithproposerdetail", angular.extend({},
                    {proposerDto: $scope.quotationDetails.proposer},
                    {"quotationId": $scope.quotationId}))
                    .success(function (data) {
                        if (data.status == "200") {
                            $scope.quotationId = data.id;
                            $scope.proposerCodeDisabled = true;
                            saveStep();
                        }
                    });
            };

            $scope.updatePremiumDetail = function (quotationId) {
                $http.get("/pla/quotation/grouphealth/getpremiumdetail/" + quotationId)
                    .success(function (data) {
                        console.log('received data' + JSON.stringify(data));
                        $scope.quotationDetails.premium = data;
                        $scope.premiumData = data;
                        $scope.quotationDetails.premium.policyTermValue = data.policyTermValue;
                        $scope.quotationDetails.premium.profitAndSolvencyLoading = $scope.quotationDetails.premium.profitAndSolvencyLoading || 0;
                        $scope.quotationDetails.premium.discounts = $scope.quotationDetails.premium.discounts || 0;

                    });
            }

            $scope.proceedToNext = function () {
                saveStep();
                $scope.showModal = false;


            }
            $scope.errorMessage='';
            $scope.$watch('selectedItem', function (newVal, oldVal) {
                //  console.log("STEP"+newVal);
                //  console.log(!$scope.stepsSaved[newVal]);
                if (newVal == 3) {
                    $http.get("/pla/quotation/grouphealth/isValidPremiumAndPerson/" + $scope.quotationId)
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
                    url: '/pla/quotation/grouphealth/uploadinsureddetail?quotationId=' + $scope.quotationId,
                    headers: {'Authorization': 'xxx'},
                    fields: $scope.quotationDetails.plan,
                    file: $scope.fileSaved
                }).success(function (data, status, headers, config) {
                    if (data.status == "200") {
                        if(data.id) {
                            $scope.quotationId = data.id;
                            $timeout($scope.updatePremiumDetail($scope.quotationId), 500);
                            $http.get("/pla/quotation/grouphealth/isValidPremiumAndPerson/" + $scope.quotationId)
                                .success(function (response) {
                                    console.log(response);
                                    if (response.data) {
                                        $scope.showModal = true;
                                        $scope.errorMessage=response.message;

                                    } else {
                                        saveStep();

                                    }

                                });

                        }

                    }else{
                        if(data.data){
                            $scope.showDownload = false;

                        }else{
                            $scope.showDownload = true;
                        }
                    }
                });
            };
            $scope.premiumInstallment = false;


            $scope.updateNumberOfInstallments = function (installmntNo) {
                if (installmntNo > 0) {
                    $scope.premiumInstallment = true;
                }
            };

            $scope.installments = _.sortBy($scope.premiumData.installments, 'installmentNo');

            $scope.recalculatePremium = function () {
                $scope.premiumInstallment = false;
                $scope.quotationDetails.premium.premiumInstallment = $scope.selectedInstallment || null;
                $http.post('/pla/quotation/grouphealth/recalculatePremium', angular.extend({},
                    {premiumDetailDto: $scope.quotationDetails.premium},
                    {"quotationId": $scope.quotationId})).success(function (data) {
                    // console.log(data.data);
                    $scope.quotationDetails.premium = data.data;
                    $scope.premiumData.totalPremium = data.data.totalPremium;
                    if (data.data.annualPremium) {
                        $scope.premiumData.annualPremium = data.data.annualPremium;
                        $scope.premiumData.semiannualPremium = data.data.semiannualPremium;
                        $scope.premiumData.quarterlyPremium = data.data.quarterlyPremium;
                        $scope.premiumData.monthlyPremium = data.data.monthlyPremium;
                    }
                    if (data.data.installments) {
                        $scope.selectedInstallment = null;
                        $scope.installments = _.sortBy(data.data.installments, 'installmentNo');
                    }
                });

            }

            $scope.savePremiumDetails = function () {
                var premiumDetailDto = $scope.quotationDetails.premium;
                var request = angular.extend({premiumDetailDto: $scope.quotationDetails.premium},
                    {"quotationId": $scope.quotationId});
                request.premiumDetailDto["premiumInstallment"] = $scope.selectedInstallment;
                console.log(JSON.stringify(request));
                $http.post('/pla/quotation/grouphealth/savepremiumdetail', request).success(function (data) {
                    if($scope.premiumData.quotationStatus != 'SHARED'){
                        $http.post("/pla/quotation/grouphealth/generate", angular.extend({},
                            {"quotationId": $scope.quotationId}))
                            .success(function (data) {
                                /* if (data.status == "200") {
                                 saveStep();
                                 $('#searchFormQuotation').val($scope.quotationId);
                                 $('#searchForm').submit();
                                 }*/
                            });
                    }
                   /* $http.post("/pla/quotation/grouphealth/generate", angular.extend({},
                        {"quotationId": $scope.quotationId}))
                        .success(function (data) {
                            if (data.status == "200") {
                                saveStep();
                                $('#searchFormQuotation').val($scope.quotationId);
                                $('#searchForm').submit();
                            }
                        });*/
                    if (data.status == "200") {
                        saveStep();
                        $('#searchFormQuotation').val($scope.quotationId);
                        $('#searchForm').submit();
                    }
                });

            }

            $scope.setSelectedInstallment = function (selectedInstallment) {
                $scope.selectedInstallment = selectedInstallment;
                console.log('setSelectedInstallment ***');
            }

            $scope.back = function () {
                $window.location.href = 'listgrouphealthquotation';
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
            templateUrl: 'createQuotationTpl.html',
            controller: 'quotationCtrl',
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
                    queryParam = getQueryParameter('quotationId');
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get('/pla/quotation/grouphealth/getagentdetailfromquotation/' + queryParam).success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        stepsSaved["1"] = true;
                        return deferred.promise;
                    } else {
                        return {};
                    }
                }],
                proposerDetails: ['$q', '$http', function ($q, $http) {
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get('/pla/quotation/grouphealth/getproposerdetail/' + queryParam).success(function (response, status, headers, config) {
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
                checkIfInsuredUploaded: ['$q', '$http', function ($q, $http) {
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get('/pla/quotation/grouphealth/isinsureddetailavailable/' + queryParam).success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        return deferred.promise;
                    } else {
                        return false;
                    }
                }],
                quotationNumber: ['$q', '$http', function ($q, $http) {
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get("/pla/quotation/grouphealth/getquotationnumber/" + queryParam)
                            .success(function (response) {
                                deferred.resolve(response.id)
                            })
                            .error(function () {
                                deferred.reject();
                            });
                        return deferred.promise;
                    } else {
                        return null;
                    }
                }],
                premiumData: ['$q', '$http', function ($q, $http) {
                    if (queryParam && !_.isEmpty(queryParam)) {
                        var deferred = $q.defer();
                        $http.get('/pla/quotation/grouphealth/getpremiumdetail/' + queryParam).success(function (response, status, headers, config) {
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



