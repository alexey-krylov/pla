var App = angular.module('createPremium', ['ngRoute','ui.bootstrap','ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap','mgcrea.ngStrap.alert','angularFileUpload']);
App.controller('CreatePremiumController',['$scope','$http','$rootScope','$upload','$window',function($scope,$http,$rootScope,$upload,$window){

         $scope.uploaded=false;
         $scope.verified=false;
         $scope.boolVal=false;
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
                              planId: $scope.planList.planId,
                              coverages: $scope.planList.coverages
                              });
                }
         });
         $scope.$watch('createPremium.definedFor',function(newValue, oldValue){
             if(newValue=='plan'){
               $scope.boolVal=true;
             }else if(newValue == 'optionalCoverage'){
               $scope.boolVal=false;
             }
         });
         $scope.$watch('createPremium.planId',function(newValue, oldValue){
             if(newValue){
                         var planId=$scope.createPremium.planId;
                        $scope.optionalCoverageData =_.findWhere($scope.newPlanList,{planId:planId});
                        $scope.optionalCoverageList = _.where($scope.optionalCoverageData.coverages, {coverageType: "OPTIONAL"});

            }

         });
         $scope.$watch('createPremium.coverageCode',function(newValue, oldValue){
            if(newValue){
                $scope.createPremium.coverageId=$scope.createPremium.coverageCode.coverageId;
                $scope.boolVal=true;
            }else{
               $scope.boolVal=false;
            }
         });
     $scope.$watch('createPremium.effectiveFrom',function(newValue, oldValue){
         if(newValue) {
             if (!moment($scope.createPremium.effectiveFrom, 'DD/MM/YYYY').isValid()) {
                 $scope.newDateField.fromDate = moment($scope.createPremium.effectiveFrom).format("DD/MM/YYYY");
                 $scope.createPremium.effectiveFrom = $scope.newDateField.fromDate;
             }
         }
     });
         $scope.getDownloadedTemplate = function(){
             $http({url: '/pla/core/premium/downloadpremiumtemplate',method: 'POST',responseType: 'arraybuffer',data: $scope.createPremium,
                 headers: {'Content-type': 'application/json','Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
             }
        }).success(function(data, status, headers){
             var filename = "";
             var header = headers('content-disposition');
             if (header && header.indexOf('attachment') !== -1) {
                 var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                 var matches = filenameRegex.exec(header);
                 if (matches != null && matches[1]) filename = matches[1].replace(/['"]/g, '');
             }
               $scope.fileName=filename;
                var blob = new Blob([data], {
                type: 'application/msexcel'

               });

                 var objectUrl = URL.createObjectURL(blob);
                     var a = document.createElement("a");
                     document.body.appendChild(a);
                     a.style = "display: none";
                     a.href = objectUrl;
                     a.download = filename;
                     a.click();

                }).error(function(){
                    //Some error log
                });
         }

    $scope.verifyPremiumData= function(files){
       var aVal= $scope.createPremium.premiumInfluencingFactors;
       var output = [];
       for (var i=0; i<aVal.length; i++) {
           output.push(JSON.stringify(aVal[i]).replace(/"/g, ''));
           }
        if (files) {
            $upload.upload({
                 url: '/pla/core/premium/verifypremiumdata',
                 file: files,
                 fields:{planId:$scope.createPremium.planId,coverageId:$scope.createPremium.coverageId,
                 premiumInfluencingFactors:output.join(',')}
            }).progress(function (evt) {

            }).success(function (data, status, headers) {
           // console.log(data);
              if(data.status==200){
                    $scope.verified=true;
                    $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
                }else if(data.status==500){
                    $scope.alert = {title:'Error Message! ', content:data.message, type: 'danger'};
                }else{
                     var filename = "";
                     var header = headers('content-disposition');
                     if (header && header.indexOf('attachment') !== -1) {
                          var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                          var matches = filenameRegex.exec(header);
                         if (matches != null && matches[1]) filename = matches[1].replace(/['"]/g, '');
                     }
                     var blob = new Blob([data], {
                         type: 'application/msexcel'
                     });
                     var objectUrl = URL.createObjectURL(blob);
                     var a = document.createElement("a");
                     document.body.appendChild(a);
                     a.style = "display: none";
                     a.href = objectUrl;
                     a.download = filename;
                     a.click();
                 }
            });
         }
 }

 $scope.uploadFile= function(files){

      var  a=$scope.createPremium.premiumInfluencingFactors;
      var output = [];
      for (var i=0; i<a.length; i++) {
          output.push(JSON.stringify(a[i]).replace(/"/g, ''));
      }
     if (files) {
        $upload.upload({
            url: '/pla/core/premium/uploadpremiumdata',
            file: files,
            fields:{planId:$scope.createPremium.planId,coverageId:$scope.createPremium.coverageId,
                    premiumInfluencingFactors:output.join(','),premiumFactor:$scope.createPremium.premiumFactor,premiumRate:$scope.createPremium.premiumRate,effectiveFrom:$scope.createPremium.effectiveFrom}
        }).progress(function (evt) {
             // var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
             // console.log('progress: ' + progressPercentage + '% ' +
             //      evt.config.file.name);
        }).success(function (data, status, headers, config) {
            if(data.status==200){

                $scope.alert = {title:'Success Message! ', content:data.message, type: 'success'};
            }else if(data.status==500) {
                $scope.alert = {title: 'Error Message! ', content: data.message, type: 'danger'};
            }
        });
     }
 }
}]);
