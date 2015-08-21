/*
 define(["angular"],
 function(){
 angular.module("myApp",[])
 .controller('viewBrokerCtrl',function($scope){

 })
 });*/
var viewBrokerModule = (function () {
    var agentServices = {};
    agentServices.agentSelected = undefined;
    agentServices.getTheItemSelected = function (ele) {
        this.agentSelected = $(ele).val();
        $("#agent-update").prop("disabled", "");
        $("#agent-view").prop("disabled", "");
    };
    agentServices.createBroker = function () {
        window.location.href = "new";
    };

    agentServices.viewBroker = function () {
        window.location.href = "/pla/core/broker/viewbroker?brokerId=" + this.agentSelected;
    };

    agentServices.updateBroker = function () {
        if (this.agentSelected) {
            window.location.href = "/pla/core/agent/edit?brokerId=" + this.agentSelected;
        }
    };
    agentServices.reload = function () {
        window.location.reload();
    };

    return agentServices;
})();
