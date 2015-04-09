var App = angular.module('createPremium', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert','angularFileUpload']);
/*,'angularFileUpload' ,'$upload' ,$upload*/
App.controller('CreatePremiumController',['$scope','$http','$rootScope','$upload','$window',function($scope,$http,$rootScope,$upload,$window){

         $scope.uploaded=false;
         $scope.verified=false;
         $scope.newPlanList = [];
         $scope.optionalCoverageList=[];
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

               if($scope.createPremium.definedFor == "plan"){
                     $scope.showOptionalCoverage= false;
               }else{
                     $scope.showOptionalCoverage= true;
               }

         }
         $http.get('/pla/core/premium/getpremiuminfluencingfactors').success(function(data){

                $scope.mulSelect=data;

         });
         $http.get('/pla/core/plan/getallplan').success(function(data){
                   for(var i=0;i<data.length;i++) {
                         $scope.planList=data[i];
                         $scope.newPlanList.push({
                              planName: $scope.planList.planDetail.planName,
                              planId: $scope.planList.planId.planId,
                              coverages: $scope.planList.coverages
                              });
                }
         });
         $scope.$watch('createPremium.planId',function(newValue, oldValue){
                 if(newValue){
                         var planId=$scope.createPremium.planId;
                         $scope.optionalCoverageList =_.findWhere($scope.newPlanList,{planId:planId});
                         $scope.optionalCoverageList =$scope.optionalCoverageList.coverages;

            }
         });
         $scope.$watch('createPremium.coverageCode',function(newValue, oldValue){
                          if(newValue){
                          $scope.createPremium.coverageId=$scope.createPremium.coverageCode.coverageId;
                     }
                  });
         $scope.getDownloadedTemplate = function(){
             if (!moment($scope.createPremium.fromDate,'DD/MM/YYYY').isValid()) {
             		$scope.newDateField.fromDate = moment($scope.createPremium.fromDate).format("DD/MM/YYYY");
               		$scope.createPremium.fromDate=$scope.newDateField.fromDate ;
             }

          $http({url: '/pla/core/premium/downloadpremiumtemplate',method: 'POST',responseType: 'arraybuffer',data: $scope.createPremium,
                 headers: {'Content-type': 'application/json','Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
             }
        }).success(function(data){
                 var blob = new Blob([data], {
                type: 'application/msexcel'
               });
                 var objectUrl = URL.createObjectURL(blob);
                           window.open(objectUrl);
                }).error(function(){
                    //Some error log
                });
         }

        /* $scope.verifyPremiumData= function(){

                 if (!moment($scope.createPremium.fromDate,'DD/MM/YYYY').isValid()) {
                    		$scope.newDateField.fromDate = moment($scope.createPremium.fromDate).format("DD/MM/YYYY");
                    		$scope.createPremium.fromDate=$scope.newDateField.fromDate ;
                 }
            console.log($scope.createPremium);

             $http.post('/pla/core/premium/verifypremiumdata', $scope.createPremium).success(function(data){
                              //  console.log(data);
                if(data.status==200){
                    $scope.verified=true;
                   $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
                }else if(data.status==500){
                   $scope.alert = {title:'Error Message! ', content:data.message, type: 'danger'};
                }else{
                   $scope.alert = {title:'Info Message! ', content:data.message, type: 'danger'};
                }
             });
        }  */
       $scope.verifyPremiumData= function(files){
        if (!moment($scope.createPremium.fromDate,'DD/MM/YYYY').isValid()) {
                           		$scope.newDateField.fromDate = moment($scope.createPremium.fromDate).format("DD/MM/YYYY");
                           		$scope.createPremium.fromDate=$scope.newDateField.fromDate ;
                        }
                    // console.log(files);
                   // console.log("************"+files[0].name);
                          if (files) {
                               $upload.upload({
                                    url: '/pla/core/premium/verifypremiumdata',
                                    file: files,
                                    fields:$scope.createPremium

                               }).progress(function (evt) {
                                   // var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                                   // console.log('progress: ' + progressPercentage + '% ' +
                                               //      evt.config.file.name);
                               }).success(function (data, status, headers, config) {
                                     if(data.status==200){
                                        $scope.verified=true;
                                         $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
                                     }else if(data.status==500){
                                        $scope.alert = {title:'Error Message! ', content:data.message, type: 'danger'};
                                     }
                               });
                          }

                     }
          $scope.uploadFile= function(files){
              // console.log("************"+files[0].name);
                    if (files) {
                   // console.log($scope.createPremium.file);
                         $upload.upload({
                              url: '/pla/core/premium/uploadpremiumdata',
                              file: files,
                              fields:$scope.createPremium

                         }).progress(function (evt) {
                             // var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                             // console.log('progress: ' + progressPercentage + '% ' +
                                         //      evt.config.file.name);
                         }).success(function (data, status, headers, config) {
                                console.log('file ' + config.file.name + 'uploaded. Response: ' +
                                       JSON.stringify(data));
                         });
                    }

               }
}]);
