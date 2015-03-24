define('app/planSetUpCtrl', [], function (app) {
    'use strict';
    function planSetUpCtrl($scope) {

        $scope.onlyNumbers = /^[1-9]+$/;
        $scope.today = function () {
            $scope.dt = new Date();
        };
        $scope.today();
        $scope.productName;
        $scope.planSetUpForm = {};
        $scope.relations = [
            {val: 'SELF', desc: 'Self'},
            {val: 'SISTER', desc: 'Sister'},
            {val: 'BROTHER', desc: 'Brother'},
            {val: 'WIFE', desc: 'Wife'},
            {val: 'FATHER', desc: 'Father'},
            {val: 'MOTHER', desc: 'Mother'},
            {val: 'SON', desc: 'Son'},
            {val: 'DAUGHTER', desc: 'Self'},
            {val: 'STEP_DAUGHTER', desc: 'Step Daughter'},
            {val: 'FATHER_IN_LAW', desc: 'Father-in-law'},
            {val: 'MOTHER_IN_LAW', desc: 'Mother-in-law'},
            {val: 'DEPENDENTS', desc: 'Dependents'}];

        $scope.endorsementTypes = [
            {val: 'NAME', desc: 'Correction of Name', clientType: 'INDIVIDUAL'},
            {val: 'ADDRESS', desc: 'Change of Address', category: 'INDIVIDUAL'},
            {val: 'BENEFICIARY', desc: 'Change/Add Beneficiary', category: 'INDIVIDUAL'},
            {val: 'PAYMENT', desc: 'Change method of Payment', category: 'INDIVIDUAL'},
            {val: 'AGENT', desc: 'Change Agent', category: 'INDIVIDUAL'},
            {val: 'CHANGE_PAYER', desc: 'Change Sum Assured', category: 'INDIVIDUAL'},
            {val: 'SUM_ASSURED', desc: 'Change Sum Assured', category: 'INDIVIDUAL'},
            {val: 'DATE_OF_BIRTH', desc: 'Change Life Assured Date of Birth', category: 'INDIVIDUAL'},
            {val: 'MEMBER_ADDITION', desc: 'Member Addition', category: 'GROUP'},
            {val: 'MEMBER_DELETION', desc: 'Member Deletion', category: 'GROUP'},
            {val: 'PROMOTION', desc: 'Promotion', category: 'GROUP'},
            {val: 'NEW_COVER', desc: 'Introduction of New Cover', category: 'GROUP'}
        ];

        $scope.$watch('clientType', function (val, old) {
            if (!angular.isUndefined(val))
                $scope.plan.planDetail.clientType = val;
        });

        $scope.coverageList = [{coverageId: '1', coverageName: 'Coverage 1'},
            {coverageId: '2', coverageName: 'Coverage 2'},
            {coverageId: '3', coverageName: 'Coverage 3'},
            {coverageId: '4', coverageName: 'Coverage 4'},
            {coverageId: '5', coverageName: 'Coverage 5'}];

        $scope.isPaymentTermByValue = function () {
            return $scope.plan.premiumTermType == 'SPECIFIED_VALUES';
        };

        $scope.isPolicyTermByValue = function () {
            console.log($scope.plan.policyTermType || 'none');
            return $scope.plan.policyTermType == 'SPECIFIED_VALUES';
        };

        $scope.isSumAssuredTypeRange = function () {
            return $scope.plan.sumAssured.sumAssuredType == 'RANGE';
        };

        $scope.isSurrenderDisabled = function () {
            if ($scope.clientType == 'GROUP') {
                $scope.surrenderAfter = '';
                return true;
            }
        };

        $scope.isSurrenderReq = function () {
            if ($scope.clientType == 'INDIVIDUAL')
                return 'required';
        };

        $scope.newCoverage = {};

        $scope.addCoverage = function () {
            var newCoverageStr;
            newCoverageStr = angular.toJson($scope.newCoverage, true);
            console.log(newCoverageStr);
            $scope.plan.coverages.push($scope.newCoverage);
            $scope.newCoverage = {};
        };

        $scope.removeCoverage = function (idx) {
            $scope.plan.coverages.splice(idx, 1);
        };

        $scope.isCoverageSumAssuredDisabled = function (type) {
            return $scope.newCoverage.coverageSumAssuredType != type;
        };

        $scope.isCoverageTermDisabled = function (type) {
            return $scope.newCoverage.coverageTermType != type;
        };

        $scope.addMaturityRow = function () {
            $scope.maturityAmounts.push({});
        };

        $scope.plan = {
            planDetails: {},
            policyTerm: {},
            premiumTerm: {},
            sumAssured: {},
            coverages: [],
            maturityAmounts: []
        };

        $scope.submit = function () {
            console.log($scope.plan);
        };

        $scope.datePickerSettings = {
            isOpened: false,
            dateOptions: {
                formatYear: 'yyyy',
                startingDay: 1
            }
        };
        $scope.openLaunchDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.launchDateOpen = true;

        };

        $scope.openWithdrawalDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.withdrawalDateOpen = true;

        };

        $scope.launchDt;

        $scope.$watch('launchDt', function (newVal) {
            if (!angular.isUndefined(newVal)) {
                $scope.plan.planDetail.launchDate = moment(newVal).format('YYYY-MM-DD');
            }
        });

    }

    planSetUpCtrl.$inject = ['$scope'];
    return planSetUpCtrl;

});

define('app/planSetUpModule', ['app/planSetUpCtrl'],
    function (planSetUpCtrl) {
        var app = angular.module('coreApp', ['ngTagsInput', 'ui.bootstrap', 'ui.bootstrap.tpls', 'checklist-model']);
        app.config(function (tagsInputConfigProvider) {
            tagsInputConfigProvider.setDefaults('tagsInput', {
                placeholder: 'Add a number',
                removeTagSymbol: 'x',
                minLength: 1,
                maxLength: 3
            });
        });
        app.config(['datepickerPopupConfig', function (datepickerPopupConfig) {
            datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
            datepickerPopupConfig.currentText = 'Today';
            datepickerPopupConfig.clearText = 'Clear';
            datepickerPopupConfig.closeText = 'Done';
            datepickerPopupConfig.closeOnDateSelection = true;
        }]);
        app.controller('planSetUpCtrl', planSetUpCtrl);
    });

require(['app/planSetUpModule', 'ui-bootstrap-tpls'],
    function () {
        angular.bootstrap(document, ['coreApp']);
    }
);
