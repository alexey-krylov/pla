define(['angular'],function(angular){
    angular.module('directives',[])
        .directive('fueluxWizard',function(){
            alert('fueluxWizard init invoked');
            /*use this directive to initialize the fuelUx wizard
            *
            * example: <div fuelux-wizard selected-item="[step]">
            * selected-item is used to initialize the current step the wizard is in.
             * By changing the scope value of the [step], a watch triggers and is taken to the respective step.
            */
            return{
                restrict:'A',
                scope:{
                    selectedItem:'='
                },
                link:function(scope,element,attr){
                    scope.$watch('selectedItem',function(newVal,oldVal){
                        element.wizard('selectedItem', {
                            step: newVal
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
});