var App = angular.module('updateDocumentSetup', ['common','commonServices','ngRoute', 'ui.bootstrap', 'ngSanitize','mgcrea.ngStrap.select','checklist-model']);
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

App.controller('UpdateDocumentSetupController', ['$scope', '$http','$window','$location','$alert', function ($scope, $http,$window,$location,$alert) {

    $scope.url = window.location.search.split('=')[1];
    $scope.dataList=[];
    $scope.finalList=[];
    var arrList=[];
    var multiSelectList=[];
    $scope.fieldData=[];
    $scope.successAlert={};
    $scope.newDateField = {};
    $scope.mandatoryDocument={documents:[]};
    $http.get('/pla/underwriter/getdoumentbyid/'+$scope.url).success(function(data){
        $scope.updateDocumentLevel= data;
        $scope.influencingTotalLength=$scope.updateDocumentLevel.underWriterDocumentItems.length;
        $scope.InfluencingFactorLength=$scope.updateDocumentLevel.underWriterInfluencingFactors.length;
        if($scope.updateDocumentLevel.coverageName){
            $scope.updateDocumentLevel.definedFor="optionalCoverage";
        }else{
            $scope.updateDocumentLevel.definedFor="plan";
        }
        var dataLists= data.underWriterDocumentItems;
        for (var i = 0; i < dataLists.length; i++) {
             var mandatoryDoc=[];
            for (var j = 0; j < dataLists[i].underWriterDocuments.length; j++) {
                mandatoryDoc[j] = dataLists[i].underWriterDocuments[j].documentCode;
            }
            $scope.dataList.push({"underWriterDocumentLineItem":dataLists[i].underWriterDocumentLineItem,"underWriterDocuments":mandatoryDoc});
            $scope.finalList.push({"underWriterDocumentLineItem":dataLists[i].underWriterDocumentLineItem,"underWriterDocuments":dataLists[i].underWriterDocuments});
        }
    });

    $http.get('/pla/underwriter/getdocumentapprovedbyprovider').success(function(data){
        $scope.documentList=data;
    });

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
            $scope.finalList.push({"underWriterDocumentLineItem":arrList,"underWriterDocuments":$scope.fieldData});
            $scope.dataList.push({"underWriterDocumentLineItem":arrList,"underWriterDocuments":$scope.mandatoryDocument.documents});

            $http.post('/pla/underwriter/checkforoverlapping',angular.extend(
                {underWriterDocumentItems:$scope.finalList},
                {"underWriterInfluencingFactors": $scope.updateDocumentLevel.underWriterInfluencingFactors})).success(function(data){
                if(data.status=="200"){
                    $scope.showtable  = true;
                    $scope.alert={content: data.message, type: 'success', show: true};
                }
                else if(data.status=="500"){
                    var index=$scope.finalList.length - 1;
                    $scope.finalList.splice(index,1);
                    $scope.dataList.splice(index,1);
                    $scope.alert={content: data.message, type: 'danger', show: true};
                }
            });
        }

        $http.get('/pla/underwriter/getinfluencingfactorrange/'+ $scope.updateDocumentLevel.underWriterInfluencingFactors.join(',')).success(function (data) {
            $scope.headerDataList=data;
            $scope.mandatoryDocument.documents=[];
        });
    }

    $scope.deleteCurrentRow = function(index){
        $scope.dataList.splice(index,1);
        $scope.finalList.splice(index,1);
    }
    $scope.$watch('updateDocumentLevel.processType',function(newValue, oldValue) {

        if(newValue) {
            $http.get('/pla/underwriter/getunderwriterinfluencingfactor/'+ newValue).success(function (data) {
                multiSelectList=[];
                $scope.mulSelect = data;
                for(var i=0;i< $scope.mulSelect.length;i++){
                    multiSelectList.push($scope.mulSelect[i].influencingFactor);
                }
            });
        }
    });
    $scope.$watch('updateDocumentLevel.underWriterInfluencingFactors',function(newValue, oldValue) {

        if(newValue) {

            $http.get('/pla/underwriter/getinfluencingfactorrange/'+ newValue.join(',')).success(function (data) {

                $scope.headerDataList=data;
            });

            if($scope.influencingTotalLength > $scope.updateDocumentLevel.underWriterDocumentItems.length || $scope.influencingTotalLength < $scope.updateDocumentLevel.underWriterDocumentItems.length){
                $scope.updateDocumentLevel.underWriterDocumentItems=[];
                var arrList=[];
                $scope.dataList=[];
                $scope.finalList=[];
                $scope.mandatoryDocument.documents=[];
            }
            else if($scope.InfluencingFactorLength > $scope.updateDocumentLevel.underWriterInfluencingFactors.length || $scope.InfluencingFactorLength < $scope.updateDocumentLevel.underWriterInfluencingFactors.length){
                $scope.updateDocumentLevel.underWriterDocumentItems=[];
                var arrList=[];
                $scope.dataList=[];
                $scope.finalList=[];
                $scope.mandatoryDocument.documents=[];
            }
      }
    });
    $scope.showSaveButton=true;
    $scope.checkForLength = function(){

        for(var i=0;i < $scope.dataList.length;i++){
            if($scope.dataList[i].underWriterDocuments.length <= 0){
                $scope.showSaveButton=false;
                i=$scope.dataList.length;
            }else{
                $scope.showSaveButton=true;
            }
        }

    }
    $scope.updateDocumentSetUp = function(){
        for(var i=0;i< $scope.dataList.length;i++){
            $scope.fieldData=[];
            for (var j = 0; j < $scope.dataList[i].underWriterDocuments.length; j++) {
                $scope.fieldData.push(_.findWhere($scope.documentList,{documentCode: $scope.dataList[i].underWriterDocuments[j]}));
            }
            $scope.finalList[i].underWriterDocuments=$scope.fieldData;
        }

        $scope.updateDocumentLevel.underWriterDocumentItems=$scope.finalList;
        $scope.newDateField.fromDate = moment($scope.updateDocumentLevel.effectiveFrom).format("YYYY-MM-DD");
        $scope.updateDocumentLevel.effectiveFrom = $scope.newDateField.fromDate;
        //console.log($scope.updateDocumentLevel);
      $http.post('create/underwriterdocument',$scope.updateDocumentLevel).success(function (data) {

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

App.controller('ViewDocumentSetupController', ['$scope', '$http','$window','$location', function ($scope, $http,$window,$location) {

        $scope.url = window.location.search.split('=')[1];
        $scope.dataList=[];
        $scope.finalList=[];
        var arrList=[];
        var multiSelectList=[];
        $scope.fieldData=[];
        $scope.successAlert={};
        $scope.newDateField = {};

        $http.get('/pla/underwriter/getdoumentbyid/'+$scope.url).success(function(data){

            $scope.viewDocumentLevel= data;
            $scope.fieldData=[];
            if($scope.viewDocumentLevel.coverageName){
                $scope.viewDocumentLevel.definedFor="optionalCoverage";
            }else{
                $scope.viewDocumentLevel.definedFor="plan";
            }
            $scope.dataList= data.underWriterDocumentItems;

            $http.get('/pla/underwriter/getinfluencingfactorrange/'+ $scope.viewDocumentLevel.underWriterInfluencingFactors.join(',')).success(function (data) {
                $scope.headerDataList=data;
            });
            $http.get('/pla/underwriter/getunderwriterinfluencingfactor/'+ $scope.viewDocumentLevel.processType).success(function (data) {

                $scope.mulSelect = data;
                for (var j = 0; j < $scope.viewDocumentLevel.underWriterInfluencingFactors.length; j++) {

                    $scope.fieldData.push(_.findWhere($scope.mulSelect,{influencingFactor: $scope.viewDocumentLevel.underWriterInfluencingFactors[j]}));
                }
                $scope.viewDocumentLevel.underWriterInfluencingFactors= $scope.fieldData;
            });


        });


}]);
