///<reference path="../../typings/tsd.d.ts"/>
///<reference path="../services/adminItemListViewService.ts"/>

declare var register;

module CSR {

    interface IAdminItemListViewScope extends ng.IScope {
        vm:AdminItemListViewCtrl;
    }

    interface IAdminItemListView {
        itemGroups:IListItem[];
        studentGroups:string[];
        disableDelete:boolean;
        disableUpdate:boolean;
        toastService:angular.material.IToastService;
        dialogService;
        mdMedia:angular.material.IMedia;
        adminItemListViewService:CSR.AdminItemListViewService;
        chkboxCallback(filteredItems:IListItem[]):IListItem[];
        removeItemCallback(filteredItems:IListItem[]):void
        addNewItem(evt):void;
        updateItems():void;
        selectAll(filteredItems:IListItem[], chkAll:boolean): void;
    }

    export class AdminItemListViewCtrl implements IAdminItemListView {
        static $inject=["$scope", "$mdToast", "$mdDialog", "$mdMedia", "AdminItemListViewService"];
        public itemGroups:IListItem[];
        public studentGroups: string[];
        public disableDelete:boolean;
        public disableUpdate:boolean;
        toastService: angular.material.IToastService;
        dialogService;
        mdMedia: angular.material.IMedia;
        adminItemListViewService: CSR.AdminItemListViewService;
        customFullscreen;
        constructor($scope:IAdminItemListViewScope, $mdToast:angular.material.IToastService, $mdDialog:angular.material.IDialogService,
                    $mdMedia:angular.material.IMedia, AdminItemListViewService:CSR.AdminItemListViewService) {
            this.toastService = $mdToast;
            this.dialogService = $mdDialog;
            this.mdMedia = $mdMedia;
            this.adminItemListViewService = AdminItemListViewService;
            this.init();
            $scope.vm = this;
            $scope.$watch(()=>{
                return this.adminItemListViewService.itemGroups;}, (newVal) => {
                this.itemGroups = newVal;
            });
        }
        init() {
            this.studentGroups = this.adminItemListViewService.studentGroups;
            this.itemGroups = this.adminItemListViewService.itemGroups;
            this.disableDelete = true;
            this.disableUpdate = true;
            this.customFullscreen = this.mdMedia("xs") || this.mdMedia("sm");
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
            this.itemGroups = this.adminItemListViewService.removeSelectedItem(selected);
            this.chkboxCallback(this.itemGroups);
            this.disableUpdate = false;
        }
        addNewItem(evt) {
            var useFullScreen = (this.mdMedia("sm") || this.mdMedia("xs")) && this.customFullscreen;
            this.dialogService.show({
                controller:CSR.AddDialogCtrl,
                templateUrl: "../plugins/banner-csr-1.0/js/templates/addNewItem.html",
                parent: angular.element(document.body),
                targetEvent:evt,
                clickOutsideToClose: false,
                fullscreen: true //useFullScreen
            }).then(() => {
                console.log("OK")
                }, () => {
                console.log("Cancel");
            });
            //var confirm = this.dialogService.confirm()
            //    .title("Add item")
            //    .content("Add new item here")
            //    .ariaLabel("Add new item")
            //    .targetEvent(evt)
            //    .ok("Add")
            //    .cancel("Cancel");
            //var self = this;
            //this.dialogService.show(confirm).then(() => {
            //    self.disableUpdate = false;
            //}, () => {
            //    console.log("what?");
            //});
        }
        updateItems() {
            this.disableUpdate = true;
        }
    }

    export class AddDialogCtrl {
        static $inject=["$scope", "$mdDialog", "AdminItemListViewService"]
        AdminItemListViewService;
        mdDialogService;
        studentGroup;
        listItem;
        listItems;
        constructor($scope, $mdDialog, AdminItemListViewService) {
            this.AdminItemListViewService = AdminItemListViewService;
            this.mdDialogService = $mdDialog;
            $scope.vm = this;
            this.init();
        }
        init() {
            this.studentGroup = this.AdminItemListViewService.getTestGroupData();
            this.listItem = {name:"", group:"", description:""};
            this.listItems=[];
        }
        cancel() {
            this.mdDialogService.cancel();
        }
        add() {
            this.listItem.group = this.studentGroup.indexOf(this.listItem.group);
            this.listItems.push(this.listItem);
            this.listItem = {name:"", group:"", description:""};
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