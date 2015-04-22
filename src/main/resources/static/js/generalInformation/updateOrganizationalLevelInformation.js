var App = angular.module('updateOrganizationalLevelInfo', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('UpdateOrganizationalLevelController',['$scope','$http','$templateCache','$timeout','$alert','$window',function($scope,$http,$templateCache,$timeout,$alert,$window){
    $scope.organizationList=[];
    $scope.organizationLevelInformation={};
    $scope.updateOrganizationinfo=false;
    var createObject = function(){
        var transformedObject = {};
        angular.forEach($scope.organizationLevelInformation,function(value,key){

            if(key!='serviceTax'){
                transformedObject[key] =[value];
            }else{
                transformedObject[key] = value;
            }

        })
        return transformedObject;
    }
    $http.get('/pla/core/organizationinformation/getorganizationformation').success(function(data){

       //$scope.organizationInformation=data[0];
        $scope.organizationLevelInformation=data[0];
     //  console.log($scope.organizationLevelInformation);
        if($scope.organizationLevelInformation.organizationInformationId!=null){
            $scope.updateOrganizationinfo=true;
        }else{
            $scope.updateOrganizationinfo=false;
        }

    });

    $scope.submitOrganizationLevelInfo = function(){
        //console.log(createObject());
   //console.log( $scope.organizationLevelInformation);
           $http.post('/pla/core/organizationinformation/update',  $scope.organizationLevelInformation).success(function (data) {
                console.log(data.status);
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
    var createObject = function(){
        var transformedObject = {};
        angular.forEach($scope.createOrganizationLevelInformation,function(value,key){

            if(key=='modelFactorItems'){
                transformedObject[key]=[{"SEMI_ANNUAL":$scope.createOrganizationLevelInformation.modelFactorItems.SEMI_ANNUAL},{"QUARTERLY" :$scope.createOrganizationLevelInformation.modelFactorItems.QUARTERLY},{"MONTHLY":$scope.createOrganizationLevelInformation.modelFactorItems.MONTHLY}];
            }else if(key=='discountFactorItems'){
                transformedObject[key]=[{"SEMI_ANNUAL":$scope.createOrganizationLevelInformation.discountFactorItems.SEMI_ANNUAL},{"ANNUAL":$scope.createOrganizationLevelInformation.discountFactorItems.ANNUAL},{"QUARTERLY":$scope.createOrganizationLevelInformation.discountFactorItems.QUARTERLY}];

            }else{
               transformedObject[key] = value;
            }
        })

        return transformedObject;

    }



    $scope.createOrganizationLevelInfo = function() {
    //console.log(createObject());

       $http.post('/pla/core/organizationinformation/create', createObject()).success(function (data) {

            if (data.status == 200) {
                $scope.alert = {title: 'Success Message! ', content: data.message, type: 'success'};
                // $scope.reset();
               // $window.location.reload();
               // $window.location.href="/pla/core/organizationinformation/openview";

            } else if (data.status == 500) {
                $scope.alert = {title: 'Error Message! ', content: data.message, type: 'danger'};
            }

        });
    }

}]);