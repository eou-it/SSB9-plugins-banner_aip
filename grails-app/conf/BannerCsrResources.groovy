/**
 * Created by jshin on 12/8/15.
 */

/** *****************************************************************************
 Copyright 2009-2012 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

modules = {
    'csr-angular' { // Temp resources
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular/angular.js']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-route/angular-route.js']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-aria/angular-aria.js']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-animate/angular-animate.js']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-ui-router/release/angular-ui-router.js']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-sanitize/angular-sanitize.js']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-messages/angular-messages.js']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-resource/angular-resource.js']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js']
        resource url:[plugin: 'banner-csr', file: 'node_modules/moment/moment.js']
//        resource url:[plugin: 'banner-csr', file: 'bower_components/angular-common/build/angular-common.js']
    }

    'csrThirdPartyLib' {
        dependsOn "csr-angular" // Will be replaced by banner_ui_ss' resource module "angular"
        resource url:[plugin: 'banner-csr', file: 'csrApp/utils/register.js']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-material/angular-material.css']
        resource url:[plugin: 'banner-csr', file: 'node_modules/angular-material/angular-material.js']
        resource url:[plugin: 'banner-csr', file: 'bower_components/angular-material-icons/angular-material-icons.js']
    }

    'bannerCSR' {
        dependsOn "csrThirdPartyLib, bannerSelfServiceCommonLTR, extensibilityCommon, extensibilityAngular, common-components,  angularApp"
        resource url:[plugin: 'banner-csr', file: 'csrApp/app.js']
        resource url:[plugin: 'banner-csr', file: 'csrApp/admin/adminLandingCtrl.js']
        resource url:[plugin: 'banner-csr', file: 'csrApp/admin/listActionItem/adminListItemCtrl.js']
        resource url:[plugin: 'banner-csr', file: 'csrApp/common/services/adminItemListViewService.js']
        resource url:[plugin: 'banner-csr', file: 'csrApp/common/services/csrBreadcrumbService.js']
    }
}