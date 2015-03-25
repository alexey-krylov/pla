define(['moment'],function(moment){
    angular.module('commonServices',[])
        .factory('formatJSDateToMilliseconds',function(){
            return function(date){
               return moment(date).format('x');
            }
        })
});