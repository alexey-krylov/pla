angular.module('createCommission', ['common','commonServices'])

    .controller('CreateCommissionController',['$scope','formatJSDateToDDMMYYYY','getQueryParameter',function($scope,formatJSDateToDDMMYYYY,getQueryParameter){
        var mode = getQueryParameter("mode");
        $scope.createCommission = {
            commissionTermSet:[],
            commissionType:mode
        };
        if(mode=='OVERRIDE'){
            $scope.override = "Over-ride";
        }
        $scope.showtable  = false;
        $scope.showToYear  = false;
        var yearsSelected = [];

        $scope.addCommissionDetails = function(addCommissionForm) {
            if($scope.addCommission.policyYearExpressed=='RANGE'){
                if(_.contains(yearsSelected,$scope.addCommission.toYear)){
                    $scope.yearErrorStatus = 'TO_YEAR';
                    return;
                }
                if(_.contains(yearsSelected,$scope.addCommission.fromYear)){
                    $scope.yearErrorStatus = 'FROM_YEAR';
                    return;
                }
                for(var fromYear=$scope.addCommission.fromYear;fromYear<=$scope.addCommission.toYear;fromYear++){
                    yearsSelected.push(fromYear);
                }
                $scope.createCommission.commissionTermSet.push($scope.addCommission);
            }else{
                if(_.contains(yearsSelected,$scope.addCommission.fromYear)){
                    $scope.yearErrorStatus = 'FROM_YEAR';
                    return;
                }
                yearsSelected.push($scope.addCommission.fromYear);
                $scope.createCommission.commissionTermSet.push($scope.addCommission);
            }
            console.log($scope.createCommission)
            $scope.showtable  = true;
            $scope.yearErrorStatus = null;
            $scope.addCommission = {};
            resetForm(addCommissionForm);
        };

        var resetForm = function(form){
            angular.forEach(form,function(value,key){
                if(key.indexOf("$")==-1){
                    value.$setUntouched();
                    value.$setPristine();
                }
            })
        };

        $scope.saveCommission = function(){
            $scope.createCommission.fromDate = formatJSDateToDDMMYYYY($scope.fromDate);
            console.log($scope.createCommission);
        };
        $scope.fromDatePickerSettings = {
            isOpened:false,
            dateOptions:{
                formatYear:'yyyy' ,
                startingDay:1
            }
        };

        $scope.open = function($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.fromDatePickerSettings.isOpened = true;
        };


    }]);
