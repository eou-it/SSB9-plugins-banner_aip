///<reference path="../../typings/tsd.d.ts"/>

declare var register;

module CSR {

    interface IAdminItemListViewScope extends ng.IScope {
        vm:AdminItemListView;
        nutritionList:any;
    }

    interface IListItem {
        id:number;
        name:string;
        group:number;
        description: string;
        selected: boolean;
    }

    interface IAdminItemListView {
        itemGroups:IListItem[];
        studentGroups:string[];
        disableDelete:boolean;
        disableUpdate:boolean;
        toastService:angular.material.IToastService;
        dialogService:angular.material.IDialogService;
        chkboxCallback(filteredItems:IListItem[]):IListItem[];
        removeItemCallback(filteredItems:IListItem[]):void
        addNewItem(evt):void;
        updateItems():void;
    }

    export class AdminItemListView implements IAdminItemListView {
        static $inject=["$scope", "$mdToast", "$mdDialog"];
        public itemGroups:IListItem[];
        public studentGroups: string[];
        public disableDelete:boolean;
        public disableUpdate:boolean;
        toastService: angular.material.IToastService;
        dialogService: angular.material.IDialogService;
        constructor($scope:IAdminItemListViewScope, $mdToast:angular.material.IToastService, $mdDialog:angular.material.IDialogService) {
            this.toastService = $mdToast;
            this.dialogService = $mdDialog;
            this.init();
            $scope.vm = this;
        }
        init() {
            this.studentGroups = ["International", "Out of state", "In state", "All"];
            this.disableDelete = true;
            this.disableUpdate = true;
            this.itemGroups = [
                {id: 0, name: "Visa status", group:0, description:"Visa documents upload", selected:false},
                {id: 1, name: "Medical Record", group:0, description: "Medical record documents upload", selected:false},
                {id: 2, name: "Address", group:3, description: "Permanent residential address", selected:false}
            ];
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
        removeItemCallback(filteredItems) {
            var tempItems = [];
            var selected = this.chkboxCallback(filteredItems);
            angular.forEach(this.itemGroups, (item)=> {
                var exist = selected.filter((_item)=>{return item.id === _item.id;});
                if(exist.length===0) {
                    tempItems.push(item);
                }
            });
            this.itemGroups = tempItems;
            this.chkboxCallback(this.itemGroups);
            this.disableUpdate = false;
        }
        addNewItem(evt) {
            var confirm = this.dialogService.confirm()
                .title("Add item")
                .content("Add new item here")
                .ariaLabel("Add new item")
                .targetEvent(evt)
                .ok("Add")
                .cancel("Cancel");
            var self = this;
            this.dialogService.show(confirm).then(() => {
                self.disableUpdate = false;
            }, () => {
                console.log("what?");
            });
        }
        updateItems() {
            this.disableUpdate = true;
        }
    }
}

register("bannercsr").controller("AdminItemListView", CSR.AdminItemListView);