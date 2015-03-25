var App = angular.module('assignTeam', ['ngRoute','ui.bootstrap','mgcrea.ngStrap.select']);

App.controller('AssignTeamController',['$scope','$http','$window','$location',function($scope,$http,$window,$location){

     $scope.selectedDate =moment(new Date).format("YYYY-MM-DD");
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
     $http.get('http://localhost:6443/pla/core/team/getteamleaders').success(function(data){
                   console.log(data);
                   $scope.teamLeaders=data;
     });
      $scope.url = window.location.search.split('=')[1];
      // console.log("WINDOW LOCATION-------"+$scope.url.split('=')[1]);

         // var tid = $window.teamId;
    // var id=parseInt(tid);
     $http.get('http://localhost:6443/pla/core/team/openAssignPage?teamId='+$scope.url).success(function(data){
                   console.log(data);
                  // $scope.assignTeam=data;
      });
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
