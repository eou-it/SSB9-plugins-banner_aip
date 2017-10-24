/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
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
    Failed(true);

    boolean terminal;


    ActionItemPostWorkExecutionState( boolean terminal ) {
        this.terminal = terminal;
    }

    Set<ActionItemPostWorkExecutionState> set() {
    	return EnumSet.range( Ready, Failed );
    }

    String toString() {
        return this.name();
    }

    static ActionItemPostWorkExecutionState valueOf( int ordinal) {
        for(ActionItemPostWorkExecutionState ds: values()) {
            if (ds.ordinal() == ordinal) return ds;
        }
        throw new IllegalArgumentException( "ordinal out of range:" + ordinal );
    }

    boolean isTerminal() {
        return terminal;
    }

    boolean isRunning() {
        return !terminal;
    }

}
