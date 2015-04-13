var webjars = {
    versions: {
        "angular-ui-bootstrap": "0.12.1-1",
        "angular-moment": "0.9.0",
        "jquery-ui": "1.11.2",
        "tinymce": "4.1.9",
        "bootstrap": "3.3.2",
        "angular-ui-router": "0.2.13",
        "angular-strap": "2.1.4",
        "jquery": "2.1.3",
        "bootstrap-modal": "2.2.5",
        "ng-tags-input": "2.1.1",
        "datatables": "1.10.5",
        "jquery-ui-themes": "1.11.2",
        "fuelux": "3.3.1",
        "datatables-bootstrap": "2-20120201",
        "angular-file-upload": "3.2.4",
        "requirejs": "2.1.15",
        "underscorejs": "1.8.2",
        "bootstrap-multiselect": "0.9.9",
        "angularjs": "1.3.14",
        "bootstrap-datepicker": "1.3.1",
        "angular-ui-tinymce": "0.0.5",
        "momentjs": "2.9.0",
        "angular-loading-bar": "0.7.1"
    },
    path: function (webJarId, path) {
        console.error('The webjars.path() method of getting a WebJar path has been deprecated.  The RequireJS config in the ' + webJarId + ' WebJar may need to be updated.  Please file an issue: http://github.com/webjars/' + webJarId + '/issues/new');
        return ['/pla/webjars/' + webJarId + '/' + webjars.versions[webJarId] + '/' + path];
    }
};

var require = {
    callback: function () {
        // Deprecated WebJars RequireJS plugin loader
        define('webjars', function () {
            return {
                load: function (name, req, onload, config) {
                    if (name.indexOf('.js') >= 0) {
                        console.warn('Detected a legacy file name (' + name + ') as the thing to load.  Loading via file name is no longer supported so the .js will be dropped in an effort to resolve the module name instead.');
                        name = name.replace('.js', '');
                    }
                    console.error('The webjars plugin loader (e.g. webjars!' + name + ') has been deprecated.  The RequireJS config in the ' + name + ' WebJar may need to be updated.  Please file an issue: http://github.com/webjars/webjars/issues/new');
                    req([name], function () {
                        ;
                        onload();
                    });
                }
            }
        });

        // All of the WebJar configs
        requirejs.config({
            "paths": {
                "ui-bootstrap": ["/pla/webjars/angular-ui-bootstrap/0.12.1-1/ui-bootstrap.min", "ui-bootstrap"],
                "ui-bootstrap-tpls": ["/pla/webjars/angular-ui-bootstrap/0.12.1-1/ui-bootstrap-tpls.min", "ui-bootstrap-tpls"]
            }, "shim": {"ui-bootstrap": ["angular"], "ui-bootstrap-tpls": ["angular"]}, "packages": []
        })
        requirejs.config({
            "paths": {"angular-moment": ["/pla/webjars/angular-moment/0.9.0/angular-moment.min", "angular-moment"]},
            "shim": {"angular-moment": ["angular", "momentjs"]},
            "packages": []
        })
        requirejs.config({
            "paths": {
                "jquery-ui": ["/pla/webjars/jquery-ui/1.11.2/jquery-ui", "jquery-ui"],
                "jquery-ui-min": ["/pla/webjars/jquery-ui/1.11.2/jquery-ui.min", "jquery-ui.min"]
            }, "shim": {"jquery-ui": ["jquery"], "jquery-ui-min": ["jquery"]}, "packages": []
        })
        requirejs.config({"paths": {"tinymce": ["/pla/webjars/tinymce/4.1.9/tinymce.min", "tinymce.min"]}, "packages": []})
        requirejs.config({
            "paths": {
                "bootstrap": ["/pla/webjars/bootstrap/3.3.4/js/bootstrap.min", "js/bootstrap"],
                "bootstrap-css": ["/pla/webjars/bootstrap/3.3.4/css/bootstrap.min", "css/bootstrap"]
            }, "shim": {"bootstrap": ["jquery"]}, "packages": []
        })
        requirejs.config({
            "paths": {"angular-ui-router": ["/pla/webjars/angular-ui-router/0.2.13/angular-ui-router.min", "angular-ui-router"]},
            "shim": {"angular-ui-router": ["angular"]},
            "packages": []
        })
        requirejs.config({
            "paths": {
                "angular-strap": ["/pla/webjars/angular-strap/2.1.4/angular-strap.min", "angular-strap"],
                "angular-strap-tpl": ["/pla/webjars/angular-strap/2.1.4/angular-strap.tpl.min", "angular-strap.tpl"]
            }, "shim": {"angular-strap-tpl": ["angular-strap"], "angular-strap": ["angular"]}, "packages": []
        })
        requirejs.config({"paths": {"jquery": ["/pla/webjars/jquery/2.1.3/jquery.min", "jquery"]}, "shim": {"jquery": {"exports": "$"}}, "packages": []})

        requirejs.config({"paths": {"ng-tags-input": ["/pla/webjars/ng-tags-input/2.1.1/ng-tags-input", "ng-tags-input"]}, "shim": {"ng-tags-input": ["angular"]}, "packages": []})
        requirejs.config({
            "paths": {"datatables": ["/pla/webjars/datatables/1.10.5/js/jquery.dataTables.min", "js/jquery.dataTables"]},
            "shim": {"datatables": ["jquery"]},
            "packages": []
        })

// WebJar config for fuelux
        /*global requirejs */

// Ensure any request for this webjar brings in dependencies. For example if this webjar contains
// bootstrap.js which depends on jQuery then you would have the following configuration.
//
//     requirejs.config({
//        shim: {
//            'bootstrap': [ 'webjars!jquery.js' ]
//        }
//    });

// WebJar config for datatables-bootstrap
        /*global requirejs */

// Ensure any request for this webjar brings in jQuery.
        requirejs.config({
            shim: {
                'DT_bootstrap': ['webjars!jquery.js']
            }
        });

        requirejs.config({
            "paths": {
                "ng-file-upload-all": ["/pla/webjars/angular-file-upload/3.2.4/ng-file-upload-all", "ng-file-upload-all"],
                "ng-file-upload-all-min": ["/pla/webjars/angular-file-upload/3.2.4/ng-file-upload-all.min", "ng-file-upload-all.min"],
                "angular-file-upload": ["/pla/webjars/angular-file-upload/3.2.4/angular-file-upload", "angular-file-upload"],
                "angular-file-upload-min": ["/pla/webjars/angular-file-upload/3.2.4/angular-file-upload.min", "angular-file-upload.min"],
                "angular-file-upload-shim": ["/pla/webjars/angular-file-upload/3.2.4/angular-file-upload-shim", "angular-file-upload-shim"],
                "angular-file-upload-shim-min": ["/pla/webjars/angular-file-upload/3.2.4/angular-file-upload-shim.min", "angular-file-upload-shim.min"],
                "angular-file-upload-all": ["/pla/webjars/angular-file-upload/3.2.4/angular-file-upload-all", "angular-file-upload-all"],
                "angular-file-upload-all-min": ["/pla/webjars/angular-file-upload/3.2.4/angular-file-upload-all.min", "angular-file-upload-all.min"]
            },
            "shim": {
                "ng-file-upload-all": ["angular"],
                "ng-file-upload-all-min": ["angular"],
                "angular-file-upload": ["angular"],
                "angular-file-upload-min": ["angular"],
                "angular-file-upload-shim": ["angular"],
                "angular-file-upload-shim-min": ["angular"],
                "angular-file-upload-all": ["angular"],
                "angular-file-upload-all-min": ["angular"]
            },
            "packages": []
        })
        requirejs.config({"paths": {}, "packages": []})
        requirejs.config({
            "paths": {
                "underscore": ["/pla/webjars/underscorejs/1.8.2/underscore-min", "underscore"],
                "underscorejs": ["/pla/webjars/underscorejs/1.8.2/underscore-min", "underscore"]
            }, "shim": {"underscore": {"exports": "_"}, "underscorejs": {"exports": "_"}}, "packages": []
        })
        requirejs.config({
            "paths": {"bootstrap-multiselect": ["/pla/webjars/bootstrap-multiselect/0.9.9/js/bootstrap-multiselect", "js/bootstrap-multiselect"]},
            "shim": {"bootstrap-multiselect": ["bootstrap"]},
            "packages": []
        })
        requirejs.config({
            "paths": {
                "angular": ["/pla/webjars/angularjs/1.3.14/angular.min", "angular"],
                "angular-animate": ["/pla/webjars/angularjs/1.3.14/angular-animate.min", "angular-animate"],
                "angular-aria": ["/pla/webjars/angularjs/1.3.14/angular-aria.min", "angular-aria"],
                "angular-cookies": ["/pla/webjars/angularjs/1.3.14/angular-cookies.min", "angular-cookies"],
                "angular-loader": ["/pla/webjars/angularjs/1.3.14/angular-loader.min", "angular-loader"],
                "angular-messages": ["/pla/webjars/angularjs/1.3.14/angular-messages.min", "angular-messages"],
                "angular-mocks": ["/pla/webjars/angularjs/1.3.14/angular-mocks.min", "angular-mocks"],
                "angular-resource": ["/pla/webjars/angularjs/1.3.14/angular-resource.min", "angular-resource"],
                "angular-route": ["/pla/webjars/angularjs/1.3.14/angular-route.min", "angular-route"],
                "angular-sanitize": ["/pla/webjars/angularjs/1.3.14/angular-sanitize.min", "angular-sanitize"],
                "angular-scenario": ["/pla/webjars/angularjs/1.3.14/angular-scenario.min", "angular-scenario"],
                "angular-touch": ["/pla/webjars/angularjs/1.3.14/angular-touch.min", "angular-touch"],
                "angular-locale_en-in": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-in", "i18n/angular-locale_en-in"],
                "angular-locale_sk-sk": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_sk-sk", "i18n/angular-locale_sk-sk"],
                "angular-locale_en-dsrt-us": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-dsrt-us", "i18n/angular-locale_en-dsrt-us"],
                "angular-locale_cs-cz": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_cs-cz", "i18n/angular-locale_cs-cz"],
                "angular-locale_pt-pt": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_pt-pt", "i18n/angular-locale_pt-pt"],
                "angular-locale_ml-in": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ml-in", "i18n/angular-locale_ml-in"],
                "angular-locale_ro-ro": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ro-ro", "i18n/angular-locale_ro-ro"],
                "angular-locale_sr": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_sr", "i18n/angular-locale_sr"],
                "angular-locale_en-ie": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-ie", "i18n/angular-locale_en-ie"],
                "angular-locale_hr": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_hr", "i18n/angular-locale_hr"],
                "angular-locale_ko": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ko", "i18n/angular-locale_ko"],
                "angular-locale_sw-tz": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_sw-tz", "i18n/angular-locale_sw-tz"],
                "angular-locale_de-ch": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_de-ch", "i18n/angular-locale_de-ch"],
                "angular-locale_ja": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ja", "i18n/angular-locale_ja"],
                "angular-locale_sq-al": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_sq-al", "i18n/angular-locale_sq-al"],
                "angular-locale_ln": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ln", "i18n/angular-locale_ln"],
                "angular-locale_zh-cn": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_zh-cn", "i18n/angular-locale_zh-cn"],
                "angular-locale_en-zz": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-zz", "i18n/angular-locale_en-zz"],
                "angular-locale_en-sg": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-sg", "i18n/angular-locale_en-sg"],
                "angular-locale_kn": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_kn", "i18n/angular-locale_kn"],
                "angular-locale_mr-in": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_mr-in", "i18n/angular-locale_mr-in"],
                "angular-locale_mr": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_mr", "i18n/angular-locale_mr"],
                "angular-locale_fil-ph": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fil-ph", "i18n/angular-locale_fil-ph"],
                "angular-locale_zh-hans": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_zh-hans", "i18n/angular-locale_zh-hans"],
                "angular-locale_am": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_am", "i18n/angular-locale_am"],
                "angular-locale_he-il": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_he-il", "i18n/angular-locale_he-il"],
                "angular-locale_gl": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_gl", "i18n/angular-locale_gl"],
                "angular-locale_en": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en", "i18n/angular-locale_en"],
                "angular-locale_en-mh": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-mh", "i18n/angular-locale_en-mh"],
                "angular-locale_hi": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_hi", "i18n/angular-locale_hi"],
                "angular-locale_ro": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ro", "i18n/angular-locale_ro"],
                "angular-locale_pt": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_pt", "i18n/angular-locale_pt"],
                "angular-locale_hu": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_hu", "i18n/angular-locale_hu"],
                "angular-locale_sl-si": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_sl-si", "i18n/angular-locale_sl-si"],
                "angular-locale_fr-ca": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fr-ca", "i18n/angular-locale_fr-ca"],
                "angular-locale_fil": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fil", "i18n/angular-locale_fil"],
                "angular-locale_it": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_it", "i18n/angular-locale_it"],
                "angular-locale_fr-bl": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fr-bl", "i18n/angular-locale_fr-bl"],
                "angular-locale_fr-gp": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fr-gp", "i18n/angular-locale_fr-gp"],
                "angular-locale_ms": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ms", "i18n/angular-locale_ms"],
                "angular-locale_sv-se": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_sv-se", "i18n/angular-locale_sv-se"],
                "angular-locale_it-it": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_it-it", "i18n/angular-locale_it-it"],
                "angular-locale_sr-latn-rs": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_sr-latn-rs", "i18n/angular-locale_sr-latn-rs"],
                "angular-locale_ar-eg": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ar-eg", "i18n/angular-locale_ar-eg"],
                "angular-locale_en-vi": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-vi", "i18n/angular-locale_en-vi"],
                "angular-locale_vi-vn": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_vi-vn", "i18n/angular-locale_vi-vn"],
                "angular-locale_ja-jp": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ja-jp", "i18n/angular-locale_ja-jp"],
                "angular-locale_ta": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ta", "i18n/angular-locale_ta"],
                "angular-locale_en-iso": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-iso", "i18n/angular-locale_en-iso"],
                "angular-locale_en-gu": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-gu", "i18n/angular-locale_en-gu"],
                "angular-locale_eu-es": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_eu-es", "i18n/angular-locale_eu-es"],
                "angular-locale_no": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_no", "i18n/angular-locale_no"],
                "angular-locale_hu-hu": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_hu-hu", "i18n/angular-locale_hu-hu"],
                "angular-locale_fr-mf": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fr-mf", "i18n/angular-locale_fr-mf"],
                "angular-locale_en-um": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-um", "i18n/angular-locale_en-um"],
                "angular-locale_gl-es": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_gl-es", "i18n/angular-locale_gl-es"],
                "angular-locale_pl": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_pl", "i18n/angular-locale_pl"],
                "angular-locale_nl-nl": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_nl-nl", "i18n/angular-locale_nl-nl"],
                "angular-locale_et": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_et", "i18n/angular-locale_et"],
                "angular-locale_de-lu": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_de-lu", "i18n/angular-locale_de-lu"],
                "angular-locale_da": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_da", "i18n/angular-locale_da"],
                "angular-locale_zh": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_zh", "i18n/angular-locale_zh"],
                "angular-locale_tr-tr": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_tr-tr", "i18n/angular-locale_tr-tr"],
                "angular-locale_am-et": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_am-et", "i18n/angular-locale_am-et"],
                "angular-locale_te-in": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_te-in", "i18n/angular-locale_te-in"],
                "angular-locale_hi-in": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_hi-in", "i18n/angular-locale_hi-in"],
                "angular-locale_et-ee": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_et-ee", "i18n/angular-locale_et-ee"],
                "angular-locale_tr": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_tr", "i18n/angular-locale_tr"],
                "angular-locale_sk": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_sk", "i18n/angular-locale_sk"],
                "angular-locale_sr-rs": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_sr-rs", "i18n/angular-locale_sr-rs"],
                "angular-locale_lv": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_lv", "i18n/angular-locale_lv"],
                "angular-locale_fa-ir": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fa-ir", "i18n/angular-locale_fa-ir"],
                "angular-locale_sl": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_sl", "i18n/angular-locale_sl"],
                "angular-locale_el-gr": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_el-gr", "i18n/angular-locale_el-gr"],
                "angular-locale_bn-bd": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_bn-bd", "i18n/angular-locale_bn-bd"],
                "angular-locale_lt-lt": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_lt-lt", "i18n/angular-locale_lt-lt"],
                "angular-locale_sq": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_sq", "i18n/angular-locale_sq"],
                "angular-locale_fr-mq": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fr-mq", "i18n/angular-locale_fr-mq"],
                "angular-locale_ms-my": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ms-my", "i18n/angular-locale_ms-my"],
                "angular-locale_id": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_id", "i18n/angular-locale_id"],
                "angular-locale_bn": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_bn", "i18n/angular-locale_bn"],
                "angular-locale_hr-hr": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_hr-hr", "i18n/angular-locale_hr-hr"],
                "angular-locale_fr-mc": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fr-mc", "i18n/angular-locale_fr-mc"],
                "angular-locale_id-id": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_id-id", "i18n/angular-locale_id-id"],
                "angular-locale_es": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_es", "i18n/angular-locale_es"],
                "angular-locale_es-es": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_es-es", "i18n/angular-locale_es-es"],
                "angular-locale_cs": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_cs", "i18n/angular-locale_cs"],
                "angular-locale_zh-hans-cn": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_zh-hans-cn", "i18n/angular-locale_zh-hans-cn"],
                "angular-locale_fr": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fr", "i18n/angular-locale_fr"],
                "angular-locale_th-th": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_th-th", "i18n/angular-locale_th-th"],
                "angular-locale_te": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_te", "i18n/angular-locale_te"],
                "angular-locale_fa": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fa", "i18n/angular-locale_fa"],
                "angular-locale_kn-in": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_kn-in", "i18n/angular-locale_kn-in"],
                "angular-locale_or": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_or", "i18n/angular-locale_or"],
                "angular-locale_pt-br": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_pt-br", "i18n/angular-locale_pt-br"],
                "angular-locale_zh-hk": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_zh-hk", "i18n/angular-locale_zh-hk"],
                "angular-locale_gu": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_gu", "i18n/angular-locale_gu"],
                "angular-locale_en-au": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-au", "i18n/angular-locale_en-au"],
                "angular-locale_is-is": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_is-is", "i18n/angular-locale_is-is"],
                "angular-locale_en-gb": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-gb", "i18n/angular-locale_en-gb"],
                "angular-locale_ln-cd": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ln-cd", "i18n/angular-locale_ln-cd"],
                "angular-locale_mo": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_mo", "i18n/angular-locale_mo"],
                "angular-locale_sw": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_sw", "i18n/angular-locale_sw"],
                "angular-locale_tl": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_tl", "i18n/angular-locale_tl"],
                "angular-locale_mt": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_mt", "i18n/angular-locale_mt"],
                "angular-locale_vi": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_vi", "i18n/angular-locale_vi"],
                "angular-locale_de": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_de", "i18n/angular-locale_de"],
                "angular-locale_mt-mt": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_mt-mt", "i18n/angular-locale_mt-mt"],
                "angular-locale_pl-pl": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_pl-pl", "i18n/angular-locale_pl-pl"],
                "angular-locale_uk-ua": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_uk-ua", "i18n/angular-locale_uk-ua"],
                "angular-locale_gsw-ch": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_gsw-ch", "i18n/angular-locale_gsw-ch"],
                "angular-locale_ca": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ca", "i18n/angular-locale_ca"],
                "angular-locale_tl-ph": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_tl-ph", "i18n/angular-locale_tl-ph"],
                "angular-locale_da-dk": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_da-dk", "i18n/angular-locale_da-dk"],
                "angular-locale_ca-es": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ca-es", "i18n/angular-locale_ca-es"],
                "angular-locale_bg-bg": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_bg-bg", "i18n/angular-locale_bg-bg"],
                "angular-locale_nl": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_nl", "i18n/angular-locale_nl"],
                "angular-locale_or-in": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_or-in", "i18n/angular-locale_or-in"],
                "angular-locale_gu-in": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_gu-in", "i18n/angular-locale_gu-in"],
                "angular-locale_zh-tw": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_zh-tw", "i18n/angular-locale_zh-tw"],
                "angular-locale_bg": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_bg", "i18n/angular-locale_bg"],
                "angular-locale_is": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_is", "i18n/angular-locale_is"],
                "angular-locale_eu": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_eu", "i18n/angular-locale_eu"],
                "angular-locale_el-polyton": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_el-polyton", "i18n/angular-locale_el-polyton"],
                "angular-locale_fr-re": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fr-re", "i18n/angular-locale_fr-re"],
                "angular-locale_sr-cyrl-rs": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_sr-cyrl-rs", "i18n/angular-locale_sr-cyrl-rs"],
                "angular-locale_lt": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_lt", "i18n/angular-locale_lt"],
                "angular-locale_sv": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_sv", "i18n/angular-locale_sv"],
                "angular-locale_en-za": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-za", "i18n/angular-locale_en-za"],
                "angular-locale_ur-pk": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ur-pk", "i18n/angular-locale_ur-pk"],
                "angular-locale_fi": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fi", "i18n/angular-locale_fi"],
                "angular-locale_he": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_he", "i18n/angular-locale_he"],
                "angular-locale_en-mp": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-mp", "i18n/angular-locale_en-mp"],
                "angular-locale_ru-ru": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ru-ru", "i18n/angular-locale_ru-ru"],
                "angular-locale_de-at": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_de-at", "i18n/angular-locale_de-at"],
                "angular-locale_en-as": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-as", "i18n/angular-locale_en-as"],
                "angular-locale_gsw": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_gsw", "i18n/angular-locale_gsw"],
                "angular-locale_ko-kr": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ko-kr", "i18n/angular-locale_ko-kr"],
                "angular-locale_de-de": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_de-de", "i18n/angular-locale_de-de"],
                "angular-locale_ru": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ru", "i18n/angular-locale_ru"],
                "angular-locale_iw": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_iw", "i18n/angular-locale_iw"],
                "angular-locale_uk": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_uk", "i18n/angular-locale_uk"],
                "angular-locale_in": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_in", "i18n/angular-locale_in"],
                "angular-locale_en-dsrt": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-dsrt", "i18n/angular-locale_en-dsrt"],
                "angular-locale_el": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_el", "i18n/angular-locale_el"],
                "angular-locale_en-us": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_en-us", "i18n/angular-locale_en-us"],
                "angular-locale_fi-fi": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fi-fi", "i18n/angular-locale_fi-fi"],
                "angular-locale_ar": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ar", "i18n/angular-locale_ar"],
                "angular-locale_de-be": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_de-be", "i18n/angular-locale_de-be"],
                "angular-locale_th": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_th", "i18n/angular-locale_th"],
                "angular-locale_ta-in": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ta-in", "i18n/angular-locale_ta-in"],
                "angular-locale_ml": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ml", "i18n/angular-locale_ml"],
                "angular-locale_lv-lv": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_lv-lv", "i18n/angular-locale_lv-lv"],
                "angular-locale_ur": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_ur", "i18n/angular-locale_ur"],
                "angular-locale_fr-fr": ["/pla/webjars/angularjs/1.3.14/i18n/angular-locale_fr-fr", "i18n/angular-locale_fr-fr"]
            },
            "shim": {
                "angular": {"exports": "angular"},
                "angular-animate": ["angular"],
                "angular-aria": ["angular"],
                "angular-cookies": ["angular"],
                "angular-loader": ["angular"],
                "angular-messages": ["angular"],
                "angular-mocks": ["angular"],
                "angular-resource": ["angular"],
                "angular-route": ["angular"],
                "angular-sanitize": ["angular"],
                "angular-scenario": ["angular"],
                "angular-touch": ["angular"],
                "angular-locale_en-in": ["angular"],
                "angular-locale_sk-sk": ["angular"],
                "angular-locale_en-dsrt-us": ["angular"],
                "angular-locale_cs-cz": ["angular"],
                "angular-locale_pt-pt": ["angular"],
                "angular-locale_ml-in": ["angular"],
                "angular-locale_ro-ro": ["angular"],
                "angular-locale_sr": ["angular"],
                "angular-locale_en-ie": ["angular"],
                "angular-locale_hr": ["angular"],
                "angular-locale_ko": ["angular"],
                "angular-locale_sw-tz": ["angular"],
                "angular-locale_de-ch": ["angular"],
                "angular-locale_ja": ["angular"],
                "angular-locale_sq-al": ["angular"],
                "angular-locale_ln": ["angular"],
                "angular-locale_zh-cn": ["angular"],
                "angular-locale_en-zz": ["angular"],
                "angular-locale_en-sg": ["angular"],
                "angular-locale_kn": ["angular"],
                "angular-locale_mr-in": ["angular"],
                "angular-locale_mr": ["angular"],
                "angular-locale_fil-ph": ["angular"],
                "angular-locale_zh-hans": ["angular"],
                "angular-locale_am": ["angular"],
                "angular-locale_he-il": ["angular"],
                "angular-locale_gl": ["angular"],
                "angular-locale_en": ["angular"],
                "angular-locale_en-mh": ["angular"],
                "angular-locale_hi": ["angular"],
                "angular-locale_ro": ["angular"],
                "angular-locale_pt": ["angular"],
                "angular-locale_hu": ["angular"],
                "angular-locale_sl-si": ["angular"],
                "angular-locale_fr-ca": ["angular"],
                "angular-locale_fil": ["angular"],
                "angular-locale_it": ["angular"],
                "angular-locale_fr-bl": ["angular"],
                "angular-locale_fr-gp": ["angular"],
                "angular-locale_ms": ["angular"],
                "angular-locale_sv-se": ["angular"],
                "angular-locale_it-it": ["angular"],
                "angular-locale_sr-latn-rs": ["angular"],
                "angular-locale_ar-eg": ["angular"],
                "angular-locale_en-vi": ["angular"],
                "angular-locale_vi-vn": ["angular"],
                "angular-locale_ja-jp": ["angular"],
                "angular-locale_ta": ["angular"],
                "angular-locale_en-iso": ["angular"],
                "angular-locale_en-gu": ["angular"],
                "angular-locale_eu-es": ["angular"],
                "angular-locale_no": ["angular"],
                "angular-locale_hu-hu": ["angular"],
                "angular-locale_fr-mf": ["angular"],
                "angular-locale_en-um": ["angular"],
                "angular-locale_gl-es": ["angular"],
                "angular-locale_pl": ["angular"],
                "angular-locale_nl-nl": ["angular"],
                "angular-locale_et": ["angular"],
                "angular-locale_de-lu": ["angular"],
                "angular-locale_da": ["angular"],
                "angular-locale_zh": ["angular"],
                "angular-locale_tr-tr": ["angular"],
                "angular-locale_am-et": ["angular"],
                "angular-locale_te-in": ["angular"],
                "angular-locale_hi-in": ["angular"],
                "angular-locale_et-ee": ["angular"],
                "angular-locale_tr": ["angular"],
                "angular-locale_sk": ["angular"],
                "angular-locale_sr-rs": ["angular"],
                "angular-locale_lv": ["angular"],
                "angular-locale_fa-ir": ["angular"],
                "angular-locale_sl": ["angular"],
                "angular-locale_el-gr": ["angular"],
                "angular-locale_bn-bd": ["angular"],
                "angular-locale_lt-lt": ["angular"],
                "angular-locale_sq": ["angular"],
                "angular-locale_fr-mq": ["angular"],
                "angular-locale_ms-my": ["angular"],
                "angular-locale_id": ["angular"],
                "angular-locale_bn": ["angular"],
                "angular-locale_hr-hr": ["angular"],
                "angular-locale_fr-mc": ["angular"],
                "angular-locale_id-id": ["angular"],
                "angular-locale_es": ["angular"],
                "angular-locale_es-es": ["angular"],
                "angular-locale_cs": ["angular"],
                "angular-locale_zh-hans-cn": ["angular"],
                "angular-locale_fr": ["angular"],
                "angular-locale_th-th": ["angular"],
                "angular-locale_te": ["angular"],
                "angular-locale_fa": ["angular"],
                "angular-locale_kn-in": ["angular"],
                "angular-locale_or": ["angular"],
                "angular-locale_pt-br": ["angular"],
                "angular-locale_zh-hk": ["angular"],
                "angular-locale_gu": ["angular"],
                "angular-locale_en-au": ["angular"],
                "angular-locale_is-is": ["angular"],
                "angular-locale_en-gb": ["angular"],
                "angular-locale_ln-cd": ["angular"],
                "angular-locale_mo": ["angular"],
                "angular-locale_sw": ["angular"],
                "angular-locale_tl": ["angular"],
                "angular-locale_mt": ["angular"],
                "angular-locale_vi": ["angular"],
                "angular-locale_de": ["angular"],
                "angular-locale_mt-mt": ["angular"],
                "angular-locale_pl-pl": ["angular"],
                "angular-locale_uk-ua": ["angular"],
                "angular-locale_gsw-ch": ["angular"],
                "angular-locale_ca": ["angular"],
                "angular-locale_tl-ph": ["angular"],
                "angular-locale_da-dk": ["angular"],
                "angular-locale_ca-es": ["angular"],
                "angular-locale_bg-bg": ["angular"],
                "angular-locale_nl": ["angular"],
                "angular-locale_or-in": ["angular"],
                "angular-locale_gu-in": ["angular"],
                "angular-locale_zh-tw": ["angular"],
                "angular-locale_bg": ["angular"],
                "angular-locale_is": ["angular"],
                "angular-locale_eu": ["angular"],
                "angular-locale_el-polyton": ["angular"],
                "angular-locale_fr-re": ["angular"],
                "angular-locale_sr-cyrl-rs": ["angular"],
                "angular-locale_lt": ["angular"],
                "angular-locale_sv": ["angular"],
                "angular-locale_en-za": ["angular"],
                "angular-locale_ur-pk": ["angular"],
                "angular-locale_fi": ["angular"],
                "angular-locale_he": ["angular"],
                "angular-locale_en-mp": ["angular"],
                "angular-locale_ru-ru": ["angular"],
                "angular-locale_de-at": ["angular"],
                "angular-locale_en-as": ["angular"],
                "angular-locale_gsw": ["angular"],
                "angular-locale_ko-kr": ["angular"],
                "angular-locale_de-de": ["angular"],
                "angular-locale_ru": ["angular"],
                "angular-locale_iw": ["angular"],
                "angular-locale_uk": ["angular"],
                "angular-locale_in": ["angular"],
                "angular-locale_en-dsrt": ["angular"],
                "angular-locale_el": ["angular"],
                "angular-locale_en-us": ["angular"],
                "angular-locale_fi-fi": ["angular"],
                "angular-locale_ar": ["angular"],
                "angular-locale_de-be": ["angular"],
                "angular-locale_th": ["angular"],
                "angular-locale_ta-in": ["angular"],
                "angular-locale_ml": ["angular"],
                "angular-locale_lv-lv": ["angular"],
                "angular-locale_ur": ["angular"],
                "angular-locale_fr-fr": ["angular"]
            },
            "packages": []
        })
        requirejs.config({
            "paths": {"bootstrap-datepicker": ["/pla/webjars/bootstrap-datepicker/1.3.1/js/bootstrap-datepicker.min", "js/bootstrap-datepicker"]},
            "shim": {"bootstrap-datepicker": ["bootstrap"]},
            "packages": []
        })
        requirejs.config({
            "paths": {"angular-ui-tinymce": ["/pla/webjars/angular-ui-tinymce/0.0.5/angular-ui-tinymce.min", "angular-ui-tinymce"]},
            "shim": {"angular-ui-tinymce": ["angular-ui", "tinymce"]},
            "packages": []
        })
        requirejs.config({
            "paths": {
                "momentjs": ["/pla/webjars/momentjs/2.9.0/min/moment.min", "moment"],
                "moment": ["/pla/webjars/momentjs/2.9.0/min/moment.min", "moment"],
                "moment-with-locales": ["/pla/webjars/momentjs/2.9.0/min/moment-with-locales", "min/moment-with-locales"]
            }, "packages": []
        })
        requirejs.config({
            "paths": {"angular-loading-bar": ["/pla/webjars/angular-loading-bar/0.7.1/loading-bar", "loading-bar"]},
            "shim": {"angular-loading-bar": ["angular"]},
            "packages": []
        })
    }
}
