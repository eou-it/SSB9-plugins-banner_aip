/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip.post.grouppost

/**
 * ActionItemGroupSendExecutionState describes the state of executing Action Item job item.
 */
enum ActionItemPostExecutionState implements Serializable {

    New( false ),
    Scheduled( false ),
    Queued( false ),

    Calculating( false ),
    Processing( false ),

    Complete( true ),
    Stopped( true ),
    Error( true )

    boolean terminal

    /**
     *
     * Constructor
     */
    ActionItemPostExecutionState( boolean terminal ) {
        this.terminal = terminal
    }

    /**
     * Gets Execution states
     * @return
     */
    Set<ActionItemPostExecutionState> set() {
        return EnumSet.range( New, Error )
    }

    /**
     *
     * @param ordinal
     * @return
     */
    static ActionItemPostExecutionState getStateEnum( String executionState ) {
        for (ActionItemPostExecutionState ds : values()) {
            if (ds.name() == executionState) {
                return ds
            }
        }
        throw new IllegalArgumentException( "executionState not found:" + executionState )
    }


    boolean isTerminal() {
        terminal
    }


    boolean isRunning() {
        !terminal
    }


    boolean isPending() {
        this in [New, Scheduled, Queued]
    }

}
