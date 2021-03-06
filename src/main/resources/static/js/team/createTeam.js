var App = angular.module('createTeam', ['common','commonServices','ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert']);

App.controller('CreateTeamController',['$scope','$http','$templateCache','$timeout','$alert','$window',function($scope,$http,$templateCache,$timeout,$alert,$window){

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
         $http.get('/pla/core/team/getteamleaders').success(function(data){
                          //  console.log(data);
                           $scope.teamLeaders=data;

         });
    $scope.$watch('createTeam.fromDate',function(newValue, oldValue){
        if(newValue){
            if (!moment($scope.createTeam.fromDate,'DD/MM/YYYY').isValid()) {
                $scope.newDateField.fromDate = moment($scope.createTeam.fromDate).format("DD/MM/YYYY");
                $scope.createTeam.fromDate=$scope.newDateField.fromDate ;
            }
        }
    });



    $scope.getAllBranch = function(obj){
              $http.get('/pla/core/master/getbranchbyregion?regioncode='+ obj).success(function(data){
               // console.log(data);
                 $scope.branchList=data;
               });
         }
         $scope.$watch('createTeam.employeeId',function(newValue, oldValue){
                 if(newValue){
                  var empId = $scope.createTeam.employeeId;
                  $scope.employeeData =_.findWhere($scope.teamLeaders,{employeeId:empId});
                  $scope.createTeam.firstName=$scope.employeeData.firstName;
                  $scope.createTeam.lastName=$scope.employeeData.lastName;
                }
           });
         $scope.submitTeam = function(){


            $http.post('/pla/core/team/create', $scope.createTeam).success(function(data){
              //  console.log(data);
                if(data.status==200){
                    // $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
                    // $scope.reset();
                   $window.location.reload();
                }else if(data.status==500){
               //  $scope.alert = {title:'Error Message! ', content:data.message, type: 'danger'};
                }

             });
         };
         $scope.reset = function(){

             $scope.createTeam.branchCode ='';
             $scope.createTeam.fromDate ='';
         	 $scope.createTeam.regionCode ='';
         	 $scope.createTeam.teamCode ='';
         	 $scope.createTeam.employeeId ='';
         	 $scope.createTeam.teamName ='';
         	 }
 }]);