angular.module('createCommission', ['common','commonServices'])

    .controller('CreateCommissionController',['$scope','formatJSDateToDDMMYYYY','$http','$window',function($scope,formatJSDateToDDMMYYYY,$http,$window){
        var mode= null;
        if($window.location.href.indexOf("Normal")!=-1){
            mode = "NORMAL";
        }else{
            mode = "OVERRIDE";
        }
        $scope.isSaved =  false;
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
            if($scope.addCommission.commissionTermType=='RANGE'){
                if(_.contains(yearsSelected,$scope.addCommission.endYear)){
                    $scope.yearErrorStatus = 'TO_YEAR';
                    return;
                }
                if(_.contains(yearsSelected,$scope.addCommission.startYear)){
                    $scope.yearErrorStatus = 'FROM_YEAR';
                    return;
                }
                for(var fromYear=$scope.addCommission.startYear;fromYear<=$scope.addCommission.endYear;fromYear++){
                    yearsSelected.push(fromYear);
                }
                $scope.createCommission.commissionTermSet.push($scope.addCommission);
            }else{
                if($scope.addCommission && $scope.addCommission.endYear){
                    delete $scope.addCommission.endYear;
                }
                if(_.contains(yearsSelected,$scope.addCommission.startYear)){
                    $scope.yearErrorStatus = 'FROM_YEAR';
                    return;
                }
                yearsSelected.push($scope.addCommission.startYear);
                $scope.createCommission.commissionTermSet.push($scope.addCommission);
            }
            $scope.showtable  = true;
            $scope.yearErrorStatus = null;
            $scope.addCommission = {};
            resetForm(addCommissionForm);
        };

        var removeYearsSelected=function(selectedRow){
                if(selectedRow.commissionTermType=='RANGE'){
                    for(var fromYear=selectedRow.startYear;fromYear<=selectedRow.endYear;fromYear++){
                        yearsSelected.splice(yearsSelected.indexOf(fromYear));
                    }
                }else{
                    yearsSelected.splice(yearsSelected.indexOf(selectedRow.startYear));
                }
                $scope.createCommission.commissionTermSet.splice($scope.createCommission.commissionTermSet.indexOf(selectedRow));
        };

        var resetForm = function(form){
            angular.forEach(form,function(value,key){
                if(key.indexOf("$")==-1){
                    value.$setUntouched();
                    value.$setPristine();
                }
            })
        };

        $scope.deleteCurrentRow =  function(index){
            removeYearsSelected($scope.createCommission.commissionTermSet[index]);
        };

        $scope.saveCommission = function(){
            $scope.createCommission.fromDate = formatJSDateToDDMMYYYY($scope.fromDate);
            $http.post("/pla/core/commission/create", $scope.createCommission)
                .success(function(data,status){
                    if(data.status="200"){
                        $scope.isSaved=true;
                    }
                })
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


    }])
    .filter('camelCase', function() {
        return function(input) {
            return input.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
        }
    });
