/**
 * Created by jshin on 12/8/15.
 */

/** *****************************************************************************
 Copyright 2009-2012 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

modules = {
    'csrTestDependencies' {
        dependsOn "angularApp"
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-material/angular-material.css']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-material/angular-material.js']
//        resource url:[plugin: 'banner-csr', file: 'node_modules/requirejs/require.js']
        resource url:[plugin: 'banner-csr', file: 'js/utils/register.js']
    }

    'bannerCSR' {
        dependsOn "csrTestDependencies, jquery"
        resource url:[plugin: 'banner-csr', file: 'js/app.js']
        resource url:[plugin: 'banner-csr', file: 'js/controller/testCtrl.js']
    }

}