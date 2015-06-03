var App = angular.module('createPremium', ['ngRoute', 'ui.bootstrap', 'ngSanitize','angularFileUpload','mgcrea.ngStrap.select','checklist-model']);

App.controller('CreatePremiumController', ['$scope', '$http', function ($scope, $http) {

    console.log(JSON.stringify(createPremium));
    $scope.uploaded = false;
    $scope.showOptionalCoverageValue=true;
    $scope.verified = false;
    $scope.boolVal = false;
    $scope.newPlanList = [];
    $scope.coverageList = [];
    $scope.optionalCoverageList = [];
    $scope.showOptionalCoverage = false;
    $scope.selectedDate = moment().add(1, 'days').format("YYYY-MM-DD");
    $scope.newDateField = {};
    $scope.datePickerSettings = {
        isOpened: false,
        dateOptions: {
            formatYear: 'yyyy',
            startingDay: 1
        }
    };
    $scope.createPremium = createPremium;
    $scope.open = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.datePickerSettings.isOpened = true;
    };

    $scope.getDefinedOption = function () {

        if ($scope.createPremium.definedFor == "plan") {
            $scope.showOptionalCoverage = true;
        } else {
           // $scope.createPremium.definedFor = "optionalCoverage"
            $scope.showOptionalCoverage = true;

        }

    }


    $scope.$watch('createPremium.definedFor',function(newValue, oldValue){
        if(newValue=='optionalCoverage'){
            $scope.createPremium.planId="";
            $scope.createPremium.coverageId="";
            $scope.showOptionalCoverageValue = false;
        }else if (newValue == 'plan'){
            $scope.createPremium.planId="";
            $scope.showOptionalCoverageValue = true;

        }

    });
    $scope.$watch('createPremium.coverageId',function(newValue, oldValue){
        if(newValue){

            $scope.showOptionalCoverageValue=true;
        }
    });


    $scope.createPremium.premiumInfluencingFactors=[];
    $scope.premiumInfluencingFactors=[];
    $scope.checkboxValues=false;
    $scope.toggleSelection = function toggleSelection(influencingFactorCode) {
        $scope.createPremium.premiumInfluencingFactors = $scope.premiumInfluencingFactors ;
        var idx = $scope.createPremium.premiumInfluencingFactors.indexOf(influencingFactorCode);
        // alert($scope.createPremium.premiumInfluencingFactors.length);
        if (idx > -1) {
            $scope.createPremium.premiumInfluencingFactors.splice(idx, 1);
            $scope.premiumInfluencingFactors = $scope.createPremium.premiumInfluencingFactors;


        }
        else {
            $scope.createPremium.premiumInfluencingFactors.push(influencingFactorCode);
            $scope.premiumInfluencingFactors = $scope.createPremium.premiumInfluencingFactors;
        }
        if($scope.createPremium.premiumInfluencingFactors.length > 0){
            $scope.checkboxValues=true;
        }else{
            $scope.checkboxValues=false;
        }
    };


    $http.get('/pla/core/plan/getallplan').success(function (data) {
        for (var i = 0; i < data.length; i++) {
            $scope.planList = data[i];
            $scope.newPlanList.push({
                planName: $scope.planList.planDetail.planName,
                planId: $scope.planList.planId,
                coverages: $scope.planList.coverages
            });
        }
    });
    $http.get('/pla/core/premium/getpremiuminfluencingfactors').success(function(data){

        $scope.mulSelect=data;

    });
    $scope.$watch('createPremium.planId', function (newValue, oldValue) {
        if (newValue) {
            $scope.showOptionalCoverage = true;
            var planId = $scope.createPremium.planId;
            $scope.premiumForm.planId = planId;
            $http.get('/pla/core/plan/getcoveragebyplanid/' + planId).success(function (data) {
                $scope.optionalCoverageList = data;
                //console.log($scope.optionalCoverageList);
            });
        }
    });

    $scope.$watch('createPremium.effectiveFrom', function (newValue, oldValue) {
        if (newValue) {
            if (!moment($scope.createPremium.effectiveFrom, 'DD/MM/YYYY').isValid()) {
                $scope.newDateField.fromDate = moment($scope.createPremium.effectiveFrom).format("DD/MM/YYYY");
                $scope.createPremium.effectiveFrom = $scope.newDateField.fromDate;
            }
        }
    });


}]);
