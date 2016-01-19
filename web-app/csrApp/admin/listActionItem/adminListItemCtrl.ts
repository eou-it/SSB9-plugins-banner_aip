///<reference path="../../../typings/tsd.d.ts"/>
///<reference path="../../common/services/adminItemListViewService.ts"/>

declare var register;

module CSR {

    interface IAdminItemListViewScope extends ng.IScope {
        vm:AdminListItemCtrl;
    }

    interface IAdminListItem {
        gridData: IGridData;
        codeTypes: string[];
        disableDelete: boolean;
        disableUpdate: boolean;
        dialogService;
        adminItemListViewService: CSR.AdminItemListViewService;
        chkboxCallback(filteredItems:IListItem[]): IListItem[];
        removeItemCallback(filteredItems:IListItem[]): void
        addNewItem(evt): void;
        updateItems(): void;
        selectAll(filteredItems:IListItem[], chkAll:boolean): void;
    }

    export class AdminListItemCtrl implements IAdminListItem {
        static $inject=["$scope", "AdminItemListViewService"];
        public gridData: IGridData;
        public codeTypes: string[];
        public disableDelete:boolean;
        public disableUpdate:boolean;
        toastService: angular.material.IToastService;
        dialogService;
        mdMedia: angular.material.IMedia;
        listEndPoint:string;
        adminItemListViewService: CSR.AdminItemListViewService;
        constructor($scope:IAdminItemListViewScope, AdminItemListViewService:CSR.AdminItemListViewService) {
            this.adminItemListViewService = AdminItemListViewService;
            this.init();
            $scope.vm = this;
            $scope.$watch(()=>{
                return this.adminItemListViewService.gridData;}, (newVal) => {
                this.gridData = newVal;
            });
            $scope.$watch(()=>{
                return this.adminItemListViewService.codeTypes;}, (newVal) => {
                this.codeTypes = newVal;
            });
        }
        init() {
            this.listEndPoint = "/StudentSSB/ssb/csr/actionItems";
            this.codeTypes = this.adminItemListViewService.codeTypes;
            this.gridData = this.adminItemListViewService.gridData;
            this.disableDelete = true;
            this.disableUpdate = true;
        }
        chkboxCallback(filteredItems) {
            var selected = filteredItems.filter((item)=>{return item.selected;});
            if(selected.length > 0) {
                this.disableDelete = false;
            } else {
                this.disableDelete = true;
            }
            return selected;
        }
        selectAll(filteredItems, chkAll) {
            angular.forEach(filteredItems, (item) => {
                item.selected = chkAll;
            });
        }
        removeItemCallback(filteredItems) {
            var selected = this.chkboxCallback(filteredItems);
            this.gridData.result = this.adminItemListViewService.removeSelectedItem(selected);
            this.chkboxCallback(this.gridData.result);
            this.disableUpdate = false;
        }
        addNewItem(evt) {

        }
        updateItems() {
            this.disableUpdate = true;
        }
    }
}

register("bannercsr")
    .controller("AdminListItemCtrl", CSR.AdminListItemCtrl);