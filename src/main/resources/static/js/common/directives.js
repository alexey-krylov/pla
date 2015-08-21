angular.module('directives', ['mgcrea.ngStrap.alert'])
    .directive('nthTleafBinder', [function () {
        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, element, attr, ngModel) {
                if (!ngModel) return;
                var content = attr.nthTleafBinder;
                ngModel.$render = function () {
                    element.val(content);
                    ngModel.$setViewValue(content);
                };
            }
        }
    }])
    .directive('fueluxWizard', ['$timeout', function ($timeout) {
        /*use this directive to initialize the fuelUx wizard
         *
         * example: <div fuelux-wizard selected-item="[step]">
         * selected-item is used to initialize the current step the wizard is in.
         * By changing the scope value of the [step], a watch triggers and is taken to the respective step.
         */
        return {
            restrict: 'AEC',
            require: ['?^form'],
            scope: {
                selectedItem: '=?',
                skipValidation: '=?',
                removeSteps: '=?'
            },
            link: function (scope, element, attr, ctrl) {
                scope.$watch('selectedItem', function (newVal, oldVal) {
                    $(element).wizard('selectedItem', {
                        step: scope.selectedItem
                    });
                });

                scope.$watch('removeSteps', function (newVal, oldVal) {
                    if (newVal) {
                        $(element).wizard('removeSteps', newVal.index, newVal.howMany)
                    }
                });
                if (scope.selectedItem) {
                    $(element).wizard('selectedItem', {
                        step: scope.selectedItem
                    });
                } else {
                    $(element).wizard();
                }

                $(element).on('actionclicked.fu.wizard', function (event, data) {
                    if (data.direction == 'previous')return;
                    if (ctrl && ctrl[0]) {
                        var currentStep = ctrl[0]['step' + data.step] && ctrl[0]['step' + data.step].$name;
                        if (angular.isDefined(currentStep) && $.inArray(currentStep, scope.skipValidation) == -1) {
                            var stepForm = ctrl[0]['step' + data.step];
                            validateStep(stepForm);
                            if (stepForm.$invalid) {
                                event.preventDefault();
                            }
                        }
                    }
                    scope.$emit('actionclicked.fu.wizard', event, data);
                });
                $(element).on('changed.fu.wizard', function (event, data) {
                    scope.selectedItem = data.step;
                    scope.$emit('changed.fu.wizard', event, data);
                    $timeout(function () {
                        scope.$apply();
                    });
                });
                $(element).on('stepclicked.fu.wizard', function (event, data) {
                    scope.$emit('stepclicked.fu.wizard', event, data);
                });

                $(element).on('finished.fu.wizard', function (event) {
                    scope.$emit('finished.fu.wizard', event);
                });

                this.validateStep = function (stepForm) {
                    for (var propertyName in stepForm) {
                        if (propertyName.charAt(0) != '$') {
                            if (stepForm[propertyName].$invalid) {
                                stepForm[propertyName].$setDirty(true);
                            }
                        }
                    }
                    scope.$apply();
                }

            }
        }
    }])
    .directive('datatable', function () {
        return {
            restrict: 'EA',
            link: function (scope, iElement, attrs) {
                $(iElement).dataTable();
            }
        }
    })
    .directive('disableAllFields', function () {
        return {
            restrict: 'A',
            scope: {
                disableAllFields: "=?"
            },
            link: function (scope, controls) {
                scope.$watch('disableAllFields', function (newVal, oldVal) {
                    $(controls).find(":input").not(":button").prop("disabled", newVal);
                });
            }
        }
    })
    .directive('nthAlert', ['$rootScope', '$alert', function ($rootScope, $alert) {
        return {
            restrict: 'E',
            link: function () {
                $rootScope.$on('httpInterceptorAlert', function (event, args) {
                    $alert({title: args.message, placement: 'top-right', type: args.type, show: true, duration: 6});
                })
            }
        }
    }]);
