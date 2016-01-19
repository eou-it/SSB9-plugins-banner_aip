///<reference path="../../../typings/tsd.d.ts"/>

declare var register;

module CSR {
    export interface IListItem {
        id: number;
        name: string;
        type: number;
        description: string;
        lastModifiedDate: Date;
        lastModifiedBy: string;
    }
    export interface IGridData {
        header: {position: number, name: string, title: string, options?: {visible:boolean, isSortable: boolean}}[];
        result: IListItem[];
    }
    interface IAdminItemListViewService {
        $http:ng.IHttpService;
        $q:ng.IQService;
        gridData:IGridData;
        codeTypes:string[];
        getCodeTypes():ng.IHttpPromise<string[]>
        getGridData(): ng.IHttpPromise<IGridData>;
        getLastItemId():number;
        removeSelectedItem(items:IListItem[]):IListItem[];
        addNewItems(items:IListItem[]):void;
    }
    interface IGridDataResponse {
        data: IGridData;
    }
    interface ICodeTypeResponse {
        data: string[];
    }
    export class AdminItemListViewService implements IAdminItemListViewService{
        static $inject=["$http", "$q"];
        $http: ng.IHttpService;
        $q:ng.IQService;
        gridData:IGridData;
        codeTypes: string[];
        constructor($http:ng.IHttpService, $q:ng.IQService) {
            this.$http = $http;
            this.$q = $q;
            this.init();
        }
        init() {
            this.getGridData().then((response:IGridDataResponse) => {
                this.gridData = {
                    header: response.data.header,
                    result : response.data.result
                };
            }, (errorResponse) => {
                console.log(errorResponse);
                //TODO: handling error
            });
            this.getCodeTypes().then((response:ICodeTypeResponse) => {
                this.codeTypes = response.data;
            }, (errorResponse) => {
                console.log(errorResponse);
                //TODO: handling error
            })
        }
        getCodeTypes() {
            var request = this.$http({
                method: "POST",
                url: "csr/codeTypes"
            });
            return request;
        }
        getGridData() {
            var request = this.$http({
                method:"POST",
                url: "csr/actionItems"
            });
            request
            return request;
        }
        getLastItemId() {
            var idArray = this.gridData.result.map((item)=>{return item.id;});
            return Math.max(...idArray);
        }
        removeSelectedItem(selectedItems) {
            angular.forEach(this.gridData.result, (item, idx)=> {
                var exist = selectedItems.filter((_item)=>{return item.id === _item.id;});
                if(exist.length===0) {
                    this.gridData.result.splice(idx, 1);
                }
            });
            return this.gridData.result;
        }
        addNewItems(items) {
            this.gridData.result = items.concat(this.gridData.result);
        }
    }
}

register("bannercsr").service("AdminItemListViewService", CSR.AdminItemListViewService);