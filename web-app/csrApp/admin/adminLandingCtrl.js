///<reference path="../../typings/tsd.d.ts"/>
///<reference path="../common/services/csrBreadcrumbService.ts"/>
var CSR;
(function (CSR) {
    var AdminLandingCtrl = (function () {
        function AdminLandingCtrl($scope, CsrBreadcrumbService) {
            this.$inject = ["$scope", "CsrBreadcrumbService"];
            $scope.vm = this;
            this.breadcrumbService = CsrBreadcrumbService;
            this.testValue = "TEST";
            var breadItem = {
                "Admin Landing": "/landing"
            };
            this.breadcrumbService.updateBreadcrumb(breadItem);
        }
        return AdminLandingCtrl;
    })();
    CSR.AdminLandingCtrl = AdminLandingCtrl;
})(CSR || (CSR = {}));
register("bannercsr").controller("AdminLandingCtrl", CSR.AdminLandingCtrl);
//# sourceMappingURL=adminLandingCtrl.js.map