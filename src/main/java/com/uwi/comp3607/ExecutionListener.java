package com.uwi.comp3607;

import bluej.extensions2.event.InvocationFinishedEvent;
import bluej.extensions2.event.InvocationFinishedListener;

/**
 * Listens for method execution events in BlueJ and records
 * whether executions complete successfully or unsuccessfully.
 *
 * Stores execution results, available return values, and
 * exception information as telemetry events.
 */
public class ExecutionListener implements InvocationFinishedListener{

    /**
     * Stores the execution events recorded by this listener.
     */
    private final TelemetryStorage storage; 

    /**
     * Creates an execution listener.
     *
     * @param storage the storage used to record telemetry events
     */
    public ExecutionListener (TelemetryStorage storage){
        this.storage = storage;
    }

    /**
     * Records whether a BlueJ method execution finished normally. Successful
     * executions include the method name and any available return value. Failed
     * executions include a general exception or abnormal-exit description. The stored
     * text is a summary; it does not capture console output or retrieve exact
     * exception types or messages.
     *
     * @param event the execution event received from BlueJ
     */
    @Override
    public void invocationFinished(InvocationFinishedEvent event) {

        String methodName = event.getMethodName();

        if (methodName == null) {
            methodName = "Unknown";
        }

        String outputSummary = "Method: " + methodName;

        ExecutionEvent execEvent;

        if (event.getEventType() ==
                InvocationFinishedEvent.EventType.NORMAL_EXIT) {

            Object result = event.getResult();

            if (result != null) {
                outputSummary += " | Return value: " + result;
            } else {
                outputSummary += " | No return value";
            }

            execEvent =
                ExecutionEvent.successfulExecution(outputSummary);

        } else {
            String exceptionDetails;

            if (event.getEventType() == InvocationFinishedEvent.EventType.EXCEPTION_EXIT) {
                String className = event.getClassName();

                if (className == null) {
                    className = "Unknown";
                }

                exceptionDetails = "Runtime exception in class " + className + ", method " + methodName;
            } else {
                exceptionDetails = "Execution did not finish normally: " + methodName;
            }

            execEvent = ExecutionEvent.failedExecution(
                exceptionDetails,
                outputSummary
            );
        }

        storage.addEvent(execEvent);
    }

}
