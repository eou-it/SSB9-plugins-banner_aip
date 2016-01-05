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
    <div class="listItemContainer" layout-margin ng-controller="AdminItemListView">
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
        <md-list>
            <md-list-item ng-repeat="item in filteredItems">
                <p class="listItemName">{{item.name}}</p>
                <p class="listItemGroup">{{vm.studentGroups[item.group]}}</p>
                <p class="listItemDesc">{{item.description}}</p>
                <md-checkbox ng-model="item.selected" ng-change="vm.chkboxCallback(filteredItems)"></md-checkbox>
                <md-divider></md-divider>
            </md-list-item>
        </md-list>
        <div class="listItemControl">
            <md-button ng-click="vm.addNewItem()">ADD</md-button>
            <md-button ng-disabled="vm.disableDelete" ng-click="vm.removeItemCallback(filteredItems)">
                REMOVE
            </md-button>
            <md-button ng-disabled="vm.disableUpdate" ng-click="vm.updateItems(evt)">UPDATE</md-button>
        </div>
    </div>
</div>
</body>
</html>