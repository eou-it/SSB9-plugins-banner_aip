///<reference path="../../typings/tsd.d.ts"/>
declare var register;

module BannerCSR {
    export class TestCtrl {
        $inject = ["$scope"];
        constructor($scope) {
            $scope.testValue = "TEST STRING FROM ANGULAR CONTROLLER";
            $scope.pageTitle = "ANGULAR GRAILS";
        }
    }
}

register("bannercsr").controller("BannerCSR.TestCtrl", BannerCSR.TestCtrl);