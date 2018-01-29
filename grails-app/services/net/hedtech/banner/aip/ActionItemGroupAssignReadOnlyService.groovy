/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.i18n.MessageHelper

class ActionItemGroupAssignReadOnlyService {

    /**
     * Gets Assigned Action Items In the Group
     * @param groupId
     * @return
     */
    def getAssignedActionItemsInGroup( Long groupId ) {
        def assignedActionItems = ActionItemGroupAssignReadOnly.fetchActionItemGroupAssignROByGroupId( groupId )
        assignedActionItems?.collect {it ->
            [
                    id                        : it.id,
                    actionItemId              : it.actionItemId,
                    sequenceNumber            : it.sequenceNumber,
                    actionItemName            : it.actionItemName,
                    actionItemStatus          : it.actionItemStatus ? MessageHelper.message( "aip.status.${it.actionItemStatus.trim()}" ) : null,
                    actionItemFolderName      : it.actionItemFolderName,
                    actionItemTitle           : it.actionItemTitle,
                    actionItemDescription     : it.actionItemDescription,
                    actionItemFolderId        : it.actionItemFolderId,
                    actionItemPostingIndicator: it.actionItemPostingIndicator
            ]
        }
    }

    /**
     *
     * @param groupId
     * @return
     */
    def fetchActiveActionItemByGroupId( groupId ) {
        ActionItemGroupAssignReadOnly.fetchActiveActionItemByGroupId( groupId ).collect {
            [actionItemId        : it[0],
             actionItemName      : it[1],
             actionItemTitle     : it[2],
             actionItemFolderName: it[3]]
        }
    }

    /**
     * Fetches active groups which has active action itesm
     * @return
     */
    def fetchGroupLookup() {
        ActionItemGroupAssignReadOnly.fetchGroupLookup().collect() {
            [folderId: it[0], folderName: it[1], groupId: it[2], groupName: it[3], groupTitle: it[4]]
        }
    }
}
