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
    <div id="title-panel" class="aurora-theme"><h1>List Items</h1></div>
    <div class="listItemContainer" layout-margin ng-controller="AdminItemListViewCtrl">
        <md-card style="margin:0px">
            <md-subheader>Filter by search</md-subheader>
            <md-input-container style="margin:5px;">
                <label>Search</label>
                <input type="text" ng-model="filterName">
            </md-input-container>
            <div ng-hide="true">
                {{(filteredItems = (vm.itemGroups | filter:filterName))}}
            </div>
        </md-card>
        <md-list>
            <md-list-item>
                <p>Name</p>
                <p>Group</p>
                <p>Description</p>
                <md-checkbox ng-model="chkAll" ng-change="vm.selectAll(filteredItems, chkAll)" aria-label="select all">
                    <md-tooltip>Select All</md-tooltip>
                </md-checkbox>
            </md-list-item>
            <md-divider></md-divider>
            <md-list-item ng-repeat="item in filteredItems">
                <p class="listItemName">{{item.name}}</p>
                <p class="listItemGroup">{{vm.studentGroups[item.group]}}</p>
                <p class="listItemDesc">{{item.description}}</p>
                <md-checkbox ng-model="item.selected" ng-change="vm.chkboxCallback(filteredItems)"></md-checkbox>
                <md-divider></md-divider>
            </md-list-item>
        </md-list>
        <md-list class="listItemControl">
            <md-list-item>
                <md-button ng-click="vm.addNewItem()">ADD</md-button>
                <md-button ng-disabled="vm.disableDelete" ng-click="vm.removeItemCallback(filteredItems)">
                    REMOVE
                </md-button>
                <md-button ng-disabled="vm.disableUpdate" ng-click="vm.updateItems(evt)">UPDATE</md-button>
            </md-list-item>
        </md-list>
    </div>
</div>
</body>
</html>