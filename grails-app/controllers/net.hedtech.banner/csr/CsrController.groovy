/**
 * Created by jshin on 12/22/15.
 */
package net.hedtech.banner.csr


class CsrController {

    static defaultAction = "adminItemList"
    def model=[:]
    def adminItemList() {
        render(model: model, view: "adminItemList")
    }
}