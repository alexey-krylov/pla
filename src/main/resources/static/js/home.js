/**
 * Created by Mohan Sharma on 2/12/2016.
 */
var  app = angular.module('homeModule', ['common', 'ngRoute','ngMessages', 'mgcrea.ngStrap.select', 'mgcrea.ngStrap.alert', 'mgcrea.ngStrap.popover',
        'directives', 'mgcrea.ngStrap.dropdown', 'ngSanitize', 'commonServices','ui.bootstrap.modal'])
    .config(['datepickerPopupConfig', function (datepickerPopupConfig) {
        datepickerPopupConfig.datepickerPopup = 'MM/dd/yyyy';
        datepickerPopupConfig.currentText = 'Today';
        datepickerPopupConfig.clearText = 'Clear';
        datepickerPopupConfig.closeText = 'Done';
        datepickerPopupConfig.closeOnDateSelection = true;
    }])
    .controller('NotificationPortletCtrl', ['$scope', '$http', '$window', function ($scope, $http, $window) {

    $http.get('/pla/core/notification/getnotification').success(function(data){
        $scope.notificationList=data;
    });
    $http.get('/pla/core/notification/getnotificationhistory').success(function(data){
        $scope.notificationHistoryList=data;
    });
    $scope.openEmailWindow = function (notificationId) {
        // console.log('invoke email...');
        window.open('/pla/core/notification/openemailnotification/'+notificationId,"_blank","toolbar=no,resizable=no," +
            "scrollable=no,menubar=no,personalbar=no,dependent=yes,dialog=yes,split=no,titlebar=no,resizable=no,location=no,left=100px");
    };
    $scope.openPrintWindow = function(notificationId) {
        //  window.location.href = '/pla/core/notification/printnotification/' + notificationId;
        window.open('/pla/core/notification/openprintnotification/'+notificationId,"_blank","toolbar=no,resizable=no," +
            "scrollable=no,menubar=no,personalbar=no,dependent=yes,dialog=yes,split=no,titlebar=no,resizable=no,location=no,left=100px");
    }
    $scope.openEmailReadOnlyWindow = function (notificationHistoryId) {
        console.log('invoke email...'+notificationHistoryId);
        window.open('/pla/core/notification/openemailnotificationhistory/'+notificationHistoryId,"_blank","toolbar=no,resizable=no," +
            "scrollable=no,menubar=no,personalbar=no,dependent=yes,dialog=yes,split=no,titlebar=no,resizable=no,location=no,left=100px");
    };
    $scope.openPrintReadOnlyWindow = function(notificationHistoryId){
        // window.location.href='/pla/core/notification/printnotificationhistory/'+notificationHistoryId;
        window.open('/pla/core/notification/openprintnotificationhistory/'+notificationHistoryId,"_blank","toolbar=no,resizable=no," +
            "scrollable=no,menubar=no,personalbar=no,dependent=yes,dialog=yes,split=no,titlebar=no,resizable=no,location=no,left=100px");
    }
}]);