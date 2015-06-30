define(['angular','angular-loading-bar','directives','angular-animate','ui-bootstrap-tpls'],function(angular){
    angular.module('common',['directives','angular-loading-bar','ngAnimate','ui.bootstrap'])
        .constant('globalConstants',{
            numberPattern : /^\d+$/,
            numberPatternWithDecimal : /(^\d{1,2}$|^\d{1,2}\.\d{1,2}$)/,
            title:["Mr.","Mrs.","Miss","Dr.","Prof.","Hon.","Ms.","Rev.","Pst."],
            gender:[{code:'MALE',description:"Male"},{code:'FEMALE',description:'Female'}]
        })
        .factory('nthHttpInterceptor',['$rootScope',function($rootScope){
            return {
                'response': function(response) {
                    if(response.config.method=="POST" && response.status==200){
                        if ((response && response.status == "500") ||
                            (response.data && response.data.status == "500")) {
                            $rootScope.$broadcast('httpInterceptorAlert',{
                                message:response.data.message,
                                type:"danger"
                            });
                        }else{
                            $rootScope.$broadcast('httpInterceptorAlert',{
                                message:response.data.message,
                                type:"success"
                            });
                        }
                    }
                    return response;
                }
            }
        }])
        .config(['$httpProvider',function($httpProvider){
            $httpProvider.interceptors.push('nthHttpInterceptor');
        }]);
});
