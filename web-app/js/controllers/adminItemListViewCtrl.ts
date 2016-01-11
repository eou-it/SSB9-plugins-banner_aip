///<reference path="../../typings/tsd.d.ts"/>
///<reference path="../services/adminItemListViewService.ts"/>

declare var register;

module CSR {

    interface IAdminItemListViewScope extends ng.IScope {
        vm:AdminItemListViewCtrl;
    }

    interface IAdminItemListView {
        gridData: IGridData;
        codeTypes: string[];
        disableDelete: boolean;
        disableUpdate: boolean;
        toastService: angular.material.IToastService;
        dialogService;
        mdMedia: angular.material.IMedia;
        adminItemListViewService: CSR.AdminItemListViewService;
        chkboxCallback(filteredItems:IListItem[]): IListItem[];
        removeItemCallback(filteredItems:IListItem[]): void
        addNewItem(evt): void;
        updateItems(): void;
        selectAll(filteredItems:IListItem[], chkAll:boolean): void;
    }

    export class AdminItemListViewCtrl implements IAdminItemListView {
        static $inject=["$scope", "$mdToast", "$mdDialog", "$mdMedia", "AdminItemListViewService"];
        public gridData: IGridData;
        public codeTypes: string[];
        public disableDelete:boolean;
        public disableUpdate:boolean;
        toastService: angular.material.IToastService;
        dialogService;
        mdMedia: angular.material.IMedia;
        adminItemListViewService: CSR.AdminItemListViewService;
        constructor($scope:IAdminItemListViewScope, $mdToast:angular.material.IToastService, $mdDialog:angular.material.IDialogService,
                    $mdMedia:angular.material.IMedia, AdminItemListViewService:CSR.AdminItemListViewService) {
            this.toastService = $mdToast;
            this.dialogService = $mdDialog;
            this.mdMedia = $mdMedia;
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
            this.gridData.data = this.adminItemListViewService.removeSelectedItem(selected);
            this.chkboxCallback(this.gridData.data);
            this.disableUpdate = false;
        }
        addNewItem(evt) {
            this.dialogService.show({
                controller:CSR.AddDialogCtrl,
                templateUrl: "../plugins/banner-csr-1.0/js/templates/addNewItem.html",
                parent: angular.element(document.body),
                targetEvent:evt,
                clickOutsideToClose: false,
                fullscreen: true
            }).then(() => {
                console.log("OK")
                }, () => {
                console.log("Cancel");
            });
        }
        updateItems() {
            this.disableUpdate = true;
        }
    }


    interface IAddDialogCtrl extends ng.IScope {
        vm:AddDialogCtrl;
    }
    export class AddDialogCtrl {
        static $inject=["$scope", "$mdDialog", "AdminItemListViewService"];
        AdminItemListViewService:CSR.AdminItemListViewService;
        mdDialogService;
        codeTypes:string[];
        listItem:IListItem;
        listItems:IListItem[];
        constructor($scope:IAddDialogCtrl, $mdDialog, AdminItemListViewService:CSR.AdminItemListViewService) {
            this.AdminItemListViewService = AdminItemListViewService;
            this.mdDialogService = $mdDialog;
            $scope.vm = this;
            this.init();
        }
        init() {
            this.codeTypes = this.AdminItemListViewService.codeTypes;
            this.listItem = {id: this.AdminItemListViewService.getLastItemId()+1, name: "", type: 0, description: "", lastModifiedDate: new Date(), lastModifiedBy: "me"};
            this.listItems=[];
        }
        cancel() {
            this.mdDialogService.cancel();
        }
        add() {
            this.listItems.push(this.listItem);
            this.listItem = {id: this.listItem.id+1, name: "", type: 0, description: "", lastModifiedDate: new Date(), lastModifiedBy: "me"};
        }
        apply() {
            this.AdminItemListViewService.addNewItems(this.listItems);
            this.mdDialogService.cancel();
        }
    }
}

register("bannercsr")
    .controller("AdminItemListViewCtrl", CSR.AdminItemListViewCtrl)
    .controller("AddDialogCtrl", CSR.AddDialogCtrl);