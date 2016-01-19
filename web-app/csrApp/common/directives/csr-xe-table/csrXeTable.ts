///<reference path="../../../../typings/tsd.d.ts"/>

declare var register;

module CSR_UI {

    interface ICsrXeTableScope {
        header;
        content;
        onSort: void;
        onRowClick: void;
        toolbar;
        paginate: boolean;
        endPoint: string;
        resultsFound;
        fetch;
        pageOffsets;
        height;
        mobileLayout;
        continuousScrolling;
        caption: string;
        onRowDoubleClick: void;
    }

    export class CsrXeTable {
        $inject = ["$rootScope", "$scope", "$filter", "$attrs", "$transclude", "$http"];
        scope: ICsrXeTableScope;
        templateUrl: string;
        restrict: string;
        transclude: boolean;
        replace: boolean;
        constructor($rootScope:ng.IRootScopeService, $scope:ng.IScope, $filter:ng.IFilterService,
                    $attrs:ng.IAttributes, $transclude:ng.ITranscludeFunction, $http:ng.IHttpService) {
            this.templateUrl = "csrXeTable.html";
            this.restrict = "E"
            this.transclude = true;
            this.replace = true;
        }
        compile() {

        }
        link($scope:ng.IScope, element:HTMLElement, attr:ng.IAttributes) {

        }
    }
}

register("bannercsr").directive("csrXeTable", CSR_UI.CsrXeTable);