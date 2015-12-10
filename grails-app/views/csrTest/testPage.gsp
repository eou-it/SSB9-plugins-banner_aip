<%--
  Created by IntelliJ IDEA.
  User: jshin
  Date: 12/9/15
  Time: 10:39 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <r:require modules="bannerCSR"/>
    <title>TEST</title>

</head>

<body ng-app="bannercsr">
<div ng-controller="BannerCSR.TestCtrl">
    {{testValue}}
</div>
</body>
</html>