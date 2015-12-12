///<reference path="../../typings/tsd.d.ts"/>
declare var register;

module BannerCSR {
    export class TestService{
        testServiceValue:string;
        constructor() {
            this.testServiceValue="TEXT FROM SERVICE";
        }
    }
}

register("bannercsr").service("TestService", BannerCSR.TestService);