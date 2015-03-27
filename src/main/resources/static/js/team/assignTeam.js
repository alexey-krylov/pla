var App = angular.module('assignTeam', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('AssignTeamController',['$scope','$http','$window','$location','$alert',function($scope,$http,$window,$location,$alert){

     $scope.currentTeamLeader;
     $scope.currentFromDate;
     $scope.boolVal=false;
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
     $http.get('http://localhost:6443/pla/core/team/getteamleaders').success(function(data){
                 //  console.log(data);
                  $scope.teamLeaders=data;

     });
     /* $scope.teamLeaders=[{'teamLeaderId':'100','firstName':'sara','lastName':'ali'},
                       {'teamLeaderId':'101','firstName':'James','lastName':'Mathew'},
                       {'teamLeaderId':'102','firstName':'Sandy','lastName':'Malhotra'},
                        {'teamLeaderId':'103','firstName':'Raj','lastName':'Kumar'}];  */
      $scope.url = window.location.search.split('=')[1];
       $http.get('http://localhost:6443/pla/core/team/openAssignPage?teamId='+$scope.url).success(function(data){
                  // console.log(data);
                   $scope.assignTeam=data;
                   $scope.currentTeamLeader=$scope.assignTeam.currentTeamLeader;
                   $scope.currentFromDate=moment($scope.assignTeam.fromDate).format("DD/MM/YYYY");
                   $scope.assignTeam.fromDate= $scope.currentFromDate;
     });

     $scope.getNewTeamLeader = function(teamLeaderId){
           if(teamLeaderId != $scope.currentTeamLeader || $scope.assignTeam.fromDate != $scope.currentFromDate ){
                   $scope.boolVal=true;
            }else{
                 $scope.boolVal=false;
            }
     }
     $scope.getNewFromDate = function(fromDate) {
         $scope.assignTeam.fromDate=moment(fromDate).format("DD/MM/YYYY");
         if($scope.assignTeam.fromDate != $scope.currentFromDate || $scope.assignTeam.currentTeamLeader != $scope.currentTeamLeader){
               $scope.boolVal=true;
           }else{
               $scope.boolVal=false;
          }
     }

      $scope.submitAssignTeam = function(){
           //       console.log($scope.assignTeam);
         $http.post('http://localhost:6443/pla/core/team/assign', $scope.assignTeam).success(function(data){
            $scope.alert = {title:'Success Message! ', content:'Team Updated Successfully', type: 'info'};
           //  console.log(data);
            $scope.reset();
         });
     }
     $scope.reset = function(){

         $scope.assignTeam.currentTeamLeader ='';
         $scope.assignTeam.fromDate ='';
         $scope.assignTeam.teamCode ='';
         $scope.assignTeam.teamName ='';
         $scope.assignTeam.teamId ='';
         $scope.assignTeam.branchName ='';
         $scope.assignTeam.regionName ='';
         $scope.assignTeam.regionalManager ='';
         $scope.assignTeam.branchCode ='';
         $scope.assignTeam.regionCode ='';

     }
}]);
