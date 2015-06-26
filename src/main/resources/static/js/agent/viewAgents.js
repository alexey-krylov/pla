/*
 define(["angular"],
 function(){
 angular.module("myApp",[])
 .controller('viewAgentCtrl',function($scope){

 })
 });*/
var viewAgentModule = (function(){
    var agentServices = {};
    agentServices.agentSelected = undefined;
    agentServices.getTheItemSelected = function(ele){
        this.agentSelected=$(ele).val();
        $("#agent-update").prop("disabled","");
        $("#agent-view").prop("disabled","");
    };
    agentServices.createAgent = function(){
        window.location.href = "opencreatepage";
    };

    agentServices.createBroker = function () {
        window.location.href = "/pla/core/broker/new";
    };

    agentServices.viewAgent =  function(){
        window.location.href = "/pla/core/agent/viewagentdetail?agentId="+this.agentSelected;
    };
    agentServices.viewBroker = function () {
        window.location.href = "/pla/core/broker/viewbroker?agentId=" + this.agentSelected;
    };

    agentServices.updateAgent = function(){
        if(this.agentSelected){
            window.location.href = "/pla/core/agent/openeditpage?agentId="+this.agentSelected;
        }
    };
    agentServices.reload = function(){
        window.location.reload();
    };


    return agentServices;
})();
