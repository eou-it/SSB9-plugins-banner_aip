/**
 * Created by jshin on 12/22/15.
 */
package net.hedtech.banner.csr

import grails.converters.JSON

import java.security.InvalidParameterException

class CsrTestController {

    def checkActionItem() {
        def model=[:]
        model.isActionItem = true;
        render model as JSON;
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
                        [id: 1, name: "drugAndAlcohol", state: "csr.user.list.item.state.pending", title: getItemInfo("drugAndAlcohol").title, description: getItemInfo("drugAndAlcohol").description],
                        [id: 2, name: "registrationTraining", state: "csr.user.list.item.state.pending", title: getItemInfo("registrationTraining").title, description: getItemInfo("registrationTraining").description],
                        [id: 3, name: "personalInfo", state: "csr.user.list.item.state.pending", title: getItemInfo("personalInfo").title, description: getItemInfo("personalInfo").description],
                        [id: 4, name: "meetAdvisor", state: "csr.user.list.item.state.pending", title: getItemInfo("meetAdvisor").title, description: getItemInfo("meetAdvisor").description],
                        [id: 5, name: "residenceProof", state: "csr.user.list.item.state.pending", title: getItemInfo("residenceProof").title, description: getItemInfo("residenceProof").description]
                    ]
                ], [
                    name: "graduation",
                    info: getActionGroupDescription("graduation"),
                    header: ["title", "state", "description"],
                    items: [
                        [id: 4, name: "meetAdvisor", state: "csr.user.list.item.state.pending", title: getItemInfo("meetAdvisor").title, description: getItemInfo("meetAdvisor").description],
                    ]
                ]
            ]
        render actionItems as JSON
    }



    def getItemInfo(type) {
        Map item = [:]
        switch(type) {
            case "drugAndAlcohol":
                item.put("description", "You must review and confirm the Ellucian University Campus Drug and Alcohol Policy prior to registering for classes.")
                item.put("title", "Drug and Alcohol Policy")
                break
            case "registrationTraining":
                item.put("description", "It is takes 10 minutes, review the training video provided to help expedite your registration experience.")
                item.put("title", "Registration Process Training")
                break;
            case "personalInfo":
                item.put("description", "It is important that we have you current information such as your name, and contact information therefore it is required that you review, update and confirm your personal information.")
                item.put("title", "Personal Information")
                break;
            case "meetAdvisor":
                item.put("description", "You must meet with you Advisor or ensure you are on target to meet your educational goals for graduation.")
                item.put("title", "Meet with Advisor")
                break;
            case "residenceProof":
                item.put("description", "")
                item.put("title", "Proof of Residence")
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
                item.put("title", "Prepare for Registration")
                item.put("description", "You must complete the confirmation action items below you will be permitted to register for Spring Term 2016. Select each pending item in the list to complete the requirements.")
                break;
            case "graduation":
                item.put("title", "Prepare for Graduation")
                item.put("description", "You must have a minimum of 121 credits to graduate, when you are in your final semester you must complete the steps below prior to applying to graduate.")
                break;
            default:
                item.put("title", "")
                item.put("description", "")
        }
        return item
    }
}