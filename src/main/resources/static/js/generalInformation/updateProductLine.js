var App = angular.module('updateProductLine', ['common','ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('SelectUpdateController',['$rootScope','$scope','$http','$templateCache','$timeout','$alert','$window','$location',function($rootScope,$scope,$http,$templateCache,$timeout,$alert,$window,$location) {
   // console.log("******************************SELECT UPDATE CONTROLLER CALLED*********************************************")
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
App.controller('UpdateGroupHealthLevelInformationController',['$rootScope','$scope','$http','$templateCache','$timeout','$alert','$window','$location',function($rootScope,$scope,$http,$templateCache,$timeout,$alert,$window,$location) {
    $scope.forms = {};
    $scope.groupHealth=false;
    $rootScope.$watch('PanelName',function(n,o){
        if(n=='groupHealth'){
            $scope.groupHealth=true;
        }
    });
   /* $scope.$watch('forms.form1.$valid', function(n,o) {
        if(n){
            console.log("watching " + $scope.forms.form1.$valid);

        }
    });*/
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


    /* $scope.$watch('forms.form1.$valid', function() {
         console.log("watching " + $scope.forms.form1.$valid);
     });*/
   $http.get('/pla/core/productlineinformation/getproducrlineinformation').success(function(data){
      //  console.log(data);

    });
    $http.get('/pla/core/productlineinformation/getproductlineprocessitem').success(function(data){
       // console.log(data);

    });

   /* $scope.submitGroupHealth=function(){
        $http.post('/pla/core/productlineinformation/update', $scope.items).success(function(data){
            if(data.status==200){
                $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
                //  $scope.reset();
            }else if(data.status==500){
                $scope.alert = {title:'Error Message! ', content:data.message, type: 'danger'};
            }
        });

    }*/

}]);
App.controller('UpdateGroupLifeLevelInformationController',['$rootScope','$scope','$http','$templateCache','$timeout','$alert','$window','$location',function($rootScope,$scope,$http,$templateCache,$timeout,$alert,$window,$location) {
    $scope.forms = {};

    $scope.groupLife=false;
    $rootScope.$watch('PanelName',function(n,o){
        if(n=='groupLife'){
            $scope.groupLife=true;
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

    /* $scope.$watch('forms.form1.$valid', function() {
     console.log("watching " + $scope.forms.form1.$valid);
     });*/
  /*  $http.get('/pla/core/productlineinformation/getproductlineprocessitem').success(function(data){
       // console.log(data);
       // $scope.groupHealth=data;
    });

    $http.get('/pla/core/productlineinformation/getproducrlineinformation').success(function(data){
       // console.log(data);
       // $scope.groupHealth=data;
    });
    $scope.submitGroupHealth=function() {
              console.log($scope.items)
        $http.post('/pla/core/productlineinformation/update', $scope.items).success(function (data) {
            if (data.status == 200) {
                $scope.alert = {title: 'Success Message! ', content: data.message, type: 'success'};
                //  $scope.reset();
            } else if (data.status == 500) {
                $scope.alert = {title: 'Error Message! ', content: data.message, type: 'danger'};
            }
        });
    }*/

}]);
App.controller('UpdateIndividualLifeLevelInformationController',['$rootScope','$scope','$http','$templateCache','$timeout','$alert','$window','$location',function($rootScope,$scope,$http,$templateCache,$timeout,$alert,$window,$location) {

    $scope.individualLife=false;
    $rootScope.$watch('PanelName',function(n,o){
        if(n=='individualLife'){
            $scope.individualLife=true;
        }
    });


    $http.get('/pla/core/productlineinformation/getproductlineinformationitem').success(function(data){
        console.log(data);
        $scope.groupHealth=data;
    });

    $scope.submitGroupHealth=function() {
        $http.post('/pla/core/productlineinformation/update', $scope.groupHealth).success(function (data) {
            if (data.status == 200) {
                $scope.alert = {title: 'Success Message! ', content: data.message, type: 'success'};
                //  $scope.reset();
            } else if (data.status == 500) {
                $scope.alert = {title: 'Error Message! ', content: data.message, type: 'danger'};
            }
        });
    }

}]);