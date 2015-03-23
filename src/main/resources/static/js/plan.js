/**
 * Created by pradyumna on 23-03-2015.
 */
requirejs.config({
    paths: {
        'jquery': 'jquery',
        'bootstrap': 'bootstrap.min',
        'fuelux': 'fuelux.min',
        'ngTagsInput': 'http://cdn.jsdelivr.net/webjars/ng-tags-input/2.1.1/ng-tags-input'
    },
    // Bootstrap is a "browser globals" script :-(
    shim: {'bootstrap': {deps: ['jquery', 'jquery-ui']}}

});
// Require all.js or include individual files as needed
require(['jquery', 'bootstrap', 'fuelux', 'jquery-ui', 'plan-app'], function ($) {
    $('#launchDate').datepicker({
        format: "dd/mm/yyyy",
        orientation: "top auto",
        clearBtn: true,
        autoclose: true
    });
    $('#withdrawalDate').datepicker({
        format: "dd/mm/yyyy",
        orientation: "top auto",
        clearBtn: true,
        autoclose: true
    });

    $('#planSetUpWizard').wizard();
    $('#accordion').accordion({
        heightStyle: "content"
    });
});
