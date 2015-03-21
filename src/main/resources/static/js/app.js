var AngularApp = {};
var App = angular.module('AngularApp', ['ngRoute','ui.bootstrap','mgcrea.ngStrap.select','ngSanitize','angularFileUpload']);

 App.controller('ViewTeamController',['$scope','$http',function($scope,$http){
    $scope.teamList={};
    $http.get('http://localhost:6443/pla/core/team/view').success(function(data){
                   // console.log(data);
           //$scope.teamList= data;
    });
    $scope.openAssignTeam = function(teamId){
   var id=parseInt(teamId);
        $http.get('http://localhost:6443/pla/core/team/openAssignPage/'+id).success(function(data){
                          //   console.log(data);
                   $scope.teamList= data;
            });

    }
    $scope.openCreateTeam = function(){
       // var id=parseInt(teamId);
            $http.get('http://localhost:6443/pla/core/team/openCreatePage').success(function(data){
                              //   console.log(data);
                      // $scope.teamList= data;
                });

        }

 }]);
 App.controller('AssignTeamController',['$scope','$http',function($scope,$http){
     $scope.newDateField={};
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
     $scope.submitAssignTeam = function(){
         if (!moment($scope.assignTeam.fromDate,'DD/MM/YYYY').isValid()) {
         	    $scope.newDateField.fromDate = moment($scope.assignTeam.fromDate).format("DD/MM/YYYY");
            	$scope.assignTeam.fromDate=$scope.newDateField.fromDate ;
         }
         console.log($scope.assignTeam);
         $http.post('http://localhost:6443/pla/core/team/assign', $scope.assignTeam).success(function(data){
                 console.log(data);

         });
     }

  }]);



 App.controller('CreateTeamController',['$scope','$http',function($scope,$http){
     //   console.log("called CreateTeamController................");
       	$scope.newDateField={};
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

         $scope.getBranchList = function(regionName){
                console.log(regionName);
          		//$scope.branchList =_.findWhere($scope.employeeDetails,{regionName:regionName});
        }
         $scope.submitTeam = function(){

             if (!moment($scope.createTeam.fromDate,'DD/MM/YYYY').isValid()) {
             			$scope.newDateField.fromDate = moment($scope.createTeam.fromDate).format("DD/MM/YYYY");
             			$scope.createTeam.fromDate=$scope.newDateField.fromDate ;

             }
             $scope.testTeamData={'teamName':'team A','teamCode':'123','regionCode':'555','branchCode':'666','employeeId':'100','fromDate':'02/05/2015','firstName':'Sara','lastName':'ali'}

             $http.post('http://localhost:6443/pla/core/team/create', $scope.testTeamData).success(function(data){
                                     console.log(data);


             });
            // console.log($scope.createTeam);

             $scope.reset();

         };
         $scope.reset = function(){

             $scope.branchName ='';
             $scope.fromDate ='';
         	 $scope.regionName ='';
         	 $scope.teamCode ='';
         	 $scope.teamLeader ='';
         	 $scope.teamName ='';
         	 }


 }]);
 App.controller('AssignRegionalManagerController',['$scope','$http',function($scope,$http){
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

  }]);
  App.controller('AssignBranchManagerController',['$scope','$http',function($scope,$http){
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

   }]);
 App.controller('CreateHealthCareProviderController',['$scope','$http',function($scope,$http){

    }]);

App.controller('UpdateHealthCareProviderController',['$scope','$http',function($scope,$http){


  }]);
