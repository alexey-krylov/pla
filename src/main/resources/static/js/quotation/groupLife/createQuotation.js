angular.module('createQuotation',['common','ngRoute','mgcrea.ngStrap.select','mgcrea.ngStrap.alert','mgcrea.ngStrap.popover','directives','angularFileUpload','mgcrea.ngStrap.dropdown','ngSanitize','commonServices'])
    .controller('quotationCtrl',['$scope','$http','$timeout','$upload','provinces','getProvinceAndCityDetail','globalConstants','agentDetails','stepsSaved','proposerDetails','quotationNumber','getQueryParameter',
        function($scope,$http,$timeout,$upload,provinces,getProvinceAndCityDetail,globalConstants,agentDetails,stepsSaved,proposerDetails,quotationNumber,getQueryParameter){
            $scope.isEditMode = !!getQueryParameter("mode");

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
            $scope.dropdown = [
                {
                    "text": "<a><img src=\"/pla/images/xls-icon.png\">Ready Reckoner</a>",
                    "href": "/pla/quotation/grouplife/downloadplandetail"
                },
                {
                    "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                    "href": "/pla/quotation/grouplife/downloadinsuredtemplate"
                }
            ];

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
                        $scope.quotationId = agentDetails.id;
                        setQuotationNumberAndVersionNumber(agentDetails.id);
                        saveStep();
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
                        saveStep();
                    });
            };


            $scope.savePlanDetails = function () {
                $upload.upload({
                    url: '/pla/quotation/grouplife/uploadinsureddetail',
                    headers: {'Authorization': 'xxx'},
                    fields:$scope.quotationDetails.plan,
                    file: $scope.fileSaved
                }).success(function (data, status, headers, config) {
                    saveStep();
                    $http.get("/pla/quotation/getpremiumdetail/"+ $scope.quotationId)
                        .success(function(){

                        })
                });
            };
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



