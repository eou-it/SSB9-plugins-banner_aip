<%--
  Created by IntelliJ IDEA.
  User: jshin
  Date: 1/4/16
  Time: 8:16 AM
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
<div id="content" ng-app="bannercsr">
    <div class="itemListContainer" layout-margin ng-controller="AdminItemListView">
        <md-card style="margin:0px">
            <md-subheader>Filter by search</md-subheader>
            <md-input-container style="margin:15px;">
                <label>Search</label>
                <input type="text" ng-model="filterName">
            </md-input-container>
            <div ng-hide="true">
                {{(filteredItems = (vm.itemGroups | filter:filterName))}}
            </div>
        </md-card>
        <mdt-table mdt-row="{'data':vm.itemGroups,
                            'table-row-id-key':'id',
                            'column-keys':['name', 'group', 'description']}"
                    paginated-rows="{isEnabled: true, rowsPerPageValues: [5, 10, 25, 50]}"
                    selectable-rows="true",
                    alternate-headers="'contextual'"
                    table-card="{visible:true, title: 'List'"
                    delete-row-callback="vm.deleteRowCallback(rows)">
            <mdt-header-row>
                <mdt-column align-rule="left">Name</mdt-column>
                <mdt-column align-rule="left">Group</mdt-column>
                <mdt-column align-rule="left">Description</mdt-column>
            </mdt-header-row>
        </mdt-table>

    </div>
</div>
</body>
</html>