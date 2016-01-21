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
</head>

<body>
    <div role="main">
        <div id="title-panel" class="aurora-theme"><h1>List Items</h1></div>
        <div ng-app="bannercsr">
            <div class="viewContainer">
                <div ui-view></div>
            </div>
        </div>
    </div>
</body>
</html>