var App = angular.module('updateOrganizationalLevelInfo', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('UpdateOrganizationalLevelController',['$scope','$http','$templateCache','$timeout','$alert','$window',function($scope,$http,$templateCache,$timeout,$alert,$window){
    $scope.organizationList=[];
    $scope.organizationLevelInformation={};
    $scope.updateOrganizationinfo=false;

    $http.get('/pla/core/organizationinformation/getorganizationformation').success(function(data){

       $scope.organizationInformation=data[0];

     //  console.log($scope.organizationLevelInformation);
        if($scope.organizationInformation.organizationInformationId!=null){
            $scope.updateOrganizationinfo=true;
        }else{
            $scope.updateOrganizationinfo=false;
        }

    });
    $http.get('/pla/core/organizationinformation/getorganizationprocessitem').success(function(data){
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
        $window.location.href="/pla/core/organizationinformation/openview";

    }

    $scope.submitOrganizationLevelInfo = function(){

           $http.post('/pla/core/organizationinformation/update',  $scope.organizationInformation).success(function (data) {

                if (data.status == 200) {

                    $scope.alert = {title: 'Success Message! ', content: data.message, type: 'success'};
                    // $scope.reset();
                   // $window.location.reload();
                    $window.location.href="/pla/core/organizationinformation/openview";

                } else if (data.status == 500) {
                    $scope.alert = {title: 'Error Message! ', content: data.message, type: 'danger'};
                }

            });


    };



}]);
App.controller('ViewOrganizationalLevelController',['$scope','$http','$templateCache','$timeout','$alert','$window',function($scope,$http,$templateCache,$timeout,$alert,$window){
    $http.get('/pla/core/organizationinformation/getorganizationformation').success(function(data){
       $scope.organizationInformation=data[0];

    });

}]);
App.controller('CreateOrganizationalLevelController',['$scope','$http','$templateCache','$timeout','$alert','$window',function($scope,$http,$templateCache,$timeout,$alert,$window){
    $scope.createOrganizationinfo=false;
    $http.get('/pla/core/organizationinformation/getorganizationformation').success(function(data){
        $scope.organizationInformation=data[0];

        if($scope.organizationInformation.organizationInformationId==null){
            $scope.createOrganizationinfo=true;
        }else{
            $scope.createOrganizationinfo=false;
        }
    });
    $http.get('/pla/core/organizationinformation/getorganizationprocessitem').success(function(data){
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
        $window.location.href="/pla/core/organizationinformation/openview";

    }

    $scope.createOrganizationLevelInfo = function() {
       $http.post('/pla/core/organizationinformation/create', $scope.organizationInformation).success(function (data) {

            if (data.status == 200) {
                $scope.alert = {title: 'Success Message! ', content: data.message, type: 'success'};
                // $scope.reset();
               // $window.location.reload();
              $window.location.href="/pla/core/organizationinformation/openview";

            } else if (data.status == 500) {
                $scope.alert = {title: 'Error Message! ', content: data.message, type: 'danger'};
            }

        });
    }

}]);