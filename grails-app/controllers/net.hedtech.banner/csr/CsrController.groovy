/**
 * Created by jshin on 12/22/15.
 */
package net.hedtech.banner.csr

import grails.converters.JSON

import java.security.InvalidParameterException
import org.springframework.context.i18n.LocaleContextHolder

class CsrController {

    static defaultAction = "adminLanding"
    def model=[:]
    def adminLanding() {
        render(model: model, view: "index")
    }

    def adminActionItems() {
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

    def actionItems() {
        def actionItems = [
                [
                    name: "registration",
                    info: getActionGroupDescription("registration"),
                    header: ["title", "state", "description"],
                    items: [
                        [name: "drugAndAlcohol", state: "csr.user.list.item.state.pending", title: getItemInfo("drugAndAlcohol").title, description: getItemInfo("drugAndAlcohol").description],
                        [name: "registrationTraining", state: "csr.user.list.item.state.pending", title: getItemInfo("registrationTraining").title, description: getItemInfo("registrationTraining").description],
                        [name: "personalInfo", state: "csr.user.list.item.state.pending", title: getItemInfo("personalInfo").title, description: getItemInfo("personalInfo").description],
                        [name: "meetAdvisor", state: "csr.user.list.item.state.pending", title: getItemInfo("meetAdvisor").title, description: getItemInfo("meetAdvisor").description],
                        [name: "residenceProof", state: "csr.user.list.item.state.pending", title: getItemInfo("residenceProof").title, description: getItemInfo("residenceProof").description]
                    ]
                ], [
                    name: "graduation",
                    info: getActionGroupDescription("graduation"),
                    header: ["title", "state", "description"],
                    items: [
                        [name: "meetAdvisor", state: "csr.user.list.item.state.pending", title: getItemInfo("meetAdvisor").title, description: getItemInfo("meetAdvisor").description],
                    ]
                ]
            ]
        render actionItems as JSON
    }



    def getItemInfo(type) {
        Map item = [:]
        switch(type) {
            case "drugAndAlcohol":
                item.put("description", "csr.user.list.item.dsc.drugAlcohol")
                item.put("title", "csr.user.list.item.name,drugAlcohol")
                break
            case "registrationTraining":
                item.put("description", "csr.user.list.item.dsc.registrationTraining")
                item.put("title", "csr.user.list.item.name.registrationTraining")
                break;
            case "personalInfo":
                item.put("description", "csr.user.list.item.dsc.personalInfo")
                item.put("title", "csr.user.list.item.name.personalInfo")
                break;
            case "meetAdvisor":
                item.put("description", "csr.user.list.item.dsc.meetWithAdvisor")
                item.put("title", "csr.user.list.item.name.meetWithAdvisor")
                break;
            case "residenceProof":
                item.put("description", "csr.user.list.item.dsc.residence")
                item.put("title", "csr.user.list.item.name.residence")
                break;
            default:
                throw new InvalidParameterException("Invalid action item type")
                break;
        }
        return item
    }
    def getActionGroupDescription(type) {
        Map item=[:]
        switch (type) {
            case "registration":
                item.put("title", "csr.user.list.group.name.registration")
                item.put("description", "csr.user.list.group.dsc.registration")
                break;
            case "graduation":
                item.put("title", "csr.user.list.group.name.graduation")
                item.put("description", "csr.user.list.group.dsc.graduation")
                break;
            default:
                item.put("title", "")
                item.put("description", "")
        }
        return item
    }
}