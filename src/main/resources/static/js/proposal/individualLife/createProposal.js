angular.module('createProposal',['common','ngRoute'])
    .controller('createProposalCtrl',['$scope','resources','$bsmodal',function($scope,resources,$bsmodal){
        $scope.part={
            isPart:true
        };
        $scope.resources = resources;
        $scope.agentDetails = [];
        $scope.accordionStatus = {
            proposedAssuredDetails:{agents:true},
            proposerDetails:{proposedAssured:true},
            planDetails:{plan:true},
            generalDetails:{tab1:false},
            healthDetailsPart1:{tab1:false},
            healthDetailsPart2:{tab1:false}
        };

        $scope.openAgentModal = function(){
            var agentModalInstance = $bsmodal.open({
                templateUrl: resources.agentModal,
                controller: 'addAgentCtrl',
                backdrop:'static'
            });

            agentModalInstance.result.then(function (agent) {
                $scope.agentDetails.push(agent);
            });

        };

        $scope.openAccordion = function(status,tab){
            console.log(status);
            if(status==='YES'){
                $scope.accordionStatus.generalDetails[tab]=true;
            }else{
                $scope.accordionStatus.generalDetails[tab]=false;
            }
        };

        $scope.openModalWindow =  function(templateName){
            var modalInstance = $bsmodal.open({
                templateUrl: templateName,
                controller: 'modalCtrl',
                backdrop:'static'
            });
        };

        $scope.openBeneficiaryModal= function(){
            var beneficiaryModalInstance = $bsmodal.open({
                templateUrl: resources.beneficiaryModal,
                controller: 'addBeneficiaryCtrl',
                backdrop:'static'
            });
        }
    }])
    .controller('modalCtrl',['$scope','$modalInstance',function($scope, $modalInstance){
        $scope.addAgent = function () {
            $modalInstance.close([]);
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }])
    .controller('addAgentCtrl',['$scope','$modalInstance',function($scope, $modalInstance){
        $scope.addAgent = function (agent) {
            $modalInstance.close(agent);
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }])
    .controller('addBeneficiaryCtrl',['$scope','$modalInstance','globalConstants',function($scope, $modalInstance,globalConstants){
        $scope.titleList = globalConstants.title;
        $scope.genderList = globalConstants.gender;
        $scope.addBeneficiary = function (beneficiary) {
            $modalInstance.close(beneficiary);
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }])
    .config(["$routeProvider","$provide",function($routeProvider,$provide){
        $routeProvider.when('/', {
            templateUrl: '/pla/proposal/individualLife/createProposal.html',
            controller: 'createProposalCtrl',
            resolve: {

            }
        });
        /*$provide.decorator('accordionGroupDirective', ['$delegate', function($delegate) {
         var ngModel = $delegate[0];
         ngModel.templateUrl = 'accordionGroup.html';
         ngModel.$$isolateBindings.isPart = {
         attrName: 'isPart',
         mode: '=',
         optional: true
         };
         console.log(ngModel);
         return $delegate;
         }]);*/

    }])
    .constant('resources', {
        agentModal:"/pla/proposal/individualLife/agentDetailModal.html",
        proposedAssuredUrl:"/pla/proposal/individualLife/proposedAssuredDetails.html",
        proposerDetails:"/pla/proposal/individualLife/proposerDetails.html",
        planDetails:"/pla/proposal/individualLife/planDetails.html",
        beneficiaryModal:"/pla/proposal/individualLife/beneficiaryDetailModal.html",
        generalDetails:"/pla/proposal/individualLife/generalDetails.html",
        compulsoryHealthDetailsPart1:"/pla/proposal/individualLife/compulsoryHealthDetailsPart1.html",
        compulsoryHealthDetailsPart2:"/pla/proposal/individualLife/compulsoryHealthDetailsPart2.html",
        familyHabitAndBuild:"/pla/proposal/individualLife/familyHabitAndBuild.html",
        additionalDetail:"/pla/proposal/individualLife/additionalDetail.html"
    })
    .filter('getTrustedUrl',['$sce',function($sce){
        return function(url){
            return $sce.getTrustedResourceUrl(url);
        }
    }]);

