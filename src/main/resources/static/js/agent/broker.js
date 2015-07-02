angular.module('brokerModule', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'commonServices', 'ngMessages'])
    .controller('brokerController', ['$scope', '$http', 'authorisedToSell', 'provinces', '$timeout', '$alert', '$route', '$window', 'transformJson',
        'getQueryParameter', 'agentDetails', 'globalConstants', 'nextAgentSequence', 'getProvinceAndCityDetail',


        function ($scope, $http,
                  authorisedToSell, provinces, $timeout, $alert, $route, $window, transformJson, getQueryParameter, agentDetails, globalConstants, nextAgentSequence, getProvinceAndCityDetail) {
            $scope.numberPattern = globalConstants.numberPattern;

            $scope.agentDetails = {agentId:nextAgentSequence};

            if (!$scope.agentDetails.contactPersonDetails || $scope.agentDetails.contactPersonDetails.length == 0)
                $scope.agentDetails.contactPersonDetails = [{}, {}, {}];

            $scope.isEditMode = false;
            $scope.titleList = globalConstants.title;
            $scope.primaryCities = [];
            $scope.physicalCities = [];
            $scope.$watch('agentDetails.physicalAddress.physicalGeoDetail.provinceCode', function (newVal, oldVal) {
                if (newVal) {
                    var provinceDetails = $scope.getProvinceDetails(newVal);
                    $scope.physicalCities = provinceDetails.cities;
                    $scope.agentDetails.physicalAddress.physicalGeoDetail.provinceName = provinceDetails.provinceName;

                }
            });

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


            if (!$scope.agentDetails.contactPersons || $scope.agentDetails.contactPersons.length == 0)
                $scope.agentDetails.contactPersons = [{}, {}, {}];

            $scope.submit = function () {
                $http.post("/pla/core/agent/createbroker", $scope.agentDetails)
                    .success(function (response, status, headers, config) {
                        if (response.status == "200") {
                        }
                    })
                    .error(function (response, status, headers, config) {
                    });
            }

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
                    if ($window.location.href.indexOf("/openeditpage") > -1) {
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
                    if ($window.location.href.indexOf("/openeditpage") > -1) {
                        var queryParam = {'agentId': getQueryParameter('agentId')};
                        var deferred = $q.defer();
                        $http.get('/pla/core/agent/agentdetail', {params: queryParam}).success(function (response, status, headers, config) {
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        return deferred.promise;
                    } else {
                        return [];
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
        })
    }])




