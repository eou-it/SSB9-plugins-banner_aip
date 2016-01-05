/**
 * Created by jshin on 12/8/15.
 */
// angular module init and configuration
"use strict";

var bannerCSRApp = angular.module("bannercsr", [
    "ngMaterial",
    "mdDataTable",
    "ngMdIcons"
]);

//provider-injector
bannerCSRApp.config(function() {

});

//instance-injector
bannerCSRApp.run(function() {

});

//last, bootstraping angular module into html element
//not working with GSP.
//angular.bootstrap(document, ["bannercsr"]);