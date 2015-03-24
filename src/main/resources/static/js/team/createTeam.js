var App = angular.module('createTeam', ['ngRoute','ui.bootstrap','mgcrea.ngStrap.select']);


App.controller('CreateTeamController',['$scope','$http',function($scope,$http){

       $scope.selectedDate =moment(new Date).format("YYYY-MM-DD");
      // console.log($scope.selectedDate);
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
                 console.log(data);
                 $scope.branchList=data;

                  });
         }
         $scope.submitTeam = function(){
            $scope.createTeam.fromDate= $scope.selectedDateAsNumber;

           if($scope.createTeam.fromDate) {
             if (!moment($scope.createTeam.fromDate,'DD/MM/YYYY').isValid()) {
             			$scope.newDateField.fromDate = moment($scope.createTeam.fromDate).format("DD/MM/YYYY");
             			$scope.createTeam.fromDate=$scope.newDateField.fromDate ;

             }
            }
             $scope.testTeamData={'teamName':'toofan','teamCode':'132','regionCode':'123','branchCode':'666','employeeId':'100','fromDate':'02/05/2015','firstName':'Sara','lastName':'ali'}
             // console.log($scope.createTeam);
             $http.post('http://localhost:6443/pla/core/team/create', $scope.testTeamData).success(function(data){
                                     console.log(data);
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