///<reference path="../../typings/tsd.d.ts"/>

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
        header: {field:string, title:string}[];
        data: IListItem[];
    }
    interface IAdminItemListViewService {
        httpService:ng.IHttpService;
        qService:ng.IQService;
        gridData:IGridData;
        codeTypes:string[];
        getCodeTypes():ng.IHttpPromise<string[]>
        getGridData(): ng.IHttpPromise<IGridData>;
        getLastItemId():number;
        removeSelectedItem(items:IListItem[]):IListItem[];
        addNewItems(items:IListItem[]):void;
    }
    interface IGridDataResponse {
        data: {
            header:{field:string, title:string}[];
            data: IListItem[]
        };
    }
    interface ICodeTypeResponse {
        data: string[];
    }
    export class AdminItemListViewService implements IAdminItemListViewService{
        static $inject=["$http", "$q"];
        httpService: ng.IHttpService;
        qService:ng.IQService;
        gridData:IGridData;
        codeTypes: string[];
        constructor($http:ng.IHttpService, $q:ng.IQService) {
            this.httpService = $http;
            this.qService = $q;
            this.init();
        }
        init() {
            this.getGridData().then((response:IGridDataResponse) => {
                this.gridData = {
                    header: response.data.header,
                    data : response.data.data
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
            var request = this.httpService({
                method: "POST",
                url: "csr/codeTypes"
            });
            return request;
        }
        getGridData() {
            var request = this.httpService({
                method:"POST",
                url: "csr/actionItems"
            });
            request
            return request;
        }
        getLastItemId() {
            var idArray = this.gridData.data.map((item)=>{return item.id;});
            return Math.max(...idArray);
        }
        removeSelectedItem(selectedItems) {
            angular.forEach(this.gridData.data, (item, idx)=> {
                var exist = selectedItems.filter((_item)=>{return item.id === _item.id;});
                if(exist.length===0) {
                    this.gridData.data.splice(idx, 1);
                }
            });
            return this.gridData.data;
        }
        addNewItems(items) {
            this.gridData.data = items.concat(this.gridData.data);
        }
    }
}

register("bannercsr").service("AdminItemListViewService", CSR.AdminItemListViewService);