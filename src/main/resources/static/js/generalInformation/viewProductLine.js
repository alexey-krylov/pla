var App = angular.module('viewProductLine', ['common','ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('SelectViewController',['$rootScope','$scope','$http','$templateCache','$timeout','$alert','$window','$location',function($rootScope,$scope,$http,$templateCache,$timeout,$alert,$window,$location) {
   // console.log("******************************SELECT VIEW CONTROLLER CALLED*********************************************")
    $scope.selectUpdate=true;

    $scope.$watch('selectedOption.productLine', function (newValue, oldValue) {
        if (newValue) {

            if(newValue=="groupHealth"){
                $rootScope.PanelName ="groupHealth";

                $scope.selectUpdate=false;

            }else if(newValue=="groupLife"){
                $rootScope.PanelName ="groupLife";
                $scope.selectUpdate=false;
            }else if(newValue=="individualLife"){
                $rootScope.PanelName ="individualLife";
                $scope.selectUpdate=false;
            }

        }

    });


}]);
App.controller('ViewGroupHealthLevelInformationController',['$rootScope','$scope','$http','$templateCache','$timeout','$alert','$window','$location',function($rootScope,$scope,$http,$templateCache,$timeout,$alert,$window,$location) {

    $scope.groupHealth=false;
    $rootScope.$watch('PanelName',function(n,o){
        if(n=='groupHealth'){
            $scope.groupHealth=true;
        }
    });
    $scope.items = [
        {
            "productLine": "GROUP_HEALTH",
            "processType": [
                {
                    "quotationProcessItems": [
                        {
                            "productLineProcessItem": "GAP",
                            "value": 4
                        },
                        {
                            "productLineProcessItem": "NO_OF_REMAINDER",
                            "value": 3
                        },
                        {
                            "productLineProcessItem": "CLOSURE",
                            "value": 6
                        },
                        {
                            "productLineProcessItem": "PURGE_TIME_PERIOD",
                            "value": 1
                        },
                        {
                            "productLineProcessItem": "FIRST_REMAINDER",
                            "value": 2
                        }
                    ]
                },
                {
                    "enrollmentProcessItems": [
                        {
                            "productLineProcessItem": "GAP",
                            "value": 4
                        },
                        {
                            "productLineProcessItem": "NO_OF_REMAINDER",
                            "value": 3
                        },
                        {
                            "productLineProcessItem": "CLOSURE",
                            "value": 6
                        },
                        {
                            "productLineProcessItem": "PURGE_TIME_PERIOD",
                            "value": 1
                        },
                        {
                            "productLineProcessItem": "FIRST_REMAINDER",
                            "value": 2
                        }
                    ]
                },
                {
                    "reinstatementProcessItems": [
                        {
                            "productLineProcessItem": "GAP",
                            "value": 4
                        },
                        {
                            "productLineProcessItem": "NO_OF_REMAINDER",
                            "value": 3
                        },
                        {
                            "productLineProcessItem": "CLOSURE",
                            "value": 6
                        },
                        {
                            "productLineProcessItem": "PURGE_TIME_PERIOD",
                            "value": 1
                        },
                        {
                            "productLineProcessItem": "FIRST_REMAINDER",
                            "value": 2
                        }
                    ]
                },
                {
                    "endorsementProcessItems": [
                        {
                            "productLineProcessItem": "GAP",
                            "value": 4
                        },
                        {
                            "productLineProcessItem": "NO_OF_REMAINDER",
                            "value": 3
                        },
                        {
                            "productLineProcessItem": "CLOSURE",
                            "value": 6
                        },
                        {
                            "productLineProcessItem": "PURGE_TIME_PERIOD",
                            "value": 1
                        },
                        {
                            "productLineProcessItem": "FIRST_REMAINDER",
                            "value": 2
                        }
                    ]
                },
                {
                    "claimProcessItems": [
                        {
                            "productLineProcessItem": "GAP",
                            "value": 4
                        },
                        {
                            "productLineProcessItem": "NO_OF_REMAINDER",
                            "value": 3
                        },
                        {
                            "productLineProcessItem": "CLOSURE",
                            "value": 6
                        },
                        {
                            "productLineProcessItem": "PURGE_TIME_PERIOD",
                            "value": 1
                        },
                        {
                            "productLineProcessItem": "FIRST_REMAINDER",
                            "value": 2
                        },
                        {
                            "productLineProcessItem": "EARLY_DEATH_CRITERIA",
                            "value": 15
                        }
                    ]
                },
                {
                    "policyFeeProcessItems": [
                        {
                            "policyFeeProcessType": "ANNUAL",
                            "policyFee": 1
                        },
                        {
                            "policyFeeProcessType": "MONTHLY",
                            "policyFee": 4
                        },
                        {
                            "policyFeeProcessType": "QUARTERLY",
                            "policyFee": 3
                        },
                        {
                            "policyFeeProcessType": "SEMI_ANNUAL",
                            "policyFee": 2
                        }
                    ]
                },
                {
                    "policyProcessMinimumLimitItems": [
                        {
                            "policyProcessMinimumLimitType": "ANNUAL",
                            "noOfPersonPerPolicy": 18,
                            "minimumPremium": 20
                        },
                        {
                            "policyProcessMinimumLimitType": "SEMI_ANNUAL",
                            "noOfPersonPerPolicy": 3,
                            "minimumPremium": 15
                        }
                    ]
                },
                {
                    "surrenderProcessItems": [
                        {
                            "productLineProcessItem": "GAP",
                            "value": 4
                        },
                        {
                            "productLineProcessItem": "NO_OF_REMAINDER",
                            "value": 3
                        },
                        {
                            "productLineProcessItem": "CLOSURE",
                            "value": 6
                        },
                        {
                            "productLineProcessItem": "PURGE_TIME_PERIOD",
                            "value": 1
                        },
                        {
                            "productLineProcessItem": "FIRST_REMAINDER",
                            "value": 2
                        }
                    ]
                },
                {
                    "maturityProcessItems": [
                        {
                            "productLineProcessItem": "GAP",
                            "value": 4
                        },
                        {
                            "productLineProcessItem": "NO_OF_REMAINDER",
                            "value": 3
                        },
                        {
                            "productLineProcessItem": "CLOSURE",
                            "value": 6
                        },
                        {
                            "productLineProcessItem": "PURGE_TIME_PERIOD",
                            "value": 1
                        },
                        {
                            "productLineProcessItem": "FIRST_REMAINDER",
                            "value": 2
                        }
                    ]
                }
            ]
        }
    ];



}]);
App.controller('ViewGroupLifeLevelInformationController',['$rootScope','$scope','$http','$templateCache','$timeout','$alert','$window','$location',function($rootScope,$scope,$http,$templateCache,$timeout,$alert,$window,$location) {

    $scope.groupLife=false;
    $rootScope.$watch('PanelName',function(n,o){
        if(n=='groupLife'){
            $scope.groupLife=true;
        }
    });

}]);
App.controller('ViewIndividualLifeLevelInformationController',['$rootScope','$scope','$http','$templateCache','$timeout','$alert','$window','$location',function($rootScope,$scope,$http,$templateCache,$timeout,$alert,$window,$location) {

    $scope.individualLife=false;
    $rootScope.$watch('PanelName',function(n,o){
        if(n=='individualLife'){
            $scope.individualLife=true;
        }
    });

}]);