///<reference path="../../typings/tsd.d.ts"/>

declare var register;

interface IAdminLandingCtrlScrope extends ng.IScope {
    vm:CSR.AdminLandingCtrl;
}

module CSR {
    export class AdminLandingCtrl {
        $inject = ["$scope"];
        testValue: string;
        constructor($scope:IAdminLandingCtrlScrope) {
            $scope.vm = this;
            this.testValue = "TEST";
        }
    }
}

register("bannercsr").controller("AdminLandingCtrl", CSR.AdminLandingCtrl);