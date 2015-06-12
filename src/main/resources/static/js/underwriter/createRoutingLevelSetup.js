var App = angular.module('createRoutingLevel', ['ngRoute', 'ui.bootstrap', 'ngSanitize','angularFileUpload','mgcrea.ngStrap.select','checklist-model']);

App.controller('CreateRoutingLevelController', ['$scope', '$http', function ($scope, $http) {

   console.log(JSON.stringify(createRoutingLevel));
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
    $scope.selectedUsers = [];
    $scope.datePickerSettings = {
        isOpened: false,
        dateOptions: {
            formatYear: 'yyyy',
            startingDay: 1
        }
    };
    $scope.createRoutingLevel = createRoutingLevel;
    $scope.open = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.datePickerSettings.isOpened = true;
    };

    $scope.$watch('createRoutingLevel.definedFor',function(newValue, oldValue){
        if(newValue=='optionalCoverage'){
            $scope.createRoutingLevel.planId="";
            $scope.createRoutingLevel.coverageId="";
            $scope.showOptionalCoverageValue = false;
        }else if (newValue == 'plan'){
            $scope.createRoutingLevel.planId="";
            $scope.showOptionalCoverageValue = true;

        }

    });

    $scope.$watch('createRoutingLevel.coverageId',function(newValue, oldValue){

        if(newValue){

            $scope.showOptionalCoverageValue=true;
        }
    });


    $http.get('/pla/underwriter/getplancoveragedetail').success(function (data) { /*/pla/underwriter/getplancoveragedetail*/
                   $scope.planList = data;
    });

    $scope.$watch('createRoutingLevel.planCode', function (newValue, oldValue) {
        if (newValue) {
            var planCode = $scope.createRoutingLevel.planCode;
            $scope.routingForm.planCode = planCode;
           var planDetails= _.findWhere($scope.planList, {planCode: planCode});
          //  console.log(planDetails);
             $scope.createRoutingLevel.planName = planDetails.planName;

            $http.get('/pla/underwriter/getoptionalcoverage/' + planDetails.planId).success(function (data) { /*/pla/underwriter/getoptionalcoverage/*/
                $scope.optionalCoverageList = data[0].coverageDtoList;
                //console.log($scope.optionalCoverageList);
            });
        }
    });

    $http.get('/pla/underwriter/getunderwriterprocess').success(function (data) {
       //console.log(data);
        $scope.processList=data;
    });
    $scope.$watch('createRoutingLevel.processType',function(newValue, oldValue) {
        if(newValue) {

            $http.get('/pla/underwriter/getunderwriterinfluencingfactor/'+ newValue).success(function (data) {

                $scope.mulSelect = data;

            });
        }
    });

    $scope.createRoutingLevel.underWriterInfluencingFactors=[];
    $scope.underWriterInfluencingFactors=[];
    $scope.checkboxValues=false;
    $scope.toggleSelection = function toggleSelection(influencingFactorCode) {
        $scope.createRoutingLevel.underWriterInfluencingFactors = $scope.underWriterInfluencingFactors ;
        var idx = $scope.createRoutingLevel.underWriterInfluencingFactors.indexOf(influencingFactorCode);
       // alert($scope.createRoutingLevel.underWriterInfluencingFactors.length);
        if (idx > -1) {
            $scope.createRoutingLevel.underWriterInfluencingFactors.splice(idx, 1);
            $scope.underWriterInfluencingFactors = $scope.createRoutingLevel.underWriterInfluencingFactors;


        }
        else {
            $scope.createRoutingLevel.underWriterInfluencingFactors.push(influencingFactorCode);
            $scope.underWriterInfluencingFactors = $scope.createRoutingLevel.underWriterInfluencingFactors;
        }
    if($scope.createRoutingLevel.underWriterInfluencingFactors.length > 0){
        $scope.checkboxValues=true;
    }else{
        $scope.checkboxValues=false;
    }
    };


    $scope.$watch('createRoutingLevel.effectiveFrom', function (newValue, oldValue) {
        if (newValue) {
            if (!moment($scope.createRoutingLevel.effectiveFrom, 'DD/MM/YYYY').isValid()) {
                $scope.newDateField.fromDate = moment($scope.createRoutingLevel.effectiveFrom).format("DD/MM/YYYY");
                $scope.createRoutingLevel.effectiveFrom = $scope.newDateField.fromDate;
            }
        }
    });


}]);
