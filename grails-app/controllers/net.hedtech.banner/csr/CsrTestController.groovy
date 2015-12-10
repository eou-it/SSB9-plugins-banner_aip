/**
 * Created by jshin on 12/9/15.
 */
package net.hedtech.banner.csr

import grails.converters.JSON

class CsrTestController {
    def index() {
        def model=[:]
        model.testValue="TEST TEXT FROM CsrTestController"
        render (model: model, view: "testPage")
    }
}