/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip.post.grouppost

/**
 * ActionItemJobItemExecutionState describes the state of executing action item job item.
 */
enum ActionItemPostWorkExecutionState implements Serializable {

	Ready(false),
    Complete(true),
    Partial(true),
    Stopped(true),
    Failed(true)

    boolean terminal


    ActionItemPostWorkExecutionState( boolean terminal ) {
        this.terminal = terminal
    }

    Set<ActionItemPostWorkExecutionState> set() {
    	EnumSet.range( Ready, Failed )
    }

    String toString() {
        this.name(  )
    }

    boolean isTerminal() {
        terminal
    }

    boolean isRunning() {
        !terminal
    }

}
