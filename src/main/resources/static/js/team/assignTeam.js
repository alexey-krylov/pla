var App = angular.module('assignTeam', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('AssignTeamController',['$scope','$http','$window','$location','$alert',function($scope,$http,$window,$location,$alert){

     $scope.currentTeamLeader;
     $scope.currentFromDate;
     $scope.teamLeaders;
     //$scope.boolVal=false;
     $scope.selectedDate =moment().add(1,'days').format("YYYY-MM-DD");
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

      $scope.url = window.location.search.split('=')[1];
      $http.get('/pla/core/team/getteamdetail?teamId='+$scope.url).success(function(data){
                  // console.log(data);
                   $scope.assignTeam=data;
                   $scope.currentFromDate=moment($scope.assignTeam.fromDate).format("DD/MM/YYYY");
                   $scope.assignTeam.fromDate= $scope.currentFromDate;
                   var empId = $scope.assignTeam.currentTeamLeader;
                   $http.get('/pla/core/team/getteamleaders').success(function(data){
                               $scope.teamLeaders=data;
                               $scope.currentTeamLeader =_.findWhere($scope.teamLeaders,{employeeId:empId});

                        });
      });

    $scope.getNewTeamLeader = function(teamLeaderId){
           if(teamLeaderId != $scope.assignTeam.currentTeamLeader ){
                 $scope.assignTeam.employeeId=teamLeaderId;
            }else{
            $scope.assignTeam.employeeId='';
           }
     }

       $scope.$watch( 'assignTeam.employeeId',function(newValue, oldValue){
         if(newValue){
            $http.get('/pla/core/team/getteamleaders').success(function(data){
               $scope.teamLeaders=data;
               var empId = $scope.assignTeam.employeeId;
               if(empId){
                 $scope.newTeamLeader =_.findWhere($scope.teamLeaders,{employeeId:empId});
                 $scope.newTeamLeader =_.findWhere($scope.teamLeaders,{employeeId:empId});
                 $scope.assignTeam.firstName=$scope.newTeamLeader.firstName;
                 $scope.assignTeam.lastName=$scope.newTeamLeader.lastName;
               }
            });
          }
       });
      $scope.submitAssignTeam = function(){
          if (!moment($scope.assignTeam.teamLeaderFrom,'DD/MM/YYYY').isValid()) {
                   		$scope.newDateField.fromDate = moment($scope.assignTeam.teamLeaderFrom).format("DD/MM/YYYY");
                   		$scope.assignTeam.fromDate=$scope.newDateField.fromDate ;
          }

        $http.post('/pla/core/team/assign', $scope.assignTeam).success(function(data){
             if(data.status==200){
                  $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
                  $scope.reset();
             }else if(data.status==500){
                  $scope.alert = {title:'Error Message! ', content:data.message, type: 'danger'};
             }

         });
     }
     $scope.reset = function(){

         $scope.assignTeam.fromDate ='';
         $scope.assignTeam.teamCode ='';
         $scope.assignTeam.teamName ='';
         $scope.assignTeam.teamId ='';
         $scope.assignTeam.branchName ='';
         $scope.assignTeam.regionName ='';
         $scope.assignTeam.regionalManager ='';
         $scope.assignTeam.branchCode ='';
         $scope.assignTeam.regionCode ='';
        $scope.assignTeam.teamLeaderFrom='';
        $scope.assignTeam.employeeId='';
        $scope.assignTeam.firstName='';
        $scope.assignTeam.lastName='';
    }
}]);
