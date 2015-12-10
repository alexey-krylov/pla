var App = angular.module('createPremium', ['ngRoute', 'ui.bootstrap', 'ngSanitize']);

App.controller('CreatePremiumController', ['$scope', '$http', function ($scope, $http) {

    console.log(JSON.stringify(createPremium));
    $scope.uploaded = false;
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
            $scope.showOptionalCoverage = false;
        } else {
            $scope.showOptionalCoverage = true;
        }

    }

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

    $http.get('/pla/core/premium/getpremiuminfluencingfactors').success(function (data) {
        $scope.mulSelect = data;
    });

    $scope.$watch('createPremium.planId', function (newValue, oldValue) {
        if (newValue) {
            var planId = $scope.createPremium.planId;
            $scope.premiumForm.planId = planId;
            $http.get('/pla/core/plan/getcoveragebyplanid/' + planId).success(function (data) {
                $scope.optionalCoverageList = data;
                console.log($scope.optionalCoverageList);
                /*if($scope.optionalCoverageList.length == 1){
                    if($scope.optionalCoverageList[0].coverageTermType == 'SINGLE'){
                        $scope.createPremium.premiumPaymentTermType='SINGLEPREMIUM';
                    }
                    else{
                        $scope.createPremium.premiumPaymentTermType='OTHERPREMIUM';
                    }
                }*/
            });
        }
    });

    $scope.createPremium.premiumInfluencingFactors=[];

    $scope.toggleSelection=function($event,influencingFactorCode) {
        var checkbox = $event.target;
        if(checkbox.checked)
        {
            $scope.createPremium.premiumInfluencingFactors.push(influencingFactorCode);
        }
        else
        {
            for (var i = 0; i < $scope.createPremium.premiumInfluencingFactors.length; i++) {

                if($scope.createPremium.premiumInfluencingFactors[i] == influencingFactorCode)
                {
                    $scope.createPremium.premiumInfluencingFactors.splice(i, 1);
                }
            }
        }
    }

    $scope.$watch('createPremium.effectiveFrom', function (newValue, oldValue) {
        if (newValue) {
            if (!moment($scope.createPremium.effectiveFrom, 'DD/MM/YYYY').isValid()) {
                $scope.newDateField.fromDate = moment($scope.createPremium.effectiveFrom).format("DD/MM/YYYY");
                $scope.createPremium.effectiveFrom = $scope.newDateField.fromDate;
            }
        }
    });

    $scope.isDownloadTemplateEnabled=function(){

        if($scope.createPremium.planId && $scope.createPremium.definedFor && $scope.createPremium.premiumFactor && $scope.createPremium.premiumInfluencingFactors.length >0 ){
            return true;
        }
        else{
            return false;
        }
    }

}]);