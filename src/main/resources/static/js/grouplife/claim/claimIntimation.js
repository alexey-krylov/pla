
var App = angular.module('claimIntimation', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives',
                                               , 'ngSanitize', 'commonServices', 'ngMessages','angularFileUpload']);

       App.config(['datepickerPopupConfig', function (datepickerPopupConfig) {
            datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
            datepickerPopupConfig.currentText = 'Today';
            datepickerPopupConfig.clearText = 'Clear';
            datepickerPopupConfig.closeText = 'Done';
            datepickerPopupConfig.closeOnDateSelection = true;
}
]);

App.controller('ClaimIntimationController', ['$scope', '$http','$window', '$upload','getQueryParameter','provinces','bankDetails',
        function ($scope, $http, $window,$upload,getQueryParameter,provinces,bankDetails) {
//alert("********CONTROLLER INCLUDED***********");
        $scope.schedule={};
        $scope.searchObj = {};
        $scope.referral={};
        $scope.agency={};
        $scope.selectedItem = 1;
        $scope.saveAccidentObject={};
        $scope.provinces = [];
        $scope.provinces = provinces;
        $scope.towns=[];
        $scope.bankDetails=[];
        $scope.bankDetails=bankDetails;
        $scope.bankBranchDetails=[];
        $scope.rcvPolicyNumber = getQueryParameter('policyNumber');
        $scope.minIntimationDate=moment().add(0,'days').format("YYYY-MM-DD");
        //$scope.minIntimationDate=moment().add(1,'days').format("YYYY-MM-DD");

            /***
             *
             * @param $event for Claim intimation Date
             */
            $scope.open = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettings.isOpened = true;
            };
            $scope.datePickerSettings = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }
            /***
             *@param $event for Claim incidence Date
             */
            $scope.openClaimIncidenceDate = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForIncidence.isOpened = true;
            };
            $scope.datePickerSettingsForIncidence = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }

            /**
             *
             * @param province
             * Getting List of Related Cities
             */
            $scope.getProvinceValue=function(province){
                if(province){
                    var provinceDetails1 = _.findWhere($scope.provinces, {provinceId: province});
                    if(provinceDetails1){
                        $scope.towns=provinceDetails1.cities;
                    }
                }

            }
            /***
             * Getting List of Related Branch
             */
            $scope.$watch('bankDetails.bankName', function (newvalue, oldvalue) {
                if (newvalue) {
                    var bankCode = _.findWhere($scope.bankDetails, {bankName: newvalue});
                    if (bankCode) {
                        $http.get('/pla/individuallife/proposal/getAllBankBranchNames/' + bankCode.bankCode).success(function (response, status, headers, config) {
                            $scope.bankBranchDetails = response;
                        }).error(function (response, status, headers, config) {
                        });
                    }
                }
            });

            /***
             * Geating All Details Of Particular PolicyNumber and Populating in GL ClaimIntimation Screen
             */
            if($scope.rcvPolicyNumber){
                // Logic to Populate All The date to ClaimIntimation Class
                $scope.policyNumber=$scope.rcvPolicyNumber;
                $scope.claimIntimationDate=moment(new Date()).format('YYYY-MM-DD');

            }


/*

            $http.get("/pla/grouplife/claim/getclaimtype")
             .success(function (data) {
                $scope.claimTypes = data
                    });
*/


    $scope.items=[
         {
           "assuredNumber":"code1",
            "assuredFirst":"document1",
            "assuredSurname":"codesurname",
            "assuredDOB":"8/4/15"
          },

          {
             "assuredNumber":"code2",
             "assuredFirst":"document12",
             "assuredSurname":"code22",
             "assuredDOB":"8/4/16"
           }
     ];

       $scope.launchclaimDate = function ($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    $scope.claimIntimationDate = true;
                };

       $scope.showDisabilityDate= function ($event){
            $event.preventDefault();
            $event.stopPropagation();
           $scope.openDisabilityDate=true;
       };
       $scope.showDob=function($event){
       $event.preventDefault();
       $event.stopPropagation();
       $event.openDob=true;
       };
        $scope.ShowDeathDate=function($event){
              $event.preventDefault();
              $event.stopPropagation();
              $event.openDeathDate=true;
              };
               $scope.ShowdateOfConsultation=function($event){
                            $event.preventDefault();
                            $event.stopPropagation();
                            $event.opendateOfConsultation=true;
                            };
                             $scope.ShowdateOfAccident=function($event){
                                                        $event.preventDefault();
                                                        $event.stopPropagation();
                                                        $event.opendateOfAccident=true;
                                                        };

       $scope.showAssureDob= function ($event){
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openAssureDob=true;
                 };

       $scope.showDignosisDate= function ($event){
            $event.preventDefault();
            $event.stopPropagation();
           $scope.openDignosisDate=true;
       };
      $scope.showFirstConsultanceDate= function ($event){
            $event.preventDefault();
            $event.stopPropagation();
           $scope.openFirstConsultanceDate=true;
       };

     $scope.showFromDate= function ($event){
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openFromDate=true;
        };
  $scope.showDisabilityDate= function ($event){
             $event.preventDefault();
             $event.stopPropagation();
             $scope.openDisabilityDate=true;
         };
  $scope.showDignosisDate= function ($event){
             $event.preventDefault();
             $event.stopPropagation();
             $scope.openDignosisDate=true;
         };
  $scope.showFirstConsultanceDate= function ($event){
              $event.preventDefault();
              $event.stopPropagation();
              $scope.openFirstConsultanceDate=true;
          };
   $scope.showFromDate= function ($event){
               $event.preventDefault();
               $event.stopPropagation();
               $scope.openFromDate=true;
 };
  $scope.retriveAssuredDetails=function(cid)
              {
              console.log(cid)
              angular.forEach($scope.items, function(eachData){
                    if(angular.equals(eachData.assuredNumber,cid)) {
                $scope.schedule.assuredField=eachData.assuredFirst;
                     $scope.schedule.assuredField=eachData.assuredFirst;
                                    $('#claimintimationModal').modal('hide');

                    }
              });

              }


                          $scope.showMe=function(){
                                $scope.show=true;
                          }
                         $scope.hideMe=function(){
                          $scope.show=false;
                          }



     $scope.showModal=function(){
           $('#claimintimationModal').modal('show');
         };

    }]);


 App.config(["$routeProvider", function ($routeProvider) {
                      $routeProvider.when('/', {
                            templateUrl: 'claimintimation.html',
                            controller: 'ClaimIntimationController',
                           resolve: {
                               provinces: ['$q', '$http', function ($q, $http) {
                                   var deferred = $q.defer();
                                   $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                                       deferred.resolve(response)
                                   }).error(function (response, status, headers, config) {
                                       deferred.reject();
                                   });
                                   return deferred.promise;
                               }],
                               bankDetails: ['$q', '$http', function ($q, $http) {
                                   var deferred = $q.defer();
                                   $http.get('/pla/individuallife/proposal/getAllBankNames').success(function (response, status, headers, config) {
                                       deferred.resolve(response)
                                   }).error(function (response, status, headers, config) {
                                       deferred.reject();
                                   });
                                   return deferred.promise;
                               }]

                                   }
                    }

       )}]);

