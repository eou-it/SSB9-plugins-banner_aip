///<reference path="../../typings/tsd.d.ts"/>
var CSR;
(function (CSR) {
    var AdminLandingCtrl = (function () {
        function AdminLandingCtrl($scope) {
            this.$inject = ["$scope"];
            $scope.vm = this;
            this.testValue = "TEST";
        }
        return AdminLandingCtrl;
    })();
    CSR.AdminLandingCtrl = AdminLandingCtrl;
})(CSR || (CSR = {}));
register("bannercsr").controller("AdminLandingCtrl", CSR.AdminLandingCtrl);
//# sourceMappingURL=adminLandingCtrl.js.map