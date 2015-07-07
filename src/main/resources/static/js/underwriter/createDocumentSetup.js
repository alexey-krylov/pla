var App = angular.module('createDocumentSetup', ['common','commonServices','ngRoute', 'ui.bootstrap', 'ngSanitize','mgcrea.ngStrap.select','mgcrea.ngStrap.alert']);

App.directive('popover', function($compile){
    return {
        restrict : 'A',
        link : function(scope, elem){
            var content = $(".popover-content").html();
            var compileContent = scope.col.underWriterDocuments;
            var content="<ul>";
            for (var i=0; i<compileContent.length; i++ ){

                content=content + "<li>" + compileContent[i].documentName +"</li>";
            }
            content=content +"</ul>";
            var title = $(".popover-head").html();
            var options = {
                content: content,
                html: true,
                title: title
            };

            $(elem).popover(options);
        }
    }
});

App.controller('CreateDocumentSetupController', ['$scope', '$http','$alert','$window', function ($scope, $http,$alert,$window) {

    $scope.showOptionalCoverageValue=true;
    $scope.newPlanList = [];
    $scope.coverageList = [];
    $scope.optionalCoverageList = [];
    $scope.mandatoryDocument = {
        documents:[]
    };
   // $scope.showOptionalCoverage = false;
    $scope.selectedDate = moment().add(1, 'days').format("YYYY-MM-DD");
    $scope.newDateField = {};
    $scope.selectedUsers = [];
    $scope.showtable  = false;
    $scope.datePickerSettings = {
        isOpened: false,
        dateOptions: {
            formatYear: 'yyyy',
            startingDay: 1
        }
    };
  $scope.createDocumentLevel=createDocumentLevel;
    $scope.open = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.datePickerSettings.isOpened = true;
    };

   /* $scope.getDefinedOption = function () {

        if ($scope.createDocumentLevel.definedFor == "plan") {
            $scope.showOptionalCoverage = false;
        } else {
            $scope.showOptionalCoverage = true;
        }
    }*/
    $scope.$watch('createDocumentLevel.definedFor',function(newValue, oldValue){
        if(newValue=='optionalCoverage'){
            $scope.createDocumentLevel.planCode="";
            $scope.createDocumentLevel.coverageId="";
            $scope.showOptionalCoverageValue = false;
        }
        if (newValue == 'plan'){
            $scope.createDocumentLevel.planCode="";
            $scope.showOptionalCoverageValue = true;

        }

    });

   /* $scope.$watch('createDocumentLevel.definedFor',function(newValue, oldValue){
        if(newValue=='optionalCoverage'){
            $scope.createDocumentLevel.planCode="";
            $scope.showOptionalCoverageValue = false;

        }
    });*/

    $scope.$watch('createDocumentLevel.coverageId',function(newValue, oldValue){
        if(newValue){
            $scope.showOptionalCoverageValue=true;
        }
    });

    $http.get('/pla/underwriter/getdocumentapprovedbyprovider').success(function(data){
        $scope.documentList=data;
    });

    $scope.createDocumentLevel.underWriterDocumentItems=[];

    var arrList=[];
    $scope.fieldData=[];
    $scope.dataList=[];
    $scope.successAlert={};
    $scope.addDocumentDetails = function(addDocumentForm){
        $scope.fieldData=[];
        for (var i = 0; i < $scope.mandatoryDocument.documents.length; i++) {
            $scope.fieldData.push(_.findWhere($scope.documentList,{documentCode: $scope.mandatoryDocument.documents[i]}));
        }
        if($scope.headerDataList) {
            arrList=[];
            for (var j = 0; j < $scope.headerDataList.length; j++) {

                var underWriterLineItem = {
                    "underWriterInfluencingFactor": $scope.headerDataList[j].name,
                    "influencingItem": $scope.headerDataList[j].value

                };
                arrList.push(underWriterLineItem);
            }
            $scope.dataList.push({"underWriterDocumentLineItem":arrList,"underWriterDocuments":$scope.fieldData});
            $http.post('/pla/underwriter/checkforoverlapping',angular.extend(
               {underWriterDocumentItems:$scope.dataList},
               {"underWriterInfluencingFactors": $scope.createDocumentLevel.underWriterInfluencingFactors})).success(function(data){
                if(data.status=="200"){
                    $scope.createDocumentLevel.underWriterDocumentItems=$scope.dataList;
                   // console.log($scope.createDocumentLevel.underWriterDocumentItems);
                    $scope.showtable  = true;
                    $scope.alert={content: data.message, type: 'success', show: true};
                }
               else if(data.status=="500"){
                   var index=$scope.dataList.length - 1;
                   $scope.dataList.splice(index,1);
                   $scope.createDocumentLevel.underWriterDocumentItems=$scope.dataList;
                   $scope.alert={content: data.message, type: 'danger', show: true};
               }
           });
        }
         $http.get('/pla/underwriter/getinfluencingfactorrange/'+ createDocumentLevel.underWriterInfluencingFactors.join(',')).success(function (data) {
            $scope.headerDataList=data;
            $scope.mandatoryDocument.documents=[];
         });
    }

    $scope.deleteCurrentRow = function(index){
        $scope.dataList.splice(index,1);
        $scope.createDocumentLevel.underWriterDocumentItems=$scope.dataList;
    }

    $http.get('/pla/underwriter/getplancoveragedetail').success(function (data) {
          $scope.newPlanList=data;

    });

    $scope.$watch('createDocumentLevel.planCode', function (newValue, oldValue) {
        if (newValue) {
           /* if($scope.createDocumentLevel.definedFor == 'optionalCoverage'){
                $scope.createDocumentLevel.coverageId="";
                $scope.showOptionalCoverageValue = false;
            }*/

            var planCode = $scope.createDocumentLevel.planCode;
            var planDetails= _.findWhere($scope.newPlanList, {planCode: planCode});
            $scope.createDocumentLevel.planName = planDetails.planName;
            $http.get('/pla/underwriter/getoptionalcoverage/'+ planDetails.planId).success(function (data) {
                if(data) {
                    if(data[0]) {
                        $scope.optionalCoverageList = data[0].coverageDtoList;
                    }else{
                        if($scope.createDocumentLevel.definedFor == 'optionalCoverage'){
                            $scope.optionalCoverageList ="";
                            $scope.createDocumentLevel.coverageId="";
                            $scope.showOptionalCoverageValue = false;
                        }

                    }
                }


            });
        }
    });

    $http.get('/pla/underwriter/getunderwriterprocess').success(function (data) {
        $scope.processList=data;
    });
    var multiSelectList=[];
    $scope.$watch('createDocumentLevel.processType',function(newValue, oldValue) {
        if(newValue) {

            $http.get('/pla/underwriter/getunderwriterinfluencingfactor/'+ newValue).success(function (data) {
                multiSelectList=[];
                $scope.mulSelect = data;
               for(var i=0;i< $scope.mulSelect.length;i++){
                   multiSelectList.push($scope.mulSelect[i].influencingFactor);
               }
              $scope.createDocumentLevel.underWriterInfluencingFactors=multiSelectList;
           });
        }
    });
    $scope.$watch('createDocumentLevel.underWriterInfluencingFactors',function(newValue, oldValue) {
        if(newValue) {
            $http.get('/pla/underwriter/getinfluencingfactorrange/'+ newValue.join(',')).success(function (data) {
                $scope.headerDataList=data;

          });
            $scope.createDocumentLevel.underWriterDocumentItems=[];
            var arrList=[];
            $scope.dataList=[];
            $scope.mandatoryDocument.documents=[];
        }
    });

    $scope.$watch('createDocumentLevel.effectiveFrom', function (newValue, oldValue) {
        if (newValue) {
            if (!moment($scope.createDocumentLevel.effectiveFrom, 'YYYY-MM-DD').isValid()) {
                $scope.newDateField.fromDate = moment($scope.createDocumentLevel.effectiveFrom).format("YYYY-MM-DD");
                $scope.createDocumentLevel.effectiveFrom = $scope.newDateField.fromDate;
            }
        }
    });

    $scope.submitDocumentSetUp = function(){
        $scope.createDocumentLevel.underWriterDocumentItems=$scope.dataList;
        $http.post('create/underwriterdocument',$scope.createDocumentLevel).success(function (data) {
            console.log(data);
            if(data.status=="200"){

                $scope.alert={content: data.message, type: 'success', show: true};
                $window.location.href="/pla/underwriter/viewdocumentsetup";
            }
            else if(data.status=="500"){

                $scope.alert={content: data.message, type: 'danger', show: true};
            }

        });
    }

}]);
