///<reference path="../../typings/tsd.d.ts"/>
///<reference path="../services/adminItemListViewService.ts"/>
var CSR;
(function (CSR) {
    var AdminItemListViewCtrl = (function () {
        function AdminItemListViewCtrl($scope, $mdToast, $mdDialog, $mdMedia, AdminItemListViewService) {
            var _this = this;
            this.toastService = $mdToast;
            this.dialogService = $mdDialog;
            this.mdMedia = $mdMedia;
            this.adminItemListViewService = AdminItemListViewService;
            this.init();
            $scope.vm = this;
            $scope.$watch(function () {
                return _this.adminItemListViewService.itemGroups;
            }, function (newVal) {
                _this.itemGroups = newVal;
            });
        }
        AdminItemListViewCtrl.prototype.init = function () {
            this.studentGroups = this.adminItemListViewService.studentGroups;
            this.itemGroups = this.adminItemListViewService.itemGroups;
            this.disableDelete = true;
            this.disableUpdate = true;
        };
        AdminItemListViewCtrl.prototype.chkboxCallback = function (filteredItems) {
            var selected = filteredItems.filter(function (item) { return item.selected; });
            if (selected.length > 0) {
                this.disableDelete = false;
            }
            else {
                this.disableDelete = true;
            }
            return selected;
        };
        AdminItemListViewCtrl.prototype.selectAll = function (filteredItems, chkAll) {
            angular.forEach(filteredItems, function (item) {
                item.selected = chkAll;
            });
        };
        AdminItemListViewCtrl.prototype.removeItemCallback = function (filteredItems) {
            var selected = this.chkboxCallback(filteredItems);
            this.itemGroups = this.adminItemListViewService.removeSelectedItem(selected);
            this.chkboxCallback(this.itemGroups);
            this.disableUpdate = false;
        };
        AdminItemListViewCtrl.prototype.addNewItem = function (evt) {
            this.dialogService.show({
                controller: CSR.AddDialogCtrl,
                templateUrl: "../plugins/banner-csr-1.0/js/templates/addNewItem.html",
                parent: angular.element(document.body),
                targetEvent: evt,
                clickOutsideToClose: false,
                fullscreen: true
            }).then(function () {
                console.log("OK");
            }, function () {
                console.log("Cancel");
            });
        };
        AdminItemListViewCtrl.prototype.updateItems = function () {
            this.disableUpdate = true;
        };
        AdminItemListViewCtrl.$inject = ["$scope", "$mdToast", "$mdDialog", "$mdMedia", "AdminItemListViewService"];
        return AdminItemListViewCtrl;
    })();
    CSR.AdminItemListViewCtrl = AdminItemListViewCtrl;
    var AddDialogCtrl = (function () {
        function AddDialogCtrl($scope, $mdDialog, AdminItemListViewService) {
            this.AdminItemListViewService = AdminItemListViewService;
            this.mdDialogService = $mdDialog;
            $scope.vm = this;
            this.init();
        }
        AddDialogCtrl.prototype.init = function () {
            this.studentGroup = this.AdminItemListViewService.getTestGroupData();
            this.listItem = { name: "", group: 0, description: "" };
            this.listItems = [];
        };
        AddDialogCtrl.prototype.cancel = function () {
            this.mdDialogService.cancel();
        };
        AddDialogCtrl.prototype.add = function () {
            this.listItem.group = this.studentGroup.indexOf(this.listItem.group);
            this.listItems.push(this.listItem);
            this.listItem = { name: "", group: 0, description: "" };
        };
        AddDialogCtrl.prototype.apply = function () {
            this.AdminItemListViewService.addNewItems(this.listItems);
            this.mdDialogService.cancel();
        };
        AddDialogCtrl.$inject = ["$scope", "$mdDialog", "AdminItemListViewService"];
        return AddDialogCtrl;
    })();
    CSR.AddDialogCtrl = AddDialogCtrl;
})(CSR || (CSR = {}));
register("bannercsr")
    .controller("AdminItemListViewCtrl", CSR.AdminItemListViewCtrl)
    .controller("AddDialogCtrl", CSR.AddDialogCtrl);
//# sourceMappingURL=adminItemListViewCtrl.js.map