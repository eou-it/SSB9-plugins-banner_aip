///<reference path="../../typings/tsd.d.ts"/>
///<reference path="../service/testService.ts"/>
var BannerCSR;
(function (BannerCSR) {
    var TestCtrl = (function () {
        function TestCtrl($scope, TestService) {
            this.$inject = ["$scope"];
            $scope.vm = this;
            this.sideNavText = "Side Navigation";
            this.mainContentText = "Main content area";
            this.TestService = TestService;
        }
        return TestCtrl;
    })();
    BannerCSR.TestCtrl = TestCtrl;
})(BannerCSR || (BannerCSR = {}));
register("bannercsr").controller("TestCtrl", BannerCSR.TestCtrl);
//# sourceMappingURL=testCtrl.js.map