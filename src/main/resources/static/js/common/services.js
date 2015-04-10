define(['moment'],function(moment){
    angular.module('commonServices',[])
        .factory('formatJSDateToDDMMYYYY',function(){
            return function(date){
               return moment(date).format('DD/MM/YYYY');
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