/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip.post.grouppost

/**
 * ActionItemGroupSendExecutionState describes the state of executing Action Item job item.
 */
enum ActionItemPostExecutionState implements Serializable {

    New (false),
    Scheduled (false),
    Queued (false),

    Calculating (false),
    Processing (false),

    Complete (true),
    Stopped (true),
    Error (true);

    boolean terminal;


    ActionItemPostExecutionState( boolean terminal ) {
        this.terminal = terminal;
    }

    Set<ActionItemPostExecutionState> set() {
    	return EnumSet.range( New, Error );
    }

    String toString() {
        return this.name();
    }

    static ActionItemPostExecutionState valueOf( int ordinal) {
        for(ActionItemPostExecutionState ds: values()) {
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

    boolean isPending() {
        return this == New || this == Scheduled || this == Queued
    }

}
