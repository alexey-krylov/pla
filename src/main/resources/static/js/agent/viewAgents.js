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
    this.channelType = null;
    agentServices.getTheItemSelected = function(ele){
        this.agentSelected=$(ele).val();
        //$("#agent-update").prop("disabled","");
        //$("#agent-view").prop("disabled","");

        this.channelType = $(ele).parent().find('input[type=hidden]').val();
        //alert(this.channelType);

        if(this.channelType === 'DIRECT'){
            $("#agent-update").prop("disabled","false");
            $("#agent-view").prop("disabled","");
        }
        else{
            $("#agent-update").prop("disabled","");
            $("#agent-view").prop("disabled","");
        }
    };
    agentServices.createAgent = function(){
        window.location.href = "opencreatepage";
    };

    agentServices.createBroker = function () {
        window.location.href = "/pla/core/broker/new";
    };

    agentServices.viewAgent =  function(){
        if (this.agentSelected) {
            if ('BROKER' === this.channelType) {
                window.location.href = "/pla/core/broker/view?agentId=" + this.agentSelected + "&mode=view";
            } else {
                window.location.href = "/pla/core/agent/viewagentdetail?agentId=" + this.agentSelected;

            }
        }
    };

    agentServices.updateAgent = function(){
        if(this.agentSelected){
            if ('BROKER' === this.channelType) {
                window.location.href = "/pla/core/broker/edit?agentId=" + this.agentSelected + "&mode=edit";
            } else {
                window.location.href = "/pla/core/agent/openeditpage?agentId=" + this.agentSelected;
            }
        }
    };
    agentServices.reload = function(){
        window.location.reload();
    };


    return agentServices;
})();
