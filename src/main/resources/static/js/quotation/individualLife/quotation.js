/**
 * Created by pradyumna on 26-05-2015.
 */
angular.module('individualQuotation', ['common', 'ngTagsInput', 'checklist-model', 'ngRoute'])
    .config(function ($routeProvider, $locationProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'quotationlist'
            })
    });
