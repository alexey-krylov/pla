angular.module('createCommission', ['common','commonServices','ngRoute'])

    .controller('commissionController',['$scope','formatJSDateToDDMMYYYY','$http','$window','commissionDetails','getQueryParameter','addDays','globalConstants',
        function($scope,formatJSDateToDDMMYYYY,$http,$window,commissionDetails,getQueryParameter,addDays,globalConstants){
            var mode= null;
            $scope.isSaving = false;
            $scope.showtable  = false;
            $scope.showToYear  = false;
            $scope.viewType = "create";
            var yearsSelected = [];
            if($window.location.href.indexOf("Normal")!=-1){
                mode = "NORMAL";
            }else{
                mode = "OVERRIDE";
            }

            $scope.numberPatternWithDecimal= globalConstants.numberPatternWithDecimal;

            var viewType = getQueryParameter('type');

            if(viewType == 'view'){
                $scope.viewType = "view";
                $scope.showtable = true;
            }else if(viewType == 'update'){
                $scope.viewType = "update";
                $scope.showtable = true;
            }

            $scope.isSaved =  false;
            $scope.commissionDetails = {
                commissionTermSet:[],
                commissionType:mode
            };
            if(!_.isEmpty(commissionDetails)){
                angular.extend($scope.commissionDetails,commissionDetails);
                $scope.fromDate = commissionDetails.fromDate;
                angular.forEach($scope.commissionDetails.commissionTermSet,function(value,key){
                   if(value.commissionTermType == 'RANGE'){
                       for(var fromYear=value.startYear;fromYear<=value.endYear;fromYear++){
                           yearsSelected.push(fromYear);
                       }
                   }else{
                       yearsSelected.push(value.startYear);
                   }
                });
            }
            if(mode=='OVERRIDE'){
                $scope.override = "Over-ride";
            }

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
                    $scope.commissionDetails.commissionTermSet.push($scope.addCommission);

                }else{
                    if($scope.addCommission && $scope.addCommission.endYear){
                        delete $scope.addCommission.endYear;
                    }
                    if(_.contains(yearsSelected,$scope.addCommission.startYear)){
                        $scope.yearErrorStatus = 'FROM_YEAR';
                        return;
                    }
                    yearsSelected.push($scope.addCommission.startYear);
                    $scope.commissionDetails.commissionTermSet.push($scope.addCommission);
                }
                sort();
                $scope.showtable  = true;
                $scope.yearErrorStatus = null;
                $scope.addCommission = {};
                resetForm(addCommissionForm);
            };

            var sort =  function(){
                $scope.commissionDetails.commissionTermSet = _.sortBy($scope.commissionDetails.commissionTermSet,'startYear');
            };

            var removeYearsSelected=function(selectedRow){
                if(selectedRow.commissionTermType=='RANGE'){
                    for(var fromYear=selectedRow.startYear;fromYear<=selectedRow.endYear;fromYear++){
                        yearsSelected.splice(yearsSelected.indexOf(fromYear));
                    }
                }else{
                    yearsSelected.splice(yearsSelected.indexOf(selectedRow.startYear));
                }
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
                removeYearsSelected($scope.commissionDetails.commissionTermSet[index]);
                $scope.commissionDetails.commissionTermSet.splice(index,1);
            };

            $scope.saveCommission = function(){
                $scope.commissionDetails.fromDate = formatJSDateToDDMMYYYY($scope.fromDate);
                $http.post("/pla/core/commission/create", $scope.commissionDetails)
                    .success(function(data,status){
                        if(data.status=="200"){
                            $scope.isSaving = true;
                            $scope.isSaved=true;
                        }
                    })
            };

            $scope.updateCommission = function(){
                var commissionDetailsToUpdate = angular.copy($scope.commissionDetails);
                commissionDetailsToUpdate.CommissionId = getQueryParameter('commissionId');
                delete commissionDetailsToUpdate.availableFor;
                delete commissionDetailsToUpdate.commissionType;
                $http.post("/pla/core/commission/update", $scope.commissionDetails)
                    .success(function(data,status){
                        if(data.status=="200"){
                            $scope.isSaving = true;
                            $scope.isSaved=true;
                        }
                    })
            };
            $scope.fromDatePickerSettings = {
                tomorrow : addDays(new Date(),1),
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


            $scope.back = function(){
                $window.location.href = "/pla/core/commission/list/"+mode;
            }

        }])
    .filter('camelCase', function() {
        return function(input) {
            return input.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
        }
    })
    .config(["$routeProvider",function($routeProvider){
        var commissionType = null;
        if(window.location.href.indexOf("Override")!=-1){
            commissionType = "Override"
        }else{
            commissionType = "Normal"
        }
        $routeProvider.when('/', {
            templateUrl: '/pla/core/commission/commissionPage/'+commissionType,
            controller: 'commissionController',
            resolve: {
                commissionDetails:['$q','$http','getQueryParameter',function($q,$http,getQueryParameter){
                    var typeQueryParam = getQueryParameter('type');
                    if(typeQueryParam){
                        var commissionId = getQueryParameter('commissionId');
                        var deferred = $q.defer();
                        $http.get('/pla/core/commission/getcommissiondetail/'+commissionId).success(function (response, status, headers, config) {
                            response.commissionTermSet = _.sortBy(response.commissionTermSet,'startYear')
                            deferred.resolve(response)
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        return deferred.promise;
                    }else{
                        return {}
                    }
                }]
            }
        })
    }]);
