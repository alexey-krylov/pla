angular.module('directives', [])
    .directive('nthTleafBinder', ['$timeout', function ($timeout) {
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
    .directive('fueluxWizard', function ($timeout) {
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
                selectedItem: '=',
                skipValidation:'='
            },
            link: function (scope, element, attr, ctrl) {
                scope.$watch('selectedItem',function(newVal,oldVal){
                    element.wizard('selectedItem', {
                        step: newVal
                    });
                });

                $(element).wizard();
                $(element).on('actionclicked.fu.wizard', function (event, data) {
                    if (data.direction == 'previous')return;
                    if(ctrl && ctrl[0]){
                        var currentStep =ctrl[0]['step' + data.step].$name;
                        if($.inArray(currentStep,scope.skipValidation)==-1){
                            var stepForm = ctrl[0]['step' + data.step];
                            validateStep(stepForm);
                            if (stepForm.$invalid) {
                                event.preventDefault();
                            }
                        }
                    }
                    scope.$emit('actionclicked.fu.wizard', event, data);
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
    })
    .directive('datatable', function () {
        return {
            restrict: 'EA',
            link: function (scope, iElement, attrs) {
                iElement.dataTable();
            }
        }
    });
   