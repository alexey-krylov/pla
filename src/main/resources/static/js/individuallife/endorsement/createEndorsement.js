(function (angular) {
    "use strict";
var app= angular.module('createEndorsement', ['common', 'ngRoute', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover', 'directives',
    'angularFileUpload', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices','ui.bootstrap.modal','ngMessages']);

app.config(["$routeProvider", function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'createEndorsementTpl.html',
        controller: 'EndorsementCtrl',
        resolve: {

        }
    })
}])
    .config(['datepickerPopupConfig', function (datepickerPopupConfig) {
        datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
        datepickerPopupConfig.currentText = 'Today';
        datepickerPopupConfig.clearText = 'Clear';
        datepickerPopupConfig.closeText = 'Done';
        datepickerPopupConfig.closeOnDateSelection = true;
    }]);
    app.directive('validateUpdateDob', function () {
        return {
            // restrict to an attribute type.
            restrict: 'A',
            // element must have ng-model attribute.
            require: 'ngModel',
            link: function (scope, ele, attrs, ctrl) {
                scope.$watch('ilEndrosementDetils.policyHolderNew.dateOfBirth', function (newval) {
                    var planDetail = scope.$eval('plan.planDetail');
                    var policyHolder = scope.$eval('ilEndrosementDetils.policyHolder');
                    if(policyHolder && ! policyHolder.isProposedAssured){
                        var ageNextBirthday = moment().diff(new moment(new Date(newval)), 'years') + 1;
                        ctrl.$setValidity('invalidUpdateMinAge', ageNextBirthday >= 18);
                        ctrl.$setValidity('invalidUpdateMaxAge', ageNextBirthday <= 60);
                    }
                    else if (policyHolder && policyHolder.isProposedAssured && planDetail) {
                        var ageNextBirthday = moment().diff(new moment(new Date(newval)), 'years') + 1;
                        ctrl.$setValidity('invalidMinAge', ageNextBirthday >= planDetail.minEntryAge);
                        ctrl.$setValidity('invalidMaxAge', ageNextBirthday <= planDetail.maxEntryAge);
                    }
                });
                scope.$watch('ilEndrosementDetils.lifeAssuredNew.dateOfBirth',function(newVal,oldVal){
                    var planDetail = scope.$eval('plan.planDetail');
                    if(planDetail){
                        var ageNextBirthday = moment().diff(new moment(new Date(newVal)), 'years') + 1;
                        ctrl.$setValidity('invalidMinAge', ageNextBirthday >= planDetail.minEntryAge);
                        ctrl.$setValidity('invalidMaxAge', ageNextBirthday <= planDetail.maxEntryAge);
                    }
                });

            }
        }
    })
        .directive('validateNrc', function () {
            return {
                // restrict to an attribute type.
                restrict: 'A',
                // element must have ng-model attribute.
                require: 'ngModel',
                link: function (scope, ele, attrs, ctrl) {
                    scope.$watch('ilEndrosementDetils.policyHolderNew.nrc', function (newval, oldval) {
                        if (newval == oldval)return;
                        if (newval) {
                            console.log('NRC Validation...***');
                            if(scope.ilEndrosementDetils.policyHolderNew.nrc === scope.ilEndrosementDetils.policyHolder.nrc){
                                var valid = false;
                                ctrl.$setValidity('duplicateNRC', valid);
                            }
                            else{
                                var valid = true;
                                ctrl.$setValidity('duplicateNRC', valid);
                            }
                        }
                        return valid ? newval : undefined;
                    });

                    scope.$watch('ilEndrosementDetils.lifeAssuredNew.nrc', function (newval, oldval) {
                        if (newval == oldval)return;
                        if (newval) {
                            console.log('NRC Validation...***');
                            if(scope.ilEndrosementDetils.lifeAssuredNew.nrc === scope.ilEndrosementDetils.lifeAssured.nrc){
                                var valid = false;
                                ctrl.$setValidity('duplicateNRC', valid);
                            }
                            else{
                                var valid = true;
                                ctrl.$setValidity('duplicateNRC', valid);
                            }
                        }
                        return valid ? newval : undefined;
                    });
                }
            }
        })
        .directive('validateDate', function () {
        return {
            // restrict to an attribute type.
            restrict: 'A',
            // element must have ng-model attribute.
            require: 'ngModel',
            link: function (scope, ele, attrs, ctrl) {
                scope.$watch('ilEndrosementDetils.lifeAssuredNew.dateOfBirth', function (newval, oldval) {
                    if (newval == oldval)return;
                    if (newval) {
                        console.log('Date Validation...***');
                        if(((moment(scope.ilEndrosementDetils.lifeAssuredNew.dateOfBirth).diff(moment(scope.ilEndrosementDetils.lifeAssured.dateOfBirth), 'days')) == 0)){
                            var valid = false;
                            ctrl.$setValidity('dateDuplicate', valid);
                        }
                        else{
                            var valid = true;
                            ctrl.$setValidity('dateDuplicate', valid);
                        }
                    }
                    return valid ? newval : undefined;
                });

                scope.$watch('ilEndrosementDetils.policyHolderNew.dateOfBirth',function(newVal,oldVal){
                    if(newVal == oldVal) return;
                    if(newVal){
                        console.log('Policy Update Date Validation..');
                        if(((moment(scope.ilEndrosementDetils.policyHolderNew.dateOfBirth).diff(moment(scope.ilEndrosementDetils.policyHolder.dateOfBirth), 'days')) == 0)){
                            var valid = false;
                            ctrl.$setValidity('dateDuplicate', valid);
                        }
                        else{
                            var valid = true;
                            ctrl.$setValidity('dateDuplicate', valid);
                        }
                    }
                    return valid ? newVal : undefined;
                });
            }
        }
    });
    app.controller('EndorsementCtrl', ['$scope', '$http', '$location','getQueryParameter','globalConstants', function ($scope, $http, $location, getQueryParameter,globalConstants) {

            $scope.selectedItem = 1;
            $scope.provinces = [];
            $scope.townList=[];
            $scope.empTownList=[];
            $scope.titleList = globalConstants.title;
            $scope.genderList = globalConstants.gender;
            $scope.beneficiariesList = [];
            $scope.agentDetails = [];
            $scope.bankDetailsResponse=[];
            $scope.additionalDocumentList = [];
            $scope.documentList = [];
            $scope.todayDate = new Date();
            $scope.policy={};
            $scope.ilEndrosementDetils={};
            $scope.plan={};
            $scope.serverError=false;
            $scope.endorsementRequestNumber=null;  // Capturing Endrosement RequestNumber..
            $scope.townListForLAEmp=[];
            $scope.townListForPHRes=[];  //Capturing TownList For PolicyHolder Contact Change (Residential)
            $scope.townListForPHEmp=[];  //Capturing TownList For PolicyHolder Contact Change (Employement)
            $scope.trusteeCities=[]; //Capturing TownList For Trustee(Beneficiary change type Endrosement)

            $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                $scope.provinces = response;
            }).error(function (response, status, headers, config) {
            });

            $http.get('/pla/individuallife/endorsement/getAllBankNames').success(function (response, status, headers, config) {
                $scope.bankDetailsResponse = response;
                //console.log("Bank Details :"+JSON.stringify(response));
            }).error(function (response, status, headers, config) {
            });


            /**
             * Finding Branch Detail Releated to Bank Name
             * @type {Array}
             */
             $scope.bankBranchDetails=[];// Collecting Branch Details

            $scope.$watch('ilEndrosementDetils.premiumPaymentDetailsNew.bankDetails.bankName', function (newvalue, oldvalue) {
                if (newvalue) {
                    var bankCode = _.findWhere($scope.bankDetailsResponse, {bankName: newvalue});
                    // //alert("Bank Details.."+JSON.stringify(bankCode));
                    if (bankCode) {
                        $http.get('/pla/individuallife/endorsement/getAllBankBranchNames/' + bankCode.bankCode).success(function (response, status, headers, config) {
                            $scope.bankBranchDetails = response;
                            //console.log("Bank Details :"+JSON.stringify(response));
                        }).error(function (response, status, headers, config) {
                        });
                    }
                }
            });

        $scope.$watch('ilEndrosementDetils.premiumPaymentDetailsNew.bankDetails.bankBranchName', function (newvalue, oldvalue) {
            if (newvalue) {
                $scope.ilEndrosementDetils.premiumPaymentDetailsNew.bankDetails.bankBranchSortCode = newvalue;
            }
        });

        /**
         * Clearing The Detail Related to Basnk Name
         */
            $scope.clearBankBranchName=function(){
                $scope.ilEndrosementDetils.premiumPaymentDetailsNew.bankDetails.bankBranchName = null;
                $scope.ilEndrosementDetils.premiumPaymentDetailsNew.bankDetails.bankBranchSortCode=null;
            }

            $scope.LADOB = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob= true;
            };
        /**
         *
         * @param $event
         * Updated Life Assured DoB
         */
            $scope.updateLADOB = function ($event) {
                if($scope.ilEndrosementDetils.lifeAssuredNew == null){
                    $scope.ilEndrosementDetils.lifeAssuredNew={};
                }
                    $event.preventDefault();
                    $event.stopPropagation();
                    $scope.datePickerSettingsForUpdateLifeAssuredDOB.isOpened = true;
                };

        $scope.datePickerSettingsForUpdateLifeAssuredDOB = {
            isOpened:false,
            dateOptions:{
                formatYear:'yyyy' ,
                startingDay:1

            }
        }

        /**
         * Date of Birth Of Trustee Individual
         * @param $event
         */
        $scope.launchTrusteeDobIl = function ($event) {
            if($scope.ilEndrosementDetils.beneficiariesNew == null){
                $scope.ilEndrosementDetils.beneficiariesNew=[];
            }
            $event.preventDefault();
            $event.stopPropagation();
            $scope.datePickerSettingsForUpdateILTrusteeDOB.isOpened = true;
        };

        $scope.datePickerSettingsForUpdateILTrusteeDOB = {
            isOpened:false,
            dateOptions:{
                formatYear:'yyyy' ,
                startingDay:1

            }
        }

        $scope.updateEffectiveDate = function ($event) {
            if($scope.ilEndrosementDetils.effectiveDate == null){
                $scope.ilEndrosementDetils.effectiveDate='';
            }
            $event.preventDefault();
            $event.stopPropagation();
            $scope.datePickerSettingsForUpdateEffectiveDate.isOpened = true;
        };

        $scope.datePickerSettingsForUpdateEffectiveDate = {
            isOpened:false,
            dateOptions:{
                formatYear:'yyyy' ,
                startingDay:1

            }
        }
        /**
         * @param $event
         * Update Policy Holder DOB
         */

        $scope.updatePHDOB = function ($event) {
            if($scope.ilEndrosementDetils.policyHolderNew == null){
                $scope.ilEndrosementDetils.policyHolderNew={};
            }
            $event.preventDefault();
            $event.stopPropagation();
            $scope.datePickerSettingsForUpdatePolicyHolderDOB.isOpened = true;
        };
        $scope.datePickerSettingsForUpdatePolicyHolderDOB = {
            isOpened:false,
            dateOptions:{
                formatYear:'yyyy' ,
                startingDay:1

            }
        }

        $scope.$watch('ilEndrosementDetils.lifeAssured.dateOfBirth',function(newVal,oldVal){
            if(newVal){
                var earlieAge=(moment($scope.ilEndrosementDetils.inceptionOn).diff(moment($scope.ilEndrosementDetils.lifeAssured.dateOfBirth), 'years'))+1;
                $scope.ilEndrosementDetils.lifeAssured.earlierAge=earlieAge;
            }
        });
        $scope.earlierAgeCalculation=function(){
            if($scope.ilEndrosementDetils.lifeAssured.dateOfBirth){
                var earlieAge=(moment($scope.ilEndrosementDetils.inceptionOn).diff(moment($scope.ilEndrosementDetils.lifeAssured.dateOfBirth), 'years'))+1;
                $scope.ilEndrosementDetils.lifeAssured.earlierAge=earlieAge;
            }
        }
        $scope.$watch('ilEndrosementDetils.lifeAssuredNew.dateOfBirth',function(newVal,oldVal){
            if (newVal == oldVal)return;
            //Corrected Age Calculation
            if(newVal && $scope.policy.endrosementType =='ASSURED_DOB_CHANGE'){
                var correcteAge=(moment($scope.ilEndrosementDetils.inceptionOn).diff(moment(newVal), 'years'))+1;
                $scope.ilEndrosementDetils.lifeAssured.correctedAge=correcteAge;
            }
        });

        $scope.$watch('ilEndrosementDetils.policyHolder.dateOfBirth',function(newVal,oldVal){
            if(newVal && $scope.policy.endrosementType =='POLICYHOLDER_DOB_CHANGE'){
                var earlieAge=(moment($scope.ilEndrosementDetils.inceptionOn).diff(moment($scope.ilEndrosementDetils.policyHolder.dateOfBirth), 'years'))+1;
                $scope.ilEndrosementDetils.policyHolder.earlierAge=earlieAge;
            }
        });

        $scope.earlierAgeCalculationForPolicyHolder=function(){
            if($scope.ilEndrosementDetils.policyHolder.dateOfBirth){
                var earlieAge=(moment($scope.ilEndrosementDetils.inceptionOn).diff(moment($scope.ilEndrosementDetils.policyHolder.dateOfBirth), 'years'))+1;
                $scope.ilEndrosementDetils.policyHolder.earlierAge=earlieAge;
            }
        }

        $scope.$watch('ilEndrosementDetils.policyHolderNew.dateOfBirth',function(newVal,oldVal){
            if(newVal == oldVal) return;
            if(newVal && $scope.policy.endrosementType =='POLICYHOLDER_DOB_CHANGE'){
                var correcteAge=(moment($scope.ilEndrosementDetils.inceptionOn).diff(moment(newVal), 'years'))+1;
                $scope.ilEndrosementDetils.policyHolderNew.correctedAge=correcteAge;
            }
        });

            $scope.launchBeneficiaryDob = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob4 = true;
            };
        /**
         * Getting Residential TownList of LifeAssured during change of Change of Contact Details- Life Assured
         */
        $scope.$watch('ilEndrosementDetils.lifeAssuredNew.residentialAddress.address.province',function(newVal,oldVal){
            if(newVal){
                var provinceDetails = _.findWhere($scope.provinces, {provinceId: newVal});
                if (provinceDetails)
                    $scope.townList = provinceDetails.cities;
            }
        });

        $scope.getTownList = function (province) {
            //alert(province);
            var provinceDetails = _.findWhere($scope.provinces, {provinceId: $scope.ilEndrosementDetils.lifeAssuredNew.residentialAddress.address.province});
            if (provinceDetails)
                $scope.townList = provinceDetails.cities;
        }

        /**
         * Getting EMPTownList of LifeAssured during change of Change of Contact Details- Life Assured
         */
        $scope.$watch('ilEndrosementDetils.lifeAssuredNew.employmentDetail.address.province',function(newVal,oldVal){
            if(newVal){
                var provinceDetails = _.findWhere($scope.provinces, {provinceId: newVal});
                if (provinceDetails)
                    $scope.townListForLAEmp = provinceDetails.cities;
            }
        });

        /**
         * Getting EMPTownList and ResidentialTownList of LifeAssured during change of Change of Contact Details- Policy Holder
         */

        $scope.$watch('ilEndrosementDetils.policyHolderNew.residentialAddress.address.province',function(newVal,oldVal){
            if(newVal){
                var provinceDetails = _.findWhere($scope.provinces, {provinceId: newVal});
                if (provinceDetails)
                    $scope.townListForPHRes = provinceDetails.cities;
            }
        });

        $scope.$watch('ilEndrosementDetils.policyHolderNew.employmentDetail.address.province',function(newVal,oldVal){
            if(newVal){
                var provinceDetails = _.findWhere($scope.provinces, {provinceId: newVal});
                if (provinceDetails)
                    $scope.townListForPHEmp = provinceDetails.cities;
            }
        });


        /**
         * Getting TownList of Trustee during change of Change of Beneficiary
         */

        $scope.getTrusteeProvinceValue = function (province) {
            var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
            if (provinceDetails)
                $scope.trusteeCities = provinceDetails.cities;
        }


        $scope.isShowTrusteeType=false; // To Decide Trustee Type and Trustee Information To Display or Not

        $scope.showBeneficiaryDob = function (dob) {
                if (dob != null) {
                    $scope.newBeneficiary.age = moment().diff(new moment(new Date(dob)), 'years');
                }
                        if (($scope.newBeneficiary.age >= 0) && ($scope.newBeneficiary.age < 18)) {
                            $scope.isShowTrusteeType = true;
                        }else{
                            $scope.isShowTrusteeType=false;
                        }
            };
        $scope.applicableRelationships=[]; //Will Take All The relationshipAccording to Beneficiary Age
        $scope.$watch('newBeneficiary.age', function (newVal, oldVal) {
            if (newVal) {
                $http.get("getallrelations/" + newVal).success(function (response, status, headers, config) {
                    $scope.applicableRelationships = response;
                }).error(function (response, status, headers, config) {
                });
            }
        });

            $scope.shareSumTest = function () {
                var sum = 0;
                for (var i=0; i< $scope.ilEndrosementDetils.beneficiariesNew.length;i++) {
                    sum = parseFloat(sum) + parseFloat($scope.ilEndrosementDetils.beneficiariesNew[i].share);
                }
                //console.log('sum: ' + sum);
                if (sum == 100.00) {
                    $scope.commisionStatus = false;
                    $scope.commisionMessage = false;
                }
                else {
                    $scope.commisionStatus = true;
                    $scope.commisionMessage = true;
                }
            };

        $scope.addBeneficiary = function (beneficiary) {
            if ($scope.ilEndrosementDetils.beneficiariesNew.length == 0) {
                $scope.ilEndrosementDetils.beneficiariesNew.push(beneficiary);
            }

            else {
                var checkLoopNameStatus = "true";
                for (var i=0;i< $scope.ilEndrosementDetils.beneficiariesNew.length;i++) {
                    if (beneficiary.nrc && $scope.ilEndrosementDetils.beneficiariesNew[i].nrc == beneficiary.nrc) {
                        checkLoopNameStatus = "false";
                        break;
                    } else if (($scope.ilEndrosementDetils.beneficiariesNew[i].firstName == beneficiary.firstName) &&
                        ($scope.ilEndrosementDetils.beneficiariesNew[i].gender == beneficiary.gender) && ((moment($scope.ilEndrosementDetils.beneficiariesNew[i].dateOfBirth).diff(moment(beneficiary.dateOfBirth), 'days')) == 0)) {
                        checkLoopNameStatus = "false";
                        break;
                    }else if (beneficiary.relationshipId == 'FATHER_IN_LAW' || beneficiary.relationshipId == 'MOTHER_IN_LAW' || beneficiary.relationshipId == 'FATHER' || beneficiary.relationshipId == 'MOTHER') {
                        if (($scope.ilEndrosementDetils.beneficiariesNew[i].relationshipId == beneficiary.relationshipId)) {
                            checkLoopNameStatus = "false";
                            break;
                        }
                    }
                }
                if (checkLoopNameStatus == "true") {
                    $scope.ilEndrosementDetils.beneficiariesNew.unshift(beneficiary);
                } else {
                    alert("This record is already existing");
                }
            }
            //$scope.clear();
            $scope.newBeneficiary={};
            $('#beneficialModal').modal('hide');
            console.log("BeneficiaryList:" + JSON.stringify($scope.ilEndrosementDetils.beneficiariesNew));
        };

        $scope.searchAgent = function () {
            $scope.check = false;
            $scope.checking = true;
            $scope.agentId = $scope.newAgent.agentId;

            $http.get("getagentdetail/" + $scope.agentId).success(function (response, status, headers, config) {
                $scope.newAgent = response;
                $scope.checking = false;
            }).error(function (response, status, headers, config) {
                var check = status;
                if (check == 500) {
                    $scope.check = true;
                    $scope.newAgent.firstName = null;
                    $scope.newAgent.lastName = null;
                }
            });

        };

        $scope.deleteAgent=function(agentId){
            for(var i=0;i< $scope.ilEndrosementDetils.agentCommissionDetailsNew.length;i++){
                if($scope.ilEndrosementDetils.agentCommissionDetailsNew[i].agentId == agentId){
                    $scope.ilEndrosementDetils.agentCommissionDetailsNew.splice(i,1);
                }
            }
            $scope.commisionSumTest();
        }

        $scope.commisionSumTest = function () {
            var sum = 0;
            for (var i=0;i< $scope.agentDetails.length;i++) {

                sum = parseFloat(sum) + parseFloat($scope.agentDetails[i].commission);
            }
            //console.log('sum: ' + sum);
            if (sum == 100.00) {
                $scope.agentMessage = false;
            }
            else {
                $scope.agentMessage = true;
            }
        };
        $scope.ilEndrosementDetilsCopy={};
        $scope.searchPolicyNumber=function(){
            if($scope.policy.policyNumber){
                $http.get('/pla/individuallife/endorsement/searchpolicy/' + $scope.policy.policyNumber)
                    .success(function (response) {
                        $scope.serverError = false;
                        var policyData=response.data;
                        $scope.ilEndrosementDetils=response.data;
                        if($scope.ilEndrosementDetils.effectiveDate == null){
                            $scope.ilEndrosementDetils.effectiveDate='';
                            $scope.ilEndrosementDetils.effectiveDate=moment().add(0,'days').format("YYYY-MM-DD");
                        }
                        angular.copy($scope.ilEndrosementDetils,$scope.ilEndrosementDetilsCopy); // Keeping One Copy Of Original Object
                        console.log('************');
                        console.log(JSON.stringify($scope.ilEndrosementDetils));
                        $scope.policy.policyHolderName=$scope.ilEndrosementDetils.policyHolder.firstName;
                        $scope.policy.endrosmentDate=$scope.ilEndrosementDetils.policyHolder.dateOfBirth;
                    }).error(function (response, status, headers, config) {
                        $scope.serverError = true;
                        $scope.serverErrMsg = response.message;
                    });
            }
        }

        /**
         * Clearing the Data to existing data
         */
        $scope.clearData=function(){
            angular.copy($scope.ilEndrosementDetilsCopy,$scope.ilEndrosementDetils);
            //Respective Field Populating
            if($scope.policy.endrosementType == 'ASSURED_NAME_CHANGE' && $scope.ilEndrosementDetils.lifeAssuredNew == null){
                $scope.ilEndrosementDetils.lifeAssuredNew={};
                angular.copy($scope.ilEndrosementDetils.lifeAssured,$scope.ilEndrosementDetils.lifeAssuredNew);
            }else if($scope.policy.endrosementType == 'POLICYHOLDER_NAME_CHANGE' && $scope.ilEndrosementDetils.policyHolderNew == null){
                $scope.ilEndrosementDetils.policyHolderNew={};
                angular.copy($scope.ilEndrosementDetils.policyHolder,$scope.ilEndrosementDetils.policyHolderNew);
            }else if($scope.policy.endrosementType == 'POLICYHOLDER_GENDER_CHANGE' && $scope.ilEndrosementDetils.policyHolderNew == null){
                $scope.ilEndrosementDetils.policyHolderNew={};
                angular.copy($scope.ilEndrosementDetils.policyHolder,$scope.ilEndrosementDetils.policyHolderNew);
            }else if($scope.policy.endrosementType == 'ASSURED_GENDER_CHANGE' && $scope.ilEndrosementDetils.lifeAssuredNew == null){
                $scope.ilEndrosementDetils.lifeAssuredNew={};
                angular.copy($scope.ilEndrosementDetils.lifeAssured,$scope.ilEndrosementDetils.lifeAssuredNew);
            }else if($scope.policy.endrosementType == 'ASSURED_DOB_CHANGE' && $scope.ilEndrosementDetils.lifeAssuredNew == null){
                $scope.ilEndrosementDetils.lifeAssuredNew={};
                angular.copy($scope.ilEndrosementDetils.lifeAssured,$scope.ilEndrosementDetils.lifeAssuredNew);
                $scope.ilEndrosementDetils.lifeAssuredNew.dateOfBirth='';
            }else if($scope.policy.endrosementType == 'POLICYHOLDER_DOB_CHANGE' && $scope.ilEndrosementDetils.policyHolderNew == null){
                $scope.ilEndrosementDetils.policyHolderNew={};
                angular.copy($scope.ilEndrosementDetils.policyHolder,$scope.ilEndrosementDetils.policyHolderNew);
                $scope.ilEndrosementDetils.policyHolderNew.dateOfBirth='';
            }else if($scope.policy.endrosementType == 'POLICYHOLDER_NRC_CHANGE' && $scope.ilEndrosementDetils.policyHolderNew == null){
                $scope.ilEndrosementDetils.policyHolderNew={};
                angular.copy($scope.ilEndrosementDetils.policyHolder,$scope.ilEndrosementDetils.policyHolderNew);
                $scope.ilEndrosementDetils.policyHolderNew.nrc='';
            }else if($scope.policy.endrosementType == 'ASSURED_DOB_CHANGE' && $scope.ilEndrosementDetils.lifeAssuredNew == null){
                $scope.ilEndrosementDetils.lifeAssuredNew={};
                angular.copy($scope.ilEndrosementDetils.lifeAssured,$scope.ilEndrosementDetils.lifeAssuredNew);
                $scope.ilEndrosementDetils.lifeAssuredNew.nrc='';
            }else if($scope.policy.endrosementType == 'ASSURED_CONTACT_DETAILS_CHANGE' && $scope.ilEndrosementDetils.lifeAssuredNew == null){
                $scope.ilEndrosementDetils.lifeAssuredNew={};
                angular.copy($scope.ilEndrosementDetils.lifeAssured,$scope.ilEndrosementDetils.lifeAssuredNew);
            }else if($scope.policy.endrosementType == 'POLICYHOLDER_CONTACT_DETAILS_CHANGE' && $scope.ilEndrosementDetils.policyHolderNew == null){
                $scope.ilEndrosementDetils.policyHolderNew={};
                angular.copy($scope.ilEndrosementDetils.policyHolder,$scope.ilEndrosementDetils.policyHolderNew);
            }else if($scope.policy.endrosementType == 'BENEFICIARY_DETAILS_CHANGE' && $scope.ilEndrosementDetils.beneficiariesNew == null){
                $scope.ilEndrosementDetils.beneficiariesNew=[];
                angular.copy($scope.ilEndrosementDetils.beneficiaries,$scope.ilEndrosementDetils.beneficiariesNew);
            }else if($scope.policy.endrosementType == 'PAYMENT_MODE_CHANGE' && $scope.ilEndrosementDetils.premiumPaymentDetailsNew == null){
                $scope.ilEndrosementDetils.premiumPaymentDetailsNew={};
                angular.copy($scope.ilEndrosementDetils.premiumPaymentDetails,$scope.ilEndrosementDetils.premiumPaymentDetailsNew);
            }else if($scope.policy.endrosementType == 'AGENT_DETAILS_CHANGE' && $scope.ilEndrosementDetils.agentCommissionDetailsNew == null){
                $scope.ilEndrosementDetils.agentCommissionDetailsNew=[];
                angular.copy($scope.ilEndrosementDetils.agentCommissionDetails,$scope.ilEndrosementDetils.agentCommissionDetailsNew);
            }

            if($scope.policy.endrosementType == 'ASSURED_DOB_CHANGE'){
                $scope.earlierAgeCalculation();
                $scope.planSetUp();
            }else if($scope.policy.endrosementType == 'POLICYHOLDER_DOB_CHANGE'){
                $scope.earlierAgeCalculationForPolicyHolder();
                $scope.planSetUp();
            }
        }

        /**
         * Method to Create Endrosement From Policy
         */
        $scope.createEndrosement=function(){
            angular.extend($scope.ilEndrosementDetils,{"ilEndorsementType":$scope.policy.endrosementType});
            console.log('** Submit..');
            console.log(JSON.stringify($scope.ilEndrosementDetils));
            // Validation Test for Required Object
            var endrosementTypeCheck= $scope.policy.endrosementType;
            if(endrosementTypeCheck == 'ASSURED_NAME_CHANGE'){
                if(angular.equals($scope.ilEndrosementDetils.lifeAssuredNew, $scope.ilEndrosementDetils.lifeAssured)){
                    $scope.serverError = true;
                    $scope.serverErrMsg = 'Both Existing and Updated Life Assured Name Details Should Not be Same.';
                    return;
                }
                else{
                    $scope.serverError = false;
                    $scope.serverErrMsg = '';
                }
            }else if(endrosementTypeCheck == 'POLICYHOLDER_NAME_CHANGE'){
                if(angular.equals($scope.ilEndrosementDetils.policyHolderNew, $scope.ilEndrosementDetils.policyHolder)){
                    $scope.serverError = true;
                    $scope.serverErrMsg = 'Both Existing and Updated Policy Holder Name Details Should Not be Same.';
                    return;
                }
                else{
                    $scope.serverError = false;
                    $scope.serverErrMsg = '';
                }
            }else if(endrosementTypeCheck == 'ASSURED_CONTACT_DETAILS_CHANGE'){
                if(angular.equals($scope.ilEndrosementDetils.lifeAssuredNew, $scope.ilEndrosementDetils.lifeAssured)){
                    $scope.serverError = true;
                    $scope.serverErrMsg = 'Both Existing and Updated Life Assured Contact Details Should Not be Same.';
                    return;
                }
                else{
                    $scope.serverError = false;
                    $scope.serverErrMsg = '';
                }
            }else if(endrosementTypeCheck == 'POLICYHOLDER_CONTACT_DETAILS_CHANGE'){
                if(angular.equals($scope.ilEndrosementDetils.policyHolderNew, $scope.ilEndrosementDetils.policyHolder)){
                    $scope.serverError = true;
                    $scope.serverErrMsg = 'Both Existing and Updated Policy Holder Contact Details Should Not be Same.';
                    return;
                }
                else{
                    $scope.serverError = false;
                    $scope.serverErrMsg = '';
                }
            }else if(endrosementTypeCheck == 'BENEFICIARY_DETAILS_CHANGE'){
                //Checking is Beneficiaries is Empty or Not
                if(!$scope.ilEndrosementDetils.beneficiariesNew.length >0){
                    $scope.serverError = true;
                    $scope.serverErrMsg = 'Beneficiary Details Should Not be Empty.';
                    angular.copy($scope.ilEndrosementDetils.beneficiaries,$scope.ilEndrosementDetils.beneficiariesNew);
                    return;
                }
                else{
                    $scope.serverError = false;
                    $scope.serverErrMsg = '';
                }
                //Checking is Beneficiaries's Share % Sum is 100 or Not
                if($scope.commisionMessage){
                    $scope.serverError = true;
                    $scope.serverErrMsg = 'Sum of share % is not 100..Please update share % to ensure it should be 100';
                    return;
                }
                else {
                    $scope.serverError = false;
                    $scope.serverErrMsg = '';
                }

                //Checking Existing and Updated Beneficiary Details is Same or Not
                if(angular.equals($scope.ilEndrosementDetils.beneficiariesNew,$scope.ilEndrosementDetils.beneficiaries)){
                    $scope.serverError = true;
                    $scope.serverErrMsg = 'Both Existing and Updated Beneficiary Details Should Not be Same.';
                    return;
                }
                else{
                    $scope.serverError = false;
                    $scope.serverErrMsg = '';
                }
            }else if(endrosementTypeCheck == 'PAYMENT_MODE_CHANGE'){
                if(angular.equals($scope.ilEndrosementDetils.premiumPaymentDetailsNew,$scope.ilEndrosementDetils.premiumPaymentDetails)){
                    $scope.serverError = true;
                    $scope.serverErrMsg = 'Both Existing and Updated Premium Payment Details Should Not be Same.';
                    return;
                }
                else{
                    $scope.serverError = false;
                    $scope.serverErrMsg = '';
                }
            }else if(endrosementTypeCheck == 'AGENT_DETAILS_CHANGE'){
                if(!$scope.ilEndrosementDetils.agentCommissionDetailsNew.length >0){
                    $scope.serverError = true;
                    $scope.serverErrMsg = 'Agent Details Should Not be Empty.';
                    angular.copy($scope.ilEndrosementDetils.agentCommissionDetails,$scope.ilEndrosementDetils.agentCommissionDetailsNew);
                    return;
                }else if($scope.agentMessage){
                    $scope.serverError = true;
                    $scope.serverErrMsg = 'Sum of commission % is not 100..Please update commission % to ensure it should be 100.';
                    return;
                }else if(angular.equals($scope.ilEndrosementDetils.agentCommissionDetailsNew,$scope.ilEndrosementDetils.agentCommissionDetails)){
                    $scope.serverError = true;
                    $scope.serverErrMsg = 'Both Existing and Updated Agent Details Should Not be Same.';
                    return;
                }
                else{
                    $scope.serverError = false;
                    $scope.serverErrMsg = '';
                }

            }

            if($scope.endorsementRequestNumber == null) {
                // Create Fresh Endrosement...
                $http.post('/pla/individuallife/endorsement/opencreateendorsementpage',$scope.ilEndrosementDetils).success(function (response, status, headers, config) {
                    console.log('*************');
                    console.log('Response:-'+response.id);
                    $scope.endorsementRequestNumber = response.id;
                }).error(function (response, status, headers, config) {
                });
            }
            else{
                // Update Endrosement
                console.log('Updating Endrosement...');
            }
        }

        $scope.planSetUp=function(){
            $http.get('/pla/core/plan/getPlanById/'+$scope.ilEndrosementDetils.proposalPlanDetail.planId).success(function (response, status, headers, config) {
                $scope.plan = response;
            }).error(function (response, status, headers, config) {
            });
        }

        $scope.changePolicyHolderGender=function(){
            if($scope.policy.endrosementType == 'POLICYHOLDER_GENDER_CHANGE'){
                if($scope.ilEndrosementDetils.policyHolderNew == null){
                    $scope.ilEndrosementDetils.policyHolderNew={};
                }
                if($scope.ilEndrosementDetils.policyHolder.gender == 'MALE'){
                    $scope.ilEndrosementDetils.policyHolderNew.gender ='FEMALE';
                }else if($scope.ilEndrosementDetils.policyHolder.gender == 'FEMALE'){
                    $scope.ilEndrosementDetils.policyHolderNew.gender ='MALE';
                }
            }
        }

        /**
         * LifeAssured Gender Change Operation is happening
         */
        $scope.changeLifeAssuredGender=function(){
            if($scope.policy.endrosementType == 'ASSURED_GENDER_CHANGE'){
                if($scope.ilEndrosementDetils.lifeAssuredNew == null){
                    $scope.ilEndrosementDetils.lifeAssuredNew={};
                }
                if($scope.ilEndrosementDetils.lifeAssured.gender == 'MALE'){
                    $scope.ilEndrosementDetils.lifeAssuredNew.gender ='FEMALE';
                }else if($scope.ilEndrosementDetils.lifeAssured.gender == 'FEMALE'){
                    $scope.ilEndrosementDetils.lifeAssuredNew.gender ='MALE';
                }
            }
        }

        /**
         * Update Beneficiary
         */
        $scope.newBeneficiary={};
        $scope.isUpdateBeneficiary=false;

        /**
         * Selected Beneficiary Detail retrival Logic
         * @param index
         */
        $scope.updateBeneficiary=function(index){
            angular.copy($scope.ilEndrosementDetils.beneficiariesNew[index],$scope.newBeneficiary);
            $scope.indexToUpdate=index;
            $scope.isUpdateBeneficiary=true;
        }

        $scope.deleteBeneficiary=function(index){
            $scope.ilEndrosementDetils.beneficiariesNew.splice(index,1);
        }

        /**
         * Updated Beneficary will Add To NewBeneficiary List
         * @param beneficiary
         */
        $scope.updateBeneficiaryToOriginalList = function (beneficiary) {

            $scope.ilEndrosementDetils.beneficiariesNew.splice($scope.indexToUpdate,1);

            if ($scope.ilEndrosementDetils.beneficiariesNew.length == 0) {
                $scope.ilEndrosementDetils.beneficiariesNew.push(beneficiary);
            }

            else {
                var checkLoopNameStatus = "true";
                for (var i=0;i< $scope.ilEndrosementDetils.beneficiariesNew.length;i++) {
                    if (beneficiary.nrc && $scope.ilEndrosementDetils.beneficiariesNew[i].nrc == beneficiary.nrc) {
                        checkLoopNameStatus = "false";
                        break;
                    } else if (($scope.ilEndrosementDetils.beneficiariesNew[i].firstName == beneficiary.firstName) &&
                        ($scope.ilEndrosementDetils.beneficiariesNew[i].gender == beneficiary.gender) && ((moment($scope.beneficiariesList[i].dateOfBirth).diff(moment(beneficiary.dateOfBirth), 'days')) == 0)) {
                        checkLoopNameStatus = "false";
                        break;
                    }else if (beneficiary.relationshipId == 'FATHER_IN_LAW' || beneficiary.relationshipId == 'MOTHER_IN_LAW' || beneficiary.relationshipId == 'FATHER' || beneficiary.relationshipId == 'MOTHER') {
                        if (($scope.ilEndrosementDetils.beneficiariesNew[i].relationshipId == beneficiary.relationshipId)) {
                            checkLoopNameStatus = "false";
                            break;
                        }
                    }
                }
                if (checkLoopNameStatus == "true") {
                    $scope.ilEndrosementDetils.beneficiariesNew.unshift(beneficiary);
                } else {
                    alert("This record is already existing");
                }
            }
            //$scope.clear();
            $scope.newBeneficiary={};
            $('#beneficialModal').modal('hide');
            console.log("BeneficiaryList:" + JSON.stringify($scope.beneficiariesList));
        };

        //Clearing The Assignning Beneficiary from Local Object On Clicking On Cancel button
        $scope.clearNewBeneficiary=function(){
            $scope.newBeneficiary={};
            $('#beneficialModal').modal('hide');
        }

        $scope.isBeneficiaryTrusteeDOBSame = false; // Variable to Test DOB of Both Trustee and Beneficiary are same Or Not

        /**
         * Watch Function Are Meant to Check Dob Of Both Beneficiary and Trustee Are Same Or Not
         */
        $scope.$watch('newBeneficiary.trusteeDetail.dateOfBirth', function (newVal, oldVal) {
            if (newVal && $scope.newBeneficiary.dateOfBirth) {
                if (((moment(newVal).diff(moment($scope.newBeneficiary.dateOfBirth), 'days')) == 0)) {
                    //alert('Both Are Same..');
                    $scope.isBeneficiaryTrusteeDOBSame = true;
                }
                else {
                    $scope.isBeneficiaryTrusteeDOBSame = false;
                }
            }
        });
        $scope.$watch('newBeneficiary.dateOfBirth', function (newVal, oldVal) {
            if($scope.newBeneficiary.trusteeDetail == null){
                $scope.newBeneficiary.trusteeDetail={};
            }
            if (newVal && $scope.newBeneficiary.trusteeDetail.dateOfBirth != null) {
                if (newVal == $scope.newBeneficiary.trusteeDetail.dateOfBirth) {
                    $scope.isBeneficiaryTrusteeDOBSame = true;
                }
                else {
                    $scope.isBeneficiaryTrusteeDOBSame = false;
                }
            }
        });

        /**
         * Checking For Age of Trustee
         * @type {boolean}
         */
        $scope.isTrusteeValid = true; //Testing For Trustee's Age is Above 18yr or less.
        $scope.showTrusteeAge = function (dob) {
            if (dob != null) {
                $scope.newBeneficiary.trusteeDetail.age = moment().diff(new moment(new Date(dob)), 'years');
            }
            $scope.isTrusteeValid = false;
            //$scope.beneficiary.age = moment().diff(new moment(new Date(dob)), 'years');

            if ($scope.newBeneficiary.trusteeDetail.age < 18) {
                $scope.isTrusteeValid = false;
            }
            else {
                $scope.isTrusteeValid = true;
            }
        };


        /**
         *
         * Chceking the FirstName of Beneficiary and Trustee FirstName Same or Not
         */
        $scope.isBeneficiaryTrusteeNameSame = false; //To Test Beneficiary and Trustee Name Same or Not
        $scope.$watch('newBeneficiary.trusteeDetail.firstName', function (newVal, oldVal) {

            if (newVal) {
                if (newVal == $scope.newBeneficiary.firstName) {
                    //alert('both are Same');
                    $scope.isBeneficiaryTrusteeNameSame = true;
                }
                else {
                    $scope.isBeneficiaryTrusteeNameSame = false;
                }
            }

        });

        $scope.$watch('newBeneficiary.firstName', function (newVal, oldVal) {
            if (newVal) {

                if (newVal == $scope.newBeneficiary.trusteeDetail.firstName) {
                    //alert('Both Are Same..');
                    $scope.isBeneficiaryTrusteeNameSame = true;
                }
                else {
                    $scope.isBeneficiaryTrusteeNameSame = false;
                }
            }
        });

        /**
         *
         * Checking the NRC of Beneficiary and Trustee FirstName Same or Not
         */
        $scope.isBeneficiaryTrusteeNRCSame = false; // To Test Beneficiary and Trustee NRC same Or Not
        $scope.$watch('newBeneficiary.nrc', function (newVal, oldVal) {
            if (newVal) {

                if (newVal == $scope.newBeneficiary.trusteeDetail.nrc) {
                    //alert('Both Are  Same..');
                    $scope.isBeneficiaryTrusteeNRCSame = true;
                }
                else {
                    $scope.isBeneficiaryTrusteeNRCSame = false;
                }
            }
        });
        $scope.$watch('newBeneficiary.trusteeDetail.nrc', function (newVal, oldVal) {
            if (newVal) {

                if (newVal == $scope.newBeneficiary.nrc) {
                    //alert('Both Are Same..');
                    $scope.isBeneficiaryTrusteeNRCSame = true;
                }
                else {
                    $scope.isBeneficiaryTrusteeNRCSame = false;
                }
            }
        });


        $scope.agentMessage = false; //Agents Commission Sum Checking is 100 Or Not
        /***
         * Commision Test For All Agent
         */
        $scope.commisionSumTest = function () {
            var sum = 0;
            for (var i in $scope.ilEndrosementDetils.agentCommissionDetailsNew) {
                sum = parseFloat(sum) + parseFloat($scope.ilEndrosementDetils.agentCommissionDetailsNew[i].commission);
            }
            if (sum == 100.00) {
                $scope.agentMessage = false;
            }
            else {
                $scope.agentMessage = true;
            }
        };

        /**
         * Agent Search Logic
         */

        $scope.searchAgent = function () {
            $scope.check = false;
            $scope.checking = true;
            //console.log('Testing In SearchCode..');
            $scope.agentId = $scope.newAgent.agentId;
            //console.log('Value is: ' + $scope.agentId);
            $http.get("getAgentDetailsByPlanAndAgentId/"+$scope.ilEndrosementDetils.proposalPlanDetail.planId+'/' + $scope.agentId).success(function (response, status, headers, config) {
                $scope.newAgent = response;
                $scope.checking = false;
            }).error(function (response, status, headers, config) {
                var check = status;
                if (check == 500) {
                    $scope.check = true;
                    $scope.newAgent.firstName = null;
                    $scope.newAgent.lastName = null;
                }
            });

        };

        /**
         * Agent Addition
         * @param agent
         */
        $scope.addAgent = function (agent) {
            if ($scope.ilEndrosementDetils.agentCommissionDetailsNew.length == 0) {
                $scope.ilEndrosementDetils.agentCommissionDetailsNew.unshift(agent);
            }
            else {
                var checkLoopNameStatus = "true";

                for (var i in $scope.ilEndrosementDetils.agentCommissionDetailsNew) {
                    if ($scope.ilEndrosementDetils.agentCommissionDetailsNew[i].agentId == agent.agentId) {
                        checkLoopNameStatus = "false";
                        break;
                    }
                }

                if (checkLoopNameStatus == "true") {
                    $scope.ilEndrosementDetils.agentCommissionDetailsNew.unshift(agent);
                }
                else {
                    alert("Please Select Different AgentId");
                }
            }
            $scope.newAgent={};
            $('#agentModal').modal('hide');
        };

        $scope.addAdditionalDocument = function () {
            $scope.additionalDocumentList.unshift({});
            $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();

        };

        $scope.removeAdditionalDocument = function (index) {
            $scope.additionalDocumentList.splice(index, 1);
            $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
        };

        $scope.callAdditionalDoc = function (file) {
            if (file[0]) {
                $scope.checkDocumentAttached = $scope.isUploadEnabledForAdditionalDocument();
            }
        }

        $scope.isUploadEnabledForAdditionalDocument = function () {
            var enableAdditionalUploadButton = ($scope.additionalDocumentList != null);
            for (var i = 0; i < $scope.additionalDocumentList.length; i++) {
                var document = $scope.additionalDocumentList[i];
                var files = document.documentAttached;
                //alert(i+"--"+files)
                //alert(i+"--"+document.content);
                if (!(files || document.content)) {
                    enableAdditionalUploadButton = false;
                    break;
                }
            }
            return enableAdditionalUploadButton;
        }

        $scope.documentNameLengthTest=function(){
            var enableAdditionalUploadButton= true;
            for(var i=0;i < $scope.documentList.length;i++){
                if($scope.documentList[i].documentAttached != null && $scope.documentList[i].documentAttached[0] != null){
                    //alert($scope.documentList[i].documentAttached[0].name.length);
                    if($scope.documentList[i].documentAttached[0].name.length >100)
                    {
                        enableAdditionalUploadButton=false;
                        break;
                    }
                }
            }
            if(enableAdditionalUploadButton){
                 return true;
            }
            else
            {
                return false
            }
        }


        $scope.documentList=[
            {
                "documentId":"BROUGHT_IN_DEAD_(BID)_CERTIFICATE",
                "documentName":"BROUGHT IN DEAD(BID) CERTIFICATE",
                "contentType":"application/x-javascript",
                "gridFsDocId":"56040bfe7c8508839f7c0f56",
                "mandatory":true,
                "requireForSubmission":true,
                "approved":true
            },
            {
                "documentId":"ANY_CLINICAL_ABSTRACTS_RECORDS_IF_AVAILABLE",
                "documentName":"ANY CLINICAL ABSTRACTS RECORDS IF AVAILABLE",
                "contentType":"application/octet-stream",
                "gridFsDocId":"56040bfe7c8508839f7c0f58",
                "mandatory":true,
                "requireForSubmission":true,
                "approved":true
            },
            {
                "documentId":"ADDRESS_PROOF",
                "documentName":"ADDRESS PROOF",
                "contentType":"application/octet-stream",
                "gridFsDocId":"56040bff7c8508839f7c0f5a",
                "mandatory":true,
                "requireForSubmission":true,
                "approved":true
            },
            {
                "documentId":"BURIAL_CERTIFICATE",
                "documentName":"BURIAL CERTIFICATE",
                "contentType":"application/octet-stream",
                "gridFsDocId":"56040bff7c8508839f7c0f5c",
                "mandatory":true,
                "requireForSubmission":true,
                "approved":true
            }
        ];

    }])
})(angular);