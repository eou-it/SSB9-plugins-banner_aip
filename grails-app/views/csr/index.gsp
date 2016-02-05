<%--
  Created by IntelliJ IDEA.
  User: jshin
  Date: 1/13/16
  Time: 4:31 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="headerAttributes" content=""/>
    <title></title>
    <meta name="layout" content="bannerSelfServicePage"/>
    <r:require modules="bannerCSR"/>
    <g:if test="${message(code: 'default.language.direction')  == 'rtl'}">
        <r:require modules="bannerCSRRTL"/>
    </g:if>
    <script type="text/javascript">
        console.log("run i18n setup");
        <g:i18n_setup/>
        console.log("setup done");
        console.log(window.i18n.csr.default);
        console.log(window.i18n.csr-ui.default);
    </script>
</head>

<body>
<div id="content" role="main">
    <div id="title-panel" class="aurora-theme"></div>
    <div class="viewContainer container-fluid" ng-app="bannercsr">
        <div ui-view></div>
    </div>
</div>
</body>
</html>