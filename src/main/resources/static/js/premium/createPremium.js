var App = angular.module('createPremium', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert','angularFileUpload']);
/*,'angularFileUpload' ,'$upload' ,$upload*/
App.controller('CreatePremiumController',['$scope','$http','$rootScope','$upload',function($scope,$http,$rootScope,$upload){

         $scope.uploaded=false;
         $scope.verified=false;
         $scope.showProduct=false;
         $scope.showOptionalCoverage= false;
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

         $scope.getDefinedOption = function(){
              // console.log($scope.createPremium.definedFor);
               if($scope.createPremium.definedFor == "plan"){
                     $scope.showProduct=true;
                     $scope.showOptionalCoverage= false;
               }else{
                     $scope.showProduct=true;
                     $scope.showOptionalCoverage= true;
               }

         }
         $http.get('/pla/core/premium/getpremiuminfluencingfactors').success(function(data){
                  //  console.log(data);
                // $scope.mulSelect=data;

         });
         $scope.getDownloadedTemplate = function(){
             if (!moment($scope.createPremium.fromDate,'DD/MM/YYYY').isValid()) {
             		$scope.newDateField.fromDate = moment($scope.createPremium.fromDate).format("DD/MM/YYYY");
               		$scope.createPremium.fromDate=$scope.newDateField.fromDate ;
             }
             console.log($scope.createPremium);
             $http.post('/pla/core/premium/downloadpremiumtemplate', $scope.createPremium).success(function(data){
                console.log(data);
             });
         }
         $scope.verifyPremiumData= function(){
         $scope.verified=true;
                 if (!moment($scope.createPremium.fromDate,'DD/MM/YYYY').isValid()) {
                    		$scope.newDateField.fromDate = moment($scope.createPremium.fromDate).format("DD/MM/YYYY");
                    		$scope.createPremium.fromDate=$scope.newDateField.fromDate ;
                 }
            console.log($scope.createPremium);

             $http.post('/pla/core/premium/verifypremiumdata', $scope.createPremium).success(function(data){
                              //  console.log(data);
                if(data.status==200){
                   $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
                }else if(data.status==500){
                   $scope.alert = {title:'Error Message! ', content:data.message, type: 'danger'};
                }else{
                   $scope.alert = {title:'Info Message! ', content:data.message, type: 'info'};
                }
             });
        }
        $scope.savePremium = function(){

            if (!moment($scope.createPremium.fromDate,'DD/MM/YYYY').isValid()) {
           		$scope.newDateField.fromDate = moment($scope.createPremium.fromDate).format("DD/MM/YYYY");
           		$scope.createPremium.fromDate=$scope.newDateField.fromDate ;
            }
          console.log($scope.createPremium);
        }
        $scope.mulSelect =[
                                   {
                                     "id": "1",
                                     "name": "Team One"
                                   },
                                   {
                                     "id": "2",
                                     "name": "Team Two"
                                   },
                                   {
                                     "id": "3",
                                     "name": "Team Three"
                                   }
                                 ]
              /* $scope.$watch('files', function () {
                  $scope.upload($scope.files);
                  console.log($scope.files[0].name);
               });*/
               $scope.uploadFile= function(files){
               $scope.uploaded=true;
               console.log("************"+files[0].name);
                    if (files) {
                         $upload.upload({
                              url: '/pla/core/premium/uploadpremiumdata',
                              file: files
                         }).progress(function (evt) {
                              var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                              console.log('progress: ' + progressPercentage + '% ' +
                                               evt.config.file.name);
                         }).success(function (data, status, headers, config) {
                                console.log('file ' + config.file.name + 'uploaded. Response: ' +
                                       JSON.stringify(data));
                         });
                    }

               }



}]);
