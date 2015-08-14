requirejs.config({
    paths: {
        'jquery': 'https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min',
        'bootstrap': 'https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min',
        "ui-bootstrap-tpls":"https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.12.1/ui-bootstrap-tpls",
        "angular":"https://ajax.googleapis.com/ajax/libs/angularjs/1.3.12/angular.min",
        "angular-mock":"https://code.angularjs.org/1.3.12/angular-mocks",
        "angular-route":"https://code.angularjs.org/1.3.12/angular-route",
        "angular-strap":"https://cdnjs.cloudflare.com/ajax/libs/angular-strap/2.1.4/angular-strap",
        "angular-strap-tpls":"https://cdnjs.cloudflare.com/ajax/libs/angular-strap/2.1.4/angular-strap.tpl",
        'fuelux': 'http://www.fuelcdn.com/fuelux/3.6.3/js/fuelux.min',
        'angular-file-upload-shim':'http://cdn.jsdelivr.net/webjars/angular-file-upload/3.2.4/angular-file-upload-shim.min',
        'angular-file-upload':'http://cdn.jsdelivr.net/webjars/angular-file-upload/3.2.4/angular-file-upload.min',
        'angular-sanitize':'https://code.angularjs.org/1.3.12/angular-sanitize',
        'bootstrap-tags-input':'http://cdn.jsdelivr.net/webjars/org.webjars/bootstrap-tagsinput/0.3.9/bootstrap-tagsinput',
        'directives':'common/directives',
        'mock':'mock/angularMocks',
        'commonModule':'common/commonModule',
        'angular-loading-bar':'http://cdn.jsdelivr.net/webjars/org.webjars/angular-loading-bar/0.7.1/loading-bar',
        'angular-animate':'http://ajax.googleapis.com/ajax/libs/angularjs/1.3.12/angular-animate'
    },
    shim:{
        'commonModule':{
            deps:['angular']
        },
        'directives':{
          deps:['angular','fuelux']
        },
        'ui-bootstrap-tpls':{
            deps:['angular']
        },
        'angular':{
            deps:['jquery'],
            exports:'angular'
        },
        'bootstrap':{
            deps:['jquery']
        },
        'angular-route':{
            deps:['angular']
        },
        'angular-mock':{
            deps:['angular']
        },
        'angular-sanitize':{
            deps:['angular']
        },
        'angular-strap':{
            deps:['angular']
        },
        'angular-strap-tpls':{
            deps:['angular-strap']
        },
        'angular-file-upload-shim':{
            deps:['angular']
        },
        'angular-file-upload':{
            deps:['angular']
        },
        'bootstrap-tags-input':{
            deps:['jquery','bootstrap']
        },
        'angular-loading-bar':{
            deps:['angular']
        },
        'angular-animate':{
            deps:['angular']
        }


    }
});

