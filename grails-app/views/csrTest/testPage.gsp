<%--
  Created by IntelliJ IDEA.
  User: jshin
  Date: 12/9/15
  Time: 10:39 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="bannerSelfServicePage"/>
    <meta name="menuEndPoint" content="${g.createLink(controller: 'selfServiceMenu', action: 'data')}"/>
    <meta name="menuBaseURL" content="${createLinkTo(dir: '/ssb')}"/>
    <r:require modules="bannerCSR"/>
    <title>TEST</title>

</head>

<body>
<div id="content" ng-app="bannercsr" layout="column">
    <div id="title-panel" class="aurora-theme" role="heading" aria-level="1">
        Angular Material - Test page
    </div>

    <!-- Container #2 -->
    <div flex layout="row" ng-controller="TestCtrl">

        <!-- Container #3 -->
        <md-sidenav md-is-locked-open="true" class="md-whiteframe-z2">
            {{vm.sideNavText}}
        </md-sidenav>

        <!-- Container #4 -->
        <md-content flex id="content1" style="background-color: lightgrey">
            {{vm.mainContentText}}
        </md-content>

    </div>
</div>

</body>
</html>