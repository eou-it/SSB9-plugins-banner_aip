///<reference path="../../typings/tsd.d.ts"/>
///<reference path="../common/services/csrBreadcrumbService.ts"/>

declare var register;

interface IAdminLandingCtrlScrope extends ng.IScope {
    vm:CSR.AdminLandingCtrl;
}

module CSR {
    export class AdminLandingCtrl {
        $inject = ["$scope", "CsrBreadcrumbService"];
        testValue: string;
        breadcrumbService: CSR.CsrBreadcrumbService;
        constructor($scope:IAdminLandingCtrlScrope, CsrBreadcrumbService:CSR.CsrBreadcrumbService) {
            $scope.vm = this;
            this.breadcrumbService = CsrBreadcrumbService;
            this.testValue = "TEST";
            var breadItem = {
                "Admin Landing": "/landing"
            };
            this.breadcrumbService.updateBreadcrumb(breadItem);
        }
    }
}

register("bannercsr").controller("AdminLandingCtrl", CSR.AdminLandingCtrl);