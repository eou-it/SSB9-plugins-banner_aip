/**
 * Created by jshin on 12/8/15.
 */

/** *****************************************************************************
 Copyright 2009-2012 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

modules = {
    'csrTestDependencies' {
        //dependsOn "angularApp"
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-material/angular-material.css']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular/angular.js']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-aria/angular-aria.js']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-animate/angular-animate.js']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-material/angular-material.js']
        resource url:[plugin: 'banner-csr', file: 'js/utils/register.js']
    }

    'csrThirdPartyLib' {
        dependsOn "csrTestDependencies"
        resource url:[plugin: 'banner-csr', file: 'bower_components/angular-material-icons/angular-material-icons.js']
        resource url:[plugin: 'banner-csr', file: 'bower_components/mdDataTable/dist/md-data-table-style.css']
        resource url:[plugin: 'banner-csr', file: 'bower_components/md-data-table.js']
        resource url:[plugin: 'banner-csr', file: 'bower_components/md-data-table-template.js']
    }

    'bannerCSR' {
        dependsOn "csrThirdPartyLib, bannerSelfService"
        resource url:[plugin: 'banner-csr', file: 'js/app.js']
        resource url:[plugin: 'banner-csr', file: 'js/controllers/adminItemListView.js']
    }
}