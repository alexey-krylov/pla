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

            $http.get('/pla/core/master/getgeodetail').success(function (response, status, headers, config) {
                $scope.provinces = response;
            }).error(function (response, status, headers, config) {
            });

            $http.get('/pla/individuallife/endorsement/getAllBankNames').success(function (response, status, headers, config) {
                $scope.bankDetailsResponse = response;
                //console.log("Bank Details :"+JSON.stringify(response));
            }).error(function (response, status, headers, config) {
            });

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

            $scope.getEmpTownList = function (province) {
                //alert(province);
                var provinceDetails = _.findWhere($scope.provinces, {provinceId: province});
                if (provinceDetails)
                    $scope.empTownList = provinceDetails.cities;
            }

            $scope.showBeneficiaryDob = function (dob) {
                    if (dob != null) {
                        $scope.newBeneficiary.age = moment().diff(new moment(new Date(dob)), 'years');
                    }
                };

            $scope.shareSumTest = function () {
                var sum = 0;
                for (var i=0; i< $scope.beneficiariesList.length;i++) {
                    sum = parseFloat(sum) + parseFloat($scope.beneficiariesList[i].share);
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
            if ($scope.beneficiariesList.length == 0) {
                $scope.beneficiariesList.push(beneficiary);
            }

            else {
                var checkLoopNameStatus = "true";
                for (var i=0;i< $scope.beneficiariesList.length;i++) {
                    if (beneficiary.nrc && $scope.beneficiariesList[i].nrc == beneficiary.nrc) {
                        checkLoopNameStatus = "false";
                        break;
                    } else if (($scope.beneficiariesList[i].firstName == beneficiary.firstName) &&
                        ($scope.beneficiariesList[i].gender == beneficiary.gender) && ((moment($scope.beneficiariesList[i].dateOfBirth).diff(moment(beneficiary.dateOfBirth), 'days')) == 0)) {
                        checkLoopNameStatus = "false";
                        break;
                    }
                }
                if (checkLoopNameStatus == "true") {
                    $scope.beneficiariesList.unshift(beneficiary);
                } else {
                    alert("This record is already existing");
                }
            }
            //$scope.clear();
            $scope.newBeneficiary={};
            $('#beneficialModal').modal('hide');
            console.log("BeneficiaryList:" + JSON.stringify($scope.beneficiariesList));
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

        $scope.addAgent = function (agent) {
            if ($scope.agentDetails.length == 0) {
                $scope.agentDetails.unshift(agent);
            }
            else {
                var checkLoopNameStatus = "true";

                for (var i=0;i< $scope.agentDetails.length;i++) {
                    if ($scope.agentDetails[i].agentId == agent.agentId) {
                        checkLoopNameStatus = "false";
                        break;
                    }
                }

                if (checkLoopNameStatus == "true") {
                    $scope.agentDetails.unshift(agent);
                }
                else {
                    alert("Particular AgentId is Already Added..Please Choose different AgentId");
                }
            }
            $scope.newAgent={};
            $('#agentModal').modal('hide');
        };
        $scope.deleteAgent=function(agentId){
            for(var i=0;i< $scope.agentDetails.length;i++){
                if($scope.agentDetails[i].agentId == agentId){
                    $scope.agentDetails.splice(i,1);
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
                    $scope.serverErrMsg = 'Both Existing and Updated Life Assured Name Details Should Not be Same..';
                    return;
                }
                else{
                    $scope.serverError = false;
                    $scope.serverErrMsg = '';
                }
            }else if(endrosementTypeCheck == 'POLICYHOLDER_NAME_CHANGE'){
                if(angular.equals($scope.ilEndrosementDetils.policyHolderNew, $scope.ilEndrosementDetils.policyHolder)){
                    $scope.serverError = true;
                    $scope.serverErrMsg = 'Both Existing and Updated Policy Holder Name Details Should Not be Same..';
                    return;
                }
                else{
                    $scope.serverError = false;
                    $scope.serverErrMsg = '';
                }
            }else if(endrosementTypeCheck == 'ASSURED_CONTACT_DETAILS_CHANGE'){
                if(angular.equals($scope.ilEndrosementDetils.lifeAssuredNew, $scope.ilEndrosementDetils.lifeAssuredNew)){
                    $scope.serverError = true;
                    $scope.serverErrMsg = 'Both Existing and Updated Life Assured Contact Details Should Not be Same..';
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