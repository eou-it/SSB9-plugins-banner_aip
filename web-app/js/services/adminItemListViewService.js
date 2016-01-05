///<reference path="../../typings/tsd.d.ts"/>
var CSR;
(function (CSR) {
    var AdminItemListViewService = (function () {
        function AdminItemListViewService($http) {
            this.httpService = $http;
            this.itemGroups = this.getTestData();
            this.studentGroups = this.getTestGroupData();
        }
        AdminItemListViewService.prototype.getTestGroupData = function () {
            var data = ["International", "Out of state", "In state", "All"];
            return data;
        };
        AdminItemListViewService.prototype.getTestData = function () {
            var data = [
                { id: 0, name: "Visa status", group: 0, description: "Visa documents upload", selected: false },
                { id: 1, name: "Medical Record", group: 0, description: "Medical record documents upload", selected: false },
                { id: 2, name: "Address", group: 3, description: "Permanent residential address", selected: false }
            ];
            return data;
        };
        AdminItemListViewService.prototype.removeSelectedItem = function (selectedItems) {
            var tempItems = [];
            angular.forEach(this.itemGroups, function (item) {
                var exist = selectedItems.filter(function (_item) { return item.id === _item.id; });
                if (exist.length === 0) {
                    tempItems.push(item);
                }
            });
            this.itemGroups = tempItems;
            return this.itemGroups;
        };
        AdminItemListViewService.prototype.addNewItems = function (items) {
            this.itemGroups = items.concat(this.itemGroups);
        };
        AdminItemListViewService.$inject = ["$http"];
        return AdminItemListViewService;
    })();
    CSR.AdminItemListViewService = AdminItemListViewService;
})(CSR || (CSR = {}));
register("bannercsr").service("AdminItemListViewService", CSR.AdminItemListViewService);
//# sourceMappingURL=adminItemListViewService.js.map