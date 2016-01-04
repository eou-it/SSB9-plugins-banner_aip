///<reference path="../../typings/tsd.d.ts"/>
var CSR;
(function (CSR) {
    var AdminItemListView = (function () {
        function AdminItemListView($scope, $mdToast) {
            $scope.vm = this;
            this.toastService = $mdToast;
            this.init();
        }
        AdminItemListView.prototype.init = function () {
            this.studentGroups = ["International", "Out of state", "In state", "All"];
            this.itemGroups = [
                { id: 0, name: "Visa status", group: 0, description: "Visa documents upload" },
                { id: 1, name: "Medical Record", group: 0, description: "Medical record documents upload" },
                { id: 2, name: "Address", group: 3, description: "Permanent residential address" }
            ];
        };
        AdminItemListView.prototype.deleteRowCallback = function (rows) {
            this.toastService.show(this.toastService.simple()
                .textContent("Deleted row id(s): " + rows)
                .hideDelay(3000));
        };
        AdminItemListView.$inject = ["$scope", "$mdToast"];
        return AdminItemListView;
    })();
    CSR.AdminItemListView = AdminItemListView;
})(CSR || (CSR = {}));
register("bannercsr").controller("AdminItemListView", CSR.AdminItemListView);
//# sourceMappingURL=adminItemListView.js.map