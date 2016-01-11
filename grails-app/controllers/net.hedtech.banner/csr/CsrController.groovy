/**
 * Created by jshin on 12/22/15.
 */
package net.hedtech.banner.csr

import grails.converters.JSON

class CsrController {

    static defaultAction = "adminItemList"
    def model=[:]
    def adminItemList() {
        render(model: model, view: "adminItemList")
    }

    def actionItems() {
        def jsonTestHeaderData = '[' +
                '{field: "id", title: "ID"},' +
                '{field: "name", title: "Name"},' +
                '{field: "type", title: "Type"},' +
                '{field: "description", title: "Description"},' +
                '{field: "lastModifiedDate", title: "Last Modified Date"},' +
                '{field: "lastModifiedBy", title: "Last Modified By"}' +
                ']'

        def jsonTestItemData = '[' +
                '{id: 0, name: "Visa_status", type:0, description:"Visa documents upload", lastModifiedDate: new Date(1452207955638), lastModifiedBy: "userId"},' +
                '{id: 1, name: "Medical_Record", type:0, description: "Medical record documents upload", lastModifiedDate: new Date(1452207955638), lastModifiedBy: "another userId"},' +
                '{id: 2, name: "Permanent_Address", type:3, description: "Permanent residential address", lastModifiedDate: new Date(1452207955638), lastModifiedBy: "same user Id"}' +
                ']'
        def model = [:]
        model.header = JSON.parse(jsonTestHeaderData)
        model.data = JSON.parse(jsonTestItemData)

        render model as JSON
    }
    def codeTypes() {
        def model = ["Student", "Person", "General", "All"]
        render model as JSON
    }
}