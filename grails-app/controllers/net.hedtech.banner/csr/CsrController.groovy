/**
 * Created by jshin on 12/22/15.
 */
package net.hedtech.banner.csr

import grails.converters.JSON

class CsrController {

    static defaultAction = "adminLanding"
    def model=[:]
    def adminLanding() {
        render(model: model, view: "index")
    }

    def actionItems() {
        def jsonTestHeaderData = '[' +
                '{name: "id", title: "ID", options:{visible:false, isSortable: false}},' +
                '{name: "name", title: "Name", options:{visible:true, isSortable: false}},' +
                '{name: "type", title: "Type", options:{visible:true, isSortable: false}},' +
                '{name: "description", title: "Description", options:{visible:true, isSortable: false}},' +
                '{name: "lastModifiedDate", title: "Last Modified Date", options:{visible:true, isSortable: false}},' +
                '{name: "lastModifiedBy", title: "Last Modified By", options:{visible:true, isSortable: false}}' +
                ']'

        def jsonTestItemData = '[' +
                '{id: 0, name: "Visa_status", type:0, description:"Visa documents upload", lastModifiedDate: new Date(1452207955638), lastModifiedBy: "userId"},' +
                '{id: 1, name: "Medical_Record", type:0, description: "Medical record documents upload", lastModifiedDate: new Date(1452207955638), lastModifiedBy: "another userId"},' +
                '{id: 2, name: "Permanent_Address", type:3, description: "Permanent residential address", lastModifiedDate: new Date(1452207955638), lastModifiedBy: "same user Id"}' +
                ']'
        def model = [:]
        model.header = JSON.parse(jsonTestHeaderData)
        model.result = JSON.parse(jsonTestItemData)

        render model as JSON
    }

    def codeTypes() {
        def model = ["Student", "Person", "General", "All"]
        render model as JSON
    }
}