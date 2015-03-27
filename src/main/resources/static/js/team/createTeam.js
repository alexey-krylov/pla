var App = angular.module('createTeam', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('CreateTeamController',['$scope','$http','$templateCache','$timeout','$alert',function($scope,$http,$templateCache,$timeout,$alert){

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
         $scope.getAllBranch = function(obj){
              $http.get('http://localhost:6443/pla/core/master/getbranchbyregion?regioncode='+ obj).success(function(data){
               //  console.log(data);
                 $scope.branchList=data;
               });
         }

         $scope.submitTeam = function(){

             if (!moment($scope.createTeam.fromDate,'DD/MM/YYYY').isValid()) {
             		$scope.newDateField.fromDate = moment($scope.createTeam.fromDate).format("DD/MM/YYYY");
             		$scope.createTeam.fromDate=$scope.newDateField.fromDate ;
             }

           //console.log($scope.createTeam);
           $http.post('http://localhost:6443/pla/core/team/create', $scope.createTeam).success(function(data){
              //  console.log(data);
                  $scope.alert = {title:'Success Message! ', content:'Team Created Successfully', type: 'success'};
                 $scope.reset();

             });
         };
         $scope.reset = function(){

             $scope.createTeam.branchCode ='';
             $scope.createTeam.fromDate ='';
         	 $scope.createTeam.regionCode ='';
         	 $scope.createTeam.teamCode ='';
         	 $scope.createTeam.EmployeeId ='';
         	 $scope.createTeam.teamName ='';
         	 }
 }]);