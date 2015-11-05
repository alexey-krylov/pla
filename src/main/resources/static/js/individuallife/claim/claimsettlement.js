angular.module('claimSettlement', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives',
    'angularFileUpload', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices'])

.config(['datepickerPopupConfig', function (datepickerPopupConfig) {
            datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
            datepickerPopupConfig.currentText = 'Today';
            datepickerPopupConfig.clearText = 'Clear';
            datepickerPopupConfig.closeText = 'Done';
            datepickerPopupConfig.closeOnDateSelection = true;
        }])


    .controller('ClaimSettlementController', ['$scope', '$http', '$timeout', '$upload', 'provinces', 'getProvinceAndCityDetail', 'globalConstants',
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
            $scope.claimIntimationDetailResponse=[];
            $scope.isReturnStatus = false;




            /*This scope value is binded to fueluxWizard directive and hence it changes as and when next button is clicked*/
            $scope.selectedItem = 1;


            /*Inter id used for programmatic purpose*/
            $scope.proposalId = getQueryParameter('proposalId') || null;

            $scope.versionNumber = getQueryParameter('version') || null;



            $scope.provinces = provinces;
            $scope.proposer={};
            $scope.bankBranchDetail={};


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

       $scope.launchassuereddate = function ($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    $scope.assuredDate = true;
                           };
            $scope.claimDetails={};
            $scope.ClaimantDetails={};
             $scope.bankDetails={};
             $scope.saveBasicDetail = function () {
                    var requestSaveDetails= {  "claimDetails":$scope.claimDetails,
                    "ClaimantDetails":$scope.ClaimantDetails,
                    "Bank details":$scope.bankDetails
                    };
                    console.log("claimDetails"+JSON.stringify(requestSaveDetails));
              }
/*================pla gl claim================*/
$scope.policyId = getQueryParameter("policyId");
$scope.rcvurlPolicyNumber=getQueryParameter("policyNumber");
if($scope.rcvurlPolicyNumber){
$scope.claimDetails.policyNumber=$scope.rcvurlPolicyNumber;
}
else{
//alert("not");
}

   $http.get('/pla/grouplife/claim/claimintimationdetail/'+$scope.rcvurlPolicyNumber)
         .success(function(response,status,headers,config){
         $scope.claimIntimationDetailResponse=response;
//         alert("Bank bankCode :"+JSON.stringify( $scope.claimDetailsResponse));

         })
         .error(function(respone,status,headers,config){
         })



            $http.get("/pla/grouplife/claim/getclaimtype")
                         .success(function (data) {
                            $scope.claimTypes = data
                                });
               $http.get('/pla/individuallife/proposal/getAllOccupation').success(function (response, status, headers, config) {
                              $scope.occupations = response;
                          }).error(function (response, status, headers, config) {
                          });
/*bank details*/
         $http.get('/pla/grouplife/claim/getclaimtype').success(function (response, status, headers, config) {
                $scope.claimTypeDetailsResponse= response;
                console.log("claim Details :"+JSON.stringify(response));
            })
            .error(function (response, status, headers, config) {
            });
/*bank details*/
         $http.get('/pla/individuallife/proposal/getAllBankNames').success(function (response, status, headers, config) {
                $scope.bankDetailsResponse= response;
                console.log("Bank Details :"+JSON.stringify(response));
            })
            .error(function (response, status, headers, config) {
            });
            $scope.bankDetails={};
               $scope.$watch('bankDetails.bankName',function(newvalue,oldvalue){

                                if(newvalue){
                                    var bankCode = _.findWhere($scope.bankDetailsResponse, {bankName: newvalue});
//                                   console.log("Bank bankCode :"+JSON.stringify(bankCode));
//                                   console.log("Bank newvalue :"+JSON.stringify(newvalue));
                                    if (bankCode){
                                        $http.get('/pla/individuallife/proposal/getAllBankBranchNames/'+bankCode.bankCode).success(function (response, status, headers, config) {
                                            $scope.bankBranchDetails= response;
//                                        console.log("Bank Details :"+JSON.stringify(response));
                                        }).error(function (response, status, headers, config) {
                                        });
                                    }
                                }
                            });
                       $scope.$watch('bankDetails.bankBranchName',function(newvalue,oldvalue){
                                      if(newvalue){
                                          var bankBranchNames = _.findWhere($scope.bankBranchDetails, {branchName: newvalue});
                                          if(bankBranchNames)
                                          {
                                              $scope.bankDetails.bankBranchSortCode=bankBranchNames.sortCode;
                                          }
                                      }
                                  });

/*bank details*/

            $scope.additionalDocumentList = [{}];

            $scope.$watch('ClaimantDetails.province', function (newVal, oldVal) {
                if (newVal) {
                console.log(newVal);
                 console.log(oldVal);

                    $scope.getProvinceDetails(newVal);
                }
            });

            $scope.getProvinceDetails = function (provinceCode) {console.log(provinceCode);
                var provinceDetails = getProvinceAndCityDetail(provinces, provinceCode);
                if (provinceDetails) {
                    $scope.cities = provinceDetails.cities;
                }
            };
// after open page first tab is closed accordian first tab
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
     $scope.planDetail={};

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
            templateUrl: 'claimSettlement.html',
            controller: 'ClaimSettlementController',
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



