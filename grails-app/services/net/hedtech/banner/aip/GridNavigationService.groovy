/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase


class GridNavigationService extends ServiceBase {

    public ascSort( arr, params ) {
        arr.sort { a, b ->
            if (a[params.sortColumnName] instanceof LinkedHashMap) {
                a[params.sortColumnName]['label'] <=> b[params.sortColumnName]['label']

            } else {
                a[params.sortColumnName] <=> b[params.sortColumnName]
            }
        }
        return arr;
    }


    public descSort( arr, params ) {
        arr.sort { a, b ->
            if (a[params.sortColumnName] instanceof LinkedHashMap) {
                b[params.sortColumnName]['label'] <=> a[params.sortColumnName]['label']
            } else {
                b[params.sortColumnName] <=> a[params.sortColumnName]
            }
        }
        return arr;
    }
}