///<reference path="../../typings/tsd.d.ts"/>

declare var register;

module CSR {

    interface IAdminItemListViewScope extends ng.IScope {
        vm:AdminItemListView;
    }

    interface IListItem {
        id:number;
        name:string;
        group:number;
        description: string;
    }

    interface IAdminItemListView {
        studentGroups:string[];
        itemGroups:IListItem[];
        toastService:angular.material.IToastService;
        deleteRowCallback(rows:any[]):void;
    }

    export class AdminItemListView implements IAdminItemListView {
        static $inject=["$scope", "$mdToast"];
        itemGroups:IListItem[];
        studentGroups: string[];
        toastService:angular.material.IToastService;
        constructor($scope:IAdminItemListViewScope, $mdToast:angular.material.IToastService) {
            $scope.vm = this;
            this.toastService = $mdToast;
            this.init();
        }
        init() {
            this.studentGroups = ["International", "Out of state", "In state", "All"];
            this.itemGroups = [
                {id: 0, name: "Visa status", group:0, description:"Visa documents upload"},
                {id: 1, name: "Medical Record", group:0, description: "Medical record documents upload"},
                {id: 2, name: "Address", group:3, description: "Permanent residential address"}
            ];
        }
        deleteRowCallback(rows) {
            this.toastService.show(
                this.toastService.simple()
                .textContent("Deleted row id(s): " + rows)
                .hideDelay(3000)
            );
        }
    }
}

register("bannercsr").controller("AdminItemListView", CSR.AdminItemListView);