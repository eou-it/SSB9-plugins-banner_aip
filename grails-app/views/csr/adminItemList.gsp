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
    <div ng-view></div>
    <div class="listItemContainer" layout-margin ng-controller="AdminItemListViewCtrl">
        <div layout="row" layout-sm="column">
            <md-input-container class="md-block" flex-gt-sm>
                <label>Search</label>
                <input type="text" ng-model="filterName">
            </md-input-container>
            <div ng-hide="true">
                {{(filteredItems = (vm.gridData.data | filter:filterName))}}
            </div>


            <section layout="row" flex-gt-sm layout-align="end center" layout-wrap>
                <md-button ng-click="vm.addNewItem()">ADD</md-button>
                <md-button ng-disabled="vm.disableDelete" ng-click="vm.removeItemCallback(filteredItems)">
                    REMOVE
                </md-button>
                <md-button ng-disabled="vm.disableUpdate" ng-click="vm.updateItems(evt)">UPDATE</md-button>
            </section>
        </div>
        <md-tabs md-dynamic-height md-border-bottom>
            <md-tab label="Template">
                <md-content class="md-padding">
                    <md-list>
                        <md-list-item>
                            <p ng-repeat="item in vm.gridData.header">
                                {{item.title}}
                            </p>
                            <md-checkbox ng-model="chkAll" ng-change="vm.selectAll(filteredItems, chkAll)" aria-label="select all">
                                <md-tooltip>Select All</md-tooltip>
                            </md-checkbox>
                        </md-list-item>
                        <md-divider></md-divider>
                        <md-list-item ng-repeat="item in filteredItems">
                            <p ng-repeat="header in vm.gridData.header">{{header.field==="type"?vm.codeTypes[item[header.field]]:item[header.field]}}</p>
                            <md-checkbox ng-model="item.selected" ng-change="vm.chkboxCallback(filteredItems)"></md-checkbox>
                            <md-divider></md-divider>
                        </md-list-item>
                    </md-list>
                </md-content>
            </md-tab>
            <md-tab label="Data Fields">
                <md-content class="md-padding">
                    Data fields content goes here
                </md-content>
            </md-tab>
        </md-tabs>
    </div>
</div>
</body>
</html>