angular.module('brokerModule', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'commonServices', 'ngMessages'])
    .directive('viewEnabled', ['$window', function ($window) {
        return {
            link: function (scope, elem, attr, ctrl) {
                var viewmode = $window.location.href.indexOf("/view") > -1;
                if (!viewmode) {
                    return;
                }
                $(elem).attr('readonly', true);
                $(elem).attr('disabled', true);
                $("#BrokerUpdateBtn").css('visibility', 'hidden');
                $("#BrokerSubmitBtn").css('visibility', 'hidden');
            }
        }
    }]).directive('popover', function ($compile) {
        return {
            restrict: 'A',
            link: function (scope, elem) {
                var content = $(".popover-content").html();
                scope.fieldData = [];
                if (scope.agentDetails) {
                    if (scope.agentDetails.authorizePlansToSell) {
                        for (var i = 0; i < scope.agentDetails.authorizePlansToSell.length; i++) {
                            scope.fieldData.push(_.findWhere(scope.authorisedToSell, {planId: scope.agentDetails.authorizePlansToSell[i]}));
                        }
                    }
                }
                //var compileContent = scope.fieldData;
               // console.log(compileContent);
                var compileContent = _.sortBy(scope.fieldData, 'planName');
                var content = "<ul>";
                for (var i = 0; i < compileContent.length; i++) {

                    content = content + "<li>" + compileContent[i].planName + "</li>";
                }
                content = content + "</ul>";
                var title = $(".popover-head").html();
                var options = {
                    content: content,
                    html: true,
                    title: title
                };

                $(elem).popover(options);
            }
        }
    })

    .controller('brokerController', ['$scope', '$http', 'authorisedToSell', 'provinces', '$timeout', '$alert', '$route', '$window', 'transformJson',
        'getQueryParameter', 'agentDetails', 'globalConstants', 'nextAgentSequence', 'getProvinceAndCityDetail', '$alert',


        function ($scope, $http,
                  authorisedToSell, provinces, $timeout, $alert, $route, $window, transformJson, getQueryParameter, agentDetails, globalConstants, nextAgentSequence, getProvinceAndCityDetail, $alert) {
            $scope.numberPattern = globalConstants.numberPattern;

            var mode = getQueryParameter("mode");
            // console.log(mode);
            // alert(mode);
            if (mode == 'view') {
                $scope.isViewMode = true;

            } else {
                $scope.isViewMode = false;
            }


            //  console.log(' Broker Controller invoked.. ');
            $scope.agentDetails = agentDetails;
            $scope.cancel = function () {
                $window.location.href = "/pla/core/agent/listagent"
            };

            if (_.size(agentDetails) != 0) {
                //console.log(agentDetails);
                if (agentDetails.agentProfile) {
                    if (agentDetails.agentProfile.overrideCommissionApplicable) {
                        $scope.agentDetails.overrideCommissionApplicable = agentDetails.agentProfile.overrideCommissionApplicable;
                    }
                }
                /*This is used to disabled and hide some of the fields in the UI*/
                $scope.isEditMode = true;
                $scope.trainingCompleteOn = agentDetails.agentProfile.trainingCompleteOn;
            } else {
                $scope.agentDetails = {agentId: nextAgentSequence};
            }
            $scope.lineOfBusinessList = [{
                "lineOfBusinessId": "INDIVIDUAL_LIFE",
                "value": "Individual Life"
            }, {"lineOfBusinessId": "GROUP_HEALTH", "value": "Group Health"}, {
                "lineOfBusinessId": "GROUP_LIFE",
                "value": "Group Life"
            },]

            // Keeping The Copy of Line Of Business List
            $scope.lineOfBusinessListCopy=[];
            angular.copy($scope.lineOfBusinessList,$scope.lineOfBusinessListCopy);

            $scope.contact={};
            $scope.agentDetails.contactPersonDetails=[];
            $scope.contactPersonDetailsCopy=[];
            $scope.edit=false;
            $scope.addContactPerson=function(contact,tab1Status){
                var retContact = _.findWhere($scope.lineOfBusinessListCopy, {lineOfBusinessId: contact.lineOfBusinessId});
                if(retContact && !$scope.edit){
                    contact=angular.extend(contact,{value:retContact.value});
                }
                if($scope.agentDetails.contactPersonDetails && $scope.agentDetails.contactPersonDetails.length == 0){
                    $scope.agentDetails.contactPersonDetails.push(contact);
                    //$scope.contact={};
                }else{
                    var checkLoopNameStatus = "true";
                    for(i in $scope.agentDetails.contactPersonDetails){
                        if($scope.agentDetails.contactPersonDetails[i].lineOfBusinessId == contact.lineOfBusinessId){
                            $scope.agentDetails.contactPersonDetails[i]=contact;
                            checkLoopNameStatus = "true";
                            break;
                        }
                        else{
                            checkLoopNameStatus = "false";
                            //$scope.agentDetails.contactPersonDetails.push(contact);
                        }
                    }
                    if(checkLoopNameStatus == 'false'){
                        $scope.agentDetails.contactPersonDetails.push(contact);
                    }

                }
                if($scope.edit){
                    $scope.edit=false;
                }
                $scope.contact={};
                $scope.lineOfBusinessList=_.reject($scope.lineOfBusinessList,{"lineOfBusinessId":contact.lineOfBusinessId});
                $scope.tabOneEnableControl(tab1Status);
            }
            $scope.editContactPerson=function(contacts){
                $scope.edit=true;
                if(contacts){
                    for(i in $scope.agentDetails.contactPersonDetails){
                        if($scope.agentDetails.contactPersonDetails[i].lineOfBusinessId == contacts.lineOfBusinessId){
                            //var contactToEdit=$scope.agentDetails.contactPersonDetails[i];
                            //$scope.contact=$scope.agentDetails.contactPersonDetails[i];
                            angular.copy($scope.agentDetails.contactPersonDetails[i],$scope.contact);
                            break;
                        }
                    }

                }

            }
            $scope.cancelContactPerson=function(){
                $scope.contact=null;
                $scope.edit=false;
            }
            $scope.selectedItem = 1;
            $scope.stepsSaved = {};
            var mode = getQueryParameter("mode");
            if (mode == 'view' || mode == 'edit') {
                $scope.stepsSaved["1"] = true;
                var queryParam = {'agentId': getQueryParameter('agentId')};
                $http.get('/pla/core/agent/agentdetail', {params: queryParam})
                    .success(function (response, status, headers, config) {
                        $scope.agentDetails = response;
                        if($scope.agentDetails && $scope.agentDetails.contactPersonDetails.length >0){
                            for(i in $scope.agentDetails.contactPersonDetails){
                                if($scope.agentDetails.contactPersonDetails[i].lineOfBusinessId){
                                    $scope.lineOfBusinessList=_.reject($scope.lineOfBusinessList,{"lineOfBusinessId":$scope.agentDetails.contactPersonDetails[i].lineOfBusinessId});
                                }
                            }

                            /***
                             * Setting value Attribute Additionally to Retrival AgentDetail.ContactPerson Details
                             * For Displaying in the Table
                             */

                            for(i in $scope.agentDetails.contactPersonDetails){
                                if($scope.agentDetails.contactPersonDetails[i].lineOfBusinessId){

                                    var retContact = _.findWhere($scope.lineOfBusinessListCopy, {lineOfBusinessId: $scope.agentDetails.contactPersonDetails[i].lineOfBusinessId});
                                    if(retContact){
                                        $scope.agentDetails.contactPersonDetails[i]=angular.extend($scope.agentDetails.contactPersonDetails[i],{value:retContact.value});
                                    }
                                    //$scope.agentDetails.contactPersonDetails[i].lineOfBusinessId;
                                }

                            }
                        }
                    });


                console.log($scope.agentDetails);
                console.log('**************'+JSON.stringify($scope.agentDetails));
            }
            $scope.contact = {};
            $scope.titleList = globalConstants.title;
            $scope.primaryCities = [];
            $scope.physicalCities = [];
            $scope.count = 0;
            $scope.errorMsg = false;
            $scope.contactPersonStatus=false;

            $scope.tabOneEnableControl=function(tabStatusCheck){
                if(tabStatusCheck && $scope.agentDetails.contactPersonDetails.length >0){
                    $scope.stepsSaved["1"] = true;
                }
                else{
                    $scope.stepsSaved["1"] = false;
                }
            }
            $scope.$watch('agentDetails.physicalAddress.physicalGeoDetail.provinceCode', function (newVal, oldVal) {
                if (newVal) {
                    var provinceDetails = $scope.getProvinceDetails(newVal);
                    $scope.physicalCities = provinceDetails.cities;
                    $scope.agentDetails.physicalAddress.physicalGeoDetail.provinceName = provinceDetails.provinceName;

                }
            });
            $scope.getCurrentVal=function(val){

                $scope.agentDetails.contactDetail.geoDetail.cityCode='';

            }
            $scope.getCurrentValTown=function(val){
                $scope.agentDetails.physicalAddress.physicalGeoDetail.cityCode='';

            }


            $scope.$watch('agentDetails.contactDetail.geoDetail.provinceCode', function (newVal, oldVal) {
                if (newVal) {
                    var provinceDetails = $scope.getProvinceDetails(newVal);
                    $scope.primaryCities = provinceDetails.cities;
                    $scope.agentDetails.contactDetail.geoDetail.provinceName = provinceDetails.provinceName;
                }
            });
            $scope.getProvinceDetails = function (provinceCode) {
                return getProvinceAndCityDetail(provinces, provinceCode);
            };

            $scope.authorisedToSell = authorisedToSell;
            $scope.provinces = provinces;
            $scope.today = new Date();
            $scope.datePickerSettings = {
                isOpened: false,
                dateOptions: {
                    formatYear: 'yyyy',
                    startingDay: 1
                }
            };
            $scope.open = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettings.isOpened = true;
            };

            /*if (!$scope.agentDetails.contactPersonDetails || $scope.agentDetails.contactPersonDetails.length == 0)
                $scope.agentDetails.contactPersonDetails = [{}, {}, {}];*/

            $scope.submit = function () {
                if ($scope.isEditMode) {
                    $http.post("/pla/core/agent/update",
                        transformJson.createCompatibleJson(angular.copy($scope.agentDetails), $scope.physicalCities, $scope.primaryCities, $scope.trainingCompleteOn, true))
                        .success(function (response, status, headers, config) {
                            if (response.status == "200") {
                                $scope.contactDetailsForm.$submitted = true;
                            }
                        })
                        .error(function (response, status, headers, config) {
                        });
                } else {
                    $http.post("/pla/core/agent/createbroker",
                        transformJson.createCompatibleJson(angular.copy($scope.agentDetails), $scope.physicalCities, $scope.primaryCities, $scope.trainingCompleteOn, false))
                        .success(function (response, status, headers, config) {
                            if (response.status == "200") {
                                $scope.contactDetailsForm.$submitted = true;
                            }
                        })
                        .error(function (response, status, headers, config) {
                        });
                }
            };

        }])
    .factory('transformJson', ['formatJSDateToDDMMYYYY', function (formatJSDateToDDMMYYYY) {
        var transformService = {};
        transformService.createCompatibleJson = function (agentDetails, physicalCities, primaryCities, trainingCompleteOn, isUpdate) {
            agentDetails.physicalAddress.physicalGeoDetail.cityName = _.findWhere(physicalCities, {geoId: agentDetails.physicalAddress.physicalGeoDetail.cityCode}).geoName;
            agentDetails.contactDetail.geoDetail.cityName = _.findWhere(primaryCities, {geoId: agentDetails.contactDetail.geoDetail.cityCode}).geoName;
            if (!isUpdate) {
                agentDetails.agentProfile.trainingCompleteOn = formatJSDateToDDMMYYYY(trainingCompleteOn);
                delete agentDetails.agentStatus;
            }
            if (agentDetails.teamDetail) {
                if (agentDetails.teamDetail.regionName) {
                    delete agentDetails.teamDetail.regionName;
                    delete agentDetails.teamDetail.branchName;
                }
            }

            if (!agentDetails.licenseNumber || _.size(agentDetails.licenseNumber.licenseNumber) == 0) {
                delete agentDetails.licenseNumber;
            }
            return agentDetails;


        };

        transformService.toPlanIdPlanNameObject = function (authorisedToSell) {
            var plans = [];
            angular.forEach(authorisedToSell, function (plan, key) {
                this.push({planId: plan.planId, planName: plan.planDetail.planName});
            }, plans);
            return plans;
        };
        transformService.fromHrmsToPla = function (agentDetails) {
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
                        "provinceName": "",
                        "postalCode": agentDetails.primaryContactDetail.postalCode,
                        "cityName": "",
                        "provinceCode": agentDetails.primaryContactDetail.province,
                        "cityCode": agentDetails.primaryContactDetail.city
                    }
                },
                "physicalAddress": {
                    "physicalAddressLine1": agentDetails.physicalContactDetail.addressLine1,
                    "physicalAddressLine2": agentDetails.physicalContactDetail.addressLine2,
                    "physicalGeoDetail": {
                        "provinceName": "",
                        "postalCode": agentDetails.physicalContactDetail.postalCode,
                        "cityName": "",
                        "provinceCode": agentDetails.physicalContactDetail.province,
                        "cityCode": agentDetails.physicalContactDetail.city
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
    .config(["$routeProvider", function ($routeProvider) {
        $routeProvider.when('/', {
            templateUrl: 'createBrokerTpl.html',
            controller: 'brokerController',
            resolve: {
                nextAgentSequence: ['$q', '$http', '$window', function ($q, $http, $window) {
                    if ($window.location.href.indexOf("/edit") > -1) {
                        return null;
                    } else {
                        var deferred = $q.defer();
                        $http.get('/pla/core/agent/getagentid').success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        return deferred.promise;
                    }
                }],
                agentDetails: ['$q', '$http', 'getQueryParameter', '$window', function ($q, $http, getQueryParameter, $window) {
                    var queryParam = {'agentId': getQueryParameter('agentId')};
                    // console.log('AGENT ID == ' + getQueryParameter('agentId'));
                    if (angular.isDefined(getQueryParameter('agentId')) && getQueryParameter('agentId') != null) {
                        var deferred = $q.defer();
                        $http.get('/pla/core/agent/agentdetail', {params: queryParam})
                            .success(function (response, status, headers, config) {
                                deferred.resolve(response)
                            })
                            .error(function (response, status, headers, config) {
                                deferred.reject();
                            });
                        return deferred.promise;
                    } else {
                        return {};
                    }
                }],
                authorisedToSell: ['$q', '$http', 'transformJson', function ($q, $http, transformJson) {
                    var deferred = $q.defer();
                    $http.get('/pla/core/plan/getallplan').success(function (response, status, headers, config) {
                        deferred.resolve(transformJson.toPlanIdPlanNameObject(response))
                    }).error(function (response, status, headers, config) {
                        deferred.reject();
                    });
                    return deferred.promise;
                }],
                provinces: ['$q', '$http', function ($q, $http) {
                    var deferred = $q.defer();
                    $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                        deferred.resolve(response)
                    }).error(function (response, status, headers, config) {
                        deferred.reject();
                    });
                    return deferred.promise;
                }]
            }
        });
    }])




