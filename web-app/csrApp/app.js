/**
 * Created by jshin on 12/8/15.
 */
// angular module init and configuration
"use strict";

var bannerCSRApp = angular.module("bannercsr", [
    "ngResource",
    "ngSanitize",
    "ui.router",
    "ui.bootstrap",
    "ngAria",
    "ngAnimate",
    "xe-ui-components"
]);

//provider-injector
bannerCSRApp.config(["$stateProvider", "$urlRouterProvider", "$locationProvider", function($stateProvider, $urlRouteProvider, $locationProvider) {
    $urlRouteProvider.otherwise("/admin/landing");
    $stateProvider
        .state("adminLanding", {
            url: "/admin/landing",
            templateUrl: "../plugins/banner-csr-1.0/csrApp/admin/landing.html",
            controller: "AdminLandingCtrl"
        })
        .state("adminList", {
            url: "/admin/list",
            templateUrl: "../plugins/banner-csr-1.0/csrApp/admin/listActionItem/adminListItem.html",
            controller: "AdminListItemCtrl"
        });
}]);

//instance-injector
bannerCSRApp.run(function() {

});

//last, bootstraping angular module into html element
//not working with GSP.
//angular.bootstrap(document, ["bannercsr"]);