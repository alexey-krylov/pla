define(['jquery','bootstrap','ui-bootstrap-tpls','angular','angular-route','angular-strap','angular-strap-tpls','fuelux','../../common/directives','angular-file-upload-shim','angular-file-upload','angular-sanitize'],
    function($,bootstrap,uibootstrap,angular,route,strap,strapTpls,fuelux){
        angular.module('createQuotation',['ui.bootstrap','ngRoute','mgcrea.ngStrap.select','mgcrea.ngStrap.alert','mgcrea.ngStrap.popover','directives','angularFileUpload','mgcrea.ngStrap.dropdown','ngSanitize'])
            .controller('quotationCtrl',['$scope','$http','$timeout','$upload',
                function($scope,$http,$timeout,$upload){
                    $scope.step =3;
                    $("#quotationWizard").wizard();
                    $scope.fileSaved=null;
                    $scope.dropdown = [
                        {
                            "text": "<a><img src=\"../../../../static/images/file-extension-xls.png\">XLS</a>",
                            "href": "#"
                        },
                        {
                            "text": "<a><img src=\"../../../../static/images/file-extension-csv.png\">CSV</a>",
                            "href": "#"
                        }
                    ];
                    $scope.accordionStatus = {
                        contact:false,
                        proposer:true
                    };
                    $scope.$watch('fileSaved', function (n,o) {
                        if(n&& n.length){
                            $scope.fileName=n[0].name
                        }
                    });
                    $scope.openNewTab=function(event){
                        if(event && event.keyCode ==9){
                            $scope.accordionStatus.contact =true;
                            $scope.accordionStatus.proposer =false;
                        }
                    };
                    $scope.upload = function () {
                        console.log($scope.fileSaved);
                        /*if (files && files.length) {
                            for (var i = 0; i < files.length; i++) {
                                var file = files[i];
                                $upload.upload({
                                    url: 'upload',
                                    file: file
                                }).progress(function (evt) {
                                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                                    console.log('progress: ' + progressPercentage + '% ' + evt.config.file.name);
                                }).success(function (data, status, headers, config) {
                                    console.log('file ' + config.file.name + 'uploaded. Response: ' + data);
                                });
                            }
                        }*/
                    };
                }])
            .config(["$routeProvider","$dropdownProvider",function($routeProvider,$dropdownProvider){
                angular.extend($dropdownProvider.defaults, {
                    html: true
                });
                $routeProvider.when('/', {
                    templateUrl: 'createQuotationTpl.html',
                    controller: 'quotationCtrl',
                    resolve: {

                    }
                })
            }]);

    });


