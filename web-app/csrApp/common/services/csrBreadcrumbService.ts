///<reference path = "../../../typings/tsd.d.ts"/>

declare var register;
declare var BreadCrumbAndPageTitle;

module CSR {
    export class CsrBreadcrumbService {
        $inject = ["$location"];
        breadcrumbs: {};
        $location;
        constructor($location) {
            this.$location = $location;
            this.init();
        }
        init() {
            this.breadcrumbs = {};
        }
        updateBreadcrumb(item) {
            var existPageTitle = Object.keys(this.breadcrumbs);
            var pageTitle = Object.keys(item)[0];

            if(existPageTitle.indexOf(pageTitle)===-1) {
                if(item[pageTitle].indexOf("/csr#/admin")===-1) {
                    item[pageTitle] = "/csr#/admin".concat(item[pageTitle]);
                }
                this.breadcrumbs[pageTitle] = item[pageTitle];
            } else {
                var temp = {};
                existPageTitle = existPageTitle.slice(0, existPageTitle.indexOf(pageTitle) + 1);
                angular.forEach(existPageTitle, (item) => {
                    temp[item] = this.breadcrumbs[item];
                });
                this.breadcrumbs = temp;
            }
            this.draw();
        }
        draw() {
            var allPageTitle = Object.keys(this.breadcrumbs);
            var updatedHeaderAttributes = {
                "pageTitle": allPageTitle[allPageTitle.length - 1],
                "breadcrumb":this.breadcrumbs
            };
            BreadCrumbAndPageTitle.draw(updatedHeaderAttributes);
        }
    }
}

register("bannercsr").service("CsrBreadcrumbService", CSR.CsrBreadcrumbService);