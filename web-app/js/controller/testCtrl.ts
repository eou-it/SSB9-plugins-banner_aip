///<reference path="../../typings/tsd.d.ts"/>
///<reference path="../service/testService.ts"/>

declare var register;

interface ITestCtrlScope extends ng.IScope {
    vm:BannerCSR.TestCtrl;
}

module BannerCSR {
    export class TestCtrl {
        $inject = ["$scope"];
        sideNavText:string;
        mainContentText:string;
        TestService: BannerCSR.TestService;
        constructor($scope:ITestCtrlScope, TestService:BannerCSR.TestService) {
            $scope.vm = this;
            this.sideNavText = "Side Navigation";
            this.mainContentText = "Main content area";
            this.TestService = TestService;

        }
    }
}
register("bannercsr").controller("TestCtrl", BannerCSR.TestCtrl);