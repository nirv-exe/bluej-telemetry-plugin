package com.uwi.comp3607;

import bluej.extensions2.event.InvocationFinishedEvent;
import bluej.extensions2.event.InvocationFinishedListener;

public class ExecutionListener implements InvocationFinishedListener{
    private final TelemetryStorage storage; 

    public ExecutionListener (TelemetryStorage storage){
        this.storage = storage;
    }

    
    @Override
    public void invocationFinished(InvocationFinishedEvent event) {

        String methodName = event.getMethodName();

        if (methodName == null) {
            methodName = "Constructor";
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
