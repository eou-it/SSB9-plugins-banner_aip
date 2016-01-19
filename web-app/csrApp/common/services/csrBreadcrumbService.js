///<reference path = "../../../typings/tsd.d.ts"/>
var CSR;
(function (CSR) {
    var CsrBreadcrumbService = (function () {
        function CsrBreadcrumbService($location) {
            this.$inject = ["$location"];
            this.$location = $location;
            this.init();
        }
        CsrBreadcrumbService.prototype.init = function () {
            this.breadcrumbs = {};
        };
        CsrBreadcrumbService.prototype.updateBreadcrumb = function (item) {
            var _this = this;
            var existPageTitle = Object.keys(this.breadcrumbs);
            var pageTitle = Object.keys(item)[0];
            if (existPageTitle.indexOf(pageTitle) === -1) {
                if (item[pageTitle].indexOf("/csr#/admin") === -1) {
                    item[pageTitle] = "/csr#/admin".concat(item[pageTitle]);
                }
                this.breadcrumbs[pageTitle] = item[pageTitle];
            }
            else {
                var temp = {};
                existPageTitle = existPageTitle.slice(0, existPageTitle.indexOf(pageTitle) + 1);
                angular.forEach(existPageTitle, function (item) {
                    temp[item] = _this.breadcrumbs[item];
                });
                this.breadcrumbs = temp;
            }
            this.draw();
        };
        CsrBreadcrumbService.prototype.draw = function () {
            var allPageTitle = Object.keys(this.breadcrumbs);
            var updatedHeaderAttributes = {
                "pageTitle": allPageTitle[allPageTitle.length - 1],
                "breadcrumb": this.breadcrumbs
            };
            BreadCrumbAndPageTitle.draw(updatedHeaderAttributes);
        };
        return CsrBreadcrumbService;
    })();
    CSR.CsrBreadcrumbService = CsrBreadcrumbService;
})(CSR || (CSR = {}));
register("bannercsr").service("CsrBreadcrumbService", CSR.CsrBreadcrumbService);
//# sourceMappingURL=csrBreadcrumbService.js.map