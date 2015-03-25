angular.module('directives',[])
    .directive('fueluxWizard',function($timeout){
        /*use this directive to initialize the fuelUx wizard
         *
         * example: <div fuelux-wizard selected-item="[step]">
         * selected-item is used to initialize the current step the wizard is in.
         * By changing the scope value of the [step], a watch triggers and is taken to the respective step.
         */
        return{
            restrict:'AEC',
            scope:{
                selectedItem:'='
            },
            link:function(scope,element,attr){
                scope.$watch('selectedItem',function(newVal,oldVal){
                    element.wizard('selectedItem', {
                        step: newVal
                    });
                });
                element.on('changed.fu.wizard',function(event,data){
                    $timeout(function() {
                        scope.selectedItem=data.step;
                    });
                });
                var wizardOptions={
                    disablePreviousStep:false,
                    selectedItem:{step:scope.selectedItem}
                };
                element.wizard(wizardOptions);
            }
        }
    })