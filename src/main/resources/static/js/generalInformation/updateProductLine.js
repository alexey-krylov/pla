var App = angular.module('updateProductLine', ['common','ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('SelectUpdateController',['$rootScope','$scope','$http','$templateCache','$timeout','$alert','$window','$location',function($rootScope,$scope,$http,$templateCache,$timeout,$alert,$window,$location) {
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
    $scope.items={};
    $scope.groupHealth=false;
    $rootScope.$watch('PanelName',function(n,o){
        if(n=='groupHealth'){
            $scope.groupHealth=true;
        }
    });

    $scope.updatePanelLabel=false;
    $scope.createPanelLabel=false;
   $http.get('/pla/core/productlineinformation/getproductlineinformation').success(function(data){
      //  console.log(data);
       $scope.groupHealthData=data;
       var productLineName="GROUP_HEALTH";
      $scope.items =_.findWhere($scope.groupHealthData,{productLine:productLineName});
      // console.log($scope.items.productLineInformationId);should chk for id in update page

      // console.log($scope.items);
       if($scope.items.productLineInformationId !=null){
           $scope.updatePanelLabel=true;
       }else{
           $scope.createPanelLabel=true;

       }

    });

    $scope.reloadPage = function(){
        $window.location.href="/pla/core/productlineinformation/openupdate";

    }

    $http.get('/pla/core/productlineinformation/getproductlineprocessitem').success(function(data){
        $scope.processItems=data;

    });
    $scope.fieldData={};
    $scope.getFieldName = function(fieldtype){
  //  console.log(fieldtype);
         if(fieldtype){
             $scope.fieldData=_.findWhere($scope.processItems,{type:fieldtype});
             if($scope.fieldData)
                 return $scope.fieldData.description;
            }
    }

    $scope.submitGroupHealth=function(){
        ///console.log($scope.items);
       // console.log("*****************************AFTER CHANGING THE OBJECT *************************");

       if($scope.items.productLineInformationId !=null) {
            $http.post('/pla/core/productlineinformation/update',$scope.items).success(function (data) {
                if (data.status == 200) {
                    $scope.alert = {title: 'Success Message! ', content: data.message, type: 'success'};
                    //  $scope.reset();
                    $window.location.href="/pla/core/productlineinformation/openupdate";

                } else if (data.status == 500) {
                    $scope.alert = {title: 'Error Message! ', content: data.message, type: 'danger'};
                }
            });
        }else{

            $http.post('/pla/core/productlineinformation/create', $scope.items).success(function (data) {
                if (data.status == 200) {
                    $scope.alert = {title: 'Success Message! ', content: data.message, type: 'success'};
                    //  $scope.reset();
                    $window.location.href="/pla/core/productlineinformation/openupdate";

                } else if (data.status == 500) {
                    $scope.alert = {title: 'Error Message! ', content: data.message, type: 'danger'};
                }
            });
        }

    }

}]);
App.controller('UpdateGroupLifeLevelInformationController',['$rootScope','$scope','$http','$templateCache','$timeout','$alert','$window','$location',function($rootScope,$scope,$http,$templateCache,$timeout,$alert,$window,$location) {
    $scope.forms = {};
    $scope.updatePanelLabel=false;
    $scope.createPanelLabel=false;
    $scope.groupLife=false;
    $rootScope.$watch('PanelName',function(n,o){
        if(n=='groupLife'){
            $scope.groupLife=true;
        }
    });

    $http.get('/pla/core/productlineinformation/getproductlineinformation').success(function(data){
        //  console.log(data);
        $scope.groupLifeData=data;
        var productLineName="GROUP_INSURANCE";
        $scope.items =_.findWhere($scope.groupLifeData,{productLine:productLineName});
        // console.log($scope.items.productLineInformationId);should chk for id in update page

        // console.log($scope.items);

        if($scope.items.productLineInformationId !=null){
            $scope.updatePanelLabel=true;
        }else{
            $scope.createPanelLabel=true;

        }

    });
    $http.get('/pla/core/productlineinformation/getproductlineprocessitem').success(function(data){
        $scope.processItems=data;

    });

    $scope.fieldData={};
    $scope.getFieldName = function(fieldtype){
        //  console.log(fieldtype);
        if(fieldtype){
            $scope.fieldData=_.findWhere($scope.processItems,{type:fieldtype});
            if($scope.fieldData)
                return $scope.fieldData.description;
        }
    }

    $scope.reloadPage = function(){
        $window.location.href="/pla/core/productlineinformation/openupdate";

    }

    $scope.submitGroupLife=function(){
        if($scope.items.productLineInformationId !=null) {
            $http.post('/pla/core/productlineinformation/update', $scope.items).success(function (data) {
                if (data.status == 200) {
                    $scope.alert = {title: 'Success Message! ', content: data.message, type: 'success'};
                    //  $scope.reset();
                    $window.location.href="/pla/core/productlineinformation/openupdate";

                } else if (data.status == 500) {
                    $scope.alert = {title: 'Error Message! ', content: data.message, type: 'danger'};
                }
            });
        }else{

            $http.post('/pla/core/productlineinformation/create', $scope.items).success(function (data) {
                if (data.status == 200) {
                    $scope.alert = {title: 'Success Message! ', content: data.message, type: 'success'};
                    //  $scope.reset();
                    $window.location.href="/pla/core/productlineinformation/openupdate";

                } else if (data.status == 500) {
                    $scope.alert = {title: 'Error Message! ', content: data.message, type: 'danger'};
                }
            });
        }

    }

}]);
App.controller('UpdateIndividualLifeLevelInformationController',['$rootScope','$scope','$http','$templateCache','$timeout','$alert','$window','$location',function($rootScope,$scope,$http,$templateCache,$timeout,$alert,$window,$location) {

    $scope.updatePanelLabel=false;
    $scope.createPanelLabel=false;
    $scope.individualLife=false;
    $rootScope.$watch('PanelName',function(n,o){
        if(n=='individualLife'){
            $scope.individualLife=true;
        }
    });

    $http.get('/pla/core/productlineinformation/getproductlineinformation').success(function(data){
        //  console.log(data);
        $scope.individualLifeData=data;
        var productLineName="INDIVIDUAL_INSURANCE";
        $scope.items =_.findWhere($scope.individualLifeData,{productLine:productLineName});
        // console.log($scope.items.productLineInformationId);should chk for id in update page

        // console.log($scope.items);
        if($scope.items.productLineInformationId !=null){
            $scope.updatePanelLabel=true;
        }else{
            $scope.createPanelLabel=true;

        }

    });
    $http.get('/pla/core/productlineinformation/getproductlineprocessitem').success(function(data){
        $scope.processItems=data;

    });

    $scope.fieldData={};
    $scope.getFieldName = function(fieldtype){
        //  console.log(fieldtype);
        if(fieldtype){
            $scope.fieldData=_.findWhere($scope.processItems,{type:fieldtype});
            if($scope.fieldData)
                return $scope.fieldData.description;
        }
    }

    $scope.reloadPage = function(){
        $window.location.href="/pla/core/productlineinformation/openupdate";

    }

    $scope.submitIndividualLife=function() {
        if($scope.items.productLineInformationId !=null) {
            $http.post('/pla/core/productlineinformation/update', $scope.items).success(function (data) {
                if (data.status == 200) {
                    $scope.alert = {title: 'Success Message! ', content: data.message, type: 'success'};
                    //  $scope.reset();
                    $window.location.href="/pla/core/productlineinformation/openupdate";

                } else if (data.status == 500) {
                    $scope.alert = {title: 'Error Message! ', content: data.message, type: 'danger'};
                }
            });
        }else{

            $http.post('/pla/core/productlineinformation/create', $scope.items).success(function (data) {
                if (data.status == 200) {
                    $scope.alert = {title: 'Success Message! ', content: data.message, type: 'success'};
                    //  $scope.reset();
                    $window.location.href="/pla/core/productlineinformation/openupdate";

                } else if (data.status == 500) {
                    $scope.alert = {title: 'Error Message! ', content: data.message, type: 'danger'};
                }
            });
        }

    }



}]);