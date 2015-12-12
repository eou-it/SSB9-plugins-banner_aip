///<reference path="../../typings/tsd.d.ts"/>
var BannerCSR;
(function (BannerCSR) {
    var TestService = (function () {
        function TestService() {
            this.testServiceValue = "TEXT FROM SERVICE";
        }
        return TestService;
    })();
    BannerCSR.TestService = TestService;
})(BannerCSR || (BannerCSR = {}));
register("bannercsr").service("TestService", BannerCSR.TestService);
//# sourceMappingURL=testService.js.map