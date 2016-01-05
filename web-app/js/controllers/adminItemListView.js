///<reference path="../../typings/tsd.d.ts"/>
var CSR;
(function (CSR) {
    var AdminItemListView = (function () {
        function AdminItemListView($scope, $mdToast, $mdDialog) {
            this.toastService = $mdToast;
            this.dialogService = $mdDialog;
            this.init();
            $scope.vm = this;
        }
        AdminItemListView.prototype.init = function () {
            this.studentGroups = ["International", "Out of state", "In state", "All"];
            this.disableDelete = true;
            this.disableUpdate = true;
            this.itemGroups = [
                { id: 0, name: "Visa status", group: 0, description: "Visa documents upload", selected: false },
                { id: 1, name: "Medical Record", group: 0, description: "Medical record documents upload", selected: false },
                { id: 2, name: "Address", group: 3, description: "Permanent residential address", selected: false }
            ];
        };
        AdminItemListView.prototype.chkboxCallback = function (filteredItems) {
            var selected = filteredItems.filter(function (item) { return item.selected; });
            if (selected.length > 0) {
                this.disableDelete = false;
            }
            else {
                this.disableDelete = true;
            }
            return selected;
        };
        AdminItemListView.prototype.removeItemCallback = function (filteredItems) {
            var tempItems = [];
            var selected = this.chkboxCallback(filteredItems);
            angular.forEach(this.itemGroups, function (item) {
                var exist = selected.filter(function (_item) { return item.id === _item.id; });
                if (exist.length === 0) {
                    tempItems.push(item);
                }
            });
            this.itemGroups = tempItems;
            this.chkboxCallback(this.itemGroups);
            this.disableUpdate = false;
        };
        AdminItemListView.prototype.addNewItem = function (evt) {
            var confirm = this.dialogService.confirm()
                .title("Add item")
                .content("Add new item here")
                .ariaLabel("Add new item")
                .targetEvent(evt)
                .ok("Add")
                .cancel("Cancel");
            var self = this;
            this.dialogService.show(confirm).then(function () {
                self.disableUpdate = false;
            }, function () {
                console.log("what?");
            });
        };
        AdminItemListView.prototype.updateItems = function () {
            this.disableUpdate = true;
        };
        AdminItemListView.$inject = ["$scope", "$mdToast", "$mdDialog"];
        return AdminItemListView;
    })();
    CSR.AdminItemListView = AdminItemListView;
})(CSR || (CSR = {}));
register("bannercsr").controller("AdminItemListView", CSR.AdminItemListView);
//# sourceMappingURL=adminItemListView.js.map