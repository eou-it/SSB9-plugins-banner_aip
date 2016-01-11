///<reference path="../../typings/tsd.d.ts"/>
var CSR;
(function (CSR) {
    var AdminItemListViewService = (function () {
        function AdminItemListViewService($http, $q) {
            this.httpService = $http;
            this.qService = $q;
            this.init();
        }
        AdminItemListViewService.prototype.init = function () {
            var _this = this;
            this.getGridData().then(function (response) {
                _this.gridData = {
                    header: response.data.header,
                    data: response.data.data
                };
            }, function (errorResponse) {
                console.log(errorResponse);
                //TODO: handling error
            });
            this.getCodeTypes().then(function (response) {
                _this.codeTypes = response.data;
            }, function (errorResponse) {
                console.log(errorResponse);
                //TODO: handling error
            });
        };
        AdminItemListViewService.prototype.getCodeTypes = function () {
            var request = this.httpService({
                method: "POST",
                url: "csr/codeTypes"
            });
            return request;
        };
        AdminItemListViewService.prototype.getGridData = function () {
            var request = this.httpService({
                method: "POST",
                url: "csr/actionItems"
            });
            request;
            return request;
        };
        AdminItemListViewService.prototype.getLastItemId = function () {
            var idArray = this.gridData.data.map(function (item) { return item.id; });
            return Math.max.apply(Math, idArray);
        };
        AdminItemListViewService.prototype.removeSelectedItem = function (selectedItems) {
            var _this = this;
            angular.forEach(this.gridData.data, function (item, idx) {
                var exist = selectedItems.filter(function (_item) { return item.id === _item.id; });
                if (exist.length === 0) {
                    _this.gridData.data.splice(idx, 1);
                }
            });
            return this.gridData.data;
        };
        AdminItemListViewService.prototype.addNewItems = function (items) {
            this.gridData.data = items.concat(this.gridData.data);
        };
        AdminItemListViewService.$inject = ["$http", "$q"];
        return AdminItemListViewService;
    })();
    CSR.AdminItemListViewService = AdminItemListViewService;
})(CSR || (CSR = {}));
register("bannercsr").service("AdminItemListViewService", CSR.AdminItemListViewService);
//# sourceMappingURL=adminItemListViewService.js.map