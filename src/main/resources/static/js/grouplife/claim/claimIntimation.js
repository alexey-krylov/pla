
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

App.controller('ClaimIntimationController', ['$scope', '$http','$window', '$upload','getQueryParameter','provinces','bankDetails','globalConstants','occupations',
        function ($scope, $http, $window,$upload,getQueryParameter,provinces,bankDetails,globalConstants,occupations) {
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
        $scope.rcvPolicyId = getQueryParameter('policyId');
        $scope.minIntimationDate=moment().add(0,'days').format("YYYY-MM-DD");
        $scope.isAdvanceSearchEnable=false;
        //$scope.minIntimationDate=moment().add(1,'days').format("YYYY-MM-DD");
        $scope.titles = globalConstants.title;
        $scope.occupations = [];
        $scope.occupations=occupations;
        $scope.assuredDetails={};
        $scope.claimDetails={};
        $scope.claimType=[];
        $scope.assuredCriteria={};
        $scope.assuredSearchResult=[];
        $scope.documentList = [];
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
             * @param $event for Advance Search Assured DOB
             */
            $scope.openAssuredDOB = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForAssuredDOB.isOpened = true;
            };
            $scope.datePickerSettingsForAssuredDOB = {
                isOpened:false,
                dateOptions:{
                    formatYear:'yyyy' ,
                    startingDay:1

                }
            }

            /***
             *
             * @param $event for Assured Details Date Of Birth
             */
            $scope.openDob = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.datePickerSettingsForDOB.isOpened = true;
            };
            $scope.datePickerSettingsForDOB = {
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
            $scope.$watch('claimantDetail.province',function(newVal,oldVal){
                if(newVal){
                    var provinceDetails1 = _.findWhere($scope.provinces, {provinceId: newVal});
                    if(provinceDetails1){
                        $scope.towns=provinceDetails1.cities;
                    }
                }
            });
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
            if($scope.rcvPolicyId){
                // Logic to Populate All The date to ClaimIntimation Class
                //$scope.policyNumber=$scope.rcvPolicyNumber;

               $http.get('/pla/grouplife/claim/getclaimant/' + $scope.rcvPolicyId).success(function (response, status, headers, config) {
                    console.log(JSON.stringify(response));
                   $scope.schemeName=response.schemeName;
                   $scope.claimDetails.policyNumber=response.policyNumber;
                   $scope.claimantDetail=response.claimantDetail;
                   $scope.categorySet=response.categorySet;
                }).error(function (response, status, headers, config) {
                });

                $scope.claimIntimationDate=moment(new Date()).format('YYYY-MM-DD');

            }
            /**
             * Openning Advance Search panel
             */
            $scope.openAdvaceSearch=function(){
                //alert('Advance Search');
                $scope.isAdvanceSearchEnable=true;
            }

            /**
             * Calculating Nex DOB
             */
            $scope.calculateAssuredDOB=function(){
                if ($scope.assuredDetails.dateOfBirth) {
                    $scope.assuredDetails.nextDob = moment().diff(new moment(new Date($scope.assuredDetails.dateOfBirth)), 'years') + 1;
                    //alert('dateof Birth');
                }
            }

            /**
             * Retrieving the RelationShip List
             */
            $scope.getRelationShipList=function(){
                $http.get('/pla/grouplife/claim/getrelationship/'+ $scope.rcvPolicyId +'/'+$scope.claimDetails.category).success(function (response, status, headers, config) {
                    //console.log(JSON.stringify(response));
                    $scope.reltaionShipList=response.relationship;
                }).error(function (response, status, headers, config) {
                });
            }
            /**
             * Retrieving PlanDetailWithClaimTYpeList
             */
            $scope.getPlanDetailWithClaimTypeList=function(){
                $http.get('/pla/grouplife/claim/getplandetail/'+ $scope.rcvPolicyId +'/'+$scope.claimDetails.category+'/'+$scope.claimDetails.relationship).success(function (response, status, headers, config) {
                    console.log(JSON.stringify(response));
                    $scope.claimTypes=response.claimTypes;
                    $scope.planDetail=response.planDetailDto;  //planDetailDto

                    //Retriving the DocumentList
                     $http.get('/pla/grouplife/claim/getclaimdmandatorydocuments/'+$scope.planDetail.planId).success(function (response, status, headers, config) {
                                        console.log('Document List...******');
                                        console.log(JSON.stringify(response));
//                                        $scope.documentList=response;

                                        $scope.documentList=[
                                          {
                                            "documentId": "ACTIVE_AT_WORK_DECLARATION_FORM",
                                            "documentName": "Active At Work Declaration Form",
                                            "file": null,
                                            "content": null,
                                            "submitted": false,
                                            "fileName": null,
                                            "contentType": null,
                                            "gridFsDocId": null,
                                            "requireForSubmission": false
                                          },
                                          {
                                            "documentId": "ADDRESS_PROOF",
                                            "documentName": "Address Proof",
                                            "file": null,
                                            "content": null,
                                            "submitted": false,
                                            "fileName": null,
                                            "contentType": null,
                                            "gridFsDocId": null,
                                            "requireForSubmission": false
                                          }
                                        ]
                                    }).error(function (response, status, headers, config) {
                                    });


                }).error(function (response, status, headers, config) {
                });
            }
              /**
                         * claimSubmit Logic
                         */

            $scope.createClimIntimation=function(){
            // Submiting Claim
            var claimSubmitObj={
            "policyNumber":$scope.claimDetails.policyNumber,
            "schemeName":$scope.claimDetails.schemeName,
            "relationship":$scope.claimDetails.relationship,
            "category":$scope.claimDetails.category,
            "claimantDetail":$scope.claimantDetail,
            "planDetailDto":$scope.planDetail,
            "bankDetails":$scope.bankDetails,
            "claimAssuredDetailDto":$scope.assuredDetails};

           /*  $http.post('/pla/grouplife/claim/createclaimintimation',claimSubmitObj).success(function (response, status, headers, config) {
                                console.log('generate Claim Intimation Response..');
                                console.log(JSON.stringify(response));
                            }).error(function (response, status, headers, config) {
                            });*/

            }
            /**
             * Adding Contact Details..
             * @type {{contact: boolean}}
             */
            $scope.addContactDetail = function (contactDetails) {
                console.log(contactDetails);
                $scope.claimantDetail.contactPersonDetail.push(contactDetails);
                $scope.contactDetails = {};

            }
            /**
             * Edit ContactDetails..
             * @type {{contact: boolean}}
             */
            $scope.editCurrentRow = function (contactDetails, index) {
                $scope.contactDetails = contactDetails;
                $scope.claimantDetail.contactPersonDetail.splice(index, 1);
            }
            /**
             * Delete Contact Details..
             * @type {{contact: boolean}}
             */
            $scope.deleteCurrentRow = function (index) {
                $scope.claimantDetail.contactPersonDetail.splice(index, 1);
            }
            $scope.accordionStatus = {
                contact: false
            };

            /***
             * Assured Search Criteria
             */
            $scope.assuredSearch=function(){
                var searchRequestObj={
                    "category":$scope.claimDetails.category,
                    "relationShip":$scope.claimDetails.relationship,
                    "policyNumber":$scope.claimDetails.policyNumber,
                    "firstName":$scope.assuredCriteria.firstName,
                    "surName":$scope.assuredCriteria.surname,
                    "dateOfBirth":$scope.assuredCriteria.assuredDOB,
                    "clientId":$scope.assuredCriteria.clientId,
                    "nrcNumber":$scope.assuredCriteria.nrc,
                    "manNumber":$scope.assuredCriteria.manNumber,
                    "gender":$scope.assuredCriteria.gender
                };
                console.log('AssuredSearchObject '+JSON.stringify(searchRequestObj));

                // Calling Service to get AssuredSearch Related data
                $http.post('/pla/grouplife/claim/assuredsearch',searchRequestObj).success(function (response, status, headers, config) {
                    console.log('Assured Search Response..');
                    console.log(JSON.stringify(response));
                     $scope.assuredSearchResult=response;
                    if($scope.assuredSearchResult.length > 0){
                        $('#assuredSearchModal').modal('show');
                    }
                 //$('#assuredSearchModal').modal('show');

                }).error(function (response, status, headers, config) {
                });

                /*$scope.assuredSearchResult=[
                    {
                        "firstName": "ravi",
                        "surName": "kumar",
                        "dateOfBirth": "09/06/1990",
                        "gender": "MALE",
                        "nrcNumber": "",
                        "manNumber": "",
                        "clientId": "10012301"
                    },
                    {
                        "firstName": "ravi",
                        "surName": "kumar",
                        "dateOfBirth": "09/06/1990",
                        "gender": "MALE",
                        "nrcNumber": "",
                        "manNumber": "",
                        "clientId": "10012301"
                    }
                ];*/

                //$('#assuredSearchModal').modal('show');
                //console.log('JSON**'+JSON.stringify($scope.assuredSearchResult));
            }
            /**
             * closeAssuredSearchModal
             */
            $scope.closeAssuredSearchModal=function(){
                $('#assuredSearchModal').modal('toggle');
            }


            /**
             * Retriving the Selected Assured Details..
             */
            $scope.selectCurrentAssured=function(index,clientId){
                // Call server to Retrival of the Assured Details.
                $http.get('/pla/grouplife/claim/assureddetail/'+ $scope.rcvPolicyId +'/'+clientId).success(function (response, status, headers, config) {
                    console.log(JSON.stringify(response));
                    $scope.assuredDetails=response;
                }).error(function (response, status, headers, config) {
                });

                $scope.closeAssuredSearchModal();
            }
            /**
             * Claim Detail HarCoded Value
             */

            /*$scope.claimantDetail={
                "proposerName":"sca",
                "addressLine1":"dsa",
                "addressLine2":"asa",
                "postalCode":"1212121",
                "province":"PR-LUA",
                "town":"CI-SAM",
                "workPhone":"2222222222",
                "contactPersonEmail":"mail@mail.com",
                "emailId":"mail@mail.com",
                "contactPersonName":"ascas",
                "mobileNumber":"2222222222",
                "contactPersonPhone":"2222222222"
            }*/
            /*$scope.claimantDetail={
                "proposerName":"AXIZ",
                "addressLine1":"aaaaaa",
                "addressLine2":"aaa",
                "postalCode":"aaa",
                "province":"PR-EAS",
                "town":"CI-KAT",
                "emailId":"mail@mail.com",
                "workPhone":"2222222222",
                "mobileNumber":"2222222222",
                "contactPersonDetail":[
                    {
                        "contactPersonName":"e",
                        "contactPersonEmail":null,
                        "contactPersonMobileNumber":"4444444444",
                        "contactPersonWorkPhoneNumber":"4444444444444"
                    }
                ]
            }*/

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
                               }],
                               occupations: ['$q', '$http', function ($q, $http) {
                                   var deferred = $q.defer();
                                   $http.get('/pla/individuallife/proposal/getAllOccupation').success(function (response, status, headers, config) {
                                       deferred.resolve(response)
                                   }).error(function (response, status, headers, config) {
                                       deferred.reject();
                                   });
                                   return deferred.promise;
                               }]

                                   }
                    }

       )}]);

