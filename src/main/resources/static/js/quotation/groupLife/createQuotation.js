angular.module('createQuotation',['common','ngRoute','mgcrea.ngStrap.select','mgcrea.ngStrap.alert','mgcrea.ngStrap.popover','directives','angularFileUpload','mgcrea.ngStrap.dropdown','ngSanitize','commonServices'])
    .controller('quotationCtrl',['$scope','$http','$timeout','$upload','provinces','getProvinceAndCityDetail','globalConstants','agentDetails','stepsSaved','proposerDetails','quotationNumber','getQueryParameter','$window',
        function($scope,$http,$timeout,$upload,provinces,getProvinceAndCityDetail,globalConstants,agentDetails,stepsSaved,proposerDetails,quotationNumber,getQueryParameter,$window){
            var mode = getQueryParameter("mode");
            $scope.qId=null;
            if(mode=='view'){
                $scope.isViewMode = true;
                $scope.isEditMode = true;
            }else if(mode=='edit'){
                $scope.isEditMode = true;
            }

            $scope.showDownload=true;
            /*This scope holds the list of installments from which user can select one */
            $scope.numberOfInstallmentsDropDown = [];

            /*regex for number pattern for more details see commonModule.js*/
            $scope.numberPattern =globalConstants.numberPattern;

            $scope.fileSaved=null;

            /*This scope value is binded to fueluxWizard directive and hence it changes as and when next button is clicked*/
            $scope.selectedItem = 1;

            /*Holds the indicator for steps in which save button is clicked*/
            $scope.setpsSaved =stepsSaved;

            /*Inter id used for programmatic purpose*/
            $scope.quotationId = getQueryParameter('quotationId') || null;

            $scope.versionNumber = getQueryParameter('version') || null;

            /*actual quotation number to be used in the view*/
            $scope.quotationNumber = quotationNumber;

            $scope.provinces = provinces;

            $scope.quotationDetails = {
                /*initialize with default values*/
                plan:{
                    samePlanForAllRelation:false,
                    samePlanForAllCategory:false
                },
                premium:{
                    addOnBenefit:20,
                    profitAndSolvencyLoading:0,
                    discounts:0
                }
            };

            $scope.quotationDetails.basic = agentDetails;
            $scope.quotationDetails.proposer = proposerDetails;
          // console.log(getQueryParameter('quotationId'));
           // console.log($scope.quotationId);


            $scope.$watchCollection('[quotationId,showDownload]',function(n){
                if(n[0]){
                    $scope.qId=n[0];
                    console.log(n[0]);
                    console.log(n[1]);
                    if(n[1]) {
                        $scope.dropdown = [
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Ready Reckoner</a>",
                                "href": "/pla/quotation/grouplife/downloadplandetail/" + $scope.qId
                            },
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                                "href": "/pla/quotation/grouplife/downloadinsuredtemplate/" + $scope.qId
                            }
                        ];
                    }else{
                        $scope.dropdown = [
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Ready Reckoner</a>",
                                "href": "/pla/quotation/grouplife/downloadplandetail/" + $scope.qId
                            },
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                                "href": "/pla/quotation/grouplife/downloadinsuredtemplate/" + $scope.qId
                            },
                            {
                                "text": "<a><img src=\"/pla/images/xls-icon.png\">Error File</a>",
                                "href": "/pla/quotation/grouplife/downloaderrorinsuredtemplate/" + $scope.qId
                            }
                        ];
                    }
                }
            });


                /*used for bs-dropdown*/
              /*  $scope.dropdown = [
                    {
                        "text": "<a><img src=\"/pla/images/xls-icon.png\">Ready Reckoner</a>",
                        "href": "/pla/quotation/grouplife/downloadplandetail/" + $scope.qId
                    },
                    {
                        "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                        "href": "/pla/quotation/grouplife/downloadinsuredtemplate/" + $scope.qId
                    },
                    {
                        "text": "<a><img src=\"/pla/images/xls-icon.png\">Error File</a>",
                        "href": "/pla/quotation/grouplife/downloadinsuredtemplate/" + $scope.qId
                    }
                ];*/


            $scope.$watch('quotationDetails.proposer.province',function(newVal,oldVal){
                if(newVal){
                    $scope.getProvinceDetails(newVal);
                }
            });

            $scope.getProvinceDetails=function(provinceCode){
                var provinceDetails = getProvinceAndCityDetail(provinces,provinceCode);
                if(provinceDetails){
                    $scope.cities = provinceDetails.cities;
                }
            };
            $scope.accordionStatus = {
                contact:false,
                proposer:true
            };
            $scope.$watch('fileSaved', function (n,o) {
                if(n&& n.length){
                    $scope.fileName=n[0].name
                }
            });
            $scope.openNewTab=function(event){
                /*keyCode 9 is tab key*/
                if(event && event.keyCode ==9){
                    $scope.accordionStatus.contact =true;
                    $scope.accordionStatus.proposer =false;
                }
            };

            /*clear all fields in the agent details except agentId*/
            $scope.clearAgentDetails =  function(){
                angular.extend($scope.quotationDetails.basic,{agentName:null,branchName:null,teamName:null});
            };

            $scope.isSaveDisabled = function(formName){
                return formName.$invalid || ($scope.setpsSaved[$scope.selectedItem] && !$scope.isEditMode)
            };

            $scope.searchAgent = function(){
                $http.get("/pla/quotation/grouplife/getagentdetail/"+$scope.quotationDetails.basic.agentId)
                    .success(function(data,status){
                        if(data.status=="200"){
                            $scope.agentNotFound = false;
                            $scope.quotationDetails.basic=data.data;
                        }else{
                            $scope.agentNotFound = true;
                        }
                    })
                    .error(function(data,status){

                    });
            };

            function isInteger(x) {
                return Math.round(x) === x;
            }


            function generateListOfInstallments(numberOfInstallments){
                $scope.numberOfInstallmentsDropDown=[];
                for(var installment=1;installment<=numberOfInstallments;installment++){
                    $scope.numberOfInstallmentsDropDown.push(installment);
                }
            }

            $scope.$watch('quotationDetails.premium.policyTermValue',function(newVal,oldVal){
                /*TODO check for the minimum amd maximum value for the policy term value*/
                if(newVal && newVal!=365 && newVal>=30 && newVal<=999){
                    /*used to toggle controls between dropdown and text*/
                    $scope.isPolicyTermNot365 = true;
                    /*used to show the error message when inappropriate value is entered*/
                    $scope.inappropriatePolicyTerm = false;
                    var numberOfInstallments = newVal/30;
                    if(isInteger(numberOfInstallments)){
                        generateListOfInstallments(numberOfInstallments-1)
                    }else{
                        generateListOfInstallments(Math.floor(numberOfInstallments));
                    }
                }else{
                    if(newVal<30 || newVal>999){
                       $scope.inappropriatePolicyTerm = true;
                    }else{
                        $scope.isPolicyTermNot365 = false;
                    }
                }
            });

            var setQuotationNumberAndVersionNumber = function(quotationId){
                $http.get("/pla/quotation/grouplife/getquotationnumber/"+quotationId)
                    .success(function(data,status){
                        $scope.quotationNumber=data.id;
                    });
                $http.get("/pla/quotation/grouplife/getversionnumber/"+quotationId)
                    .success(function(data,status){
                        $scope.versionNumber=data.id;
                    });
            };

            $scope.saveBasicDetails = function(){
                $http.post("/pla/quotation/grouplife/createquotation",angular.extend($scope.quotationDetails.basic,{proposerName:$scope.quotationDetails.proposer.proposerName}))
                    .success(function(agentDetails){
                        if(agentDetails.status=="200"){
                            $scope.quotationId = agentDetails.id;
                            setQuotationNumberAndVersionNumber(agentDetails.id);

                            saveStep();
                        }
                    });
            };

            var saveStep = function(){
                $scope.setpsSaved[$scope.selectedItem]=true;
            };

            $scope.saveProposerDetails =  function(){
                $http.post("/pla/quotation/grouplife/updatewithproposerdetail",angular.extend({},
                    {proposerDto : $scope.quotationDetails.proposer},
                    {"quotationId":$scope.quotationId}))
                    .success(function(data){
                        if(data.status=="200"){
                            saveStep();
                        }
                    });
            };


            $scope.savePlanDetails = function () {
                $upload.upload({
                    url: '/pla/quotation/grouplife/uploadinsureddetail?quotationId=' + $scope.quotationId,
                    headers: {'Authorization': 'xxx'},
                    fields:$scope.quotationDetails.plan,
                    file: $scope.fileSaved
                }).success(function (data, status, headers, config) {
                    if (data.status == "200") {
                        saveStep();
                        $scope.showDownload=true;
                        $http.get("/pla/quotation/getpremiumdetail/"+ $scope.quotationId)
                            .success(function(data){
                                console.log(data);

                            })
                    }else{
                        $scope.showDownload=false;
                       // console.log($scope.showDownload);
                    }

                });
            };

            $scope.savePremiumDetails = function(){


            }

            $scope.back = function(){
                $window.location.href= 'listgrouplifequotation';
            }
        }])
    .config(['$dropdownProvider',function($dropdownProvider){
        angular.extend($dropdownProvider.defaults, {
            html: true
        });
    }])
    .config(["$routeProvider",function($routeProvider){
        var stepsSaved = {};
        var queryParam = null;
        $routeProvider.when('/', {
            templateUrl: 'createQuotationTpl.html',
            controller: 'quotationCtrl',
            resolve: {

                provinces:['$q', '$http', function ($q, $http) {
                    var deferred = $q.defer();
                    $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                        deferred.resolve(response)
                    }).error(function (response, status, headers, config) {
                        deferred.reject();
                    });
                    return deferred.promise;
                }],
                agentDetails:['$q','$http','getQueryParameter',function($q,$http,getQueryParameter){
                    queryParam = getQueryParameter('quotationId');
                    if(queryParam && !_.isEmpty(queryParam) ){
                        var deferred = $q.defer();
                        $http.get('/pla/quotation/grouplife/getagentdetailfromquotation/'+queryParam).success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        stepsSaved["1"]=true;
                        return deferred.promise;
                    }else{
                        return {};
                    }
                }],
                proposerDetails:['$q','$http',function($q,$http){
                    if(queryParam && !_.isEmpty(queryParam) ){
                        var deferred = $q.defer();
                        $http.get('/pla/quotation/grouplife/getproposerdetail/'+queryParam).success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        stepsSaved["2"]=true;
                        return deferred.promise;
                    }else{
                        return {};
                    }
                }],
                quotationNumber:['$q','$http',function($q,$http){
                    if(queryParam && !_.isEmpty(queryParam) ){
                        var deferred = $q.defer();
                        $http.get("/pla/quotation/grouplife/getquotationnumber/"+queryParam)
                            .success(function(response){
                                deferred.resolve(response.id)
                            })
                            .error(function(){
                                deferred.reject();
                            });
                        return deferred.promise;
                    }else{
                        return null;
                    }
                }],
                stepsSaved:function(){
                    return stepsSaved;
                }
            }
        })
    }]);



