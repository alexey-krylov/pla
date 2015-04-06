define(['moment'],function(moment){
    angular.module('commonServices',[])
        .factory('formatJSDateToYYYYMMDD',function(){
            return function(date){
               return moment(date).format('YYYY-MM-DD');
            }
        })
        .factory()
        .factory('getQueryParameter',['$window',function($window){
            return function(name){
                name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
                var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
                    results = regex.exec($window.location.search);
                return results === null ? null : decodeURIComponent(results[1].replace(/\+/g, " "));
            }
        }])
});