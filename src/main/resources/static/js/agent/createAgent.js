
angular.module('createAgent',['common','ngRoute','mgcrea.ngStrap.select','mgcrea.ngStrap.alert','commonServices'])
    .controller('agentCtrl',['$scope','$http','channelType','authorisedToSell','teamDetails','provinces','$timeout','$alert','$route','$window','transformJson','getQueryParameter','agentDetails','globalConstants','nextAgentSequence','$rootScope',
        function($scope,$http,channelType,authorisedToSell,teamDetails,provinces,$timeout,$alert,$route,$window,transformJson,getQueryParameter,agentDetails,globalConstants,nextAgentSequence,$rootScope){
            $scope.numberPattern =globalConstants.numberPattern;
            $scope.selectedWizard = 1;
            $scope.searchResult = {
                isEmpty:false,
                isSearched:false
            };
            $scope.agentDetails={};
            $scope.isFormSubmitted =  false;
            $scope.isEditMode =  false;
            $scope.stepValues = {
                agent:2,
                team:3,
                contact:4
            };
            if(_.size(agentDetails)!=0){
                $scope.agentDetails=angular.copy(agentDetails);
                $scope.isEditMode =  true;
                $scope.trainingCompleteOn = agentDetails.agentProfile.trainingCompleteOn;
                $scope.stepValues = {
                    agent:1,
                    team:2,
                    contact:3
                };
                $scope.agentDetails.authorizePlansToSell=[];
                angular.forEach(agentDetails.authorizePlansToSell,function(value,key){
                    this.push(value.planId);
                },$scope.agentDetails.authorizePlansToSell);
                $scope.stepsToRemove={index:1,howMany:1};
            }
            console.log($scope.agentDetails);
            $scope.$watch('agentDetails.teamDetail.teamId',function(newVal,oldVal){
                if(newVal){
                    $scope.prePopulateTeamLeader();
                }
            });
            $scope.primaryCities = [];
            $scope.physicalCities = [];
            $scope.$watch('agentDetails.physicalAddress.physicalGeoDetail.provinceCode',function(newVal,oldVal){
                if(newVal){
                    var provinceDetails = $scope.getProvinceDetails(newVal);
                    $scope.physicalCities =  provinceDetails.cities;
                    $scope.agentDetails.physicalAddress.physicalGeoDetail.provinceName = provinceDetails.provinceName;
                }
            });
            $scope.$watch('agentDetails.contactDetail.geoDetail.provinceCode',function(newVal,oldVal){
                if(newVal){
                    var provinceDetails = $scope.getProvinceDetails(newVal);
                    $scope.primaryCities = provinceDetails.cities;
                    $scope.agentDetails.contactDetail.geoDetail.provinceName = provinceDetails.provinceName;
                }
            });
            $scope.getProvinceDetails=function(provinceCode){
                var province =  _.findWhere(provinces, {provinceId:provinceCode});
                if(province){
                    return {
                        provinceName:province.provinceName,
                        provinceCode:province.provinceId,
                        cities:province.cities
                    }
                }
            };

            if(!$scope.isEditMode){
                $scope.agentDetails={ agentProfile :{designationDto:{description:"Agent",code:"AGENT"}}};
            }
            $scope.teamCodeStatus = {
                isRequired : true,
                isDisabled : false
            };

            $scope.isSearchDisabled =  function(){
                if($scope.search && ((!$scope.search.nrc && $scope.search.empId) || ($scope.search.nrc && !$scope.search.empId))){
                    return false;
                }
                return true;
            };
            $scope.channelTypes = [{channelName:"Personal Selling",channelCode:"PERSONAL_SELLING"},
                {channelName:"Direct",channelCode:"DIRECT"},
                {channelName:"Brokers",channelCode:"BROKERS"}];
            $scope.authorisedToSell = authorisedToSell;
            $scope.teamDetails= teamDetails;
            $scope.provinces=provinces;
            $scope.today = new Date();
            $scope.datePickerSettings = {
                isOpened:false,
                dateOptions:{
                    formatYear: 'yyyy',
                    startingDay: 1
                }
            };
            $scope.open = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettings.isOpened = true;
            };

            $scope.jumpToNthStep = function(step){
                $scope.selectedWizard=step;
                $scope.searchResult.isEmpty=false;
            };
            $scope.searchAgent =  function(){
                $scope.searchResult.isSearched=true;
                $http.get("/pla/core/agent/getemployeedeatil/"+(_.isEmpty($scope.search.empId)?null:$scope.search.empId)+"/"+(_.isEmpty($scope.search.nrc)?null:$scope.search.nrc))
                    .success(function(data,status){
                        if(data && (_.size(data) ==0 || data.firstName==null)){
                            $scope.searchResult.isEmpty=true;
                            $scope.agentDetails.agentProfile.nrcNumberInString=$scope.search.nrc;
                        }else{
                            $scope.jumpToNthStep(2);
                            $scope.agentDetails = transformJson.fromHrmsToPla(data);
                            $scope.trainingCompleteOn = $scope.agentDetails.trainingCompleteOn;
                        }
                    })
                    .error(function(data,status){
                        $scope.searchResult.isEmpty=true;
                        $scope.agentDetails.agentProfile.nrcNumberInString=$scope.search.nrc;
                    });
                if(nextAgentSequence){
                    $scope.agentDetails.agentId = nextAgentSequence;
                }
            };
            $scope.prePopulateTeamLeader = function(){
                var teamDetails = _.findWhere($scope.teamDetails, {teamId:$scope.agentDetails.teamDetail.teamId});
                $scope.teamLeaderName = teamDetails.firstName+' '+teamDetails.lastName;
                $scope.branchName =teamDetails.branchName;
                $scope.regionName = teamDetails.regionName;

            };
            $scope.cancel = function(){
                $window.location.href = "listagent"
            };
            $scope.update =  function(){
                $scope.isFormSubmitted = true;
                if($scope.agentDetailsForm.$valid && $scope.teamDetailsForm.$valid && $scope.contactDetailsForm.$valid){
                    $scope.contactDetailsForm.$submitted=true;
                    $http.post('/pla/core/agent/update',transformJson.createCompatibleJson(angular.copy($scope.agentDetails),$scope.physicalCities,$scope.primaryCities,$scope.trainingCompleteOn,true))
                        .success(function(response, status, headers, config){
                        })
                        .error(function(response, status, headers, config){
                            alert("error!!!");
                        });
                }
            };
            $scope.submit = function(){
                $scope.isFormSubmitted = true;
                if($scope.agentDetailsForm.$valid && $scope.teamDetailsForm.$valid && $scope.contactDetailsForm.$valid){
                    $scope.contactDetailsForm.$submitted=true;
                    $http.post('/pla/core/agent/create',transformJson.createCompatibleJson(angular.copy($scope.agentDetails),$scope.physicalCities,$scope.primaryCities,$scope.trainingCompleteOn,false))
                        .success(function(response, status, headers, config){

                        })
                        .error(function(response, status, headers, config){
                            alert("error!!!");
                        });
                }
            };

        }])
    .factory('transformJson',['formatJSDateToDDMMYYYY',function(formatJSDateToDDMMYYYY){
        var transformService = {};
        transformService.createCompatibleJson = function (agentDetails,physicalCities,primaryCities,trainingCompleteOn,isUpdate) {
            var authorizePlansToSell = angular.copy(agentDetails.authorizePlansToSell);
            agentDetails.authorizePlansToSell = [];
            angular.forEach(authorizePlansToSell,function(value,key){
                this.push({"planId":value});
            },agentDetails.authorizePlansToSell);
            agentDetails.physicalAddress.physicalGeoDetail.cityName = _.findWhere(physicalCities,{geoId:agentDetails.physicalAddress.physicalGeoDetail.cityCode}).geoName;
            agentDetails.contactDetail.geoDetail.cityName =_.findWhere(primaryCities,{geoId:agentDetails.contactDetail.geoDetail.cityCode}).geoName;
            console.log(trainingCompleteOn);
            agentDetails.agentProfile.trainingCompleteOn = formatJSDateToDDMMYYYY(trainingCompleteOn);
            if(!isUpdate){
                delete agentDetails.agentStatus;
            }
            delete agentDetails.teamDetail.regionName;
            delete agentDetails.teamDetail.branchName;
            if(!agentDetails.licenseNumber ||_.size(agentDetails.licenseNumber.licenseNumber)==0){
                delete agentDetails.licenseNumber;
            }
            return agentDetails;
        };
        transformService.toPlanIdPlanNameObject = function(authorisedToSell){
            var plans = [];
            angular.forEach(authorisedToSell,function(plan,key){
                this.push({planId:plan.planId.planId,planName:plan.planDetail.planName});
            },plans);
            return plans;
        };
        transformService.fromHrmsToPla = function(agentDetails){
            return {
                "agentProfile": {
                    "title": agentDetails.title,
                    "firstName": agentDetails.firstName,
                    "lastName": agentDetails.lastName,
                    "nrcNumberInString": agentDetails.nrcNumber,
                    "employeeId": agentDetails.employeeId,
                    "designationDto": {
                        "code": agentDetails.designation,
                        "description": agentDetails.designationDescription
                    }
                },
                "licenseNumber": {"licenseNumber": agentDetails.licenseNumber},
                "teamDetail": agentDetails.teamDetail,
                "contactDetail": {
                    "mobileNumber": agentDetails.primaryContactDetail.mobileNumber,
                    "homePhoneNumber": agentDetails.primaryContactDetail.homePhoneNumber,
                    "workPhoneNumber": agentDetails.primaryContactDetail.workPhoneNumber,
                    "emailAddress": agentDetails.primaryContactDetail.email,
                    "addressLine1": agentDetails.primaryContactDetail.addressLine1,
                    "addressLine2": agentDetails.primaryContactDetail.addressLine2,
                    "geoDetail": {
                        "provinceName":"",
                        "postalCode": agentDetails.primaryContactDetail.postalCode,
                        "cityName": "",
                        "provinceCode": agentDetails.primaryContactDetail.province,
                        "cityCode":agentDetails.primaryContactDetail.city
                    }
                },
                "physicalAddress": {
                    "physicalAddressLine1": agentDetails.physicalContactDetail.addressLine1,
                    "physicalAddressLine2": agentDetails.physicalContactDetail.addressLine2,
                    "physicalGeoDetail": {
                        "provinceName":"",
                        "postalCode": agentDetails.physicalContactDetail.postalCode,
                        "cityName": "",
                        "provinceCode": agentDetails.physicalContactDetail.province,
                        "cityCode":agentDetails.physicalContactDetail.city
                    }
                },
                "authorizePlansToSell": agentDetails.authorizePlansToSell,
                "overrideCommissionApplicable": agentDetails.overrideCommissionApplicable,
                "channelType": agentDetails.channelType,
                "agentId": agentDetails.agentId
            }
        };
        return transformService;
    }])
    .config(["$routeProvider",function($routeProvider){
        $routeProvider.when('/', {
            templateUrl: 'createAgentTpl.html',
            controller: 'agentCtrl',
            resolve: {
                nextAgentSequence:['$q','$http','$window',function($q,$http,$window){
                    if($window.location.href.indexOf("/openeditpage")>-1){
                        return null;
                    }else{
                        var deferred = $q.defer();
                        $http.get('/pla/core/agent/getagentid').success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        return deferred.promise;
                    }
                }],
                agentDetails:['$q','$http','getQueryParameter','$window',function($q,$http,getQueryParameter,$window){
                    if($window.location.href.indexOf("/openeditpage")>-1){
                        var queryParam = {'agentId':getQueryParameter('agentId')};
                        var deferred = $q.defer();
                        $http.get('/pla/core/agent/agentdetail',{params:queryParam}).success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        return deferred.promise;
                    }else{
                        return [];
                    }
                }],
                channelType:['$q', '$http', function ($q, $http) {
                    var deferred = $q.defer();
                    $http.get('/pla/core/master/getchannelType').success(function (response, status, headers, config) {
                        deferred.resolve(response)
                    }).error(function (response, status, headers, config) {
                        deferred.reject();
                    });
                    return deferred.promise;
                }],
                authorisedToSell:['$q', '$http','transformJson', function ($q, $http,transformJson) {
                    var deferred = $q.defer();
                    $http.get('/pla/core/agent/getallplan').success(function (response, status, headers, config) {
                        deferred.resolve(transformJson.toPlanIdPlanNameObject(response))
                    }).error(function (response, status, headers, config) {
                        deferred.reject();
                    });
                    return deferred.promise;
                }],
                teamDetails:['$q', '$http', function ($q, $http) {
                    var deferred = $q.defer();
                    $http.get('/pla/core/agent/getteams').success(function (response, status, headers, config) {
                        deferred.resolve(response)
                    }).error(function (response, status, headers, config) {
                        deferred.reject();
                    });
                    return deferred.promise;
                }],
                provinces:['$q', '$http', function ($q, $http) {
                    var deferred = $q.defer();
                    $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                        deferred.resolve(response)
                    }).error(function (response, status, headers, config) {
                        deferred.reject();
                    });
                    return deferred.promise;
                }]
            }
        })
    }])




