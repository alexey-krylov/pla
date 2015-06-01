/**
 * Created by pradyumna on 26-05-2015.
 */
angular.module('individualQuotation', ['common', 'ngRoute', 'commonServices', 'ngMessages', 'angucomplete-alt'])
    .config(['datepickerPopupConfig', function (datepickerPopupConfig) {
        datepickerPopupConfig.datepickerPopup = 'dd/MM/yyyy';
        datepickerPopupConfig.currentText = 'Today';
        datepickerPopupConfig.clearText = 'Clear';
        datepickerPopupConfig.closeText = 'Done';
        datepickerPopupConfig.closeOnDateSelection = true;
    }])
    .config(function ($routeProvider, $locationProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'quotation/searchForm'
            })
            .when('/new', {
                templateUrl: 'quotation/new',
                controller: 'QuotationController',
                controllerAs: 'ctrl',
                resolve: {
                    occupations: ['$q', '$http', function ($q, $http) {
                        var deferred = $q.defer();
                        $http.get('/pla/individuallife/proposal/getAllOccupation').success(function (response, status, headers, config) {
                            deferred.resolve(response);
                        }).error(function (response, status, headers, config) {
                            deferred.reject();
                        });
                        return deferred.promise;
                        ;
                    }]
                }
            })
        $routeProvider.otherwise({
            templateUrl: 'quotation/searchForm'
        });
    })
    .controller('QuotationController', ['$scope', '$http', '$bsmodal',
        'globalConstants', 'occupations',
        function ($scope, $http, $bsmodal, globalConstants, occupations) {
            $scope.titleList = globalConstants.title;
            $scope.occupations = occupations;
            $scope.quotation = {};
            $scope.selectedItem = 1;
            $scope.onlyNumbers = /^[0-9]+$/;

            $scope.$watch('selectedAgent', function (newval, oldval) {
                console.log('selectedAgent' + JSON.stringify(newval));
                if (newval) {
                    $scope.agent = newval.description;
                    $scope.quotation.agentId = $scope.agent["agent_id"];
                }
            });

            $scope.$watch('selectedPlan', function (newval, oldval) {
                console.log(JSON.stringify(newval));
                if (newval) {
                    $scope.quotation.planId = newval.description.plan_id;
                }
            });

            $scope.$watch('proposerSameAsProposedAssured', function (newval, oldval) {
                if (newval) {
                    $scope.proposer = $scope.proposedAssured;
                }
            });

            $scope.launchProposerDOB = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob2 = true;
            };

            $scope.launchProposedAssuredDOB = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.launchdob1 = true;
            };

            $scope.$watchGroup(['proposedAssured.dateOfBirth', 'proposer.dateOfBirth'], function (newval, oldval) {
                if (newval) {
                    if (newval[0] && typeof newval[0] == 'Date') {
                        $scope.proposedAssuredAge = moment().diff(new moment(newval[0].toDateString()), 'years');
                        console.log('$scope.proposedAssuredAge ' + $scope.proposedAssuredAge);
                    }
                    if (newval[1] && typeof newval[1] == 'Date') {
                        $scope.proposerAge = moment().diff(new moment(newval[1].toDateString()), 'years');
                    }
                }
            });

            $scope.saveStep1 = function () {
                $http.post('quotation/createquotation',
                    angular.extend($scope.proposedAssured, {
                        agentId: $scope.quotation.agentId,
                        planId: $scope.quotation.planId,
                        isAssuredTheProposer: false
                    }))
                    .success(function (data) {
                        $scope.quotation.quotationId = data.id;
                        $http.get('quotation/getquotationnumber/' + data.id).success(function (data) {
                            $scope.quotation.quotationNumber = data.id;
                        });
                    });
            };

            $scope.saveStep2 = function () {
                $scope.proposedAssured.dateOfBirth = new moment($scope.proposedAssured.dateOfBirth).format('DD/MM/YYYY');
                var request = {proposedAssured: $scope.proposedAssured};
                $http.post('quotation/updatewithassureddetail',
                    angular.extend(request, {
                        quotationId: $scope.quotation.quotationId,
                        isAssuredTheProposer: $scope.proposerSameAsProposedAssured
                    }))
                    .success(function (data) {
                    });
            };


            $scope.remoteUrlRequestFn = function (str) {
                console.log($scope.quotation.agentId);
                return {agentId: $scope.quotation.agentId};
            };
        }]
);

var viewQuotationModule = (function () {
    var services = {};
    services.selectedItem = "";
    services.status = '';
    services.getTheItemSelected = function (ele) {
        this.selectedItem = $(ele).val();
        this.status = $(ele).parent().find('input[type=hidden]').val();
        $(".btn-disabled").attr("disabled", false);
        if (this.status == 'GENERATED') {
            $('#emailaddress').attr('disabled', false);
            $('#print').attr('disabled', false);
        } else {
            $('#emailaddress').attr('disabled', true);
            $('#print').attr('disabled', true);
        }
    };

    services.reload = function () {
        window.location.reload();
    };

    services.printQuotation = function () {
        window.location.href = '/pla/individuallife/quotation/printquotation/' + this.selectedItem;
    }

    services.emailQuotation = function () {
        window.location.href = '/pla/individuallife/quotation/openemailquotation/' + this.selectedItem;
    }

    services.modifyQuotation = function () {
        var quotationId = this.selectedItem;
        $.ajax({
            url: '/pla/individuallife/quotation/getversionnumber/' + quotationId,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            success: function (msg) {
                if (msg.status == '200') {
                    window.location.href = "/pla/quotation/grouplife/creategrouplifequotation?quotationId=" + quotationId + "&version=" + msg.data + "&mode=edit";
                } else if (msg.status == '500') {
                }
            }
        });
    };

    services.viewQuotation = function () {
        var quotationId = this.selectedItem;
        $.ajax({
            url: '/pla/individuallife/quotation/getversionnumber/' + quotationId,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            success: function (msg) {
                if (msg.status == '200') {
                    window.location.href = "/pla/individuallife/quotation/creategrouplifequotation?quotationId=" + quotationId + "&version=" + msg.data + "&mode=view";
                } else if (msg.status == '500') {
                }
            }
        });
    };

    return services;
})();

/*function QuotationController(){
 this.selectedItem=5;
 this.proposedAssured={
 title:'MR'
 };
 }
 */
