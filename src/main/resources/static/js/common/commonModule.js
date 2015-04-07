define(['angular','angular-loading-bar','directives','angular-animate','ui-bootstrap-tpls'],function(angular){
    angular.module('common',['directives','angular-loading-bar','ngAnimate','ui.bootstrap'])
        .constant('globalConstants',{
            numberPattern : /^\d+$/
        })
        .factory('nthHttpInterceptor',['$rootScope',function($rootScope){
            return {
                'response': function(response) {
                    if(response.config.method=="POST" && response.status==200){
                        $rootScope.$broadcast('httpInterceptorAlert',{
                            message:response.data.message
                        });
                    }
                    return response;
                }
            }
        }])
        .config(['$httpProvider',function($httpProvider){
            $httpProvider.interceptors.push('nthHttpInterceptor');
        }])
});