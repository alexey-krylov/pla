angular.module('createProposal',['common','ngRoute','mgcrea.ngStrap.select','mgcrea.ngStrap.alert','mgcrea.ngStrap.popover','directives','angularFileUpload','mgcrea.ngStrap.dropdown','ngSanitize','commonServices'])
    .controller('proposalCtrl',['$scope','$http','$timeout','$upload','provinces','getProvinceAndCityDetail','globalConstants','agentDetails','stepsSaved','proposerDetails','quotationNumber','getQueryParameter','$window',
        function($scope,$http,$timeout,$upload,provinces,getProvinceAndCityDetail,globalConstants,agentDetails,stepsSaved,proposerDetails,quotationNumber,getQueryParameter,$window){
            var mode = getQueryParameter("mode");
            if(mode=='view'){
                $scope.isViewMode = true;
                $scope.isEditMode = true;
            }else if(mode=='edit'){
                $scope.isEditMode = true;
            }
            /*This scope holds the list of installments from which user can select one */
            $scope.numberOfInstallmentsDropDown = [];

            /*regex for number pattern for more details see commonModule.js*/
            $scope.numberPattern =globalConstants.numberPattern;

            $scope.fileSaved=null;
            $scope.disableUploadButton=false;

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

            $scope.documentList=[
                {
                    "documentName" : "Document-ONE"
                },
                {
                    "documentName" : "Document-TWO"
                },
                {
                    "documentName" : "Document-THREE"
                },
                {
                    "documentName" : "Document-FOUR"
                },
                {
                    "documentName" : "Document-FIVE"
                }

            ];
            $scope.uploadDocumentFiles = function(){
               // console.log($scope.documentList.length);
                for(var i=0; i<$scope.documentList.length; i++){
                    var files = $scope.documentList[i].documentAttached;
                   // console.log(files);
                    if (files) {

                        $upload.upload({
                            url: '/pla/core/proposal/uploadProposalDocument',
                            file: files,
                            method: 'POST'
                        }).progress(function (evt) {

                        }).success(function (data, status, headers, config) {
                            //console.log('file ' + config.file.name + 'uploaded. Response: ' +
                           // JSON.stringify(data));
                        });
                    }

                }

            };
            if($scope.documentList) {
                if($scope.documentList.documentAttached) {
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
            $scope.proposalDetails.basic = agentDetails;
            $scope.proposalDetails.proposer = proposerDetails;
            /*used for bs-dropdown*/
            $scope.dropdown = [
                {
                    "text": "<a><img src=\"/pla/images/xls-icon.png\">Ready Reckoner</a>",
                    "href": "/pla/proposal/grouplife/downloadplandetail"
                },
                {
                    "text": "<a><img src=\"/pla/images/xls-icon.png\">Template</a>",
                    "href": "/pla/proposal/grouplife/downloadinsuredtemplate"
                }
            ];

            $scope.$watch('proposalDetails.proposer.province',function(newVal,oldVal){
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
            $scope.accordionStatusDocuments = {
                documents:true,
                additionalDocuments:false
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
                angular.extend($scope.proposalDetails.basic,{agentName:null,branchName:null,teamName:null});
            };

            $scope.isSaveDisabled = function(formName){
                //return formName.$invalid || ($scope.setpsSaved[$scope.selectedItem] && !$scope.isEditMode)
            };

            $scope.searchAgent = function(){
                $http.get("/pla/quotation/grouplife/getagentdetail/"+$scope.proposalDetails.basic.agentId)
                    .success(function(data,status){
                        if(data.status=="200"){
                            $scope.agentNotFound = false;
                            $scope.proposalDetails.basic=data.data;
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
                $http.get("/pla/proposal/grouplife/getquotationnumber/"+quotationId)
                    .success(function(data,status){
                        $scope.quotationNumber=data.id;
                    });
                $http.get("/pla/proposal/grouplife/getversionnumber/"+quotationId)
                    .success(function(data,status){
                        $scope.versionNumber=data.id;
                    });
            };

            $scope.saveBasicDetails = function(){
               // console.log($scope.proposalDetails.basic);
                $http.post("/pla/proposal/grouplife/createproposal",angular.extend($scope.proposalDetails.basic,{proposerName:$scope.proposalDetails.proposer.proposerName}))
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
                $http.post("/pla/proposal/grouplife/updatewithproposerdetail",angular.extend({},
                    {proposerDto : $scope.proposalDetails.proposer},
                    {"quotationId":$scope.quotationId}))
                    .success(function(data){
                        if(data.status=="200"){
                            saveStep();
                        }
                    });
            };
            $scope.savePlanDetails = function () {
                $upload.upload({
                    url: '/pla/proposal/grouplife/uploadinsureddetail',
                    headers: {'Authorization': 'xxx'},
                    fields:$scope.proposalDetails.plan,
                    file: $scope.fileSaved
                }).success(function (data, status, headers, config) {
                    if(data.status="200"){
                        saveStep();
                        $http.get("/pla/proposal/getpremiumdetail/"+ $scope.quotationId)
                            .success(function(){

                            })
                    }
                });
            };

            $scope.back = function(){
                $window.location.href= 'listgrouplifeproposal';
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
            templateUrl: 'createProposalTpl.html',
            controller: 'proposalCtrl',
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
                        $http.get('/pla/proposal/grouplife/getagentdetailfromquotation/'+queryParam).success(function (response, status, headers, config) {
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
                        $http.get('/pla/proposal/grouplife/getproposerdetail/'+queryParam).success(function (response, status, headers, config) {
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
                        $http.get("/pla/proposal/grouplife/getquotationnumber/"+queryParam)
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



