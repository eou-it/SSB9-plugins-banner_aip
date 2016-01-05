///<reference path="../../typings/tsd.d.ts"/>

declare var register;

module CSR {
    export interface IListItem {
        name:string;
        group:any;
        description: string;
        selected?: boolean;
        id?:number;
    }

    interface IAdminItemListViewService {
        httpService:ng.IHttpService;
        itemGroups:IListItem[];
        studentGroups:string[];
        removeSelectedItem(items:IListItem[]):IListItem[];
        addNewItems(items:IListItem[]):void;
    }

    export class AdminItemListViewService implements IAdminItemListViewService{
        static $inject=["$http"];
        httpService:ng.IHttpService;
        itemGroups:IListItem[];
        studentGroups:string[];
        constructor($http:ng.IHttpService) {
            this.httpService = $http;
            this.itemGroups = this.getTestData();
            this.studentGroups = this.getTestGroupData();
        }
        getTestGroupData() {
            var data = ["International", "Out of state", "In state", "All"];
            return data;
        }
        getTestData() {
            var data= [
                {id: 0, name: "Visa status", group:0, description:"Visa documents upload", selected:false},
                {id: 1, name: "Medical Record", group:0, description: "Medical record documents upload", selected:false},
                {id: 2, name: "Address", group:3, description: "Permanent residential address", selected:false}
            ];
            return data;
        }
        removeSelectedItem(selectedItems) {
            var tempItems = [];
            angular.forEach(this.itemGroups, (item)=> {
                var exist = selectedItems.filter((_item)=>{return item.id === _item.id;});
                if(exist.length===0) {
                    tempItems.push(item);
                }
            });
            this.itemGroups = tempItems;
            return this.itemGroups;
        }
        addNewItems(items) {
            this.itemGroups = items.concat(this.itemGroups);
        }
    }
}

register("bannercsr").service("AdminItemListViewService", CSR.AdminItemListViewService);