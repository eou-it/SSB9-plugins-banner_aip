/*********************************************************************************
 Copyright 2010-2015 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

grails.project.class.dir = "target/classes"
grails.project.lib.dir = "lib"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

// When deploying a war it is important to exclude the Oracle database drivers.  Not doing so will
// result in the all-too-familiar exception:
// "Cannot cast object 'oracle.jdbc.driver.T4CConnection@6469adc7'... to class 'oracle.jdbc.OracleConnection'
grails.war.resources = { stagingDir ->
    delete(file: "${stagingDir}/WEB-INF/lib/ojdbc6.jar")
}

grails.plugin.location.'banner-student-common'="../banner_student_common.git"
grails.plugin.location.'banner-general-person'="../banner_general_person.git"

grails.project.dependency.resolver = "maven" // or maven

grails.project.dependency.resolution = {

    inherits "global" // inherit Grails' default dependencies
    log "warn"        // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        if (System.properties['PROXY_SERVER_NAME']) {
            mavenRepo "${System.properties['PROXY_SERVER_NAME']}"
        }
        mavenLocal()
        grailsCentral()
        mavenCentral()
        mavenRepo "https://code.lds.org/nexus/content/groups/main-repo"
        mavenRepo "http://repository.jboss.org/maven2/"
    }


    dependencies {
    }

    plugins {
          test ':code-coverage:1.2.5'
    }

}

// CodeNarc rulesets
codenarc.ruleSetFiles="rulesets/banner.groovy"
codenarc.reportName="target/CodeNarcReport.html"
codenarc.propertiesFile="grails-app/conf/codenarc.properties"
