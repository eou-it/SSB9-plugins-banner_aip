/**
 * Created by jshin on 12/8/15.
 */
// load angular controllers, services, directives, and etc.

require.config({
    baseUrl:"../plugins/banner-csr-1.0/js"
});

require([
    "app",
    "controller/testCtrl"
    ],
    function() {
        //do post process if necessary after loads all the components
    }
);