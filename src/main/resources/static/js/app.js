var AngularApp = {};
var App = angular.module('AngularApp', ['ngRoute','ui.bootstrap','mgcrea.ngStrap.select','ngSanitize','angularFileUpload','mgcrea.ngStrap.popover']);


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

   //  console.log($scope.selectedDate);
   $scope.start = new Date('11/20/13');
      $scope.end = new Date();
     $scope.selectedDateAsNumber = new Date();
     $scope.minEndDate = $scope.start; //init value
     $scope.maxEndDate = $scope.end;


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
         $scope.getAllBranch = function(obj){
                //     console.log("^^^^^^^^^^^^^"+obj);
                $http.get('http://localhost:6443/pla/core/master/getbranchbyregion?regioncode='+ obj).success(function(data){
                 console.log(data);
                 $scope.branchList=data;

         });
         }
         $scope.getSelectedBranch = function(branchCode){
                console.log(branchCode);


         };
       /* $scope.getBranchList = function(regionName){
                console.log(regionName);

          		//$scope.branchList =_.findWhere($scope.employeeDetails,{regionName:regionName});
        } */
         $scope.submitTeam = function(){
            $scope.createTeam.fromDate= $scope.selectedDateAsNumber;

           if($scope.createTeam.fromDate) {
             if (!moment($scope.createTeam.fromDate,'DD/MM/YYYY').isValid()) {
             			$scope.newDateField.fromDate = moment($scope.createTeam.fromDate).format("DD/MM/YYYY");
             			$scope.createTeam.fromDate=$scope.newDateField.fromDate ;

             }
            }
            // $scope.testTeamData={'teamName':'team A','teamCode':'123','regionCode':'555','branchCode':'666','employeeId':'100','fromDate':'02/05/2015','firstName':'Sara','lastName':'ali'}

             $http.post('http://localhost:6443/pla/core/team/create', $scope.createTeam).success(function(data){
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
  var uid = 1;

   App.controller('CreateCommissionController',['$scope',function($scope){
           console.log("called CreateCommissionController................");
           $scope.commissions = [];
           $scope.showtable  = false;
           $scope.showToYear  = false;
               $scope.addCommissionDetails = function() {
                   $scope.showtable  = true;
                   if($scope.addCommission.id == null) {
                        $scope.addCommission.id = uid++;
                        $scope.commissions.push($scope.addCommission);
                   } else {

                        for(i in $scope.commissions) {
                               if($scope.commissions[i].id == $scope.addCommission.id) {
                                   $scope.commissions[i] = $scope.addCommission;
                               }
                        }
                   }
                   $scope.addCommission = {};
               }

            $scope.getPolicyYearExpressed = function(policyYearExpressed){
                    // alert(policyYearExpressed);
                     if(policyYearExpressed == "Range"){
                       $scope.showToYear  = true;
                     }else{
                       $scope.showToYear  = false;
                     }

            }
            $scope.saveCommission = function(){
                    console.log($scope.createCommission);
                    console.log($scope.commissions);
            }
            $scope.fromDatePickerSettings = {
                   isOpened:false,
                   dateOptions:{
                      formatYear:'yyyy' ,
                      startingDay:1
                   }
              }
              $scope.toDatePickerSettings = {
                                 isOpened:false,
                                 dateOptions:{
                                    formatYear:'yyyy' ,
                                    startingDay:1
                                 }
                            }
              $scope.open = function($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    $scope.fromDatePickerSettings.isOpened = true;
              };
              $scope.openToDate = function($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    $scope.toDatePickerSettings.isOpened = true;
              };

    }]);
    App.controller('CreateOverrideCommissionController',['$scope',function($scope){
             console.log("called CreateOverrideCommissionController................");
             $scope.overRideCommissions = [];
             $scope.showtable  = false;
             $scope.showToYear  = false;
                 $scope.addOverRideCommissionDetails = function() {
                     $scope.showtable  = true;
                     if($scope.addOverRideCommission.id == null) {
                          $scope.addOverRideCommission.id = uid++;
                          $scope.overRideCommissions.push($scope.addOverRideCommission);
                     } else {

                          for(i in $scope.overRideCommissions) {
                                 if($scope.overRideCommissions[i].id == $scope.addOverRideCommission.id) {
                                     $scope.overRideCommissions[i] = $scope.addOverRideCommission;
                                 }
                          }
                     }
                     $scope.addOverRideCommission = {};
                 }

              $scope.getPolicyYearExpressed = function(policyYearExpressed){
                      // alert(policyYearExpressed);
                       if(policyYearExpressed == "Range"){
                         $scope.showToYear  = true;
                       }else{
                         $scope.showToYear  = false;
                       }

              }
              $scope.saveOverRideCommission = function(){
                      console.log($scope.createOverRideCommission);
                      console.log($scope.overRideCommissions);
              }
$scope.fromDatePickerSettings = {
                   isOpened:false,
                   dateOptions:{
                      formatYear:'yyyy' ,
                      startingDay:1
                   }
              }
              $scope.toDatePickerSettings = {
                  isOpened:false,
                  dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1
                  }
              }
              $scope.open = function($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    $scope.fromDatePickerSettings.isOpened = true;
              };
              $scope.openToDate = function($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    $scope.toDatePickerSettings.isOpened = true;
              };

}]);
App.controller('ViewCalculatedCommissionController',['$scope','$http',function($scope,$http){

}]);
App.controller('ViewCalculatedOverrideCommissionController',['$scope','$http',function($scope,$http){

}]);

App.controller('CreateHealthCareProviderController',['$scope','$http',function($scope,$http){

}]);

App.controller('UpdateHealthCareProviderController',['$scope','$http',function($scope,$http){


}]);
App.controller('ViewPremiumController',['$scope','$http',function($scope,$http){

     $scope.items =[
                              {
                                "name": "Team One"
                              },
                              {
                                "name": "Team Two"
                              },
                              {
                                "name": "Team Three"
                              }
                            ]
        $scope.popover = {
          "title": "Influencing Factors",
          "content": $scope.items
        };

}]);
App.controller('DownloadErrorFileController',['$scope','$http',function($scope,$http){
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
    $scope.downloadErrorFilePremium = function(){
       // console.log($scope.downloadErrorFile.file);
     //  if ($scope.downloadErrorFile.file == 'Select File') return;
            window.location = 'http://localhost:6443/pla/downloadTemplates/' + $scope.downloadErrorFile.file;

    };

}]);
App.controller('CreatePremiumController',['$scope','$upload', function($scope,$upload){
       //$scope.files[0]='';
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
      $scope.showProduct=false;
      $scope.showOptionalCoverage= false;
      $scope.getDefinedValue = function(definedVal){
              if(definedVal == "Product"){
                $scope.showProduct=true;
                $scope.showOptionalCoverage= false;
              }else if(definedVal == "Optional Coverage"){
                 $scope.showProduct=false;
                 $scope.showOptionalCoverage= true;
              }
      }
      $scope.savePremium = function(){
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
     // $scope.$watch('files', function () {
         //$scope.upload($scope.files);
       //  console.log($scope.files[0].name);
     // });
      $scope.uploadFile= function(files){
      //console.log("************"+files[0].name);
           if (files) {
                $upload.upload({
                     url: 'http://localhost:6443/pla/upload',
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
App.controller('ViewMandatoryDocumentController',['$scope','$http',function($scope,$http){

     $scope.items =[   {
                                "name": "MandatoryDoc A"
                              },
                              {
                                "name": "MandatoryDoc B"
                              },
                              {
                                "name": "MandatoryDoc C"
                              }
                            ]
        $scope.popover = {
          "title": "Mandatory Documents",
          "content": $scope.items
        };

}]);
App.controller('CreateMandatoryDocumentsController',['$scope', function($scope){
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
      $scope.showProduct=false;
      $scope.showOptionalCoverage= false;
      $scope.getDefinedValue = function(definedVal){
              if(definedVal == "Product"){
                $scope.showProduct=true;
                $scope.showOptionalCoverage= false;
              }else if(definedVal == "Optional Coverage"){
                 $scope.showProduct=false;
                 $scope.showOptionalCoverage= true;
              }
      }
      $scope.saveMandatoryDoc = function(){
           console.log($scope.createMandatoryDocument);

      }
      $scope.mulSelect =[
                          {
                            "id": "1",
                            "name": "MandatoryDoc One"
                          },
                          {
                            "id": "2",
                            "name": "MandatoryDoc Two"
                          },
                          {
                            "id": "3",
                            "name": "MandatoryDoc Three"
                          }
                        ]

  }]);
 App.controller('UpdateMandatoryDocumentsController',['$scope', function($scope){
       $scope.showProduct=false;
       $scope.showOptionalCoverage= false;
       $scope.getDefinedValue = function(definedVal){
               if(definedVal == "Product"){
                 $scope.showProduct=true;
                 $scope.showOptionalCoverage= false;
               }else if(definedVal == "Optional Coverage"){
                  $scope.showProduct=false;
                  $scope.showOptionalCoverage= true;
               }
       }
       $scope.updateMandatoryDoc = function(){
            console.log($scope.updateMandatoryDocument);

       }
       $scope.mulSelect =[
                           {
                             "id": "1",
                             "name": "MandatoryDoc One"
                           },
                           {
                             "id": "2",
                             "name": "MandatoryDoc Two"
                           },
                           {
                             "id": "3",
                             "name": "MandatoryDoc Three"
                           }
                         ]

   }]);
