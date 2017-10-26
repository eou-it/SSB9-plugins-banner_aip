/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
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


    String toString() {
        this.name()
    }

    /**
     *
     * @param ordinal
     * @return
     */
    static ActionItemPostExecutionState valueOf( int ordinal ) {
        for (ActionItemPostExecutionState ds : values()) {
            if (ds.ordinal() == ordinal) {
                return ds
            }
        }
        throw new IllegalArgumentException( "ordinal out of range:" + ordinal )
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
        'undefined'
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
