angular.module('createProposal',['common','ngRoute'])
    .controller('createProposalCtrl',['$scope','resources','$bsmodal','globalConstants',function($scope,resources,$bsmodal,globalConstants){
        $scope.titles = globalConstants.title;
        $scope.part={
            isPart:true
        };
        $scope.isProposed = null;
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
                $scope.isAgentEmpty = false;
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
        };

        $scope.hasAccordionError =  function(form){
            return $scope.createProposal.step1.$submitted  && form.$invalid;
        };

        function isFormValidated(){
            if(_.isEmpty($scope.agentDetails)){
                $scope.isAgentEmpty = true;
                return false;
            }

            if($scope.step1.isProposed.$invalid){
                return false;
            }
        }

        function setSubFormSubmitted(){
            $scope.createProposal.step1.$setSubmitted();
            $scope.createProposal.step1.proposedAssuredDetails.$setPristine();
            $scope.createProposal.step1.spouseDetails.$setPristine();
            $scope.createProposal.step1.employmentDetails.$setPristine();
            $scope.createProposal.step1.residentDetails.$setPristine();
        }

        $scope.savePrposedAssuredDetails = function(){
            setSubFormSubmitted();
            if(isFormValidated()){

            }
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
            templateUrl: '/individualLife/proposal/createProposalForm',
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
        agentModal:"/pla/proposal/individualLife/getPage/agentDetailModal",
        proposedAssuredUrl:"/pla/proposal/individualLife/getPage/proposedAssuredDetails",
        proposerDetails:"/pla/proposal/individualLife/getPage/proposerDetails",
        planDetails:"/pla/proposal/individualLife/getPage/planDetails",
        beneficiaryModal:"/pla/proposal/individualLife/getPage/beneficiaryDetailModal",
        generalDetails:"/pla/proposal/individualLife/getPage/generalDetails",
        compulsoryHealthDetailsPart1:"/pla/proposal/individualLife/getPage/compulsoryHealthDetailsPart1",
        compulsoryHealthDetailsPart2:"/pla/proposal/individualLife/getPage/compulsoryHealthDetailsPart2",
        familyHabitAndBuild:"/pla/proposal/individualLife/getPage/familyHabitAndBuild",
        additionalDetail:"/pla/proposal/individualLife/getPage/additionalDetail"
    })
    .filter('getTrustedUrl',['$sce',function($sce){
        return function(url){
            return $sce.getTrustedResourceUrl(url);
        }
    }]);

