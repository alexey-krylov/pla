(function (angular) {
    "use strict";
    var app= angular.module('CreatePreAuthorizationRequest', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives',
                                                                  'angularFileUpload', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices']);

        app.config(['datepickerPopupConfig', function (datepickerPopupConfig) {
            datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
            datepickerPopupConfig.currentText = 'Today';
            datepickerPopupConfig.clearText = 'Clear';
            datepickerPopupConfig.closeText = 'Done';
            datepickerPopupConfig.closeOnDateSelection = true;
        }])

    app.config(["$routeProvider", function ($routeProvider) {
        $routeProvider.when('/', {
                templateUrl: 'preAuthorizationRequest.html',
                controller: 'createPreAuthorizationRequestCtrl',
                resolve:{
                    createUpdateDto: ['$q', '$http', 'getQueryParameter', function ($q, $http, getQueryParameter) {
                      var deferred = $q.defer();

                        var preAuthorizationId = getQueryParameter('preAuthorizationId');
                        var clientId = getQueryParameter('clientId');

                        $http.get('/pla/grouphealth/claim/cashless/preauthorizationrequest/getpreauthorizationbypreauthorizationIdandclientId/'+preAuthorizationId+'/'+clientId).success(function (response, status, headers, config) {
                            deferred.resolve(response);
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
//                        console.log(deferred);



                        return deferred.promise;
                    }]
                }

            }
        )}]);

   app.controller('createPreAuthorizationRequestCtrl', ['$scope', '$http','createUpdateDto','$timeout','getQueryParameter','$window',
                                                        function ($scope, $http,createUpdateDto, getQueryParameter,$window){
          $scope.createUpdateDto = createUpdateDto;
          $scope.drugServicesDtoList = $scope.createUpdateDto.drugServicesDtos;
          $scope.treatmentDiagnosis={};
          $scope.diagnosisTreatmentDtosUpdate={};
          $scope.index = null;
          $scope.isEditMode = false;
          $scope.createUpdateCommand={};
          $scope.diagnosisTreatmentDtos={};
          $scope.createUpdateCommand.claimantHCPDetailDto=$scope.createUpdateDto.claimantHCPDetailDto;
          $scope.createUpdateCommand.claimantHCPDetailDto={};
          $scope.createUpdateCommand.claimantPolicyDetailDto={};
          $scope.createUpdateCommand.diagnosisTreatmentDtos=[];
          $scope.createUpdateCommand.illnessDetailDto={};
          $scope.createUpdateCommand.drugServicesDtos=[];

          $scope.viewTreatmentDiagnosis = function(treatmentDiagnosis){
          $scope.diagnosisTreatmentDtos = treatmentDiagnosis;
          $scope.drugServicesDto = {};
          $scope.diagnosisTreatmentDtosUpdate = {};
          $scope.createUpdateDto.drugServicesDtosSave = [];
         }

         $scope.updatedrugServicesDtos = function(drugServicesDto,index){
         $scope.index = index;
         $scope.isEditMode = true;
         $scope.diagnosisTreatmentDtosUpdate = drugServicesDto;
         }



          $scope.savediagnosisTreatmentDtos = function(diagnosisTreatmentDtosUpdate){

           console.log("before - "+$scope.drugServicesDtoList);

            if($scope.isEditMode){

                $scope.createUpdateDto.drugServicesDtos[$scope.index] = diagnosisTreatmentDtosUpdate;

                $scope.isEditMode = false;

             } else{
                $scope.drugServicesDtoList.push(diagnosisTreatmentDtosUpdate);
               }
            }


              $scope.deleteTreatmentDiagnosis = function(drugServicesDto,index){

               $scope.createUpdateDto.drugServicesDtos.splice(index, 1);

               }


             $scope.saveClaimant= function(){
                $scope.createUpdateCommand=$scope.createUpdateDto;
                $http({
                 url: '/pla/grouphealth/claim/cashless/preauthorizationrequest/createorupdate',
                  method: 'POST',
                   data: $scope.createUpdateCommand
                }).then(function(response) {
                   },
                    function(response) {
                });
             }


              $scope.back = function () {
               window.location.reload();
            }



    }]);
})(angular);

function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
}
