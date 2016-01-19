///<reference path="../../../typings/tsd.d.ts"/>
///<reference path="../../common/services/adminItemListViewService.ts"/>
///<reference path="../../common/services/csrBreadcrumbService.ts"/>
var CSR;
(function (CSR) {
    var AdminListItemCtrl = (function () {
        function AdminListItemCtrl($scope, AdminItemListViewService, CsrBreadcrumbService) {
            var _this = this;
            this.adminItemListViewService = AdminItemListViewService;
            this.breadcrumbService = CsrBreadcrumbService;
            this.init();
            $scope.vm = this;
            $scope.$watch(function () {
                return _this.adminItemListViewService.gridData;
            }, function (newVal) {
                _this.gridData = newVal;
            });
            $scope.$watch(function () {
                return _this.adminItemListViewService.codeTypes;
            }, function (newVal) {
                _this.codeTypes = newVal;
            });
        }
        AdminListItemCtrl.prototype.init = function () {
            this.listEndPoint = "/StudentSSB/ssb/csr/actionItems";
            this.codeTypes = this.adminItemListViewService.codeTypes;
            this.gridData = this.adminItemListViewService.gridData;
            this.disableDelete = true;
            this.disableUpdate = true;
            var breadItem = {
                "Admin List": "/list"
            };
            this.breadcrumbService.updateBreadcrumb(breadItem);
        };
        AdminListItemCtrl.prototype.chkboxCallback = function (filteredItems) {
            var selected = filteredItems.filter(function (item) { return item.selected; });
            if (selected.length > 0) {
                this.disableDelete = false;
            }
            else {
                this.disableDelete = true;
            }
            return selected;
        };
        AdminListItemCtrl.prototype.selectAll = function (filteredItems, chkAll) {
            angular.forEach(filteredItems, function (item) {
                item.selected = chkAll;
            });
        };
        AdminListItemCtrl.prototype.removeItemCallback = function (filteredItems) {
            var selected = this.chkboxCallback(filteredItems);
            this.gridData.result = this.adminItemListViewService.removeSelectedItem(selected);
            this.chkboxCallback(this.gridData.result);
            this.disableUpdate = false;
        };
        AdminListItemCtrl.prototype.addNewItem = function (evt) {
        };
        AdminListItemCtrl.prototype.updateItems = function () {
            this.disableUpdate = true;
        };
        AdminListItemCtrl.$inject = ["$scope", "AdminItemListViewService", "CsrBreadcrumbService"];
        return AdminListItemCtrl;
    })();
    CSR.AdminListItemCtrl = AdminListItemCtrl;
})(CSR || (CSR = {}));
register("bannercsr")
    .controller("AdminListItemCtrl", CSR.AdminListItemCtrl);
//# sourceMappingURL=adminListItemCtrl.js.map