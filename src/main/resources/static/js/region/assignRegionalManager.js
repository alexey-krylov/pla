var App = angular.module('assignRegionalManager', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('AssignRegionalManagerController',['$scope','$http','$window','$location',function($scope,$http,$window,$location){

        $scope.selectedDate =moment().add(1,'days').format("YYYY-MM-DD");

        $scope.datePickerSettings = {
               isOpened:false,
               dateOptions:{
               formatYear:'yyyy' ,
               startingDay:1
               }
        }
        $scope.open = function($event) {
               $event.preventDefault();
               $event.stopPropagation();
               $scope.datePickerSettings.isOpened = true;
        };
        $scope.url = window.location.search.split('=')[1];
        $http.get('/pla/core/region/getregiondetail?regionId='+$scope.url).success(function(data){
              //console.log(data);
              $scope.assignRegionalManager=data;
        });

        $http.get('/pla/core/region/getallregionalmanager').success(function(data){
              $scope.regionalManagerList=data;
        });
        $scope.submitAssign=function(){
               if (!moment($scope.assignRegionalManager.fromDate,'DD/MM/YYYY').isValid()) {
                  $scope.newDateField = moment($scope.assignRegionalManager.fromDate).format("DD/MM/YYYY");
                  $scope.assignRegionalManager.fromDate=$scope.newDateField ;
               }
             $http.get('/pla/core/region/getallregionalmanager').success(function(data){
                   $scope.regionalManagerList=data;
                   var id=$scope.assignRegionalManager.employeeId;
                   $scope.newRegionalManager =_.findWhere($scope.regionalManagerList,{employeeId:id});
                   $scope.assignRegionalManager.firstName=$scope.newRegionalManager.firstName;
                   $scope.assignRegionalManager.lastName=$scope.newRegionalManager.lastName;
                   $http.post('/pla/core/region/assign', $scope.assignRegionalManager).success(function(data){
                        if(data.status==200){
                             $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
                             $scope.reset();
                        }else{
                             $scope.alert = {title:'Error Message! ', content:data.message, type: 'danger'};
                        }
                   });
             });
            // console.log("**************");
           // console.log($scope.assignRegionalManager);

        }
        $scope.reset=function(){
           $scope.assignRegionalManager.fromDate='';
           $scope.assignRegionalManager.employeeId='';
           $scope.assignBranchManager.regionName='';
           $scope.assignBranchManager.regionalManagerName='';
           $scope.assignBranchManager.regionalManagerFromDate='';
        }
}]);
