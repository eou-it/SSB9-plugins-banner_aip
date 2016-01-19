///<reference path="../../../../typings/tsd.d.ts"/>
var CSR_UI;
(function (CSR_UI) {
    var CsrXeTable = (function () {
        function CsrXeTable($rootScope, $scope, $filter, $attrs, $transclude, $http) {
            this.$inject = ["$rootScope", "$scope", "$filter", "$attrs", "$transclude", "$http"];
            this.templateUrl = "csrXeTable.html";
            this.restrict = "E";
            this.transclude = true;
            this.replace = true;
        }
        CsrXeTable.prototype.compile = function () {
        };
        CsrXeTable.prototype.link = function ($scope, element, attr) {
        };
        return CsrXeTable;
    })();
    CSR_UI.CsrXeTable = CsrXeTable;
})(CSR_UI || (CSR_UI = {}));
register("bannercsr").directive("csrXeTable", CSR_UI.CsrXeTable);
//# sourceMappingURL=csrXeTable.js.map